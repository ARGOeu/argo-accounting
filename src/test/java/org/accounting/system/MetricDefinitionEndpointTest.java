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
import org.accounting.system.dtos.UpdateMetricDefinitionDtoRequest;
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
    public void create_metric_definition_bad_request() {

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
        MetricDefinitionDtoRequest request= new MetricDefinitionDtoRequest();

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
        MetricDefinitionDtoRequest request= new MetricDefinitionDtoRequest();

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

        var request= new MetricDefinitionDtoRequest();
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

        var request= new MetricDefinitionDtoRequest();
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

        var request= new MetricDefinitionDtoRequest();
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
        MetricDefinitionDtoRequest request= new MetricDefinitionDtoRequest();

        request.metricName = "metric";
        request.metricDescription = "description";
        request.unitType = "SECOND";
        request.metricType = "Aggregated";

        given()
                .body(request)
                .contentType(ContentType.JSON)
                .post()
                .then()
                .assertThat()
                .statusCode(201)
                .extract()
                .as(MetricDefinitionDtoResponse.class);


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
        MetricDefinitionDtoRequest request= new MetricDefinitionDtoRequest();

        request.metricName = "metric";
        request.metricDescription = "description";
        request.unitType = "SECOND";
        request.metricType = "Aggregated";

        given()
                .body(request)
                .contentType(ContentType.JSON)
                .post()
                .then()
                .assertThat()
                .statusCode(201)
                .extract()
                .as(MetricDefinitionDtoResponse.class);
    }

    @Test
    public void update_metric_definition_cannot_consume_content_type() {

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
    public void update_metric_definition_full() {

        Mockito.when(readPredefinedTypesService.searchForUnitType(any())).thenReturn(Optional.of("SECOND"));
        Mockito.when(readPredefinedTypesService.searchForMetricType(any())).thenReturn(Optional.of("Aggregated"));
        var request= new UpdateMetricDefinitionDtoRequest();

        request.metricName = "metric";
        request.metricDescription = "description";
        request.unitType = "SECOND";
        request.metricType = "Aggregated";

        var createdMetricDefinition = given()
                .body(request)
                .contentType(ContentType.JSON)
                .post()
                .then()
                .extract()
                .as(MetricDefinitionDtoResponse.class);


        var updatedRequest = new UpdateMetricDefinitionDtoRequest();

        updatedRequest.metricName = "updated_name";
        updatedRequest.metricDescription = "updated_description";
        updatedRequest.unitType = "updated_unit_type";
        updatedRequest.metricType = "updated_metric_type";


        given()
                .body(updatedRequest)
                .contentType(ContentType.JSON)
                .patch("/{id}", createdMetricDefinition.id)
                .then()
                .assertThat()
                .statusCode(200)
                .body("metric_definition_id", is(createdMetricDefinition.id))
                .body("metric_name", is(updatedRequest.metricName))
                .body("metric_description", is(updatedRequest.metricDescription))
                .body("unit_type", is(updatedRequest.unitType))
                .body("metric_type", is(updatedRequest.metricType));

    }

    @Test
    public void update_metric_definition_partial() {

        Mockito.when(readPredefinedTypesService.searchForUnitType(any())).thenReturn(Optional.of("SECOND"));
        Mockito.when(readPredefinedTypesService.searchForMetricType(any())).thenReturn(Optional.of("Aggregated"));
        var request= new UpdateMetricDefinitionDtoRequest();

        request.metricName = "metric";
        request.metricDescription = "description";
        request.unitType = "SECOND";
        request.metricType = "Aggregated";

        var createdMetricDefinition = given()
                .body(request)
                .contentType(ContentType.JSON)
                .post()
                .then()
                .extract()
                .as(MetricDefinitionDtoResponse.class);

        var updatedRequest = new MetricDefinitionDtoRequest();

        updatedRequest.metricName = "updated_name";
        updatedRequest.metricDescription = "updated_description";


        given()
                .body(updatedRequest)
                .contentType(ContentType.JSON)
                .patch("/{id}", createdMetricDefinition.id)
                .then()
                .assertThat()
                .statusCode(200)
                .body("metric_definition_id", is(createdMetricDefinition.id))
                .body("metric_name", is(updatedRequest.metricName))
                .body("metric_description", is(updatedRequest.metricDescription))
                .body("unit_type", is(createdMetricDefinition.unitType))
                .body("metric_type", is(createdMetricDefinition.metricType));

    }

    @Test
    public void update_metric_definition_no_unit_type() {

        var updatedRequest = new MetricDefinitionDtoRequest();

        Mockito.when(readPredefinedTypesService.searchForUnitType(any())).thenReturn(Optional.empty());

        updatedRequest.metricName = "updated_name";
        updatedRequest.metricDescription = "updated_description";
        updatedRequest.unitType = "SECOND";

        var response = given()
                .body(updatedRequest)
                .contentType(ContentType.JSON)
                .patch("/{id}", "any-id")
                .then()
                .assertThat()
                .statusCode(404)
                .extract()
                .as(InformativeResponse.class);

        assertEquals("There is no unit type : SECOND", response.message);
    }

    @Test
    public void update_metric_definition_no_metric_type() {

        var updatedRequest = new MetricDefinitionDtoRequest();

        Mockito.when(readPredefinedTypesService.searchForUnitType(any())).thenReturn(Optional.of("SECOND"));
        Mockito.when(readPredefinedTypesService.searchForMetricType(any())).thenReturn(Optional.empty());

        updatedRequest.metricName = "updated_name";
        updatedRequest.metricDescription = "updated_description";
        updatedRequest.unitType = "SECOND";
        updatedRequest.metricType = "aggregated";


        var response = given()
                .body(updatedRequest)
                .contentType(ContentType.JSON)
                .patch("/{id}", "any-id")
                .then()
                .assertThat()
                .statusCode(404)
                .extract()
                .as(InformativeResponse.class);

        assertEquals("There is no metric type : aggregated", response.message);
    }

    @Test
    public void update_metric_definition_empty() {

        Mockito.when(readPredefinedTypesService.searchForUnitType(any())).thenReturn(Optional.of("SECOND"));
        Mockito.when(readPredefinedTypesService.searchForMetricType(any())).thenReturn(Optional.of("Aggregated"));
        var request= new UpdateMetricDefinitionDtoRequest();

        request.metricName = "metric";
        request.metricDescription = "description";
        request.unitType = "SECOND";
        request.metricType = "Aggregated";

        var createdMetricDefinition = given()
                .body(request)
                .contentType(ContentType.JSON)
                .post()
                .then()
                .extract()
                .as(MetricDefinitionDtoResponse.class);

        var response = given()
                .contentType(ContentType.JSON)
                .patch("/{id}", createdMetricDefinition.id)
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

        var dto= new UpdateMetricDefinitionDtoRequest();

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

        assertEquals("The Metric Definition has not been found.", response.message);
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
        var request= new MetricDefinitionDtoRequest();

        request.metricName = "metric";
        request.metricDescription = "description";
        request.unitType = "SECOND";
        request.metricType = "Aggregated";

        var createdMetricDefinition = given()
                .body(request)
                .contentType(ContentType.JSON)
                .post()
                .then()
                .extract()
                .as(MetricDefinitionDtoResponse.class);

        var storedMetricDefinition = given()
                .get("/{id}", createdMetricDefinition.id)
                .then()
                .assertThat()
                .statusCode(200)
                .extract()
                .as(MetricDefinitionDtoResponse.class);

        assertEquals(createdMetricDefinition.id, storedMetricDefinition.id);
    }

    @Test
    public void fetch_all_metric_definitions() {

        Mockito.when(readPredefinedTypesService.searchForUnitType(any())).thenReturn(Optional.of("SECOND"));
        Mockito.when(readPredefinedTypesService.searchForMetricType(any())).thenReturn(Optional.of("Aggregated"));
        var request= new MetricDefinitionDtoRequest();

        request.metricName = "metric";
        request.metricDescription = "description";
        request.unitType = "SECOND";
        request.metricType = "Aggregated";

        given()
                .body(request)
                .contentType(ContentType.JSON)
                .post()
                .then()
                .assertThat()
                .statusCode(201)
                .extract()
                .as(MetricDefinitionDtoResponse.class);


        var request1= new MetricDefinitionDtoRequest();

        request1.metricName = "metric1";
        request1.metricDescription = "description";
        request1.unitType = "kg";
        request1.metricType = "Aggregated";

        given()
                .body(request1)
                .contentType(ContentType.JSON)
                .post()
                .then()
                .assertThat()
                .statusCode(201)
                .extract()
                .as(MetricDefinitionDtoResponse.class);

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
        var request= new MetricDefinitionDtoRequest();

        request.metricName = "metric";
        request.metricDescription = "description";
        request.unitType = "SECOND";
        request.metricType = "Aggregated";

        var response = given()
                .body(request)
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
        requestForMetric.metricDefinitionId = response.id;

        given()
                .basePath("accounting-system/metrics")
                .body(requestForMetric)
                .contentType(ContentType.JSON)
                .post()
                .then()
                .assertThat()
                .statusCode(201);

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
        var request= new MetricDefinitionDtoRequest();

        request.metricName = "metric";
        request.metricDescription = "description";
        request.unitType = "SECOND";
        request.metricType = "Aggregated";

       var response = given()
                .body(request)
                .contentType(ContentType.JSON)
                .post()
                .then()
                .assertThat()
                .statusCode(201)
                .extract()
                .as(MetricDefinitionDtoResponse.class);


        given()
                .delete("/{metric_definiton_id}", response.id)
                .then()
                .assertThat()
                .statusCode(200)
                .extract()
                .as(InformativeResponse.class);
    }

}