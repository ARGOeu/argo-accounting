package org.accounting.system.services.clients;

import jakarta.inject.Inject;
import jakarta.ws.rs.client.ClientRequestContext;
import jakarta.ws.rs.client.ClientRequestFilter;
import org.accounting.system.services.groupmanagement.AAIGroupManagement;

public class BearerTokenRequestFilter implements ClientRequestFilter {

    @Inject
    AAIGroupManagement aaiGroupManagement;

    @Override
    public void filter(ClientRequestContext requestContext) {
        String token = aaiGroupManagement.getAccessToken();
        requestContext.getHeaders().add("Authorization", "Bearer " + token);
    }
}
