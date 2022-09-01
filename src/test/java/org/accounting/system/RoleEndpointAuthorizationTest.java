package org.accounting.system;

import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import io.quarkus.test.keycloak.client.KeycloakTestClient;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.accounting.system.dtos.InformativeResponse;
import org.accounting.system.dtos.authorization.AccessPermissionDto;
import org.accounting.system.dtos.authorization.CollectionAccessPermissionDto;
import org.accounting.system.dtos.authorization.request.RoleRequestDto;
import org.accounting.system.dtos.authorization.response.RoleResponseDto;
import org.accounting.system.endpoints.RoleEndpoint;
import org.accounting.system.enums.ApiMessage;
import org.accounting.system.repositories.client.ClientAccessAlwaysRepository;
import org.accounting.system.services.client.ClientService;
import org.accounting.system.util.Utility;
import org.json.simple.parser.ParseException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import javax.inject.Inject;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
@TestProfile(AccountingSystemTestProfile.class)
@TestHTTPEndpoint(RoleEndpoint.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class RoleEndpointAuthorizationTest {

    @Inject
    Utility utility;

    @Inject
    ClientService clientService;

    @Inject
    ClientAccessAlwaysRepository clientAccessAlwaysRepository;

    KeycloakTestClient keycloakClient = new KeycloakTestClient();

    @BeforeAll
    public void setup() throws ExecutionException, InterruptedException, ParseException {

        clientService.register(utility.getIdFromToken(keycloakClient.getAccessToken("admin").split("\\.")[1]), "admin", "admin@email.com");

        clientAccessAlwaysRepository.assignRolesToRegisteredClient(utility.getIdFromToken(keycloakClient.getAccessToken("creator").split("\\.")[1]), Set.of("collection_owner"));
    }

    @Test
    public void createRoleInspectorForbidden(){

        var request= new RoleRequestDto();

        var roleResponse = createRole(request, "inspector");

        var informativeResponse = roleResponse
                .then()
                .assertThat()
                .statusCode(403)
                .extract()
                .as(InformativeResponse.class);

        assertEquals(ApiMessage.UNAUTHORIZED_CLIENT.message, informativeResponse.message);
    }

    @Test
    public void createTestRole() {

        //we will create a test_role that has only access to read roles
        var request= new RoleRequestDto();
        request.name = "test_role";

        var collectionPermission = new HashSet<CollectionAccessPermissionDto>();
        CollectionAccessPermissionDto collectionAccessPermissionDto = new CollectionAccessPermissionDto();

        var permissions = new HashSet<AccessPermissionDto>();

        var permissionDto = new AccessPermissionDto();
        permissionDto.operation = "READ";
        permissionDto.accessType = "ALWAYS";

        permissions.add(permissionDto);

        collectionAccessPermissionDto.accessPermissions = permissions;
        collectionAccessPermissionDto.collection = "Role";

        collectionPermission.add(collectionAccessPermissionDto);

        request.collectionsAccessPermissions = collectionPermission;

        var response = createRole(request, "admin");

        response
                .then()
                .assertThat()
                .statusCode(201)
                .extract()
                .as(RoleResponseDto.class);

        // we will try to create a new role with test user who has actually the test_role role

        var notAuthorizedResponse = createRole(request, "test");

        var informativeResponse = notAuthorizedResponse
                .then()
                .assertThat()
                .statusCode(403)
                .extract()
                .as(InformativeResponse.class);

        assertEquals(ApiMessage.UNAUTHORIZED_CLIENT.message, informativeResponse.message);
    }

    @Test
    public void deleteRoleForbidden(){

        var metricDefinitionResponse = deleteRole("inspector", "507f1f77bcf86cd799439011");

        var informativeResponse = metricDefinitionResponse
                .then()
                .assertThat()
                .statusCode(403)
                .extract()
                .as(InformativeResponse.class);

        assertEquals(ApiMessage.UNAUTHORIZED_CLIENT.message, informativeResponse.message);
    }

    @Test
    public void updateRoleForbidden(){

        var metricDefinitionResponse = updateRole("inspector", "507f1f77bcf86cd799439011");

        var informativeResponse = metricDefinitionResponse
                .then()
                .assertThat()
                .statusCode(403)
                .extract()
                .as(InformativeResponse.class);

        assertEquals(ApiMessage.UNAUTHORIZED_CLIENT.message, informativeResponse.message);
    }

    @Test
    public void fetchRoleForbidden(){

        var metricDefinitionResponse = fetchRole("inspector", "507f1f77bcf86cd799439011");

        var informativeResponse = metricDefinitionResponse
                .then()
                .assertThat()
                .statusCode(403)
                .extract()
                .as(InformativeResponse.class);

        assertEquals(ApiMessage.UNAUTHORIZED_CLIENT.message, informativeResponse.message);
    }

    protected String getAccessToken(String userName) {
        return keycloakClient.getAccessToken(userName);
    }

    private Response createRole(RoleRequestDto request, String user){

        return given()
                .auth()
                .oauth2(getAccessToken(user))
                .body(request)
                .contentType(ContentType.JSON)
                .post();
    }

    private Response deleteRole(String user, String id){

        return given()
                .auth()
                .oauth2(getAccessToken(user))
                .delete("/{id}", id);
    }

    private Response updateRole(String user, String id){

        return given()
                .auth()
                .oauth2(getAccessToken(user))
                .contentType(ContentType.JSON)
                .patch("/{id}", id);
    }
    private Response fetchRole(String user, String id){

        return given()
                .auth()
                .oauth2(getAccessToken(user))
                .get("/{id}", id);
    }
}