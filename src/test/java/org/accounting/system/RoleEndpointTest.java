package org.accounting.system;

import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import io.quarkus.test.keycloak.client.KeycloakTestClient;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.accounting.system.dtos.InformativeResponse;
import org.accounting.system.dtos.RoleResponseDto;
import org.accounting.system.dtos.authorization.CollectionPermissionDto;
import org.accounting.system.dtos.authorization.PermissionDto;
import org.accounting.system.dtos.authorization.RoleRequestDto;
import org.accounting.system.endpoints.RoleEndpoint;
import org.accounting.system.repositories.authorization.RoleRepository;
import org.accounting.system.services.authorization.RoleService;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.util.ArrayList;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
@TestProfile(AccountingSystemTestProfile.class)
@TestHTTPEndpoint(RoleEndpoint.class)
public class RoleEndpointTest {

    @Inject
    RoleRepository roleRepository;

    @Inject
    RoleService roleService;

    KeycloakTestClient keycloakClient = new KeycloakTestClient();

    @Test
    public void createRoleNotAuthenticated() {

        var notAuthenticatedResponse = given()
                .auth()
                .oauth2("invalidToken")
                .contentType(ContentType.JSON)
                .post()
                .thenReturn();

        assertEquals(401, notAuthenticatedResponse.statusCode());
    }

    @Test
    public void createRoleRequestBodyIsEmpty() {

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
    public void createRoleCannotConsumeContentType() {

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
    public void createRoleNameIsEmpty() {

        var request= new RoleRequestDto();

        var list = new ArrayList<CollectionPermissionDto>();
        CollectionPermissionDto collectionPermissionDto = new CollectionPermissionDto();
        list.add(collectionPermissionDto);

        request.collectionPermission = list;

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

        assertEquals("role name may not be empty.", response.message);
    }

    @Test
    public void createRoleCollectionPermissionListIsEmpty() {

        var request= new RoleRequestDto();
        request.name = "role";

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

        assertEquals("collection_permission_list should have at least one entry.", response.message);
    }

    @Test
    public void createRoleCollectionPermissionListShouldHaveOneEntry() {

        var request= new RoleRequestDto();
        request.name = "role";
        request.collectionPermission = new ArrayList<>();

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

        assertEquals("collection_permission_list should have at least one entry.", response.message);
    }

    @Test
    public void createRolePermissionListIsEmpty() {

        var request= new RoleRequestDto();
        request.name = "role";

        var collectionPermission = new ArrayList<CollectionPermissionDto>();
        CollectionPermissionDto collectionPermissionDto = new CollectionPermissionDto();
        collectionPermissionDto.collection = "Role";
        collectionPermission.add(collectionPermissionDto);

        request.collectionPermission = collectionPermission;

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

        assertEquals("permissions list should have at least one entry.", response.message);
    }

    @Test
    public void createRolePermissionListShouldHaveOneEntry() {

        var request= new RoleRequestDto();
        request.name = "role";

        var collectionPermission = new ArrayList<CollectionPermissionDto>();
        CollectionPermissionDto collectionPermissionDto = new CollectionPermissionDto();
        collectionPermissionDto.collection = "Role";

        var permissions = new ArrayList<PermissionDto>();

        collectionPermissionDto.permissions = permissions;

        collectionPermission.add(collectionPermissionDto);

        request.collectionPermission = collectionPermission;

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

        assertEquals("permissions list should have at least one entry.", response.message);
    }

    @Test
    public void createRoleCollectionIsEmpty() {

        var request= new RoleRequestDto();
        request.name = "role";

        var collectionPermission = new ArrayList<CollectionPermissionDto>();
        CollectionPermissionDto collectionPermissionDto = new CollectionPermissionDto();

        var permissions = new ArrayList<PermissionDto>();

        var permissionDto = new PermissionDto();

        permissions.add(permissionDto);

        collectionPermissionDto.permissions = permissions;

        collectionPermission.add(collectionPermissionDto);

        request.collectionPermission = collectionPermission;

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

        assertEquals("collection may not be empty.", response.message);
    }

    @Test
    public void createRoleCollectionIsNotValid() {

        var request= new RoleRequestDto();
        request.name = "role";

        var collectionPermission = new ArrayList<CollectionPermissionDto>();
        CollectionPermissionDto collectionPermissionDto = new CollectionPermissionDto();

        var permissions = new ArrayList<PermissionDto>();

        var permissionDto = new PermissionDto();

        permissions.add(permissionDto);

        collectionPermissionDto.permissions = permissions;
        collectionPermissionDto.collection = "Test";

        collectionPermission.add(collectionPermissionDto);

        request.collectionPermission = collectionPermission;

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

        assertEquals("The value Test is not a valid collection. Valid collection values are: [Role, MetricDefinition, Metric]", response.message);
    }

    @Test
    public void createRoleOperationIsEmpty() {

        var request= new RoleRequestDto();
        request.name = "role";

        var collectionPermission = new ArrayList<CollectionPermissionDto>();
        CollectionPermissionDto collectionPermissionDto = new CollectionPermissionDto();

        var permissions = new ArrayList<PermissionDto>();

        var permissionDto = new PermissionDto();
        permissionDto.accessType = "ALWAYS";

        permissions.add(permissionDto);

        collectionPermissionDto.permissions = permissions;
        collectionPermissionDto.collection = "Role";

        collectionPermission.add(collectionPermissionDto);

        request.collectionPermission = collectionPermission;

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

        assertEquals("operation may not be empty.", response.message);
    }

    @Test
    public void createRoleOperationIsNotValid() {

        var request= new RoleRequestDto();
        request.name = "role";

        var collectionPermission = new ArrayList<CollectionPermissionDto>();
        CollectionPermissionDto collectionPermissionDto = new CollectionPermissionDto();

        var permissions = new ArrayList<PermissionDto>();

        var permissionDto = new PermissionDto();
        permissionDto.operation = "NOT_VALID";
        permissionDto.accessType = "ALWAYS";

        permissions.add(permissionDto);

        collectionPermissionDto.permissions = permissions;
        collectionPermissionDto.collection = "Role";

        collectionPermission.add(collectionPermissionDto);

        request.collectionPermission = collectionPermission;

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

        assertEquals("The value NOT_VALID is not a valid operation. Valid operation values are: [READ, DELETE, CREATE, UPDATE]", response.message);
    }

    @Test
    public void createRoleAccessTypeIsEmpty() {

        var request= new RoleRequestDto();
        request.name = "role";

        var collectionPermission = new ArrayList<CollectionPermissionDto>();
        CollectionPermissionDto collectionPermissionDto = new CollectionPermissionDto();

        var permissions = new ArrayList<PermissionDto>();

        var permissionDto = new PermissionDto();
        permissionDto.operation = "CREATE";

        permissions.add(permissionDto);

        collectionPermissionDto.permissions = permissions;
        collectionPermissionDto.collection = "Role";

        collectionPermission.add(collectionPermissionDto);

        request.collectionPermission = collectionPermission;

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

        assertEquals("access_type may not be empty.", response.message);
    }

    @Test
    public void createRoleAccessTypeIsNotValid() {

        var request= new RoleRequestDto();
        request.name = "role";

        var collectionPermission = new ArrayList<CollectionPermissionDto>();
        CollectionPermissionDto collectionPermissionDto = new CollectionPermissionDto();

        var permissions = new ArrayList<PermissionDto>();

        var permissionDto = new PermissionDto();
        permissionDto.operation = "CREATE";
        permissionDto.accessType = "NOT_VALID";

        permissions.add(permissionDto);

        collectionPermissionDto.permissions = permissions;
        collectionPermissionDto.collection = "Role";

        collectionPermission.add(collectionPermissionDto);

        request.collectionPermission = collectionPermission;

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

        assertEquals("The value NOT_VALID is not a valid access_type. Valid access_type values are: [ENTITY, NEVER, ALWAYS]", response.message);
    }

    @Test
    public void createRole() {

        var request= new RoleRequestDto();
        request.name = "role";

        var collectionPermission = new ArrayList<CollectionPermissionDto>();
        CollectionPermissionDto collectionPermissionDto = new CollectionPermissionDto();

        var permissions = new ArrayList<PermissionDto>();

        var permissionDto = new PermissionDto();
        permissionDto.operation = "CREATE";
        permissionDto.accessType = "ALWAYS";

        permissions.add(permissionDto);

        collectionPermissionDto.permissions = permissions;
        collectionPermissionDto.collection = "Role";

        collectionPermission.add(collectionPermissionDto);

        request.collectionPermission = collectionPermission;

        var response = createRole(request, "admin");

        var roleResponse = response
                .then()
                .assertThat()
                .statusCode(201)
                .extract()
                .as(RoleResponseDto.class);

        assertEquals(request.name, roleResponse.name);
    }

    @Test
    public void createRoleWithExistingName() {

        var request= new RoleRequestDto();
        request.name = "similar_role";

        var collectionPermission = new ArrayList<CollectionPermissionDto>();
        CollectionPermissionDto collectionPermissionDto = new CollectionPermissionDto();

        var permissions = new ArrayList<PermissionDto>();

        var permissionDto = new PermissionDto();
        permissionDto.operation = "CREATE";
        permissionDto.accessType = "ALWAYS";

        permissions.add(permissionDto);

        collectionPermissionDto.permissions = permissions;
        collectionPermissionDto.collection = "Role";

        collectionPermission.add(collectionPermissionDto);

        request.collectionPermission = collectionPermission;

        var response = createRole(request, "admin");

        response
                .then()
                .assertThat()
                .statusCode(201)
                .extract()
                .as(RoleResponseDto.class);

        var conflictResponse = createRole(request, "admin");

        var informativeResponse = conflictResponse
                .then()
                .assertThat()
                .statusCode(409)
                .extract()
                .as(InformativeResponse.class);


        assertEquals("There is already a role with this name : "+request.name, informativeResponse.message);
    }

    @Test
    public void deleteRoleNotAuthenticated() {


        var notAuthenticatedResponse = given()
                .auth()
                .oauth2("invalidToken")
                .delete("/{roleId}", "7dyebdheb7377e")
                .thenReturn();

        assertEquals(401, notAuthenticatedResponse.statusCode());
    }

    @Test
    public void deleteRoleNotFound() {


        var response = given()
                .auth()
                .oauth2(getAccessToken("admin"))
                .delete("/{roleId}", "7dyebdheb7377e")
                .then()
                .assertThat()
                .statusCode(404)
                .extract()
                .as(InformativeResponse.class);

        assertEquals("There is no Role with the following id: 7dyebdheb7377e", response.message);
    }

    @Test
    public void deleteRole() {

        var request= new RoleRequestDto();
        request.name = "role_deleted";

        var collectionPermission = new ArrayList<CollectionPermissionDto>();
        CollectionPermissionDto collectionPermissionDto = new CollectionPermissionDto();

        var permissions = new ArrayList<PermissionDto>();

        var permissionDto = new PermissionDto();
        permissionDto.operation = "CREATE";
        permissionDto.accessType = "ALWAYS";

        permissions.add(permissionDto);

        collectionPermissionDto.permissions = permissions;
        collectionPermissionDto.collection = "Role";

        collectionPermission.add(collectionPermissionDto);

        request.collectionPermission = collectionPermission;

        var response = createRole(request, "admin");

        var roleResponse = response
                .then()
                .assertThat()
                .statusCode(201)
                .extract()
                .as(RoleResponseDto.class);

        var deleteResponse = given()
                .auth()
                .oauth2(getAccessToken("admin"))
                .delete("/{roleId}", roleResponse.id)
                .then()
                .assertThat()
                .statusCode(200)
                .extract()
                .as(InformativeResponse.class);

        assertEquals("Role has been deleted successfully.", deleteResponse.message);
    }

    @Test
    public void fetchRoleNotAuthenticated() {

        var notAuthenticatedResponse = given()
                .auth()
                .oauth2("invalidToken")
                .get("/{id}", "507f1f77bcf86cd799439011")
                .thenReturn();

        assertEquals(401, notAuthenticatedResponse.statusCode());
    }

    @Test
    public void fetchRoleNotFound() {

        var response = given()
                .auth()
                .oauth2(getAccessToken("admin"))
                .get("/{id}", "507f1f77bcf86cd799439011")
                .then()
                .assertThat()
                .statusCode(404)
                .extract()
                .as(InformativeResponse.class);

        assertEquals("There is no Role with the following id: 507f1f77bcf86cd799439011", response.message);
    }

    @Test
    public void fetchRoleNotFoundNonHexId() {

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
    public void fetchRole() {

        var request= new RoleRequestDto();
        request.name = "role_retrieved";

        var collectionPermission = new ArrayList<CollectionPermissionDto>();
        CollectionPermissionDto collectionPermissionDto = new CollectionPermissionDto();

        var permissions = new ArrayList<PermissionDto>();

        var permissionDto = new PermissionDto();
        permissionDto.operation = "CREATE";
        permissionDto.accessType = "ALWAYS";

        permissions.add(permissionDto);

        collectionPermissionDto.permissions = permissions;
        collectionPermissionDto.collection = "Role";

        collectionPermission.add(collectionPermissionDto);

        request.collectionPermission = collectionPermission;

        var response = createRole(request, "admin");

        var role = response
                .then()
                .assertThat()
                .statusCode(201)
                .extract()
                .as(RoleResponseDto.class);


        var storedRole = given()
                .auth()
                .oauth2(getAccessToken("admin"))
                .get("/{id}", role.id)
                .then()
                .assertThat()
                .statusCode(200)
                .extract()
                .as(RoleResponseDto.class);

        assertEquals(role.id, storedRole.id);
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
}