package org.accounting.system;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import org.accounting.system.clients.ProjectClient;
import org.accounting.system.clients.responses.openaire.OpenAireProject;
import org.accounting.system.dtos.installation.InstallationRequestDto;
import org.accounting.system.dtos.installation.InstallationResponseDto;
import org.accounting.system.dtos.metric.MetricRequestDto;
import org.accounting.system.dtos.metric.MetricResponseDto;
import org.accounting.system.dtos.metricdefinition.MetricDefinitionRequestDto;
import org.accounting.system.dtos.metricdefinition.MetricDefinitionResponseDto;
import org.accounting.system.endpoints.ProjectEndpoint;
import org.accounting.system.wiremock.ProjectWireMockServer;
import org.accounting.system.wiremock.ProviderWireMockServer;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.Set;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;


@QuarkusTest
@TestProfile(AccountingSystemTestProfile.class)
@TestHTTPEndpoint(ProjectEndpoint.class)
@QuarkusTestResource(ProjectWireMockServer.class)
@QuarkusTestResource(ProviderWireMockServer.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ProjectEndpointTest extends PrepareTest {

    @Inject
    @RestClient
    ProjectClient projectClient;

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
    public void saveProjectNotAuthenticated() {

        var notAuthenticatedResponse = given()
                .auth()
                .oauth2("invalidToken")
                .post("/{id}", "447535")
                .thenReturn();

        assertEquals(401, notAuthenticatedResponse.statusCode());
    }

    @Test
    public void assignMetric(){

        projectRepository.associateProjectWithProviders("777536", Set.of("grnet"));

        //Registering an installation
        var requestForMetricDefinition = new MetricDefinitionRequestDto();

        requestForMetricDefinition.metricName = "metric";
        requestForMetricDefinition.metricDescription = "description";
        requestForMetricDefinition.unitType = "TB";
        requestForMetricDefinition.metricType = "aggregated";

        var metricDefinitionResponse = createMetricDefinition(requestForMetricDefinition, "admin");

        var request= new InstallationRequestDto();

        request.project = "777536";
        request.organisation = "grnet";
        request.infrastructure = "okeanos-knossos";
        request.installation = "SECOND";
        request.unitOfAccess = metricDefinitionResponse.id;

        var installation = createInstallation(request, "admin");

        var metric = new MetricRequestDto();
        metric.start = "2022-01-05T09:13:07Z";
        metric.end = "2022-01-05T09:14:07Z";
        metric.value = 10.8;
        metric.metricDefinitionId = metricDefinitionResponse.id;

        // creating and assigning a new Metric
        var assignedMetric = given()
                .auth()
                .oauth2(getAccessToken("admin"))
                .basePath("accounting-system/installations")
                .body(metric)
                .contentType(ContentType.JSON)
                .post("/{installationId}/metrics", installation.id)
                .then()
                .assertThat()
                .statusCode(201)
                .extract()
                .as(MetricResponseDto.class);

        assertEquals(metric.start, assignedMetric.start.toString());
        assertEquals(metric.end, assignedMetric.end.toString());
        assertEquals(metric.value, assignedMetric.value);
    }

    private InstallationResponseDto createInstallation(InstallationRequestDto request, String user){

        return given()
                .auth()
                .oauth2(getAccessToken(user))
                .basePath("accounting-system/installations")
                .body(request)
                .contentType(ContentType.JSON)
                .post()
                .then()
                .assertThat()
                .statusCode(201)
                .extract()
                .as(InstallationResponseDto.class);
    }

    private MetricDefinitionResponseDto createMetricDefinition(MetricDefinitionRequestDto request, String user){

        return given()
                .auth()
                .oauth2(getAccessToken(user))
                .basePath("accounting-system/metric-definitions")
                .body(request)
                .contentType(ContentType.JSON)
                .post()
                .then()
                .assertThat()
                .statusCode(201)
                .extract()
                .as(MetricDefinitionResponseDto.class);
    }
}
