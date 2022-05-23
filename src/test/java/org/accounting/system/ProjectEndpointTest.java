package org.accounting.system;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import io.quarkus.test.keycloak.client.KeycloakTestClient;
import org.accounting.system.clients.ProjectClient;
import org.accounting.system.clients.responses.openaire.OpenAireProject;
import org.accounting.system.dtos.InformativeResponse;
import org.accounting.system.dtos.project.ProjectResponseDto;
import org.accounting.system.endpoints.ProjectEndpoint;
import org.accounting.system.entities.Project;
import org.accounting.system.mappers.ProjectMapper;
import org.accounting.system.wiremock.ProjectWireMockServer;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;


@QuarkusTest
@TestProfile(AccountingSystemTestProfile.class)
@TestHTTPEndpoint(ProjectEndpoint.class)
@QuarkusTestResource(ProjectWireMockServer.class)
public class ProjectEndpointTest {

    @Inject
    @RestClient
    ProjectClient projectClient;

    KeycloakTestClient keycloakClient = new KeycloakTestClient();

    @Test
    public void retrieveOpenAireResponse() {

        //We are going to retrieve the EOSC-hub project from OpenAire API
        OpenAireProject project = projectClient.getById("777536","json");

        assertEquals(project.response.results.result.get(0).metadata.entity.project.code.value, "777536");
        assertEquals(project.response.results.result.get(0).metadata.entity.project.acronym.value, "EOSC-hub");
        assertEquals(project.response.results.result.get(0).metadata.entity.project.title.value, "Integrating and managing services for the European Open Science Cloud");
        assertEquals(project.response.results.result.get(0).metadata.entity.project.startDate.value, "2018-01-01");
        assertEquals(project.response.results.result.get(0).metadata.entity.project.endDate.value, "2021-03-31");
        assertEquals(project.response.results.result.get(0).metadata.entity.project.callIdentifier.value, "H2020-EINFRA-2017");
    }

    @Test
    public void openAireResponseToProject() {

        //We are going to retrieve the EOSC-hub project from OpenAire APIx
        OpenAireProject response = projectClient.getById("777536","json");

        Project project = ProjectMapper.INSTANCE.openAireResponseToProject(response);

        assertEquals(project.getId(), "777536");
        assertEquals(project.getAcronym(), "EOSC-hub");
        assertEquals(project.getTitle(), "Integrating and managing services for the European Open Science Cloud");
        assertEquals(project.getStartDate(), "2018-01-01");
        assertEquals(project.getEndDate(), "2021-03-31");
        assertEquals(project.getCallIdentifier(), "H2020-EINFRA-2017");
    }

    @Test
    public void fetchProjectNotAuthenticated() {

        var notAuthenticatedResponse = given()
                .auth()
                .oauth2("invalidToken")
                .get("/{id}", "447535")
                .thenReturn();

        assertEquals(401, notAuthenticatedResponse.statusCode());
    }

    @Test
    public void fetchProjectById(){

        var project = given()
                .auth()
                .oauth2(getAccessToken("admin"))
                .get("/{id}", "777536")
                .then()
                .assertThat()
                .statusCode(200)
                .extract()
                .as(ProjectResponseDto.class);

        assertEquals(project.id, "777536");
        assertEquals(project.acronym, "EOSC-hub");
        assertEquals(project.title, "Integrating and managing services for the European Open Science Cloud");
        assertEquals(project.startDate, "2018-01-01");
        assertEquals(project.endDate, "2021-03-31");
        assertEquals(project.callIdentifier, "H2020-EINFRA-2017");
    }

    @Test
    public void fetchProjectByIdNotFound(){

        var response = given()
                .auth()
                .oauth2(getAccessToken("admin"))
                .get("/{id}", "lalala")
                .then()
                .assertThat()
                .statusCode(404)
                .extract()
                .as(InformativeResponse.class);

        assertEquals(response.message, "Project with id {lalala} not found.");
    }

    protected String getAccessToken(String userName) {
        return keycloakClient.getAccessToken(userName);
    }
}
