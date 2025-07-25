package org.accounting.system.templates;

import io.quarkus.qute.Template;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.openapi.annotations.Operation;

/**
 * This endpoint is responsible for rendering the oidc client .
 * The web page is created by processing template file. Templates are located under src/main/resources/templates.
 */
@Path("/oidc-client")
public class OidcClientTemplate {

    @Inject
    Template client;

    @ConfigProperty(name = "api.html.keycloak.url")
    String keycloakServerUrl;

    @ConfigProperty(name = "api.html.keycloak.realm")
    String keycloakServerRealm;

    @ConfigProperty(name = "api.html.keycloak.public.client.id")
    String keycloakServerClientId;

    @ConfigProperty(name = "api.html.keycloak.javascript.adapter")
    String keycloakServerJavascriptAdapter;

    @ConfigProperty(name = "client.scope")
    String clientScope;


    @GET
    @Produces(MediaType.TEXT_HTML)
    @Operation(hidden = true)
    public String keycloakClient() {
        return client
                .data(
                        "keycloak_server_url",
                        keycloakServerUrl,
                        "keycloak_server_realm",
                        keycloakServerRealm,
                        "keycloak_server_client_id",
                        keycloakServerClientId,
                        "keycloak_server_javascript_adapter",
                        keycloakServerJavascriptAdapter,
                        "client_scope",
                        clientScope)
                .render();
    }
}
