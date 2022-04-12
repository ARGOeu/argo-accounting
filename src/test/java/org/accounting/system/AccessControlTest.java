package org.accounting.system;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import io.quarkus.test.junit.mockito.InjectMock;
import io.quarkus.test.keycloak.client.KeycloakTestClient;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.accounting.system.dtos.InformativeResponse;
import org.accounting.system.dtos.MetricDefinitionRequestDto;
import org.accounting.system.dtos.MetricDefinitionResponseDto;
import org.accounting.system.dtos.acl.AccessControlRequestDto;
import org.accounting.system.dtos.acl.AccessControlUpdateDto;
import org.accounting.system.enums.acl.AccessControlPermission;
import org.accounting.system.repositories.metricdefinition.MetricDefinitionRepository;
import org.accounting.system.services.ReadPredefinedTypesService;
import org.accounting.system.util.Utility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javax.inject.Inject;
import java.util.HashSet;
import java.util.Optional;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;

@QuarkusTest
@TestProfile(AccountingSystemTestProfile.class)
public class AccessControlTest {

    @InjectMock
    ReadPredefinedTypesService readPredefinedTypesService;

    @Inject
    MetricDefinitionRepository metricDefinitionRepository;


    KeycloakTestClient keycloakClient = new KeycloakTestClient();

    protected String getAccessToken(String userName) {
        return keycloakClient.getAccessToken(userName);
    }

    @BeforeEach
    public void setup() {
        metricDefinitionRepository.deleteAll();
    }

    @Test
    public void createAccessControlNotAuthenticated() {

        var notAuthenticatedResponse = given()
                .auth()
                .oauth2("invalidToken")
                .contentType(ContentType.JSON)
                .post("accounting-system/metric-definition/{metricDefinitionId}/acl", "507f1f77bcf86cd799439011")
                .thenReturn();

        assertEquals(401, notAuthenticatedResponse.statusCode());
    }

    @Test
    public void createAccessControlInspectorForbidden() {

        var notAuthenticatedResponse = given()
                .auth()
                .oauth2(getAccessToken("inspector"))
                .contentType(ContentType.JSON)
                .post("/accounting-system/metric-definition/{metricDefinitionId}/acl", "507f1f77bcf86cd799439011")
                .thenReturn();

        assertEquals(403, notAuthenticatedResponse.statusCode());
    }

    @Test
    public void createAccessControlRequestBodyIsEmpty() {

        var errorResponse = given()
                .auth()
                .oauth2(getAccessToken("admin"))
                .contentType(ContentType.JSON)
                .post("/accounting-system/metric-definition/{metricDefinitionId}/acl", "507f1f77bcf86cd799439011")
                .then()
                .assertThat()
                .statusCode(400)
                .extract()
                .as(InformativeResponse.class);

        assertEquals("The request body is empty.", errorResponse.message);
    }

    @Test
    public void createAccessControlCannotConsumeContentType() {

        var errorResponse = given()
                .auth()
                .oauth2(getAccessToken("admin"))
                .post("/accounting-system/metric-definition/{metricDefinitionId}/acl", "507f1f77bcf86cd799439011")
                .then()
                .assertThat()
                .statusCode(415)
                .extract()
                .as(InformativeResponse.class);

        assertEquals("Cannot consume content type.", errorResponse.message);
    }

    @Test
    public void createAccessControlWhoIsEmpty() {

        Mockito.when(readPredefinedTypesService.searchForUnitType(any())).thenReturn(Optional.of("SECOND"));
        Mockito.when(readPredefinedTypesService.searchForMetricType(any())).thenReturn(Optional.of("Aggregated"));
        MetricDefinitionRequestDto requestMetricDefinition = new MetricDefinitionRequestDto();

        requestMetricDefinition.metricName = "metric";
        requestMetricDefinition.metricDescription = "description";
        requestMetricDefinition.unitType = "SECOND";
        requestMetricDefinition.metricType = "Aggregated";

        var metricDefinition = createMetricDefinition(requestMetricDefinition);


        var request= new AccessControlRequestDto();

        var permissions = new HashSet<String>();
        permissions.add("READ");
        request.permissions = permissions;

        var response = createAccessControlForMetricDefinition(request, "admin", metricDefinition.id);


        var errorResponse = response
                .then()
                .assertThat()
                .statusCode(400)
                .extract()
                .as(InformativeResponse.class);

        assertEquals("who may not be empty.", errorResponse.message);
    }

    @Test
    public void createAccessControlEntityNotValid() {

        var request= new AccessControlRequestDto();

        request.who = "fbdb4e4a-6e93-4b08-a1e7-0b7bd08520a6";

        var permissions = new HashSet<String>();
        permissions.add("READ");
        request.permissions = permissions;

        var response = createAccessControlForMetricDefinition(request, "admin", "507f1f77bcf86cd799439011");

        var errorResponse = response
                .then()
                .assertThat()
                .statusCode(404)
                .extract()
                .as(InformativeResponse.class);

        assertEquals("There is no Metric Definition with the following id: 507f1f77bcf86cd799439011", errorResponse.message);
    }

    @Test
    public void createAccessControlForMetricDefinition() {

        Mockito.when(readPredefinedTypesService.searchForUnitType(any())).thenReturn(Optional.of("SECOND"));
        Mockito.when(readPredefinedTypesService.searchForMetricType(any())).thenReturn(Optional.of("Aggregated"));
        MetricDefinitionRequestDto requestMetricDefinition = new MetricDefinitionRequestDto();

        requestMetricDefinition.metricName = "metric";
        requestMetricDefinition.metricDescription = "description";
        requestMetricDefinition.unitType = "SECOND";
        requestMetricDefinition.metricType = "Aggregated";

        var metricDefinition = createMetricDefinition(requestMetricDefinition);

        var request= new AccessControlRequestDto();

        request.who = "fbdb4e4a-6e93-4b08-a1e7-0b7bd08520a6";

        var permissions = new HashSet<String>();
        permissions.add("READ");
        request.permissions = permissions;

        var response = createAccessControlForMetricDefinition(request, "admin", metricDefinition.id);

        var informativeResponse = response
                .then()
                .assertThat()
                .statusCode(200)
                .extract()
                .as(InformativeResponse.class);

        assertEquals("Access Control entry has been created successfully", informativeResponse.message);
    }

    @Test
    public void createAccessControlForMetricDefinitionCreatorForbidden() {

        Mockito.when(readPredefinedTypesService.searchForUnitType(any())).thenReturn(Optional.of("SECOND"));
        Mockito.when(readPredefinedTypesService.searchForMetricType(any())).thenReturn(Optional.of("Aggregated"));
        MetricDefinitionRequestDto requestMetricDefinition = new MetricDefinitionRequestDto();

        requestMetricDefinition.metricName = "metric";
        requestMetricDefinition.metricDescription = "description";
        requestMetricDefinition.unitType = "SECOND";
        requestMetricDefinition.metricType = "Aggregated";

        var metricDefinition = createMetricDefinition(requestMetricDefinition);

        var request= new AccessControlRequestDto();

        request.who = "fbdb4e4a-6e93-4b08-a1e7-0b7bd08520a6";

        var permissions = new HashSet<String>();
        permissions.add("READ");
        request.permissions = permissions;

        // creator (role metric_definition_creator) can grant acl permissions only to its entities since it has been assigned to it the tuple (ACL, ENTITY)
        var response = createAccessControlForMetricDefinition(request, "creator", metricDefinition.id);

        var informativeResponse = response
                .then()
                .assertThat()
                .statusCode(403)
                .extract()
                .as(InformativeResponse.class);

        assertEquals("You have no access to this entity : "+metricDefinition.id, informativeResponse.message);
    }

    @Test
    public void updateAccessControlNotAuthenticated() {

        var notAuthenticatedResponse = given()
                .auth()
                .oauth2("invalidToken")
                .contentType(ContentType.JSON)
                .patch("accounting-system/metric-definition/{metricDefinitionId}/acl/{who}", "507f1f77bcf86cd799439011", "fbdb4e4a-6e93-4b08-a1e7-0b7bd08520a6")
                .thenReturn();

        assertEquals(401, notAuthenticatedResponse.statusCode());
    }

    @Test
    public void updateControlInspectorForbidden() {

        var notAuthenticatedResponse = given()
                .auth()
                .oauth2(getAccessToken("inspector"))
                .contentType(ContentType.JSON)
                .patch("/accounting-system/metric-definition/{metricDefinitionId}/acl/{fbdb4e4a-6e93-4b08-a1e7-0b7bd08520a6}", "507f1f77bcf86cd799439011", "fbdb4e4a-6e93-4b08-a1e7-0b7bd08520a6")
                .thenReturn();

        assertEquals(403, notAuthenticatedResponse.statusCode());
    }

    @Test
    public void updateControlRequestBodyIsEmpty() {

        var errorResponse = given()
                .auth()
                .oauth2(getAccessToken("admin"))
                .contentType(ContentType.JSON)
                .patch("/accounting-system/metric-definition/{metricDefinitionId}/acl/{who}", "507f1f77bcf86cd799439011", "fbdb4e4a-6e93-4b08-a1e7-0b7bd08520a6")
                .then()
                .assertThat()
                .statusCode(400)
                .extract()
                .as(InformativeResponse.class);

        assertEquals("The request body is empty.", errorResponse.message);
    }

    @Test
    public void updateAccessControlCannotConsumeContentType() {

        var errorResponse = given()
                .auth()
                .oauth2(getAccessToken("admin"))
                .patch("/accounting-system/metric-definition/{metricDefinitionId}/acl/{who}", "507f1f77bcf86cd799439011", "fbdb4e4a-6e93-4b08-a1e7-0b7bd08520a6")
                .then()
                .assertThat()
                .statusCode(415)
                .extract()
                .as(InformativeResponse.class);

        assertEquals("Cannot consume content type.", errorResponse.message);
    }

    @Test
    public void updateAccessControlEntityNotValid() {

        var request= new AccessControlUpdateDto();

        var permissions = new HashSet<String>();
        permissions.add("READ");
        request.permissions = permissions;

        var response = updateAccessControlForMetricDefinition(request, "admin", "507f1f77bcf86cd799439011", "fbdb4e4a-6e93-4b08-a1e7-0b7bd08520a6");

        var errorResponse = response
                .then()
                .assertThat()
                .statusCode(404)
                .extract()
                .as(InformativeResponse.class);

        assertEquals("There is no Metric Definition with the following id: 507f1f77bcf86cd799439011", errorResponse.message);
    }

    @Test
    public void updateAccessControlPermissionNotValid() {

        Mockito.when(readPredefinedTypesService.searchForUnitType(any())).thenReturn(Optional.of("SECOND"));
        Mockito.when(readPredefinedTypesService.searchForMetricType(any())).thenReturn(Optional.of("Aggregated"));
        MetricDefinitionRequestDto requestMetricDefinition = new MetricDefinitionRequestDto();

        requestMetricDefinition.metricName = "metric";
        requestMetricDefinition.metricDescription = "description";
        requestMetricDefinition.unitType = "SECOND";
        requestMetricDefinition.metricType = "Aggregated";

        var metricDefinition = createMetricDefinition(requestMetricDefinition);

        var request= new AccessControlUpdateDto();

        var permissions = new HashSet<String>();
        permissions.add("TEST");
        request.permissions = permissions;

        var response = updateAccessControlForMetricDefinition(request, "admin", metricDefinition.id, "fbdb4e4a-6e93-4b08-a1e7-0b7bd08520a6");

        var informativeResponse = response
                .then()
                .assertThat()
                .statusCode(400)
                .extract()
                .as(InformativeResponse.class);

        assertEquals("The value TEST is not a valid entry. Valid entry values are: "+ Utility.getNamesSet(AccessControlPermission.class), informativeResponse.message);
    }

    @Test
    public void updateAccessControlNoPermissions() {

        Mockito.when(readPredefinedTypesService.searchForUnitType(any())).thenReturn(Optional.of("SECOND"));
        Mockito.when(readPredefinedTypesService.searchForMetricType(any())).thenReturn(Optional.of("Aggregated"));
        MetricDefinitionRequestDto requestMetricDefinition = new MetricDefinitionRequestDto();

        requestMetricDefinition.metricName = "metric";
        requestMetricDefinition.metricDescription = "description";
        requestMetricDefinition.unitType = "SECOND";
        requestMetricDefinition.metricType = "Aggregated";

        var metricDefinition = createMetricDefinition(requestMetricDefinition);

        var requestForUpdateAccessControl= new AccessControlUpdateDto();

        var updatePermissions = new HashSet<String>();
        updatePermissions.add("UPDATE");
        requestForUpdateAccessControl.permissions = updatePermissions;

        var response = updateAccessControlForMetricDefinition(requestForUpdateAccessControl, "admin", metricDefinition.id, "fbdb4e4a-6e93-4b08-a1e7-0b7bd08520a6");

        var informativeResponse = response
                .then()
                .assertThat()
                .statusCode(404)
                .extract()
                .as(InformativeResponse.class);

        assertEquals("There is no assigned permission for the fbdb4e4a-6e93-4b08-a1e7-0b7bd08520a6 to control access to the "+metricDefinition.id, informativeResponse.message);
    }

    @Test
    public void updateAccessControlForMetricDefinitionCreatorForbidden() {

        Mockito.when(readPredefinedTypesService.searchForUnitType(any())).thenReturn(Optional.of("SECOND"));
        Mockito.when(readPredefinedTypesService.searchForMetricType(any())).thenReturn(Optional.of("Aggregated"));
        MetricDefinitionRequestDto requestMetricDefinition = new MetricDefinitionRequestDto();

        requestMetricDefinition.metricName = "metric";
        requestMetricDefinition.metricDescription = "description";
        requestMetricDefinition.unitType = "SECOND";
        requestMetricDefinition.metricType = "Aggregated";

        var metricDefinition = createMetricDefinition(requestMetricDefinition);

        var request= new AccessControlRequestDto();

        request.who = "fbdb4e4a-6e93-4b08-a1e7-0b7bd08520a6";

        var permissions = new HashSet<String>();
        permissions.add("READ");
        request.permissions = permissions;

        createAccessControlForMetricDefinition(request, "admin", metricDefinition.id);

        var requestForUpdateAccessControl= new AccessControlUpdateDto();
        var updatePermissions = new HashSet<String>();
        updatePermissions.add("UPDATE");
        requestForUpdateAccessControl.permissions = updatePermissions;

        // creator (role metric_definition_creator) can update only to its entities since it has been assigned to it the tuple (ACL, ENTITY)
        var response = updateAccessControlForMetricDefinition(requestForUpdateAccessControl, "creator", metricDefinition.id, "fbdb4e4a-6e93-4b08-a1e7-0b7bd08520a6");

        var informativeResponse = response
                .then()
                .assertThat()
                .statusCode(403)
                .extract()
                .as(InformativeResponse.class);

        assertEquals("You have no access to modify this permission.", informativeResponse.message);
    }

    @Test
    public void updateAccessControl() {

        Mockito.when(readPredefinedTypesService.searchForUnitType(any())).thenReturn(Optional.of("SECOND"));
        Mockito.when(readPredefinedTypesService.searchForMetricType(any())).thenReturn(Optional.of("Aggregated"));
        MetricDefinitionRequestDto requestMetricDefinition = new MetricDefinitionRequestDto();

        requestMetricDefinition.metricName = "metric";
        requestMetricDefinition.metricDescription = "description";
        requestMetricDefinition.unitType = "SECOND";
        requestMetricDefinition.metricType = "Aggregated";

        var metricDefinition = createMetricDefinition(requestMetricDefinition);

        var requestAccessControl= new AccessControlRequestDto();

        requestAccessControl.who = "fbdb4e4a-6e93-4b08-a1e7-0b7bd08520a6";

        var permissions = new HashSet<String>();
        permissions.add("READ");
        requestAccessControl.permissions = permissions;

        createAccessControlForMetricDefinition(requestAccessControl, "admin", metricDefinition.id);

        var requestForUpdateAccessControl= new AccessControlUpdateDto();

        var updatePermissions = new HashSet<String>();
        updatePermissions.add("UPDATE");
        requestForUpdateAccessControl.permissions = updatePermissions;

        var response = updateAccessControlForMetricDefinition(requestForUpdateAccessControl, "admin", metricDefinition.id, "fbdb4e4a-6e93-4b08-a1e7-0b7bd08520a6");

        var informativeResponse = response
                .then()
                .assertThat()
                .statusCode(200)
                .extract()
                .as(InformativeResponse.class);

        assertEquals("Access Control entry has been updated successfully.", informativeResponse.message);
    }

    @Test
    public void deleteAccessControlNotAuthenticated() {

        var notAuthenticatedResponse = given()
                .auth()
                .oauth2("invalidToken")
                .delete("accounting-system/metric-definition/{metricDefinitionId}/acl/{who}", "507f1f77bcf86cd799439011", "fbdb4e4a-6e93-4b08-a1e7-0b7bd08520a6")
                .thenReturn();

        assertEquals(401, notAuthenticatedResponse.statusCode());
    }

    @Test
    public void deleteControlInspectorForbidden() {

        var notAuthenticatedResponse = given()
                .auth()
                .oauth2(getAccessToken("inspector"))
                .delete("/accounting-system/metric-definition/{metricDefinitionId}/acl/{fbdb4e4a-6e93-4b08-a1e7-0b7bd08520a6}", "507f1f77bcf86cd799439011", "fbdb4e4a-6e93-4b08-a1e7-0b7bd08520a6")
                .thenReturn();

        assertEquals(403, notAuthenticatedResponse.statusCode());
    }

    @Test
    public void deleteAccessControlEntityNotValid() {


        var response = deleteAccessControlForMetricDefinition("admin", "507f1f77bcf86cd799439011", "fbdb4e4a-6e93-4b08-a1e7-0b7bd08520a6");

        var errorResponse = response
                .then()
                .assertThat()
                .statusCode(404)
                .extract()
                .as(InformativeResponse.class);

        assertEquals("There is no Metric Definition with the following id: 507f1f77bcf86cd799439011", errorResponse.message);
    }

    @Test
    public void deleteAccessControlNoPermissions() {

        Mockito.when(readPredefinedTypesService.searchForUnitType(any())).thenReturn(Optional.of("SECOND"));
        Mockito.when(readPredefinedTypesService.searchForMetricType(any())).thenReturn(Optional.of("Aggregated"));
        MetricDefinitionRequestDto requestMetricDefinition = new MetricDefinitionRequestDto();

        requestMetricDefinition.metricName = "metric";
        requestMetricDefinition.metricDescription = "description";
        requestMetricDefinition.unitType = "SECOND";
        requestMetricDefinition.metricType = "Aggregated";

        var metricDefinition = createMetricDefinition(requestMetricDefinition);

        var response = deleteAccessControlForMetricDefinition("admin", metricDefinition.id, "fbdb4e4a-6e93-4b08-a1e7-0b7bd08520a6");

        var informativeResponse = response
                .then()
                .assertThat()
                .statusCode(404)
                .extract()
                .as(InformativeResponse.class);

        assertEquals("There is no assigned permission for the fbdb4e4a-6e93-4b08-a1e7-0b7bd08520a6 to control access to the "+metricDefinition.id, informativeResponse.message);
    }

    @Test
    public void deleteAccessControlForMetricDefinitionCreatorForbidden() {

        Mockito.when(readPredefinedTypesService.searchForUnitType(any())).thenReturn(Optional.of("SECOND"));
        Mockito.when(readPredefinedTypesService.searchForMetricType(any())).thenReturn(Optional.of("Aggregated"));
        MetricDefinitionRequestDto requestMetricDefinition = new MetricDefinitionRequestDto();

        requestMetricDefinition.metricName = "metric";
        requestMetricDefinition.metricDescription = "description";
        requestMetricDefinition.unitType = "SECOND";
        requestMetricDefinition.metricType = "Aggregated";

        var metricDefinition = createMetricDefinition(requestMetricDefinition);

        var request= new AccessControlRequestDto();

        request.who = "fbdb4e4a-6e93-4b08-a1e7-0b7bd08520a6";

        var permissions = new HashSet<String>();
        permissions.add("READ");
        request.permissions = permissions;

        createAccessControlForMetricDefinition(request, "admin", metricDefinition.id);

        // creator (role metric_definition_creator) can delete only to its entities since it has been assigned to it the tuple (ACL, ENTITY)
        var response = deleteAccessControlForMetricDefinition("creator", metricDefinition.id, "fbdb4e4a-6e93-4b08-a1e7-0b7bd08520a6");

        var informativeResponse = response
                .then()
                .assertThat()
                .statusCode(403)
                .extract()
                .as(InformativeResponse.class);

        assertEquals("You have no access to delete this permission.", informativeResponse.message);
    }

    @Test
    public void deleteAccessControl() {

        Mockito.when(readPredefinedTypesService.searchForUnitType(any())).thenReturn(Optional.of("SECOND"));
        Mockito.when(readPredefinedTypesService.searchForMetricType(any())).thenReturn(Optional.of("Aggregated"));
        MetricDefinitionRequestDto requestMetricDefinition = new MetricDefinitionRequestDto();

        requestMetricDefinition.metricName = "metric";
        requestMetricDefinition.metricDescription = "description";
        requestMetricDefinition.unitType = "SECOND";
        requestMetricDefinition.metricType = "Aggregated";

        var metricDefinition = createMetricDefinition(requestMetricDefinition);

        var requestAccessControl= new AccessControlRequestDto();

        requestAccessControl.who = "fbdb4e4a-6e93-4b08-a1e7-0b7bd08520a6";

        var permissions = new HashSet<String>();
        permissions.add("READ");
        requestAccessControl.permissions = permissions;

        createAccessControlForMetricDefinition(requestAccessControl, "admin", metricDefinition.id);

        var response = deleteAccessControlForMetricDefinition("admin", metricDefinition.id, "fbdb4e4a-6e93-4b08-a1e7-0b7bd08520a6");

        var informativeResponse = response
                .then()
                .assertThat()
                .statusCode(200)
                .extract()
                .as(InformativeResponse.class);

        assertEquals("Access Control entry has been deleted successfully.", informativeResponse.message);
    }

    private Response createAccessControlForMetricDefinition(AccessControlRequestDto request, String user, String metricDefinitionId){

        return given()
                .auth()
                .oauth2(getAccessToken(user))
                .body(request)
                .contentType(ContentType.JSON)
                .post("/accounting-system/metric-definition/{metricDefinitionId}/acl", metricDefinitionId);
    }

    private Response updateAccessControlForMetricDefinition(AccessControlUpdateDto request, String user, String metricDefinitionId, String who){

        return given()
                .auth()
                .oauth2(getAccessToken(user))
                .body(request)
                .contentType(ContentType.JSON)
                .patch("/accounting-system/metric-definition/{metricDefinitionId}/acl/{who}", metricDefinitionId, who);
    }

    private Response deleteAccessControlForMetricDefinition(String user, String metricDefinitionId, String who){

        return given()
                .auth()
                .oauth2(getAccessToken(user))
                .delete("/accounting-system/metric-definition/{metricDefinitionId}/acl/{who}", metricDefinitionId, who);
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
}