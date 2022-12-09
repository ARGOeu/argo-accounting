package org.accounting.system;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import io.quarkus.test.keycloak.client.KeycloakTestClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
@TestProfile(AccountingSystemTestProfile.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SearchTest {

    KeycloakTestClient keycloakClient = new KeycloakTestClient();

    @Test
    public void operands(){

        var operands = given()
                .auth()
                .oauth2(getAccessToken("admin"))
                .basePath("accounting-system/operands")
                .get()
                .then()
                .assertThat()
                .statusCode(200)
                .extract()
                .as(List.class);

        assertEquals(6, operands.size());
    }

    @Test
    public void operators(){

        var operands = given()
                .auth()
                .oauth2(getAccessToken("admin"))
                .basePath("accounting-system/operators")
                .get()
                .then()
                .assertThat()
                .statusCode(200)
                .extract()
                .as(List.class);

        assertEquals(2, operands.size());
    }

    protected String getAccessToken(String userName) {
        return keycloakClient.getAccessToken(userName);
    }

}
