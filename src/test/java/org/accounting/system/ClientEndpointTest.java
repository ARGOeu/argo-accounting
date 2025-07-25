package org.accounting.system;

import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import io.quarkus.test.security.TestSecurity;
import io.quarkus.test.security.oidc.OidcSecurity;
import io.quarkus.test.security.oidc.TokenIntrospection;
import io.quarkus.test.security.oidc.UserInfo;
import jakarta.inject.Inject;
import org.accounting.system.dtos.client.ClientResponseDto;
import org.accounting.system.endpoints.ClientEndpoint;
import org.accounting.system.entities.client.Client;
import org.accounting.system.repositories.client.ClientAccessAlwaysRepository;
import org.accounting.system.repositories.client.ClientRepository;
import org.accounting.system.services.client.ClientService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.Set;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
@TestProfile(AccountingSystemTestProfile.class)
@TestHTTPEndpoint(ClientEndpoint.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ClientEndpointTest {

    @Inject
    ClientRepository clientRepository;

    @Inject
    ClientService clientService;

    @Inject
    ClientAccessAlwaysRepository clientAccessAlwaysRepository;

    @BeforeAll
    public void setup() {

        clientService.register("xyz@example.org", "John Doe", "john.doe@example.org", "http://localhost:58080/realm/quarkus");

        clientAccessAlwaysRepository.assignRolesToRegisteredClient("xyz@example.org", Set.of("collection_owner"));
    }

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
                    @TokenIntrospection(key = "iss", value = "http://localhost:58080/realms/quarkus")
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
}
