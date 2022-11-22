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
import org.accounting.system.dtos.authorization.update.UpdateAccessPermissionDto;
import org.accounting.system.dtos.authorization.update.UpdateCollectionAccessPermissionDto;
import org.accounting.system.dtos.authorization.update.UpdateRoleRequestDto;
import org.accounting.system.endpoints.RoleEndpoint;
import org.accounting.system.enums.AccessType;
import org.accounting.system.enums.Collection;
import org.accounting.system.enums.Operation;
import org.accounting.system.repositories.authorization.RoleRepository;
import org.accounting.system.services.authorization.RoleService;
import org.accounting.system.util.Utility;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.util.HashSet;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;
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

        var list = new HashSet<CollectionAccessPermissionDto>();
        CollectionAccessPermissionDto collectionAccessPermissionDto = new CollectionAccessPermissionDto();
        list.add(collectionAccessPermissionDto);

        request.collectionsAccessPermissions = list;

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

        assertEquals("collections_access_permissions should have at least one entry.", response.message);
    }

    @Test
    public void createRoleCollectionPermissionListShouldHaveOneEntry() {

        var request= new RoleRequestDto();
        request.name = "role";
        request.collectionsAccessPermissions = new HashSet<>();

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

        assertEquals("collections_access_permissions should have at least one entry.", response.message);
    }

    @Test
    public void createRolePermissionListIsEmpty() {

        var request= new RoleRequestDto();
        request.name = "role";

        var collectionPermission = new HashSet<CollectionAccessPermissionDto>();
        CollectionAccessPermissionDto collectionAccessPermissionDto = new CollectionAccessPermissionDto();
        collectionAccessPermissionDto.collection = "Role";
        collectionPermission.add(collectionAccessPermissionDto);

        request.collectionsAccessPermissions = collectionPermission;

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

        assertEquals("access_permissions list should have at least one entry.", response.message);
    }

    @Test
    public void createRolePermissionListShouldHaveOneEntry() {

        var request= new RoleRequestDto();
        request.name = "role";

        var collectionPermission = new HashSet<CollectionAccessPermissionDto>();
        CollectionAccessPermissionDto collectionAccessPermissionDto = new CollectionAccessPermissionDto();
        collectionAccessPermissionDto.collection = "Role";

        var permissions = new HashSet<AccessPermissionDto>();

        collectionAccessPermissionDto.accessPermissions = permissions;

        collectionPermission.add(collectionAccessPermissionDto);

        request.collectionsAccessPermissions = collectionPermission;

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

        assertEquals("access_permissions list should have at least one entry.", response.message);
    }

    @Test
    public void createRoleCollectionIsEmpty() {

        var request= new RoleRequestDto();
        request.name = "role";

        var collectionPermission = new HashSet<CollectionAccessPermissionDto>();
        CollectionAccessPermissionDto collectionAccessPermissionDto = new CollectionAccessPermissionDto();

        var permissions = new HashSet<AccessPermissionDto>();

        var permissionDto = new AccessPermissionDto();

        permissions.add(permissionDto);

        collectionAccessPermissionDto.accessPermissions = permissions;

        collectionPermission.add(collectionAccessPermissionDto);

        request.collectionsAccessPermissions = collectionPermission;

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

        var collectionPermission = new HashSet<CollectionAccessPermissionDto>();
        CollectionAccessPermissionDto collectionAccessPermissionDto = new CollectionAccessPermissionDto();

        var permissions = new HashSet<AccessPermissionDto>();

        var permissionDto = new AccessPermissionDto();

        permissions.add(permissionDto);

        collectionAccessPermissionDto.accessPermissions = permissions;
        collectionAccessPermissionDto.collection = "Test";

        collectionPermission.add(collectionAccessPermissionDto);

        request.collectionsAccessPermissions = collectionPermission;

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

        assertEquals("The value Test is not a valid collection. Valid collection values are: "+ Utility.getNamesSet(Collection.class), response.message);
    }

    @Test
    public void createRoleOperationIsEmpty() {

        var request= new RoleRequestDto();
        request.name = "role";

        var collectionPermission = new HashSet<CollectionAccessPermissionDto>();
        CollectionAccessPermissionDto collectionAccessPermissionDto = new CollectionAccessPermissionDto();

        var permissions = new HashSet<AccessPermissionDto>();

        var permissionDto = new AccessPermissionDto();
        permissionDto.accessType = "ALWAYS";

        permissions.add(permissionDto);

        collectionAccessPermissionDto.accessPermissions = permissions;
        collectionAccessPermissionDto.collection = "Role";

        collectionPermission.add(collectionAccessPermissionDto);

        request.collectionsAccessPermissions = collectionPermission;

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

        var collectionPermission = new HashSet<CollectionAccessPermissionDto>();
        CollectionAccessPermissionDto collectionAccessPermissionDto = new CollectionAccessPermissionDto();

        var permissions = new HashSet<AccessPermissionDto>();

        var permissionDto = new AccessPermissionDto();
        permissionDto.operation = "NOT_VALID";
        permissionDto.accessType = "ALWAYS";

        permissions.add(permissionDto);

        collectionAccessPermissionDto.accessPermissions = permissions;
        collectionAccessPermissionDto.collection = "Role";

        collectionPermission.add(collectionAccessPermissionDto);

        request.collectionsAccessPermissions = collectionPermission;

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

        assertEquals("The value NOT_VALID is not a valid operation. Valid operation values are: "+Utility.getNamesSet(Operation.class), response.message);
    }

    @Test
    public void createRoleAccessTypeIsEmpty() {

        var request= new RoleRequestDto();
        request.name = "role";

        var collectionPermission = new HashSet<CollectionAccessPermissionDto>();
        CollectionAccessPermissionDto collectionAccessPermissionDto = new CollectionAccessPermissionDto();

        var permissions = new HashSet<AccessPermissionDto>();

        var permissionDto = new AccessPermissionDto();
        permissionDto.operation = "CREATE";

        permissions.add(permissionDto);

        collectionAccessPermissionDto.accessPermissions = permissions;
        collectionAccessPermissionDto.collection = "Role";

        collectionPermission.add(collectionAccessPermissionDto);

        request.collectionsAccessPermissions = collectionPermission;

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

        var collectionPermission = new HashSet<CollectionAccessPermissionDto>();
        CollectionAccessPermissionDto collectionAccessPermissionDto = new CollectionAccessPermissionDto();

        var permissions = new HashSet<AccessPermissionDto>();

        var permissionDto = new AccessPermissionDto();
        permissionDto.operation = "CREATE";
        permissionDto.accessType = "NOT_VALID";

        permissions.add(permissionDto);

        collectionAccessPermissionDto.accessPermissions = permissions;
        collectionAccessPermissionDto.collection = "Role";

        collectionPermission.add(collectionAccessPermissionDto);

        request.collectionsAccessPermissions = collectionPermission;

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

        assertEquals("The value NOT_VALID is not a valid access_type. Valid access_type values are: "+Utility.getNamesSet(AccessType.class), response.message);
    }

    @Test
    public void createRole() {

        var request= new RoleRequestDto();
        request.name = "role";

        var collectionPermission = new HashSet<CollectionAccessPermissionDto>();
        CollectionAccessPermissionDto collectionAccessPermissionDto = new CollectionAccessPermissionDto();

        var permissions = new HashSet<AccessPermissionDto>();

        var permissionDto = new AccessPermissionDto();
        permissionDto.operation = "CREATE";
        permissionDto.accessType = "ALWAYS";

        permissions.add(permissionDto);

        collectionAccessPermissionDto.accessPermissions = permissions;
        collectionAccessPermissionDto.collection = "Role";

        collectionPermission.add(collectionAccessPermissionDto);

        request.collectionsAccessPermissions = collectionPermission;

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
    public void createRoleNotSupportedOperation() {

        var request= new RoleRequestDto();
        request.name = "role";

        var collectionPermission = new HashSet<CollectionAccessPermissionDto>();
        CollectionAccessPermissionDto collectionAccessPermissionDto = new CollectionAccessPermissionDto();

        var permissions = new HashSet<AccessPermissionDto>();

        var permissionDto = new AccessPermissionDto();
        permissionDto.operation = "CREATE";
        permissionDto.accessType = "ALWAYS";

        permissions.add(permissionDto);

        collectionAccessPermissionDto.accessPermissions = permissions;
        collectionAccessPermissionDto.collection = "Client";

        collectionPermission.add(collectionAccessPermissionDto);

        request.collectionsAccessPermissions = collectionPermission;

        var response = createRole(request, "admin");

        var informativeResponse = response
                .then()
                .assertThat()
                .statusCode(400)
                .extract()
                .as(InformativeResponse.class);

        assertEquals(String.format("The collection Client doesn't support one or more of given operations"), informativeResponse.message);
    }

    @Test
    public void createRoleWithExistingName() {

        var request= new RoleRequestDto();
        request.name = "similar_role";

        var collectionPermission = new HashSet<CollectionAccessPermissionDto>();
        CollectionAccessPermissionDto collectionAccessPermissionDto = new CollectionAccessPermissionDto();

        var permissions = new HashSet<AccessPermissionDto>();

        var permissionDto = new AccessPermissionDto();
        permissionDto.operation = "CREATE";
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

        var collectionPermission = new HashSet<CollectionAccessPermissionDto>();
        CollectionAccessPermissionDto collectionAccessPermissionDto = new CollectionAccessPermissionDto();

        var permissions = new HashSet<AccessPermissionDto>();

        var permissionDto = new AccessPermissionDto();
        permissionDto.operation = "CREATE";
        permissionDto.accessType = "ALWAYS";

        permissions.add(permissionDto);

        collectionAccessPermissionDto.accessPermissions = permissions;
        collectionAccessPermissionDto.collection = "Role";

        collectionPermission.add(collectionAccessPermissionDto);

        request.collectionsAccessPermissions = collectionPermission;

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

        var collectionPermission = new HashSet<CollectionAccessPermissionDto>();
        CollectionAccessPermissionDto collectionAccessPermissionDto = new CollectionAccessPermissionDto();

        var permissions = new HashSet<AccessPermissionDto>();

        var permissionDto = new AccessPermissionDto();
        permissionDto.operation = "CREATE";
        permissionDto.accessType = "ALWAYS";

        permissions.add(permissionDto);

        collectionAccessPermissionDto.accessPermissions = permissions;
        collectionAccessPermissionDto.collection = "Role";

        collectionPermission.add(collectionAccessPermissionDto);

        request.collectionsAccessPermissions = collectionPermission;

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

    @Test
    public void updateRoleCannotConsumeContentType() {

        var request= new RoleRequestDto();
        request.name = "role_update_cannot_consume_content_type";

        var collectionPermission = new HashSet<CollectionAccessPermissionDto>();
        CollectionAccessPermissionDto collectionAccessPermissionDto = new CollectionAccessPermissionDto();

        var permissions = new HashSet<AccessPermissionDto>();

        var permissionDto = new AccessPermissionDto();
        permissionDto.operation = "CREATE";
        permissionDto.accessType = "ALWAYS";

        permissions.add(permissionDto);

        collectionAccessPermissionDto.accessPermissions = permissions;
        collectionAccessPermissionDto.collection = "Role";

        collectionPermission.add(collectionAccessPermissionDto);

        request.collectionsAccessPermissions = collectionPermission;

        var response = createRole(request, "admin");

        var role = response
                .then()
                .assertThat()
                .statusCode(201)
                .extract()
                .as(RoleResponseDto.class);


        var error = given()
                .auth()
                .oauth2(getAccessToken("admin"))
                .body(request)
                .patch("/{id}", role.id)
                .then()
                .assertThat()
                .statusCode(415)
                .extract()
                .as(InformativeResponse.class);

        assertEquals("Cannot consume content type.", error.message);
    }

    @Test
    public void updateRoleNotAuthenticated() {

        var notAuthenticatedResponse = given()
                .auth()
                .oauth2("invalidToken")
                .contentType(ContentType.JSON)
                .patch("/{id}", "556787878e-rrr")
                .thenReturn();

        assertEquals(401, notAuthenticatedResponse.statusCode());
    }

    @Test
    public void updateRoleNotFound() {

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

        assertEquals("There is no Role with the following id: 556787878e-rrr", response.message);
    }

    @Test
    public void updateRoleNameAndDescription() {

        var request= new RoleRequestDto();
        request.name = "role_update_name";
        request.description = "description";

        var collectionPermission = new HashSet<CollectionAccessPermissionDto>();
        CollectionAccessPermissionDto collectionAccessPermissionDto = new CollectionAccessPermissionDto();

        var permissions = new HashSet<AccessPermissionDto>();

        var permissionDto = new AccessPermissionDto();
        permissionDto.operation = "CREATE";
        permissionDto.accessType = "ALWAYS";

        permissions.add(permissionDto);

        collectionAccessPermissionDto.accessPermissions = permissions;
        collectionAccessPermissionDto.collection = "Role";

        collectionPermission.add(collectionAccessPermissionDto);

        request.collectionsAccessPermissions = collectionPermission;

        var response = createRole(request, "admin");

        var role = response
                .then()
                .assertThat()
                .statusCode(201)
                .extract()
                .as(RoleResponseDto.class);

        var updateRoleRequestDto = new UpdateRoleRequestDto();

        updateRoleRequestDto.name = "role_update_name_changed";
        updateRoleRequestDto.description = "role_update_name_changed";

        var updateResponse = given()
                .auth()
                .oauth2(getAccessToken("admin"))
                .body(updateRoleRequestDto)
                .contentType(ContentType.JSON)
                .patch("/{id}", role.id)
                .then()
                .assertThat()
                .statusCode(200)
                .body("id", is(role.id))
                .body("name", is(updateRoleRequestDto.name))
                .body("description", is(updateRoleRequestDto.description))
                .extract()
                .as(RoleResponseDto.class);

        assertEquals(updateRoleRequestDto.name, updateResponse.name);
    }

    @Test
    public void updateRolePermissions() {

        var request= new RoleRequestDto();
        request.name = "role_update_collection";
        request.description = "description";

        var collectionPermission = new HashSet<CollectionAccessPermissionDto>();
        CollectionAccessPermissionDto collectionAccessPermissionDto = new CollectionAccessPermissionDto();

        var permissions = new HashSet<AccessPermissionDto>();

        var permissionDto = new AccessPermissionDto();
        permissionDto.operation = "CREATE";
        permissionDto.accessType = "ALWAYS";

        permissions.add(permissionDto);

        collectionAccessPermissionDto.accessPermissions = permissions;
        collectionAccessPermissionDto.collection = "Role";

        collectionPermission.add(collectionAccessPermissionDto);

        request.collectionsAccessPermissions = collectionPermission;

        var response = createRole(request, "admin");

        var role = response
                .then()
                .assertThat()
                .statusCode(201)
                .extract()
                .as(RoleResponseDto.class);

        var updateRoleRequestDto = new UpdateRoleRequestDto();

        var updateCollectionPermission = new HashSet<UpdateCollectionAccessPermissionDto>();
        UpdateCollectionAccessPermissionDto updateCollectionAccessPermissionDto = new UpdateCollectionAccessPermissionDto();

        var updatePermissions = new HashSet<UpdateAccessPermissionDto>();

        var updatePermissionDto = new UpdateAccessPermissionDto();
        updatePermissionDto.operation = "UPDATE";
        updatePermissionDto.accessType = "ALWAYS";

        updatePermissions.add(updatePermissionDto);

        updateCollectionAccessPermissionDto.accessPermissions = updatePermissions;
        updateCollectionAccessPermissionDto.collection = "Metric";

        updateCollectionPermission.add(updateCollectionAccessPermissionDto);

        updateRoleRequestDto.collectionsAccessPermissions = updateCollectionPermission;

        var updateResponse = given()
                .auth()
                .oauth2(getAccessToken("admin"))
                .body(updateRoleRequestDto)
                .contentType(ContentType.JSON)
                .patch("/{id}", role.id)
                .then()
                .assertThat()
                .statusCode(200)
                .extract()
                .as(RoleResponseDto.class);

        assertEquals("Metric", updateResponse.collectionsAccessPermissions.stream().findAny().get().collection);
    }

    @Test
    public void updateRoleNotValidCollection() {

        var request= new RoleRequestDto();
        request.name = "role_update_not_valid_collection";
        request.description = "description";

        var collectionPermission = new HashSet<CollectionAccessPermissionDto>();
        CollectionAccessPermissionDto collectionAccessPermissionDto = new CollectionAccessPermissionDto();

        var permissions = new HashSet<AccessPermissionDto>();

        var permissionDto = new AccessPermissionDto();
        permissionDto.operation = "CREATE";
        permissionDto.accessType = "ALWAYS";

        permissions.add(permissionDto);

        collectionAccessPermissionDto.accessPermissions = permissions;
        collectionAccessPermissionDto.collection = "Role";

        collectionPermission.add(collectionAccessPermissionDto);

        request.collectionsAccessPermissions = collectionPermission;

        var response = createRole(request, "admin");

        var role = response
                .then()
                .assertThat()
                .statusCode(201)
                .extract()
                .as(RoleResponseDto.class);

        var updateRoleRequestDto = new UpdateRoleRequestDto();

        var updateCollectionPermission = new HashSet<UpdateCollectionAccessPermissionDto>();
        UpdateCollectionAccessPermissionDto updateCollectionAccessPermissionDto = new UpdateCollectionAccessPermissionDto();

        var updatePermissions = new HashSet<UpdateAccessPermissionDto>();

        var updatePermissionDto = new UpdateAccessPermissionDto();
        updatePermissionDto.operation = "UPDATE";
        updatePermissionDto.accessType = "ALWAYS";

        updatePermissions.add(updatePermissionDto);

        updateCollectionAccessPermissionDto.accessPermissions = updatePermissions;
        updateCollectionAccessPermissionDto.collection = "Test";

        updateCollectionPermission.add(updateCollectionAccessPermissionDto);

        updateRoleRequestDto.collectionsAccessPermissions = updateCollectionPermission;

        var informativeResponse = given()
                .auth()
                .oauth2(getAccessToken("admin"))
                .body(updateRoleRequestDto)
                .contentType(ContentType.JSON)
                .patch("/{id}", role.id)
                .then()
                .assertThat()
                .statusCode(400)
                .extract()
                .as(InformativeResponse.class);

        assertEquals("The value Test is not a valid collection. Valid collection values are: "+Utility.getNamesSet(Collection.class), informativeResponse.message);
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