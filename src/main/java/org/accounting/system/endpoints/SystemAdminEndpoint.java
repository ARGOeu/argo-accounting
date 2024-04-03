package org.accounting.system.endpoints;

import io.quarkus.security.Authenticated;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.accounting.system.interceptors.annotations.SystemAdmin;
import org.accounting.system.services.SystemAdminService;
import org.accounting.system.util.Utility;
import org.eclipse.microprofile.openapi.annotations.Operation;

import java.util.Set;

@Path("/system-admin")
@Authenticated
public class SystemAdminEndpoint {

    @Inject
    SystemAdminService systemAdminService;

    @Inject
    Utility utility;

    @Operation(
            hidden = true)
    @POST
    @Path("/assign-projects")
    @Consumes(value = MediaType.APPLICATION_JSON)
    @SystemAdmin
    public Response assignProjects(@Valid @NotNull(message = "The request body is empty.") Set<String> projects) {

        systemAdminService.registerProjectsToAccountingService(projects, utility.getClientVopersonId());

        return Response.ok().build();
    }
}