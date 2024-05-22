package org.accounting.system.clients;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import org.accounting.system.clients.responses.openaire.OpenAireProject;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

/**
 * This HTTP client communicates with OpenAire to retrieve projects.
 */
@Path("/search")
@RegisterRestClient
@ApplicationScoped
public interface ProjectClient {

    /**
     * Gets the OpenAIRE project with the given grant identifier, if any.
     *
     * @param id The Project grant identifier.
     * @param format The format of the response.
     * @return The OpenAIRE Project
     */
    @GET
    @Path("/projects")
    OpenAireProject getById(@QueryParam("grantID") String id, @QueryParam("format") String format);
}