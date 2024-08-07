package org.accounting.system;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import io.quarkus.test.security.TestSecurity;
import io.quarkus.test.security.oidc.OidcSecurity;
import io.quarkus.test.security.oidc.TokenIntrospection;
import io.quarkus.test.security.oidc.UserInfo;
import io.restassured.http.ContentType;
import org.accounting.system.dtos.InformativeResponse;
import org.accounting.system.dtos.acl.role.RoleAccessControlRequestDto;
import org.accounting.system.dtos.installation.InstallationRequestDto;
import org.accounting.system.dtos.installation.InstallationResponseDto;
import org.accounting.system.dtos.metricdefinition.MetricDefinitionRequestDto;
import org.accounting.system.dtos.metricdefinition.MetricDefinitionResponseDto;
import org.accounting.system.dtos.pagination.PageResource;
import org.accounting.system.endpoints.InstallationEndpoint;
import org.accounting.system.enums.ApiMessage;
import org.accounting.system.wiremock.ProjectWireMockServer;
import org.accounting.system.wiremock.ProviderWireMockServer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.Set;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
@TestProfile(AccountingSystemTestProfile.class)
@TestHTTPEndpoint(InstallationEndpoint.class)
@QuarkusTestResource(ProjectWireMockServer.class)
@QuarkusTestResource(ProviderWireMockServer.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class InstallationAuthorizationTest extends PrepareTest {

    @Test
    @TestSecurity(user = "provider_admin")
    @OidcSecurity(introspectionRequired = true,
            introspection = {
                    @TokenIntrospection(key = "voperson_id", value = "provider_admin@example.org"),
                    @TokenIntrospection(key = "sub", value = "provider_admin@example.org")
            },
            userinfo = {
                    @UserInfo(key = "name", value = "provider_admin"),
                    @UserInfo(key = "email", value = "provider_admin@example.org")
            }
    )
    public void clientGrantAccessToOtherClientToManageAProvider(){

        projectRepository.associateProjectWithProviders("777536", Set.of("grnet", "sites"));

        // admin user will submit two installations

        //the first installation has been created by admin

        var metricDefinitionResponse = createMetricDefinition("admin");

        var request = createInstallationRequest("777536", "grnet", "okeanos-knossos", "SECOND", metricDefinitionResponse.id);

        createInstallation(request, "admin");

        //the second installation has been created by admin

        var request1 = createInstallationRequest("777536", "grnet", "okeanos-knossos", "GRNET-KNS", metricDefinitionResponse.id);

        createInstallation(request1, "admin");

        //the third installation has been created by provider_admin

        var acl = new RoleAccessControlRequestDto();

        acl.roles = Set.of("provider_admin");

        given()
                .auth()
                .oauth2(getAccessToken("admin"))
                .basePath("accounting-system/projects")
                .contentType(ContentType.JSON)
                .body(acl)
                .post("/{project_id}/providers/{provider_id}/acl/{who}", "777536", "sites", "provider_admin@example.org")
                .then()
                .assertThat()
                .statusCode(200)
                .extract()
                .as(InformativeResponse.class);

        var request2 = createInstallationRequest("777536", "sites", "okeanos-knossos", "okeanos", metricDefinitionResponse.id);

        given()
                .body(request2)
                .contentType(ContentType.JSON)
                .post()
                .then()
                .assertThat()
                .statusCode(201)
                .extract()
                .as(InstallationResponseDto.class);

        //because admin can access all installations the size of list should be 3

        PageResource pageResourceAdmin = given()
                .basePath("accounting-system/projects")
                .auth()
                .oauth2(getAccessToken("admin"))
                .get("/{project_id}/installations", "777536")
                .body()
                .as(PageResource.class);

        assertEquals(3, pageResourceAdmin.getTotalElements());

        given()
                .basePath("accounting-system/projects")
                .get("/{project_id}/installations", "777536")
                .then()
                .assertThat()
                .statusCode(403);


        PageResource pageResourceProviderAdmin = given()
                .basePath("accounting-system/projects")
                .auth()
                .oauth2(getAccessToken("admin"))
                .get("/{project_id}/providers/{provider_id}/installations", "777536", "sites")
                .body()
                .as(PageResource.class);


        assertEquals(1, pageResourceProviderAdmin.getTotalElements());
    }

    @Test
    public void getInstallation(){

        projectRepository.associateProjectWithProviders("777536", Set.of("grnet", "sites"));

        // admin user will submit one installation

        //the first installation has been created by admin

        var metricDefinitionResponse = createMetricDefinition("admin");

        var request= new InstallationRequestDto();

        request.project = "777536";
        request.organisation = "grnet";
        request.infrastructure = "okeanos-knossos";
        request.installation = "SECOND";
        request.unitOfAccess = metricDefinitionResponse.id;

        var installation = createInstallation(request, "admin");

        //the second installation has been created by admin

        var request1= new InstallationRequestDto();

        request1.project = "777536";
        request1.organisation = "sites";
        request1.infrastructure = "okeanos-knossos";
        request1.installation = "GRNET-KNS";
        request1.unitOfAccess = metricDefinitionResponse.id;

        var installation1 = createInstallation(request1, "admin");

        //admin can access all installations

        var response = fetchInstallation(installation1.id, "admin");

        var installationResponseDto = response
                .then()
                .assertThat()
                .statusCode(200)
                .extract()
                .as(InstallationResponseDto.class);

        assertEquals(installation1.id, installationResponseDto.id);

        //inspector cannot retrieve the installation created by admin

        var response1 = fetchInstallation(installation.id, "inspector");

        var informativeResponse = response1
                .then()
                .assertThat()
                .statusCode(403)
                .extract()
                .as(InformativeResponse.class);

        assertEquals(ApiMessage.UNAUTHORIZED_CLIENT.message, informativeResponse.message);
    }

    private InstallationResponseDto createInstallation(InstallationRequestDto request, String user){

        return given()
                .auth()
                .oauth2(getAccessToken(user))
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

    private InstallationRequestDto createInstallationRequest(String project, String organisation, String infrastructure, String installation, String unitOfAccess){

        var requestDto= new InstallationRequestDto();

        requestDto.project = project;
        requestDto.organisation = organisation;
        requestDto.infrastructure = infrastructure;
        requestDto.installation = installation;
        requestDto.unitOfAccess = unitOfAccess;

        return requestDto;
    }

    private io.restassured.response.Response fetchInstallation(String id, String user){

        return given()
                .auth()
                .oauth2(getAccessToken(user))
                .get("/{id}", id);
    }

    protected String getAccessToken(String userName) {
        return keycloakClient.getAccessToken(userName);
    }

}
