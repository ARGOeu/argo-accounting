package org.accounting.system;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.keycloak.client.KeycloakTestClient;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import org.accounting.system.clients.ProviderClient;
import org.accounting.system.clients.responses.eoscportal.Response;
import org.accounting.system.clients.responses.eoscportal.Total;
import org.accounting.system.dtos.admin.ProjectRegistrationRequest;
import org.accounting.system.mappers.ProviderMapper;
import org.accounting.system.repositories.client.ClientAccessAlwaysRepository;
import org.accounting.system.repositories.client.ClientRepository;
import org.accounting.system.repositories.metric.MetricRepository;
import org.accounting.system.repositories.metricdefinition.MetricDefinitionRepository;
import org.accounting.system.repositories.project.ProjectRepository;
import org.accounting.system.repositories.provider.ProviderRepository;
import org.accounting.system.services.client.ClientService;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.json.simple.parser.ParseException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;

import java.util.Set;
import java.util.concurrent.ExecutionException;

import static io.restassured.RestAssured.given;

@QuarkusTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PrepareTest {

    @Inject
    @RestClient
    ProviderClient providerClient;

    @Inject
    ProviderRepository providerRepository;

    @Inject
    ClientRepository clientRepository;

    @Inject
    ClientService clientService;

    @Inject
    MetricDefinitionRepository metricDefinitionRepository;

    @Inject
    ProjectRepository projectRepository;

    @Inject
    MetricRepository metricRepository;

    @Inject
    ClientAccessAlwaysRepository clientAccessAlwaysRepository;


    KeycloakTestClient keycloakClient = new KeycloakTestClient();

    @BeforeAll
    public void setup() throws ExecutionException, InterruptedException, ParseException {

        Total total = providerClient.getTotalNumberOfProviders("all").toCompletableFuture().get();

        Response response = providerClient.getAll("all", total.total).toCompletableFuture().get();

        providerRepository.persistOrUpdate(ProviderMapper.INSTANCE.eoscProvidersToProviders(response.results));

        clientRepository.addSystemAdmin("admin_voperson_id", "admin", "admin@email.com");

        clientService.register("creator_voperson_id", "creator", "creator@email.com");
        clientService.register("project_admin@example.org", "project_admin", "project_admin@example.org");
        clientService.register("provider_admin@example.org", "provider_admin", "provider_admin@example.org");
        clientAccessAlwaysRepository.assignRolesToRegisteredClient("creator_voperson_id", Set.of("metric_definition_creator"));
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
    protected String getAccessToken(String userName) {
        return keycloakClient.getAccessToken(userName);
    }
}