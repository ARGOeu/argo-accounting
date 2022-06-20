package org.accounting.system;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import io.quarkus.test.junit.mockito.InjectMock;
import io.quarkus.test.keycloak.client.KeycloakTestClient;
import io.restassured.RestAssured;
import io.restassured.config.JsonConfig;
import io.restassured.http.ContentType;
import io.restassured.path.json.config.JsonPathConfig;
import org.accounting.system.clients.ProviderClient;
import org.accounting.system.clients.responses.eoscportal.Response;
import org.accounting.system.clients.responses.eoscportal.Total;
import org.accounting.system.dtos.InformativeResponse;
import org.accounting.system.dtos.installation.InstallationRequestDto;
import org.accounting.system.dtos.installation.InstallationResponseDto;
import org.accounting.system.dtos.metric.MetricRequestDto;
import org.accounting.system.dtos.metric.MetricResponseDto;
import org.accounting.system.dtos.metric.UpdateMetricRequestDto;
import org.accounting.system.dtos.metricdefinition.MetricDefinitionRequestDto;
import org.accounting.system.dtos.metricdefinition.MetricDefinitionResponseDto;
import org.accounting.system.dtos.project.ProjectResponseDto;
import org.accounting.system.endpoints.MetricEndpoint;
import org.accounting.system.mappers.ProviderMapper;
import org.accounting.system.repositories.installation.InstallationRepository;
import org.accounting.system.repositories.metric.MetricRepository;
import org.accounting.system.repositories.metricdefinition.MetricDefinitionRepository;
import org.accounting.system.repositories.project.ProjectAccessAlwaysRepository;
import org.accounting.system.repositories.project.ProjectModulator;
import org.accounting.system.repositories.provider.ProviderRepository;
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
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;


@QuarkusTest
@TestProfile(AccountingSystemTestProfile.class)
@TestHTTPEndpoint(MetricEndpoint.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@QuarkusTestResource(ProjectWireMockServer.class)
@QuarkusTestResource(ProviderWireMockServer.class)
public class MetricEndpointTest {

    @InjectMock
    ReadPredefinedTypesService readPredefinedTypesService;

    @Inject
    MetricDefinitionRepository metricDefinitionRepository;

    @Inject
    InstallationRepository installationRepository;

    @Inject
    @RestClient
    ProviderClient providerClient;

    @Inject
    ProviderRepository providerRepository;

    @Inject
    MetricRepository metricRepository;

    @Inject
    ProjectAccessAlwaysRepository projectAccessAlwaysRepository;

    KeycloakTestClient keycloakClient = new KeycloakTestClient();

    @BeforeAll
    public void setup() throws ExecutionException, InterruptedException {

        projectAccessAlwaysRepository.deleteAll();

        Total total = providerClient.getTotalNumberOfProviders().toCompletableFuture().get();

        Response response = providerClient.getAll(total.total).toCompletableFuture().get();

        providerRepository.persistOrUpdate(ProviderMapper.INSTANCE.eoscProvidersToProviders(response.results));

        //We are going to register the EOSC-hub project from OpenAire API
        projectAccessAlwaysRepository.save("777536", ProjectModulator.given());
    }

    @BeforeEach
    public void before() {
        installationRepository.deleteAll();
        metricDefinitionRepository.deleteAll();
        metricRepository.deleteAll();
        projectAccessAlwaysRepository.deleteAll();
    }

    @Test
    public void createMetricRequestNotAuthenticated() {

        var notAuthenticatedResponse =  given()
                .auth()
                .oauth2("invalidToken")
                .contentType(ContentType.JSON)
                .post("/{projectId}/providers/{providerId}/installations/{installationId}/metrics", "projectId", "grnet", "installationId");

        assertEquals(401, notAuthenticatedResponse.statusCode());
    }


    @Test
    public void createMetricNoMetricDefinition() {

        var request = new MetricRequestDto();
        request.start = "2022-01-05T09:13:07Z";
        request.end = "2022-01-05T09:14:07Z";
        request.metricDefinitionId = "507f1f77bcf86cd799439011";
        request.value = 10.3;

        var response = assignMetric("admin", request);

        var informativeResponse = response
                .then()
                .assertThat()
                .statusCode(404)
                .extract()
                .as(InformativeResponse.class);

        assertEquals("There is no Metric Definition with the following id: 507f1f77bcf86cd799439011", informativeResponse.message);
    }

    @Test
    public void createMetricEmptyMetricDefinitionAttribute() {

        var request = new MetricRequestDto();
        request.start = "2022-01-05T09:13:07Z";
        request.end = "2022-01-05T09:14:07Z";
        request.value = 10.3;

        var response = assignMetric("admin", request);

        var informativeResponse = response
                .then()
                .assertThat()
                .statusCode(400)
                .extract()
                .as(InformativeResponse.class);

        assertEquals("metric_definition_id may not be empty.", informativeResponse.message);
    }

    @Test
    public void createMetricNoValidZuluTimestamp() {

        Mockito.when(readPredefinedTypesService.searchForUnitType(any())).thenReturn(Optional.of("SECOND"));
        Mockito.when(readPredefinedTypesService.searchForMetricType(any())).thenReturn(Optional.of("Aggregated"));
        var requestForMetricDefinition = new MetricDefinitionRequestDto();

        requestForMetricDefinition.metricName = "metric";
        requestForMetricDefinition.metricDescription = "description";
        requestForMetricDefinition.unitType = "SECOND";
        requestForMetricDefinition.metricType = "Aggregated";

        var metricDefinition = createMetricDefinition(requestForMetricDefinition, "admin");

        var request = new MetricRequestDto();
        request.metricDefinitionId = metricDefinition.id;
        request.start = "2022-01-0509:13:07";
        request.end = "2022-01-05T09:14:07Z";
        request.value = 10.3;

        var response = assignMetric("admin", request);

        var informativeResponse = response
                .then()
                .assertThat()
                .statusCode(400)
                .extract()
                .as(InformativeResponse.class);

        assertEquals("time_period_start must be a valid zulu timestamp. found: 2022-01-0509:13:07", informativeResponse.message);
    }

    @Test
    public void createMetricNoZuluTimestamp() {

        Mockito.when(readPredefinedTypesService.searchForUnitType(any())).thenReturn(Optional.of("SECOND"));
        Mockito.when(readPredefinedTypesService.searchForMetricType(any())).thenReturn(Optional.of("Aggregated"));
        MetricDefinitionRequestDto requestMetricDefinition = new MetricDefinitionRequestDto();

        requestMetricDefinition.metricName = "metric";
        requestMetricDefinition.metricDescription = "description";
        requestMetricDefinition.unitType = "SECOND";
        requestMetricDefinition.metricType = "Aggregated";

        MetricDefinitionResponseDto metricDefinition = createMetricDefinition(requestMetricDefinition, "admin");

        var request = new MetricRequestDto();
        request.metricDefinitionId = metricDefinition.id;
        request.end = "2022-01-05T09:13:07Z";
        request.value = 10.3;

        var response = assignMetric("admin", request);

        var informativeResponse = response
                .then()
                .assertThat()
                .statusCode(400)
                .extract()
                .as(InformativeResponse.class);

        assertEquals("time_period_start may not be empty.", informativeResponse.message);
    }


    @Test
    public void createMetricStartIsAfterEnd() {

        //first create a metric definition

        Mockito.when(readPredefinedTypesService.searchForUnitType(any())).thenReturn(Optional.of("SECOND"));
        Mockito.when(readPredefinedTypesService.searchForMetricType(any())).thenReturn(Optional.of("Aggregated"));
        var requestForMetricDefinition = new MetricDefinitionRequestDto();

        requestForMetricDefinition.metricName = "metric";
        requestForMetricDefinition.metricDescription = "description";
        requestForMetricDefinition.unitType = "SECOND";
        requestForMetricDefinition.metricType = "Aggregated";

        var metricDefinitionResponse = createMetricDefinition(requestForMetricDefinition, "admin");

        //then execute a request for creating a metric

        var requestForMetric = new MetricRequestDto();
        requestForMetric.start = "2024-01-05T09:15:07Z";
        requestForMetric.end = "2022-01-05T09:13:07Z";
        requestForMetric.value = 10.8;
        requestForMetric.metricDefinitionId = metricDefinitionResponse.id;

        var response = assignMetric("admin", requestForMetric);

        var informativeResponse = response
                .then()
                .assertThat()
                .statusCode(400)
                .extract()
                .as(InformativeResponse.class);

        assertEquals("Timestamp of the starting date time cannot be after of Timestamp of the end date time.", informativeResponse.message);
    }

    @Test
    public void createMetricStartIsEqualEnd() {

        //first create a metric definition

        Mockito.when(readPredefinedTypesService.searchForUnitType(any())).thenReturn(Optional.of("SECOND"));
        Mockito.when(readPredefinedTypesService.searchForMetricType(any())).thenReturn(Optional.of("Aggregated"));
        var requestForMetricDefinition = new MetricDefinitionRequestDto();

        requestForMetricDefinition.metricName = "metric";
        requestForMetricDefinition.metricDescription = "description";
        requestForMetricDefinition.unitType = "SECOND";
        requestForMetricDefinition.metricType = "Aggregated";

        var metricDefinitionResponse = createMetricDefinition(requestForMetricDefinition, "admin");

        //then execute a request for creating a metric

        var requestForMetric = new MetricRequestDto();
        requestForMetric.start = "2024-01-05T09:15:07Z";
        requestForMetric.end = "2024-01-05T09:15:07Z";
        requestForMetric.value = 10.8;
        requestForMetric.metricDefinitionId = metricDefinitionResponse.id;

        var response = assignMetric("admin", requestForMetric);

        var informativeResponse = response
                .then()
                .assertThat()
                .statusCode(400)
                .extract()
                .as(InformativeResponse.class);

        assertEquals("Timestamp of the starting date time cannot be equal to Timestamp of the end date time.", informativeResponse.message);
    }

    @Test
    public void fetchMetricNotAuthenticated() {

        var notAuthenticatedResponse = given()
                .auth()
                .oauth2("invalidToken")
                .get("/{metricId}", "507f1f77bcf86cd799439011")
                .thenReturn();

        assertEquals(401, notAuthenticatedResponse.statusCode());
    }

    @Test
    public void fetchMetricNotFound() {

        var notFoundResponse = given()
                .auth()
                .oauth2(getAccessToken("admin"))
                .get("/{metricId}", "507f1f77bcf86cd799439011")
                .then()
                .assertThat()
                .statusCode(404)
                .extract()
                .as(InformativeResponse.class);

        assertEquals("There is no Metric with the following id: 507f1f77bcf86cd799439011", notFoundResponse.message);
    }

    @Test
    public void fetchMetric() {

        Mockito.when(readPredefinedTypesService.searchForUnitType(any())).thenReturn(Optional.of("SECOND"));
        Mockito.when(readPredefinedTypesService.searchForMetricType(any())).thenReturn(Optional.of("Aggregated"));
        var requestForMetricDefinition = new MetricDefinitionRequestDto();

        requestForMetricDefinition.metricName = "metric";
        requestForMetricDefinition.metricDescription = "description";
        requestForMetricDefinition.unitType = "SECOND";
        requestForMetricDefinition.metricType = "Aggregated";

        var metricDefinitionResponse = createMetricDefinition(requestForMetricDefinition, "admin");

        var requestForMetric = new MetricRequestDto();
        requestForMetric.start = "2024-01-05T09:15:07Z";
        requestForMetric.end = "2024-01-05T09:18:07Z";
        requestForMetric.value = 10.8;
        requestForMetric.metricDefinitionId = metricDefinitionResponse.id;

        var response = assignMetric("admin", requestForMetric);

        var metric = response
                .then()
                .assertThat()
                .statusCode(201)
                .extract()
                .as(MetricResponseDto.class);

        var getMetric = given()
                .auth()
                .oauth2(getAccessToken("admin"))
                .get("/{metricId}", metric.id)
                .then()
                .assertThat()
                .statusCode(200)
                .extract()
                .as(MetricResponseDto.class);

        assertEquals(metric.id, getMetric.id);
    }

    @Test
    public void deleteMetricNotAuthenticated() {

        var notAuthenticatedResponse = given()
                .auth()
                .oauth2("invalidToken")
                .delete("/{metricId}", "507f1f77bcf86cd799439011")
                .thenReturn();

        assertEquals(401, notAuthenticatedResponse.statusCode());
    }

    @Test
    public void deleteMetricNotFound() {

        var response = given()
                .auth()
                .oauth2(getAccessToken("admin"))
                .delete("/{metricId}", "507f1f77bcf86cd799439011")
                .then()
                .assertThat()
                .statusCode(404)
                .extract()
                .as(InformativeResponse.class);

        assertEquals("There is no Metric with the following id: 507f1f77bcf86cd799439011", response.message);
    }

    @Test
    public void deleteMetric() {

        Mockito.when(readPredefinedTypesService.searchForUnitType(any())).thenReturn(Optional.of("SECOND"));
        Mockito.when(readPredefinedTypesService.searchForMetricType(any())).thenReturn(Optional.of("Aggregated"));
        var requestForMetricDefinition = new MetricDefinitionRequestDto();

        requestForMetricDefinition.metricName = "metric";
        requestForMetricDefinition.metricDescription = "description";
        requestForMetricDefinition.unitType = "SECOND";
        requestForMetricDefinition.metricType = "Aggregated";

        var metricDefinitionResponse = createMetricDefinition(requestForMetricDefinition, "admin");

        var requestForMetric = new MetricRequestDto();
        requestForMetric.start = "2024-01-05T09:15:07Z";
        requestForMetric.end = "2024-01-05T09:18:07Z";
        requestForMetric.value = 10.8;
        requestForMetric.metricDefinitionId = metricDefinitionResponse.id;

        var response = assignMetric("admin", requestForMetric);

        var metric = response
                .then()
                .assertThat()
                .statusCode(201)
                .extract()
                .as(MetricResponseDto.class);

        var deleteResponse = given()
                .auth()
                .oauth2(getAccessToken("admin"))
                .delete("/{metricId}", metric.id)
                .then()
                .assertThat()
                .statusCode(200)
                .extract()
                .as(InformativeResponse.class);

        assertEquals("Metric has been deleted successfully.", deleteResponse.message);
    }

    @Test
    public void updateMetricRequestBodyIsEmpty() {

        Mockito.when(readPredefinedTypesService.searchForUnitType(any())).thenReturn(Optional.of("SECOND"));
        Mockito.when(readPredefinedTypesService.searchForMetricType(any())).thenReturn(Optional.of("Aggregated"));
        var requestForMetricDefinition = new MetricDefinitionRequestDto();

        requestForMetricDefinition.metricName = "metric";
        requestForMetricDefinition.metricDescription = "description";
        requestForMetricDefinition.unitType = "SECOND";
        requestForMetricDefinition.metricType = "Aggregated";

        var createdMetricDefinition = createMetricDefinition(requestForMetricDefinition, "admin");

        // create a metric
        var requestForMetric = new MetricRequestDto();
        requestForMetric.start = "2022-01-05T09:13:07Z";
        requestForMetric.end = "2022-01-05T09:14:07Z";
        requestForMetric.value = 10.8;
        requestForMetric.metricDefinitionId = createdMetricDefinition.id;

        var response = assignMetric("admin", requestForMetric);

        var metric = response
                .then()
                .assertThat()
                .statusCode(201)
                .extract()
                .as(MetricResponseDto.class);

        var informativeResponse = given()
                .auth()
                .oauth2(getAccessToken("admin"))
                .contentType(ContentType.JSON)
                .patch("/{id}", metric.id)
                .then()
                .assertThat()
                .statusCode(400)
                .extract()
                .as(InformativeResponse.class);

        assertEquals("The request body is empty.", informativeResponse.message);
    }

    @Test
    public void updateMetricNotAuthenticated() {

        var notAuthenticatedResponse = given()
                .auth()
                .oauth2("invalidToken")
                .patch("/{id}", "507f1f77bcf86cd799439011")
                .thenReturn();

        assertEquals(401, notAuthenticatedResponse.statusCode());
    }

    @Test
    public void updateMetricCannotConsumeContentType() {

        var response = given()
                .auth()
                .oauth2(getAccessToken("admin"))
                .patch("/{id}", "507f1f77bcf86cd799439011")
                .then()
                .assertThat()
                .statusCode(415)
                .extract()
                .as(InformativeResponse.class);

        assertEquals("Cannot consume content type.", response.message);
    }

    @Test
    public void updateMetricNotFound() {

        var response = given()
                .auth()
                .oauth2(getAccessToken("admin"))
                .contentType(ContentType.JSON)
                .patch("/{id}", "jnejenjdfn")
                .then()
                .assertThat()
                .statusCode(404)
                .extract()
                .as(InformativeResponse.class);

        assertEquals("There is no Metric with the following id: jnejenjdfn", response.message);
    }

    @Test
    public void updateMetricMetricDefinitionNotValidZuluTimestamp() {

        Mockito.when(readPredefinedTypesService.searchForUnitType(any())).thenReturn(Optional.of("SECOND"));
        Mockito.when(readPredefinedTypesService.searchForMetricType(any())).thenReturn(Optional.of("Aggregated"));
        var requestForMetricDefinition = new MetricDefinitionRequestDto();

        requestForMetricDefinition.metricName = "metric";
        requestForMetricDefinition.metricDescription = "description";
        requestForMetricDefinition.unitType = "SECOND";
        requestForMetricDefinition.metricType = "Aggregated";

        var createdMetricDefinition = createMetricDefinition(requestForMetricDefinition, "admin");

        // create a metric
        var requestForMetric = new MetricRequestDto();
        requestForMetric.start = "2022-01-05T09:13:07Z";
        requestForMetric.end = "2022-01-05T09:14:07Z";
        requestForMetric.value = 10.8;
        requestForMetric.metricDefinitionId = createdMetricDefinition.id;

        var response = assignMetric("admin", requestForMetric);

        var metric = response
                .then()
                .assertThat()
                .statusCode(201)
                .extract()
                .as(MetricResponseDto.class);

        // update an existing metric
        var updateMetricRequest = new UpdateMetricRequestDto();

        updateMetricRequest.start = "2023-01-0509:13:07";

        var informativeResponse = given()
                .auth()
                .oauth2(getAccessToken("admin"))
                .body(updateMetricRequest)
                .contentType(ContentType.JSON)
                .patch("/{id}", metric.id)
                .then()
                .assertThat()
                .statusCode(400)
                .extract()
                .as(InformativeResponse.class);

        assertEquals("time_period_start must be a valid zulu timestamp. found: 2023-01-0509:13:07", informativeResponse.message);
    }

    @Test
    public void updateMetricMetricDefinitionNotFound() {

        Mockito.when(readPredefinedTypesService.searchForUnitType(any())).thenReturn(Optional.of("SECOND"));
        Mockito.when(readPredefinedTypesService.searchForMetricType(any())).thenReturn(Optional.of("Aggregated"));
        var requestForMetricDefinition = new MetricDefinitionRequestDto();

        requestForMetricDefinition.metricName = "metric";
        requestForMetricDefinition.metricDescription = "description";
        requestForMetricDefinition.unitType = "SECOND";
        requestForMetricDefinition.metricType = "Aggregated";

        var createdMetricDefinition = createMetricDefinition(requestForMetricDefinition, "admin");

        // create a metric
        var requestForMetric = new MetricRequestDto();
        requestForMetric.start = "2022-01-05T09:13:07Z";
        requestForMetric.end = "2022-01-05T09:14:07Z";
        requestForMetric.value = 10.8;
        requestForMetric.metricDefinitionId = createdMetricDefinition.id;

        var response = assignMetric("admin", requestForMetric);

        var metric = response
                .then()
                .assertThat()
                .statusCode(201)
                .extract()
                .as(MetricResponseDto.class);

        // update an existing metric
        var updateMetricRequest = new UpdateMetricRequestDto();

        updateMetricRequest.start = "2023-01-05T09:13:07Z";
        updateMetricRequest.end = "2024-01-05T09:13:07Z";
        updateMetricRequest.value = 15.8;
        updateMetricRequest.metricDefinitionId = "507f1f77bcf86cd799439011";

        var informativeResponse = given()
                .auth()
                .oauth2(getAccessToken("admin"))
                .body(updateMetricRequest)
                .contentType(ContentType.JSON)
                .patch("/{id}", metric.id)
                .then()
                .assertThat()
                .statusCode(404)
                .extract()
                .as(InformativeResponse.class);

        assertEquals("There is no Metric Definition with the following id: 507f1f77bcf86cd799439011", informativeResponse.message);
    }

    @Test
    public void updateMetricMetricNotFound() {

        var updateMetricRequest = new UpdateMetricRequestDto();

        updateMetricRequest.start = "2023-01-05T09:13:07Z";
        updateMetricRequest.end = "2024-01-05T09:14:07Z";
        updateMetricRequest.value = 15.8;
        updateMetricRequest.metricDefinitionId = "507f1f77bcf86cd799439011";

        var response = given()
                .auth()
                .oauth2(getAccessToken("admin"))
                .body(updateMetricRequest)
                .contentType(ContentType.JSON)
                .patch("/{id}", "507f1f77bcf86cd799439011")
                .then()
                .assertThat()
                .statusCode(404)
                .extract()
                .as(InformativeResponse.class);

        assertEquals("There is no Metric with the following id: 507f1f77bcf86cd799439011", response.message);
    }

    @Test
    public void updateMetricModifiedStartCannotBeAfterEnd() {

        //first, create a metric definition
        Mockito.when(readPredefinedTypesService.searchForUnitType(any())).thenReturn(Optional.of("SECOND"));
        Mockito.when(readPredefinedTypesService.searchForMetricType(any())).thenReturn(Optional.of("Aggregated"));
        var requestForMetricDefinition = new MetricDefinitionRequestDto();

        requestForMetricDefinition.metricName = "metric";
        requestForMetricDefinition.metricDescription = "description";
        requestForMetricDefinition.unitType = "SECOND";
        requestForMetricDefinition.metricType = "Aggregated";

        var createdMetricDefinition = createMetricDefinition(requestForMetricDefinition, "admin");

        // create a metric
        var requestForMetric = new MetricRequestDto();
        requestForMetric.start = "2021-01-05T09:13:07Z";
        requestForMetric.end = "2022-01-05T09:14:07Z";
        requestForMetric.value = 10.8;
        requestForMetric.metricDefinitionId = createdMetricDefinition.id;

        var response = assignMetric("admin", requestForMetric);

        var metric = response
                .then()
                .assertThat()
                .statusCode(201)
                .extract()
                .as(MetricResponseDto.class);

        // update an existing metric
        var updateMetricRequest = new UpdateMetricRequestDto();

        updateMetricRequest.start = "2023-01-05T09:13:07Z";


        var informativeResponse = given()
                .auth()
                .oauth2(getAccessToken("admin"))
                .body(updateMetricRequest)
                .contentType(ContentType.JSON)
                .patch("/{id}", metric.id)
                .then()
                .assertThat()
                .statusCode(400)
                .extract()
                .as(InformativeResponse.class);

        assertEquals("Timestamp of the starting date time cannot be after of Timestamp of the end date time.", informativeResponse.message);
    }

    @Test
    public void updateMetricModifiedStartCannotBeAfterModifiedEnd() {

        //first, create a metric definition
        Mockito.when(readPredefinedTypesService.searchForUnitType(any())).thenReturn(Optional.of("SECOND"));
        Mockito.when(readPredefinedTypesService.searchForMetricType(any())).thenReturn(Optional.of("Aggregated"));
        var requestForMetricDefinition = new MetricDefinitionRequestDto();

        requestForMetricDefinition.metricName = "metric";
        requestForMetricDefinition.metricDescription = "description";
        requestForMetricDefinition.unitType = "SECOND";
        requestForMetricDefinition.metricType = "Aggregated";

        var createdMetricDefinition = createMetricDefinition(requestForMetricDefinition, "admin");

        // create a metric
        var requestForMetric = new MetricRequestDto();
        requestForMetric.start = "2021-01-05T09:13:07Z";
        requestForMetric.end = "2022-01-05T09:14:07Z";
        requestForMetric.value = 10.8;
        requestForMetric.metricDefinitionId = createdMetricDefinition.id;

        var response = assignMetric("admin", requestForMetric);

        var metric = response
                .then()
                .assertThat()
                .statusCode(201)
                .extract()
                .as(MetricResponseDto.class);

        // update an existing metric
        var updateMetricRequest = new UpdateMetricRequestDto();

        updateMetricRequest.start = "2025-01-05T09:13:07Z";
        updateMetricRequest.end = "2024-01-05T09:13:07Z";

        var informativeResponse = given()
                .auth()
                .oauth2(getAccessToken("admin"))
                .body(updateMetricRequest)
                .contentType(ContentType.JSON)
                .patch("/{id}", metric.id)
                .then()
                .assertThat()
                .statusCode(400)
                .extract()
                .as(InformativeResponse.class);

        assertEquals("Timestamp of the starting date time cannot be after of Timestamp of the end date time.", informativeResponse.message);
    }

    @Test
    public void updateMetricModifiedEndCannotBeBeforeStart() {

        //first, create a metric definition
        Mockito.when(readPredefinedTypesService.searchForUnitType(any())).thenReturn(Optional.of("SECOND"));
        Mockito.when(readPredefinedTypesService.searchForMetricType(any())).thenReturn(Optional.of("Aggregated"));
        var requestForMetricDefinition = new MetricDefinitionRequestDto();

        requestForMetricDefinition.metricName = "metric";
        requestForMetricDefinition.metricDescription = "description";
        requestForMetricDefinition.unitType = "SECOND";
        requestForMetricDefinition.metricType = "Aggregated";

        var createdMetricDefinition = createMetricDefinition(requestForMetricDefinition, "admin");

        // create a metric
        var requestForMetric = new MetricRequestDto();
        requestForMetric.start = "2021-01-05T09:13:07Z";
        requestForMetric.end = "2022-01-05T09:14:07Z";
        requestForMetric.value = 10.8;
        requestForMetric.metricDefinitionId = createdMetricDefinition.id;

        var response = assignMetric("admin", requestForMetric);

        var metric = response
                .then()
                .assertThat()
                .statusCode(201)
                .extract()
                .as(MetricResponseDto.class);

        // update an existing metric
        var updateMetricRequest = new UpdateMetricRequestDto();

        updateMetricRequest.end = "2020-01-05T09:13:07Z";

        var informativeResponse = given()
                .auth()
                .oauth2(getAccessToken("admin"))
                .body(updateMetricRequest)
                .contentType(ContentType.JSON)
                .patch("/{id}", metric.id)
                .then()
                .assertThat()
                .statusCode(400)
                .extract()
                .as(InformativeResponse.class);

        assertEquals("Timestamp of the end date time cannot be before of Timestamp of the starting date time.", informativeResponse.message);
    }

    @Test
    public void updateMetricModifiedStartCannotBeEqualToModifiedEnd() {

        //first, create a metric definition
        Mockito.when(readPredefinedTypesService.searchForUnitType(any())).thenReturn(Optional.of("SECOND"));
        Mockito.when(readPredefinedTypesService.searchForMetricType(any())).thenReturn(Optional.of("Aggregated"));
        var requestForMetricDefinition = new MetricDefinitionRequestDto();

        requestForMetricDefinition.metricName = "metric";
        requestForMetricDefinition.metricDescription = "description";
        requestForMetricDefinition.unitType = "SECOND";
        requestForMetricDefinition.metricType = "Aggregated";

        var createdMetricDefinition = createMetricDefinition(requestForMetricDefinition, "admin");

        // create a metric
        var requestForMetric = new MetricRequestDto();
        requestForMetric.start = "2021-01-05T09:13:07Z";
        requestForMetric.end = "2022-01-05T09:14:07Z";
        requestForMetric.value = 10.8;
        requestForMetric.metricDefinitionId = createdMetricDefinition.id;

        var response = assignMetric("admin", requestForMetric);

        var metric = response
                .then()
                .assertThat()
                .statusCode(201)
                .extract()
                .as(MetricResponseDto.class);

        // update an existing metric
        var updateMetricRequest = new UpdateMetricRequestDto();

        updateMetricRequest.start = "2024-01-05T09:13:07Z";
        updateMetricRequest.end = "2024-01-05T09:13:07Z";


        var informativeResponse = given()
                .auth()
                .oauth2(getAccessToken("admin"))
                .body(updateMetricRequest)
                .contentType(ContentType.JSON)
                .patch("/{id}", metric.id)
                .then()
                .assertThat()
                .statusCode(400)
                .extract()
                .as(InformativeResponse.class);

        assertEquals("Timestamp of the starting date time cannot be equal to Timestamp of the end date time.", informativeResponse.message);
    }

    @Test
    public void updateMetricModifiedStartCannotBeEqualToEnd() {

        //first, create a metric definition
        Mockito.when(readPredefinedTypesService.searchForUnitType(any())).thenReturn(Optional.of("SECOND"));
        Mockito.when(readPredefinedTypesService.searchForMetricType(any())).thenReturn(Optional.of("Aggregated"));
        var requestForMetricDefinition = new MetricDefinitionRequestDto();

        requestForMetricDefinition.metricName = "metric";
        requestForMetricDefinition.metricDescription = "description";
        requestForMetricDefinition.unitType = "SECOND";
        requestForMetricDefinition.metricType = "Aggregated";

        var createdMetricDefinition = createMetricDefinition(requestForMetricDefinition, "admin");

        // create a metric
        var requestForMetric = new MetricRequestDto();
        requestForMetric.start = "2021-01-05T09:13:07Z";
        requestForMetric.end = "2022-01-05T09:14:07Z";
        requestForMetric.value = 10.8;
        requestForMetric.metricDefinitionId = createdMetricDefinition.id;

        var response = assignMetric("admin", requestForMetric);

        var metric = response
                .then()
                .assertThat()
                .statusCode(201)
                .extract()
                .as(MetricResponseDto.class);

        // update an existing metric
        var updateMetricRequest = new UpdateMetricRequestDto();

        updateMetricRequest.start = "2022-01-05T09:14:07Z";

        var informativeResponse = given()
                .auth()
                .oauth2(getAccessToken("admin"))
                .body(updateMetricRequest)
                .contentType(ContentType.JSON)
                .patch("/{id}", metric.id)
                .then()
                .assertThat()
                .statusCode(400)
                .extract()
                .as(InformativeResponse.class);

        assertEquals("Timestamp of the starting date time cannot be equal to Timestamp of the end date time.", informativeResponse.message);
    }

    @Test
    public void updateMetricStartCannotBeEqualToModifiedEnd() {

        //first, create a metric definition
        Mockito.when(readPredefinedTypesService.searchForUnitType(any())).thenReturn(Optional.of("SECOND"));
        Mockito.when(readPredefinedTypesService.searchForMetricType(any())).thenReturn(Optional.of("Aggregated"));
        var requestForMetricDefinition = new MetricDefinitionRequestDto();

        requestForMetricDefinition.metricName = "metric";
        requestForMetricDefinition.metricDescription = "description";
        requestForMetricDefinition.unitType = "SECOND";
        requestForMetricDefinition.metricType = "Aggregated";

        var createdMetricDefinition = createMetricDefinition(requestForMetricDefinition, "admin");

        // create a metric
        var requestForMetric = new MetricRequestDto();
        requestForMetric.start = "2021-01-05T09:13:07Z";
        requestForMetric.end = "2022-01-05T09:14:07Z";
        requestForMetric.value = 10.8;
        requestForMetric.metricDefinitionId = createdMetricDefinition.id;

        var response = assignMetric("admin", requestForMetric);

        var metric = response
                .then()
                .assertThat()
                .statusCode(201)
                .extract()
                .as(MetricResponseDto.class);

        // update an existing metric
        var updateMetricRequest = new UpdateMetricRequestDto();

        updateMetricRequest.end = "2021-01-05T09:13:07Z";

        var informativeResponse = given()
                .auth()
                .oauth2(getAccessToken("admin"))
                .body(updateMetricRequest)
                .contentType(ContentType.JSON)
                .patch("/{id}", metric.id)
                .then()
                .assertThat()
                .statusCode(400)
                .extract()
                .as(InformativeResponse.class);

        assertEquals("Timestamp of the starting date time cannot be equal to Timestamp of the end date time.", informativeResponse.message);
    }

    @Test
    public void updateMetricFull() {

        //first, create a metric definition
        Mockito.when(readPredefinedTypesService.searchForUnitType(any())).thenReturn(Optional.of("SECOND"));
        Mockito.when(readPredefinedTypesService.searchForMetricType(any())).thenReturn(Optional.of("Aggregated"));
        var requestForMetricDefinition = new MetricDefinitionRequestDto();

        requestForMetricDefinition.metricName = "metric";
        requestForMetricDefinition.metricDescription = "description";
        requestForMetricDefinition.unitType = "SECOND";
        requestForMetricDefinition.metricType = "Aggregated";

        var createdMetricDefinition = createMetricDefinition(requestForMetricDefinition, "admin");

        //create another metric definition
        Mockito.when(readPredefinedTypesService.searchForUnitType(any())).thenReturn(Optional.of("#"));
        Mockito.when(readPredefinedTypesService.searchForMetricType(any())).thenReturn(Optional.of("count"));
        var requestForMetricDefinition1 = new MetricDefinitionRequestDto();

        requestForMetricDefinition1.metricName = "metric";
        requestForMetricDefinition1.metricDescription = "description";
        requestForMetricDefinition1.unitType = "#";
        requestForMetricDefinition1.metricType = "count";

        var createdMetricDefinition1 = createMetricDefinition(requestForMetricDefinition1, "admin");


        // create a metric
        var requestForMetric = new MetricRequestDto();
        requestForMetric.start = "2022-01-05T09:13:07Z";
        requestForMetric.end = "2022-01-05T09:14:07Z";
        requestForMetric.value = 10.8;
        requestForMetric.metricDefinitionId = createdMetricDefinition.id;

        var response = assignMetric("admin", requestForMetric);

        var metric = response
                .then()
                .assertThat()
                .statusCode(201)
                .extract()
                .as(MetricResponseDto.class);

        // update an existing metric
        var updateMetricRequest = new UpdateMetricRequestDto();

        updateMetricRequest.start = "2023-01-05T09:13:07Z";
        updateMetricRequest.end = "2024-01-05T09:14:07Z";
        updateMetricRequest.value = 15.8;
        updateMetricRequest.metricDefinitionId = createdMetricDefinition1.id;

        var updateResponse = given()
                .auth()
                .oauth2(getAccessToken("admin"))
                .config(RestAssured.config()
                        .jsonConfig(JsonConfig.jsonConfig()
                                .numberReturnType(JsonPathConfig.NumberReturnType.DOUBLE)))
                .body(updateMetricRequest)
                .contentType(ContentType.JSON)
                .patch("/{id}", metric.id)
                .then()
                .assertThat()
                .statusCode(200)
                .body("metric_id", is(metric.id))
                .body("metric_definition_id", is(updateMetricRequest.metricDefinitionId))
                .body("time_period_start", is(updateMetricRequest.start))
                .body("time_period_end", is(updateMetricRequest.end))
                .body("value", is(updateMetricRequest.value))
                .extract()
                .as(MetricResponseDto.class);

        assertEquals(createdMetricDefinition1.id, updateResponse.metricDefinitionId);
    }

    @Test
    public void updateMetricPartial() {

        //first, create a metric definition
        Mockito.when(readPredefinedTypesService.searchForUnitType(any())).thenReturn(Optional.of("SECOND"));
        Mockito.when(readPredefinedTypesService.searchForMetricType(any())).thenReturn(Optional.of("Aggregated"));
        var requestForMetricDefinition = new MetricDefinitionRequestDto();

        requestForMetricDefinition.metricName = "metric";
        requestForMetricDefinition.metricDescription = "description";
        requestForMetricDefinition.unitType = "SECOND";
        requestForMetricDefinition.metricType = "Aggregated";

        var createdMetricDefinition = createMetricDefinition(requestForMetricDefinition, "admin");

        // create a metric
        var requestForMetric = new MetricRequestDto();
        requestForMetric.start = "2022-01-05T09:13:07Z";
        requestForMetric.end = "2022-01-05T09:14:07Z";
        requestForMetric.value = 10.8;
        requestForMetric.metricDefinitionId = createdMetricDefinition.id;

        var response = assignMetric("admin", requestForMetric);

        var metric = response
                .then()
                .assertThat()
                .statusCode(201)
                .extract()
                .as(MetricResponseDto.class);

        // update an existing metric
        var updateMetricRequest = new UpdateMetricRequestDto();

        updateMetricRequest.value = 12.0;

        var updateResponse = given()
                .auth()
                .oauth2(getAccessToken("admin"))
                .config(RestAssured.config()
                        .jsonConfig(JsonConfig.jsonConfig()
                                .numberReturnType(JsonPathConfig.NumberReturnType.DOUBLE)))
                .body(updateMetricRequest)
                .contentType(ContentType.JSON)
                .patch("/{id}", metric.id)
                .then()
                .assertThat()
                .statusCode(200)
                .body("metric_id", is(metric.id))
                .body("metric_definition_id", is(requestForMetric.metricDefinitionId))
                .body("time_period_start", is(requestForMetric.start))
                .body("time_period_end", is(requestForMetric.end))
                .body("value", is(updateMetricRequest.value))
                .extract()
                .as(MetricResponseDto.class);

        assertEquals(requestForMetric.metricDefinitionId, updateResponse.metricDefinitionId);
    }

    private io.restassured.response.Response assignMetric(String user, MetricRequestDto body){

        //Registering a project
        var project = given()
                .auth()
                .oauth2(getAccessToken("admin"))
                .basePath("accounting-system/projects")
                .post("/{id}", "777536")
                .then()
                .assertThat()
                .statusCode(200)
                .extract()
                .as(ProjectResponseDto.class);

        //Registering an installation
        Mockito.when(readPredefinedTypesService.searchForUnitType(any())).thenReturn(Optional.of("KG"));
        Mockito.when(readPredefinedTypesService.searchForMetricType(any())).thenReturn(Optional.of("Aggregated"));
        var requestForMetricDefinition = new MetricDefinitionRequestDto();

        requestForMetricDefinition.metricName = "metric";
        requestForMetricDefinition.metricDescription = "description";
        requestForMetricDefinition.unitType = "KG";
        requestForMetricDefinition.metricType = "Aggregated";

        var metricDefinitionResponse = createMetricDefinition(requestForMetricDefinition, "admin");

        var request= new InstallationRequestDto();

        request.project = "777536";
        request.organisation = "grnet";
        request.infrastructure = "okeanos-knossos";
        request.installation = "SECOND";
        request.unitOfAccess = metricDefinitionResponse.id;

        var installation = createInstallation(request, "admin");

        return given()
                .auth()
                .oauth2(getAccessToken(user))
                .basePath("accounting-system/installations")
                .body(body)
                .contentType(ContentType.JSON)
                .post("/{installationId}/metrics", installation.id);
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

