package org.accounting.system;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import org.accounting.system.dtos.InformativeResponse;
import org.accounting.system.dtos.provider.ProviderRequestDto;
import org.accounting.system.dtos.provider.ProviderResponseDto;
import org.accounting.system.dtos.provider.UpdateProviderRequestDto;
import org.accounting.system.endpoints.ProviderEndpoint;
import org.accounting.system.services.ProjectService;
import org.accounting.system.wiremock.ProjectWireMockServer;
import org.accounting.system.wiremock.ProviderWireMockServer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.Set;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;


@QuarkusTest
@TestProfile(AccountingSystemTestProfile.class)
@TestHTTPEndpoint(ProviderEndpoint.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@QuarkusTestResource(ProjectWireMockServer.class)
@QuarkusTestResource(ProviderWireMockServer.class)
public class ProviderEndpointTest extends PrepareTest {

    @Inject
    ProjectService projectService;

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
    public void createProviderNotAuthenticated() {

        var notAuthenticatedResponse = given()
                .auth()
                .oauth2("invalidToken")
                .contentType(ContentType.JSON)
                .post()
                .thenReturn();

        assertEquals(401, notAuthenticatedResponse.statusCode());
    }

    @Test
    public void createProviderRequestBodyIsEmpty() {

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
    public void createProviderCannotConsumeContentType() {

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
    public void createProviderIdIsEmpty() {

        var request= new ProviderRequestDto();
        request.name = "test-name";

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

        assertEquals("id may not be empty.", response.message);
    }

    @Test
    public void createProviderNameIsEmpty() {

        var request= new ProviderRequestDto();
        request.id = "test-provider-id";

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

        assertEquals("name may not be empty.", response.message);
    }

    @Test
    public void createProviderIdExists() {

        var request= new ProviderRequestDto();
        request.id = "sites";
        request.name = "test-name";

        var response = given()
                .auth()
                .oauth2(getAccessToken("admin"))
                .body(request)
                .contentType(ContentType.JSON)
                .post()
                .then()
                .assertThat()
                .statusCode(409)
                .extract()
                .as(InformativeResponse.class);

        assertEquals("There is a Provider with id: "+request.id, response.message);
    }

    @Test
    public void createProviderNameExists() {

        var request= new ProviderRequestDto();
        request.id = "test-id";
        request.name = "Swedish Infrastructure for Ecosystem Science";

        var response = given()
                .auth()
                .oauth2(getAccessToken("admin"))
                .body(request)
                .contentType(ContentType.JSON)
                .post()
                .then()
                .assertThat()
                .statusCode(409)
                .extract()
                .as(InformativeResponse.class);

        assertEquals("There is a Provider with id: "+request.id, response.message);
    }

    @Test
    public void createProvider() {

        var request= new ProviderRequestDto();
        request.id = "test-id";
        request.name = "test-name";
        request.abbreviation = "test-abbreviation";
        request.logo = "test-logo";
        request.website = "test-website";

        var response = createProvider(request, "admin");

        assertEquals(response.id, request.id);
        assertEquals(response.name, request.name);
        assertEquals(response.abbreviation, request.abbreviation);
        assertEquals(response.logo, request.logo);
        assertEquals(response.website, request.website);
    }

    @Test
    public void deleteProviderNotAuthenticated() {

        var notAuthenticatedResponse = given()
                .auth()
                .oauth2("invalidToken")
                .delete("/{providerId}", "sites")
                .thenReturn();

        assertEquals(401, notAuthenticatedResponse.statusCode());
    }

    @Test
    public void deleteProviderNotFound() {

        var response = given()
                .auth()
                .oauth2(getAccessToken("admin"))
                .delete("/{providerId}", "lalala")
                .then()
                .assertThat()
                .statusCode(404)
                .extract()
                .as(InformativeResponse.class);

        assertEquals("There is no Provider with the following id: lalala", response.message);
    }

    @Test
    public void deleteProviderProhibited() {

        var errorResponse = given()
                .auth()
                .oauth2(getAccessToken("admin"))
                .delete("/{providerId}", "sites")
                .then()
                .assertThat()
                .statusCode(403)
                .extract()
                .as(InformativeResponse.class);

        assertEquals("You cannot delete a Provider which derives from EOSC-Portal.", errorResponse.message);
    }

    @Test
    public void deleteProviderBelongsToProjectProhibited() {

        var request= new ProviderRequestDto();
        request.id = "delete-provider-id-prohibited";
        request.name = "delete-provider-name-prohibited";
        request.abbreviation = "delete-provider-abbreviation-prohibited";
        request.logo = "delete-provider-logo-prohibited";
        request.website = "delete-provider-website-prohibited";

        createProvider(request, "admin");

        projectService.associateProjectWithProviders("777536", Set.of("delete-provider-id-prohibited"));

        var response = given()
                .auth()
                .oauth2(getAccessToken("admin"))
                .delete("/{providerId}", request.id)
                .then()
                .assertThat()
                .statusCode(403)
                .extract()
                .as(InformativeResponse.class);

        assertEquals("You cannot delete a Provider which belongs to a Project.", response.message);
    }

    @Test
    public void deleteProvider() {

        var request= new ProviderRequestDto();
        request.id = "delete-provider-id";
        request.name = "delete-provider-name";
        request.abbreviation = "delete-provider-abbreviation";
        request.logo = "delete-provider-logo";
        request.website = "delete-provider-website";

        createProvider(request, "admin");

        var response = given()
                .auth()
                .oauth2(getAccessToken("admin"))
                .delete("/{providerId}", request.id)
                .then()
                .assertThat()
                .statusCode(200)
                .extract()
                .as(InformativeResponse.class);

        assertEquals("Provider has been deleted successfully.", response.message);
    }

    @Test
    public void updateProviderCannotConsumeContentType() {

        var request= new ProviderRequestDto();
        request.id = "update-provider-no-content-type-id";
        request.name = "update-provider-no-content-type-name";
        request.abbreviation = "update-provider-no-content-type-abbreviation";
        request.logo = "update-provider-no-content-type-logo";
        request.website = "update-provider-no-content-type-website";

        var provider = createProvider(request, "admin");

        var response = given()
                .auth()
                .oauth2(getAccessToken("admin"))
                .body(provider)
                .patch("/{id}", provider.id)
                .then()
                .assertThat()
                .statusCode(415)
                .extract()
                .as(InformativeResponse.class);

        assertEquals("Cannot consume content type.", response.message);
    }

    @Test
    public void updateProviderBelongsToProjectProhibited() {

        var request= new ProviderRequestDto();

        request.id = "update-provider-id-prohibited";
        request.name = "update-provider-name-prohibited";
        request.abbreviation = "update-provider-abbreviation-prohibited";
        request.logo = "update-provider-logo-prohibited";
        request.website = "update-provider-website-prohibited";

        createProvider(request, "admin");

        projectService.associateProjectWithProviders("777536", Set.of("update-provider-id-prohibited"));

        var requestForUpdating= new UpdateProviderRequestDto();

        requestForUpdating.id = "update-provider-id-prohibited-new";

        var response = given()
                .auth()
                .oauth2(getAccessToken("admin"))
                .contentType(ContentType.JSON)
                .body(requestForUpdating)
                .patch("/{providerId}", request.id)
                .then()
                .assertThat()
                .statusCode(403)
                .extract()
                .as(InformativeResponse.class);

        assertEquals("You cannot update a Provider which belongs to a Project.", response.message);
    }

    @Test
    public void updateProviderNotAuthenticated() {

        var notAuthenticatedResponse = given()
                .auth()
                .oauth2("invalidToken")
                .contentType(ContentType.JSON)
                .patch("/{id}", "556787878e-rrr")
                .thenReturn();

        assertEquals(401, notAuthenticatedResponse.statusCode());
    }

    @Test
    public void updateProviderNotFound() {

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

        assertEquals("There is no Provider with the following id: 556787878e-rrr", response.message);
    }

    @Test
    public void updateProviderRequestBodyIsEmpty() {

        var request= new ProviderRequestDto();
        request.id = "update-provider-request-body-is-empty-id";
        request.name = "update-provider-request-body-is-empty-name";
        request.abbreviation = "update-provider-request-body-is-empty-abbreviation";
        request.logo = "update-provider-request-body-is-empty-logo";
        request.website = "update-provider-request-body-is-empty-website";

        var provider = createProvider(request, "admin");

        var response = given()
                .auth()
                .oauth2(getAccessToken("admin"))
                .contentType(ContentType.JSON)
                .patch("/{id}", provider.id)
                .then()
                .assertThat()
                .statusCode(400)
                .extract()
                .as(InformativeResponse.class);

        assertEquals("The request body is empty.", response.message);
    }

    @Test
    public void updateProviderIdExists() {

        var request= new ProviderRequestDto();
        request.id = "update-provider-id-exists-id";
        request.name = "update-provider-id-exists-name";
        request.abbreviation = "update-provider-id-exists-abbreviation";
        request.logo = "update-provider-id-exists-logo";
        request.website = "update-provider-id-exists-website";

        var provider = createProvider(request, "admin");

        var requestForUpdating = new UpdateProviderRequestDto();
        requestForUpdating.id = "sites";

        var response = given()
                .auth()
                .oauth2(getAccessToken("admin"))
                .body(requestForUpdating)
                .contentType(ContentType.JSON)
                .patch("/{id}", provider.id)
                .then()
                .assertThat()
                .statusCode(409)
                .extract()
                .as(InformativeResponse.class);

        assertEquals("There is a Provider with id: sites", response.message);
    }

    @Test
    public void updateProviderNameExists() {

        var request= new ProviderRequestDto();
        request.id = "update-provider-name-exists-id";
        request.name = "update-provider-name-exists-name";
        request.abbreviation = "update-provider-name-exists-abbreviation";
        request.logo = "update-provider-name-exists-logo";
        request.website = "update-provider-name-exists-website";

        var provider = createProvider(request, "admin");

        var requestForUpdating = new UpdateProviderRequestDto();
        requestForUpdating.name = "Swedish Infrastructure for Ecosystem Science";

        var response = given()
                .auth()
                .oauth2(getAccessToken("admin"))
                .body(requestForUpdating)
                .contentType(ContentType.JSON)
                .patch("/{id}", provider.id)
                .then()
                .assertThat()
                .statusCode(409)
                .extract()
                .as(InformativeResponse.class);

        assertEquals("There is a Provider with name: Swedish Infrastructure for Ecosystem Science", response.message);
    }

    @Test
    public void updateProviderProhibited() {

        var request= new UpdateProviderRequestDto();
        request.id = "update-provider-prohibited-id";
        request.name = "update-provider-prohibited-name";
        request.abbreviation = "update-provider-prohibited-abbreviation";
        request.logo = "update-provider-prohibited-logo";
        request.website = "update-provider-prohibited-website";

        var errorResponse = given()
                .auth()
                .oauth2(getAccessToken("admin"))
                .body(request)
                .contentType(ContentType.JSON)
                .patch("/{id}", "sites")
                .then()
                .assertThat()
                .statusCode(403)
                .extract()
                .as(InformativeResponse.class);

        assertEquals("You cannot update a Provider which derives from EOSC-Portal.", errorResponse.message);
    }

    @Test
    public void updateProvider() {

        var request= new ProviderRequestDto();
        request.id = "update-provider-id";
        request.name = "update-providers-name";
        request.abbreviation = "update-provider-abbreviation";
        request.logo = "update-provider-logo";
        request.website = "update-provider-website";

        var provider = createProvider(request, "admin");

        var requestForUpdating= new UpdateProviderRequestDto();

        requestForUpdating.id = "update-provider-id-new";
        requestForUpdating.name = "update-providers-name-new";
        requestForUpdating.abbreviation = "update-provider-abbreviation-new";
        requestForUpdating.logo = "update-provider-logo-new";
        requestForUpdating.website = "update-provider-website-new";


        var updatedProvider = given()
                .auth()
                .oauth2(getAccessToken("admin"))
                .body(requestForUpdating)
                .contentType(ContentType.JSON)
                .patch("/{id}", provider.id)
                .then()
                .assertThat()
                .statusCode(200)
                .extract()
                .as(ProviderResponseDto.class);

        assertEquals(requestForUpdating.id, updatedProvider.id);
        assertEquals(requestForUpdating.name, updatedProvider.name);
        assertEquals(requestForUpdating.abbreviation, updatedProvider.abbreviation);
        assertEquals(requestForUpdating.logo, updatedProvider.logo);
        assertEquals(requestForUpdating.website, updatedProvider.website);
    }

    private ProviderResponseDto createProvider(ProviderRequestDto request, String user){

        return given()
                .auth()
                .oauth2(getAccessToken(user))
                .body(request)
                .contentType(ContentType.JSON)
                .post()
                .then()
                .assertThat()
                .statusCode(201)
                .extract()
                .as(ProviderResponseDto.class);
    }

    @Test
    public void fetchProviderNotAuthenticated() {

        var notAuthenticatedResponse = given()
                .auth()
                .oauth2("invalidToken")
                .get("/{id}", "sites")
                .thenReturn();

        assertEquals(401, notAuthenticatedResponse.statusCode());
    }

    @Test
    public void fetchProviderNotFound() {

        var response = given()
                .auth()
                .oauth2(getAccessToken("admin"))
                .get("/{id}", "lalala")
                .then()
                .assertThat()
                .statusCode(404)
                .extract()
                .as(InformativeResponse.class);

        assertEquals("There is no Provider with the following id: lalala", response.message);
    }

    @Test
    public void fetchProvider() {

        var storedProvider = given()
                .auth()
                .oauth2(getAccessToken("admin"))
                .get("/{id}", "sites")
                .then()
                .assertThat()
                .statusCode(200)
                .extract()
                .as(ProviderResponseDto.class);

        assertEquals("sites", storedProvider.id);
        assertEquals("Swedish Infrastructure for Ecosystem Science", storedProvider.name);
        assertEquals("SITES", storedProvider.abbreviation);
        assertEquals("https://www.fieldsites.se/en-GB", storedProvider.website);
        assertEquals("https://dst15js82dk7j.cloudfront.net/231546/95187636-P5q11.png", storedProvider.logo);
    }
}