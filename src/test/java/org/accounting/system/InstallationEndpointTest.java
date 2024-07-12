package org.accounting.system;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import io.restassured.http.ContentType;
import org.accounting.system.dtos.InformativeResponse;
import org.accounting.system.dtos.installation.InstallationRequestDto;
import org.accounting.system.dtos.installation.InstallationResponseDto;
import org.accounting.system.dtos.metric.MetricRequestDto;
import org.accounting.system.dtos.metricdefinition.MetricDefinitionRequestDto;
import org.accounting.system.dtos.metricdefinition.MetricDefinitionResponseDto;
import org.accounting.system.endpoints.InstallationEndpoint;
import org.accounting.system.wiremock.ProjectWireMockServer;
import org.accounting.system.wiremock.ProviderWireMockServer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.Set;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
@TestProfile(AccountingSystemTestProfile.class)
@TestHTTPEndpoint(InstallationEndpoint.class)
@QuarkusTestResource(ProjectWireMockServer.class)
@QuarkusTestResource(ProviderWireMockServer.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class InstallationEndpointTest extends PrepareTest {

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

        var requestNoMetricDefinition= new InstallationRequestDto();

        requestNoMetricDefinition.project = "777536";
        requestNoMetricDefinition.organisation = "grnet";
        requestNoMetricDefinition.infrastructure = "okeanos-knossos";
        requestNoMetricDefinition.installation = "SECOND";

        InformativeResponse responseNoMetricDefinition = given()
                .auth()
                .oauth2(getAccessToken("admin"))
                .body(requestNoMetricDefinition)
                .contentType(ContentType.JSON)
                .post()
                .then()
                .assertThat()
                .statusCode(400)
                .extract()
                .as(InformativeResponse.class);

        assertEquals("unit_of_access may not be empty.", responseNoMetricDefinition.message);
    }

    @Test
    public void createInstallationAlreadyExists() {

        projectRepository.associateProjectWithProviders("777536", Set.of("grnet"));

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

        projectRepository.associateProjectWithProviders("777536", Set.of("grnet"));

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

        projectRepository.associateProjectWithProviders("777536", Set.of("grnet"));

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

        projectRepository.associateProjectWithProviders("777536", Set.of("grnet"));

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

        projectRepository.associateProjectWithProviders("777536", Set.of("grnet"));

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

        projectRepository.associateProjectWithProviders("777536", Set.of("grnet"));

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

        projectRepository.associateProjectWithProviders("777536", Set.of("grnet"));

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

        projectRepository.associateProjectWithProviders("777536", Set.of("grnet"));

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

        projectRepository.associateProjectWithProviders("777536", Set.of("grnet"));

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

        projectRepository.associateProjectWithProviders("777536", Set.of("grnet"));

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

        var requestForMetricDefinition1 = new MetricDefinitionRequestDto();

        requestForMetricDefinition1.metricName = "metric";
        requestForMetricDefinition1.metricDescription = "description";
        requestForMetricDefinition1.unitType = "TB/year";
        requestForMetricDefinition1.metricType = "aggregated";

        var metricDefinitionResponse1 = createMetricDefinition(requestForMetricDefinition1, "admin");

        var requestForUpdating = new InstallationRequestDto();

        requestForUpdating.infrastructure = "okeanos-knossos";
        requestForUpdating.installation = "GRNET-KNS";
        requestForUpdating.unitOfAccess = metricDefinitionResponse1.id;

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

        assertEquals("There is an Installation with the following combination : {777536, grnet, "+requestForUpdating.installation+"}. Its id is "+installation.id, informativeResponse.message);
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
