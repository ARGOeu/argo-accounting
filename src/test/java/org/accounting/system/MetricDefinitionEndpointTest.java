package org.accounting.system;

import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import io.quarkus.test.junit.mockito.InjectMock;
import io.quarkus.test.keycloak.client.KeycloakTestClient;
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
import org.accounting.system.repositories.metricdefinition.MetricDefinitionRepository;
import org.accounting.system.services.ReadPredefinedTypesService;
import org.accounting.system.util.Utility;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javax.inject.Inject;
import java.util.List;
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
    Utility utility;

    @InjectMock
    ReadPredefinedTypesService readPredefinedTypesService;

    KeycloakTestClient keycloakClient = new KeycloakTestClient();


    @BeforeEach
    public void setup() {
        metricDefinitionRepository.deleteAll();
    }


    @Test
    public void createMetricDefinitionNotAuthenticated() {

        var notAuthenticatedResponse = given()
                .auth()
                .oauth2("invalidToken")
                .contentType(ContentType.JSON)
                .post()
                .thenReturn();

        assertEquals(401, notAuthenticatedResponse.statusCode());
    }

        @Test
    public void createMetricDefinitionRequestBodyIsEmpty() {

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
    public void createMetricDefinitionCannotConsumeContentType() {

        var response = given()
                .auth()
                .oauth2(getAccessToken("admin"))
                .post()
                .then()
                .assertThat()
                .statusCode(415)
                .extract()
                .as(InformativeResponse.class);

        assertEquals("Cannot consume content type.", response.message);
    }

    @Test
    public void createMetricDefinitionNoUnitType() {

        Mockito.when(readPredefinedTypesService.searchForUnitType(any())).thenReturn(Optional.empty());
        Mockito.when(readPredefinedTypesService.searchForMetricType(any())).thenReturn(Optional.of("Aggregated"));
        MetricDefinitionRequestDto request= new MetricDefinitionRequestDto();

        request.metricName = "metric";
        request.metricDescription = "description";
        request.unitType = "SECOND";
        request.metricType = "Aggregated";

        InformativeResponse response = given()
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

        assertEquals("There is no unit type : SECOND", response.message);
    }

    @Test
    public void createMetricDefinitionNoMetricType() {

        Mockito.when(readPredefinedTypesService.searchForUnitType(any())).thenReturn(Optional.of("SECOND"));
        Mockito.when(readPredefinedTypesService.searchForMetricType(any())).thenReturn(Optional.empty());
        MetricDefinitionRequestDto request= new MetricDefinitionRequestDto();

        request.metricName = "metric";
        request.metricDescription = "description";
        request.unitType = "SECOND";
        request.metricType = "Aggregated";

        InformativeResponse response = given()
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

        assertEquals("There is no metric type : Aggregated", response.message);
    }

    @Test
    public void createMetricDefinitionMetricNameIsEmpty() {

        Mockito.when(readPredefinedTypesService.searchForUnitType(any())).thenReturn(Optional.of("SECOND"));
        Mockito.when(readPredefinedTypesService.searchForMetricType(any())).thenReturn(Optional.of("Aggregated"));

        var request= new MetricDefinitionRequestDto();
        request.metricDescription = "description";
        request.unitType = "SECOND";
        request.metricType = "Aggregated";

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

        assertEquals("metric_name may not be empty.", response.message);
    }

    @Test
    public void createMetricDefinitionUnitTypeIsEmpty() {

        var request= new MetricDefinitionRequestDto();
        request.unitType="";
        request.metricName = "metric";
        request.metricDescription = "description";
        request.metricType = "Aggregated";

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

        assertEquals("unit_type may not be empty.", response.message);
    }

    @Test
    public void createMetricDefinitionMetricTypeIsEmpty() {

        var request= new MetricDefinitionRequestDto();
        request.metricName = "metric";
        request.unitType = "SECOND";
        request.metricDescription = "description";

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

        assertEquals("metric_type may not be empty.", response.message);
    }

    @Test
    public void createMetricDefinitionSimilar() {

        Mockito.when(readPredefinedTypesService.searchForUnitType(any())).thenReturn(Optional.of("SECOND"));
        Mockito.when(readPredefinedTypesService.searchForMetricType(any())).thenReturn(Optional.of("Aggregated"));
        MetricDefinitionRequestDto request= new MetricDefinitionRequestDto();

        request.metricName = "metric";
        request.metricDescription = "description";
        request.unitType = "SECOND";
        request.metricType = "Aggregated";

        var metricDefinition = createMetricDefinition(request);

        var response = given()
                .auth()
                .oauth2(getAccessToken("admin"))
                .body(request)
                .contentType(ContentType.JSON)
                .post()
                .then()
                .assertThat()
                .statusCode(409)
                .extract()
                .as(InformativeResponse.class);

        assertEquals("There is a Metric Definition with unit type SECOND and name metric. Its id is "+metricDefinition.id, response.message);
    }

    @Test
    public void createMetricDefinition() {

        Mockito.when(readPredefinedTypesService.searchForUnitType(any())).thenReturn(Optional.of("SECOND"));
        Mockito.when(readPredefinedTypesService.searchForMetricType(any())).thenReturn(Optional.of("Aggregated"));
        MetricDefinitionRequestDto request= new MetricDefinitionRequestDto();

        request.metricName = "metric";
        request.metricDescription = "description";
        request.unitType = "SECOND";
        request.metricType = "Aggregated";

        var metricDefinition = createMetricDefinition(request);
        assertEquals(request.unitType, metricDefinition.unitType);
    }

    @Test
    public void updateMetricDefinitionCannotConsumeContentType() {

        Mockito.when(readPredefinedTypesService.searchForUnitType(any())).thenReturn(Optional.of("SECOND"));
        Mockito.when(readPredefinedTypesService.searchForMetricType(any())).thenReturn(Optional.of("Aggregated"));
        MetricDefinitionRequestDto request= new MetricDefinitionRequestDto();

        request.metricName = "metric";
        request.metricDescription = "description";
        request.unitType = "SECOND";
        request.metricType = "Aggregated";

        var metricDefinition = createMetricDefinition(request);

        var response = given()
                .auth()
                .oauth2(getAccessToken("admin"))
                .patch("/{id}", metricDefinition.id)
                .then()
                .assertThat()
                .statusCode(415)
                .extract()
                .as(InformativeResponse.class);

        assertEquals("Cannot consume content type.", response.message);
    }

    @Test
    public void updateMetricDefinitionNotAuthenticated() {

        var notAuthenticatedResponse = given()
                .auth()
                .oauth2("invalidToken")
                .contentType(ContentType.JSON)
                .patch("/{id}", "556787878e-rrr")
                .thenReturn();

        assertEquals(401, notAuthenticatedResponse.statusCode());
    }

    @Test
    public void updateMetricDefinitionNotFound() {

        var response = given()
                .auth()
                .oauth2(getAccessToken("admin"))
                .contentType(ContentType.JSON)
                .patch("/{id}", "556787878e-rrr")
                .then()
                .assertThat()
                .statusCode(404)
                .extract()
                .as(InformativeResponse.class);

        assertEquals("There is no Metric Definition with the following id: 556787878e-rrr", response.message);
    }

    @Test
    public void updateMetricDefinitionFull() {

        Mockito.when(readPredefinedTypesService.searchForUnitType(any())).thenReturn(Optional.of("SECOND"));
        Mockito.when(readPredefinedTypesService.searchForMetricType(any())).thenReturn(Optional.of("Aggregated"));
        var request= new MetricDefinitionRequestDto();

        request.metricName = "metric";
        request.metricDescription = "description";
        request.unitType = "SECOND";
        request.metricType = "Aggregated";

        var metricDefinition = createMetricDefinition(request);

        var metricDefinitiontoBeUpdated = new UpdateMetricDefinitionRequestDto();

        metricDefinitiontoBeUpdated.metricName = "update";
        metricDefinitiontoBeUpdated.metricDescription = "updatedDescription";
        metricDefinitiontoBeUpdated.unitType = "updatedUnitType";
        metricDefinitiontoBeUpdated.metricType = "updatedMetricType";


        var updateResponse = given()
                .auth()
                .oauth2(getAccessToken("admin"))
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
                .body("metric_type", is(metricDefinitiontoBeUpdated.metricType))
                .extract()
                .as(MetricDefinitionResponseDto.class);

        assertEquals(metricDefinitiontoBeUpdated.unitType, updateResponse.unitType);
    }

    @Test
    public void updateMetricDefinitionPartial() {

        Mockito.when(readPredefinedTypesService.searchForUnitType(any())).thenReturn(Optional.of("SECOND"));
        Mockito.when(readPredefinedTypesService.searchForMetricType(any())).thenReturn(Optional.of("Aggregated"));
        var request= new MetricDefinitionRequestDto();

        request.metricName = "metric";
        request.metricDescription = "description";
        request.unitType = "SECOND";
        request.metricType = "Aggregated";

        var metricDefinition = createMetricDefinition(request);

        var metricDefinitionToBeUpdated = new MetricDefinitionRequestDto();

        metricDefinitionToBeUpdated.metricName = "update";
        metricDefinitionToBeUpdated.metricDescription = "updatedDescription";

        Mockito.when(readPredefinedTypesService.searchForUnitType(any())).thenReturn(Optional.empty());
        Mockito.when(readPredefinedTypesService.searchForMetricType(any())).thenReturn(Optional.empty());

        var updateResponse = given()
                .auth()
                .oauth2(getAccessToken("admin"))
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
                .body("metric_type", is(metricDefinition.metricType))
                .extract()
                .as(MetricDefinitionResponseDto.class);

        assertEquals(metricDefinitionToBeUpdated.metricDescription, updateResponse.metricDescription);
    }

    @Test
    public void updateMetricDefinitionNoUnitType() {

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

        metricDefinitionToBeUpdated.metricName = "update";
        metricDefinitionToBeUpdated.metricDescription = "updatedDescription";
        metricDefinitionToBeUpdated.unitType = "SECOND";

        var response = given()
                .auth()
                .oauth2(getAccessToken("admin"))
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
    public void updateMetricDefinitionNoMetricType() {

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

        metricDefinitionToBeUpdated.metricName = "update";
        metricDefinitionToBeUpdated.metricDescription = "updatedDescription";
        metricDefinitionToBeUpdated.unitType = "SECOND";
        metricDefinitionToBeUpdated.metricType = "aggregated";


        var response = given()
                .auth()
                .oauth2(getAccessToken("admin"))
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
    public void updateMetricDefinitionRequestBodyIsEmpty() {

        Mockito.when(readPredefinedTypesService.searchForUnitType(any())).thenReturn(Optional.of("SECOND"));
        Mockito.when(readPredefinedTypesService.searchForMetricType(any())).thenReturn(Optional.of("Aggregated"));
        var request= new MetricDefinitionRequestDto();

        request.metricName = "metric";
        request.metricDescription = "description";
        request.unitType = "SECOND";
        request.metricType = "Aggregated";

        var metricDefinition = createMetricDefinition(request);

        var response = given()
                .auth()
                .oauth2(getAccessToken("admin"))
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
    public void updateMetricDefinitionWithMapper() {

        var metricDefinition = new MetricDefinition();

        metricDefinition.setId(new ObjectId("507f1f77bcf86cd799439011"));
        metricDefinition.setMetricName("name");
        metricDefinition.setMetricDescription("description");
        metricDefinition.setUnitType("unit");
        metricDefinition.setMetricType("metricType");

        var dto= new UpdateMetricDefinitionRequestDto();

        dto.metricName = "update";
        dto.metricDescription = "updatedDescription";

        MetricDefinitionMapper.INSTANCE.updateMetricDefinitionFromDto(dto, metricDefinition);

        assertEquals(dto.metricName, metricDefinition.getMetricName());
        assertEquals(dto.metricDescription, metricDefinition.getMetricDescription());
        assertEquals("unit", metricDefinition.getUnitType());
        assertEquals("metricType", metricDefinition.getMetricType());
        assertEquals(new ObjectId("507f1f77bcf86cd799439011"), metricDefinition.getId());
    }

    @Test
    public void fetchMetricDefinitionNotAuthenticated() {

        var notAuthenticatedResponse = given()
                .auth()
                .oauth2("invalidToken")
                .get("/{id}", "507f1f77bcf86cd799439011")
                .thenReturn();

        assertEquals(401, notAuthenticatedResponse.statusCode());
    }

    @Test
    public void fetchMetricDefinitionNotFound() {

        var response = given()
                .auth()
                .oauth2(getAccessToken("admin"))
                .get("/{id}", "507f1f77bcf86cd799439011")
                .then()
                .assertThat()
                .statusCode(404)
                .extract()
                .as(InformativeResponse.class);

        assertEquals("There is no Metric Definition with the following id: 507f1f77bcf86cd799439011", response.message);
    }

    @Test
    public void fetchMetricDefinitionNotFoundNonHexId() {

        var response = given()
                .auth()
                .oauth2(getAccessToken("admin"))
                .get("/{id}", "iiejijirj33i3i")
                .then()
                .assertThat()
                .statusCode(404)
                .extract()
                .as(InformativeResponse.class);

        assertEquals(404, response.code);
    }

    @Test
    public void fetchMetricDefinition() {

        Mockito.when(readPredefinedTypesService.searchForUnitType(any())).thenReturn(Optional.of("SECOND"));
        Mockito.when(readPredefinedTypesService.searchForMetricType(any())).thenReturn(Optional.of("Aggregated"));
        var request= new MetricDefinitionRequestDto();

        request.metricName = "metric";
        request.metricDescription = "description";
        request.unitType = "SECOND";
        request.metricType = "Aggregated";

        var metricDefinition = createMetricDefinition(request);

        var storedMetricDefinition = given()
                .auth()
                .oauth2(getAccessToken("admin"))
                .get("/{id}", metricDefinition.id)
                .then()
                .assertThat()
                .statusCode(200)
                .extract()
                .as(MetricDefinitionResponseDto.class);

        assertEquals(metricDefinition.id, storedMetricDefinition.id);
    }

    @Test
    public void fetchMetricDefinitionPaginationMetric() {

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

        var paginateResponse = given()
                .auth()
                .oauth2(getAccessToken("admin"))
                .queryParam("size", 1)
                .get("/{metricDefinitionId}/metrics", metricDefinition.id)
                .thenReturn();

        assertEquals(200, paginateResponse.statusCode());
    }

    @Test
    public void fetchMetricDefinitionPaginationNotAcceptableSize() {

        Mockito.when(readPredefinedTypesService.searchForUnitType(any())).thenReturn(Optional.of("SECOND"));
        Mockito.when(readPredefinedTypesService.searchForMetricType(any())).thenReturn(Optional.of("Aggregated"));
        var request= new MetricDefinitionRequestDto();

        request.metricName = "metric";
        request.metricDescription = "description";
        request.unitType = "SECOND";
        request.metricType = "Aggregated";

        var metricDefinition = createMetricDefinition(request);

        var response = given()
                .auth()
                .oauth2(getAccessToken("admin"))
                .queryParam("size", 110)
                .get("/{metricDefinitionId}/metrics", metricDefinition.id)
                .then()
                .assertThat()
                .statusCode(422)
                .extract()
                .as(InformativeResponse.class);

        assertEquals("You cannot request more than 100 items.",response.message);
    }

    @Test
    public void fetchMetricDefinitionPaginationNotAcceptablePageIndex() {

        Mockito.when(readPredefinedTypesService.searchForUnitType(any())).thenReturn(Optional.of("SECOND"));
        Mockito.when(readPredefinedTypesService.searchForMetricType(any())).thenReturn(Optional.of("Aggregated"));
        var request= new MetricDefinitionRequestDto();

        request.metricName = "metric";
        request.metricDescription = "description";
        request.unitType = "SECOND";
        request.metricType = "Aggregated";

        var metricDefinition = createMetricDefinition(request);

        var response = given()
                .auth()
                .oauth2(getAccessToken("admin"))
                .queryParam("page", 0)
                .get("/{metricDefinitionId}/metrics", metricDefinition.id)
                .then()
                .assertThat()
                .statusCode(400)
                .extract()
                .as(InformativeResponse.class);

        assertEquals("Page index must be >= 1.",response.message);
    }

    @Test
    public void fetchMetricDefinitionPaginationNoMetricDefinition() {

        var response = given()
                .auth()
                .oauth2(getAccessToken("admin"))
                .queryParam("page", 0)
                .get("/{metricDefinitionId}/metrics", "507f1f77bcf86cd799439011")
                .then()
                .assertThat()
                .statusCode(404)
                .extract()
                .as(InformativeResponse.class);

        assertEquals("There is no Metric Definition with the following id: 507f1f77bcf86cd799439011", response.message);
    }

    @Test
    public void fetchMetricDefinitionPaginationNonHexId() {

        var response = given()
                .auth()
                .oauth2(getAccessToken("admin"))
                .queryParam("page", 0)
                .get("/{metricDefinitionId}/metrics", "ijidij3d333")
                .then()
                .assertThat()
                .statusCode(404)
                .extract()
                .as(InformativeResponse.class);

        assertEquals("There is no Metric Definition with the following id: ijidij3d333", response.message);
    }

    @Test
    public void fetchAllMetricDefinitions() {

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

        var fetchResponse = given()
                .auth()
                .oauth2(getAccessToken("admin"))
                .get()
                .thenReturn();

        assertEquals(200, fetchResponse.statusCode());
        assertEquals(2, fetchResponse.body().as(List.class).size());
    }

    @Test
    public void deleteMetricDefinitionNotAuthenticated() {


        var notAuthenticatedResponse = given()
                .auth()
                .oauth2("invalidToken")
                .delete("/{metricDefinitionId}", "7dyebdheb7377e")
                .thenReturn();

        assertEquals(401, notAuthenticatedResponse.statusCode());
    }

    @Test
    public void deleteMetricDefinitionNotFound() {


        var response = given()
                .auth()
                .oauth2(getAccessToken("admin"))
                .delete("/{metricDefinitionId}", "7dyebdheb7377e")
                .then()
                .assertThat()
                .statusCode(404)
                .extract()
                .as(InformativeResponse.class);

        assertEquals("There is no Metric Definition with the following id: 7dyebdheb7377e", response.message);
    }

    @Test
    public void deleteMetricDefinitionProhibited() {

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
                .auth()
                .oauth2(getAccessToken("admin"))
                .delete("/{metricDefinitionId}", response.id)
                .then()
                .assertThat()
                .statusCode(409)
                .extract()
                .as(InformativeResponse.class);

        assertEquals("The Metric Definition cannot be deleted. There is a Metric assigned to it.", errorResponse.message);
    }

    @Test
    public void deleteMetricDefinition() {

        Mockito.when(readPredefinedTypesService.searchForUnitType(any())).thenReturn(Optional.of("SECOND"));
        Mockito.when(readPredefinedTypesService.searchForMetricType(any())).thenReturn(Optional.of("Aggregated"));
        var request= new MetricDefinitionRequestDto();

        request.metricName = "metric";
        request.metricDescription = "description";
        request.unitType = "SECOND";
        request.metricType = "Aggregated";

       var response = createMetricDefinition(request);

        var deleteResponse = given()
                .auth()
                .oauth2(getAccessToken("admin"))
                .delete("/{metricDefinitionId}", response.id)
                .then()
                .assertThat()
                .statusCode(200)
                .extract()
                .as(InformativeResponse.class);

        assertEquals("Metric Definition has been deleted successfully.", deleteResponse.message);
    }

    private MetricDefinitionResponseDto createMetricDefinition(MetricDefinitionRequestDto request){

        return given()
                .auth()
                .oauth2(getAccessToken("admin"))
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
                .auth()
                .oauth2(getAccessToken("admin"))
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

    protected String getAccessToken(String userName) {
        return keycloakClient.getAccessToken(userName);
    }
}