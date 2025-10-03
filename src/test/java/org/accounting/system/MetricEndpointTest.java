package org.accounting.system;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import io.restassured.RestAssured;
import io.restassured.config.JsonConfig;
import io.restassured.http.ContentType;
import io.restassured.path.json.config.JsonPathConfig;
import io.vavr.collection.Array;
import jakarta.inject.Inject;
import org.accounting.system.dtos.InformativeResponse;
import org.accounting.system.dtos.installation.InstallationRequestDto;
import org.accounting.system.dtos.installation.InstallationResponseDto;
import org.accounting.system.dtos.metric.MetricRequestDto;
import org.accounting.system.dtos.metric.MetricResponseDto;
import org.accounting.system.dtos.metric.UpdateMetricRequestDto;
import org.accounting.system.dtos.metricdefinition.MetricDefinitionRequestDto;
import org.accounting.system.dtos.metricdefinition.MetricDefinitionResponseDto;
import org.accounting.system.dtos.pagination.PageResource;
import org.accounting.system.endpoints.MetricEndpoint;
import org.accounting.system.repositories.project.ProjectRepository;
import org.accounting.system.wiremock.KeycloakWireMockServer;
import org.accounting.system.wiremock.ProjectWireMockServer;
import org.accounting.system.wiremock.ProviderWireMockServer;
import org.accounting.system.wiremock.TokenWireMockServer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.ArrayList;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertEquals;


@QuarkusTest
@TestProfile(AccountingSystemTestProfile.class)
@TestHTTPEndpoint(MetricEndpoint.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@QuarkusTestResource(ProjectWireMockServer.class)
@QuarkusTestResource(ProviderWireMockServer.class)
@QuarkusTestResource(KeycloakWireMockServer.class)
@QuarkusTestResource(TokenWireMockServer.class)
public class MetricEndpointTest extends PrepareTest {

    @Inject
    ProjectRepository projectRepository;

    @Test
    public void createMetricRequestNotAuthenticated() {

        var notAuthenticatedResponse =  given()
                .auth()
                .oauth2("invalidToken")
                .contentType(ContentType.JSON)
                .post("/{projectId}/providers/{providerId}/installations/{installationId}/metrics", "projectId", "grnet", "installationId");

        assertEquals(401, notAuthenticatedResponse.statusCode());
    }

    @Test
    public void createMetricNoMetricDefinition() {

        var request = new MetricRequestDto();
        request.start = "2022-01-05T09:13:07Z";
        request.end = "2022-01-05T09:14:07Z";
        request.metricDefinitionId = "507f1f77bcf86cd799439011";
        request.value = 10.3;

        var response = assignMetric("admin", request, new ArrayList<>());

        var informativeResponse = response
                .then()
                .assertThat()
                .statusCode(404)
                .extract()
                .as(InformativeResponse.class);

        assertEquals("There is no Metric Definition with the following id: 507f1f77bcf86cd799439011", informativeResponse.message);
    }

    @Test
    public void createMetricEmptyMetricDefinitionAttribute() {

        var request = new MetricRequestDto();
        request.start = "2022-01-05T09:13:07Z";
        request.end = "2022-01-05T09:14:07Z";
        request.value = 10.3;

        var response = assignMetric("admin", request, new ArrayList<>());

        var informativeResponse = response
                .then()
                .assertThat()
                .statusCode(400)
                .extract()
                .as(InformativeResponse.class);

        assertEquals("metric_definition_id may not be empty.", informativeResponse.message);
    }

    @Test
    public void createMetricNoValidZuluTimestamp() {

        var requestForMetricDefinition = new MetricDefinitionRequestDto();

        requestForMetricDefinition.metricName = "metric";
        requestForMetricDefinition.metricDescription = "description";
        requestForMetricDefinition.unitType = "TB";
        requestForMetricDefinition.metricType = "aggregated";

        var metricDefinition = createMetricDefinition(requestForMetricDefinition, "admin");

        var request = new MetricRequestDto();
        request.metricDefinitionId = metricDefinition.id;
        request.start = "2022-01-0509:13:07";
        request.end = "2022-01-05T09:14:07Z";
        request.value = 10.3;

        var response = assignMetric("admin", request, new ArrayList<>());

        var informativeResponse = response
                .then()
                .assertThat()
                .statusCode(400)
                .extract()
                .as(InformativeResponse.class);

        assertEquals("time_period_start must be a valid zulu timestamp. found: 2022-01-0509:13:07", informativeResponse.message);
    }

    @Test
    public void createMetricNoZuluTimestamp() {

        MetricDefinitionRequestDto requestMetricDefinition = new MetricDefinitionRequestDto();

        requestMetricDefinition.metricName = "metric";
        requestMetricDefinition.metricDescription = "description";
        requestMetricDefinition.unitType = "TB";
        requestMetricDefinition.metricType = "aggregated";

        MetricDefinitionResponseDto metricDefinition = createMetricDefinition(requestMetricDefinition, "admin");

        var request = new MetricRequestDto();
        request.metricDefinitionId = metricDefinition.id;
        request.end = "2022-01-05T09:13:07Z";
        request.value = 10.3;

        var response = assignMetric("admin", request, new ArrayList<>());

        var informativeResponse = response
                .then()
                .assertThat()
                .statusCode(400)
                .extract()
                .as(InformativeResponse.class);

        assertEquals("time_period_start may not be empty.", informativeResponse.message);
    }

    @Test
    public void createMetricStartIsAfterEnd() {

        //first create a metric definition

        var requestForMetricDefinition = new MetricDefinitionRequestDto();

        requestForMetricDefinition.metricName = "metric";
        requestForMetricDefinition.metricDescription = "description";
        requestForMetricDefinition.unitType = "TB";
        requestForMetricDefinition.metricType = "aggregated";

        var metricDefinitionResponse = createMetricDefinition(requestForMetricDefinition, "admin");

        //then execute a request for creating a metric

        var requestForMetric = new MetricRequestDto();
        requestForMetric.start = "2020-01-05T09:15:07Z";
        requestForMetric.end = "2020-01-05T09:13:07Z";
        requestForMetric.value = 10.8;
        requestForMetric.metricDefinitionId = metricDefinitionResponse.id;

        var response = assignMetric("admin", requestForMetric, new ArrayList<>());

        var informativeResponse = response
                .then()
                .assertThat()
                .statusCode(400)
                .extract()
                .as(InformativeResponse.class);

        assertEquals("Timestamp of the starting date time cannot be after of Timestamp of the end date time.", informativeResponse.message);
    }

    @Test
    public void createMetricStartIsEqualEnd() {

        //first create a metric definition

        var requestForMetricDefinition = new MetricDefinitionRequestDto();

        requestForMetricDefinition.metricName = "metric";
        requestForMetricDefinition.metricDescription = "description";
        requestForMetricDefinition.unitType = "TB";
        requestForMetricDefinition.metricType = "aggregated";

        var metricDefinitionResponse = createMetricDefinition(requestForMetricDefinition, "admin");

        //then execute a request for creating a metric

        var requestForMetric = new MetricRequestDto();
        requestForMetric.start = "2020-01-05T09:15:07Z";
        requestForMetric.end = "2020-01-05T09:15:07Z";
        requestForMetric.value = 10.8;
        requestForMetric.metricDefinitionId = metricDefinitionResponse.id;

        var response = assignMetric("admin", requestForMetric, new ArrayList<>());

        var informativeResponse = response
                .then()
                .assertThat()
                .statusCode(400)
                .extract()
                .as(InformativeResponse.class);

        assertEquals("Timestamp of the starting date time cannot be equal to Timestamp of the end date time.", informativeResponse.message);
    }

    @Test
    public void createMetricStartIsAfterCurrentDate() {

        //first create a metric definition

        var requestForMetricDefinition = new MetricDefinitionRequestDto();

        requestForMetricDefinition.metricName = "metric";
        requestForMetricDefinition.metricDescription = "description";
        requestForMetricDefinition.unitType = "TB";
        requestForMetricDefinition.metricType = "aggregated";

        var metricDefinitionResponse = createMetricDefinition(requestForMetricDefinition, "admin");

        //then execute a request for creating a metric

        var requestForMetric = new MetricRequestDto();
        requestForMetric.start = "3023-01-05T09:15:07Z";
        requestForMetric.end = "3023-01-05T09:19:07Z";
        requestForMetric.value = 10.8;
        requestForMetric.metricDefinitionId = metricDefinitionResponse.id;

        var response = assignMetric("admin", requestForMetric, new ArrayList<>());

        var informativeResponse = response
                .then()
                .assertThat()
                .statusCode(400)
                .extract()
                .as(InformativeResponse.class);

        assertEquals("Timestamp cannot be after the current date-time.", informativeResponse.message);
    }

    @Test
    public void fetchMetricNotAuthenticated() {

        var notAuthenticatedResponse = given()
                .auth()
                .oauth2("invalidToken")
                .get("/{metricId}", "507f1f77bcf86cd799439011")
                .thenReturn();

        assertEquals(401, notAuthenticatedResponse.statusCode());
    }

    @Test
    public void fetchMetricNotFound() {

        var requestForMetricDefinition = new MetricDefinitionRequestDto();

        requestForMetricDefinition.metricName = "metric";
        requestForMetricDefinition.metricDescription = "description";
        requestForMetricDefinition.unitType = "TB/year";
        requestForMetricDefinition.metricType = "aggregated";

        var metricDefinitionResponse = createMetricDefinition(requestForMetricDefinition, "admin");

        var request= new InstallationRequestDto();

        request.project = "777536";
        request.organisation = "grnet";
        request.infrastructure = "okeanos-knossos";
        request.installation = "SECOND";
        request.unitOfAccess = metricDefinitionResponse.id;

        projectRepository.associateProjectWithProvider("777536", "grnet");

        var installation = createInstallation(request, "admin");

        var notFoundResponse = given()
                .basePath("accounting-system/installations")
                .auth()
                .oauth2(getAccessToken("admin"))
                .get("/{installation_id}/metrics/{metricId}", installation.id, "507f1f77bcf86cd799439011")
                .then()
                .assertThat()
                .statusCode(404)
                .extract()
                .as(InformativeResponse.class);

        assertEquals("There is no Metric with the following id: 507f1f77bcf86cd799439011", notFoundResponse.message);
    }

    @Test
    public void fetchMetric() {

        var requestForMetricDefinition = new MetricDefinitionRequestDto();

        requestForMetricDefinition.metricName = "metric";
        requestForMetricDefinition.metricDescription = "description";
        requestForMetricDefinition.unitType = "TB";
        requestForMetricDefinition.metricType = "aggregated";

        var metricDefinitionResponse = createMetricDefinition(requestForMetricDefinition, "admin");

        var requestForMetric = new MetricRequestDto();
        requestForMetric.start = "2020-01-05T09:15:07Z";
        requestForMetric.end = "2020-01-05T09:18:07Z";
        requestForMetric.value = 10.8;
        requestForMetric.metricDefinitionId = metricDefinitionResponse.id;

        List<String> installationId = new ArrayList();

        var response = assignMetric("admin", requestForMetric, installationId);

        var metric = response
                .then()
                .assertThat()
                .statusCode(201)
                .extract()
                .as(MetricResponseDto.class);

        var getMetric = given()
                .basePath("accounting-system/installations")
                .auth()
                .oauth2(getAccessToken("admin"))
                .get("/{installation_id}/metrics/{metricId}", installationId.get(0), metric.id)
                .then()
                .assertThat()
                .statusCode(200)
                .extract()
                .as(MetricResponseDto.class);

        assertEquals(metric.id, getMetric.id);
    }

    @Test
    public void fetchInstallationMetricsDateFiltering() {

        projectRepository.associateProjectWithProvider("777536", "grnet");

        var installationId = assignMetricsToSpecificInstallation();

        var getMetricPageable = given()
                .basePath("accounting-system/installations")
                .auth()
                .oauth2(getAccessToken("admin"))
                .queryParam("start", "2019-01-01")
                .queryParam("end", "2019-12-31")
                .get("/{installation_id}/metrics", installationId)
                .then()
                .assertThat()
                .statusCode(200)
                .extract()
                .as(PageResource.class);

        assertEquals(1, getMetricPageable.getTotalElements());

        var startCannotBeforeEndError = given()
                .basePath("accounting-system/installations")
                .auth()
                .oauth2(getAccessToken("admin"))
                .queryParam("start", "2019-12-01")
                .queryParam("end", "2019-01-31")
                .get("/{installation_id}/metrics", installationId)
                .then()
                .assertThat()
                .statusCode(400)
                .extract()
                .as(InformativeResponse.class);

        assertEquals("start cannot be after end.", startCannotBeforeEndError.message);

        var dateFormatError = given()
                .basePath("accounting-system/installations")
                .auth()
                .oauth2(getAccessToken("admin"))
                .queryParam("start", "xxxxx")
                .queryParam("end", "2019-01-31")
                .get("/{installation_id}/metrics", installationId)
                .then()
                .assertThat()
                .statusCode(400)
                .extract()
                .as(InformativeResponse.class);

        assertEquals("The date format must be as follows YYYY-MM-DD", dateFormatError.message);

        var dateFormatError1 = given()
                .basePath("accounting-system/installations")
                .auth()
                .oauth2(getAccessToken("admin"))
                .queryParam("start", "2019-31-25")
                .queryParam("end", "2019-12-31")
                .get("/{installation_id}/metrics", installationId)
                .then()
                .assertThat()
                .statusCode(400)
                .extract()
                .as(InformativeResponse.class);

        assertEquals("The date format must be as follows YYYY-MM-DD", dateFormatError1.message);
    }

    @Test
    public void fetchMetricsDateFiltering() {

        projectRepository.associateProjectWithProvider("777536", "grnet");

        assignMetricsToSpecificInstallation();

        var getProjectMetricPageable = given()
                .basePath("accounting-system/projects")
                .auth()
                .oauth2(getAccessToken("admin"))
                .queryParam("start", "2020-01-01")
                .queryParam("end", "2020-12-31")
                .get("/{project_id}/metrics", "777536")
                .then()
                .assertThat()
                .statusCode(200)
                .extract()
                .as(PageResource.class);

        assertEquals(1, getProjectMetricPageable.getTotalElements());

        var getProviderMetricPageable = given()
                .basePath("accounting-system/projects")
                .auth()
                .oauth2(getAccessToken("admin"))
                .queryParam("start", "2021-01-01")
                .queryParam("end", "2021-12-31")
                .get("/{project_id}/providers/{provider_id}/metrics", "777536", "grnet")
                .then()
                .assertThat()
                .statusCode(200)
                .extract()
                .as(PageResource.class);

        assertEquals(1, getProviderMetricPageable.getTotalElements());

        var getProviderMetricPageable1 = given()
                .basePath("accounting-system/projects")
                .auth()
                .oauth2(getAccessToken("admin"))
                .queryParam("start", "2022-01-01")
                .queryParam("end", "2022-12-31")
                .get("/{project_id}/providers/{provider_id}/metrics", "777536", "grnet")
                .then()
                .assertThat()
                .statusCode(200)
                .extract()
                .as(PageResource.class);

        assertEquals(0, getProviderMetricPageable1.getTotalElements());
    }

    @Test
    public void fetchMetricsMetricDefinitionFiltering() {

        projectRepository.associateProjectWithProvider("777536", "grnet");

        var array = assignMetricsToSpecificInstallationMultipleMetricDefinitions();

        var getProjectMetricPageable = given()
                .basePath("accounting-system/projects")
                .auth()
                .oauth2(getAccessToken("admin"))
                .get("/{project_id}/metrics", "777536")
                .then()
                .assertThat()
                .statusCode(200)
                .extract()
                .as(PageResource.class);

        assertEquals(3, getProjectMetricPageable.getTotalElements());

        var getProviderMetricPageable = given()
                .basePath("accounting-system/projects")
                .auth()
                .oauth2(getAccessToken("admin"))
                .queryParam("metric-definition-id", array.get(1))
                .get("/{project_id}/providers/{provider_id}/metrics", "777536", "grnet")
                .then()
                .assertThat()
                .statusCode(200)
                .extract()
                .as(PageResource.class);

        assertEquals(2, getProviderMetricPageable.getTotalElements());

        var getProviderMetricPageable1 = given()
                .basePath("accounting-system/projects")
                .auth()
                .oauth2(getAccessToken("admin"))
                .queryParam("metric-definition-id", array.get(0))
                .get("/{project_id}/providers/{provider_id}/metrics", "777536", "grnet")
                .then()
                .assertThat()
                .statusCode(200)
                .extract()
                .as(PageResource.class);

        assertEquals(1, getProviderMetricPageable1.getTotalElements());
    }

    @Test
    public void deleteMetricNotAuthenticated() {

        var notAuthenticatedResponse = given()
                .auth()
                .oauth2("invalidToken")
                .delete("/{metricId}", "507f1f77bcf86cd799439011")
                .thenReturn();

        assertEquals(401, notAuthenticatedResponse.statusCode());
    }

    @Test
    public void deleteMetric() {

        var requestForMetricDefinition = new MetricDefinitionRequestDto();

        requestForMetricDefinition.metricName = "metric";
        requestForMetricDefinition.metricDescription = "description";
        requestForMetricDefinition.unitType = "TB";
        requestForMetricDefinition.metricType = "aggregated";

        var metricDefinitionResponse = createMetricDefinition(requestForMetricDefinition, "admin");

        var requestForMetric = new MetricRequestDto();
        requestForMetric.start = "2020-01-05T09:15:07Z";
        requestForMetric.end = "2020-01-05T09:18:07Z";
        requestForMetric.value = 10.8;
        requestForMetric.metricDefinitionId = metricDefinitionResponse.id;

        List<String> installationId = new ArrayList();

        var response = assignMetric("admin", requestForMetric, installationId);

        var metric = response
                .then()
                .assertThat()
                .statusCode(201)
                .extract()
                .as(MetricResponseDto.class);

        var deleteResponse = given()
                .basePath("accounting-system/installations")
                .auth()
                .oauth2(getAccessToken("admin"))
                .delete("/{installation_id}/metrics/{metricId}", installationId.get(0), metric.id)
                .then()
                .assertThat()
                .statusCode(200)
                .extract()
                .as(InformativeResponse.class);

        assertEquals("Metric has been deleted successfully.", deleteResponse.message);
    }

    @Test
    public void updateMetricRequestBodyIsEmpty() {

        var requestForMetricDefinition = new MetricDefinitionRequestDto();

        requestForMetricDefinition.metricName = "metric";
        requestForMetricDefinition.metricDescription = "description";
        requestForMetricDefinition.unitType = "TB";
        requestForMetricDefinition.metricType = "aggregated";

        var createdMetricDefinition = createMetricDefinition(requestForMetricDefinition, "admin");

        // create a metric
        var requestForMetric = new MetricRequestDto();
        requestForMetric.start = "2022-01-05T09:13:07Z";
        requestForMetric.end = "2022-01-05T09:14:07Z";
        requestForMetric.value = 10.8;
        requestForMetric.metricDefinitionId = createdMetricDefinition.id;

        List<String> installationId = new ArrayList();

        var response = assignMetric("admin", requestForMetric, installationId);

        var metric = response
                .then()
                .assertThat()
                .statusCode(201)
                .extract()
                .as(MetricResponseDto.class);

        var informativeResponse = given()
                .basePath("accounting-system/installations")
                .auth()
                .oauth2(getAccessToken("admin"))
                .contentType(ContentType.JSON)
                .patch("{installation_id}/metrics/{id}", installationId.get(0), metric.id)
                .then()
                .assertThat()
                .statusCode(400)
                .extract()
                .as(InformativeResponse.class);

        assertEquals("The request body is empty.", informativeResponse.message);
    }

    @Test
    public void updateMetricNotAuthenticated() {

        var notAuthenticatedResponse = given()
                .auth()
                .oauth2("invalidToken")
                .patch("/{id}", "507f1f77bcf86cd799439011")
                .thenReturn();

        assertEquals(401, notAuthenticatedResponse.statusCode());
    }

    @Test
    public void updateMetricMetricDefinitionNotValidZuluTimestamp() {

        var requestForMetricDefinition = new MetricDefinitionRequestDto();

        requestForMetricDefinition.metricName = "metric";
        requestForMetricDefinition.metricDescription = "description";
        requestForMetricDefinition.unitType = "TB";
        requestForMetricDefinition.metricType = "aggregated";

        var createdMetricDefinition = createMetricDefinition(requestForMetricDefinition, "admin");

        // create a metric
        var requestForMetric = new MetricRequestDto();
        requestForMetric.start = "2022-01-05T09:13:07Z";
        requestForMetric.end = "2022-01-05T09:14:07Z";
        requestForMetric.value = 10.8;
        requestForMetric.metricDefinitionId = createdMetricDefinition.id;

        List<String> installationId = new ArrayList();

        var response = assignMetric("admin", requestForMetric, installationId);

        var metric = response
                .then()
                .assertThat()
                .statusCode(201)
                .extract()
                .as(MetricResponseDto.class);

        // update an existing metric
        var updateMetricRequest = new UpdateMetricRequestDto();

        updateMetricRequest.start = "2023-01-0509:13:07";

        var informativeResponse = given()
                .basePath("accounting-system/installations")
                .auth()
                .oauth2(getAccessToken("admin"))
                .body(updateMetricRequest)
                .contentType(ContentType.JSON)
                .patch("/{installation_id}/metrics/{id}", installationId.get(0), metric.id)
                .then()
                .assertThat()
                .statusCode(400)
                .extract()
                .as(InformativeResponse.class);

        assertEquals("time_period_start must be a valid zulu timestamp. found: 2023-01-0509:13:07", informativeResponse.message);
    }

    @Test
    public void updateMetricMetricDefinitionNotFound() {

        var requestForMetricDefinition = new MetricDefinitionRequestDto();

        requestForMetricDefinition.metricName = "metric";
        requestForMetricDefinition.metricDescription = "description";
        requestForMetricDefinition.unitType = "TB";
        requestForMetricDefinition.metricType = "aggregated";

        var createdMetricDefinition = createMetricDefinition(requestForMetricDefinition, "admin");

        // create a metric
        var requestForMetric = new MetricRequestDto();
        requestForMetric.start = "2022-01-05T09:13:07Z";
        requestForMetric.end = "2022-01-05T09:14:07Z";
        requestForMetric.value = 10.8;
        requestForMetric.metricDefinitionId = createdMetricDefinition.id;

        List<String> installationId = new ArrayList();

        var response = assignMetric("admin", requestForMetric, installationId);

        var metric = response
                .then()
                .assertThat()
                .statusCode(201)
                .extract()
                .as(MetricResponseDto.class);

        // update an existing metric
        var updateMetricRequest = new UpdateMetricRequestDto();

        updateMetricRequest.start = "2023-01-05T09:13:07Z";
        updateMetricRequest.end = "2024-01-05T09:13:07Z";
        updateMetricRequest.value = 15.8;
        updateMetricRequest.metricDefinitionId = "507f1f77bcf86cd799439011";

        var informativeResponse = given()
                .basePath("accounting-system/installations")
                .auth()
                .oauth2(getAccessToken("admin"))
                .body(updateMetricRequest)
                .contentType(ContentType.JSON)
                .patch("/{installation_id}/metrics/{id}", installationId.get(0), metric.id)
                .then()
                .assertThat()
                .statusCode(404)
                .extract()
                .as(InformativeResponse.class);

        assertEquals("There is no Metric Definition with the following id: 507f1f77bcf86cd799439011", informativeResponse.message);
    }

    @Test
    public void updateMetricModifiedStartCannotBeAfterEnd() {

        //first, create a metric definition
        var requestForMetricDefinition = new MetricDefinitionRequestDto();

        requestForMetricDefinition.metricName = "metric";
        requestForMetricDefinition.metricDescription = "description";
        requestForMetricDefinition.unitType = "TB";
        requestForMetricDefinition.metricType = "aggregated";

        var createdMetricDefinition = createMetricDefinition(requestForMetricDefinition, "admin");

        // create a metric
        var requestForMetric = new MetricRequestDto();
        requestForMetric.start = "2021-01-05T09:13:07Z";
        requestForMetric.end = "2022-01-05T09:14:07Z";
        requestForMetric.value = 10.8;
        requestForMetric.metricDefinitionId = createdMetricDefinition.id;

        List<String> installationId = new ArrayList();

        var response = assignMetric("admin", requestForMetric, installationId);

        var metric = response
                .then()
                .assertThat()
                .statusCode(201)
                .extract()
                .as(MetricResponseDto.class);

        // update an existing metric
        var updateMetricRequest = new UpdateMetricRequestDto();

        updateMetricRequest.start = "2023-01-05T09:13:07Z";

        var informativeResponse = given()
                .basePath("accounting-system/installations")
                .auth()
                .oauth2(getAccessToken("admin"))
                .body(updateMetricRequest)
                .contentType(ContentType.JSON)
                .patch("/{installation_id}/metrics/{id}", installationId.get(0), metric.id)
                .then()
                .assertThat()
                .statusCode(400)
                .extract()
                .as(InformativeResponse.class);

        assertEquals("Timestamp of the starting date time cannot be after of Timestamp of the end date time.", informativeResponse.message);
    }

    @Test
    public void updateMetricModifiedStartCannotBeAfterModifiedEnd() {

        //first, create a metric definition
        var requestForMetricDefinition = new MetricDefinitionRequestDto();

        requestForMetricDefinition.metricName = "metric";
        requestForMetricDefinition.metricDescription = "description";
        requestForMetricDefinition.unitType = "TB";
        requestForMetricDefinition.metricType = "aggregated";

        var createdMetricDefinition = createMetricDefinition(requestForMetricDefinition, "admin");

        // create a metric
        var requestForMetric = new MetricRequestDto();
        requestForMetric.start = "2021-01-05T09:13:07Z";
        requestForMetric.end = "2022-01-05T09:14:07Z";
        requestForMetric.value = 10.8;
        requestForMetric.metricDefinitionId = createdMetricDefinition.id;

        List<String> installationId = new ArrayList();

        var response = assignMetric("admin", requestForMetric, installationId);

        var metric = response
                .then()
                .assertThat()
                .statusCode(201)
                .extract()
                .as(MetricResponseDto.class);

        // update an existing metric
        var updateMetricRequest = new UpdateMetricRequestDto();

        updateMetricRequest.start = "2025-01-05T09:13:07Z";
        updateMetricRequest.end = "2024-01-05T09:13:07Z";

        var informativeResponse = given()
                .basePath("accounting-system/installations")
                .auth()
                .oauth2(getAccessToken("admin"))
                .body(updateMetricRequest)
                .contentType(ContentType.JSON)
                .patch("/{installation_id}/metrics/{id}", installationId.get(0), metric.id)
                .then()
                .assertThat()
                .statusCode(400)
                .extract()
                .as(InformativeResponse.class);

        assertEquals("Timestamp of the starting date time cannot be after of Timestamp of the end date time.", informativeResponse.message);
    }

    @Test
    public void updateMetricModifiedEndCannotBeBeforeStart() {

        //first, create a metric definition
        var requestForMetricDefinition = new MetricDefinitionRequestDto();

        requestForMetricDefinition.metricName = "metric";
        requestForMetricDefinition.metricDescription = "description";
        requestForMetricDefinition.unitType = "TB";
        requestForMetricDefinition.metricType = "aggregated";

        var createdMetricDefinition = createMetricDefinition(requestForMetricDefinition, "admin");

        // create a metric
        var requestForMetric = new MetricRequestDto();
        requestForMetric.start = "2021-01-05T09:13:07Z";
        requestForMetric.end = "2022-01-05T09:14:07Z";
        requestForMetric.value = 10.8;
        requestForMetric.metricDefinitionId = createdMetricDefinition.id;

        List<String> installationId = new ArrayList();

        var response = assignMetric("admin", requestForMetric, installationId);

        var metric = response
                .then()
                .assertThat()
                .statusCode(201)
                .extract()
                .as(MetricResponseDto.class);

        // update an existing metric
        var updateMetricRequest = new UpdateMetricRequestDto();

        updateMetricRequest.end = "2020-01-05T09:13:07Z";

        var informativeResponse = given()
                .basePath("accounting-system/installations")
                .auth()
                .oauth2(getAccessToken("admin"))
                .body(updateMetricRequest)
                .contentType(ContentType.JSON)
                .patch("/{installation_id}/metrics/{id}", installationId.get(0), metric.id)
                .then()
                .assertThat()
                .statusCode(400)
                .extract()
                .as(InformativeResponse.class);

        assertEquals("Timestamp of the end date time cannot be before of Timestamp of the starting date time.", informativeResponse.message);
    }

    @Test
    public void updateMetricModifiedStartCannotBeEqualToModifiedEnd() {

        //first, create a metric definition
        var requestForMetricDefinition = new MetricDefinitionRequestDto();

        requestForMetricDefinition.metricName = "metric";
        requestForMetricDefinition.metricDescription = "description";
        requestForMetricDefinition.unitType = "TB";
        requestForMetricDefinition.metricType = "aggregated";

        var createdMetricDefinition = createMetricDefinition(requestForMetricDefinition, "admin");

        // create a metric
        var requestForMetric = new MetricRequestDto();
        requestForMetric.start = "2021-01-05T09:13:07Z";
        requestForMetric.end = "2022-01-05T09:14:07Z";
        requestForMetric.value = 10.8;
        requestForMetric.metricDefinitionId = createdMetricDefinition.id;

        List<String> installationId = new ArrayList();

        var response = assignMetric("admin", requestForMetric, installationId);

        var metric = response
                .then()
                .assertThat()
                .statusCode(201)
                .extract()
                .as(MetricResponseDto.class);

        // update an existing metric
        var updateMetricRequest = new UpdateMetricRequestDto();

        updateMetricRequest.start = "2024-01-05T09:13:07Z";
        updateMetricRequest.end = "2024-01-05T09:13:07Z";


        var informativeResponse = given()
                .basePath("accounting-system/installations")
                .auth()
                .oauth2(getAccessToken("admin"))
                .body(updateMetricRequest)
                .contentType(ContentType.JSON)
                .patch("/{installation_id}/metrics/{id}", installationId.get(0), metric.id)
                .then()
                .assertThat()
                .statusCode(400)
                .extract()
                .as(InformativeResponse.class);

        assertEquals("Timestamp of the starting date time cannot be equal to Timestamp of the end date time.", informativeResponse.message);
    }

    @Test
    public void updateMetricModifiedStartCannotBeEqualToEnd() {

        //first, create a metric definition
        var requestForMetricDefinition = new MetricDefinitionRequestDto();

        requestForMetricDefinition.metricName = "metric";
        requestForMetricDefinition.metricDescription = "description";
        requestForMetricDefinition.unitType = "TB";
        requestForMetricDefinition.metricType = "aggregated";

        var createdMetricDefinition = createMetricDefinition(requestForMetricDefinition, "admin");

        // create a metric
        var requestForMetric = new MetricRequestDto();
        requestForMetric.start = "2021-01-05T09:13:07Z";
        requestForMetric.end = "2022-01-05T09:14:07Z";
        requestForMetric.value = 10.8;
        requestForMetric.metricDefinitionId = createdMetricDefinition.id;

        List<String> installationId = new ArrayList();

        var response = assignMetric("admin", requestForMetric, installationId);

        var metric = response
                .then()
                .assertThat()
                .statusCode(201)
                .extract()
                .as(MetricResponseDto.class);

        // update an existing metric
        var updateMetricRequest = new UpdateMetricRequestDto();

        updateMetricRequest.start = "2022-01-05T09:14:07Z";

        var informativeResponse = given()
                .basePath("accounting-system/installations")
                .auth()
                .oauth2(getAccessToken("admin"))
                .body(updateMetricRequest)
                .contentType(ContentType.JSON)
                .patch("/{installation_id}/metrics/{id}", installationId.get(0), metric.id)
                .then()
                .assertThat()
                .statusCode(400)
                .extract()
                .as(InformativeResponse.class);

        assertEquals("Timestamp of the starting date time cannot be equal to Timestamp of the end date time.", informativeResponse.message);
    }

    @Test
    public void updateMetricStartCannotBeEqualToModifiedEnd() {

        //first, create a metric definition
        var requestForMetricDefinition = new MetricDefinitionRequestDto();

        requestForMetricDefinition.metricName = "metric";
        requestForMetricDefinition.metricDescription = "description";
        requestForMetricDefinition.unitType = "TB";
        requestForMetricDefinition.metricType = "aggregated";

        var createdMetricDefinition = createMetricDefinition(requestForMetricDefinition, "admin");

        // create a metric
        var requestForMetric = new MetricRequestDto();
        requestForMetric.start = "2021-01-05T09:13:07Z";
        requestForMetric.end = "2022-01-05T09:14:07Z";
        requestForMetric.value = 10.8;
        requestForMetric.metricDefinitionId = createdMetricDefinition.id;

        List<String> installationId = new ArrayList();

        var response = assignMetric("admin", requestForMetric, installationId);

        var metric = response
                .then()
                .assertThat()
                .statusCode(201)
                .extract()
                .as(MetricResponseDto.class);

        // update an existing metric
        var updateMetricRequest = new UpdateMetricRequestDto();

        updateMetricRequest.end = "2021-01-05T09:13:07Z";

        var informativeResponse = given()
                .basePath("accounting-system/installations")
                .auth()
                .oauth2(getAccessToken("admin"))
                .body(updateMetricRequest)
                .contentType(ContentType.JSON)
                .patch("/{installation_id}/metrics/{id}", installationId.get(0), metric.id)
                .then()
                .assertThat()
                .statusCode(400)
                .extract()
                .as(InformativeResponse.class);

        assertEquals("Timestamp of the starting date time cannot be equal to Timestamp of the end date time.", informativeResponse.message);
    }

    @Test
    public void updateMetricFull() {

        //first, create a metric definition
        var requestForMetricDefinition = new MetricDefinitionRequestDto();

        requestForMetricDefinition.metricName = "metric";
        requestForMetricDefinition.metricDescription = "description";
        requestForMetricDefinition.unitType = "TB";
        requestForMetricDefinition.metricType = "aggregated";

        var createdMetricDefinition = createMetricDefinition(requestForMetricDefinition, "admin");

        //create another metric definition
        var requestForMetricDefinition1 = new MetricDefinitionRequestDto();

        requestForMetricDefinition1.metricName = "metric";
        requestForMetricDefinition1.metricDescription = "description";
        requestForMetricDefinition1.unitType = "#";
        requestForMetricDefinition1.metricType = "count";

        var createdMetricDefinition1 = createMetricDefinition(requestForMetricDefinition1, "admin");


        // create a metric
        var requestForMetric = new MetricRequestDto();
        requestForMetric.start = "2020-01-05T09:13:07Z";
        requestForMetric.end = "2020-01-05T09:14:07Z";
        requestForMetric.value = 10.8;
        requestForMetric.metricDefinitionId = createdMetricDefinition.id;

        List<String> installationId = new ArrayList();

        var response = assignMetric("admin", requestForMetric, installationId);

        var metric = response
                .then()
                .assertThat()
                .statusCode(201)
                .extract()
                .as(MetricResponseDto.class);

        // update an existing metric
        var updateMetricRequest = new UpdateMetricRequestDto();

        updateMetricRequest.start = "2022-01-05T09:13:07Z";
        updateMetricRequest.end = "2022-01-05T09:14:07Z";
        updateMetricRequest.value = 15.8;
        updateMetricRequest.metricDefinitionId = createdMetricDefinition1.id;

        var updateResponse = given()
                .basePath("accounting-system/installations")
                .auth()
                .oauth2(getAccessToken("admin"))
                .config(RestAssured.config()
                        .jsonConfig(JsonConfig.jsonConfig()
                                .numberReturnType(JsonPathConfig.NumberReturnType.DOUBLE)))
                .body(updateMetricRequest)
                .contentType(ContentType.JSON)
                .patch("/{installation_id}/metrics/{id}", installationId.get(0), metric.id)
                .then()
                .assertThat()
                .statusCode(200)
                .body("metric_id", is(metric.id))
                .body("metric_definition_id", is(updateMetricRequest.metricDefinitionId))
                .body("time_period_start", is(updateMetricRequest.start))
                .body("time_period_end", is(updateMetricRequest.end))
                .body("value", is(updateMetricRequest.value))
                .extract()
                .as(MetricResponseDto.class);

        assertEquals(createdMetricDefinition1.id, updateResponse.metricDefinitionId);
    }

    @Test
    public void updateMetricPartial() {

        //first, create a metric definition
        var requestForMetricDefinition = new MetricDefinitionRequestDto();

        requestForMetricDefinition.metricName = "metric";
        requestForMetricDefinition.metricDescription = "description";
        requestForMetricDefinition.unitType = "TB";
        requestForMetricDefinition.metricType = "aggregated";

        var createdMetricDefinition = createMetricDefinition(requestForMetricDefinition, "admin");

        // create a metric
        var requestForMetric = new MetricRequestDto();
        requestForMetric.start = "2022-01-05T09:13:07Z";
        requestForMetric.end = "2022-01-05T09:14:07Z";
        requestForMetric.value = 10.8;
        requestForMetric.metricDefinitionId = createdMetricDefinition.id;

        List<String> installationId = new ArrayList();

        var response = assignMetric("admin", requestForMetric, installationId);

        var metric = response
                .then()
                .assertThat()
                .statusCode(201)
                .extract()
                .as(MetricResponseDto.class);

        // update an existing metric
        var updateMetricRequest = new UpdateMetricRequestDto();

        updateMetricRequest.value = 12.0;

        var updateResponse = given()
                .basePath("accounting-system/installations")
                .auth()
                .oauth2(getAccessToken("admin"))
                .config(RestAssured.config()
                        .jsonConfig(JsonConfig.jsonConfig()
                                .numberReturnType(JsonPathConfig.NumberReturnType.DOUBLE)))
                .body(updateMetricRequest)
                .contentType(ContentType.JSON)
                .patch("/{installation_id}/metrics/{id}", installationId.get(0), metric.id)
                .then()
                .assertThat()
                .statusCode(200)
                .body("metric_id", is(metric.id))
                .body("metric_definition_id", is(requestForMetric.metricDefinitionId))
                .body("time_period_start", is(requestForMetric.start))
                .body("time_period_end", is(requestForMetric.end))
                .body("value", is(updateMetricRequest.value))
                .extract()
                .as(MetricResponseDto.class);

        assertEquals(requestForMetric.metricDefinitionId, updateResponse.metricDefinitionId);
    }

    private io.restassured.response.Response assignMetric(String user, MetricRequestDto body, List<String> installationId){

        projectRepository.associateProjectWithProvider("777536", "grnet");

        //Registering an installation
        var requestForMetricDefinition = new MetricDefinitionRequestDto();

        requestForMetricDefinition.metricName = "metric";
        requestForMetricDefinition.metricDescription = "description";
        requestForMetricDefinition.unitType = "TB/year";
        requestForMetricDefinition.metricType = "aggregated";

        var metricDefinitionResponse = createMetricDefinition(requestForMetricDefinition, "admin");

        var request= new InstallationRequestDto();

        request.project = "777536";
        request.organisation = "grnet";
        request.infrastructure = "okeanos-knossos";
        request.installation = "SECOND";
        request.unitOfAccess = metricDefinitionResponse.id;

        var installation = createInstallation(request, "admin");

        installationId.add(installation.id);

        return given()
                .auth()
                .oauth2(getAccessToken(user))
                .basePath("accounting-system/installations")
                .body(body)
                .contentType(ContentType.JSON)
                .post("/{installationId}/metrics", installation.id);
    }

    private io.restassured.response.Response assignMetric(String user, MetricRequestDto body, String installationId){

        return given()
                .auth()
                .oauth2(getAccessToken(user))
                .basePath("accounting-system/installations")
                .body(body)
                .contentType(ContentType.JSON)
                .post("/{installationId}/metrics", installationId);
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

    private String assignMetricsToSpecificInstallation(){

        var requestForMetricDefinition = new MetricDefinitionRequestDto();

        requestForMetricDefinition.metricName = "metric";
        requestForMetricDefinition.metricDescription = "description";
        requestForMetricDefinition.unitType = "TB";
        requestForMetricDefinition.metricType = "aggregated";

        var metricDefinitionResponse = createMetricDefinition(requestForMetricDefinition, "admin");

        var requestForMetric = new MetricRequestDto();
        requestForMetric.start = "2021-01-05T09:15:07Z";
        requestForMetric.end = "2021-01-05T09:18:07Z";
        requestForMetric.value = 10.8;
        requestForMetric.metricDefinitionId = metricDefinitionResponse.id;

        var request= new InstallationRequestDto();

        request.project = "777536";
        request.organisation = "grnet";
        request.infrastructure = "okeanos-knossos";
        request.installation = "SECOND";
        request.unitOfAccess = metricDefinitionResponse.id;

        var installation = createInstallation(request, "admin");

        var response = assignMetric("admin", requestForMetric, installation.id);

        response
                .then()
                .assertThat()
                .statusCode(201);

        var requestForMetric1 = new MetricRequestDto();
        requestForMetric1.start = "2020-01-05T09:15:07Z";
        requestForMetric1.end = "2020-01-05T09:18:07Z";
        requestForMetric1.value = 15;
        requestForMetric1.metricDefinitionId = metricDefinitionResponse.id;

        var response1 = assignMetric("admin", requestForMetric1, installation.id);

        response1
                .then()
                .assertThat()
                .statusCode(201);

        var requestForMetric2 = new MetricRequestDto();
        requestForMetric2.start = "2019-01-05T09:15:07Z";
        requestForMetric2.end = "2019-01-05T09:18:07Z";
        requestForMetric2.value = 20;
        requestForMetric2.metricDefinitionId = metricDefinitionResponse.id;

        var response2 = assignMetric("admin", requestForMetric2, installation.id);

        response2
                .then()
                .assertThat()
                .statusCode(201);

        return installation.id;
    }

    private Array<String> assignMetricsToSpecificInstallationMultipleMetricDefinitions(){

        var requestForMetricDefinition = new MetricDefinitionRequestDto();

        requestForMetricDefinition.metricName = "metric";
        requestForMetricDefinition.metricDescription = "description";
        requestForMetricDefinition.unitType = "TB";
        requestForMetricDefinition.metricType = "aggregated";

        var metricDefinitionResponse = createMetricDefinition(requestForMetricDefinition, "admin");

        var requestForMetricDefinition1 = new MetricDefinitionRequestDto();

        requestForMetricDefinition1.metricName = "metric";
        requestForMetricDefinition1.metricDescription = "description";
        requestForMetricDefinition1.unitType = "TB/year";
        requestForMetricDefinition1.metricType = "count";

        var metricDefinitionResponse1 = createMetricDefinition(requestForMetricDefinition1, "admin");

        var requestForMetric = new MetricRequestDto();
        requestForMetric.start = "2021-01-05T09:15:07Z";
        requestForMetric.end = "2021-01-05T09:18:07Z";
        requestForMetric.value = 10.8;
        requestForMetric.metricDefinitionId = metricDefinitionResponse.id;

        var request= new InstallationRequestDto();

        request.project = "777536";
        request.organisation = "grnet";
        request.infrastructure = "okeanos-knossos";
        request.installation = "SECOND";
        request.unitOfAccess = metricDefinitionResponse.id;

        var installation = createInstallation(request, "admin");

        var response = assignMetric("admin", requestForMetric, installation.id);

        response
                .then()
                .assertThat()
                .statusCode(201);

        var requestForMetric1 = new MetricRequestDto();
        requestForMetric1.start = "2020-01-05T09:15:07Z";
        requestForMetric1.end = "2020-01-05T09:18:07Z";
        requestForMetric1.value = 15;
        requestForMetric1.metricDefinitionId = metricDefinitionResponse1.id;

        var response1 = assignMetric("admin", requestForMetric1, installation.id);

        response1
                .then()
                .assertThat()
                .statusCode(201);

        var requestForMetric2 = new MetricRequestDto();
        requestForMetric2.start = "2019-01-05T09:15:07Z";
        requestForMetric2.end = "2019-01-05T09:18:07Z";
        requestForMetric2.value = 20;
        requestForMetric2.metricDefinitionId = metricDefinitionResponse1.id;

        var response2 = assignMetric("admin", requestForMetric2, installation.id);

        response2
                .then()
                .assertThat()
                .statusCode(201);

        return Array.of(metricDefinitionResponse.id, metricDefinitionResponse1.id);
    }
}

