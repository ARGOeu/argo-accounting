package org.accounting.system;

import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.accounting.system.dtos.InformativeResponse;
import org.accounting.system.dtos.authorization.AccessPermissionDto;
import org.accounting.system.dtos.authorization.CollectionAccessPermissionDto;
import org.accounting.system.dtos.authorization.request.RoleRequestDto;
import org.accounting.system.dtos.authorization.response.RoleResponseDto;
import org.accounting.system.endpoints.RoleEndpoint;
import org.accounting.system.enums.ApiMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.HashSet;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
@TestProfile(AccountingSystemTestProfile.class)
@TestHTTPEndpoint(RoleEndpoint.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class RoleEndpointAuthorizationTest extends PrepareTest {

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

        var notAuthorizedResponse = createRole(request, "inspector");

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