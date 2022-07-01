package org.accounting.system.endpoints;

import io.quarkus.oidc.TokenIntrospection;
import io.quarkus.security.Authenticated;
import org.accounting.system.repositories.acl.AccessControlRepository;
import org.accounting.system.repositories.client.ClientRepository;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.openapi.annotations.Operation;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Set;

@Path("/system-admin")
@Authenticated
public class SystemAdminEndpoint {

    @Inject
    AccessControlRepository accessControlRepository;

    @Inject
    TokenIntrospection tokenIntrospection;

    @Inject
    ClientRepository clientRepository;

    @ConfigProperty(name = "key.to.retrieve.id.from.access.token")
    String key;

    @Operation(
            hidden = true)
    @POST
    @Path("/assign-projects")
    @Consumes(value = MediaType.APPLICATION_JSON)
    public Response assignProjects(@Valid @NotNull(message = "The request body is empty.") Set<String> projects) {

        String id = tokenIntrospection.getJsonObject().getString(key);

        clientRepository.isSystemAdmin(id);

        accessControlRepository.accessListOfProjects(projects, id);

        return Response.ok().build();
    }
}