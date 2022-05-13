package org.accounting.system;

import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import io.quarkus.test.keycloak.client.KeycloakTestClient;
import org.accounting.system.clients.ProviderClient;
import org.accounting.system.endpoints.ProviderEndpoint;
import org.accounting.system.repositories.ProviderRepository;
import org.accounting.system.schedulers.ProviderScheduler;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;


@QuarkusTest
@TestProfile(AccountingSystemTestProfile.class)
@TestHTTPEndpoint(ProviderEndpoint.class)
public class ProviderEndpointTest {

    @Inject
    @RestClient
    ProviderClient providerClient;

    @Inject
    ProviderRepository providerRepository;

    @Inject
    ProviderScheduler providerScheduler;

    KeycloakTestClient keycloakClient = new KeycloakTestClient();

    @BeforeEach
    public void setup() {
        providerScheduler.execute();
    }


    @Test
    public void crossCheckWithEoscProviders() {

        //We are going to get the total number of EOSC Providers
        var total = providerClient.getTotalNumberOfProviders();

        //We are going to fetch all available EOSC Providers
        var response = providerClient.getAll(total.total);

        assertEquals(response.results.size(), total.total);
    }

    @Test
    public void crossCheckWithDatabaseProviders() {

        //We are going to get the total number of EOSC Providers
        var total = providerClient.getTotalNumberOfProviders();

        //We are going to fetch all Providers from database
        var count = providerRepository.findAll().stream().count();

        assertEquals(count, total.total);
    }

    @Test
    public void fetchProvidersPaginationNotAuthenticated() {

        var notAuthenticatedResponse = given()
                .auth()
                .oauth2("invalidToken")
                .get()
                .thenReturn();

        assertEquals(401, notAuthenticatedResponse.statusCode());
    }

    @Test
    public void fetchProvidersPagination() {

        var paginateResponse = given()
                .auth()
                .oauth2(getAccessToken("admin"))
                .queryParam("size", 5)
                .get()
                .thenReturn();

        assertEquals(200, paginateResponse.statusCode());
    }

    @Test
    public void fetchProviderById() {

        var paginateResponse = given()
                .auth()
                .oauth2(getAccessToken("admin"))
                .queryParam("id", "sites")
                .get()
                .thenReturn();

        assertEquals(200, paginateResponse.statusCode());
    }


    protected String getAccessToken(String userName) {
        return keycloakClient.getAccessToken(userName);
    }
}
