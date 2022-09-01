//package org.accounting.system;
//
//import io.quarkus.test.junit.QuarkusTest;
//import io.quarkus.test.junit.TestProfile;
//import io.quarkus.test.junit.mockito.InjectMock;
//import io.quarkus.test.keycloak.client.KeycloakTestClient;
//import io.restassured.http.ContentType;
//import io.restassured.response.Response;
//import org.accounting.system.dtos.InformativeResponse;
//import org.accounting.system.dtos.acl.permission.PermissionAccessControlRequestDto;
//import org.accounting.system.dtos.acl.permission.PermissionAccessControlResponseDto;
//import org.accounting.system.dtos.acl.permission.PermissionAccessControlUpdateDto;
//import org.accounting.system.dtos.metricdefinition.MetricDefinitionRequestDto;
//import org.accounting.system.dtos.metricdefinition.MetricDefinitionResponseDto;
//import org.accounting.system.enums.acl.AccessControlPermission;
//import org.accounting.system.repositories.acl.AccessControlRepository;
//import org.accounting.system.repositories.metricdefinition.MetricDefinitionRepository;
//import org.accounting.system.services.ReadPredefinedTypesService;
//import org.accounting.system.util.Utility;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mockito;
//
//import javax.inject.Inject;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Optional;
//
//import static io.restassured.RestAssured.given;
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.mockito.ArgumentMatchers.any;
//
//@QuarkusTest
//@TestProfile(AccountingSystemTestProfile.class)
//public class AccessControlTest {
//
//    @InjectMock
//    ReadPredefinedTypesService readPredefinedTypesService;
//
//    @Inject
//    MetricDefinitionRepository metricDefinitionRepository;
//
//    @Inject
//    AccessControlRepository accessControlRepository;
//
//    KeycloakTestClient keycloakClient = new KeycloakTestClient();
//
//    @BeforeEach
//    public void setup() {
//        metricDefinitionRepository.deleteAll();
//        accessControlRepository.deleteAll();
//    }
//
//    @Test
//    public void createAccessControlNotAuthenticated() {
//
//        var notAuthenticatedResponse = given()
//                .auth()
//                .oauth2("invalidToken")
//                .contentType(ContentType.JSON)
//                .post("accounting-system/metric-definition/{metricDefinitionId}/acl/{who}", "507f1f77bcf86cd799439011","fbdb4e4a-6e93-4b08-a1e7-0b7bd08520a6")
//                .thenReturn();
//
//        assertEquals(401, notAuthenticatedResponse.statusCode());
//    }
//
//    @Test
//    public void createAccessControlCannotConsumeContentType() {
//
//        var errorResponse = given()
//                .auth()
//                .oauth2(getAccessToken("admin"))
//                .post("/accounting-system/metric-definition/{metricDefinitionId}/acl/{who}", "507f1f77bcf86cd799439011", "fbdb4e4a-6e93-4b08-a1e7-0b7bd08520a6")
//                .then()
//                .assertThat()
//                .statusCode(415)
//                .extract()
//                .as(InformativeResponse.class);
//
//        assertEquals("Cannot consume content type.", errorResponse.message);
//    }
//
//    @Test
//    public void createAccessControlEntityNotValid() {
//
//        var request= new PermissionAccessControlRequestDto();
//
//        var permissions = new HashSet<String>();
//        permissions.add("READ");
//        request.permissions = permissions;
//
//        var response = createAccessControlForMetricDefinition(request, "admin", "507f1f77bcf86cd799439011", "fbdb4e4a-6e93-4b08-a1e7-0b7bd08520a6");
//
//        var errorResponse = response
//                .then()
//                .assertThat()
//                .statusCode(404)
//                .extract()
//                .as(InformativeResponse.class);
//
//        assertEquals("There is no Metric Definition with the following id: 507f1f77bcf86cd799439011", errorResponse.message);
//    }
//
//    @Test
//    public void createAccessControlForMetricDefinition() {
//
//        Mockito.when(readPredefinedTypesService.searchForUnitType(any())).thenReturn(Optional.of("SECOND"));
//        Mockito.when(readPredefinedTypesService.searchForMetricType(any())).thenReturn(Optional.of("Aggregated"));
//        MetricDefinitionRequestDto requestMetricDefinition = new MetricDefinitionRequestDto();
//
//        requestMetricDefinition.metricName = "metric";
//        requestMetricDefinition.metricDescription = "description";
//        requestMetricDefinition.unitType = "SECOND";
//        requestMetricDefinition.metricType = "Aggregated";
//
//        var metricDefinition = createMetricDefinition(requestMetricDefinition);
//
//        var request= new PermissionAccessControlRequestDto();
//
//        var permissions = new HashSet<String>();
//        permissions.add("READ");
//        request.permissions = permissions;
//
//        var response = createAccessControlForMetricDefinition(request, "admin", metricDefinition.id, "fbdb4e4a-6e93-4b08-a1e7-0b7bd08520a6");
//
//        var informativeResponse = response
//                .then()
//                .assertThat()
//                .statusCode(200)
//                .extract()
//                .as(InformativeResponse.class);
//
//        assertEquals("Access Control entry has been created successfully", informativeResponse.message);
//    }
//
//    @Test
//    public void createAccessControlForMetricDefinitionCreatorForbidden() {
//
//        Mockito.when(readPredefinedTypesService.searchForUnitType(any())).thenReturn(Optional.of("SECOND"));
//        Mockito.when(readPredefinedTypesService.searchForMetricType(any())).thenReturn(Optional.of("Aggregated"));
//        MetricDefinitionRequestDto requestMetricDefinition = new MetricDefinitionRequestDto();
//
//        requestMetricDefinition.metricName = "metric";
//        requestMetricDefinition.metricDescription = "description";
//        requestMetricDefinition.unitType = "SECOND";
//        requestMetricDefinition.metricType = "Aggregated";
//
//        var metricDefinition = createMetricDefinition(requestMetricDefinition);
//
//        var request= new PermissionAccessControlRequestDto();
//
//        var permissions = new HashSet<String>();
//        permissions.add("READ");
//        request.permissions = permissions;
//
//        // creator (role metric_definition_creator) can grant acl permissions only to its entities since it has been assigned to it the tuple (ACL, ENTITY)
//        var response = createAccessControlForMetricDefinition(request, "creator", metricDefinition.id, "fbdb4e4a-6e93-4b08-a1e7-0b7bd08520a6");
//
//        var informativeResponse = response
//                .then()
//                .assertThat()
//                .statusCode(403)
//                .extract()
//                .as(InformativeResponse.class);
//
//        assertEquals(ApiMessage.NO_PERMISSION.message, informativeResponse.message);
//    }
//
//    @Test
//    public void updateAccessControlNotAuthenticated() {
//
//        var notAuthenticatedResponse = given()
//                .auth()
//                .oauth2("invalidToken")
//                .contentType(ContentType.JSON)
//                .patch("accounting-system/metric-definition/{metricDefinitionId}/acl/{who}", "507f1f77bcf86cd799439011", "fbdb4e4a-6e93-4b08-a1e7-0b7bd08520a6")
//                .thenReturn();
//
//        assertEquals(401, notAuthenticatedResponse.statusCode());
//    }
//
//    @Test
//    public void updateAccessControlInspectorForbidden() {
//
//        var notAuthenticatedResponse = given()
//                .auth()
//                .oauth2(getAccessToken("inspector"))
//                .contentType(ContentType.JSON)
//                .patch("/accounting-system/metric-definition/{metricDefinitionId}/acl/{who}", "507f1f77bcf86cd799439011", "fbdb4e4a-6e93-4b08-a1e7-0b7bd08520a6")
//                .thenReturn();
//
//        assertEquals(403, notAuthenticatedResponse.statusCode());
//    }
//
//    @Test
//    public void updateAccessControlCannotConsumeContentType() {
//
//        var errorResponse = given()
//                .auth()
//                .oauth2(getAccessToken("admin"))
//                .patch("/accounting-system/metric-definition/{metricDefinitionId}/acl/{who}", "507f1f77bcf86cd799439011", "fbdb4e4a-6e93-4b08-a1e7-0b7bd08520a6")
//                .then()
//                .assertThat()
//                .statusCode(415)
//                .extract()
//                .as(InformativeResponse.class);
//
//        assertEquals("Cannot consume content type.", errorResponse.message);
//    }
//
//    @Test
//    public void updateAccessControlEntityNotValid() {
//
//        var request= new PermissionAccessControlUpdateDto();
//
//        var permissions = new HashSet<String>();
//        permissions.add("READ");
//        request.permissions = permissions;
//
//        var response = updateAccessControlForMetricDefinition(request, "admin", "507f1f77bcf86cd799439011", "fbdb4e4a-6e93-4b08-a1e7-0b7bd08520a6");
//
//        var errorResponse = response
//                .then()
//                .assertThat()
//                .statusCode(404)
//                .extract()
//                .as(InformativeResponse.class);
//
//        assertEquals("There is no Metric Definition with the following id: 507f1f77bcf86cd799439011", errorResponse.message);
//    }
//
//    @Test
//    public void updateAccessControlPermissionNotValid() {
//
//        Mockito.when(readPredefinedTypesService.searchForUnitType(any())).thenReturn(Optional.of("SECOND"));
//        Mockito.when(readPredefinedTypesService.searchForMetricType(any())).thenReturn(Optional.of("Aggregated"));
//        MetricDefinitionRequestDto requestMetricDefinition = new MetricDefinitionRequestDto();
//
//        requestMetricDefinition.metricName = "metric";
//        requestMetricDefinition.metricDescription = "description";
//        requestMetricDefinition.unitType = "SECOND";
//        requestMetricDefinition.metricType = "Aggregated";
//
//        var metricDefinition = createMetricDefinition(requestMetricDefinition);
//
//        var request= new PermissionAccessControlUpdateDto();
//
//        var permissions = new HashSet<String>();
//        permissions.add("TEST");
//        request.permissions = permissions;
//
//        var response = updateAccessControlForMetricDefinition(request, "admin", metricDefinition.id, "fbdb4e4a-6e93-4b08-a1e7-0b7bd08520a6");
//
//        var informativeResponse = response
//                .then()
//                .assertThat()
//                .statusCode(400)
//                .extract()
//                .as(InformativeResponse.class);
//
//        assertEquals("The value TEST is not a valid entry. Valid entry values are: "+ Utility.getNamesSet(AccessControlPermission.class), informativeResponse.message);
//    }
//
//    @Test
//    public void updateAccessControlNoPermissions() {
//
//        Mockito.when(readPredefinedTypesService.searchForUnitType(any())).thenReturn(Optional.of("SECOND"));
//        Mockito.when(readPredefinedTypesService.searchForMetricType(any())).thenReturn(Optional.of("Aggregated"));
//        MetricDefinitionRequestDto requestMetricDefinition = new MetricDefinitionRequestDto();
//
//        requestMetricDefinition.metricName = "metric";
//        requestMetricDefinition.metricDescription = "description";
//        requestMetricDefinition.unitType = "SECOND";
//        requestMetricDefinition.metricType = "Aggregated";
//
//        var metricDefinition = createMetricDefinition(requestMetricDefinition);
//
//        var requestForUpdateAccessControl= new PermissionAccessControlUpdateDto();
//
//        var updatePermissions = new HashSet<String>();
//        updatePermissions.add("UPDATE");
//        requestForUpdateAccessControl.permissions = updatePermissions;
//
//        var response = updateAccessControlForMetricDefinition(requestForUpdateAccessControl, "admin", metricDefinition.id, "fbdb4e4a-6e93-4b08-a1e7-0b7bd08520a6");
//
//        var informativeResponse = response
//                .then()
//                .assertThat()
//                .statusCode(404)
//                .extract()
//                .as(InformativeResponse.class);
//
//        assertEquals("There is no assigned permission for the fbdb4e4a-6e93-4b08-a1e7-0b7bd08520a6 to control access to the "+metricDefinition.id, informativeResponse.message);
//    }
//
//    @Test
//    public void updateAccessControlForMetricDefinitionCreatorForbidden() {
//
//        Mockito.when(readPredefinedTypesService.searchForUnitType(any())).thenReturn(Optional.of("SECOND"));
//        Mockito.when(readPredefinedTypesService.searchForMetricType(any())).thenReturn(Optional.of("Aggregated"));
//        MetricDefinitionRequestDto requestMetricDefinition = new MetricDefinitionRequestDto();
//
//        requestMetricDefinition.metricName = "metric";
//        requestMetricDefinition.metricDescription = "description";
//        requestMetricDefinition.unitType = "SECOND";
//        requestMetricDefinition.metricType = "Aggregated";
//
//        var metricDefinition = createMetricDefinition(requestMetricDefinition);
//
//        var request= new PermissionAccessControlRequestDto();
//
//        var permissions = new HashSet<String>();
//        permissions.add("READ");
//        request.permissions = permissions;
//
//        createAccessControlForMetricDefinition(request, "admin", metricDefinition.id, "fbdb4e4a-6e93-4b08-a1e7-0b7bd08520a6");
//
//        var requestForUpdateAccessControl= new PermissionAccessControlUpdateDto();
//        var updatePermissions = new HashSet<String>();
//        updatePermissions.add("UPDATE");
//        requestForUpdateAccessControl.permissions = updatePermissions;
//
//        // creator (role metric_definition_creator) can update only to its entities since it has been assigned to it the tuple (ACL, ENTITY)
//        var response = updateAccessControlForMetricDefinition(requestForUpdateAccessControl, "creator", metricDefinition.id, "fbdb4e4a-6e93-4b08-a1e7-0b7bd08520a6");
//
//        var informativeResponse = response
//                .then()
//                .assertThat()
//                .statusCode(403)
//                .extract()
//                .as(InformativeResponse.class);
//
//        assertEquals(ApiMessage.NO_PERMISSION.message, informativeResponse.message);
//    }
//
//    @Test
//    public void updateAccessControl() {
//
//        Mockito.when(readPredefinedTypesService.searchForUnitType(any())).thenReturn(Optional.of("SECOND"));
//        Mockito.when(readPredefinedTypesService.searchForMetricType(any())).thenReturn(Optional.of("Aggregated"));
//        MetricDefinitionRequestDto requestMetricDefinition = new MetricDefinitionRequestDto();
//
//        requestMetricDefinition.metricName = "metric";
//        requestMetricDefinition.metricDescription = "description";
//        requestMetricDefinition.unitType = "SECOND";
//        requestMetricDefinition.metricType = "Aggregated";
//
//        var metricDefinition = createMetricDefinition(requestMetricDefinition);
//
//        var requestAccessControl= new PermissionAccessControlRequestDto();
//
//        var permissions = new HashSet<String>();
//        permissions.add("READ");
//        requestAccessControl.permissions = permissions;
//
//        createAccessControlForMetricDefinition(requestAccessControl, "admin", metricDefinition.id, "fbdb4e4a-6e93-4b08-a1e7-0b7bd08520a6");
//
//        var requestForUpdateAccessControl= new PermissionAccessControlUpdateDto();
//
//        var updatePermissions = new HashSet<String>();
//        updatePermissions.add("UPDATE");
//        requestForUpdateAccessControl.permissions = updatePermissions;
//
//        var response = updateAccessControlForMetricDefinition(requestForUpdateAccessControl, "admin", metricDefinition.id, "fbdb4e4a-6e93-4b08-a1e7-0b7bd08520a6");
//
//        var accessControlResponseDto = response
//                .then()
//                .assertThat()
//                .statusCode(200)
//                .extract()
//                .as(PermissionAccessControlResponseDto.class);
//
//        assertEquals("UPDATE", accessControlResponseDto.permissions.stream().findAny().get());
//    }
//
//    @Test
//    public void deleteAccessControlNotAuthenticated() {
//
//        var notAuthenticatedResponse = given()
//                .auth()
//                .oauth2("invalidToken")
//                .delete("accounting-system/metric-definition/{metricDefinitionId}/acl/{who}", "507f1f77bcf86cd799439011", "fbdb4e4a-6e93-4b08-a1e7-0b7bd08520a6")
//                .thenReturn();
//
//        assertEquals(401, notAuthenticatedResponse.statusCode());
//    }
//
//    @Test
//    public void deleteAccessControlEntityNotValid() {
//
//
//        var response = deleteAccessControlForMetricDefinition("admin", "507f1f77bcf86cd799439011", "fbdb4e4a-6e93-4b08-a1e7-0b7bd08520a6");
//
//        var errorResponse = response
//                .then()
//                .assertThat()
//                .statusCode(404)
//                .extract()
//                .as(InformativeResponse.class);
//
//        assertEquals("There is no Metric Definition with the following id: 507f1f77bcf86cd799439011", errorResponse.message);
//    }
//
//    @Test
//    public void deleteAccessControlNoPermissions() {
//
//        Mockito.when(readPredefinedTypesService.searchForUnitType(any())).thenReturn(Optional.of("SECOND"));
//        Mockito.when(readPredefinedTypesService.searchForMetricType(any())).thenReturn(Optional.of("Aggregated"));
//        MetricDefinitionRequestDto requestMetricDefinition = new MetricDefinitionRequestDto();
//
//        requestMetricDefinition.metricName = "metric";
//        requestMetricDefinition.metricDescription = "description";
//        requestMetricDefinition.unitType = "SECOND";
//        requestMetricDefinition.metricType = "Aggregated";
//
//        var metricDefinition = createMetricDefinition(requestMetricDefinition);
//
//        var response = deleteAccessControlForMetricDefinition("admin", metricDefinition.id, "fbdb4e4a-6e93-4b08-a1e7-0b7bd08520a6");
//
//        var informativeResponse = response
//                .then()
//                .assertThat()
//                .statusCode(404)
//                .extract()
//                .as(InformativeResponse.class);
//
//        assertEquals("There is no assigned permission for the fbdb4e4a-6e93-4b08-a1e7-0b7bd08520a6 to control access to the "+metricDefinition.id, informativeResponse.message);
//    }
//
//    @Test
//    public void deleteAccessControlForMetricDefinitionCreatorForbidden() {
//
//        Mockito.when(readPredefinedTypesService.searchForUnitType(any())).thenReturn(Optional.of("SECOND"));
//        Mockito.when(readPredefinedTypesService.searchForMetricType(any())).thenReturn(Optional.of("Aggregated"));
//        MetricDefinitionRequestDto requestMetricDefinition = new MetricDefinitionRequestDto();
//
//        requestMetricDefinition.metricName = "metric";
//        requestMetricDefinition.metricDescription = "description";
//        requestMetricDefinition.unitType = "SECOND";
//        requestMetricDefinition.metricType = "Aggregated";
//
//        var metricDefinition = createMetricDefinition(requestMetricDefinition);
//
//        var request= new PermissionAccessControlRequestDto();
//
//        var permissions = new HashSet<String>();
//        permissions.add("READ");
//        request.permissions = permissions;
//
//        createAccessControlForMetricDefinition(request, "admin", metricDefinition.id, "fbdb4e4a-6e93-4b08-a1e7-0b7bd08520a6");
//
//        // creator (role metric_definition_creator) can delete only to its entities since it has been assigned to it the tuple (ACL, ENTITY)
//        var response = deleteAccessControlForMetricDefinition("creator", metricDefinition.id, "fbdb4e4a-6e93-4b08-a1e7-0b7bd08520a6");
//
//        var informativeResponse = response
//                .then()
//                .assertThat()
//                .statusCode(403)
//                .extract()
//                .as(InformativeResponse.class);
//
//        assertEquals(ApiMessage.NO_PERMISSION.message, informativeResponse.message);
//    }
//
//    @Test
//    public void deleteAccessControl() {
//
//        Mockito.when(readPredefinedTypesService.searchForUnitType(any())).thenReturn(Optional.of("SECOND"));
//        Mockito.when(readPredefinedTypesService.searchForMetricType(any())).thenReturn(Optional.of("Aggregated"));
//        MetricDefinitionRequestDto requestMetricDefinition = new MetricDefinitionRequestDto();
//
//        requestMetricDefinition.metricName = "metric";
//        requestMetricDefinition.metricDescription = "description";
//        requestMetricDefinition.unitType = "SECOND";
//        requestMetricDefinition.metricType = "Aggregated";
//
//        var metricDefinition = createMetricDefinition(requestMetricDefinition);
//
//        var requestAccessControl= new PermissionAccessControlRequestDto();
//
//        var permissions = new HashSet<String>();
//        permissions.add("READ");
//        requestAccessControl.permissions = permissions;
//
//        createAccessControlForMetricDefinition(requestAccessControl, "admin", metricDefinition.id, "fbdb4e4a-6e93-4b08-a1e7-0b7bd08520a6");
//
//        var response = deleteAccessControlForMetricDefinition("admin", metricDefinition.id, "fbdb4e4a-6e93-4b08-a1e7-0b7bd08520a6");
//
//        var informativeResponse = response
//                .then()
//                .assertThat()
//                .statusCode(200)
//                .extract()
//                .as(InformativeResponse.class);
//
//        assertEquals("Access Control entry has been deleted successfully.", informativeResponse.message);
//    }
//
//    @Test
//    public void getAccessControlNotAuthenticated() {
//
//        var notAuthenticatedResponse = given()
//                .auth()
//                .oauth2("invalidToken")
//                .get("accounting-system/metric-definition/{metricDefinitionId}/acl/{who}", "507f1f77bcf86cd799439011", "fbdb4e4a-6e93-4b08-a1e7-0b7bd08520a6")
//                .thenReturn();
//
//        assertEquals(401, notAuthenticatedResponse.statusCode());
//    }
//
//    @Test
//    public void getAccessControlEntityNotValid() {
//
//
//        var response = getAccessControlForMetricDefinition("admin", "507f1f77bcf86cd799439011", "fbdb4e4a-6e93-4b08-a1e7-0b7bd08520a6");
//
//        var errorResponse = response
//                .then()
//                .assertThat()
//                .statusCode(404)
//                .extract()
//                .as(InformativeResponse.class);
//
//        assertEquals("There is no Metric Definition with the following id: 507f1f77bcf86cd799439011", errorResponse.message);
//    }
//
//    @Test
//    public void getAccessControlNoPermissions() {
//
//        Mockito.when(readPredefinedTypesService.searchForUnitType(any())).thenReturn(Optional.of("SECOND"));
//        Mockito.when(readPredefinedTypesService.searchForMetricType(any())).thenReturn(Optional.of("Aggregated"));
//        MetricDefinitionRequestDto requestMetricDefinition = new MetricDefinitionRequestDto();
//
//        requestMetricDefinition.metricName = "metric";
//        requestMetricDefinition.metricDescription = "description";
//        requestMetricDefinition.unitType = "SECOND";
//        requestMetricDefinition.metricType = "Aggregated";
//
//        var metricDefinition = createMetricDefinition(requestMetricDefinition);
//
//        var response = getAccessControlForMetricDefinition("admin", metricDefinition.id, "fbdb4e4a-6e93-4b08-a1e7-0b7bd08520a6");
//
//        var informativeResponse = response
//                .then()
//                .assertThat()
//                .statusCode(404)
//                .extract()
//                .as(InformativeResponse.class);
//
//        assertEquals("There is no assigned permission for the fbdb4e4a-6e93-4b08-a1e7-0b7bd08520a6 to control access to the "+metricDefinition.id, informativeResponse.message);
//    }
//
//    @Test
//    public void getAccessControlForMetricDefinitionCreatorForbidden() {
//
//        Mockito.when(readPredefinedTypesService.searchForUnitType(any())).thenReturn(Optional.of("SECOND"));
//        Mockito.when(readPredefinedTypesService.searchForMetricType(any())).thenReturn(Optional.of("Aggregated"));
//        MetricDefinitionRequestDto requestMetricDefinition = new MetricDefinitionRequestDto();
//
//        requestMetricDefinition.metricName = "metric";
//        requestMetricDefinition.metricDescription = "description";
//        requestMetricDefinition.unitType = "SECOND";
//        requestMetricDefinition.metricType = "Aggregated";
//
//        var metricDefinition = createMetricDefinition(requestMetricDefinition);
//
//        var request= new PermissionAccessControlRequestDto();
//
//        var permissions = new HashSet<String>();
//        permissions.add("READ");
//        request.permissions = permissions;
//
//        createAccessControlForMetricDefinition(request, "admin", metricDefinition.id,"fbdb4e4a-6e93-4b08-a1e7-0b7bd08520a6");
//
//        // creator (role metric_definition_creator) can delete only to its entities since it has been assigned to it the tuple (ACL, ENTITY)
//        var response = getAccessControlForMetricDefinition("creator", metricDefinition.id, "fbdb4e4a-6e93-4b08-a1e7-0b7bd08520a6");
//
//        var informativeResponse = response
//                .then()
//                .assertThat()
//                .statusCode(403)
//                .extract()
//                .as(InformativeResponse.class);
//
//        assertEquals(ApiMessage.NO_PERMISSION.message, informativeResponse.message);
//    }
//
//    @Test
//    public void getAccessControl() {
//
//        Mockito.when(readPredefinedTypesService.searchForUnitType(any())).thenReturn(Optional.of("SECOND"));
//        Mockito.when(readPredefinedTypesService.searchForMetricType(any())).thenReturn(Optional.of("Aggregated"));
//        MetricDefinitionRequestDto requestMetricDefinition = new MetricDefinitionRequestDto();
//
//        requestMetricDefinition.metricName = "metric";
//        requestMetricDefinition.metricDescription = "description";
//        requestMetricDefinition.unitType = "SECOND";
//        requestMetricDefinition.metricType = "Aggregated";
//
//        var metricDefinition = createMetricDefinition(requestMetricDefinition);
//
//        var request= new PermissionAccessControlRequestDto();
//
//        var permissions = new HashSet<String>();
//        permissions.add("READ");
//        request.permissions = permissions;
//
//        createAccessControlForMetricDefinition(request, "admin", metricDefinition.id, "fbdb4e4a-6e93-4b08-a1e7-0b7bd08520a6");
//
//        // creator (role metric_definition_creator) can delete only to its entities since it has been assigned to it the tuple (ACL, ENTITY)
//        var response = getAccessControlForMetricDefinition("admin", metricDefinition.id, "fbdb4e4a-6e93-4b08-a1e7-0b7bd08520a6");
//
//        var accessControlResponseDto = response
//                .then()
//                .assertThat()
//                .statusCode(200)
//                .extract()
//                .as(PermissionAccessControlResponseDto.class);
//
//        assertEquals("fbdb4e4a-6e93-4b08-a1e7-0b7bd08520a6", accessControlResponseDto.who);
//    }
//
//    @Test
//    public void getAllAccessControlsNotAuthenticated() {
//
//        var notAuthenticatedResponse = given()
//                .auth()
//                .oauth2("invalidToken")
//                .get("accounting-system/metric-definition/acl")
//                .thenReturn();
//
//        assertEquals(401, notAuthenticatedResponse.statusCode());
//    }
//
//    @Test
//    public void getAllAccessControlsInspectorForbidden() {
//
//        var notAuthenticatedResponse = given()
//                .auth()
//                .oauth2(getAccessToken("inspector"))
//                .get("/accounting-system/metric-definition/acl")
//                .thenReturn();
//
//        assertEquals(403, notAuthenticatedResponse.statusCode());
//    }
//
//    @Test
//    public void getAllAccessControls() {
//
//        //first pair of Access Control - Metric Definition by madmin (metric_definition_admin)
//
//        Mockito.when(readPredefinedTypesService.searchForUnitType(any())).thenReturn(Optional.of("SECOND"));
//        Mockito.when(readPredefinedTypesService.searchForMetricType(any())).thenReturn(Optional.of("Aggregated"));
//        MetricDefinitionRequestDto requestMetricDefinition = new MetricDefinitionRequestDto();
//
//        requestMetricDefinition.metricName = "metric";
//        requestMetricDefinition.metricDescription = "description";
//        requestMetricDefinition.unitType = "SECOND";
//        requestMetricDefinition.metricType = "Aggregated";
//
//        var metricDefinition = createMetricDefinition(requestMetricDefinition);
//
//        var request= new PermissionAccessControlRequestDto();
//
//        var permissions = new HashSet<String>();
//        permissions.add("READ");
//        request.permissions = permissions;
//
//        createAccessControlForMetricDefinition(request, "madmin", metricDefinition.id, "fbdb4e4a-6e93-4b08-a1e7-0b7bd08520a6");
//
//        //second pair of Access Control - Metric Definition by admin (metric_definition_admin)
//
//        Mockito.when(readPredefinedTypesService.searchForUnitType(any())).thenReturn(Optional.of("SECOND"));
//        Mockito.when(readPredefinedTypesService.searchForMetricType(any())).thenReturn(Optional.of("Aggregated"));
//        MetricDefinitionRequestDto requestMetricDefinition1 = new MetricDefinitionRequestDto();
//
//        requestMetricDefinition1.metricName = "metric1";
//        requestMetricDefinition1.metricDescription = "description";
//        requestMetricDefinition1.unitType = "SECOND";
//        requestMetricDefinition1.metricType = "Aggregated";
//
//        var metricDefinition1 = createMetricDefinition(requestMetricDefinition1);
//
//        var request1= new PermissionAccessControlRequestDto();
//
//        var permissions1 = new HashSet<String>();
//        permissions1.add("READ");
//        request1.permissions = permissions1;
//
//        createAccessControlForMetricDefinition(request, "madmin", metricDefinition1.id, "fbdb4e4a-6e93-4b08-a1e7-0b7bd08520a6");
//
//        //third pair of Access Control - Metric Definition by creator (metric_definition_creator)
//
//        Mockito.when(readPredefinedTypesService.searchForUnitType(any())).thenReturn(Optional.of("SECOND"));
//        Mockito.when(readPredefinedTypesService.searchForMetricType(any())).thenReturn(Optional.of("Aggregated"));
//        MetricDefinitionRequestDto requestMetricDefinition2 = new MetricDefinitionRequestDto();
//
//        requestMetricDefinition2.metricName = "metric2";
//        requestMetricDefinition2.metricDescription = "description";
//        requestMetricDefinition2.unitType = "SECOND";
//        requestMetricDefinition2.metricType = "Aggregated";
//
//        var metricDefinition2 = createMetricDefinitionByUser(requestMetricDefinition2, "creator");
//
//        var request2= new PermissionAccessControlRequestDto();
//
//        var permissions2 = new HashSet<String>();
//        permissions2.add("READ");
//        request2.permissions = permissions2;
//
//        createAccessControlForMetricDefinition(request, "creator", metricDefinition2.id, "fbdb4e4a-6e93-4b08-a1e7-0b7bd08520a6");
//
//
//        // madmin can read the three created Access Controls
//
//        var allResponse = getAllAccessControlsForMetricDefinition("madmin");
//        var allAccessControls = allResponse.thenReturn();
//
//        assertEquals(200, allAccessControls.statusCode());
//        assertEquals(3, allAccessControls.body().as(List.class).size());
//
//
//        // creator can read only the Access Control that it has created
//
//        var oneResponse = getAllAccessControlsForMetricDefinition("creator");
//        var oneAccessControl = oneResponse.thenReturn();
//
//        assertEquals(200, oneAccessControl.statusCode());
//        assertEquals(1, oneAccessControl.body().as(List.class).size());
//    }
//
//    private Response createAccessControlForMetricDefinition(PermissionAccessControlRequestDto request, String user, String metricDefinitionId, String who){
//
//        return given()
//                .auth()
//                .oauth2(getAccessToken(user))
//                .body(request)
//                .contentType(ContentType.JSON)
//                .post("/accounting-system/metric-definition/{metricDefinitionId}/acl/{who}", metricDefinitionId, who);
//    }
//
//    private Response updateAccessControlForMetricDefinition(PermissionAccessControlUpdateDto request, String user, String metricDefinitionId, String who){
//
//        return given()
//                .auth()
//                .oauth2(getAccessToken(user))
//                .body(request)
//                .contentType(ContentType.JSON)
//                .patch("/accounting-system/metric-definition/{metricDefinitionId}/acl/{who}", metricDefinitionId, who);
//    }
//
//    private Response deleteAccessControlForMetricDefinition(String user, String metricDefinitionId, String who){
//
//        return given()
//                .auth()
//                .oauth2(getAccessToken(user))
//                .delete("/accounting-system/metric-definition/{metricDefinitionId}/acl/{who}", metricDefinitionId, who);
//    }
//
//    private Response getAccessControlForMetricDefinition(String user, String metricDefinitionId, String who){
//
//        return given()
//                .auth()
//                .oauth2(getAccessToken(user))
//                .get("/accounting-system/metric-definition/{metricDefinitionId}/acl/{who}", metricDefinitionId, who);
//    }
//
//    private Response getAllAccessControlsForMetricDefinition(String user){
//
//        return given()
//                .auth()
//                .oauth2(getAccessToken(user))
//                .get("/accounting-system/metric-definition/acl");
//    }
//
//    private MetricDefinitionResponseDto createMetricDefinition(MetricDefinitionRequestDto request){
//
//        return given()
//                .auth()
//                .oauth2(getAccessToken("admin"))
//                .basePath("accounting-system/metric-definition")
//                .body(request)
//                .contentType(ContentType.JSON)
//                .post()
//                .then()
//                .assertThat()
//                .statusCode(201)
//                .extract()
//                .as(MetricDefinitionResponseDto.class);
//    }
//
//    private MetricDefinitionResponseDto createMetricDefinitionByUser(MetricDefinitionRequestDto request, String user){
//
//        return given()
//                .auth()
//                .oauth2(getAccessToken(user))
//                .basePath("accounting-system/metric-definition")
//                .body(request)
//                .contentType(ContentType.JSON)
//                .post()
//                .then()
//                .assertThat()
//                .statusCode(201)
//                .extract()
//                .as(MetricDefinitionResponseDto.class);
//    }
//
//    protected String getAccessToken(String userName) {
//        return keycloakClient.getAccessToken(userName);
//    }
//}