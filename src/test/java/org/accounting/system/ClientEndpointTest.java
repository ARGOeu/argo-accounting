package org.accounting.system;

import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import io.quarkus.test.security.TestSecurity;
import io.quarkus.test.security.oidc.OidcSecurity;
import io.quarkus.test.security.oidc.TokenIntrospection;
import io.quarkus.test.security.oidc.UserInfo;
import io.restassured.http.ContentType;
import org.accounting.system.dtos.authorization.request.AssignRoleRequestDto;
import org.accounting.system.dtos.authorization.request.DetachRoleRequestDto;
import org.accounting.system.dtos.client.ClientResponseDto;
import org.accounting.system.endpoints.ClientEndpoint;
import org.accounting.system.entities.client.Client;
import org.accounting.system.repositories.client.ClientRepository;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.util.Set;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
@TestProfile(AccountingSystemTestProfile.class)
@TestHTTPEndpoint(ClientEndpoint.class)
public class ClientEndpointTest {

    @Inject
    ClientRepository clientRepository;

    @Test
    public void createMetricDefinitionNotAuthenticated() {

        var notAuthenticatedResponse = given()
                .auth()
                .oauth2("invalidToken")
                .post()
                .thenReturn();

        assertEquals(401, notAuthenticatedResponse.statusCode());
    }

    @Test
    @TestSecurity(user = "test-user")
    @OidcSecurity(introspectionRequired = true,
            introspection = {
                    @TokenIntrospection(key = "voperson_id", value = "xyz@example.org"),
                    @TokenIntrospection(key = "sub", value = "xyz@example.org"),
            },
            userinfo = {
                    @UserInfo(key = "name", value = "John Doe"),
                    @UserInfo(key = "email", value = "john.doe@example.org")
            }
    )
    public void registerClient(){

        var response = given()
                .post()
                .then()
                .assertThat()
                .statusCode(200)
                .extract()
                .as(ClientResponseDto.class);

        Client registeredClient = clientRepository.findById(response.id);

        assertEquals(response.id, registeredClient.getId());
    }

    @Test
    @TestSecurity(user = "test-user")
    @OidcSecurity(introspectionRequired = true,
            introspection = {
                    @TokenIntrospection(key = "voperson_id", value = "xyz@example.org"),
                    @TokenIntrospection(key = "sub", value = "xyz@example.org"),
                    @TokenIntrospection(key = "groups", value = "system_admin")
            },
            userinfo = {
                    @UserInfo(key = "name", value = "John Doe"),
                    @UserInfo(key = "email", value = "john.doe@example.org")
            }
    )
    public void assignRolesToClient(){

        var client = given()
                .post()
                .then()
                .assertThat()
                .statusCode(200)
                .extract()
                .as(ClientResponseDto.class);

        var request = new AssignRoleRequestDto();

        request.roles = Set.of("metric_definition_admin", "metric_definition_inspector");

        var clientWithRoles = given()
                .body(request)
                .contentType(ContentType.JSON)
                .post("/{client_id}/assign-roles", client.id)
                .then()
                .assertThat()
                .statusCode(200)
                .extract()
                .as(ClientResponseDto.class);

        assertEquals(clientWithRoles.roles, request.roles);
    }

    @Test
    @TestSecurity(user = "test-user")
    @OidcSecurity(introspectionRequired = true,
            introspection = {
                    @TokenIntrospection(key = "voperson_id", value = "xyz@example.org"),
                    @TokenIntrospection(key = "sub", value = "xyz@example.org"),
                    @TokenIntrospection(key = "groups", value = "system_admin")
            },
            userinfo = {
                    @UserInfo(key = "name", value = "John Doe"),
                    @UserInfo(key = "email", value = "john.doe@example.org")
            }
    )
    public void detachOneRoleFromClient(){

        //assign roles to a registered client
        var client = given()
                .post()
                .then()
                .assertThat()
                .statusCode(200)
                .extract()
                .as(ClientResponseDto.class);

        var request = new AssignRoleRequestDto();

        request.roles = Set.of("metric_definition_admin", "metric_definition_inspector");

        var clientWithRoles = given()
                .body(request)
                .contentType(ContentType.JSON)
                .post("/{client_id}/assign-roles", client.id)
                .then()
                .assertThat()
                .statusCode(200)
                .extract()
                .as(ClientResponseDto.class);

        assertEquals(clientWithRoles.roles, request.roles);

        //detach roles from a registered client

        var detach = new DetachRoleRequestDto();

        detach.roles = Set.of("metric_definition_inspector");

        var clientWithDetachedRoles = given()
                .body(detach)
                .contentType(ContentType.JSON)
                .post("/{client_id}/detach-roles", client.id)
                .then()
                .assertThat()
                .statusCode(200)
                .extract()
                .as(ClientResponseDto.class);

        assertEquals(clientWithDetachedRoles.roles, Set.of("metric_definition_admin"));
    }

    @Test
    @TestSecurity(user = "test-user", roles = {"system_admin"})
    @OidcSecurity(introspectionRequired = true,
            introspection = {
                    @TokenIntrospection(key = "voperson_id", value = "xyz@example.org"),
                    @TokenIntrospection(key = "sub", value = "xyz@example.org"),
                    @TokenIntrospection(key = "groups", value = "system_admin")
            },
            userinfo = {
                    @UserInfo(key = "name", value = "John Doe"),
                    @UserInfo(key = "email", value = "john.doe@example.org")
            }
    )
    public void detachAllRolesFromClient(){

        //assign roles to a registered client
        var client = given()
                .post()
                .then()
                .assertThat()
                .statusCode(200)
                .extract()
                .as(ClientResponseDto.class);

        var request = new AssignRoleRequestDto();

        request.roles = Set.of("metric_definition_admin", "metric_definition_inspector");

        var clientWithRoles = given()
                .body(request)
                .contentType(ContentType.JSON)
                .post("/{client_id}/assign-roles", client.id)
                .then()
                .assertThat()
                .statusCode(200)
                .extract()
                .as(ClientResponseDto.class);

        assertEquals(clientWithRoles.roles, request.roles);

        //detach roles from a registered client

        var detach = new DetachRoleRequestDto();

        detach.roles = Set.of("metric_definition_inspector", "metric_definition_admin");

        var clientWithDetachedRoles = given()
                .body(detach)
                .contentType(ContentType.JSON)
                .post("/{client_id}/detach-roles", client.id)
                .then()
                .assertThat()
                .statusCode(200)
                .extract()
                .as(ClientResponseDto.class);

        assertEquals(clientWithDetachedRoles.roles.size(), 0);
    }

    @Test
    @TestSecurity(user = "test-user")
    @OidcSecurity(introspectionRequired = true,
            introspection = {
                    @TokenIntrospection(key = "voperson_id", value = "xyz@example.org"),
                    @TokenIntrospection(key = "sub", value = "xyz@example.org"),
                    @TokenIntrospection(key = "groups", value = "system_admin")
            },
            userinfo = {
                    @UserInfo(key = "name", value = "John Doe"),
                    @UserInfo(key = "email", value = "john.doe@example.org")
            }
    )
    public void detachWithUnassignedRole(){

        //assign roles to a registered client
        var client = given()
                .post()
                .then()
                .assertThat()
                .statusCode(200)
                .extract()
                .as(ClientResponseDto.class);

        var request = new AssignRoleRequestDto();

        request.roles = Set.of("metric_definition_admin", "metric_definition_inspector");

        var clientWithRoles = given()
                .body(request)
                .contentType(ContentType.JSON)
                .post("/{client_id}/assign-roles", client.id)
                .then()
                .assertThat()
                .statusCode(200)
                .extract()
                .as(ClientResponseDto.class);

        assertEquals(clientWithRoles.roles, request.roles);

        //detach roles from a registered client
        var detach = new DetachRoleRequestDto();

        //  metric_definition_editor has not been assigned to client
        detach.roles = Set.of("metric_definition_inspector", "metric_definition_admin", "metric_definition_editor");

        var clientWithDetachedRoles = given()
                .body(detach)
                .contentType(ContentType.JSON)
                .post("/{client_id}/detach-roles", client.id)
                .then()
                .assertThat()
                .statusCode(200)
                .extract()
                .as(ClientResponseDto.class);

        assertEquals(clientWithDetachedRoles.roles.size(), 0);
    }
}
