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
import org.accounting.system.dtos.unittype.UnitTypeDto;
import org.accounting.system.dtos.unittype.UpdateUnitTypeRequestDto;
import org.accounting.system.endpoints.UnitTypeEndpoint;
import org.accounting.system.repositories.client.ClientAccessAlwaysRepository;
import org.accounting.system.repositories.metricdefinition.MetricDefinitionRepository;
import org.accounting.system.services.ReadPredefinedTypesService;
import org.accounting.system.services.UnitTypeService;
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
@TestHTTPEndpoint(UnitTypeEndpoint.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UnitTypeEndpointTest {

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
    UnitTypeService unitTypeService;

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
    public void createUnitTypeNotAuthenticated() {

        var notAuthenticatedResponse = given()
                .auth()
                .oauth2("invalidToken")
                .contentType(ContentType.JSON)
                .post()
                .thenReturn();

        assertEquals(401, notAuthenticatedResponse.statusCode());
    }

    @Test
    public void createUnitTypeRequestBodyIsEmpty() {

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
    public void createUnitTypeCannotConsumeContentType() {

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
    public void createUnitTypeIsEmpty() {

        var request= new UnitTypeDto();

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

        assertEquals("unit_type may not be empty.", response.message);
    }

    @Test
    public void createUnitDescriptionIsEmpty() {

        var request= new UnitTypeDto();

        request.unit = "unit";

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
    public void createUnitTypeSimilar() {

        var request= new UnitTypeDto();

        request.unit = "unit_similar";
        request.description = "description";

        var unitType = createUnitType(request);

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

        assertEquals("This Unit Type already exists in the Accounting Service database. Its id is "+unitType.id, response.message);
    }

    @Test
    public void createUnitTypeCaseInsensitive() {

        var request= new UnitTypeDto();

        request.unit = "unit_case_insensitive";
        request.description = "description";

        var unitType = createUnitType(request);

        request.unit = "Unit_Case_Insensitive";

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

        assertEquals("This Unit Type already exists in the Accounting Service database. Its id is "+unitType.id, response.message);
    }

    @Test
    public void createUnitType() {

        var request= new UnitTypeDto();

        request.unit = "unit_new";
        request.description = "description";

        var unitType = createUnitType(request);

        assertEquals(request.unit, unitType.unit);
    }

    @Test
    public void deleteUnitTypeAuthenticated() {


        var notAuthenticatedResponse = given()
                .auth()
                .oauth2("invalidToken")
                .delete("/{unitType}", "7dyebdheb7377e")
                .thenReturn();

        assertEquals(401, notAuthenticatedResponse.statusCode());
    }

    @Test
    public void deleteUnitTypeNotFound() {


        var response = given()
                .auth()
                .oauth2(getAccessToken("admin"))
                .delete("/{unitTypeId}", "7dyebdheb7377e")
                .then()
                .assertThat()
                .statusCode(404)
                .extract()
                .as(InformativeResponse.class);

        assertEquals("There is no Unit Type with the following id: 7dyebdheb7377e", response.message);
    }

    @Test
    public void deleteUnitTypeIsNotAllowed(){

        var newUnitType= new UnitTypeDto();

        newUnitType.unit = "unit_cannot_deleted";
        newUnitType.description = "description";

        var unitType = createUnitType(newUnitType);

        Mockito.when(readPredefinedTypesService.searchForUnitType(any())).thenReturn(Optional.of("SECOND"));
        Mockito.when(readPredefinedTypesService.searchForMetricType(any())).thenReturn(Optional.of("Aggregated"));
        MetricDefinitionRequestDto request= new MetricDefinitionRequestDto();

        request.metricName = "metric";
        request.metricDescription = "description";
        request.unitType = unitType.unit;
        request.metricType = "Aggregated";

        createMetricDefinition(request, "admin");

        var deleteResponse = given()
                .auth()
                .oauth2(getAccessToken("admin"))
                .delete("/{unitType}", unitType.id)
                .then()
                .assertThat()
                .statusCode(403)
                .extract()
                .as(InformativeResponse.class);

        assertEquals("This Unit Type is used in an existing Metric Definition, so you cannot delete it.", deleteResponse.message);
    }

    @Test
    public void deleteUnitTypeRegisteredByAccountingServiceIsNotAllowed(){

        var unitType = unitTypeService.getUnitByType("TB/year");

        var deleteResponse = given()
                .auth()
                .oauth2(getAccessToken("admin"))
                .delete("/{unitType}", unitType.get().getId().toString())
                .then()
                .assertThat()
                .statusCode(403)
                .extract()
                .as(InformativeResponse.class);

        assertEquals("You cannot delete a Unit Type registered by Accounting Service.", deleteResponse.message);
    }

    @Test
    public void deleteUnitType() {

        var newUnitType= new UnitTypeDto();

        newUnitType.unit = "unit_to_be_deleted";
        newUnitType.description = "description";

        var unitType = createUnitType(newUnitType);

        var deleteResponse = given()
                .auth()
                .oauth2(getAccessToken("admin"))
                .delete("/{metricDefinitionId}", unitType.id)
                .then()
                .assertThat()
                .statusCode(200)
                .extract()
                .as(InformativeResponse.class);

        assertEquals("Unit Type has been deleted successfully.", deleteResponse.message);
    }

    @Test
    public void fetchUnitTypeNotAuthenticated() {

        var notAuthenticatedResponse = given()
                .auth()
                .oauth2("invalidToken")
                .get("/{id}", "507f1f77bcf86cd799439011")
                .thenReturn();

        assertEquals(401, notAuthenticatedResponse.statusCode());
    }

    @Test
    public void fetchUnitTypeNotFound() {

        var response = given()
                .auth()
                .oauth2(getAccessToken("admin"))
                .get("/{id}", "507f1f77bcf86cd799439011")
                .then()
                .assertThat()
                .statusCode(404)
                .extract()
                .as(InformativeResponse.class);

        assertEquals("There is no Unit Type with the following id: 507f1f77bcf86cd799439011", response.message);
    }

    @Test
    public void fetchMetricDefinition() {

        var newUnitType= new UnitTypeDto();

        newUnitType.unit = "unit_to_be_retrieved";
        newUnitType.description = "description";

        var unitType = createUnitType(newUnitType);

        var storedUnitType = given()
                .auth()
                .oauth2(getAccessToken("admin"))
                .get("/{id}", unitType.id)
                .then()
                .assertThat()
                .statusCode(200)
                .extract()
                .as(UnitTypeDto.class);

        assertEquals(unitType.unit, storedUnitType.unit);
    }

    @Test
    public void updateUnitTypeIsNotAllowed(){

        var newUnitType= new UnitTypeDto();

        newUnitType.unit = "unit_cannot_updated";
        newUnitType.description = "description";

        var unitType = createUnitType(newUnitType);

        Mockito.when(readPredefinedTypesService.searchForUnitType(any())).thenReturn(Optional.of("SECOND"));
        Mockito.when(readPredefinedTypesService.searchForMetricType(any())).thenReturn(Optional.of("Aggregated"));
        MetricDefinitionRequestDto request= new MetricDefinitionRequestDto();

        request.metricName = "metric";
        request.metricDescription = "description";
        request.unitType = unitType.unit;
        request.metricType = "Aggregated";

        createMetricDefinition(request, "admin");

        var updateUnitType= new UpdateUnitTypeRequestDto();

        updateUnitType.unit = "change_unit";
        updateUnitType.description = "change_description";

        var deleteResponse = given()
                .auth()
                .oauth2(getAccessToken("admin"))
                .contentType(ContentType.JSON)
                .body(updateUnitType)
                .patch("/{unitType}", unitType.id)
                .then()
                .assertThat()
                .statusCode(403)
                .extract()
                .as(InformativeResponse.class);

        assertEquals("This Unit Type is used in an existing Metric Definition, so you cannot update it.", deleteResponse.message);
    }

    @Test
    public void updateUnitTypeRegisteredByAccountingServiceIsNotAllowed(){

        var unitType = unitTypeService.getUnitByType("TB/year");

        var updateUnitType= new UpdateUnitTypeRequestDto();

        updateUnitType.unit = "change_unit";
        updateUnitType.description = "change_description";

        var deleteResponse = given()
                .auth()
                .oauth2(getAccessToken("admin"))
                .contentType(ContentType.JSON)
                .body(updateUnitType)
                .patch("/{unitType}",  unitType.get().getId().toString())
                .then()
                .assertThat()
                .statusCode(403)
                .extract()
                .as(InformativeResponse.class);

        assertEquals("You cannot update a Unit Type registered by Accounting Service.", deleteResponse.message);
    }

    @Test
    public void updateUnitTypeCannotConsumeContentType() {

        var newUnitType= new UnitTypeDto();

        newUnitType.unit = "unit_cannot_consume_content_type";
        newUnitType.description = "description";

        var unitType = createUnitType(newUnitType);

        var response = given()
                .auth()
                .oauth2(getAccessToken("admin"))
                .body(newUnitType)
                .patch("/{id}", unitType.id)
                .then()
                .assertThat()
                .statusCode(415)
                .extract()
                .as(InformativeResponse.class);

        assertEquals("Cannot consume content type.", response.message);
    }

    @Test
    public void updateUnitTypeNotAuthenticated() {

        var notAuthenticatedResponse = given()
                .auth()
                .oauth2("invalidToken")
                .contentType(ContentType.JSON)
                .patch("/{id}", "556787878e-rrr")
                .thenReturn();

        assertEquals(401, notAuthenticatedResponse.statusCode());
    }

    @Test
    public void updateUnitTypeNotFound() {

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

        assertEquals("There is no Unit Type with the following id: 556787878e-rrr", response.message);
    }

    @Test
    public void updateUnitTypeFull() {

        var newUnitType= new UnitTypeDto();

        newUnitType.unit = "unit_type_update_full";
        newUnitType.description = "description_update_full";

        var unitType = createUnitType(newUnitType);

        var updateUnitType= new UpdateUnitTypeRequestDto();

        updateUnitType.unit = "change_unit_type_update_full";
        updateUnitType.description = "change_description_update_full";


        var updateResponse = given()
                .auth()
                .oauth2(getAccessToken("admin"))
                .body(updateUnitType)
                .contentType(ContentType.JSON)
                .patch("/{id}", unitType.id)
                .then()
                .assertThat()
                .statusCode(200)
                .body("id", is(unitType.id))
                .body("unit_type", is(updateUnitType.unit))
                .body("description", is(updateUnitType.description))
                .extract()
                .as(UnitTypeDto.class);

        assertEquals(updateUnitType.unit, updateResponse.unit);
    }

    @Test
    public void updateUnitTypePartial() {

        var newUnitType= new UnitTypeDto();

        newUnitType.unit = "unit_type_update_partial";
        newUnitType.description = "description_update_partial";

        var unitType = createUnitType(newUnitType);

        var updateUnitType= new UpdateUnitTypeRequestDto();

        updateUnitType.unit = "change_unit_type_update_partial";

        var updateResponse = given()
                .auth()
                .oauth2(getAccessToken("admin"))
                .body(updateUnitType)
                .contentType(ContentType.JSON)
                .patch("/{id}", unitType.id)
                .then()
                .assertThat()
                .statusCode(200)
                .body("id", is(unitType.id))
                .body("unit_type", is(updateUnitType.unit))
                .body("description", is(unitType.description))
                .extract()
                .as(UnitTypeDto.class);

        assertEquals(updateUnitType.unit, updateResponse.unit);

        var updateDescription= new UpdateUnitTypeRequestDto();

        updateDescription.description = "change_description_update_partial";

        var updateResponse1 = given()
                .auth()
                .oauth2(getAccessToken("admin"))
                .body(updateDescription)
                .contentType(ContentType.JSON)
                .patch("/{id}", unitType.id)
                .then()
                .assertThat()
                .statusCode(200)
                .body("id", is(unitType.id))
                .body("unit_type", is(updateResponse.unit))
                .body("description", is(updateDescription.description))
                .extract()
                .as(UnitTypeDto.class);

        assertEquals(updateDescription.description, updateResponse1.description);

    }

    @Test
    public void updateUnitTypeConflict() {

        var newUnitType= new UnitTypeDto();

        newUnitType.unit = "unit_type_update_conflict";
        newUnitType.description = "description_update_conflict";

        var unitType = createUnitType(newUnitType);

        var updateUnitType= new UpdateUnitTypeRequestDto();

        updateUnitType.unit = "TB/year";

        var informativeResponse = given()
                .auth()
                .oauth2(getAccessToken("admin"))
                .body(updateUnitType)
                .contentType(ContentType.JSON)
                .patch("/{unitType}",  unitType.id)
                .then()
                .assertThat()
                .statusCode(409)
                .extract()
                .as(InformativeResponse.class);

        assertEquals("This Unit Type has already been created.", informativeResponse.message);
    }

    @Test
    public void updateUnitTypeRequestBodyIsEmpty() {

        var newUnitType= new UnitTypeDto();

        newUnitType.unit = "unit_type_update_empty_body";
        newUnitType.description = "description_update_empty_body";

        var unitType = createUnitType(newUnitType);

        var response = given()
                .auth()
                .oauth2(getAccessToken("admin"))
                .contentType(ContentType.JSON)
                .patch("/{id}", unitType.id)
                .then()
                .assertThat()
                .statusCode(400)
                .extract()
                .as(InformativeResponse.class);

        assertEquals("The request body is empty.", response.message);
    }

    private UnitTypeDto createUnitType(UnitTypeDto request){

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
                .as(UnitTypeDto.class);
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