package org.accounting.system;

import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import io.quarkus.test.junit.mockito.InjectMock;
import io.quarkus.test.keycloak.client.KeycloakTestClient;
import io.restassured.RestAssured;
import io.restassured.config.JsonConfig;
import io.restassured.http.ContentType;
import io.restassured.path.json.config.JsonPathConfig;
import org.accounting.system.dtos.InformativeResponse;
import org.accounting.system.dtos.MetricDefinitionRequestDto;
import org.accounting.system.dtos.MetricDefinitionResponseDto;
import org.accounting.system.dtos.MetricRequestDto;
import org.accounting.system.dtos.MetricResponseDto;
import org.accounting.system.dtos.UpdateMetricRequestDto;
import org.accounting.system.endpoints.MetricEndpoint;
import org.accounting.system.entities.Metric;
import org.accounting.system.entities.MetricDefinition;
import org.accounting.system.repositories.metricdefinition.MetricDefinitionRepository;
import org.accounting.system.repositories.MetricRepository;
import org.accounting.system.services.ReadPredefinedTypesService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javax.inject.Inject;
import java.time.Instant;
import java.util.Optional;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;


@QuarkusTest
@TestProfile(AccountingSystemTestProfile.class)
@TestHTTPEndpoint(MetricEndpoint.class)
public class MetricEndpointTest {

    @InjectMock
    ReadPredefinedTypesService readPredefinedTypesService;

    @Inject
    MetricDefinitionRepository metricDefinitionRepository;

    @Inject
    MetricRepository metricRepository;

    KeycloakTestClient keycloakClient = new KeycloakTestClient();

    @BeforeEach
    public void setup() {
        metricDefinitionRepository.deleteAll();
        metricRepository.deleteAll();
    }

    @Test
    public void createMetricRequestNotAuthenticated() {

        var notAuthenticatedResponse = given()
                .auth()
                .oauth2("invalidToken")
                .contentType(ContentType.JSON)
                .post()
                .thenReturn();

        assertEquals(401, notAuthenticatedResponse.statusCode());
    }

    @Test
    public void createMetricRequestBodyIsEmpty() {

        var response = given()
                .auth()
                .oauth2(getAccessToken("admin"))
                .contentType(ContentType.JSON)
                .post()
                .then()
                .assertThat()
                .statusCode(400)
                .extract()
                .as(InformativeResponse.class);

        assertEquals("The request body is empty.", response.message);
    }

    @Test
    public void createMetricNoMetricDefinition() {

        var request = new MetricRequestDto();
        request.start = "2022-01-05T09:13:07Z";
        request.end = "2022-01-05T09:14:07Z";
        request.metricDefinitionId = "507f1f77bcf86cd799439011";
        request.resourceId = "resource-id";
        request.value = 10.3;

        var response = given()
                .auth()
                .oauth2(getAccessToken("admin"))
                .body(request)
                .contentType(ContentType.JSON)
                .post()
                .then()
                .assertThat()
                .statusCode(404)
                .extract()
                .as(InformativeResponse.class);

        assertEquals("There is no Metric Definition with the following id: 507f1f77bcf86cd799439011", response.message);
    }

    @Test
    public void createMetricCannotConsumeContentType() {

        var request = new MetricRequestDto();

        var response = given()
                .auth()
                .oauth2(getAccessToken("admin"))
                .body(request)
                .post()
                .then()
                .assertThat()
                .statusCode(415)
                .extract()
                .as(InformativeResponse.class);

        assertEquals("Cannot consume content type.", response.message);
    }

    @Test
    public void createMetricEmptyMetricDefinitionAttribute() {

        var request = new MetricRequestDto();
        request.start = "2022-01-05T09:13:07Z";
        request.end = "2022-01-05T09:14:07Z";
        request.resourceId = "resource-id";
        request.value = 10.3;

        var response = given()
                .auth()
                .oauth2(getAccessToken("admin"))
                .body(request)
                .contentType(ContentType.JSON)
                .post()
                .then()
                .assertThat()
                .statusCode(400)
                .extract()
                .as(InformativeResponse.class);

        assertEquals("metric_definition_id may not be empty.", response.message);
    }

    @Test
    public void createMetricNoValidZuluTimestamp() {

        Mockito.when(readPredefinedTypesService.searchForUnitType(any())).thenReturn(Optional.of("SECOND"));
        Mockito.when(readPredefinedTypesService.searchForMetricType(any())).thenReturn(Optional.of("Aggregated"));
        MetricDefinitionRequestDto requestMetricDefinition = new MetricDefinitionRequestDto();

        requestMetricDefinition.metricName = "metric";
        requestMetricDefinition.metricDescription = "description";
        requestMetricDefinition.unitType = "SECOND";
        requestMetricDefinition.metricType = "Aggregated";

        MetricDefinitionResponseDto metricDefinition = createMetricDefinition(requestMetricDefinition);

        var request = new MetricRequestDto();
        request.metricDefinitionId = metricDefinition.id;
        request.start = "2022-01-0509:13:07";
        request.end = "2022-01-05T09:14:07Z";
        request.resourceId = "resource-id";
        request.value = 10.3;

        var response = given()
                .auth()
                .oauth2(getAccessToken("admin"))
                .body(request)
                .contentType(ContentType.JSON)
                .post()
                .then()
                .assertThat()
                .statusCode(400)
                .extract()
                .as(InformativeResponse.class);

        assertEquals("time_period_start must be a valid zulu timestamp. found: 2022-01-0509:13:07", response.message);
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

        MetricDefinitionResponseDto metricDefinition = createMetricDefinition(requestMetricDefinition);

        var request = new MetricRequestDto();
        request.metricDefinitionId = metricDefinition.id;
        request.end = "2022-01-05T09:13:07Z";
        request.resourceId = "resource-id";
        request.value = 10.3;

        var response = given()
                .auth()
                .oauth2(getAccessToken("admin"))
                .body(request)
                .contentType(ContentType.JSON)
                .post()
                .then()
                .assertThat()
                .statusCode(400)
                .extract()
                .as(InformativeResponse.class);

        assertEquals("time_period_start may not be empty.", response.message);
    }

    @Test
    public void createMetricNonHexId() {

        var request = new MetricRequestDto();
        request.start = "2022-01-05T09:13:07Z";
        request.end = "2022-01-05T09:14:07Z";
        request.metricDefinitionId = "iiejijirj33i3i";
        request.resourceId = "resource-id";
        request.value = 10.3;

        var notFoundResponse = given()
                .auth()
                .oauth2(getAccessToken("admin"))
                .body(request)
                .contentType(ContentType.JSON)
                .post()
                .then()
                .assertThat()
                .statusCode(404)
                .extract()
                .as(InformativeResponse.class);

        assertEquals("There is no Metric Definition with the following id: iiejijirj33i3i", notFoundResponse.message);
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

        var metricDefinitionResponse = createMetricDefinition(requestForMetricDefinition);

        //then execute a request for creating a metric

        var requestForMetric = new MetricRequestDto();
        requestForMetric.resourceId = "3434349fjiirgjirj003-3r3f-f-";
        requestForMetric.start = "2024-01-05T09:15:07Z";
        requestForMetric.end = "2022-01-05T09:13:07Z";
        requestForMetric.value = 10.8;
        requestForMetric.metricDefinitionId = metricDefinitionResponse.id;

        var response = given()
                .auth()
                .oauth2(getAccessToken("admin"))
                .body(requestForMetric)
                .contentType(ContentType.JSON)
                .post()
                .then()
                .assertThat()
                .statusCode(400)
                .extract()
                .as(InformativeResponse.class);

        assertEquals("Timestamp of the starting date time cannot be after of Timestamp of the end date time.", response.message);
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

        var metricDefinitionResponse = createMetricDefinition(requestForMetricDefinition);

        //then execute a request for creating a metric

        var requestForMetric = new MetricRequestDto();
        requestForMetric.resourceId = "3434349fjiirgjirj003-3r3f-f-";
        requestForMetric.start = "2024-01-05T09:15:07Z";
        requestForMetric.end = "2024-01-05T09:15:07Z";
        requestForMetric.value = 10.8;
        requestForMetric.metricDefinitionId = metricDefinitionResponse.id;

        var response = given()
                .auth()
                .oauth2(getAccessToken("admin"))
                .body(requestForMetric)
                .contentType(ContentType.JSON)
                .post()
                .then()
                .assertThat()
                .statusCode(400)
                .extract()
                .as(InformativeResponse.class);

        assertEquals("Timestamp of the starting date time cannot be equal to Timestamp of the end date time.", response.message);
    }

    @Test
    public void createMetric() {

        //first create a metric definition

        Mockito.when(readPredefinedTypesService.searchForUnitType(any())).thenReturn(Optional.of("SECOND"));
        Mockito.when(readPredefinedTypesService.searchForMetricType(any())).thenReturn(Optional.of("Aggregated"));
        var requestForMetricDefinition = new MetricDefinitionRequestDto();

        requestForMetricDefinition.metricName = "metric";
        requestForMetricDefinition.metricDescription = "description";
        requestForMetricDefinition.unitType = "SECOND";
        requestForMetricDefinition.metricType = "Aggregated";

        var metricDefinitionResponse = createMetricDefinition(requestForMetricDefinition);

        //then execute a request for creating a metric

        var requestForMetric = new MetricRequestDto();
        requestForMetric.resourceId = "3434349fjiirgjirj003-3r3f-f-";
        requestForMetric.start = "2022-01-05T09:13:07Z";
        requestForMetric.end = "2022-01-05T09:14:07Z";
        requestForMetric.value = 10.8;
        requestForMetric.metricDefinitionId = metricDefinitionResponse.id;

        var metric = createMetric(requestForMetric);

        assertEquals(requestForMetric.resourceId, metric.resourceId);
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
    public void fetchMetricNonHexId() {

        var response = given()
                .auth()
                .oauth2(getAccessToken("admin"))
                .get("/{metricId}", "dbhbhehbeo33m23")
                .then()
                .assertThat()
                .statusCode(404)
                .extract()
                .as(InformativeResponse.class);

        assertEquals("There is no Metric with the following id: dbhbhehbeo33m23", response.message);
    }

    @Test
    public void fetchMetric() {

        //first create a metric registration

        var metricDefinition = new MetricDefinition();
        metricDefinition.setMetricName("metric");
        metricDefinition.setMetricDescription("description");
        metricDefinition.setUnitType("SECOND");
        metricDefinition.setMetricType("Aggregated");

        metricDefinitionRepository.persist(metricDefinition);

        //then create a virtual metric
        var metric = new Metric();
        metric.setMetricDefinitionId(metricDefinition.getId().toString());
        metric.setResourceId("3434349fjiirgjirj003-3r3f-f-");
        metric.setStart(Instant.now());
        metric.setEnd(Instant.now());
        metric.setValue(10.8);

        metricRepository.persist(metric);

        var response = given()
                .auth()
                .oauth2(getAccessToken("admin"))
                .get("/{metricId}", metric.getId().toString())
                .then()
                .assertThat()
                .statusCode(200)
                .extract()
                .as(MetricResponseDto.class);

        assertEquals(metric.getId().toString(), response.id);
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
    public void deleteMetricNonHexId() {

        var response = given()
                .auth()
                .oauth2(getAccessToken("admin"))
                .delete("/{metricId}", "33333")
                .then()
                .assertThat()
                .statusCode(404)
                .extract()
                .as(InformativeResponse.class);

        assertEquals("There is no Metric with the following id: 33333", response.message);
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

        //first create a metric definition

        var metricDefinition = createMetricDefinition(requestForMetricDefinition);

        //afterwards create a metric

        var requestForMetric = new MetricRequestDto();
        requestForMetric.resourceId = "3434349fjiirgjirj003-3r3f-f-";
        requestForMetric.start = "2022-01-05T09:13:07Z";
        requestForMetric.end = "2022-01-05T09:14:07Z";
        requestForMetric.value = 10.8;
        requestForMetric.metricDefinitionId = metricDefinition.id;

        //then delete the created metric

        var metric = createMetric(requestForMetric);

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

        var createdMetricDefinition = createMetricDefinition(requestForMetricDefinition);

        // create a metric
        var requestForMetric = new MetricRequestDto();
        requestForMetric.resourceId = "3434349fjiirgjirj003-3r3f-f-";
        requestForMetric.start = "2022-01-05T09:13:07Z";
        requestForMetric.end = "2022-01-05T09:14:07Z";
        requestForMetric.value = 10.8;
        requestForMetric.metricDefinitionId = createdMetricDefinition.id;

        var metric = createMetric(requestForMetric);

        var response = given()
                .auth()
                .oauth2(getAccessToken("admin"))
                .contentType(ContentType.JSON)
                .patch("/{id}", metric.id)
                .then()
                .assertThat()
                .statusCode(400)
                .extract()
                .as(InformativeResponse.class);

        assertEquals("The request body is empty.", response.message);
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

        var createdMetricDefinition = createMetricDefinition(requestForMetricDefinition);

        // create a metric
        var requestForMetric = new MetricRequestDto();
        requestForMetric.resourceId = "3434349fjiirgjirj003-3r3f-f-";
        requestForMetric.start = "2022-01-05T09:13:07Z";
        requestForMetric.end = "2022-01-05T09:14:07Z";
        requestForMetric.value = 10.8;
        requestForMetric.metricDefinitionId = createdMetricDefinition.id;

        var metric = createMetric(requestForMetric);

        // update an existing metric
        var updateMetricRequest = new UpdateMetricRequestDto();

        updateMetricRequest.start = "2023-01-0509:13:07";

        var response = given()
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

        assertEquals("time_period_start must be a valid zulu timestamp. found: 2023-01-0509:13:07", response.message);
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

        var createdMetricDefinition = createMetricDefinition(requestForMetricDefinition);

        // create a metric
        var requestForMetric = new MetricRequestDto();
        requestForMetric.resourceId = "3434349fjiirgjirj003-3r3f-f-";
        requestForMetric.start = "2022-01-05T09:13:07Z";
        requestForMetric.end = "2022-01-05T09:14:07Z";
        requestForMetric.value = 10.8;
        requestForMetric.metricDefinitionId = createdMetricDefinition.id;

        var metricResponse = createMetric(requestForMetric);

        // update an existing metric
        var updateMetricRequest = new UpdateMetricRequestDto();

        updateMetricRequest.resourceId = "updatedResourceId";
        updateMetricRequest.start = "2023-01-05T09:13:07Z";
        updateMetricRequest.end = "2024-01-05T09:13:07Z";
        updateMetricRequest.value = 15.8;
        updateMetricRequest.metricDefinitionId = "507f1f77bcf86cd799439011";


        var response = given()
                .auth()
                .oauth2(getAccessToken("admin"))
                .body(updateMetricRequest)
                .contentType(ContentType.JSON)
                .patch("/{id}", metricResponse.id)
                .then()
                .assertThat()
                .statusCode(404)
                .extract()
                .as(InformativeResponse.class);

        assertEquals("There is no Metric Definition with the following id: 507f1f77bcf86cd799439011", response.message);
    }

    @Test
    public void updateMetricMetricDefinitionNonHexId() {

        Mockito.when(readPredefinedTypesService.searchForUnitType(any())).thenReturn(Optional.of("SECOND"));
        Mockito.when(readPredefinedTypesService.searchForMetricType(any())).thenReturn(Optional.of("Aggregated"));
        var requestForMetricDefinition = new MetricDefinitionRequestDto();

        requestForMetricDefinition.metricName = "metric";
        requestForMetricDefinition.metricDescription = "description";
        requestForMetricDefinition.unitType = "SECOND";
        requestForMetricDefinition.metricType = "Aggregated";

        var createdMetricDefinition = createMetricDefinition(requestForMetricDefinition);

        // create a metric
        var requestForMetric = new MetricRequestDto();
        requestForMetric.resourceId = "3434349fjiirgjirj003-3r3f-f-";
        requestForMetric.start = "2022-01-05T09:13:07Z";
        requestForMetric.end = "2022-01-05T09:14:07Z";
        requestForMetric.value = 10.8;
        requestForMetric.metricDefinitionId = createdMetricDefinition.id;

        var metricResponse = createMetric(requestForMetric);

        // update an existing metric
        var updateMetricRequest = new UpdateMetricRequestDto();

        updateMetricRequest.resourceId = "updatedResourceId";
        updateMetricRequest.start = "2023-01-05T09:13:07Z";
        updateMetricRequest.end = "2024-01-05T09:13:07Z";
        updateMetricRequest.value = 15.8;
        updateMetricRequest.metricDefinitionId = "30dmn93jn3j";


        var response = given()
                .auth()
                .oauth2(getAccessToken("admin"))
                .body(updateMetricRequest)
                .contentType(ContentType.JSON)
                .patch("/{id}", metricResponse.id)
                .then()
                .assertThat()
                .statusCode(404)
                .extract()
                .as(InformativeResponse.class);

        assertEquals("There is no Metric Definition with the following id: 30dmn93jn3j", response.message);
    }

    @Test
    public void updateMetricMetricNotFound() {

        // update an existing metric
        var updateMetricRequest = new UpdateMetricRequestDto();

        updateMetricRequest.resourceId = "updatedResourceId";
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

        var createdMetricDefinition = createMetricDefinition(requestForMetricDefinition);

        // create a metric
        var requestForMetric = new MetricRequestDto();
        requestForMetric.resourceId = "3434349fjiirgjirj003-3r3f-f-";
        requestForMetric.start = "2021-01-05T09:13:07Z";
        requestForMetric.end = "2022-01-05T09:14:07Z";
        requestForMetric.value = 10.8;
        requestForMetric.metricDefinitionId = createdMetricDefinition.id;

        var metricResponse = createMetric(requestForMetric);

        // update an existing metric
        var updateMetricRequest = new UpdateMetricRequestDto();

        updateMetricRequest.resourceId = "updatedResourceId";
        updateMetricRequest.start = "2023-01-05T09:13:07Z";


        var response = given()
                .auth()
                .oauth2(getAccessToken("admin"))
                .body(updateMetricRequest)
                .contentType(ContentType.JSON)
                .patch("/{id}", metricResponse.id)
                .then()
                .assertThat()
                .statusCode(400)
                .extract()
                .as(InformativeResponse.class);

        assertEquals("Timestamp of the starting date time cannot be after of Timestamp of the end date time.", response.message);
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

        var createdMetricDefinition = createMetricDefinition(requestForMetricDefinition);

        // create a metric
        var requestForMetric = new MetricRequestDto();
        requestForMetric.resourceId = "3434349fjiirgjirj003-3r3f-f-";
        requestForMetric.start = "2021-01-05T09:13:07Z";
        requestForMetric.end = "2022-01-05T09:14:07Z";
        requestForMetric.value = 10.8;
        requestForMetric.metricDefinitionId = createdMetricDefinition.id;

        var metricResponse = createMetric(requestForMetric);

        // update an existing metric
        var updateMetricRequest = new UpdateMetricRequestDto();

        updateMetricRequest.resourceId = "updatedResourceId";
        updateMetricRequest.start = "2025-01-05T09:13:07Z";
        updateMetricRequest.end = "2024-01-05T09:13:07Z";


        var response = given()
                .auth()
                .oauth2(getAccessToken("admin"))
                .body(updateMetricRequest)
                .contentType(ContentType.JSON)
                .patch("/{id}", metricResponse.id)
                .then()
                .assertThat()
                .statusCode(400)
                .extract()
                .as(InformativeResponse.class);

        assertEquals("Timestamp of the starting date time cannot be after of Timestamp of the end date time.", response.message);
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

        var createdMetricDefinition = createMetricDefinition(requestForMetricDefinition);

        // create a metric
        var requestForMetric = new MetricRequestDto();
        requestForMetric.resourceId = "3434349fjiirgjirj003-3r3f-f-";
        requestForMetric.start = "2021-01-05T09:13:07Z";
        requestForMetric.end = "2022-01-05T09:14:07Z";
        requestForMetric.value = 10.8;
        requestForMetric.metricDefinitionId = createdMetricDefinition.id;

        var metricResponse = createMetric(requestForMetric);

        // update an existing metric
        var updateMetricRequest = new UpdateMetricRequestDto();

        updateMetricRequest.resourceId = "updatedResourceId";
        updateMetricRequest.end = "2020-01-05T09:13:07Z";


        var response = given()
                .auth()
                .oauth2(getAccessToken("admin"))
                .body(updateMetricRequest)
                .contentType(ContentType.JSON)
                .patch("/{id}", metricResponse.id)
                .then()
                .assertThat()
                .statusCode(400)
                .extract()
                .as(InformativeResponse.class);

        assertEquals("Timestamp of the end date time cannot be before of Timestamp of the starting date time.", response.message);
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

        var createdMetricDefinition = createMetricDefinition(requestForMetricDefinition);

        // create a metric
        var requestForMetric = new MetricRequestDto();
        requestForMetric.resourceId = "3434349fjiirgjirj003-3r3f-f-";
        requestForMetric.start = "2021-01-05T09:13:07Z";
        requestForMetric.end = "2022-01-05T09:14:07Z";
        requestForMetric.value = 10.8;
        requestForMetric.metricDefinitionId = createdMetricDefinition.id;

        var metricResponse = createMetric(requestForMetric);

        // update an existing metric
        var updateMetricRequest = new UpdateMetricRequestDto();

        updateMetricRequest.resourceId = "updatedResourceId";
        updateMetricRequest.start = "2024-01-05T09:13:07Z";
        updateMetricRequest.end = "2024-01-05T09:13:07Z";


        var response = given()
                .auth()
                .oauth2(getAccessToken("admin"))
                .body(updateMetricRequest)
                .contentType(ContentType.JSON)
                .patch("/{id}", metricResponse.id)
                .then()
                .assertThat()
                .statusCode(400)
                .extract()
                .as(InformativeResponse.class);

        assertEquals("Timestamp of the starting date time cannot be equal to Timestamp of the end date time.", response.message);
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

        var createdMetricDefinition = createMetricDefinition(requestForMetricDefinition);

        // create a metric
        var requestForMetric = new MetricRequestDto();
        requestForMetric.resourceId = "3434349fjiirgjirj003-3r3f-f-";
        requestForMetric.start = "2021-01-05T09:13:07Z";
        requestForMetric.end = "2022-01-05T09:14:07Z";
        requestForMetric.value = 10.8;
        requestForMetric.metricDefinitionId = createdMetricDefinition.id;

        var metricResponse = createMetric(requestForMetric);

        // update an existing metric
        var updateMetricRequest = new UpdateMetricRequestDto();

        updateMetricRequest.resourceId = "updatedResourceId";
        updateMetricRequest.start = "2022-01-05T09:14:07Z";

        var response = given()
                .auth()
                .oauth2(getAccessToken("admin"))
                .body(updateMetricRequest)
                .contentType(ContentType.JSON)
                .patch("/{id}", metricResponse.id)
                .then()
                .assertThat()
                .statusCode(400)
                .extract()
                .as(InformativeResponse.class);

        assertEquals("Timestamp of the starting date time cannot be equal to Timestamp of the end date time.", response.message);
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

        var createdMetricDefinition = createMetricDefinition(requestForMetricDefinition);

        // create a metric
        var requestForMetric = new MetricRequestDto();
        requestForMetric.resourceId = "3434349fjiirgjirj003-3r3f-f-";
        requestForMetric.start = "2021-01-05T09:13:07Z";
        requestForMetric.end = "2022-01-05T09:14:07Z";
        requestForMetric.value = 10.8;
        requestForMetric.metricDefinitionId = createdMetricDefinition.id;

        var metricResponse = createMetric(requestForMetric);

        // update an existing metric
        var updateMetricRequest = new UpdateMetricRequestDto();

        updateMetricRequest.resourceId = "updatedResourceId";
        updateMetricRequest.end = "2021-01-05T09:13:07Z";

        var response = given()
                .auth()
                .oauth2(getAccessToken("admin"))
                .body(updateMetricRequest)
                .contentType(ContentType.JSON)
                .patch("/{id}", metricResponse.id)
                .then()
                .assertThat()
                .statusCode(400)
                .extract()
                .as(InformativeResponse.class);

        assertEquals("Timestamp of the starting date time cannot be equal to Timestamp of the end date time.", response.message);
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

        var createdMetricDefinition = createMetricDefinition(requestForMetricDefinition);

        //create another metric definition
        Mockito.when(readPredefinedTypesService.searchForUnitType(any())).thenReturn(Optional.of("#"));
        Mockito.when(readPredefinedTypesService.searchForMetricType(any())).thenReturn(Optional.of("count"));
        var requestForMetricDefinition1 = new MetricDefinitionRequestDto();

        requestForMetricDefinition1.metricName = "metric";
        requestForMetricDefinition1.metricDescription = "description";
        requestForMetricDefinition1.unitType = "#";
        requestForMetricDefinition1.metricType = "count";

        var createdMetricDefinition1 = createMetricDefinition(requestForMetricDefinition1);


        // create a metric
        var requestForMetric = new MetricRequestDto();
        requestForMetric.resourceId = "3434349fjiirgjirj003-3r3f-f-";
        requestForMetric.start = "2022-01-05T09:13:07Z";
        requestForMetric.end = "2022-01-05T09:14:07Z";
        requestForMetric.value = 10.8;
        requestForMetric.metricDefinitionId = createdMetricDefinition.id;

        var metricResponse = createMetric(requestForMetric);

        // update an existing metric
        var updateMetricRequest = new UpdateMetricRequestDto();

        updateMetricRequest.resourceId = "updatedResourceId";
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
                .patch("/{id}", metricResponse.id)
                .then()
                .assertThat()
                .statusCode(200)
                .body("metric_id", is(metricResponse.id))
                .body("resource_id", is(updateMetricRequest.resourceId))
                .body("metric_definition_id", is(updateMetricRequest.metricDefinitionId))
                .body("time_period_start", is(updateMetricRequest.start))
                .body("time_period_end", is(updateMetricRequest.end))
                .body("value", is(updateMetricRequest.value))
                .extract()
                .as(MetricResponseDto.class);

        assertEquals("updatedResourceId", updateResponse.resourceId);
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

        var createdMetricDefinition = createMetricDefinition(requestForMetricDefinition);

        // create a metric
        var requestForMetric = new MetricRequestDto();
        requestForMetric.resourceId = "3434349fjiirgjirj003-3r3f-f-";
        requestForMetric.start = "2022-01-05T09:13:07Z";
        requestForMetric.end = "2022-01-05T09:14:07Z";
        requestForMetric.value = 10.8;
        requestForMetric.metricDefinitionId = createdMetricDefinition.id;

        var metricResponse = createMetric(requestForMetric);

        // update an existing metric
        var updateMetricRequest = new UpdateMetricRequestDto();

        updateMetricRequest.resourceId = "updatedResourceId";

        var updateResponse = given()
                .auth()
                .oauth2(getAccessToken("admin"))
                .config(RestAssured.config()
                        .jsonConfig(JsonConfig.jsonConfig()
                                .numberReturnType(JsonPathConfig.NumberReturnType.DOUBLE)))
                .body(updateMetricRequest)
                .contentType(ContentType.JSON)
                .patch("/{id}", metricResponse.id)
                .then()
                .assertThat()
                .statusCode(200)
                .body("metric_id", is(metricResponse.id))
                .body("resource_id", is(updateMetricRequest.resourceId))
                .body("metric_definition_id", is(requestForMetric.metricDefinitionId))
                .body("time_period_start", is(requestForMetric.start))
                .body("time_period_end", is(requestForMetric.end))
                .body("value", is(requestForMetric.value))
                .extract()
                .as(MetricResponseDto.class);

        assertEquals(requestForMetric.metricDefinitionId, updateResponse.metricDefinitionId);
    }

    private MetricDefinitionResponseDto createMetricDefinition(MetricDefinitionRequestDto request){

        return given()
                .auth()
                .oauth2(getAccessToken("admin"))
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

    private MetricResponseDto createMetric(MetricRequestDto requestDto){
        return given()
                .auth()
                .oauth2(getAccessToken("admin"))
                .body(requestDto)
                .contentType(ContentType.JSON)
                .post()
                .then()
                .assertThat()
                .statusCode(201)
                .extract()
                .as(MetricResponseDto.class);
    }

    protected String getAccessToken(String userName) {
        return keycloakClient.getAccessToken(userName);
    }
}

