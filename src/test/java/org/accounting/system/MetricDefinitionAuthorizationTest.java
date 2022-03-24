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
import org.accounting.system.dtos.UpdateMetricDefinitionRequestDto;
import org.accounting.system.enums.AccessType;
import org.accounting.system.enums.Collection;
import org.accounting.system.enums.Operation;
import org.accounting.system.repositories.MetricDefinitionRepository;
import org.accounting.system.repositories.authorization.RoleRepository;
import org.accounting.system.services.ReadPredefinedTypesService;
import org.accounting.system.services.authorization.RoleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javax.inject.Inject;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;


@QuarkusTest
@TestProfile(AccountingSystemTestProfile.class)
public class MetricDefinitionAuthorizationTest {

    @InjectMock
    ReadPredefinedTypesService readPredefinedTypesService;

    @Inject
    RoleService roleService;

    @Inject
    RoleRepository roleRepository;

    @Inject
    MetricDefinitionRepository metricDefinitionRepository;


    KeycloakTestClient keycloakClient = new KeycloakTestClient();

    @BeforeEach
    public void setup() {
        metricDefinitionRepository.deleteAll();
    }

    @Test
    public void createMetricDefinitionInspectorForbidden(){

        Mockito.when(readPredefinedTypesService.searchForUnitType(any())).thenReturn(Optional.of("SECOND"));
        Mockito.when(readPredefinedTypesService.searchForMetricType(any())).thenReturn(Optional.of("Aggregated"));
        MetricDefinitionRequestDto request= new MetricDefinitionRequestDto();

        request.metricName = "metric";
        request.metricDescription = "description";
        request.unitType = "SECOND";
        request.metricType = "Aggregated";

        var metricDefinitionResponse = createMetricDefinition(request, "inspector");

        var informativeResponse = metricDefinitionResponse
                .then()
                .assertThat()
                .statusCode(403)
                .extract()
                .as(InformativeResponse.class);

        assertEquals("The authenticated user/service is not permitted to perform the requested operation.", informativeResponse.message);
    }

    @Test
    public void createMetricDefinitionNoRelevantRoleForbidden(){

        Mockito.when(readPredefinedTypesService.searchForUnitType(any())).thenReturn(Optional.of("SECOND"));
        Mockito.when(readPredefinedTypesService.searchForMetricType(any())).thenReturn(Optional.of("Aggregated"));
        MetricDefinitionRequestDto request= new MetricDefinitionRequestDto();

        request.metricName = "metric";
        request.metricDescription = "description";
        request.unitType = "SECOND";
        request.metricType = "Aggregated";

        var metricDefinitionResponse = createMetricDefinition(request, "alice");

        var informativeResponse = metricDefinitionResponse
                .then()
                .assertThat()
                .statusCode(403)
                .extract()
                .as(InformativeResponse.class);

        assertEquals("The authenticated user/service is not permitted to perform the requested operation.", informativeResponse.message);
    }

    @Test
    public void createMetricDefinitionCombineForbidden(){

        Mockito.when(readPredefinedTypesService.searchForUnitType(any())).thenReturn(Optional.of("SECOND"));
        Mockito.when(readPredefinedTypesService.searchForMetricType(any())).thenReturn(Optional.of("Aggregated"));
        MetricDefinitionRequestDto request= new MetricDefinitionRequestDto();

        request.metricName = "metric";
        request.metricDescription = "description";
        request.unitType = "SECOND";
        request.metricType = "Aggregated";

        var metricDefinitionResponse = createMetricDefinition(request, "combine");

        var informativeResponse = metricDefinitionResponse
                .then()
                .assertThat()
                .statusCode(403)
                .extract()
                .as(InformativeResponse.class);

        assertEquals("The authenticated user/service is not permitted to perform the requested operation.", informativeResponse.message);
    }

    @Test
    public void createMetricDefinitionCreatorForbidden(){

        Mockito.when(readPredefinedTypesService.searchForUnitType(any())).thenReturn(Optional.of("SECOND"));
        Mockito.when(readPredefinedTypesService.searchForMetricType(any())).thenReturn(Optional.of("Aggregated"));
        MetricDefinitionRequestDto request= new MetricDefinitionRequestDto();

        request.metricName = "metric";
        request.metricDescription = "description";
        request.unitType = "SECOND";
        request.metricType = "Aggregated";

        var metricDefinitionResponse = createMetricDefinition(request, "admin");

        // admin user create a Metric Definition
        var metricDefinitionResponseDto = metricDefinitionResponse
                .then()
                .assertThat()
                .statusCode(201)
                .extract()
                .as(MetricDefinitionResponseDto.class);

        // user with creator role is trying to access the Metric Definition that has been created by admin

        var fetchMetricDefinitionResponse = fetchMetricDefinition("creator", metricDefinitionResponseDto.id);

        var informativeResponse = fetchMetricDefinitionResponse
                .then()
                .assertThat()
                .statusCode(403)
                .extract()
                .as(InformativeResponse.class);

        assertEquals("You have no access to this entity : "+metricDefinitionResponseDto.id, informativeResponse.message);
    }

    @Test
    public void updateMetricDefinitionInspectorForbidden(){

        Mockito.when(readPredefinedTypesService.searchForUnitType(any())).thenReturn(Optional.of("SECOND"));
        Mockito.when(readPredefinedTypesService.searchForMetricType(any())).thenReturn(Optional.of("Aggregated"));
        MetricDefinitionRequestDto request= new MetricDefinitionRequestDto();

        request.metricName = "metric";
        request.metricDescription = "description";
        request.unitType = "SECOND";
        request.metricType = "Aggregated";

        var metricDefinitionResponse = updateMetricDefinition(request, "inspector");

        var informativeResponse = metricDefinitionResponse
                .then()
                .assertThat()
                .statusCode(403)
                .extract()
                .as(InformativeResponse.class);

        assertEquals("The authenticated user/service is not permitted to perform the requested operation.", informativeResponse.message);
    }

    @Test
    public void updateMetricDefinitionNoRelevantRoleForbidden(){

        Mockito.when(readPredefinedTypesService.searchForUnitType(any())).thenReturn(Optional.of("SECOND"));
        Mockito.when(readPredefinedTypesService.searchForMetricType(any())).thenReturn(Optional.of("Aggregated"));
        MetricDefinitionRequestDto request= new MetricDefinitionRequestDto();

        request.metricName = "metric";
        request.metricDescription = "description";
        request.unitType = "SECOND";
        request.metricType = "Aggregated";

        var metricDefinitionResponse = updateMetricDefinition(request, "alice");

        var informativeResponse = metricDefinitionResponse
                .then()
                .assertThat()
                .statusCode(403)
                .extract()
                .as(InformativeResponse.class);

        assertEquals("The authenticated user/service is not permitted to perform the requested operation.", informativeResponse.message);
    }

    @Test
    public void updateMetricDefinitionCombineForbidden(){

        Mockito.when(readPredefinedTypesService.searchForUnitType(any())).thenReturn(Optional.of("SECOND"));
        Mockito.when(readPredefinedTypesService.searchForMetricType(any())).thenReturn(Optional.of("Aggregated"));
        MetricDefinitionRequestDto request= new MetricDefinitionRequestDto();

        request.metricName = "metric";
        request.metricDescription = "description";
        request.unitType = "SECOND";
        request.metricType = "Aggregated";

        var metricDefinitionResponse = updateMetricDefinition(request, "combine");

        var informativeResponse = metricDefinitionResponse
                .then()
                .assertThat()
                .statusCode(403)
                .extract()
                .as(InformativeResponse.class);

        assertEquals("The authenticated user/service is not permitted to perform the requested operation.", informativeResponse.message);
    }

    @Test
    public void updateMetricDefinitionCreator(){

        // first admin user is creating a Metric Definition
        Mockito.when(readPredefinedTypesService.searchForUnitType(any())).thenReturn(Optional.of("SECOND"));
        Mockito.when(readPredefinedTypesService.searchForMetricType(any())).thenReturn(Optional.of("Aggregated"));
        MetricDefinitionRequestDto request= new MetricDefinitionRequestDto();

        request.metricName = "metric";
        request.metricDescription = "description";
        request.unitType = "SECOND";
        request.metricType = "Aggregated";

        var metricDefinitionResponse = createMetricDefinition(request, "admin");

        var metricDefinitionResponseDto = metricDefinitionResponse
                .then()
                .assertThat()
                .statusCode(201)
                .extract()
                .as(MetricDefinitionResponseDto.class);

        //then creator user is trying to update the Metric Definition that has been created by admin
        UpdateMetricDefinitionRequestDto update = new UpdateMetricDefinitionRequestDto();
        var updateMetricDefinitionResponse = updateMetricDefinitionById(update, "creator", metricDefinitionResponseDto.id);

        var informativeResponse = updateMetricDefinitionResponse
                .then()
                .assertThat()
                .statusCode(403)
                .extract()
                .as(InformativeResponse.class);

        assertEquals("You have no access to this entity : "+metricDefinitionResponseDto.id, informativeResponse.message);

        //creator user is creating a Metric Definition
        Mockito.when(readPredefinedTypesService.searchForUnitType(any())).thenReturn(Optional.of("SECOND"));
        Mockito.when(readPredefinedTypesService.searchForMetricType(any())).thenReturn(Optional.of("Aggregated"));
        MetricDefinitionRequestDto request1= new MetricDefinitionRequestDto();

        request1.metricName = "metric1";
        request1.metricDescription = "description";
        request1.unitType = "SECOND";
        request1.metricType = "Aggregated";

        var creatorMetricDefinitionResponse = createMetricDefinition(request1, "creator");

        var creatorMetricDefinitionResponseDto = creatorMetricDefinitionResponse
                .then()
                .assertThat()
                .statusCode(201)
                .extract()
                .as(MetricDefinitionResponseDto.class);

        //creator can now update its Metric Definition
        UpdateMetricDefinitionRequestDto update1 = new UpdateMetricDefinitionRequestDto();
        update1.metricName = "blabla";

        var creatorUpdateMetricDefinitionResponse = updateMetricDefinitionById(update1, "creator", creatorMetricDefinitionResponseDto.id);

        var creatorUpdateMetricDefinitionResponseDto = creatorUpdateMetricDefinitionResponse
                .then()
                .assertThat()
                .statusCode(200)
                .extract()
                .as(MetricDefinitionResponseDto.class);

        assertEquals("blabla", creatorUpdateMetricDefinitionResponseDto.metricName);
    }

    @Test
    public void deleteMetricDefinitionInspectorForbidden(){

        var metricDefinitionResponse = deleteMetricDefinition("inspector");

        var informativeResponse = metricDefinitionResponse
                .then()
                .assertThat()
                .statusCode(403)
                .extract()
                .as(InformativeResponse.class);

        assertEquals("The authenticated user/service is not permitted to perform the requested operation.", informativeResponse.message);
    }

    @Test
    public void deleteMetricDefinitionNoRelevantRoleForbidden(){

        var metricDefinitionResponse = deleteMetricDefinition("alice");

        var informativeResponse = metricDefinitionResponse
                .then()
                .assertThat()
                .statusCode(403)
                .extract()
                .as(InformativeResponse.class);

        assertEquals("The authenticated user/service is not permitted to perform the requested operation.", informativeResponse.message);
    }

    @Test
    public void deleteMetricDefinitionCombineForbidden(){

        var metricDefinitionResponse = deleteMetricDefinition("combine");

        var informativeResponse = metricDefinitionResponse
                .then()
                .assertThat()
                .statusCode(403)
                .extract()
                .as(InformativeResponse.class);

        assertEquals("The authenticated user/service is not permitted to perform the requested operation.", informativeResponse.message);
    }

    @Test
    public void deleteMetricDefinitionCreator(){

        // first admin user is creating a Metric Definition
        Mockito.when(readPredefinedTypesService.searchForUnitType(any())).thenReturn(Optional.of("SECOND"));
        Mockito.when(readPredefinedTypesService.searchForMetricType(any())).thenReturn(Optional.of("Aggregated"));
        MetricDefinitionRequestDto request= new MetricDefinitionRequestDto();

        request.metricName = "metric";
        request.metricDescription = "description";
        request.unitType = "SECOND";
        request.metricType = "Aggregated";

        var metricDefinitionResponse = createMetricDefinition(request, "admin");

        var metricDefinitionResponseDto = metricDefinitionResponse
                .then()
                .assertThat()
                .statusCode(201)
                .extract()
                .as(MetricDefinitionResponseDto.class);

        // creator user is trying to delete the Metric Definition that has been created by admin user
        var deleteCreatorResponse = deleteMetricDefinitionById("creator", metricDefinitionResponseDto.id);

        var informativeResponse = deleteCreatorResponse
                .then()
                .assertThat()
                .statusCode(403)
                .extract()
                .as(InformativeResponse.class);

        assertEquals("You have no access to this entity : "+metricDefinitionResponseDto.id, informativeResponse.message);

        // creator user is creating a Metric Definition
        Mockito.when(readPredefinedTypesService.searchForUnitType(any())).thenReturn(Optional.of("SECOND"));
        Mockito.when(readPredefinedTypesService.searchForMetricType(any())).thenReturn(Optional.of("Aggregated"));
        MetricDefinitionRequestDto request1= new MetricDefinitionRequestDto();

        request1.metricName = "metric1";
        request1.metricDescription = "description";
        request1.unitType = "SECOND";
        request1.metricType = "Aggregated";

        var creatorMetricDefinitionResponse = createMetricDefinition(request1, "creator");

        var creatorMetricDefinitionResponseDto = creatorMetricDefinitionResponse
                .then()
                .assertThat()
                .statusCode(201)
                .extract()
                .as(MetricDefinitionResponseDto.class);

        // creator user is now trying to delete its Metric Definition
        var deleteCreatorResponse1 = deleteMetricDefinitionById("creator", creatorMetricDefinitionResponseDto.id);

        var informativeResponse1 = deleteCreatorResponse1
                .then()
                .assertThat()
                .statusCode(200)
                .extract()
                .as(InformativeResponse.class);

        assertEquals("Metric Definition has been deleted successfully.", informativeResponse1.message);
    }

    @Test
    public void metricDefinitionAdminCollectionPermissions(){

        var metricDefinitionRole = roleRepository.getRoleByName("metric_definition_admin").stream().findAny().get();

        var permissions = metricDefinitionRole
                .getCollectionPermission()
                .stream()
                .filter(collectionPermission -> collectionPermission.collection.equals(Collection.MetricDefinition))
                .flatMap(md->md.permissions.stream())
                .collect(Collectors.toList());

        assertEquals(AccessType.ALWAYS, permissions
                .stream()
                .filter(permission -> permission.operation.equals(Operation.CREATE))
                .findAny()
                .get().accessType
        );
        assertEquals(AccessType.ALWAYS, permissions
                .stream()
                .filter(permission -> permission.operation.equals(Operation.UPDATE))
                .findAny()
                .get().accessType
        );
        assertEquals(AccessType.ALWAYS, permissions
                .stream()
                .filter(permission -> permission.operation.equals(Operation.DELETE))
                .findAny()
                .get().accessType
        );
        assertEquals(AccessType.ALWAYS, permissions
                .stream()
                .filter(permission -> permission.operation.equals(Operation.READ))
                .findAny()
                .get().accessType
        );
    }

    @Test
    public void metricDefinitionInspectorCollectionPermissions(){

        var metricDefinitionRole = roleRepository.getRoleByName("metric_definition_inspector").stream().findAny().get();

        var permissions = metricDefinitionRole
                .getCollectionPermission()
                .stream()
                .filter(collectionPermission -> collectionPermission.collection.equals(Collection.MetricDefinition))
                .flatMap(md->md.permissions.stream())
                .collect(Collectors.toList());

        assertEquals(AccessType.NEVER, permissions
                .stream()
                .filter(permission -> permission.operation.equals(Operation.CREATE))
                .findAny()
                .get().accessType
        );
        assertEquals(AccessType.NEVER, permissions
                .stream()
                .filter(permission -> permission.operation.equals(Operation.UPDATE))
                .findAny()
                .get().accessType
        );
        assertEquals(AccessType.NEVER, permissions
                .stream()
                .filter(permission -> permission.operation.equals(Operation.DELETE))
                .findAny()
                .get().accessType
        );
        assertEquals(AccessType.ALWAYS, permissions
                .stream()
                .filter(permission -> permission.operation.equals(Operation.READ))
                .findAny()
                .get().accessType
        );
    }

    @Test
    public void getAllMetricDefinitions(){

        // admin user will submit two metric definitions
        Mockito.when(readPredefinedTypesService.searchForUnitType(any())).thenReturn(Optional.of("SECOND"));
        Mockito.when(readPredefinedTypesService.searchForMetricType(any())).thenReturn(Optional.of("Aggregated"));
        MetricDefinitionRequestDto request= new MetricDefinitionRequestDto();

        request.metricName = "metric";
        request.metricDescription = "description";
        request.unitType = "SECOND";
        request.metricType = "Aggregated";

        var metricDefinitionResponse = createMetricDefinition(request, "admin");

        metricDefinitionResponse
                .then()
                .assertThat()
                .statusCode(201);

        Mockito.when(readPredefinedTypesService.searchForUnitType(any())).thenReturn(Optional.of("SECOND"));
        Mockito.when(readPredefinedTypesService.searchForMetricType(any())).thenReturn(Optional.of("Aggregated"));
        MetricDefinitionRequestDto request1= new MetricDefinitionRequestDto();

        request1.metricName = "metric1";
        request1.metricDescription = "description";
        request1.unitType = "SECOND";
        request1.metricType = "Aggregated";

        var metricDefinitionResponse1 = createMetricDefinition(request1, "admin");

        metricDefinitionResponse1
                .then()
                .assertThat()
                .statusCode(201);

        //creator user will submit a Metric Definition
        Mockito.when(readPredefinedTypesService.searchForUnitType(any())).thenReturn(Optional.of("SECOND"));
        Mockito.when(readPredefinedTypesService.searchForMetricType(any())).thenReturn(Optional.of("Aggregated"));
        MetricDefinitionRequestDto request2= new MetricDefinitionRequestDto();

        request2.metricName = "metric2";
        request2.metricDescription = "description";
        request2.unitType = "SECOND";
        request2.metricType = "Aggregated";

        var metricDefinitionResponse2 = createMetricDefinition(request2, "creator");

        metricDefinitionResponse2
                .then()
                .assertThat()
                .statusCode(201);

        var fetchAdminMetricDefinitions = fetchAllMetricDefinitions("admin");

        var adminList= fetchAdminMetricDefinitions
                .then()
                .assertThat()
                .statusCode(200)
                .extract()
                .as(List.class);

        //because admin can access all metric definitions the size of list should be 3
        assertEquals(3, adminList.size());

        var fetchCreatorMetricDefinitions = fetchAllMetricDefinitions("creator");

        var creatorList= fetchCreatorMetricDefinitions
                .then()
                .assertThat()
                .statusCode(200)
                .extract()
                .as(List.class);

        //because creator can access only its Metric Definitions the size of list should be 1
        assertEquals(1, creatorList.size());
    }

    private Response createMetricDefinition(MetricDefinitionRequestDto request, String user){

        return given()
                .auth()
                .oauth2(getAccessToken(user))
                .basePath("accounting-system/metric-definition")
                .body(request)
                .contentType(ContentType.JSON)
                .post();
    }

    private Response updateMetricDefinition(MetricDefinitionRequestDto request, String user){

        return given()
                .auth()
                .oauth2(getAccessToken(user))
                .basePath("accounting-system/metric-definition")
                .body(request)
                .contentType(ContentType.JSON)
                .patch("/{id}", "507f1f77bcf86cd799439011");
    }

    private Response updateMetricDefinitionById(UpdateMetricDefinitionRequestDto request, String user, String id){

        return given()
                .auth()
                .oauth2(getAccessToken(user))
                .basePath("accounting-system/metric-definition")
                .body(request)
                .contentType(ContentType.JSON)
                .patch("/{id}", id);
    }

    private Response deleteMetricDefinition(String user){

        return given()
                .auth()
                .oauth2(getAccessToken(user))
                .basePath("accounting-system/metric-definition")
                .contentType(ContentType.JSON)
                .delete("/{id}", "507f1f77bcf86cd799439011");
    }

    private Response deleteMetricDefinitionById(String user, String id){

        return given()
                .auth()
                .oauth2(getAccessToken(user))
                .basePath("accounting-system/metric-definition")
                .contentType(ContentType.JSON)
                .delete("/{id}", id);
    }

    private Response fetchMetricDefinition(String user, String id){

        return given()
                .auth()
                .oauth2(getAccessToken(user))
                .basePath("accounting-system/metric-definition")
                .get("/{id}", id);
    }

    private Response fetchAllMetricDefinitions(String user){

        return given()
                .auth()
                .oauth2(getAccessToken(user))
                .basePath("accounting-system/metric-definition")
                .get();
    }

    protected String getAccessToken(String userName) {
        return keycloakClient.getAccessToken(userName);
    }
}
