package org.accounting.system.clients;

import org.accounting.system.clients.responses.openaire.OpenAireProject;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.jboss.resteasy.annotations.jaxrs.QueryParam;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("/projects")
@RegisterRestClient
/**
 * This HTTP client communicates with OpenAire to retrieve projects.
 */
public interface ProjectClient {

    @GET
    OpenAireProject getById(@QueryParam("grantID") String id, @QueryParam("format") String format);
}