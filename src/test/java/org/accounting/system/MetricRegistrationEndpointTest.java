package org.accounting.system;

import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.accounting.system.dtos.InformativeResponse;
import org.accounting.system.dtos.MetricRegistrationDtoRequest;
import org.accounting.system.dtos.MetricRegistrationDtoResponse;
import org.accounting.system.endpoints.MetricRegistrationEndpoint;
import org.accounting.system.repositories.MetricRegistrationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

import static io.restassured.RestAssured.given;
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

}