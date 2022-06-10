package org.accounting.system;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import io.quarkus.test.junit.mockito.InjectMock;
import io.quarkus.test.keycloak.client.KeycloakTestClient;
import io.restassured.http.ContentType;
import org.accounting.system.clients.ProjectClient;
import org.accounting.system.clients.ProviderClient;
import org.accounting.system.clients.responses.eoscportal.Response;
import org.accounting.system.clients.responses.eoscportal.Total;
import org.accounting.system.clients.responses.openaire.OpenAireProject;
import org.accounting.system.dtos.InformativeResponse;
import org.accounting.system.dtos.installation.InstallationRequestDto;
import org.accounting.system.dtos.installation.InstallationResponseDto;
import org.accounting.system.dtos.metric.MetricRequestDto;
import org.accounting.system.dtos.metric.MetricResponseDto;
import org.accounting.system.dtos.metricdefinition.MetricDefinitionRequestDto;
import org.accounting.system.dtos.metricdefinition.MetricDefinitionResponseDto;
import org.accounting.system.dtos.project.ProjectResponseDto;
import org.accounting.system.endpoints.ProjectEndpoint;
import org.accounting.system.entities.Project;
import org.accounting.system.mappers.ProjectMapper;
import org.accounting.system.mappers.ProviderMapper;
import org.accounting.system.repositories.installation.InstallationRepository;
import org.accounting.system.repositories.metricdefinition.MetricDefinitionRepository;
import org.accounting.system.repositories.provider.ProviderRepository;
import org.accounting.system.services.HierarchicalRelationService;
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
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;


@QuarkusTest
@TestProfile(AccountingSystemTestProfile.class)
@TestHTTPEndpoint(ProjectEndpoint.class)
@QuarkusTestResource(ProjectWireMockServer.class)
@QuarkusTestResource(ProviderWireMockServer.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ProjectEndpointTest {

    @Inject
    @RestClient
    ProjectClient projectClient;

    @InjectMock
    ReadPredefinedTypesService readPredefinedTypesService;

    @Inject
    @RestClient
    ProviderClient providerClient;

    @Inject
    ProviderRepository providerRepository;

    @Inject
    InstallationRepository installationRepository;

    @Inject
    MetricDefinitionRepository metricDefinitionRepository;

    @Inject
    HierarchicalRelationService hierarchicalRelationService;

    @Inject
    ProjectService projectService;

    KeycloakTestClient keycloakClient = new KeycloakTestClient();

    @BeforeAll
    public void setup() throws ExecutionException, InterruptedException {

        Total total = providerClient.getTotalNumberOfProviders().toCompletableFuture().get();

        Response response = providerClient.getAll(total.total).toCompletableFuture().get();

        providerRepository.persistOrUpdate(ProviderMapper.INSTANCE.eoscProvidersToProviders(response.results));

        //Registering a project

        projectService.getById("777536");

        hierarchicalRelationService.createProjectProviderRelationship("777536", List.of("grnet"));
    }

    @BeforeEach
    public void before() {
        installationRepository.deleteAll();
        metricDefinitionRepository.deleteAll();
    }

    @Test
    public void retrieveOpenAireResponse() {

        //We are going to retrieve the EOSC-hub project from OpenAire API
        OpenAireProject project = projectClient.getById("777536","json");

        assertEquals(project.response.results.result.get(0).metadata.entity.project.code.value, "777536");
        assertEquals(project.response.results.result.get(0).metadata.entity.project.acronym.value, "EOSC-hub");
        assertEquals(project.response.results.result.get(0).metadata.entity.project.title.value, "Integrating and managing services for the European Open Science Cloud");
        assertEquals(project.response.results.result.get(0).metadata.entity.project.startDate.value, "2018-01-01");
        assertEquals(project.response.results.result.get(0).metadata.entity.project.endDate.value, "2021-03-31");
        assertEquals(project.response.results.result.get(0).metadata.entity.project.callIdentifier.value, "H2020-EINFRA-2017");
    }

    @Test
    public void openAireResponseToProject() {

        //We are going to retrieve the EOSC-hub project from OpenAire APIx
        OpenAireProject response = projectClient.getById("777536","json");

        Project project = ProjectMapper.INSTANCE.openAireResponseToProject(response);

        assertEquals(project.getId(), "777536");
        assertEquals(project.getAcronym(), "EOSC-hub");
        assertEquals(project.getTitle(), "Integrating and managing services for the European Open Science Cloud");
        assertEquals(project.getStartDate(), "2018-01-01");
        assertEquals(project.getEndDate(), "2021-03-31");
        assertEquals(project.getCallIdentifier(), "H2020-EINFRA-2017");
    }

    @Test
    public void fetchProjectNotAuthenticated() {

        var notAuthenticatedResponse = given()
                .auth()
                .oauth2("invalidToken")
                .post("/{id}", "447535")
                .thenReturn();

        assertEquals(401, notAuthenticatedResponse.statusCode());
    }

    @Test
    public void fetchProjectById(){

        var project = given()
                .auth()
                .oauth2(getAccessToken("admin"))
                .post("/{id}", "777536")
                .then()
                .assertThat()
                .statusCode(200)
                .extract()
                .as(ProjectResponseDto.class);

        assertEquals(project.id, "777536");
        assertEquals(project.acronym, "EOSC-hub");
        assertEquals(project.title, "Integrating and managing services for the European Open Science Cloud");
        assertEquals(project.startDate, "2018-01-01");
        assertEquals(project.endDate, "2021-03-31");
        assertEquals(project.callIdentifier, "H2020-EINFRA-2017");
    }

    @Test
    public void fetchProjectByIdNotFound(){

        var response = given()
                .auth()
                .oauth2(getAccessToken("admin"))
                .post("/{id}", "lalala")
                .then()
                .assertThat()
                .statusCode(404)
                .extract()
                .as(InformativeResponse.class);

        assertEquals(response.message, "Project with id {lalala} not found.");
    }

    @Test
    public void assignMetric(){

        //Registering an installation
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

        var metric = new MetricRequestDto();
        metric.start = "2022-01-05T09:13:07Z";
        metric.end = "2022-01-05T09:14:07Z";
        metric.value = 10.8;
        metric.metricDefinitionId = metricDefinitionResponse.id;

        // creating and assigning a new Metric
        var assignedMetric = given()
                .auth()
                .oauth2(getAccessToken("admin"))
                .body(metric)
                .contentType(ContentType.JSON)
                .post("/{projectId}/providers/{providerId}/installations/{installationId}/metrics", "777536", "grnet", installation.id)
                .then()
                .assertThat()
                .statusCode(201)
                .extract()
                .as(MetricResponseDto.class);

        assertEquals(metric.start, assignedMetric.start.toString());
        assertEquals(metric.end, assignedMetric.end.toString());
        assertEquals(metric.value, assignedMetric.value);
    }

    @Test
    public void assignMetricAndGeneratingConflict(){

        //Registering an installation
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

        var metric = new MetricRequestDto();
        metric.start = "2022-01-05T09:13:07Z";
        metric.end = "2022-01-05T09:14:07Z";
        metric.value = 10.8;
        metric.metricDefinitionId = metricDefinitionResponse.id;

        // creating and assigning a new Metric
        given()
                .auth()
                .oauth2(getAccessToken("admin"))
                .body(metric)
                .contentType(ContentType.JSON)
                .post("/{projectId}/providers/{providerId}/installations/{installationId}/metrics", "777536", "grnet", installation.id)
                .then()
                .assertThat()
                .statusCode(201)
                .extract()
                .as(MetricResponseDto.class);

        // Generating a conflict
        var conflict = given()
                .auth()
                .oauth2(getAccessToken("admin"))
                .body(metric)
                .contentType(ContentType.JSON)
                .post("/{projectId}/providers/{providerId}/installations/{installationId}/metrics", "777536", "grnet", installation.id)
                .then()
                .assertThat()
                .statusCode(409)
                .extract()
                .as(InformativeResponse.class);

        assertEquals(String.format("There is a Metric at {%s, %s, %s} with the following attributes : {%s, %s, %s, %s}",
                "EOSC-hub", "grnet", installation.installation,
                metric.metricDefinitionId, metric.start, metric.end, metric.value), conflict.message);
    }

    @Test
    public void assignMetricInstallationNotBelongToProvider(){

        //Registering a project
        var project = given()
                .auth()
                .oauth2(getAccessToken("admin"))
                .post("/{id}", "777536")
                .then()
                .assertThat()
                .statusCode(200)
                .extract()
                .as(ProjectResponseDto.class);

        //Registering an installation
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

        var metric = new MetricRequestDto();
        metric.start = "2022-01-05T09:13:07Z";
        metric.end = "2022-01-05T09:14:07Z";
        metric.value = 10.8;
        metric.metricDefinitionId = metricDefinitionResponse.id;

        // Bad request
        var conflict = given()
                .auth()
                .oauth2(getAccessToken("admin"))
                .body(metric)
                .contentType(ContentType.JSON)
                .post("/{projectId}/providers/{providerId}/installations/{installationId}/metrics", project.id, "sites", installation.id)
                .then()
                .assertThat()
                .statusCode(400)
                .extract()
                .as(InformativeResponse.class);

        assertEquals(String.format(String.format("There is no relationship among {%s, %s, %s}", project.id, "sites", installation.id)), conflict.message);
    }

    private InstallationResponseDto createInstallation(InstallationRequestDto request, String user){

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

    protected String getAccessToken(String userName) {
        return keycloakClient.getAccessToken(userName);
    }
}
