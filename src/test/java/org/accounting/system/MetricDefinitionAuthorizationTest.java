package org.accounting.system;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import io.quarkus.test.junit.mockito.InjectMock;
import io.quarkus.test.keycloak.client.KeycloakTestClient;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.accounting.system.dtos.InformativeResponse;
import org.accounting.system.dtos.metricdefinition.MetricDefinitionRequestDto;
import org.accounting.system.dtos.metricdefinition.MetricDefinitionResponseDto;
import org.accounting.system.dtos.metricdefinition.UpdateMetricDefinitionRequestDto;
import org.accounting.system.dtos.pagination.PageResource;
import org.accounting.system.repositories.authorization.RoleRepository;
import org.accounting.system.repositories.client.ClientAccessAlwaysRepository;
import org.accounting.system.repositories.client.ClientRepository;
import org.accounting.system.repositories.metricdefinition.MetricDefinitionRepository;
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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;


@QuarkusTest
@TestProfile(AccountingSystemTestProfile.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class MetricDefinitionAuthorizationTest {

    @InjectMock
    ReadPredefinedTypesService readPredefinedTypesService;

    @Inject
    RoleRepository roleRepository;

    @Inject
    MetricDefinitionRepository metricDefinitionRepository;

    @Inject
    Utility utility;

    @Inject
    ClientRepository clientRepository;

    @Inject
    ClientService clientService;

    @Inject
    ClientAccessAlwaysRepository clientAccessAlwaysRepository;


    KeycloakTestClient keycloakClient = new KeycloakTestClient();

    @BeforeAll
    public void setup() throws ParseException {

        clientService.register(utility.getIdFromToken(keycloakClient.getAccessToken("admin").split("\\.")[1]), "admin", "admin@email.com");

        clientAccessAlwaysRepository.assignRolesToRegisteredClient(utility.getIdFromToken(keycloakClient.getAccessToken("admin").split("\\.")[1]), Set.of("collection_owner"));

        clientService.register(utility.getIdFromToken(keycloakClient.getAccessToken("creator").split("\\.")[1]), "creator", "creator@email.com");

        clientAccessAlwaysRepository.assignRolesToRegisteredClient(utility.getIdFromToken(keycloakClient.getAccessToken("creator").split("\\.")[1]), Set.of("collection_creator"));
    }

    @BeforeEach
    public void each() {

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

        assertEquals("The authenticated client is not permitted to perform the requested operation.", informativeResponse.message);
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

        assertEquals("The authenticated client is not permitted to perform the requested operation.", informativeResponse.message);
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

        var response = createMetricDefinition(request, "admin");

        var metricDefinitionResponse = response.then().extract().as(MetricDefinitionResponseDto.class);

        var error = updateMetricDefinition(request, "inspector", metricDefinitionResponse.id);

        var informativeResponse = error
                .then()
                .assertThat()
                .statusCode(403)
                .extract()
                .as(InformativeResponse.class);

        assertEquals("The authenticated client is not permitted to perform the requested operation.", informativeResponse.message);
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

        assertEquals("The authenticated client is not permitted to perform the requested operation.", informativeResponse.message);

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

        // first admin user is creating a Metric Definition
        Mockito.when(readPredefinedTypesService.searchForUnitType(any())).thenReturn(Optional.of("SECOND"));
        Mockito.when(readPredefinedTypesService.searchForMetricType(any())).thenReturn(Optional.of("Aggregated"));
        MetricDefinitionRequestDto request= new MetricDefinitionRequestDto();

        request.metricName = "metric";
        request.metricDescription = "description";
        request.unitType = "SECOND";
        request.metricType = "Aggregated";

        var metricDefinitionResponse = createMetricDefinition(request, "admin");

        var response = metricDefinitionResponse
                .then()
                .assertThat()
                .statusCode(201)
                .extract()
                .as(MetricDefinitionResponseDto.class);


        var error = deleteMetricDefinition("inspector", response.id);

        var informativeResponse = error
                .then()
                .assertThat()
                .statusCode(403)
                .extract()
                .as(InformativeResponse.class);

        assertEquals("The authenticated client is not permitted to perform the requested operation.", informativeResponse.message);
    }

    @Test
    public void deleteMetricDefinitionNoRelevantRoleForbidden() {

        // first admin user is creating a Metric Definition
        Mockito.when(readPredefinedTypesService.searchForUnitType(any())).thenReturn(Optional.of("SECOND"));
        Mockito.when(readPredefinedTypesService.searchForMetricType(any())).thenReturn(Optional.of("Aggregated"));
        MetricDefinitionRequestDto request= new MetricDefinitionRequestDto();

        request.metricName = "metric";
        request.metricDescription = "description";
        request.unitType = "SECOND";
        request.metricType = "Aggregated";

        var metricDefinitionResponse = createMetricDefinition(request, "admin");

        var response = metricDefinitionResponse
                .then()
                .assertThat()
                .statusCode(201)
                .extract()
                .as(MetricDefinitionResponseDto.class);

        var error = deleteMetricDefinition("alice", response.id);

        var informativeResponse = error
                .then()
                .assertThat()
                .statusCode(403)
                .extract()
                .as(InformativeResponse.class);

        assertEquals("The authenticated client is not permitted to perform the requested operation.", informativeResponse.message);
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

        assertEquals("The authenticated client is not permitted to perform the requested operation.", informativeResponse.message);

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

        var fetchAdminMetricDefinitionsResponse = fetchAdminMetricDefinitions.thenReturn();

        assertEquals(200, fetchAdminMetricDefinitionsResponse.statusCode());

        var fetchAdminMetricDefinitionsResponseBody = fetchAdminMetricDefinitions.body().as(PageResource.class);

        //because admin can access all metric definitions the size of list should be 3
        assertEquals(3, fetchAdminMetricDefinitionsResponseBody.getTotalElements());

        var fetchCreatorMetricDefinitions = fetchAllMetricDefinitions("creator");

        var fetchCreatorMetricDefinitionsResponse = fetchCreatorMetricDefinitions.thenReturn();

        assertEquals(200, fetchCreatorMetricDefinitionsResponse.statusCode());

        var fetchCreatorMetricDefinitionsResponseBody = fetchCreatorMetricDefinitions.body().as(PageResource.class);

        //because creator can access only its Metric Definitions the size of list should be 1
        assertEquals(3, fetchCreatorMetricDefinitionsResponseBody.getTotalElements());
    }

    private Response createMetricDefinition(MetricDefinitionRequestDto request, String user){

        return given()
                .auth()
                .oauth2(getAccessToken(user))
                .basePath("accounting-system/metric-definitions")
                .body(request)
                .contentType(ContentType.JSON)
                .post();
    }

    private Response updateMetricDefinition(MetricDefinitionRequestDto request, String user, String id){

        return given()
                .auth()
                .oauth2(getAccessToken(user))
                .basePath("accounting-system/metric-definitions")
                .body(request)
                .contentType(ContentType.JSON)
                .patch("/{id}", id);
    }

    private Response updateMetricDefinitionById(UpdateMetricDefinitionRequestDto request, String user, String id){

        return given()
                .auth()
                .oauth2(getAccessToken(user))
                .basePath("accounting-system/metric-definitions")
                .body(request)
                .contentType(ContentType.JSON)
                .patch("/{id}", id);
    }

    private Response deleteMetricDefinition(String user, String metricDefinitionId){

        return given()
                .auth()
                .oauth2(getAccessToken(user))
                .basePath("accounting-system/metric-definitions")
                .contentType(ContentType.JSON)
                .delete("/{id}", metricDefinitionId);
    }

    private Response deleteMetricDefinitionById(String user, String id){

        return given()
                .auth()
                .oauth2(getAccessToken(user))
                .basePath("accounting-system/metric-definitions")
                .contentType(ContentType.JSON)
                .delete("/{id}", id);
    }

    private Response fetchAllMetricDefinitions(String user){

        return given()
                .auth()
                .oauth2(getAccessToken(user))
                .basePath("accounting-system/metric-definitions")
                .get();
    }

    protected String getAccessToken(String userName) {
        return keycloakClient.getAccessToken(userName);
    }
}
