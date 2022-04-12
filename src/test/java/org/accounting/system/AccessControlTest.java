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
import org.accounting.system.enums.acl.AccessControlPermission;
import org.accounting.system.repositories.metricdefinition.MetricDefinitionRepository;
import org.accounting.system.services.ReadPredefinedTypesService;
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

        var permissions = new HashSet<AccessControlPermission>();
        permissions.add(AccessControlPermission.READ);
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

        var permissions = new HashSet<AccessControlPermission>();
        permissions.add(AccessControlPermission.READ);
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

        var permissions = new HashSet<AccessControlPermission>();
        permissions.add(AccessControlPermission.READ);
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

        var permissions = new HashSet<AccessControlPermission>();
        permissions.add(AccessControlPermission.READ);
        request.permissions = permissions;

        // creator (role metric_definition_creator) can grant acl permissions only to its entities since it has been assigned to it the tuple (ACL, ENTITY)
        var response = createAccessControlForMetricDefinition(request, "creator", metricDefinition.id);

        var informativeResponse = response
                .then()
                .assertThat()
                .statusCode(403)
                .extract()
                .as(InformativeResponse.class);

        assertEquals("You have no access to grant acl permissions to this entity : "+metricDefinition.id, informativeResponse.message);
    }

    private Response createAccessControlForMetricDefinition(AccessControlRequestDto request, String user, String metricDefinitionId){

        return given()
                .auth()
                .oauth2(getAccessToken(user))
                .body(request)
                .contentType(ContentType.JSON)
                .post("/accounting-system/metric-definition/{metricDefinitionId}/acl", metricDefinitionId);
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