package org.accounting.system;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import io.quarkus.test.keycloak.client.KeycloakTestClient;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import org.accounting.system.clients.ProviderClient;
import org.accounting.system.clients.responses.eoscportal.Response;
import org.accounting.system.clients.responses.eoscportal.Total;
import org.accounting.system.dtos.InformativeResponse;
import org.accounting.system.dtos.admin.ProjectRegistrationRequest;
import org.accounting.system.dtos.installation.InstallationRequestDto;
import org.accounting.system.dtos.installation.InstallationResponseDto;
import org.accounting.system.dtos.metric.MetricRequestDto;
import org.accounting.system.dtos.metricdefinition.MetricDefinitionRequestDto;
import org.accounting.system.dtos.metricdefinition.MetricDefinitionResponseDto;
import org.accounting.system.endpoints.InstallationEndpoint;
import org.accounting.system.mappers.ProviderMapper;
import org.accounting.system.repositories.metric.MetricRepository;
import org.accounting.system.repositories.metricdefinition.MetricDefinitionRepository;
import org.accounting.system.repositories.project.ProjectRepository;
import org.accounting.system.repositories.provider.ProviderRepository;
import org.accounting.system.wiremock.KeycloakWireMockServer;
import org.accounting.system.wiremock.ProjectWireMockServer;
import org.accounting.system.wiremock.ProviderWireMockServer;
import org.accounting.system.wiremock.TokenWireMockServer;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.json.simple.parser.ParseException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.Set;
import java.util.concurrent.ExecutionException;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
@TestProfile(AccountingSystemTestProfile.class)
@TestHTTPEndpoint(InstallationEndpoint.class)
@QuarkusTestResource(ProjectWireMockServer.class)
@QuarkusTestResource(ProviderWireMockServer.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@QuarkusTestResource(KeycloakWireMockServer.class)
@QuarkusTestResource(TokenWireMockServer.class)
public class InstallationEndpointTest {

    @Inject
    @RestClient
    ProviderClient providerClient;

    @Inject
    ProviderRepository providerRepository;

    @Inject
    ProjectRepository projectRepository;

    @Inject
    MetricDefinitionRepository metricDefinitionRepository;

    @Inject
    MetricRepository metricRepository;

    KeycloakTestClient keycloakClient = new KeycloakTestClient();

    protected String getAccessToken(String userName) {
        return keycloakClient.getAccessToken(userName);
    }

    @BeforeAll
    public void setup() throws ExecutionException, InterruptedException, ParseException {

        Total total = providerClient.getTotalNumberOfProviders("all").toCompletableFuture().get();

        Response response = providerClient.getAll("all", total.total).toCompletableFuture().get();

        providerRepository.persistOrUpdate(ProviderMapper.INSTANCE.eoscProvidersToProviders(response.results));
    }

    @BeforeEach
    public void before() throws ParseException {

        metricDefinitionRepository.deleteAll();
        projectRepository.deleteAll();
        metricRepository.deleteAll();

        var request = new ProjectRegistrationRequest();
        request.projects = Set.of("777536", "101017567");

        given()
                .auth()
                .oauth2(getAccessToken("admin"))
                .basePath("accounting-system/admin")
                .body(request)
                .contentType(ContentType.JSON)
                .post("/register-projects")
                .then()
                .assertThat()
                .statusCode(200);
    }

    @Test
    public void createInstallationNotAuthenticated() {

        var notAuthenticatedResponse = given()
                .auth()
                .oauth2("invalidToken")
                .contentType(ContentType.JSON)
                .post()
                .thenReturn();

        assertEquals(401, notAuthenticatedResponse.statusCode());
    }

    @Test
    public void createInstallationRequestBodyIsEmpty() {

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
    public void createInstallationCannotConsumeContentType() {

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
    public void createInstallationNoProvider(){

        var requestForMetricDefinition = new MetricDefinitionRequestDto();

        requestForMetricDefinition.metricName = "metric";
        requestForMetricDefinition.metricDescription = "description";
        requestForMetricDefinition.unitType = "TB";
        requestForMetricDefinition.metricType = "aggregated";

        var metricDefinitionResponse = createMetricDefinition(requestForMetricDefinition, "admin");

        var requestProjectNotFound= new InstallationRequestDto();

        requestProjectNotFound.project = "7775905";
        requestProjectNotFound.organisation = "grnet";
        requestProjectNotFound.infrastructure = "okeanos-knossos";
        requestProjectNotFound.installation = "SECOND";
        requestProjectNotFound.unitOfAccess = metricDefinitionResponse.id;

        InformativeResponse responseProviderNotFound = given()
                .auth()
                .oauth2(getAccessToken("admin"))
                .body(requestProjectNotFound)
                .contentType(ContentType.JSON)
                .post()
                .then()
                .assertThat()
                .statusCode(404)
                .extract()
                .as(InformativeResponse.class);

        assertEquals("There is no Project with the following id: "+requestProjectNotFound.project, responseProviderNotFound.message);
    }

    @Test
    public void createInstallationNoRequiredEntities(){

        var requestForMetricDefinition = new MetricDefinitionRequestDto();

        requestForMetricDefinition.metricName = "metric";
        requestForMetricDefinition.metricDescription = "description";
        requestForMetricDefinition.unitType = "TB";
        requestForMetricDefinition.metricType = "aggregated";

        var metricDefinitionResponse = createMetricDefinition(requestForMetricDefinition, "admin");

        var requestMetricDefinitionNotFound= new InstallationRequestDto();

        requestMetricDefinitionNotFound.project = "777536";
        requestMetricDefinitionNotFound.organisation = "grnet";
        requestMetricDefinitionNotFound.infrastructure = "okeanos-knossos";
        requestMetricDefinitionNotFound.installation = "SECOND";
        requestMetricDefinitionNotFound.unitOfAccess = "507f1f77bcf86cd799439011";

        InformativeResponse responseMetricDefinitionNotFound = given()
                .auth()
                .oauth2(getAccessToken("admin"))
                .body(requestMetricDefinitionNotFound)
                .contentType(ContentType.JSON)
                .post()
                .then()
                .assertThat()
                .statusCode(404)
                .extract()
                .as(InformativeResponse.class);

        assertEquals("There is no Metric Definition with the following id: "+requestMetricDefinitionNotFound.unitOfAccess, responseMetricDefinitionNotFound.message);

        var requestProviderNotFound= new InstallationRequestDto();

        requestProviderNotFound.project = "777536";
        requestProviderNotFound.organisation = "GRNET";
        requestProviderNotFound.infrastructure = "okeanos-knossos";
        requestProviderNotFound.installation = "SECOND";
        requestProviderNotFound.unitOfAccess = metricDefinitionResponse.id;

        InformativeResponse responseProviderNotFound = given()
                .auth()
                .oauth2(getAccessToken("admin"))
                .body(requestProviderNotFound)
                .contentType(ContentType.JSON)
                .post()
                .then()
                .assertThat()
                .statusCode(404)
                .extract()
                .as(InformativeResponse.class);

        assertEquals("There is no Provider with the following id: "+requestProviderNotFound.organisation, responseProviderNotFound.message);
    }

    @Test
    public void createInstallationNoRequiredAttributes() {

        var requestForMetricDefinition = new MetricDefinitionRequestDto();

        requestForMetricDefinition.metricName = "metric";
        requestForMetricDefinition.metricDescription = "description";
        requestForMetricDefinition.unitType = "TB";
        requestForMetricDefinition.metricType = "aggregated";

        var metricDefinitionResponse = createMetricDefinition(requestForMetricDefinition, "admin");

        var requestNoOrganisation= new InstallationRequestDto();

        requestNoOrganisation.project = "777536";
        requestNoOrganisation.infrastructure = "okeanos-knossos";
        requestNoOrganisation.installation = "SECOND";
        requestNoOrganisation.unitOfAccess = metricDefinitionResponse.id;

        InformativeResponse responseNoOrganisation = given()
                .auth()
                .oauth2(getAccessToken("admin"))
                .body(requestNoOrganisation)
                .contentType(ContentType.JSON)
                .post()
                .then()
                .assertThat()
                .statusCode(400)
                .extract()
                .as(InformativeResponse.class);

        assertEquals("organisation may not be empty.", responseNoOrganisation.message);

        var requestNoInstallation= new InstallationRequestDto();

        requestNoInstallation.project = "777536";
        requestNoInstallation.organisation = "grnet";
        requestNoInstallation.infrastructure = "okeanos-knossos";
        requestNoInstallation.unitOfAccess = metricDefinitionResponse.id;

        InformativeResponse responseNoInstallation = given()
                .auth()
                .oauth2(getAccessToken("admin"))
                .body(requestNoInstallation)
                .contentType(ContentType.JSON)
                .post()
                .then()
                .assertThat()
                .statusCode(400)
                .extract()
                .as(InformativeResponse.class);

        assertEquals("installation may not be empty.", responseNoInstallation.message);
    }

    @Test
    public void createInstallationAlreadyExists() {

        projectRepository.associateProjectWithProvider("777536", "grnet");

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
        request.installation = "GRNET-KNS";
        request.unitOfAccess = metricDefinitionResponse.id;

        var installation = createInstallation(request, "admin");

        var informativeResponse = given()
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

        assertEquals("There is an Installation with the following combination : {"+request.project+", "+request.organisation+", "+request.installation+"}. Its id is "+installation.id, informativeResponse.message);
    }

    @Test
    public void createInstallation() {

        projectRepository.associateProjectWithProvider("777536", "grnet");

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

       var response = createInstallation(request, "admin");

        assertEquals(request.project, response.project);
        assertEquals(request.organisation, response.organisation);
        assertEquals(request.infrastructure, response.infrastructure);
        assertEquals(request.installation, response.installation);
        assertEquals(request.unitOfAccess, response.metricDefinition.id);
    }

    @Test
    public void deleteInstallationNotAuthenticated() {

        var notAuthenticatedResponse = given()
                .auth()
                .oauth2("invalidToken")
                .delete("/{id}", "7dyebdheb7377e")
                .thenReturn();

        assertEquals(401, notAuthenticatedResponse.statusCode());
    }

    @Test
    public void deleteInstallationNotFound() {

        var response = given()
                .auth()
                .oauth2(getAccessToken("admin"))
                .delete("/{id}", "7dyebdheb7377e")
                .then()
                .assertThat()
                .statusCode(404)
                .extract()
                .as(InformativeResponse.class);

        assertEquals("There is no Installation with the following id: 7dyebdheb7377e", response.message);
    }

    @Test
    public void deleteInstallation(){

        projectRepository.associateProjectWithProvider("777536", "grnet");

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

        var response = createInstallation(request, "admin");

        var deleteResponse = given()
                .auth()
                .oauth2(getAccessToken("admin"))
                .delete("/{id}", response.id)
                .then()
                .assertThat()
                .statusCode(200)
                .extract()
                .as(InformativeResponse.class);

        assertEquals("Installation has been deleted successfully.", deleteResponse.message);
    }

    @Test
    public void deleteInstallationNotAllowed(){

        projectRepository.associateProjectWithProvider("777536", "grnet");

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

        var response = createInstallation(request, "admin");

        var requestForMetric = new MetricRequestDto();
        requestForMetric.start = "2020-01-05T09:15:07Z";
        requestForMetric.end = "2020-01-05T09:18:07Z";
        requestForMetric.value = 10.8;
        requestForMetric.metricDefinitionId = metricDefinitionResponse.id;

        given()
                .auth()
                .oauth2(getAccessToken("admin"))
                .basePath("accounting-system/installations")
                .body(requestForMetric)
                .contentType(ContentType.JSON)
                .post("/{installationId}/metrics", response.id);

        var requestForMetric1 = new MetricRequestDto();
        requestForMetric1.start = "2021-01-05T09:15:07Z";
        requestForMetric1.end = "2021-01-05T09:18:07Z";
        requestForMetric1.value = 10.8;
        requestForMetric1.metricDefinitionId = metricDefinitionResponse.id;

        given()
                .auth()
                .oauth2(getAccessToken("admin"))
                .basePath("accounting-system/installations")
                .body(requestForMetric1)
                .contentType(ContentType.JSON)
                .post("/{installationId}/metrics", response.id);

        var deleteResponse = given()
                .auth()
                .oauth2(getAccessToken("admin"))
                .delete("/{id}", response.id)
                .then()
                .assertThat()
                .statusCode(403)
                .extract()
                .as(InformativeResponse.class);

        assertEquals("Deleting an Installation is not allowed if there are Metrics assigned to it.", deleteResponse.message);
    }

    @Test
    public void fetchInstallationNotAuthenticated() {

        var notAuthenticatedResponse = given()
                .auth()
                .oauth2("invalidToken")
                .get("/{id}", "507f1f77bcf86cd799439011")
                .thenReturn();

        assertEquals(401, notAuthenticatedResponse.statusCode());
    }

    @Test
    public void fetchInstallationNotFound() {

        var response = given()
                .auth()
                .oauth2(getAccessToken("admin"))
                .get("/{id}", "507f1f77bcf86cd799439011")
                .then()
                .assertThat()
                .statusCode(404)
                .extract()
                .as(InformativeResponse.class);

        assertEquals("There is no Installation with the following id: 507f1f77bcf86cd799439011", response.message);
    }

    @Test
    public void fetchInstallationNotFoundNonHexId() {

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
    public void fetchInstallation() {

        projectRepository.associateProjectWithProvider("777536", "grnet");

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

        var storedInstallation = given()
                .auth()
                .oauth2(getAccessToken("admin"))
                .get("/{id}", installation.id)
                .then()
                .assertThat()
                .statusCode(200)
                .extract()
                .as(InstallationResponseDto.class);

        assertEquals(installation.id, storedInstallation.id);
    }

    @Test
    public void updateInstallationNotAuthenticated() {

        var notAuthenticatedResponse = given()
                .auth()
                .oauth2("invalidToken")
                .contentType(ContentType.JSON)
                .patch("/{id}", "556787878e-rrr")
                .thenReturn();

        assertEquals(401, notAuthenticatedResponse.statusCode());
    }

    @Test
    public void updateInstallationNotFound() {

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

        assertEquals("There is no Installation with the following id: 556787878e-rrr", response.message);
    }

    @Test
    public void updateInstallationRequestBodyIsEmpty() {

        projectRepository.associateProjectWithProvider("777536", "grnet");

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

        var response = given()
                .auth()
                .oauth2(getAccessToken("admin"))
                .contentType(ContentType.JSON)
                .patch("/{id}", installation.id)
                .then()
                .assertThat()
                .statusCode(400)
                .extract()
                .as(InformativeResponse.class);

        assertEquals("The request body is empty.", response.message);
    }

    @Test
    public void updateInstallationNoMetricDefinition() {

        projectRepository.associateProjectWithProvider("777536", "grnet");

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

        var requestForUpdating= new InstallationRequestDto();

        requestForUpdating.unitOfAccess = "507f1f77bcf86cd799439011";

        var informativeResponse = given()
                .auth()
                .oauth2(getAccessToken("admin"))
                .body(requestForUpdating)
                .contentType(ContentType.JSON)
                .patch("/{id}", installation.id)
                .then()
                .assertThat()
                .statusCode(404)
                .extract()
                .as(InformativeResponse.class);

        assertEquals("There is no Metric Definition with the following id: 507f1f77bcf86cd799439011", informativeResponse.message);
    }
    @Test
    public void updateInstallationPartial() {

        projectRepository.associateProjectWithProvider("777536", "grnet");

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

        var requestForUpdating= new InstallationRequestDto();

        requestForUpdating.infrastructure = "okeanos-knossos-test";
        requestForUpdating.installation = "installation-test";

        var updatedInstallation = given()
                .auth()
                .oauth2(getAccessToken("admin"))
                .body(requestForUpdating)
                .contentType(ContentType.JSON)
                .patch("/{id}", installation.id)
                .then()
                .assertThat()
                .statusCode(200)
                .extract()
                .as(InstallationResponseDto.class);

        assertEquals(request.organisation, updatedInstallation.organisation);
        assertEquals(requestForUpdating.infrastructure, updatedInstallation.infrastructure);
        assertEquals(requestForUpdating.installation, updatedInstallation.installation);
        assertEquals(request.unitOfAccess, updatedInstallation.metricDefinition.id);
    }

    @Test
    public void updateInstallation() {

        projectRepository.associateProjectWithProvider("777536", "grnet");

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

        var requestForMetricDefinition1 = new MetricDefinitionRequestDto();

        requestForMetricDefinition1.metricName = "metric";
        requestForMetricDefinition1.metricDescription = "description";
        requestForMetricDefinition1.unitType = "TB/year";
        requestForMetricDefinition1.metricType = "aggregated";

        var metricDefinitionResponse1 = createMetricDefinition(requestForMetricDefinition1, "admin");


        var requestForUpdating= new InstallationRequestDto();

        requestForUpdating.infrastructure = "okeanos-knossos-test";
        requestForUpdating.installation = "installation-test";
        requestForUpdating.unitOfAccess = metricDefinitionResponse1.id;

        var updatedInstallation = given()
                .auth()
                .oauth2(getAccessToken("admin"))
                .body(requestForUpdating)
                .contentType(ContentType.JSON)
                .patch("/{id}", installation.id)
                .then()
                .assertThat()
                .statusCode(200)
                .extract()
                .as(InstallationResponseDto.class);

        assertEquals(requestForUpdating.infrastructure, updatedInstallation.infrastructure);
        assertEquals(requestForUpdating.installation, updatedInstallation.installation);
        assertEquals(requestForUpdating.unitOfAccess, updatedInstallation.metricDefinition.id);
    }

    @Test
    public void updateInstallationConflict() {

        projectRepository.associateProjectWithProvider("777536", "grnet");

        var requestForMetricDefinition = new MetricDefinitionRequestDto();

        requestForMetricDefinition.metricName = "metric";
        requestForMetricDefinition.metricDescription = "description";
        requestForMetricDefinition.unitType = "TB";
        requestForMetricDefinition.metricType = "aggregated";

        var metricDefinitionResponse = createMetricDefinition(requestForMetricDefinition, "admin");

        var request = new InstallationRequestDto();

        request.project = "777536";
        request.organisation = "grnet";
        request.infrastructure = "okeanos-knossos";
        request.installation = "GRNET-KNS";
        request.unitOfAccess = metricDefinitionResponse.id;

        var installation = createInstallation(request, "admin");

        var requestForMetricDefinition1 = new MetricDefinitionRequestDto();

        requestForMetricDefinition1.metricName = "metric";
        requestForMetricDefinition1.metricDescription = "description";
        requestForMetricDefinition1.unitType = "TB/year";
        requestForMetricDefinition1.metricType = "aggregated";

        var metricDefinitionResponse1 = createMetricDefinition(requestForMetricDefinition1, "admin");

        var requestForUpdating = new InstallationRequestDto();

        requestForUpdating.infrastructure = "okeanos-knossos";
        requestForUpdating.installation = "GRNET-KNS-2";
        requestForUpdating.unitOfAccess = metricDefinitionResponse1.id;

        var request1 = new InstallationRequestDto();

        request1.project = "777536";
        request1.organisation = "grnet";
        request1.infrastructure = "okeanos-knossos";
        request1.installation = "GRNET-KNS-2";
        request1.unitOfAccess = metricDefinitionResponse.id;

        var installation2 = createInstallation(request1, "admin");

        var informativeResponse = given()
                .auth()
                .oauth2(getAccessToken("admin"))
                .body(requestForUpdating)
                .contentType(ContentType.JSON)
                .patch("/{id}", installation.id)
                .then()
                .assertThat()
                .statusCode(409)
                .extract()
                .as(InformativeResponse.class);

        assertEquals("There is an Installation with the following combination : {777536, grnet, "+requestForUpdating.installation+"}. Its id is "+installation2.id, informativeResponse.message);
    }

    private InstallationResponseDto createInstallation(InstallationRequestDto request, String user){

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
