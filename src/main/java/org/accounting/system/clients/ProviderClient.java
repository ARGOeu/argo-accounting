package org.accounting.system.clients;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import org.accounting.system.clients.responses.eoscportal.Response;
import org.accounting.system.clients.responses.eoscportal.Total;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import java.util.concurrent.CompletionStage;

@Path("/provider")
@RegisterRestClient
@ApplicationScoped
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
    CompletionStage<Total> getTotalNumberOfProviders(@QueryParam("catalogue_id") String catalogueId);

    /**
     * Communicates with EOSC Portal to fetch the available EOSC Providers.
     *
     * @param quantity The total number of Providers.
     * @return All available EOSC Providers.
     */
    @GET
    @Path("/all")
    CompletionStage<Response> getAll(@QueryParam("catalogue_id") String catalogueId, @QueryParam("quantity") int quantity);
}