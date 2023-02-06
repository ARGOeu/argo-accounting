package org.accounting.system.clients;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@Path("/.well-known")
@RegisterRestClient
@ApplicationScoped

/**
 * This HTTP client communicates with Eosc Infrastructure Proxy.
 */
public interface AAIProxyClient {

    @GET
    @Path("/openid-configuration")
    Response configurations();
}