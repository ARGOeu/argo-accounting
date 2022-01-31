package org.accounting.system;

import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import io.quarkus.test.junit.mockito.InjectMock;
import io.restassured.http.ContentType;
import org.accounting.system.dtos.InformativeResponse;
import org.accounting.system.dtos.MetricDefinitionRequestDto;
import org.accounting.system.dtos.MetricDefinitionResponseDto;
import org.accounting.system.dtos.MetricRequestDto;
import org.accounting.system.dtos.MetricResponseDto;
import org.accounting.system.dtos.UpdateMetricDefinitionRequestDto;
import org.accounting.system.endpoints.MetricDefinitionEndpoint;
import org.accounting.system.entities.MetricDefinition;
import org.accounting.system.mappers.MetricDefinitionMapper;
import org.accounting.system.repositories.MetricDefinitionRepository;
import org.accounting.system.services.ReadPredefinedTypesService;
import org.accounting.system.util.Predicates;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javax.inject.Inject;
import java.util.Optional;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;

@QuarkusTest
@TestProfile(AccountingSystemTestProfile.class)
@TestHTTPEndpoint(MetricDefinitionEndpoint.class)
public class MetricDefinitionEndpointTest {

    @Inject
    MetricDefinitionRepository metricDefinitionRepository;

    @Inject
    Predicates predicates;

    @InjectMock
    ReadPredefinedTypesService readPredefinedTypesService;

    @BeforeEach
    public void setup() {
        metricDefinitionRepository.deleteAll();
    }


    @Test
    public void create_metric_definition_request_body_is_empty() {

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
    public void create_metric_definition_cannot_consume_content_type() {

        var response = given()
                .post()
                .then()
                .assertThat()
                .statusCode(415)
                .extract()
                .as(InformativeResponse.class);

        assertEquals("Cannot consume content type.", response.message);
    }

    @Test
    public void create_metric_definition_no_unit_type() {

        Mockito.when(readPredefinedTypesService.searchForUnitType(any())).thenReturn(Optional.empty());
        Mockito.when(readPredefinedTypesService.searchForMetricType(any())).thenReturn(Optional.of("Aggregated"));
        MetricDefinitionRequestDto request= new MetricDefinitionRequestDto();

        request.metricName = "metric";
        request.metricDescription = "description";
        request.unitType = "SECOND";
        request.metricType = "Aggregated";

        InformativeResponse response = given()
                .body(request)
                .contentType(ContentType.JSON)
                .post()
                .then()
                .assertThat()
                .statusCode(404)
                .extract()
                .as(InformativeResponse.class);

        assertEquals("There is no unit type : SECOND", response.message);
    }

    @Test
    public void create_metric_definition_no_metric_type() {

        Mockito.when(readPredefinedTypesService.searchForUnitType(any())).thenReturn(Optional.of("SECOND"));
        Mockito.when(readPredefinedTypesService.searchForMetricType(any())).thenReturn(Optional.empty());
        MetricDefinitionRequestDto request= new MetricDefinitionRequestDto();

        request.metricName = "metric";
        request.metricDescription = "description";
        request.unitType = "SECOND";
        request.metricType = "Aggregated";

        InformativeResponse response = given()
                .body(request)
                .contentType(ContentType.JSON)
                .post()
                .then()
                .assertThat()
                .statusCode(404)
                .extract()
                .as(InformativeResponse.class);

        assertEquals("There is no metric type : Aggregated", response.message);
    }

    @Test
    public void create_metric_definition_metric_name_is_empty() {

        Mockito.when(readPredefinedTypesService.searchForUnitType(any())).thenReturn(Optional.of("SECOND"));
        Mockito.when(readPredefinedTypesService.searchForMetricType(any())).thenReturn(Optional.of("Aggregated"));

        var request= new MetricDefinitionRequestDto();
        request.metricDescription = "description";
        request.unitType = "SECOND";
        request.metricType = "Aggregated";

        var response = given()
                .body(request)
                .contentType(ContentType.JSON)
                .post()
                .then()
                .assertThat()
                .statusCode(400)
                .extract()
                .as(InformativeResponse.class);

        assertEquals("metric_name may not be empty.", response.message);
    }

    @Test
    public void create_metric_definition_unit_type_is_empty() {

        var request= new MetricDefinitionRequestDto();
        request.unitType="";
        request.metricName = "metric";
        request.metricDescription = "description";
        request.metricType = "Aggregated";

        var response = given()
                .body(request)
                .contentType(ContentType.JSON)
                .post()
                .then()
                .assertThat()
                .statusCode(400)
                .extract()
                .as(InformativeResponse.class);

        assertEquals("unit_type may not be empty.", response.message);
    }

    @Test
    public void create_metric_definition_metric_type_is_empty() {

        var request= new MetricDefinitionRequestDto();
        request.metricName = "metric";
        request.unitType = "SECOND";
        request.metricDescription = "description";

        var response = given()
                .body(request)
                .contentType(ContentType.JSON)
                .post()
                .then()
                .assertThat()
                .statusCode(400)
                .extract()
                .as(InformativeResponse.class);

        assertEquals("metric_type may not be empty.", response.message);
    }

    @Test
    public void create_metric_definition_similar() {

        Mockito.when(readPredefinedTypesService.searchForUnitType(any())).thenReturn(Optional.of("SECOND"));
        Mockito.when(readPredefinedTypesService.searchForMetricType(any())).thenReturn(Optional.of("Aggregated"));
        MetricDefinitionRequestDto request= new MetricDefinitionRequestDto();

        request.metricName = "metric";
        request.metricDescription = "description";
        request.unitType = "SECOND";
        request.metricType = "Aggregated";

        createMetricDefinition(request);

        given()
                .body(request)
                .contentType(ContentType.JSON)
                .post()
                .then()
                .assertThat()
                .statusCode(409)
                .extract()
                .as(InformativeResponse.class);
    }

    @Test
    public void create_metric_definition() {

        Mockito.when(readPredefinedTypesService.searchForUnitType(any())).thenReturn(Optional.of("SECOND"));
        Mockito.when(readPredefinedTypesService.searchForMetricType(any())).thenReturn(Optional.of("Aggregated"));
        MetricDefinitionRequestDto request= new MetricDefinitionRequestDto();

        request.metricName = "metric";
        request.metricDescription = "description";
        request.unitType = "SECOND";
        request.metricType = "Aggregated";

        createMetricDefinition(request);
    }

    @Test
    public void update_metric_definition_cannot_consume_content_type() {

        Mockito.when(readPredefinedTypesService.searchForUnitType(any())).thenReturn(Optional.of("SECOND"));
        Mockito.when(readPredefinedTypesService.searchForMetricType(any())).thenReturn(Optional.of("Aggregated"));
        MetricDefinitionRequestDto request= new MetricDefinitionRequestDto();

        request.metricName = "metric";
        request.metricDescription = "description";
        request.unitType = "SECOND";
        request.metricType = "Aggregated";

        var metricDefinition = createMetricDefinition(request);

        var response = given()
                .patch("/{id}", metricDefinition.id)
                .then()
                .assertThat()
                .statusCode(415)
                .extract()
                .as(InformativeResponse.class);

        assertEquals("Cannot consume content type.", response.message);
    }

    @Test
    public void update_metric_definition_full() {

        Mockito.when(readPredefinedTypesService.searchForUnitType(any())).thenReturn(Optional.of("SECOND"));
        Mockito.when(readPredefinedTypesService.searchForMetricType(any())).thenReturn(Optional.of("Aggregated"));
        var request= new MetricDefinitionRequestDto();

        request.metricName = "metric";
        request.metricDescription = "description";
        request.unitType = "SECOND";
        request.metricType = "Aggregated";

        var metricDefinition = createMetricDefinition(request);

        var metricDefinitiontoBeUpdated = new UpdateMetricDefinitionRequestDto();

        metricDefinitiontoBeUpdated.metricName = "updated_name";
        metricDefinitiontoBeUpdated.metricDescription = "updated_description";
        metricDefinitiontoBeUpdated.unitType = "updated_unit_type";
        metricDefinitiontoBeUpdated.metricType = "updated_metric_type";


        given()
                .body(metricDefinitiontoBeUpdated)
                .contentType(ContentType.JSON)
                .patch("/{id}", metricDefinition.id)
                .then()
                .assertThat()
                .statusCode(200)
                .body("metric_definition_id", is(metricDefinition.id))
                .body("metric_name", is(metricDefinitiontoBeUpdated.metricName))
                .body("metric_description", is(metricDefinitiontoBeUpdated.metricDescription))
                .body("unit_type", is(metricDefinitiontoBeUpdated.unitType))
                .body("metric_type", is(metricDefinitiontoBeUpdated.metricType));
    }

    @Test
    public void update_metric_definition_partial() {

        Mockito.when(readPredefinedTypesService.searchForUnitType(any())).thenReturn(Optional.of("SECOND"));
        Mockito.when(readPredefinedTypesService.searchForMetricType(any())).thenReturn(Optional.of("Aggregated"));
        var request= new MetricDefinitionRequestDto();

        request.metricName = "metric";
        request.metricDescription = "description";
        request.unitType = "SECOND";
        request.metricType = "Aggregated";

        var metricDefinition = createMetricDefinition(request);

        var metricDefinitionToBeUpdated = new MetricDefinitionRequestDto();

        metricDefinitionToBeUpdated.metricName = "updated_name";
        metricDefinitionToBeUpdated.metricDescription = "updated_description";

        Mockito.when(readPredefinedTypesService.searchForUnitType(any())).thenReturn(Optional.empty());
        Mockito.when(readPredefinedTypesService.searchForMetricType(any())).thenReturn(Optional.empty());

        given()
                .body(metricDefinitionToBeUpdated)
                .contentType(ContentType.JSON)
                .patch("/{id}", metricDefinition.id)
                .then()
                .assertThat()
                .statusCode(200)
                .body("metric_definition_id", is(metricDefinition.id))
                .body("metric_name", is(metricDefinitionToBeUpdated.metricName))
                .body("metric_description", is(metricDefinitionToBeUpdated.metricDescription))
                .body("unit_type", is(metricDefinition.unitType))
                .body("metric_type", is(metricDefinition.metricType));
    }

    @Test
    public void update_metric_definition_no_unit_type() {

        Mockito.when(readPredefinedTypesService.searchForUnitType(any())).thenReturn(Optional.of("SECOND"));
        Mockito.when(readPredefinedTypesService.searchForMetricType(any())).thenReturn(Optional.of("Aggregated"));
        var request= new MetricDefinitionRequestDto();

        request.metricName = "metric";
        request.metricDescription = "description";
        request.unitType = "SECOND";
        request.metricType = "Aggregated";

        var metricDefinition = createMetricDefinition(request);

        var metricDefinitionToBeUpdated = new UpdateMetricDefinitionRequestDto();

        Mockito.when(readPredefinedTypesService.searchForUnitType(any())).thenReturn(Optional.empty());

        metricDefinitionToBeUpdated.metricName = "updated_name";
        metricDefinitionToBeUpdated.metricDescription = "updated_description";
        metricDefinitionToBeUpdated.unitType = "SECOND";

        var response = given()
                .body(metricDefinitionToBeUpdated)
                .contentType(ContentType.JSON)
                .patch("/{id}", metricDefinition.id)
                .then()
                .assertThat()
                .statusCode(404)
                .extract()
                .as(InformativeResponse.class);

        assertEquals("There is no unit type : SECOND", response.message);
    }

    @Test
    public void update_metric_definition_no_metric_type() {

        Mockito.when(readPredefinedTypesService.searchForUnitType(any())).thenReturn(Optional.of("SECOND"));
        Mockito.when(readPredefinedTypesService.searchForMetricType(any())).thenReturn(Optional.of("Aggregated"));
        var request= new MetricDefinitionRequestDto();

        request.metricName = "metric";
        request.metricDescription = "description";
        request.unitType = "SECOND";
        request.metricType = "Aggregated";

        var metricDefinition = createMetricDefinition(request);

        var metricDefinitionToBeUpdated = new UpdateMetricDefinitionRequestDto();

        Mockito.when(readPredefinedTypesService.searchForUnitType(any())).thenReturn(Optional.of("SECOND"));
        Mockito.when(readPredefinedTypesService.searchForMetricType(any())).thenReturn(Optional.empty());

        metricDefinitionToBeUpdated.metricName = "updated_name";
        metricDefinitionToBeUpdated.metricDescription = "updated_description";
        metricDefinitionToBeUpdated.unitType = "SECOND";
        metricDefinitionToBeUpdated.metricType = "aggregated";


        var response = given()
                .body(metricDefinitionToBeUpdated)
                .contentType(ContentType.JSON)
                .patch("/{id}", metricDefinition.id)
                .then()
                .assertThat()
                .statusCode(404)
                .extract()
                .as(InformativeResponse.class);

        assertEquals("There is no metric type : aggregated", response.message);
    }

    @Test
    public void update_metric_definition_request_body_is_empty() {

        Mockito.when(readPredefinedTypesService.searchForUnitType(any())).thenReturn(Optional.of("SECOND"));
        Mockito.when(readPredefinedTypesService.searchForMetricType(any())).thenReturn(Optional.of("Aggregated"));
        var request= new MetricDefinitionRequestDto();

        request.metricName = "metric";
        request.metricDescription = "description";
        request.unitType = "SECOND";
        request.metricType = "Aggregated";

        var metricDefinition = createMetricDefinition(request);

        var response = given()
                .contentType(ContentType.JSON)
                .patch("/{id}", metricDefinition.id)
                .then()
                .assertThat()
                .statusCode(400)
                .extract()
                .as(InformativeResponse.class);

        assertEquals("The request body is empty.", response.message);
    }

    @Test
    public void update_metric_definition_with_mapper() {

        var metricDefinition = new MetricDefinition();

        metricDefinition.setId(new ObjectId("507f1f77bcf86cd799439011"));
        metricDefinition.setMetricName("name");
        metricDefinition.setMetricDescription("description");
        metricDefinition.setUnitType("unit");
        metricDefinition.setMetricType("metric_type");

        var dto= new UpdateMetricDefinitionRequestDto();

        dto.metricName = "updated_name";
        dto.metricDescription = "updated_description";

        MetricDefinitionMapper.INSTANCE.updateMetricDefinitionFromDto(dto, metricDefinition);

        assertEquals(dto.metricName, metricDefinition.getMetricName());
        assertEquals(dto.metricDescription, metricDefinition.getMetricDescription());
        assertEquals("unit", metricDefinition.getUnitType());
        assertEquals("metric_type", metricDefinition.getMetricType());
        assertEquals(new ObjectId("507f1f77bcf86cd799439011"), metricDefinition.getId());
    }

    @Test
    public void fetch_metric_definition_not_found() {

        var response = given()
                .get("/{id}", "507f1f77bcf86cd799439011")
                .then()
                .assertThat()
                .statusCode(404)
                .extract()
                .as(InformativeResponse.class);

        assertEquals("There is no Metric Definition with the following id: 507f1f77bcf86cd799439011", response.message);
    }

    @Test
    public void fetch_metric_definition_internal_server_error() {

        var response = given()
                .get("/{id}", "iiejijirj33i3i")
                .then()
                .assertThat()
                .statusCode(500)
                .extract()
                .as(InformativeResponse.class);

        assertEquals(500, response.code);
    }

    @Test
    public void fetch_metric_definition() {

        Mockito.when(readPredefinedTypesService.searchForUnitType(any())).thenReturn(Optional.of("SECOND"));
        Mockito.when(readPredefinedTypesService.searchForMetricType(any())).thenReturn(Optional.of("Aggregated"));
        var request= new MetricDefinitionRequestDto();

        request.metricName = "metric";
        request.metricDescription = "description";
        request.unitType = "SECOND";
        request.metricType = "Aggregated";

        var metricDefinition = createMetricDefinition(request);

        var storedMetricDefinition = given()
                .get("/{id}", metricDefinition.id)
                .then()
                .assertThat()
                .statusCode(200)
                .extract()
                .as(MetricDefinitionResponseDto.class);

        assertEquals(metricDefinition.id, storedMetricDefinition.id);
    }

    @Test
    public void fetch_metric_definition_pagination_metric() {

        Mockito.when(readPredefinedTypesService.searchForUnitType(any())).thenReturn(Optional.of("SECOND"));
        Mockito.when(readPredefinedTypesService.searchForMetricType(any())).thenReturn(Optional.of("Aggregated"));
        var request= new MetricDefinitionRequestDto();

        request.metricName = "metric";
        request.metricDescription = "description";
        request.unitType = "SECOND";
        request.metricType = "Aggregated";

        var metricDefinition = createMetricDefinition(request);

        var requestForMetric = new MetricRequestDto();
        requestForMetric.resourceId = "3434349fjiirgjirj003-3r3f-f-";
        requestForMetric.start = "2022-01-05T09:13:07Z";
        requestForMetric.end = "2023-01-05T09:13:07Z";
        requestForMetric.value = 10.8;
        requestForMetric.metricDefinitionId = metricDefinition.id;

        createMetric(requestForMetric);

        var requestForMetric1 = new MetricRequestDto();
        requestForMetric1.resourceId = "3434349fjiirgjirj003-3r3f-f-";
        requestForMetric1.start = "2022-01-05T09:13:07Z";
        requestForMetric1.end = "2023-01-05T09:13:07Z";
        requestForMetric1.value = 15;
        requestForMetric1.metricDefinitionId = metricDefinition.id;

        createMetric(requestForMetric1);

        given()
                .queryParam("size", 1)
                .get("/{metric_definition_id}/metrics", metricDefinition.id)
                .then()
                .assertThat()
                .statusCode(200);
    }

    @Test
    public void fetch_metric_definition_pagination_not_acceptable_size() {

        Mockito.when(readPredefinedTypesService.searchForUnitType(any())).thenReturn(Optional.of("SECOND"));
        Mockito.when(readPredefinedTypesService.searchForMetricType(any())).thenReturn(Optional.of("Aggregated"));
        var request= new MetricDefinitionRequestDto();

        request.metricName = "metric";
        request.metricDescription = "description";
        request.unitType = "SECOND";
        request.metricType = "Aggregated";

        var metricDefinition = createMetricDefinition(request);

        var response = given()
                .queryParam("size", 110)
                .get("/{metric_definition_id}/metrics", metricDefinition.id)
                .then()
                .assertThat()
                .statusCode(422)
                .extract()
                .as(InformativeResponse.class);

        assertEquals("You cannot request more than 100 items.",response.message);
    }

    @Test
    public void fetch_metric_definition_pagination_not_acceptable_page_index() {

        Mockito.when(readPredefinedTypesService.searchForUnitType(any())).thenReturn(Optional.of("SECOND"));
        Mockito.when(readPredefinedTypesService.searchForMetricType(any())).thenReturn(Optional.of("Aggregated"));
        var request= new MetricDefinitionRequestDto();

        request.metricName = "metric";
        request.metricDescription = "description";
        request.unitType = "SECOND";
        request.metricType = "Aggregated";

        var metricDefinition = createMetricDefinition(request);

        var response = given()
                .queryParam("page", 0)
                .get("/{metric_definition_id}/metrics", metricDefinition.id)
                .then()
                .assertThat()
                .statusCode(400)
                .extract()
                .as(InformativeResponse.class);

        assertEquals("Page index must be >= 1.",response.message);
    }

    @Test
    public void fetch_metric_definition_pagination_no_metric_definition() {

        var response = given()
                .queryParam("page", 0)
                .get("/{metric_definition_id}/metrics", "507f1f77bcf86cd799439011")
                .then()
                .assertThat()
                .statusCode(404)
                .extract()
                .as(InformativeResponse.class);

        assertEquals("There is no Metric Definition with the following id: 507f1f77bcf86cd799439011", response.message);
    }

    @Test
    public void fetch_all_metric_definitions() {

        Mockito.when(readPredefinedTypesService.searchForUnitType(any())).thenReturn(Optional.of("SECOND"));
        Mockito.when(readPredefinedTypesService.searchForMetricType(any())).thenReturn(Optional.of("Aggregated"));
        var request= new MetricDefinitionRequestDto();

        request.metricName = "metric";
        request.metricDescription = "description";
        request.unitType = "SECOND";
        request.metricType = "Aggregated";

        createMetricDefinition(request);

        var request1= new MetricDefinitionRequestDto();

        request1.metricName = "metric1";
        request1.metricDescription = "description";
        request1.unitType = "kg";
        request1.metricType = "Aggregated";

        createMetricDefinition(request1);

        given()
                .get()
                .then()
                .assertThat()
                .statusCode(200)
                .assertThat()
                .body("size()", is(2));
    }

    @Test
    public void delete_metric_definition_prohibited() {

        Mockito.when(readPredefinedTypesService.searchForUnitType(any())).thenReturn(Optional.of("SECOND"));
        Mockito.when(readPredefinedTypesService.searchForMetricType(any())).thenReturn(Optional.of("Aggregated"));
        var request= new MetricDefinitionRequestDto();

        request.metricName = "metric";
        request.metricDescription = "description";
        request.unitType = "SECOND";
        request.metricType = "Aggregated";

        var response = createMetricDefinition(request);

        //then execute a request for creating a metric

        var requestForMetric = new MetricRequestDto();
        requestForMetric.resourceId = "3434349fjiirgjirj003-3r3f-f-";
        requestForMetric.start = "2022-01-05T09:13:07Z";
        requestForMetric.end = "2022-01-05T09:15:07Z";
        requestForMetric.value = 10.8;
        requestForMetric.metricDefinitionId = response.id;

        createMetric(requestForMetric);

        var errorResponse = given()
                .delete("/{metric_definition_id}", response.id)
                .then()
                .assertThat()
                .statusCode(409)
                .extract()
                .as(InformativeResponse.class);

        assertEquals("The Metric Definition cannot be deleted. There is a Metric assigned to it.", errorResponse.message);
    }

    @Test
    public void delete_metric_definition() {

        Mockito.when(readPredefinedTypesService.searchForUnitType(any())).thenReturn(Optional.of("SECOND"));
        Mockito.when(readPredefinedTypesService.searchForMetricType(any())).thenReturn(Optional.of("Aggregated"));
        var request= new MetricDefinitionRequestDto();

        request.metricName = "metric";
        request.metricDescription = "description";
        request.unitType = "SECOND";
        request.metricType = "Aggregated";

       var response = createMetricDefinition(request);

        given()
                .delete("/{metric_definition_id}", response.id)
                .then()
                .assertThat()
                .statusCode(200)
                .extract()
                .as(InformativeResponse.class);
    }

    private MetricDefinitionResponseDto createMetricDefinition(MetricDefinitionRequestDto request){

        return given()
                .body(request)
                .contentType(ContentType.JSON)
                .post()
                .then()
                .assertThat()
                .statusCode(201)
                .extract()
                .as(MetricDefinitionResponseDto.class);
    }

    private MetricResponseDto createMetric(MetricRequestDto request){

        return given()
                .basePath("/accounting-system/metrics")
                .body(request)
                .contentType(ContentType.JSON)
                .post()
                .then()
                .assertThat()
                .statusCode(201)
                .extract()
                .as(MetricResponseDto.class);
    }
}