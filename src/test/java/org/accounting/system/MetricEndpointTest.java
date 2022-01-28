package org.accounting.system;

import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import io.quarkus.test.junit.mockito.InjectMock;
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
import org.accounting.system.repositories.MetricDefinitionRepository;
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

    @BeforeEach
    public void setup() {
        metricDefinitionRepository.deleteAll();
        metricRepository.deleteAll();
    }

    @Test
    public void create_metric_request_body_is_empty() {

        var response = given()
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
    public void create_metric_no_metric_definition() {

        var request = new MetricRequestDto();
        request.start = "2022-01-05T09:13:07Z";
        request.end = "2022-01-05T09:14:07Z";
        request.metricDefinitionId = "507f1f77bcf86cd799439011";
        request.resourceId = "resource-id";
        request.value = 10.3;

        var response = given()
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
    public void create_metric_cannot_consume_content_type() {

        var request = new MetricRequestDto();

        var response = given()
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
    public void create_metric_empty_metric_definition_attribute() {

        var request = new MetricRequestDto();
        request.start = "2022-01-05T09:13:07Z";
        request.end = "2022-01-05T09:14:07Z";
        request.resourceId = "resource-id";
        request.value = 10.3;

        var response = given()
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
    public void create_metric_no_valid_zulu_timestamp() {

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
    public void create_metric_no_zulu_timestamp() {

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
    public void create_metric_internal_server_error() {

        var request = new MetricRequestDto();
        request.start = "2022-01-05T09:13:07Z";
        request.end = "2022-01-05T09:14:07Z";
        request.metricDefinitionId = "iiejijirj33i3i";
        request.resourceId = "resource-id";
        request.value = 10.3;

        given()
                .body(request)
                .contentType(ContentType.JSON)
                .post()
                .then()
                .assertThat()
                .statusCode(500);
    }

    @Test
    public void create_metric_start_is_after_end() {

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
    public void create_metric_start_is_equal_end() {

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
    public void create_metric() {

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

        createMetric(requestForMetric);
    }

    @Test
    public void fetch_metric_not_found() {

        given()
                .get("/{metric_id}", "507f1f77bcf86cd799439011")
                .then()
                .assertThat()
                .statusCode(404);
    }

    @Test
    public void fetch_metric() {

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
                .get("/{metric_id}", metric.getId().toString())
                .then()
                .assertThat()
                .statusCode(200)
                .extract()
                .as(MetricResponseDto.class);

        assertEquals(metric.getId().toString(), response.id);
    }

    @Test
    public void delete_metric_not_found() {

        var response = given()
                .delete("/{metric_id}", "507f1f77bcf86cd799439011")
                .then()
                .assertThat()
                .statusCode(404)
                .extract()
                .as(InformativeResponse.class);

        assertEquals("There is no Metric with the following id: 507f1f77bcf86cd799439011", response.message);
    }

    @Test
    public void delete_metric() {

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

        given()
                .delete("/{metric_id}", metric.id)
                .then()
                .assertThat()
                .statusCode(200)
                .extract()
                .as(InformativeResponse.class);
    }

    @Test
    public void update_metric_request_body_is_empty() {

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
    public void update_metric_cannot_consume_content_type() {

        var response = given()
                .patch("/{id}", "507f1f77bcf86cd799439011")
                .then()
                .assertThat()
                .statusCode(415)
                .extract()
                .as(InformativeResponse.class);

        assertEquals("Cannot consume content type.", response.message);
    }

    @Test
    public void update_metric_metric_definition_not_valid_zulu_timestamp() {

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
    public void update_metric_metric_definition_not_found() {

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

        updateMetricRequest.resourceId = "updated_resource_id";
        updateMetricRequest.start = "2023-01-05T09:13:07Z";
        updateMetricRequest.end = "2024-01-05T09:13:07Z";
        updateMetricRequest.value = 15.8;
        updateMetricRequest.metricDefinitionId = "507f1f77bcf86cd799439011";


        var response = given()
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
    public void update_metric_metric_not_found() {

        // update an existing metric
        var updateMetricRequest = new UpdateMetricRequestDto();

        updateMetricRequest.resourceId = "updated_resource_id";
        updateMetricRequest.start = "2023-01-05T09:13:07Z";
        updateMetricRequest.end = "2024-01-05T09:14:07Z";
        updateMetricRequest.value = 15.8;
        updateMetricRequest.metricDefinitionId = "507f1f77bcf86cd799439011";


        var response = given()
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
    public void update_metric_modified_start_cannot_be_after_end() {

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

        updateMetricRequest.resourceId = "updated_resource_id";
        updateMetricRequest.start = "2023-01-05T09:13:07Z";


        var response = given()
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
    public void update_metric_modified_start_cannot_be_after_modified_end() {

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

        updateMetricRequest.resourceId = "updated_resource_id";
        updateMetricRequest.start = "2025-01-05T09:13:07Z";
        updateMetricRequest.end = "2024-01-05T09:13:07Z";


        var response = given()
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
    public void update_metric_modified_end_cannot_be_before_start() {

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

        updateMetricRequest.resourceId = "updated_resource_id";
        updateMetricRequest.end = "2020-01-05T09:13:07Z";


        var response = given()
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
    public void update_metric_modified_start_cannot_be_equal_to_modified_end() {

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

        updateMetricRequest.resourceId = "updated_resource_id";
        updateMetricRequest.start = "2024-01-05T09:13:07Z";
        updateMetricRequest.end = "2024-01-05T09:13:07Z";


        var response = given()
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
    public void update_metric_modified_start_cannot_be_equal_to_end() {

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

        updateMetricRequest.resourceId = "updated_resource_id";
        updateMetricRequest.start = "2022-01-05T09:14:07Z";

        var response = given()
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
    public void update_metric_start_cannot_be_equal_to_modified_end() {

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

        updateMetricRequest.resourceId = "updated_resource_id";
        updateMetricRequest.end = "2021-01-05T09:13:07Z";

        var response = given()
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
    public void update_metric_full() {

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

        updateMetricRequest.resourceId = "updated_resource_id";
        updateMetricRequest.start = "2023-01-05T09:13:07Z";
        updateMetricRequest.end = "2024-01-05T09:14:07Z";
        updateMetricRequest.value = 15.8;
        updateMetricRequest.metricDefinitionId = createdMetricDefinition1.id;

        given()
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
                .body("value", is(updateMetricRequest.value));
    }

    @Test
    public void update_metric_partial() {

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

        updateMetricRequest.resourceId = "updated_resource_id";

        given()
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
                .body("value", is(requestForMetric.value));
    }

    private MetricDefinitionResponseDto createMetricDefinition(MetricDefinitionRequestDto request){

        return given()
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
                .body(requestDto)
                .contentType(ContentType.JSON)
                .post()
                .then()
                .assertThat()
                .statusCode(201)
                .extract()
                .as(MetricResponseDto.class);
    }
}

