package org.accounting.system;

import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.accounting.system.dtos.InformativeResponse;
import org.accounting.system.dtos.MetricRegistrationDtoRequest;
import org.accounting.system.dtos.MetricRegistrationDtoResponse;
import org.accounting.system.dtos.UpdateMetricRegistrationDtoRequest;
import org.accounting.system.endpoints.MetricRegistrationEndpoint;
import org.accounting.system.entities.MetricRegistration;
import org.accounting.system.mappers.MetricRegistrationMapper;
import org.accounting.system.repositories.MetricRegistrationRepository;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
@TestHTTPEndpoint(MetricRegistrationEndpoint.class)
public class MetricRegistrationEndpointTest {

    @Inject
    MetricRegistrationRepository metricRegistrationRepository;

    @BeforeEach
    public void setup(){
        metricRegistrationRepository.deleteAll();
    }

    @Test
    public void create_metric_registration_bad_request() {

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
    public void create_metric_registration_cannot_consume_content_type() {

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
    public void create_metric_registration_metric_name_is_empty() {

        var request= new MetricRegistrationDtoRequest();
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

        assertEquals("Metric name may not be empty.", response.message);
    }

    @Test
    public void create_metric_registration_unit_type_is_empty() {

        var request= new MetricRegistrationDtoRequest();
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

        assertEquals("Unit type may not be empty.", response.message);
    }

    @Test
    public void create_metric_registration_metric_type_is_empty() {

        var request= new MetricRegistrationDtoRequest();
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

        assertEquals("Metric type may not be empty.", response.message);
    }


    @Test
    public void create_metric_registration() {

        var request= new MetricRegistrationDtoRequest();

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
                .as(MetricRegistrationDtoResponse.class);

    }

    @Test
    public void metric_registration_similar() {

        var request= new MetricRegistrationDtoRequest();

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
                .as(MetricRegistrationDtoResponse.class);


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
    public void update_metric_registration_from_dto_not_found() {

        var response = given()
                .contentType(ContentType.JSON)
                .patch("/{id}", "507f1f77bcf86cd799439011")
                .then()
                .assertThat()
                .statusCode(404)
                .extract()
                .as(InformativeResponse.class);

        assertEquals("The Metric Registration has not been found.", response.message);

    }

    @Test
    public void update_metric_registration_from_dto_cannot_consume_content_type() {

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
    public void update_metric_registration_from_dto_full() {

        var request= new UpdateMetricRegistrationDtoRequest();

        request.metricName = "metric";
        request.metricDescription = "description";
        request.unitType = "SECOND";
        request.metricType = "Aggregated";

        var createdMetricRegistration = given()
                .body(request)
                .contentType(ContentType.JSON)
                .post()
                .then()
                .extract()
                .as(MetricRegistrationDtoResponse.class);


        var updatedRequest = new UpdateMetricRegistrationDtoRequest();

        updatedRequest.metricName = "updated_name";
        updatedRequest.metricDescription = "updated_description";
        updatedRequest.unitType = "updated_unit_type";
        updatedRequest.metricType = "updated_metric_type";


        given()
                .body(updatedRequest)
                .contentType(ContentType.JSON)
                .patch("/{id}", createdMetricRegistration.id)
                .then()
                .assertThat()
                .statusCode(200)
                .body("metric_registration_id", is(createdMetricRegistration.id))
                .body("metric_name", is(updatedRequest.metricName))
                .body("metric_description", is(updatedRequest.metricDescription))
                .body("unit_type", is(updatedRequest.unitType))
                .body("metric_type", is(updatedRequest.metricType));

    }

    @Test
    public void update_metric_registration_from_dto_partial() {

        var request= new UpdateMetricRegistrationDtoRequest();

        request.metricName = "metric";
        request.metricDescription = "description";
        request.unitType = "SECOND";
        request.metricType = "Aggregated";

        var createdMetricRegistration = given()
                .body(request)
                .contentType(ContentType.JSON)
                .post()
                .then()
                .extract()
                .as(MetricRegistrationDtoResponse.class);

        var updatedRequest = new MetricRegistrationDtoRequest();

        updatedRequest.metricName = "updated_name";
        updatedRequest.metricDescription = "updated_description";


        given()
                .body(updatedRequest)
                .contentType(ContentType.JSON)
                .patch("/{id}", createdMetricRegistration.id)
                .then()
                .assertThat()
                .statusCode(200)
                .body("metric_registration_id", is(createdMetricRegistration.id))
                .body("metric_name", is(updatedRequest.metricName))
                .body("metric_description", is(updatedRequest.metricDescription))
                .body("unit_type", is(createdMetricRegistration.unitType))
                .body("metric_type", is(createdMetricRegistration.metricType));

    }

    @Test
    public void update_metric_registration_from_dto_empty() {

        var request= new UpdateMetricRegistrationDtoRequest();

        request.metricName = "metric";
        request.metricDescription = "description";
        request.unitType = "SECOND";
        request.metricType = "Aggregated";

        var createdMetricRegistration = given()
                .body(request)
                .contentType(ContentType.JSON)
                .post()
                .then()
                .extract()
                .as(MetricRegistrationDtoResponse.class);

        given()
                .contentType(ContentType.JSON)
                .patch("/{id}", createdMetricRegistration.id)
                .then()
                .assertThat()
                .statusCode(200)
                .body("metric_registration_id", is(createdMetricRegistration.id))
                .body("metric_name", is(createdMetricRegistration.metricName))
                .body("metric_description", is(createdMetricRegistration.metricDescription))
                .body("unit_type", is(createdMetricRegistration.unitType))
                .body("metric_type", is(createdMetricRegistration.metricType));

    }


    @Test
    public void update_metric_registration_from_dto_with_mapper() {

        var metricRegistration = new MetricRegistration();

        metricRegistration.setId(new ObjectId("507f1f77bcf86cd799439011"));
        metricRegistration.setMetricName("name");
        metricRegistration.setMetricDescription("description");
        metricRegistration.setUnitType("unit");
        metricRegistration.setMetricType("metric_type");

        var dto= new UpdateMetricRegistrationDtoRequest();

        dto.metricName = "updated_name";
        dto.metricDescription = "updated_description";

        MetricRegistrationMapper.INSTANCE.updateMetricRegistrationFromDto(dto, metricRegistration);

        assertEquals(dto.metricName, metricRegistration.getMetricName());
        assertEquals(dto.metricDescription, metricRegistration.getMetricDescription());
        assertEquals("unit", metricRegistration.getUnitType());
        assertEquals("metric_type", metricRegistration.getMetricType());
        assertEquals(new ObjectId("507f1f77bcf86cd799439011"), metricRegistration.getId());

    }

}