package org.accounting.system.templates;

import io.quarkus.qute.Template;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * This endpoint is responsible for rendering the keycloak.html.
 * The web page is created by processing template file. Templates are located under src/main/resources/templates.
 * Obtain the keycloak application properties and feed them to the keycloak template.
 * The keycloak.html is responsible for redirecting a user to Keycloak's login page in order to be authenticated and displaying the obtained token.
 */
@Path("/keycloak-client-template")
public class KeycloakClientTemplate {

    @Inject
    Template keycloak;

    @ConfigProperty(name = "keycloak.server.url")
    String keycloakServerUrl;

    @ConfigProperty(name = "keycloak.server.realm")
    String keycloakServerRealm;

    @ConfigProperty(name = "keycloak.server.client.id")
    String keycloakServerClientId;

    @GET
    @Produces(MediaType.TEXT_HTML)
    public String keycloakClient() {
        return keycloak.data("keycloak_server_url", keycloakServerUrl, "keycloak_server_realm", keycloakServerRealm, "keycloak_server_client_id", keycloakServerClientId).render();
    }
}
