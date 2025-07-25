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

        clientRepository.addSystemAdmin("6b6a782e0374dc4a743ff02409df46af681f25dc01e3297f2637ba8f8409a9bd", "admin", "admin@email.com");

        clientService.register("f2e411a7ab877643ab82f637778230973df4cbee013f2a2a333dac7157addc55", "creator", "creator@email.com", "http://localhost:58080/realm/quarkus");
        clientService.register("b4b181f7e2ac034e11a1552be109efa50efd82115d64e1fd6acda9f0b3d63587", "project_admin", "project_admin@example.org", "http://localhost:58080/realm/quarkus");
        clientService.register("2ba5ad4cc037343cfa53ba6633becd68ea4c5d7805f3f1bf172d66c1d9440768", "provider_admin", "provider_admin@example.org", "http://localhost:58080/realm/quarkus");
        clientAccessAlwaysRepository.assignRolesToRegisteredClient("f2e411a7ab877643ab82f637778230973df4cbee013f2a2a333dac7157addc55", Set.of("metric_definition_creator"));
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