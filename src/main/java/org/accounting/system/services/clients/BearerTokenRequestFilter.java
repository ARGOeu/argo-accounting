package org.accounting.system.services.clients;

import jakarta.inject.Inject;
import jakarta.ws.rs.client.ClientRequestContext;
import jakarta.ws.rs.client.ClientRequestFilter;
import org.accounting.system.services.KeycloakService;

public class BearerTokenRequestFilter implements ClientRequestFilter {

    @Inject
    KeycloakService keycloakService;

    @Override
    public void filter(ClientRequestContext requestContext) {
        String token = keycloakService.getAccessToken();
        requestContext.getHeaders().add("Authorization", "Bearer " + token);
    }
}
