package org.accounting.system;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.keycloak.client.KeycloakTestClient;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import org.accounting.system.clients.ProviderClient;
import org.accounting.system.clients.responses.eoscportal.Response;
import org.accounting.system.clients.responses.eoscportal.Total;
import org.accounting.system.dtos.admin.ProjectRegistrationRequest;
import org.accounting.system.mappers.ProviderMapper;
import org.accounting.system.repositories.metric.MetricRepository;
import org.accounting.system.repositories.metricdefinition.MetricDefinitionRepository;
import org.accounting.system.repositories.project.ProjectRepository;
import org.accounting.system.repositories.provider.ProviderRepository;
import org.accounting.system.services.KeycloakService;
import org.accounting.system.services.clients.GroupMembership;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.json.simple.parser.ParseException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mockito;

import java.util.Set;
import java.util.concurrent.ExecutionException;

import static io.restassured.RestAssured.given;
import static org.mockito.ArgumentMatchers.any;

@QuarkusTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PrepareTest {

    @Inject
    @RestClient
    ProviderClient providerClient;

    @Inject
    ProviderRepository providerRepository;

    @Inject
    MetricDefinitionRepository metricDefinitionRepository;

    @Inject
    ProjectRepository projectRepository;

    @Inject
    MetricRepository metricRepository;

    @InjectMock
    KeycloakService keycloakService;

    KeycloakTestClient keycloakClient = new KeycloakTestClient();

    @BeforeAll
    public void setup() throws ExecutionException, InterruptedException, ParseException {

        Total total = providerClient.getTotalNumberOfProviders("all").toCompletableFuture().get();

        Response response = providerClient.getAll("all", total.total).toCompletableFuture().get();

        providerRepository.persistOrUpdate(ProviderMapper.INSTANCE.eoscProvidersToProviders(response.results));
    }

    @BeforeEach
    public void before() throws ParseException {

        Mockito.when(keycloakService.getValueByKey(any())).thenReturn("groupId");
        Mockito.when(keycloakService.getConfiguration(any(), any())).thenReturn(new GroupMembership());


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