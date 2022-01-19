package org.accounting.system;

import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import io.quarkus.test.junit.mockito.InjectMock;
import io.restassured.http.ContentType;
import org.accounting.system.dtos.InformativeResponse;
import org.accounting.system.dtos.MetricDefinitionDtoRequest;
import org.accounting.system.dtos.MetricDefinitionDtoResponse;
import org.accounting.system.dtos.MetricRequestDto;
import org.accounting.system.dtos.MetricResponseDto;
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
    }

    @Test
    public void create_metric_bad_request() {

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
        request.end = "2022-01-05T09:13:07Z";
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
    public void create_metric_empty_metric_definition() {

        var request = new MetricRequestDto();
        request.start = "2022-01-05T09:13:07Z";
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

        assertEquals("metric_definition_id may not be empty.", response.message);
    }

    @Test
    public void create_metric_internal_server_error() {

        var request = new MetricRequestDto();
        request.start = "2022-01-05T09:13:07Z";
        request.end = "2022-01-05T09:13:07Z";
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
    public void create_metric() {

        //first create a metric definition

        Mockito.when(readPredefinedTypesService.searchForUnitType(any())).thenReturn(Optional.of("SECOND"));
        Mockito.when(readPredefinedTypesService.searchForMetricType(any())).thenReturn(Optional.of("Aggregated"));
        var requestForMetricDefinition = new MetricDefinitionDtoRequest();

        requestForMetricDefinition.metricName = "metric";
        requestForMetricDefinition.metricDescription = "description";
        requestForMetricDefinition.unitType = "SECOND";
        requestForMetricDefinition.metricType = "Aggregated";

        var metricDefinitionResponse = given()
                .basePath("/accounting-system/metric-definition")
                .body(requestForMetricDefinition)
                .contentType(ContentType.JSON)
                .post()
                .then()
                .assertThat()
                .statusCode(201)
                .extract()
                .as(MetricDefinitionDtoResponse.class);

        //then execute a request for creating a metric

        var requestForMetric = new MetricRequestDto();
        requestForMetric.resourceId = "3434349fjiirgjirj003-3r3f-f-";
        requestForMetric.start = "2022-01-05T09:13:07Z";
        requestForMetric.end = "2022-01-05T09:13:07Z";
        requestForMetric.value = 10.8;
        requestForMetric.metricDefinitionId = metricDefinitionResponse.id;

        given()
                .body(requestForMetric)
                .contentType(ContentType.JSON)
                .post()
                .then()
                .assertThat()
                .statusCode(201);
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
}

