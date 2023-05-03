package org.accounting.system.clients;

import org.accounting.system.clients.responses.eoscportal.EOSCResource;
import org.accounting.system.clients.responses.eoscportal.ResponseResource;
import org.accounting.system.clients.responses.eoscportal.Total;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

@Path("/resource")
@RegisterRestClient
@ApplicationScoped
/**
 * This HTTP client communicates with EOSC Portal to retrieve resources.
 */
public interface ResourceClient {

    /**
     * Communicates with EOSC Portal to retrieve a specific Resource based on its id.
     *
     * @return The corresponding Resource.
     */
    @GET
    @Path("/{id}")
    EOSCResource getResource(@PathParam("id") String id);

    /**
     * Communicates with EOSC Portal to get the total number of Resources.
     *
     * @return The total number of Resources.
     */
    @GET
    @Path("/all")
    Total getTotalNumberOfResources(@QueryParam("catalogue_id") String catalogueId);

    /**
     * Communicates with EOSC Portal to fetch the available EOSC Resources.
     *
     * @param quantity The total number of Resources.
     * @return All available EOSC Resources.
     */
    @GET
    @Path("/all")
    ResponseResource getAll(@QueryParam("catalogue_id") String catalogueId, @QueryParam("quantity") int quantity);

}