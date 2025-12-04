package org.accounting.system.endpoints;

import io.quarkus.security.Authenticated;
import jakarta.inject.Inject;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import org.accounting.system.dtos.InformativeResponse;
import org.accounting.system.dtos.pagination.PageResource;
import org.accounting.system.dtos.permissions.CollectionAccessPermissionDto;
import org.accounting.system.entities.projections.ProjectProjectionWithPermissions;
import org.accounting.system.services.ActorService;
import org.accounting.system.services.ClientService;
import org.accounting.system.services.ProjectService;
import org.accounting.system.util.AccountingUriInfo;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.enums.SecuritySchemeIn;
import org.eclipse.microprofile.openapi.annotations.enums.SecuritySchemeType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.eclipse.microprofile.openapi.annotations.security.SecurityScheme;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.List;

import static org.eclipse.microprofile.openapi.annotations.enums.ParameterIn.QUERY;

@Path("/clients")
@Authenticated
@SecurityScheme(securitySchemeName = "Authentication",
        description = "JWT token",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER)
public class ClientEndpoints {

    @ConfigProperty(name = "quarkus.rest.path")
    String basePath;

    @ConfigProperty(name = "api.server.url")
    String serverUrl;

    @Inject
    ClientService clientService;

    @Inject
    ProjectService projectService;

    @Inject
    ActorService actorService;

    @Tag(name = "Client")
    @Operation(
            summary = "Returns the client permissions.",
            description = "This operation returns the client's permissions upon different Projects, Providers, and Installations. By default, the first page of 10 Projects will be returned. " +
                    "You can tune the default values by using the query parameters page and size.")
    @APIResponse(
            responseCode = "200",
            description = "Client's Permissions.",
            content = @Content(schema = @Schema(
                    type = SchemaType.OBJECT,
                    implementation = PageableProjectWithPermissions.class)))
    @APIResponse(
            responseCode = "500",
            description = "Internal Server Errors.",
            content = @Content(schema = @Schema(
                    type = SchemaType.OBJECT,
                    implementation = InformativeResponse.class)))
    @SecurityRequirement(name = "Authentication")
    @GET
    @Path("/me")
    @Produces(value = MediaType.APPLICATION_JSON)
    public Response getClientPermissions(@Parameter(name = "page", in = QUERY,
            description = "Indicates the page number. Page number must be >= 1.") @DefaultValue("1") @Min(value = 1, message = "Page number must be >= 1.") @QueryParam("page") int page,
                                         @Parameter(name = "size", in = QUERY,
                                                 description = "The page size.") @DefaultValue("10") @Min(value = 1, message = "Page size must be between 1 and 100.")
                                         @Max(value = 100, message = "Page size must be between 1 and 100.") @QueryParam("size") int size,
                                         @Context UriInfo uriInfo){

        var serverInfo = new AccountingUriInfo(serverUrl.concat(basePath).concat(uriInfo.getPath()));

        return Response.ok().entity(projectService.getClientPermissions(page-1, size, serverInfo)).build();
    }

    @Tag(name = "Client")
    @Operation(
            summary = "Returns the client general permissions.",
            description = "This operation returns the client's permissions upon different Accounting Service collections.")
    @APIResponse(
            responseCode = "200",
            description = "Client's General Permissions.",
            content = @Content(schema = @Schema(
                    type = SchemaType.ARRAY,
                    implementation = CollectionAccessPermissionDto.class)))
    @APIResponse(
            responseCode = "500",
            description = "Internal Server Errors.",
            content = @Content(schema = @Schema(
                    type = SchemaType.OBJECT,
                    implementation = InformativeResponse.class)))
    @SecurityRequirement(name = "Authentication")
    @GET
    @Path("/general-permissions")
    @Produces(value = MediaType.APPLICATION_JSON)
    public Response getClientGeneralPermissions(){

        return Response.ok().entity(clientService.getGeneralPermissions()).build();
    }

    @Tag(name = "Client")
    @Operation(
            summary = "API used to register an actor in the accounting system in order to assign entitlements.",
            description = "Registers the actor in the accounting system in order to assign entitlements.")
    @APIResponse(
            responseCode = "200",
            description = "The actor was successfully registered.",
            content = @Content(schema = @Schema(
                    type = SchemaType.OBJECT,
                    implementation = InformativeResponse.class)))
    @APIResponse(
            responseCode = "401",
            description = "Client has not been authenticated.",
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
    public Response register() {

        actorService.register();

        var response = new InformativeResponse();
        response.message = "The actor was successfully registered.";
        response.code = 200;

        return Response.ok().entity(response).build();
    }

    public static class PageableProjectWithPermissions extends PageResource<ProjectProjectionWithPermissions> {

        private List<ProjectProjectionWithPermissions> content;

        @Override
        public List<ProjectProjectionWithPermissions> getContent() {
            return content;
        }

        @Override
        public void setContent(List<ProjectProjectionWithPermissions> content) {
            this.content = content;
        }
    }
}
