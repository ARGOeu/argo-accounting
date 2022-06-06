package org.accounting.system;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import io.quarkus.test.junit.mockito.InjectMock;
import io.quarkus.test.keycloak.client.KeycloakTestClient;
import io.restassured.http.ContentType;
import org.accounting.system.clients.ProviderClient;
import org.accounting.system.clients.responses.eoscportal.Response;
import org.accounting.system.clients.responses.eoscportal.Total;
import org.accounting.system.dtos.InformativeResponse;
import org.accounting.system.dtos.installation.InstallationRequestDto;
import org.accounting.system.dtos.installation.InstallationResponseDto;
import org.accounting.system.dtos.metricdefinition.MetricDefinitionRequestDto;
import org.accounting.system.dtos.metricdefinition.MetricDefinitionResponseDto;
import org.accounting.system.dtos.pagination.PageResource;
import org.accounting.system.endpoints.InstallationEndpoint;
import org.accounting.system.mappers.ProviderMapper;
import org.accounting.system.repositories.installation.InstallationRepository;
import org.accounting.system.repositories.metricdefinition.MetricDefinitionRepository;
import org.accounting.system.repositories.provider.ProviderRepository;
import org.accounting.system.services.ProjectService;
import org.accounting.system.services.ReadPredefinedTypesService;
import org.accounting.system.wiremock.ProjectWireMockServer;
import org.accounting.system.wiremock.ProviderWireMockServer;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mockito;

import javax.inject.Inject;
import java.util.Optional;
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
    InstallationRepository installationRepository;

    @Inject
    MetricDefinitionRepository metricDefinitionRepository;

    @Inject
    ProviderRepository providerRepository;

    @InjectMock
    ReadPredefinedTypesService readPredefinedTypesService;

    @Inject
    ProjectService projectService;

    @Inject
    @RestClient
    ProviderClient providerClient;

    KeycloakTestClient keycloakClient = new KeycloakTestClient();

    @BeforeAll
    public void setup() throws ExecutionException, InterruptedException {

        Total total = providerClient.getTotalNumberOfProviders().toCompletableFuture().get();

        Response response = providerClient.getAll(total.total).toCompletableFuture().get();

        providerRepository.persistOrUpdate(ProviderMapper.INSTANCE.eoscProvidersToProviders(response.results));

        //We are going to register the EOSC-hub project from OpenAire API
        projectService.getById("777536");
    }

    @BeforeEach
    public void before() {
        installationRepository.deleteAll();
        metricDefinitionRepository.deleteAll();
    }

    @Test
    public void getAllInstallations(){

        // admin user will submit two installations

        //the first installation has been created by admin

        Mockito.when(readPredefinedTypesService.searchForUnitType(any())).thenReturn(Optional.of("SECOND"));
        Mockito.when(readPredefinedTypesService.searchForMetricType(any())).thenReturn(Optional.of("Aggregated"));
        var requestForMetricDefinition = new MetricDefinitionRequestDto();

        requestForMetricDefinition.metricName = "metric";
        requestForMetricDefinition.metricDescription = "description";
        requestForMetricDefinition.unitType = "SECOND";
        requestForMetricDefinition.metricType = "Aggregated";

        var metricDefinitionResponse = createMetricDefinition(requestForMetricDefinition, "admin");

        var request= new InstallationRequestDto();

        request.project = "777536";
        request.organisation = "grnet";
        request.infrastructure = "okeanos-knossos";
        request.installation = "SECOND";
        request.unitOfAccess = metricDefinitionResponse.id;

        createInstallation(request, "admin");

        //the second installation has been created by admin

        var request1= new InstallationRequestDto();

        request1.project = "777536";
        request1.organisation = "sites";
        request1.infrastructure = "okeanos-knossos";
        request1.installation = "GRNET-KNS";
        request1.unitOfAccess = metricDefinitionResponse.id;

        createInstallation(request1, "admin");

        //the third installation has been created by installationcreator

        var request2= new InstallationRequestDto();

        request2.project = "777536";
        request2.organisation = "sites";
        request2.infrastructure = "okeanos-knossos";
        request2.installation = "okeanos";
        request2.unitOfAccess = metricDefinitionResponse.id;

        createInstallation(request2, "installationcreator");

        //because admin can access all installations the size of list should be 3

        PageResource pageResourceAdmin =  fetchAllInstallations("admin");

        assertEquals(3, pageResourceAdmin.totalElements);

        //because creator can access only its Installations the size of list should be 1

        PageResource pageResourceCreator =  fetchAllInstallations("installationcreator");

        assertEquals(1, pageResourceCreator.totalElements);
    }

    @Test
    public void getInstallation(){

        // admin user will submit one installation

        //the first installation has been created by admin

        Mockito.when(readPredefinedTypesService.searchForUnitType(any())).thenReturn(Optional.of("SECOND"));
        Mockito.when(readPredefinedTypesService.searchForMetricType(any())).thenReturn(Optional.of("Aggregated"));
        var requestForMetricDefinition = new MetricDefinitionRequestDto();

        requestForMetricDefinition.metricName = "metric";
        requestForMetricDefinition.metricDescription = "description";
        requestForMetricDefinition.unitType = "SECOND";
        requestForMetricDefinition.metricType = "Aggregated";

        var metricDefinitionResponse = createMetricDefinition(requestForMetricDefinition, "admin");

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

        assertEquals("You have no access to this entity : "+installation.id, informativeResponse.message);
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

    private MetricDefinitionResponseDto createMetricDefinition(MetricDefinitionRequestDto request, String user){

        return given()
                .auth()
                .oauth2(getAccessToken(user))
                .basePath("accounting-system/metric-definition")
                .body(request)
                .contentType(ContentType.JSON)
                .post()
                .then()
                .assertThat()
                .statusCode(201)
                .extract()
                .as(MetricDefinitionResponseDto.class);
    }

    private PageResource fetchAllInstallations(String user){

        return given()
                .auth()
                .oauth2(getAccessToken(user))
                .get()
                .body()
                .as(PageResource.class);
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
