package org.accounting.system.clients;

import org.accounting.system.clients.responses.eoscportal.Response;
import org.accounting.system.clients.responses.eoscportal.Total;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.jboss.resteasy.annotations.jaxrs.QueryParam;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("/provider")
@RegisterRestClient
/**
 * This HTTP client communicates with EOSC Portal to retrieve providers.
 */
public interface ProviderClient {

    /**
     * Communicates with EOSC Portal to get the total number of Providers.
     *
     * @return The total number of Providers.
     */
    @GET
    @Path("/all")
    Total getTotalNumberOfProviders();

    /**
     * Communicates with EOSC Portal to fetch the available EOSC Providers.
     *
     * @param quantity The total number of Providers.
     * @return All available EOSC Providers.
     */
    @GET
    @Path("/all")
    Response getAll(@QueryParam("quantity") int quantity);
}