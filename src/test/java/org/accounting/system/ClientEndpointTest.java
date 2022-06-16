package org.accounting.system;

import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import io.quarkus.test.keycloak.client.KeycloakTestClient;
import io.quarkus.test.security.TestSecurity;
import io.quarkus.test.security.oidc.OidcSecurity;
import io.quarkus.test.security.oidc.TokenIntrospection;
import io.quarkus.test.security.oidc.UserInfo;
import org.accounting.system.dtos.client.ClientResponseDto;
import org.accounting.system.endpoints.ClientEndpoint;
import org.accounting.system.entities.client.Client;
import org.accounting.system.repositories.client.ClientRepository;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
@TestProfile(AccountingSystemTestProfile.class)
@TestHTTPEndpoint(ClientEndpoint.class)
public class ClientEndpointTest {

    @Inject
    ClientRepository clientRepository;

    KeycloakTestClient keycloakClient = new KeycloakTestClient();

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
    public void registerUser(){

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

    protected String getAccessToken(String userName) {
        return keycloakClient.getAccessToken(userName);
    }
}
