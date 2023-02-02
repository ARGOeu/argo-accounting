package org.accounting.system.endpoints;

import io.quarkus.security.Authenticated;
import org.accounting.system.interceptors.annotations.SystemAdmin;
import org.accounting.system.services.SystemAdminService;
import org.accounting.system.util.Utility;
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