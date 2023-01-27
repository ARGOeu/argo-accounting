package org.accounting.system;

import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import io.quarkus.test.junit.mockito.InjectMock;
import io.quarkus.test.keycloak.client.KeycloakTestClient;
import io.restassured.http.ContentType;
import org.accounting.system.dtos.InformativeResponse;
import org.accounting.system.dtos.metricdefinition.MetricDefinitionRequestDto;
import org.accounting.system.dtos.metricdefinition.MetricDefinitionResponseDto;
import org.accounting.system.dtos.metrictype.MetricTypeDto;
import org.accounting.system.dtos.metrictype.UpdateMetricTypeRequestDto;
import org.accounting.system.dtos.unittype.UnitTypeDto;
import org.accounting.system.endpoints.MetricTypeEndpoint;
import org.accounting.system.repositories.client.ClientAccessAlwaysRepository;
import org.accounting.system.repositories.metricdefinition.MetricDefinitionRepository;
import org.accounting.system.services.MetricTypeService;
import org.accounting.system.services.ReadPredefinedTypesService;
import org.accounting.system.services.client.ClientService;
import org.accounting.system.util.Utility;
import org.json.simple.parser.ParseException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mockito;

import javax.inject.Inject;
import java.util.Optional;
import java.util.Set;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;

@QuarkusTest
@TestProfile(AccountingSystemTestProfile.class)
@TestHTTPEndpoint(MetricTypeEndpoint.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class MetricTypeEndpointTest {

    @Inject
    Utility utility;

    @Inject
    ClientService clientService;

    @Inject
    ClientAccessAlwaysRepository clientAccessAlwaysRepository;

    @Inject
    MetricDefinitionRepository metricDefinitionRepository;

    @InjectMock
    ReadPredefinedTypesService readPredefinedTypesService;

    @Inject
    MetricTypeService metricTypeService;

    KeycloakTestClient keycloakClient = new KeycloakTestClient();

    @BeforeAll
    public void setup() throws ParseException {

        clientService.register(utility.getIdFromToken(keycloakClient.getAccessToken("admin").split("\\.")[1]), "admin", "admin@email.com");

        clientAccessAlwaysRepository.assignRolesToRegisteredClient(utility.getIdFromToken(keycloakClient.getAccessToken("admin").split("\\.")[1]), Set.of("collection_owner"));
    }

    @BeforeEach
    public void each(){
        metricDefinitionRepository.deleteAll();
    }

    @Test
    public void createMetricTypeNotAuthenticated() {

        var notAuthenticatedResponse = given()
                .auth()
                .oauth2("invalidToken")
                .contentType(ContentType.JSON)
                .post()
                .thenReturn();

        assertEquals(401, notAuthenticatedResponse.statusCode());
    }

    @Test
    public void createMetricTypeRequestBodyIsEmpty() {

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
    public void createMetricTypeCannotConsumeContentType() {

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
    public void createMetricTypeIsEmpty() {

        var request= new MetricTypeDto();

        request.description = "description";

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
    public void createMetricTypeDescriptionIsEmpty() {

        var request= new MetricTypeDto();

        request.metricType = "metric-typw";

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

        assertEquals("description may not be empty.", response.message);
    }

    @Test
    public void createMetricTypeSimilar() {

        var request= new MetricTypeDto();

        request.metricType = "metric_type_similar";
        request.description = "description";

        var metricType = createMetricType(request);

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

        assertEquals("This Metric Type already exists in the Accounting Service database. Its id is "+metricType.id, response.message);
    }

    @Test
    public void createMetricTypeCaseInsensitive() {

        var request= new MetricTypeDto();

        request.metricType = "metric_type_case_insensitive";
        request.description = "description";

        var metricType = createMetricType(request);

        request.metricType = "Metric_Type_Case_Insensitive";

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

        assertEquals("This Metric Type already exists in the Accounting Service database. Its id is "+metricType.id, response.message);
    }

    @Test
    public void createMetricType() {

        var request= new MetricTypeDto();

        request.metricType = "metric_type_new";
        request.description = "description";

        var metricType = createMetricType(request);

        assertEquals(request.metricType, metricType.metricType);
    }

    @Test
    public void deleteMetricTypeAuthenticated() {

        var notAuthenticatedResponse = given()
                .auth()
                .oauth2("invalidToken")
                .delete("/{metricType}", "7dyebdheb7377e")
                .thenReturn();

        assertEquals(401, notAuthenticatedResponse.statusCode());
    }

    @Test
    public void deleteMetricTypeNotFound() {

        var response = given()
                .auth()
                .oauth2(getAccessToken("admin"))
                .delete("/{metricTypeId}", "7dyebdheb7377e")
                .then()
                .assertThat()
                .statusCode(404)
                .extract()
                .as(InformativeResponse.class);

        assertEquals("There is no Metric Type with the following id: 7dyebdheb7377e", response.message);
    }

    @Test
    public void deleteMetricTypeIsNotAllowed(){

        var newMetricType= new MetricTypeDto();

        newMetricType.metricType = "metric_type_cannot_deleted";
        newMetricType.description = "description";

        var metricType = createMetricType(newMetricType);

        Mockito.when(readPredefinedTypesService.searchForUnitType(any())).thenReturn(Optional.of("SECOND"));
        Mockito.when(readPredefinedTypesService.searchForMetricType(any())).thenReturn(Optional.of("Aggregated"));
        MetricDefinitionRequestDto request= new MetricDefinitionRequestDto();

        request.metricName = "metric";
        request.metricDescription = "description";
        request.unitType = "SECOND";
        request.metricType = metricType.metricType;

        createMetricDefinition(request, "admin");

        var deleteResponse = given()
                .auth()
                .oauth2(getAccessToken("admin"))
                .delete("/{metricType}", metricType.id)
                .then()
                .assertThat()
                .statusCode(403)
                .extract()
                .as(InformativeResponse.class);

        assertEquals("This Metric Type is used in an existing Metric Definition, so you cannot delete it.", deleteResponse.message);
    }

    @Test
    public void deleteMetricTypeRegisteredByAccountingServiceIsNotAllowed(){

        var metricType = metricTypeService.getMetricByType("aggregated");

        var deleteResponse = given()
                .auth()
                .oauth2(getAccessToken("admin"))
                .delete("/{metricType}", metricType.get().getId().toString())
                .then()
                .assertThat()
                .statusCode(403)
                .extract()
                .as(InformativeResponse.class);

        assertEquals("You cannot delete a Metric Type registered by Accounting Service.", deleteResponse.message);
    }

    @Test
    public void deleteMetricType() {

        var newMetricType= new MetricTypeDto();

        newMetricType.metricType = "metric_type_to_be_deleted";
        newMetricType.description = "description";

        var metricType = createMetricType(newMetricType);

        var deleteResponse = given()
                .auth()
                .oauth2(getAccessToken("admin"))
                .delete("/{metricType}", metricType.id)
                .then()
                .assertThat()
                .statusCode(200)
                .extract()
                .as(InformativeResponse.class);

        assertEquals("Metric Type has been deleted successfully.", deleteResponse.message);
    }

    @Test
    public void fetchMetricTypeNotAuthenticated() {

        var notAuthenticatedResponse = given()
                .auth()
                .oauth2("invalidToken")
                .get("/{id}", "507f1f77bcf86cd799439011")
                .thenReturn();

        assertEquals(401, notAuthenticatedResponse.statusCode());
    }

    @Test
    public void fetchMetricTypeNotFound() {

        var response = given()
                .auth()
                .oauth2(getAccessToken("admin"))
                .get("/{id}", "507f1f77bcf86cd799439011")
                .then()
                .assertThat()
                .statusCode(404)
                .extract()
                .as(InformativeResponse.class);

        assertEquals("There is no Metric Type with the following id: 507f1f77bcf86cd799439011", response.message);
    }

    @Test
    public void fetchMetricType() {

        var newMetricType= new MetricTypeDto();

        newMetricType.metricType = "metric_type_to_be_retrieved";
        newMetricType.description = "description";

        var metricType = createMetricType(newMetricType);

        var storedMetricType = given()
                .auth()
                .oauth2(getAccessToken("admin"))
                .get("/{id}", metricType.id)
                .then()
                .assertThat()
                .statusCode(200)
                .extract()
                .as(MetricTypeDto.class);

        assertEquals(metricType.metricType, storedMetricType.metricType);
    }

    @Test
    public void updateMetricTypeIsNotAllowed(){

        var newMetricType= new MetricTypeDto();

        newMetricType.metricType = "metric_type_cannot_updated";
        newMetricType.description = "description";

        var metricType = createMetricType(newMetricType);

        Mockito.when(readPredefinedTypesService.searchForUnitType(any())).thenReturn(Optional.of("SECOND"));
        Mockito.when(readPredefinedTypesService.searchForMetricType(any())).thenReturn(Optional.of("Aggregated"));
        MetricDefinitionRequestDto request= new MetricDefinitionRequestDto();

        request.metricName = "metric";
        request.metricDescription = "description";
        request.unitType = "SECOND";
        request.metricType = metricType.metricType;

        createMetricDefinition(request, "admin");

        var updateMetricType= new UpdateMetricTypeRequestDto();

        updateMetricType.metricType = "change_metric_type";
        updateMetricType.description = "change_description";

        var deleteResponse = given()
                .auth()
                .oauth2(getAccessToken("admin"))
                .contentType(ContentType.JSON)
                .body(updateMetricType)
                .patch("/{metricType}", metricType.id)
                .then()
                .assertThat()
                .statusCode(403)
                .extract()
                .as(InformativeResponse.class);

        assertEquals("This Metric Type is used in an existing Metric Definition, so you cannot update it.", deleteResponse.message);
    }

    @Test
    public void updateMetricTypeRegisteredByAccountingServiceIsNotAllowed(){

        var metricType = metricTypeService.getMetricByType("aggregated");

        var updateMetricType= new UpdateMetricTypeRequestDto();

        updateMetricType.metricType = "change_metric_type";
        updateMetricType.description = "change_description";

        var deleteResponse = given()
                .auth()
                .oauth2(getAccessToken("admin"))
                .contentType(ContentType.JSON)
                .body(updateMetricType)
                .patch("/{metricType}",  metricType.get().getId().toString())
                .then()
                .assertThat()
                .statusCode(403)
                .extract()
                .as(InformativeResponse.class);

        assertEquals("You cannot update a Metric Type registered by Accounting Service.", deleteResponse.message);
    }

    @Test
    public void updateMetricTypeCannotConsumeContentType() {

        var newMetricType= new MetricTypeDto();

        newMetricType.metricType = "metric_type_cannot_consume_content_type";
        newMetricType.description = "description";

        var metricType = createMetricType(newMetricType);

        var response = given()
                .auth()
                .oauth2(getAccessToken("admin"))
                .body(newMetricType)
                .patch("/{id}", metricType.id)
                .then()
                .assertThat()
                .statusCode(415)
                .extract()
                .as(InformativeResponse.class);

        assertEquals("Cannot consume content type.", response.message);
    }

    @Test
    public void updateMetricTypeNotAuthenticated() {

        var notAuthenticatedResponse = given()
                .auth()
                .oauth2("invalidToken")
                .contentType(ContentType.JSON)
                .patch("/{id}", "556787878e-rrr")
                .thenReturn();

        assertEquals(401, notAuthenticatedResponse.statusCode());
    }

    @Test
    public void updateMetricTypeNotFound() {

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

        assertEquals("There is no Metric Type with the following id: 556787878e-rrr", response.message);
    }

    @Test
    public void updateMetricTypeFull() {

        var newMetricType= new MetricTypeDto();

        newMetricType.metricType = "metric_type_update_full";
        newMetricType.description = "description_update_full";

        var metricType = createMetricType(newMetricType);

        var updateMetricType= new UpdateMetricTypeRequestDto();

        updateMetricType.metricType = "change_metric_type_update_full";
        updateMetricType.description = "change_description_update_full";


        var updateResponse = given()
                .auth()
                .oauth2(getAccessToken("admin"))
                .body(updateMetricType)
                .contentType(ContentType.JSON)
                .patch("/{id}", metricType.id)
                .then()
                .assertThat()
                .statusCode(200)
                .body("id", is(metricType.id))
                .body("metric_type", is(updateMetricType.metricType))
                .body("description", is(updateMetricType.description))
                .extract()
                .as(MetricTypeDto.class);

        assertEquals(updateMetricType.metricType, updateResponse.metricType);
    }

    @Test
    public void updateMetricTypePartial() {

        var newMetricType= new MetricTypeDto();

        newMetricType.metricType = "metric_type_update_partial";
        newMetricType.description = "description_update_partial";

        var metricType = createMetricType(newMetricType);

        var updateMetricType = new UpdateMetricTypeRequestDto();

        updateMetricType.metricType = "change_metric_type_update_partial";

        var updateResponse = given()
                .auth()
                .oauth2(getAccessToken("admin"))
                .body(updateMetricType)
                .contentType(ContentType.JSON)
                .patch("/{id}", metricType.id)
                .then()
                .assertThat()
                .statusCode(200)
                .body("id", is(metricType.id))
                .body("metric_type", is(updateMetricType.metricType))
                .body("description", is(metricType.description))
                .extract()
                .as(MetricTypeDto.class);

        assertEquals(updateMetricType.metricType, updateResponse.metricType);

        var updateDescription= new UpdateMetricTypeRequestDto();

        updateDescription.description = "change_description_update_partial";

        var updateResponse1 = given()
                .auth()
                .oauth2(getAccessToken("admin"))
                .body(updateDescription)
                .contentType(ContentType.JSON)
                .patch("/{id}", metricType.id)
                .then()
                .assertThat()
                .statusCode(200)
                .body("id", is(metricType.id))
                .body("metric_type", is(updateResponse.metricType))
                .body("description", is(updateDescription.description))
                .extract()
                .as(UnitTypeDto.class);

        assertEquals(updateDescription.description, updateResponse1.description);
    }

    @Test
    public void updateMetricTypeConflict() {

        var newMetricType= new MetricTypeDto();

        newMetricType.metricType = "metric_type_update_conflict";
        newMetricType.description = "description_update_conflict";

        var metricType = createMetricType(newMetricType);

        var updateMetricType= new UpdateMetricTypeRequestDto();

        var mt = metricTypeService.getMetricByType("aggregated");

        updateMetricType.metricType = "Aggregated";

        var informativeResponse = given()
                .auth()
                .oauth2(getAccessToken("admin"))
                .body(updateMetricType)
                .contentType(ContentType.JSON)
                .patch("/{metricType}",  metricType.id)
                .then()
                .assertThat()
                .statusCode(409)
                .extract()
                .as(InformativeResponse.class);

        assertEquals("This Metric Type already exists in the Accounting Service database. Its id is "+mt.get().getId().toString(), informativeResponse.message);
    }

    @Test
    public void updateMetricTypeRequestBodyIsEmpty() {

        var newMetricType= new MetricTypeDto();

        newMetricType.metricType = "metric_type_update_empty_body";
        newMetricType.description = "description_update_empty_body";

        var metricType = createMetricType(newMetricType);

        var response = given()
                .auth()
                .oauth2(getAccessToken("admin"))
                .contentType(ContentType.JSON)
                .patch("/{id}", metricType.id)
                .then()
                .assertThat()
                .statusCode(400)
                .extract()
                .as(InformativeResponse.class);

        assertEquals("The request body is empty.", response.message);
    }

    private MetricTypeDto createMetricType(MetricTypeDto request){

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
                .as(MetricTypeDto.class);
    }

    private MetricDefinitionResponseDto createMetricDefinition(MetricDefinitionRequestDto request, String user){

        return given()
                .auth()
                .oauth2(getAccessToken(user))
                .basePath("accounting-system/metric-definitions")
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