package org.accounting.system;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import io.quarkus.test.keycloak.client.KeycloakTestClient;
import io.quarkus.test.security.TestSecurity;
import io.quarkus.test.security.oidc.OidcSecurity;
import io.quarkus.test.security.oidc.TokenIntrospection;
import io.quarkus.test.security.oidc.UserInfo;
import io.restassured.http.ContentType;
import org.accounting.system.clients.ProviderClient;
import org.accounting.system.clients.responses.eoscportal.Response;
import org.accounting.system.clients.responses.eoscportal.Total;
import org.accounting.system.dtos.InformativeResponse;
import org.accounting.system.dtos.acl.AccessControlRequestDto;
import org.accounting.system.dtos.project.AssociateProjectProviderRequestDto;
import org.accounting.system.dtos.project.ProjectResponseDto;
import org.accounting.system.endpoints.ProjectEndpoint;
import org.accounting.system.enums.acl.AccessControlPermission;
import org.accounting.system.mappers.ProviderMapper;
import org.accounting.system.repositories.acl.AccessControlRepository;
import org.accounting.system.repositories.installation.InstallationRepository;
import org.accounting.system.repositories.metricdefinition.MetricDefinitionRepository;
import org.accounting.system.repositories.project.ProjectAccessAlwaysRepository;
import org.accounting.system.repositories.provider.ProviderRepository;
import org.accounting.system.wiremock.ProjectWireMockServer;
import org.accounting.system.wiremock.ProviderWireMockServer;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import javax.inject.Inject;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;


@QuarkusTest
@TestProfile(AccountingSystemTestProfile.class)
@TestHTTPEndpoint(ProjectEndpoint.class)
@QuarkusTestResource(ProjectWireMockServer.class)
@QuarkusTestResource(ProviderWireMockServer.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ProjectAutorizationTest {

    @Inject
    @RestClient
    ProviderClient providerClient;

    @Inject
    ProviderRepository providerRepository;

    @Inject
    InstallationRepository installationRepository;

    @Inject
    MetricDefinitionRepository metricDefinitionRepository;

    @Inject
    AccessControlRepository accessControlRepository;

    @Inject
    ProjectAccessAlwaysRepository projectAccessAlwaysRepository;

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
        accessControlRepository.deleteAll();
        projectAccessAlwaysRepository.deleteAll();
    }

    @Test
    @TestSecurity(user = "project_admin", roles = {"project_admin"})
    @OidcSecurity(introspectionRequired = true,
            introspection = {
                    @TokenIntrospection(key = "voperson_id", value = "project_admin@example.org"),
                    @TokenIntrospection(key = "sub", value = "project_admin@example.org"),
                    @TokenIntrospection(key = "groups", value = "project_admin")
            },
            userinfo = {
                    @UserInfo(key = "name", value = "project_admin"),
                    @UserInfo(key = "email", value = "project_admin@example.org")
            }
    )
    public void clientGrantsProjectAccessToOtherClient(){

        // initially the client project_admin has no access to 777536
        given()
                .post("/{id}", "777536")
                .then()
                .assertThat()
                .statusCode(403)
                .extract()
                .as(InformativeResponse.class);

        // client system_admin gives access to client project_admin

        var acl = new AccessControlRequestDto();

        acl.permissions = Set.of(AccessControlPermission.ACCESS_PROJECT.toString());

        given()
                .auth()
                .oauth2(getAccessToken("admin"))
                .contentType(ContentType.JSON)
                .body(acl)
                .post("/{id}/acl/{who}", "777536", "project_admin@example.org")
                .then()
                .assertThat()
                .statusCode(200)
                .extract()
                .as(InformativeResponse.class);

        // client project_admin has now access Project 777536

        var project = given()
                .post("/{id}", "777536")
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
    @TestSecurity(user = "project_admin", roles = {"project_admin"})
    @OidcSecurity(introspectionRequired = true,
            introspection = {
                    @TokenIntrospection(key = "voperson_id", value = "project_admin@example.org"),
                    @TokenIntrospection(key = "sub", value = "project_admin@example.org"),
                    @TokenIntrospection(key = "groups", value = "project_admin")
            },
            userinfo = {
                    @UserInfo(key = "name", value = "project_admin"),
                    @UserInfo(key = "email", value = "project_admin@example.org")
            }
    )
    public void saveProjectForbidden(){

        var informativeResponse = given()
                .post("/{id}", "777536")
                .then()
                .assertThat()
                .statusCode(403)
                .extract()
                .as(InformativeResponse.class);

        assertEquals("The authenticated client is not permitted to perform the requested operation.", informativeResponse.message);
    }

    @Test
    @TestSecurity(user = "project_admin", roles = {"project_admin"})
    @OidcSecurity(introspectionRequired = true,
            introspection = {
                    @TokenIntrospection(key = "voperson_id", value = "project_admin@example.org"),
                    @TokenIntrospection(key = "sub", value = "project_admin@example.org"),
                    @TokenIntrospection(key = "groups", value = "project_admin")
            },
            userinfo = {
                    @UserInfo(key = "name", value = "project_admin"),
                    @UserInfo(key = "email", value = "project_admin@example.org")
            }
    )
    public void associateProjectWithProvidersForbidden(){

        given()
                .auth()
                .oauth2(getAccessToken("admin"))
                .post("/{id}", "777536")
                .then()
                .assertThat()
                .statusCode(200)
                .extract()
                .as(ProjectResponseDto.class);

        var associateProjectProviderRequestDto = new AssociateProjectProviderRequestDto();

        associateProjectProviderRequestDto.providers = Set.of("grnet");

        var informativeResponse = given()
                .contentType(ContentType.JSON)
                .body(associateProjectProviderRequestDto)
                .post("/{id}/associate", "777536")
                .then()
                .assertThat()
                .statusCode(403)
                .extract()
                .as(InformativeResponse.class);

        assertEquals("The authenticated client is not permitted to perform the requested operation.", informativeResponse.message);
    }

    protected String getAccessToken(String userName) {
        return keycloakClient.getAccessToken(userName);
    }
}
