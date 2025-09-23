package org.accounting.system.services.clients;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RegisterRestClient(configKey = "keycloak-token-client")
@Path("/protocol/openid-connect/token")
public interface KeycloakTokenClient {

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    TokenResponse getToken( @FormParam("grant_type") String grantType,
                            @FormParam("client_id") String clientId,
                            @FormParam("client_secret") String clientSecret);
}
