package org.accounting.system;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import io.quarkus.test.keycloak.client.KeycloakTestClient;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import org.accounting.system.clients.ProviderClient;
import org.accounting.system.clients.responses.eoscportal.Response;
import org.accounting.system.clients.responses.eoscportal.Total;
import org.accounting.system.dtos.InformativeResponse;
import org.accounting.system.dtos.admin.ProjectRegistrationRequest;
import org.accounting.system.dtos.installation.InstallationRequestDto;
import org.accounting.system.dtos.installation.InstallationResponseDto;
import org.accounting.system.dtos.metricdefinition.MetricDefinitionRequestDto;
import org.accounting.system.dtos.metricdefinition.MetricDefinitionResponseDto;
import org.accounting.system.endpoints.InstallationEndpoint;
import org.accounting.system.enums.ApiMessage;
import org.accounting.system.mappers.ProviderMapper;
import org.accounting.system.repositories.metric.MetricRepository;
import org.accounting.system.repositories.metricdefinition.MetricDefinitionRepository;
import org.accounting.system.repositories.project.ProjectRepository;
import org.accounting.system.repositories.provider.ProviderRepository;
import org.accounting.system.wiremock.KeycloakWireMockServer;
import org.accounting.system.wiremock.ProjectWireMockServer;
import org.accounting.system.wiremock.ProviderWireMockServer;
import org.accounting.system.wiremock.TokenWireMockServer;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.json.simple.parser.ParseException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.Set;
import java.util.concurrent.ExecutionException;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
@TestProfile(AccountingSystemTestProfile.class)
@TestHTTPEndpoint(InstallationEndpoint.class)
@QuarkusTestResource(ProjectWireMockServer.class)
@QuarkusTestResource(ProviderWireMockServer.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@QuarkusTestResource(KeycloakWireMockServer.class)
@QuarkusTestResource(TokenWireMockServer.class)
public class InstallationAuthorizationTest {

    @Inject
    @RestClient
    ProviderClient providerClient;

    @Inject
    ProviderRepository providerRepository;

    @Inject
    ProjectRepository projectRepository;

    @Inject
    MetricDefinitionRepository metricDefinitionRepository;

    @Inject
    MetricRepository metricRepository;

    KeycloakTestClient keycloakClient = new KeycloakTestClient();

    protected String getAccessToken(String userName) {
        return keycloakClient.getAccessToken(userName);
    }

    @BeforeAll
    public void setup() throws ExecutionException, InterruptedException, ParseException {

        Total total = providerClient.getTotalNumberOfProviders("all").toCompletableFuture().get();

        Response response = providerClient.getAll("all", total.total).toCompletableFuture().get();

        providerRepository.persistOrUpdate(ProviderMapper.INSTANCE.eoscProvidersToProviders(response.results));
    }

    @BeforeEach
    public void before() throws ParseException {

        metricDefinitionRepository.deleteAll();
        projectRepository.deleteAll();
        metricRepository.deleteAll();

        var request = new ProjectRegistrationRequest();
        request.projects = Set.of("777536", "101017567");

        given()
                .auth()
                .oauth2(getAccessToken("admin"))
                .basePath("accounting-system/admin")
                .body(request)
                .contentType(ContentType.JSON)
                .post("/register-projects")
                .then()
                .assertThat()
                .statusCode(200);
    }

    @Test
    public void getInstallation(){

        projectRepository.associateProjectWithProvider("777536","grnet");
        projectRepository.associateProjectWithProvider("777536", "sites");

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
}
