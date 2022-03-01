package org.accounting.system.endpoints;

import org.accounting.system.dtos.InformativeResponse;
import org.accounting.system.dtos.RoleResponseDto;
import org.accounting.system.services.authorization.RoleService;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/authorization")
public class AuthorizationEndpoint {

    @Inject
    RoleService roleService;

    public AuthorizationEndpoint(RoleService roleService) {
        this.roleService = roleService;
    }

    @Tag(name = "Authorization")
    @Operation(
            summary = "Returns the available roles.",
            description = "This operation fetches the registered Accounting System roles.")
    @APIResponse(
            responseCode = "200",
            description = "Array of available roles.",
            content = @Content(schema = @Schema(
                    type = SchemaType.ARRAY,
                    implementation = RoleResponseDto.class)))
    @APIResponse(
            responseCode = "500",
            description = "Internal Server Errors.",
            content = @Content(schema = @Schema(
                    type = SchemaType.OBJECT,
                    implementation = InformativeResponse.class)))

    @GET
    @Path("roles")
    @Produces(value = MediaType.APPLICATION_JSON)
    public Response getRoles(){

        return Response.ok().entity(roleService.fetchRoles()).build();
    }
}
