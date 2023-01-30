package org.accounting.system;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import io.quarkus.test.junit.mockito.InjectMock;
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
import org.accounting.system.dtos.installation.InstallationRequestDto;
import org.accounting.system.dtos.installation.InstallationResponseDto;
import org.accounting.system.dtos.metricdefinition.MetricDefinitionRequestDto;
import org.accounting.system.dtos.metricdefinition.MetricDefinitionResponseDto;
import org.accounting.system.dtos.pagination.PageResource;
import org.accounting.system.endpoints.InstallationEndpoint;
import org.accounting.system.enums.ApiMessage;
import org.accounting.system.mappers.ProviderMapper;
import org.accounting.system.repositories.client.ClientAccessAlwaysRepository;
import org.accounting.system.repositories.metricdefinition.MetricDefinitionRepository;
import org.accounting.system.repositories.project.ProjectRepository;
import org.accounting.system.repositories.provider.ProviderRepository;
import org.accounting.system.services.ReadPredefinedTypesService;
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
import org.mockito.Mockito;

import javax.inject.Inject;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;

@QuarkusTest
@TestProfile(AccountingSystemTestProfile.class)
@TestHTTPEndpoint(InstallationEndpoint.class)
@QuarkusTestResource(ProjectWireMockServer.class)
@QuarkusTestResource(ProviderWireMockServer.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class InstallationAuthorizationTest {

    @Inject
    MetricDefinitionRepository metricDefinitionRepository;

    @Inject
    ProviderRepository providerRepository;

    @InjectMock
    ReadPredefinedTypesService readPredefinedTypesService;

    @Inject
    ProjectRepository projectRepository;

    @Inject
    SystemAdminService systemAdminService;

    @Inject
    @RestClient
    ProviderClient providerClient;

    @Inject
    Utility utility;

    @Inject
    ClientService clientService;

    @Inject
    ClientAccessAlwaysRepository clientAccessAlwaysRepository;

    KeycloakTestClient keycloakClient = new KeycloakTestClient();

    @BeforeAll
    public void setup() throws ExecutionException, InterruptedException, ParseException {

        Total total = providerClient.getTotalNumberOfProviders().toCompletableFuture().get();

        Response response = providerClient.getAll(total.total).toCompletableFuture().get();

        providerRepository.persistOrUpdate(ProviderMapper.INSTANCE.eoscProvidersToProviders(response.results));

        clientService.register(utility.getIdFromToken(keycloakClient.getAccessToken("admin").split("\\.")[1]), "admin", "admin@email.com");

        clientService.register("provider_admin@example.org", "provider_admin", "provider_admin@example.org");

        clientAccessAlwaysRepository.assignRolesToRegisteredClient(utility.getIdFromToken(keycloakClient.getAccessToken("admin").split("\\.")[1]),Set.of("collection_owner"));
    }

    @BeforeEach
    public void before() throws ParseException {

        metricDefinitionRepository.deleteAll();
        projectRepository.deleteAll();

        String sub = utility.getIdFromToken(keycloakClient.getAccessToken("admin").split("\\.")[1]);

        //We are going to register the EOSC-hub project from OpenAire API
        systemAdminService.accessListOfProjects(Set.of("777536"), sub);

        projectRepository.associateProjectWithProviders("777536", Set.of("grnet", "sites"));
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
    public void clientGrantAccessToOtherClientToManageAProvider(){

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

        //the second installation has been created by installationcreator

        var request1= new InstallationRequestDto();

        request1.project = "777536";
        request1.organisation = "sites";
        request1.infrastructure = "okeanos-knossos";
        request1.installation = "GRNET-KNS";
        request1.unitOfAccess = metricDefinitionResponse.id;

        var installation1 = createInstallation(request1, "admin");

        //because admin can access all installations, it can retrieve the installation created by installationcreator

        var response = fetchInstallation(installation1.id, "admin");

        var installationResponseDto = response
                .then()
                .assertThat()
                .statusCode(200)
                .extract()
                .as(InstallationResponseDto.class);

        assertEquals(installation1.id, installationResponseDto.id);

        //because creator can access only its Installations, it cannot can retrieve the installation created by admin

        var response1 = fetchInstallation(installation.id, "installationcreator");

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

        Mockito.when(readPredefinedTypesService.searchForUnitType(any())).thenReturn(Optional.of("SECOND"));
        Mockito.when(readPredefinedTypesService.searchForMetricType(any())).thenReturn(Optional.of("Aggregated"));

        var requestForMetricDefinition = new MetricDefinitionRequestDto();

        requestForMetricDefinition.metricName = "metric";
        requestForMetricDefinition.metricDescription = "description";
        requestForMetricDefinition.unitType = "SECOND";
        requestForMetricDefinition.metricType = "Aggregated";


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
