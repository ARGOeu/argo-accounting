package org.accounting.system.endpoints;

import io.quarkus.security.Authenticated;
import org.accounting.system.dtos.InformativeResponse;
import org.accounting.system.dtos.RoleResponseDto;
import org.accounting.system.dtos.authorization.RoleRequestDto;
import org.accounting.system.enums.Collection;
import org.accounting.system.interceptors.annotations.Permission;
import org.accounting.system.services.authorization.RoleService;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.enums.SecuritySchemeIn;
import org.eclipse.microprofile.openapi.annotations.enums.SecuritySchemeType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.eclipse.microprofile.openapi.annotations.security.SecurityScheme;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.jboss.resteasy.specimpl.ResteasyUriInfo;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

@Path("/roles")
@Authenticated
@SecurityScheme(securitySchemeName = "Authentication",
        description = "JWT token",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER)

public class RoleEndpoint {

    @ConfigProperty(name = "quarkus.resteasy.path")
    String basePath;

    @ConfigProperty(name = "server.url")
    String serverUrl;

    @Inject
    RoleService roleService;

    public RoleEndpoint(RoleService roleService) {
        this.roleService = roleService;
    }

    @Tag(name = "Roles")
    @Operation(
            summary = "Register a new Role.",
            description = "Retrieves and inserts a Role into the database.")
    @APIResponse(
            responseCode = "201",
            description = "Role has been created successfully.",
            content = @Content(schema = @Schema(
                    type = SchemaType.OBJECT,
                    implementation = RoleResponseDto.class)))
    @APIResponse(
            responseCode = "400",
            description = "Bad Request.",
            content = @Content(schema = @Schema(
                    type = SchemaType.OBJECT,
                    implementation = InformativeResponse.class)))
    @APIResponse(
            responseCode = "401",
            description = "User has not been authenticated.",
            content = @Content(schema = @Schema(
                    type = SchemaType.OBJECT,
                    implementation = InformativeResponse.class)))
    @APIResponse(
            responseCode = "403",
            description = "The authenticated user/service is not permitted to perform the requested operation.",
            content = @Content(schema = @Schema(
                    type = SchemaType.OBJECT,
                    implementation = InformativeResponse.class)))
    @APIResponse(
            responseCode = "409",
            description = "There is a Role with that name.",
            content = @Content(schema = @Schema(
                    type = SchemaType.OBJECT,
                    implementation = InformativeResponse.class)))
    @APIResponse(
            responseCode = "415",
            description = "Cannot consume content type.",
            content = @Content(schema = @Schema(
                    type = SchemaType.OBJECT,
                    implementation = InformativeResponse.class)))
    @APIResponse(
            responseCode = "500",
            description = "Internal Server Errors.",
            content = @Content(schema = @Schema(
                    type = SchemaType.OBJECT,
                    implementation = InformativeResponse.class)))
    @SecurityRequirement(name = "Authentication")

    @POST
    @Produces(value = MediaType.APPLICATION_JSON)
    @Consumes(value = MediaType.APPLICATION_JSON)
    @Permission(collection = Collection.Role, operation = org.accounting.system.enums.Operation.CREATE)
    public Response save(@Valid @NotNull(message = "The request body is empty.") RoleRequestDto roleRequestDto, @Context UriInfo uriInfo){

        UriInfo serverInfo = new ResteasyUriInfo(serverUrl.concat(basePath).concat(uriInfo.getPath()), basePath);

        var response = roleService.save(roleRequestDto);

        return Response.created(serverInfo.getAbsolutePathBuilder().path(response.id).build()).entity(response).build();
    }

    @Tag(name = "Roles")
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
    @Produces(value = MediaType.APPLICATION_JSON)
    @Permission(collection = Collection.Role, operation = org.accounting.system.enums.Operation.READ)
    public Response getRoles(){

        return Response.ok().entity(roleService.fetchRoles()).build();
    }
}
