package org.accounting.system.clients;

import org.accounting.system.clients.responses.openaire.OpenAireProject;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.jboss.resteasy.annotations.jaxrs.QueryParam;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

/**
 * This HTTP client communicates with OpenAire to retrieve projects.
 */
@Path("/search")
@RegisterRestClient
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