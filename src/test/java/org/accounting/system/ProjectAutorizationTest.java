package org.accounting.system;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import io.quarkus.test.keycloak.client.KeycloakTestClient;
import io.quarkus.test.security.TestSecurity;
import io.quarkus.test.security.oidc.OidcSecurity;
import io.quarkus.test.security.oidc.TokenIntrospection;
import io.quarkus.test.security.oidc.UserInfo;
import io.restassured.http.ContentType;
import org.accounting.system.clients.ProviderClient;
import org.accounting.system.clients.responses.eoscportal.Response;
import org.accounting.system.clients.responses.eoscportal.Total;
import org.accounting.system.dtos.InformativeResponse;
import org.accounting.system.dtos.acl.role.RoleAccessControlRequestDto;
import org.accounting.system.dtos.client.ClientResponseDto;
import org.accounting.system.dtos.installation.InstallationRequestDto;
import org.accounting.system.dtos.installation.InstallationResponseDto;
import org.accounting.system.dtos.metric.MetricRequestDto;
import org.accounting.system.dtos.metric.MetricResponseDto;
import org.accounting.system.dtos.metricdefinition.MetricDefinitionRequestDto;
import org.accounting.system.dtos.metricdefinition.MetricDefinitionResponseDto;
import org.accounting.system.dtos.pagination.PageResource;
import org.accounting.system.dtos.project.AssociateProjectProviderRequestDto;
import org.accounting.system.endpoints.ProjectEndpoint;
import org.accounting.system.entities.projections.normal.ProjectProjection;
import org.accounting.system.enums.ApiMessage;
import org.accounting.system.mappers.ProviderMapper;
import org.accounting.system.repositories.client.ClientAccessAlwaysRepository;
import org.accounting.system.repositories.client.ClientRepository;
import org.accounting.system.repositories.metric.MetricRepository;
import org.accounting.system.repositories.metricdefinition.MetricDefinitionRepository;
import org.accounting.system.repositories.project.ProjectRepository;
import org.accounting.system.repositories.provider.ProviderRepository;
import org.accounting.system.services.SystemAdminService;
import org.accounting.system.services.client.ClientService;
import org.accounting.system.util.Utility;
import org.accounting.system.wiremock.ProjectWireMockServer;
import org.accounting.system.wiremock.ProviderWireMockServer;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.json.simple.parser.ParseException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import javax.inject.Inject;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;


@QuarkusTest
@TestProfile(AccountingSystemTestProfile.class)
@TestHTTPEndpoint(ProjectEndpoint.class)
@QuarkusTestResource(ProjectWireMockServer.class)
@QuarkusTestResource(ProviderWireMockServer.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ProjectAutorizationTest {

    @Inject
    @RestClient
    ProviderClient providerClient;

    @Inject
    ProviderRepository providerRepository;

    @Inject
    MetricDefinitionRepository metricDefinitionRepository;

    @Inject
    SystemAdminService systemAdminService;

    @Inject
    ProjectRepository projectRepository;

    @Inject
    MetricRepository metricRepository;

    @Inject
    Utility utility;

    @Inject
    ClientService clientService;

    @Inject
    ClientRepository clientRepository;

    @Inject
    ClientAccessAlwaysRepository clientAccessAlwaysRepository;

    KeycloakTestClient keycloakClient = new KeycloakTestClient();

    @BeforeAll
    public void setup() throws ExecutionException, InterruptedException, ParseException {

        Total total = providerClient.getTotalNumberOfProviders("all").toCompletableFuture().get();

        Response response = providerClient.getAll("all", total.total).toCompletableFuture().get();

        providerRepository.persistOrUpdate(ProviderMapper.INSTANCE.eoscProvidersToProviders(response.results));

        clientRepository.addSystemAdmin(utility.getIdFromToken(keycloakClient.getAccessToken("admin").split("\\.")[1]), "admin", "admin@email.com");

        clientService.register("project_admin@example.org", "project_admin", "project_admin@example.org");

        clientAccessAlwaysRepository.assignRolesToRegisteredClient(utility.getIdFromToken(keycloakClient.getAccessToken("admin").split("\\.")[1]), Set.of("collection_owner"));
    }

    @BeforeEach
    public void before() throws ParseException {

        metricDefinitionRepository.deleteAll();
        projectRepository.deleteAll();
        metricRepository.deleteAll();

        String sub = utility.getIdFromToken(keycloakClient.getAccessToken("admin").split("\\.")[1]);
        systemAdminService.registerProjectsToAccountingService(Set.of("777536"), sub);
    }

    @Test
    public void clientPermissions(){

        var page = given()
                .auth()
                .oauth2(getAccessToken("admin"))
                .basePath("accounting-system/clients")
                .get("/me")
                .then()
                .assertThat()
                .statusCode(200)
                .extract()
                .as(PageResource.class);

        assertEquals(1, page.getTotalElements());
    }

    @Test
    @TestSecurity(user = "project_admin")
    @OidcSecurity(introspectionRequired = true,
            introspection = {
                    @TokenIntrospection(key = "voperson_id", value = "project_admin@example.org"),
                    @TokenIntrospection(key = "sub", value = "project_admin@example.org")
            },
            userinfo = {
                    @UserInfo(key = "name", value = "project_admin"),
                    @UserInfo(key = "email", value = "project_admin@example.org")
            }
    )
    public void clientGrantsProjectAccessToOtherClient(){

        // initially the client project_admin has no access to 777536
        given()
                .get("/{id}", "777536")
                .then()
                .assertThat()
                .statusCode(403)
                .extract()
                .as(InformativeResponse.class);

        // client system_admin gives access to client project_admin

        var acl = new RoleAccessControlRequestDto();

        acl.roles = Set.of("project_admin");

        given()
                .auth()
                .oauth2(getAccessToken("admin"))
                .contentType(ContentType.JSON)
                .body(acl)
                .post("/{id}/acl/{who}", "777536", "project_admin@example.org")
                .then()
                .assertThat()
                .statusCode(200)
                .extract()
                .as(InformativeResponse.class);

        // client project_admin has now access Project 777536

        var project = given()
                .get("/{id}", "777536")
                .then()
                .assertThat()
                .statusCode(200)
                .extract()
                .as(ProjectProjection.class);

        assertEquals(project.id, "777536");
        assertEquals(project.acronym, "EOSC-hub");
        assertEquals(project.title, "Integrating and managing services for the European Open Science Cloud");
        assertEquals(project.startDate, "2018-01-01");
        assertEquals(project.endDate, "2021-03-31");
        assertEquals(project.callIdentifier, "H2020-EINFRA-2017");
    }

    @Test
    @TestSecurity(user = "project_admin")
    @OidcSecurity(introspectionRequired = true,
            introspection = {
                    @TokenIntrospection(key = "voperson_id", value = "project_admin@example.org"),
                    @TokenIntrospection(key = "sub", value = "project_admin@example.org")
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
                    @TokenIntrospection(key = "sub", value = "project_admin@example.org")
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
                    @TokenIntrospection(key = "sub", value = "provider_admin@example.org")
            },
            userinfo = {
                    @UserInfo(key = "name", value = "provider_admin"),
                    @UserInfo(key = "email", value = "provider_admin@example.org")
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

        grantProviderAccess("777536", "grnet", "admin", "provider_admin@example.org");

        var providerAdminMetrics = given()
                .get("/{project_id}/providers/{provider_id}/metrics", "777536", "grnet");

        var providerAdminMetricsPagination = providerAdminMetrics.body().as(PageResource.class);

        assertEquals(1, providerAdminMetricsPagination.getTotalElements());
    }

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
    public void metricsUnderAnInstallation(){

        projectRepository.associateProjectWithProviders("777536", Set.of("grnet"));

        // admin grant Provider Access to provider_admin@example.org
        grantProviderAccess("777536", "grnet", "admin","provider_admin@example.org");

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
                .basePath("accounting-system/installations")
                .body(metric)
                .contentType(ContentType.JSON)
                .post("/{installationId}/metrics", installation.id)
                .then()
                .assertThat()
                .statusCode(201)
                .extract()
                .as(MetricResponseDto.class);

        // provider_admin@example.org can be able to read the Provider Metrics

        var providerAdminMetrics = given()
                .auth()
                .oauth2(getAccessToken("admin"))
                .get("/{project_id}/providers/{provider_id}/metrics", "777536", "grnet");

        var providerAdminMetricsPagination = providerAdminMetrics.body().as(PageResource.class);

        assertEquals(1, providerAdminMetricsPagination.getTotalElements());

        // installationadmin cannot be able to read the Provider Metrics
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

        // installationadmin cannot be able to read the Installation Metrics
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

        // installationadmin will be registered into Accounting System
        var client = given()
                .basePath("accounting-system/clients")
                .auth()
                .oauth2(getAccessToken("installationadmin"))
                .post()
                .then()
                .assertThat()
                .statusCode(200)
                .extract()
                .as(ClientResponseDto.class);

        //provider_admin grants access to installationadmin to manage the Provider Installation
        grantInstallationAccess(installation.id, client.id);

        var installationAdminMetrics = given()
                .basePath("accounting-system/installations")
                .auth()
                .oauth2(getAccessToken("installationadmin"))
                .get("/{installation_id}/metrics", installation.id);

        var installationAdminMetricsPagination = installationAdminMetrics.body().as(PageResource.class);

        assertEquals(1, installationAdminMetricsPagination.getTotalElements());
    }

    private void grantProviderAccess(String project, String provider, String user, String who){

        var acl = new RoleAccessControlRequestDto();

        acl.roles = Set.of("provider_admin");

        given()
                .auth()
                .oauth2(getAccessToken(user))
                .basePath("accounting-system/projects")
                .contentType(ContentType.JSON)
                .body(acl)
                .post("/{project_id}/providers/{provider_id}/acl/{who}", project, provider, who)
                .then()
                .assertThat()
                .statusCode(200)
                .extract()
                .as(InformativeResponse.class);
    }

    private void grantInstallationAccess(String installation, String who){

        var acl = new RoleAccessControlRequestDto();

        acl.roles = Set.of("installation_admin");

        given()
                .basePath("accounting-system/installations")
                .contentType(ContentType.JSON)
                .body(acl)
                .post("/{installation_id}/acl/{who}", installation, who)
                .then()
                .assertThat()
                .statusCode(200)
                .extract()
                .as(InformativeResponse.class);

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

    protected String getAccessToken(String userName) {
        return keycloakClient.getAccessToken(userName);
    }
}
