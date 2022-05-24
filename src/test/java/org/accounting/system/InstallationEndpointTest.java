package org.accounting.system;

import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import io.quarkus.test.junit.mockito.InjectMock;
import io.quarkus.test.keycloak.client.KeycloakTestClient;
import io.restassured.http.ContentType;
import org.accounting.system.clients.ProviderClient;
import org.accounting.system.clients.responses.eoscportal.Response;
import org.accounting.system.clients.responses.eoscportal.Total;
import org.accounting.system.dtos.InformativeResponse;
import org.accounting.system.dtos.installation.InstallationRequestDto;
import org.accounting.system.dtos.installation.InstallationResponseDto;
import org.accounting.system.dtos.metricdefinition.MetricDefinitionRequestDto;
import org.accounting.system.dtos.metricdefinition.MetricDefinitionResponseDto;
import org.accounting.system.endpoints.InstallationEndpoint;
import org.accounting.system.mappers.ProviderMapper;
import org.accounting.system.repositories.installation.InstallationRepository;
import org.accounting.system.repositories.metricdefinition.MetricDefinitionRepository;
import org.accounting.system.repositories.provider.ProviderRepository;
import org.accounting.system.services.ReadPredefinedTypesService;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mockito;

import javax.inject.Inject;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;

@QuarkusTest
@TestProfile(AccountingSystemTestProfile.class)
@TestHTTPEndpoint(InstallationEndpoint.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class InstallationEndpointTest {

    @Inject
    InstallationRepository installationRepository;

    @Inject
    MetricDefinitionRepository metricDefinitionRepository;

    @Inject
    ProviderRepository providerRepository;

    @InjectMock
    ReadPredefinedTypesService readPredefinedTypesService;

    @Inject
    @RestClient
    ProviderClient providerClient;

    KeycloakTestClient keycloakClient = new KeycloakTestClient();

    @BeforeAll
    public void setup() throws ExecutionException, InterruptedException {

        Total total = providerClient.getTotalNumberOfProviders().toCompletableFuture().get();

        Response response = providerClient.getAll(total.total).toCompletableFuture().get();

        providerRepository.persistOrUpdate(ProviderMapper.INSTANCE.eoscProvidersToProviders(response.results));
    }

    @BeforeEach
    public void before() {
        installationRepository.deleteAll();
        metricDefinitionRepository.deleteAll();
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
    public void createInstallationNoRequiredEntities(){

        Mockito.when(readPredefinedTypesService.searchForUnitType(any())).thenReturn(Optional.of("SECOND"));
        Mockito.when(readPredefinedTypesService.searchForMetricType(any())).thenReturn(Optional.of("Aggregated"));
        var requestForMetricDefinition = new MetricDefinitionRequestDto();

        requestForMetricDefinition.metricName = "metric";
        requestForMetricDefinition.metricDescription = "description";
        requestForMetricDefinition.unitType = "SECOND";
        requestForMetricDefinition.metricType = "Aggregated";

        var metricDefinitionResponse = createMetricDefinition(requestForMetricDefinition, "admin");

        var requestMetricDefinitionNotFound= new InstallationRequestDto();

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

        Mockito.when(readPredefinedTypesService.searchForUnitType(any())).thenReturn(Optional.of("SECOND"));
        Mockito.when(readPredefinedTypesService.searchForMetricType(any())).thenReturn(Optional.of("Aggregated"));
        var requestForMetricDefinition = new MetricDefinitionRequestDto();

        requestForMetricDefinition.metricName = "metric";
        requestForMetricDefinition.metricDescription = "description";
        requestForMetricDefinition.unitType = "SECOND";
        requestForMetricDefinition.metricType = "Aggregated";

        var metricDefinitionResponse = createMetricDefinition(requestForMetricDefinition, "admin");

        var requestNoOrganisation= new InstallationRequestDto();

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

        var requestNoInfrastructure= new InstallationRequestDto();

        requestNoInfrastructure.organisation = "grnet";
        requestNoInfrastructure.installation = "SECOND";
        requestNoInfrastructure.unitOfAccess = metricDefinitionResponse.id;

        InformativeResponse responseNoInfrastructure = given()
                .auth()
                .oauth2(getAccessToken("admin"))
                .body(requestNoInfrastructure)
                .contentType(ContentType.JSON)
                .post()
                .then()
                .assertThat()
                .statusCode(400)
                .extract()
                .as(InformativeResponse.class);

        assertEquals("infrastructure may not be empty.", responseNoInfrastructure.message);

        var requestNoInstallation= new InstallationRequestDto();

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

        Mockito.when(readPredefinedTypesService.searchForUnitType(any())).thenReturn(Optional.of("SECOND"));
        Mockito.when(readPredefinedTypesService.searchForMetricType(any())).thenReturn(Optional.of("Aggregated"));
        var requestForMetricDefinition = new MetricDefinitionRequestDto();

        requestForMetricDefinition.metricName = "metric";
        requestForMetricDefinition.metricDescription = "description";
        requestForMetricDefinition.unitType = "SECOND";
        requestForMetricDefinition.metricType = "Aggregated";

        var metricDefinitionResponse = createMetricDefinition(requestForMetricDefinition, "admin");

        var request= new InstallationRequestDto();

        request.organisation = "grnet";
        request.infrastructure = "okeanos-knossos";
        request.installation = "SECOND";
        request.unitOfAccess = metricDefinitionResponse.id;

        createInstallation(request, "admin");

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

        assertEquals("There is already an installation with {organisation, infrastructure, installation}: " + "{"+request.organisation+", "+request.infrastructure+", "+request.installation+"}.", informativeResponse.message);
    }

    @Test
    public void createInstallation() {

        Mockito.when(readPredefinedTypesService.searchForUnitType(any())).thenReturn(Optional.of("SECOND"));
        Mockito.when(readPredefinedTypesService.searchForMetricType(any())).thenReturn(Optional.of("Aggregated"));
        var requestForMetricDefinition = new MetricDefinitionRequestDto();

        requestForMetricDefinition.metricName = "metric";
        requestForMetricDefinition.metricDescription = "description";
        requestForMetricDefinition.unitType = "SECOND";
        requestForMetricDefinition.metricType = "Aggregated";

        var metricDefinitionResponse = createMetricDefinition(requestForMetricDefinition, "admin");

        var request= new InstallationRequestDto();

        request.organisation = "grnet";
        request.infrastructure = "okeanos-knossos";
        request.installation = "SECOND";
        request.unitOfAccess = metricDefinitionResponse.id;

       var response = createInstallation(request, "admin");

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

        Mockito.when(readPredefinedTypesService.searchForUnitType(any())).thenReturn(Optional.of("SECOND"));
        Mockito.when(readPredefinedTypesService.searchForMetricType(any())).thenReturn(Optional.of("Aggregated"));
        var requestForMetricDefinition = new MetricDefinitionRequestDto();

        requestForMetricDefinition.metricName = "metric";
        requestForMetricDefinition.metricDescription = "description";
        requestForMetricDefinition.unitType = "SECOND";
        requestForMetricDefinition.metricType = "Aggregated";

        var metricDefinitionResponse = createMetricDefinition(requestForMetricDefinition, "admin");

        var request= new InstallationRequestDto();

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

        Mockito.when(readPredefinedTypesService.searchForUnitType(any())).thenReturn(Optional.of("SECOND"));
        Mockito.when(readPredefinedTypesService.searchForMetricType(any())).thenReturn(Optional.of("Aggregated"));
        var requestForMetricDefinition = new MetricDefinitionRequestDto();

        requestForMetricDefinition.metricName = "metric";
        requestForMetricDefinition.metricDescription = "description";
        requestForMetricDefinition.unitType = "SECOND";
        requestForMetricDefinition.metricType = "Aggregated";

        var metricDefinitionResponse = createMetricDefinition(requestForMetricDefinition, "admin");

        var request= new InstallationRequestDto();

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

        Mockito.when(readPredefinedTypesService.searchForUnitType(any())).thenReturn(Optional.of("SECOND"));
        Mockito.when(readPredefinedTypesService.searchForMetricType(any())).thenReturn(Optional.of("Aggregated"));
        var requestForMetricDefinition = new MetricDefinitionRequestDto();

        requestForMetricDefinition.metricName = "metric";
        requestForMetricDefinition.metricDescription = "description";
        requestForMetricDefinition.unitType = "SECOND";
        requestForMetricDefinition.metricType = "Aggregated";

        var metricDefinitionResponse = createMetricDefinition(requestForMetricDefinition, "admin");

        var request= new InstallationRequestDto();

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

        Mockito.when(readPredefinedTypesService.searchForUnitType(any())).thenReturn(Optional.of("SECOND"));
        Mockito.when(readPredefinedTypesService.searchForMetricType(any())).thenReturn(Optional.of("Aggregated"));
        var requestForMetricDefinition = new MetricDefinitionRequestDto();

        requestForMetricDefinition.metricName = "metric";
        requestForMetricDefinition.metricDescription = "description";
        requestForMetricDefinition.unitType = "SECOND";
        requestForMetricDefinition.metricType = "Aggregated";

        var metricDefinitionResponse = createMetricDefinition(requestForMetricDefinition, "admin");

        var request= new InstallationRequestDto();

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
    public void updateInstallationNoProvider() {

        Mockito.when(readPredefinedTypesService.searchForUnitType(any())).thenReturn(Optional.of("SECOND"));
        Mockito.when(readPredefinedTypesService.searchForMetricType(any())).thenReturn(Optional.of("Aggregated"));
        var requestForMetricDefinition = new MetricDefinitionRequestDto();

        requestForMetricDefinition.metricName = "metric";
        requestForMetricDefinition.metricDescription = "description";
        requestForMetricDefinition.unitType = "SECOND";
        requestForMetricDefinition.metricType = "Aggregated";

        var metricDefinitionResponse = createMetricDefinition(requestForMetricDefinition, "admin");

        var request= new InstallationRequestDto();

        request.organisation = "grnet";
        request.infrastructure = "okeanos-knossos";
        request.installation = "SECOND";
        request.unitOfAccess = metricDefinitionResponse.id;

        var installation = createInstallation(request, "admin");

        var requestForUpdating= new InstallationRequestDto();

        requestForUpdating.organisation = "GRNET";

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

        assertEquals("There is no Provider with the following id: GRNET", informativeResponse.message);
    }

    @Test
    public void updateInstallationPartial() {

        Mockito.when(readPredefinedTypesService.searchForUnitType(any())).thenReturn(Optional.of("SECOND"));
        Mockito.when(readPredefinedTypesService.searchForMetricType(any())).thenReturn(Optional.of("Aggregated"));
        var requestForMetricDefinition = new MetricDefinitionRequestDto();

        requestForMetricDefinition.metricName = "metric";
        requestForMetricDefinition.metricDescription = "description";
        requestForMetricDefinition.unitType = "SECOND";
        requestForMetricDefinition.metricType = "Aggregated";

        var metricDefinitionResponse = createMetricDefinition(requestForMetricDefinition, "admin");

        var request= new InstallationRequestDto();

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

        Mockito.when(readPredefinedTypesService.searchForUnitType(any())).thenReturn(Optional.of("SECOND"));
        Mockito.when(readPredefinedTypesService.searchForMetricType(any())).thenReturn(Optional.of("Aggregated"));
        var requestForMetricDefinition = new MetricDefinitionRequestDto();

        requestForMetricDefinition.metricName = "metric";
        requestForMetricDefinition.metricDescription = "description";
        requestForMetricDefinition.unitType = "SECOND";
        requestForMetricDefinition.metricType = "Aggregated";

        var metricDefinitionResponse = createMetricDefinition(requestForMetricDefinition, "admin");

        var request= new InstallationRequestDto();

        request.organisation = "grnet";
        request.infrastructure = "okeanos-knossos";
        request.installation = "SECOND";
        request.unitOfAccess = metricDefinitionResponse.id;

        var installation = createInstallation(request, "admin");

        Mockito.when(readPredefinedTypesService.searchForUnitType(any())).thenReturn(Optional.of("KG"));
        Mockito.when(readPredefinedTypesService.searchForMetricType(any())).thenReturn(Optional.of("Aggregated"));
        var requestForMetricDefinition1 = new MetricDefinitionRequestDto();

        requestForMetricDefinition1.metricName = "metric";
        requestForMetricDefinition1.metricDescription = "description";
        requestForMetricDefinition1.unitType = "KG";
        requestForMetricDefinition1.metricType = "Aggregated";

        var metricDefinitionResponse1 = createMetricDefinition(requestForMetricDefinition1, "admin");


        var requestForUpdating= new InstallationRequestDto();

        requestForUpdating.organisation = "sites";
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

        assertEquals(requestForUpdating.organisation, updatedInstallation.organisation);
        assertEquals(requestForUpdating.infrastructure, updatedInstallation.infrastructure);
        assertEquals(requestForUpdating.installation, updatedInstallation.installation);
        assertEquals(requestForUpdating.unitOfAccess, updatedInstallation.metricDefinition.id);
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
                .basePath("accounting-system/metric-definition")
                .body(request)
                .contentType(ContentType.JSON)
                .post()
                .then()
                .assertThat()
                .statusCode(201)
                .extract()
                .as(MetricDefinitionResponseDto.class);
    }

    protected String getAccessToken(String userName) {
        return keycloakClient.getAccessToken(userName);
    }
}
