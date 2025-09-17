package org.accounting.system;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import io.quarkus.test.security.TestSecurity;
import io.quarkus.test.security.oidc.Claim;
import io.quarkus.test.security.oidc.OidcSecurity;
import io.quarkus.test.security.oidc.TokenIntrospection;
import io.quarkus.test.security.oidc.UserInfo;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import org.accounting.system.dtos.InformativeResponse;
import org.accounting.system.dtos.installation.InstallationRequestDto;
import org.accounting.system.dtos.installation.InstallationResponseDto;
import org.accounting.system.dtos.metric.MetricRequestDto;
import org.accounting.system.dtos.metric.MetricResponseDto;
import org.accounting.system.dtos.metricdefinition.MetricDefinitionRequestDto;
import org.accounting.system.dtos.metricdefinition.MetricDefinitionResponseDto;
import org.accounting.system.dtos.pagination.PageResource;
import org.accounting.system.dtos.project.AssociateProjectProviderRequestDto;
import org.accounting.system.endpoints.ProjectEndpoint;
import org.accounting.system.enums.ApiMessage;
import org.accounting.system.repositories.project.ProjectRepository;
import org.accounting.system.wiremock.KeycloakWireMockServer;
import org.accounting.system.wiremock.ProjectWireMockServer;
import org.accounting.system.wiremock.ProviderWireMockServer;
import org.accounting.system.wiremock.TokenWireMockServer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.Set;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;


@QuarkusTest
@TestProfile(AccountingSystemTestProfile.class)
@TestHTTPEndpoint(ProjectEndpoint.class)
@QuarkusTestResource(ProjectWireMockServer.class)
@QuarkusTestResource(ProviderWireMockServer.class)
@QuarkusTestResource(KeycloakWireMockServer.class)
@QuarkusTestResource(TokenWireMockServer.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ProjectAuthorizationTest extends PrepareTest {

    @Inject
    ProjectRepository projectRepository;

    @Test
    @TestSecurity(user = "project_admin")
    @OidcSecurity(introspectionRequired = true,
            introspection = {
                    @TokenIntrospection(key = "voperson_id", value = "project_admin@example.org"),
                    @TokenIntrospection(key = "sub", value = "project_admin@example.org"),
                    @TokenIntrospection(key = "iss", value = "http://localhost:58080/realms/quarkus")
            },
            userinfo = {
                    @UserInfo(key = "name", value = "project_admin"),
                    @UserInfo(key = "email", value = "project_admin@example.org")
            }
    )
    public void associateProjectWithProvidersForbidden(){

        var associateProjectProviderRequestDto = new AssociateProjectProviderRequestDto();

        associateProjectProviderRequestDto.providers = Set.of("grnet");

        var informativeResponse = given()
                .contentType(ContentType.JSON)
                .body(associateProjectProviderRequestDto)
                .post("/{id}/associate", "777536")
                .then()
                .assertThat()
                .statusCode(403)
                .extract()
                .as(InformativeResponse.class);

        assertEquals(ApiMessage.UNAUTHORIZED_CLIENT.message, informativeResponse.message);
    }

    @Test
    public void associateProjectWithProvidersNotFound(){

        var associateProjectProviderRequestDto = new AssociateProjectProviderRequestDto();

        associateProjectProviderRequestDto.providers = Set.of("not_found");

        var informativeResponse = given()
                .auth()
                .oauth2(getAccessToken("admin"))
                .contentType(ContentType.JSON)
                .body(associateProjectProviderRequestDto)
                .post("/{id}/associate", "777536")
                .then()
                .assertThat()
                .statusCode(404)
                .extract()
                .as(InformativeResponse.class);

        assertEquals("There is no Provider with the following id: not_found", informativeResponse.message);
    }

    @Test
    @TestSecurity(user = "project_admin")
    @OidcSecurity(introspectionRequired = true,
            introspection = {
                    @TokenIntrospection(key = "voperson_id", value = "project_admin@example.org"),
                    @TokenIntrospection(key = "sub", value = "project_admin@example.org"),
                    @TokenIntrospection(key = "iss", value = "http://localhost:58080/realms/quarkus")
            },
            userinfo = {
                    @UserInfo(key = "name", value = "project_admin"),
                    @UserInfo(key = "email", value = "project_admin@example.org")
            }
    )
    public void metricsUnderAProject(){

        projectRepository.associateProjectWithProviders("777536", Set.of("grnet"));

        //Registering an installation
        var requestForMetricDefinition = new MetricDefinitionRequestDto();

        requestForMetricDefinition.metricName = "metric";
        requestForMetricDefinition.metricDescription = "description";
        requestForMetricDefinition.unitType = "TB";
        requestForMetricDefinition.metricType = "aggregated";

        var metricDefinitionResponse = createMetricDefinition(requestForMetricDefinition, "admin");

        var request = new InstallationRequestDto();

        request.project = "777536";
        request.organisation = "grnet";
        request.infrastructure = "okeanos-knossos";
        request.installation = "SECOND";
        request.unitOfAccess = metricDefinitionResponse.id;

        var installation = createInstallationAuth(request, "admin");

        var metric = new MetricRequestDto();
        metric.start = "2022-01-05T09:13:07Z";
        metric.end = "2022-01-05T09:14:07Z";
        metric.value = 10.8;
        metric.metricDefinitionId = metricDefinitionResponse.id;

        // creating and assigning a new Metric
        given()
                .auth()
                .oauth2(getAccessToken("admin"))
                .basePath("accounting-system/installations")
                .body(metric)
                .contentType(ContentType.JSON)
                .post("/{installationId}/metrics", installation.id)
                .then()
                .assertThat()
                .statusCode(201)
                .extract()
                .as(MetricResponseDto.class);

        var metrics = given()
                .auth()
                .oauth2(getAccessToken("admin"))
                .get("/{project_id}/metrics", "777536");

        var paged = metrics.body().as(PageResource.class);

        assertEquals(1, paged.getTotalElements());

        var informativeResponse = given()
                .get("/{project_id}/metrics", "777536")
                .then()
                .assertThat()
                .statusCode(403)
                .extract()
                .as(InformativeResponse.class);

        assertEquals(ApiMessage.UNAUTHORIZED_CLIENT.message, informativeResponse.message);
    }

    @Test
    @TestSecurity(user = "provider_admin")
    @OidcSecurity(introspectionRequired = true,
            introspection = {
                    @TokenIntrospection(key = "voperson_id", value = "provider_admin@example.org"),
                    @TokenIntrospection(key = "sub", value = "provider_admin@example.org"),
                    @TokenIntrospection(key = "iss", value = "http://localhost:58080/realms/quarkus")
            },
            userinfo = {
                    @UserInfo(key = "name", value = "provider_admin"),
                    @UserInfo(key = "email", value = "provider_admin@example.org")
            },
            claims = {
                    @Claim(key = "entitlements", value = "[\\\"urn:geant:sandbox.eosc-beyond.eu:core:integration:group:accounting:777536:grnet:role=admin\\\"]"),
            }

    )
    public void metricsUnderAProvider(){

        projectRepository.associateProjectWithProviders("777536", Set.of("grnet"));

        var metricDefinitionResponse = createMetricDefinition("admin");

        var installation = createInstallationAuth(metricDefinitionResponse.id, "admin");

        var metric = new MetricRequestDto();
        metric.start = "2022-01-05T09:13:07Z";
        metric.end = "2022-01-05T09:14:07Z";
        metric.value = 10.8;
        metric.metricDefinitionId = metricDefinitionResponse.id;

        // creating and assigning a new Metric
        given()
                .auth()
                .oauth2(getAccessToken("admin"))
                .basePath("accounting-system/installations")
                .body(metric)
                .contentType(ContentType.JSON)
                .post("/{installationId}/metrics", installation.id)
                .then()
                .assertThat()
                .statusCode(201)
                .extract()
                .as(MetricResponseDto.class);

        var projectAdminMetrics = given()
                .auth()
                .oauth2(getAccessToken("admin"))
                .get("/{project_id}/providers/{provider_id}/metrics", "777536", "grnet");

        var projectAdminMetricsPagination = projectAdminMetrics.body().as(PageResource.class);

        assertEquals(1, projectAdminMetricsPagination.getTotalElements());

        var informativeResponse = given()
                .get("/{project_id}/providers/{provider_id}/metrics", "777536", "grnet")
                .then()
                .assertThat()
                .statusCode(403)
                .extract()
                .as(InformativeResponse.class);

        assertEquals(ApiMessage.UNAUTHORIZED_CLIENT.message, informativeResponse.message);

        var providerAdminMetrics = given()
                .auth()
                .oauth2(getAccessToken("admin"))
                .get("/{project_id}/providers/{provider_id}/metrics", "777536", "grnet");

        var providerAdminMetricsPagination = providerAdminMetrics.body().as(PageResource.class);

        assertEquals(1, providerAdminMetricsPagination.getTotalElements());
    }

    @Test
    @TestSecurity(user = "provider_admin")
    @OidcSecurity(introspectionRequired = true,
            introspection = {
                    @TokenIntrospection(key = "voperson_id", value = "provider_admin@example.org"),
                    @TokenIntrospection(key = "sub", value = "provider_admin@example.org"),
                    @TokenIntrospection(key = "iss", value = "http://localhost:58080/realms/quarkus")
            },
            userinfo = {
                    @UserInfo(key = "name", value = "provider_admin"),
                    @UserInfo(key = "email", value = "provider_admin@example.org")
            }
    )
    public void metricsUnderAnInstallation(){

        projectRepository.associateProjectWithProviders("777536", Set.of("grnet"));

        var metricDefinitionResponse = createMetricDefinition("admin");

        // provider_admin@example.org can be able to create a Provider Installation

        var installation = createInstallation(metricDefinitionResponse.id);

        var metric = new MetricRequestDto();
        metric.start = "2022-01-05T09:13:07Z";
        metric.end = "2022-01-05T09:14:07Z";
        metric.value = 10.8;
        metric.metricDefinitionId = metricDefinitionResponse.id;

        // provider_admin@example.org can be able to assign a Metric to Provider Installation

        // creating and assigning a new Metric
        given()
                .auth()
                .oauth2(getAccessToken("admin"))
                .basePath("accounting-system/installations")
                .body(metric)
                .contentType(ContentType.JSON)
                .post("/{installationId}/metrics", installation.id)
                .then()
                .assertThat()
                .statusCode(201)
                .extract()
                .as(MetricResponseDto.class);


        var providerAdminMetrics = given()
                .auth()
                .oauth2(getAccessToken("admin"))
                .get("/{project_id}/providers/{provider_id}/metrics", "777536", "grnet");

        var providerAdminMetricsPagination = providerAdminMetrics.body().as(PageResource.class);

        assertEquals(1, providerAdminMetricsPagination.getTotalElements());

        var informativeResponse = given()
                .auth()
                .oauth2(getAccessToken("installationadmin"))
                .get("/{project_id}/providers/{provider_id}/metrics", "777536", "grnet")
                .then()
                .assertThat()
                .statusCode(403)
                .extract()
                .as(InformativeResponse.class);

        assertEquals(ApiMessage.UNAUTHORIZED_CLIENT.message, informativeResponse.message);

        var informativeResponseFromInstallation = given()
                .basePath("accounting-system/installations")
                .auth()
                .oauth2(getAccessToken("installationadmin"))
                .get("/{installation_id}/metrics", installation.id)
                .then()
                .assertThat()
                .statusCode(403)
                .extract()
                .as(InformativeResponse.class);

        assertEquals(ApiMessage.UNAUTHORIZED_CLIENT.message, informativeResponseFromInstallation.message);


        var installationAdminMetrics = given()
                .basePath("accounting-system/installations")
                .auth()
                .oauth2(getAccessToken("admin"))
                .get("/{installation_id}/metrics", installation.id);

        var installationAdminMetricsPagination = installationAdminMetrics.body().as(PageResource.class);

        assertEquals(1, installationAdminMetricsPagination.getTotalElements());
    }

    private InstallationResponseDto createInstallationAuth(InstallationRequestDto request, String user){

        return given()
                .auth()
                .oauth2(getAccessToken(user))
                .basePath("accounting-system/installations")
                .body(request)
                .contentType(ContentType.JSON)
                .post()
                .then()
                .assertThat()
                .statusCode(201)
                .extract()
                .as(InstallationResponseDto.class);
    }

    private MetricDefinitionResponseDto createMetricDefinition(MetricDefinitionRequestDto request, String user){

        return given()
                .auth()
                .oauth2(getAccessToken(user))
                .basePath("accounting-system/metric-definitions")
                .body(request)
                .contentType(ContentType.JSON)
                .post()
                .then()
                .assertThat()
                .statusCode(201)
                .extract()
                .as(MetricDefinitionResponseDto.class);
    }

    private InstallationResponseDto createInstallationAuth(String metricDefinitionId, String user){

        var request = new InstallationRequestDto();

        request.project = "777536";
        request.organisation = "grnet";
        request.infrastructure = "okeanos-knossos";
        request.installation = "SECOND";
        request.unitOfAccess = metricDefinitionId;


        return given()
                .auth()
                .oauth2(getAccessToken(user))
                .basePath("accounting-system/installations")
                .body(request)
                .contentType(ContentType.JSON)
                .post()
                .then()
                .assertThat()
                .statusCode(201)
                .extract()
                .as(InstallationResponseDto.class);
    }

    private InstallationResponseDto createInstallation(String metricDefinitionId){

        var request = new InstallationRequestDto();

        request.project = "777536";
        request.organisation = "grnet";
        request.infrastructure = "okeanos-knossos";
        request.installation = "SECOND";
        request.unitOfAccess = metricDefinitionId;


        return given()
                .auth()
                .oauth2(getAccessToken("admin"))
                .basePath("accounting-system/installations")
                .body(request)
                .contentType(ContentType.JSON)
                .post()
                .then()
                .assertThat()
                .statusCode(201)
                .extract()
                .as(InstallationResponseDto.class);
    }

    private MetricDefinitionResponseDto createMetricDefinition(String user){

        var requestForMetricDefinition = new MetricDefinitionRequestDto();

        requestForMetricDefinition.metricName = "metric";
        requestForMetricDefinition.metricDescription = "description";
        requestForMetricDefinition.unitType = "TB";
        requestForMetricDefinition.metricType = "aggregated";


        return given()
                .auth()
                .oauth2(getAccessToken(user))
                .basePath("accounting-system/metric-definitions")
                .body(requestForMetricDefinition)
                .contentType(ContentType.JSON)
                .post()
                .then()
                .assertThat()
                .statusCode(201)
                .extract()
                .as(MetricDefinitionResponseDto.class);
    }
}
