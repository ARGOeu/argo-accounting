package org.accounting.system.endpoints;

import io.quarkus.oidc.TokenIntrospection;
import io.quarkus.oidc.UserInfo;
import io.quarkus.security.Authenticated;
import org.accounting.system.constraints.NotFoundEntity;
import org.accounting.system.dtos.InformativeResponse;
import org.accounting.system.dtos.authorization.CollectionAccessPermissionDto;
import org.accounting.system.dtos.authorization.request.AssignRoleRequestDto;
import org.accounting.system.dtos.authorization.request.DetachRoleRequestDto;
import org.accounting.system.dtos.client.ClientResponseDto;
import org.accounting.system.dtos.pagination.PageResource;
import org.accounting.system.entities.projections.permissions.ProjectProjectionWithPermissions;
import org.accounting.system.enums.Collection;
import org.accounting.system.enums.Operation;
import org.accounting.system.interceptors.annotations.AccessPermission;
import org.accounting.system.repositories.client.ClientRepository;
import org.accounting.system.services.ProjectService;
import org.accounting.system.services.client.ClientService;
import org.accounting.system.util.AccountingUriInfo;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.microprofile.config.inject.ConfigProperty;
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

import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.List;
import java.util.Objects;

import static org.eclipse.microprofile.openapi.annotations.enums.ParameterIn.QUERY;

@Path("/clients")
@Authenticated
@SecurityScheme(securitySchemeName = "Authentication",
        description = "JWT token",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER)

public class ClientEndpoint {

    @Inject
    ClientService clientService;

    @Inject
    TokenIntrospection tokenIntrospection;

    @Inject
    UserInfo userInfo;

    @Inject
    ProjectService projectService;

    @ConfigProperty(name = "key.to.retrieve.id.from.access.token")
    String key;

    @ConfigProperty(name = "quarkus.resteasy-reactive.path")
    String basePath;

    @ConfigProperty(name = "server.url")
    String serverUrl;


    @Tag(name = "Client")
    @org.eclipse.microprofile.openapi.annotations.Operation(
            summary = "Registers a client into the Accounting System.",
            description = "Passing the acquired access token to this operation, any client can register itself into the Accounting System.")
    @APIResponse(
            responseCode = "200",
            description = "Client has been successfully registered.",
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
            responseCode = "403",
            description = "The authenticated client is not permitted to perform the requested operation.",
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
    public Response clientRegistration() {

        String name = StringUtils.EMPTY;
        String email = StringUtils.EMPTY;

        if(userInfo !=null && userInfo.getJsonObject() != null){
            name = Objects.isNull(userInfo.getJsonObject().get("name")) ? "": userInfo.getJsonObject().getString("name");
            email = Objects.isNull(userInfo.getJsonObject().get("email")) ? "": userInfo.getJsonObject().getString("email");
        }

        var response = clientService.register(tokenIntrospection.getJsonObject().getString(key), name, email);

        return Response.ok().entity(response).build();
    }

    @Tag(name = "Client")
    @org.eclipse.microprofile.openapi.annotations.Operation(
            summary = "Returns the available clients.",
            description = "This operation fetches the registered Accounting System clients. By default, the first page of 10 Clients will be returned. " +
                    "You can tune the default values by using the query parameters page and size.")
    @APIResponse(
            responseCode = "200",
            description = "Array of available clients.",
            content = @Content(schema = @Schema(
                    type = SchemaType.OBJECT,
                    implementation = PageableClientResponseDto.class)))
    @APIResponse(
            responseCode = "500",
            description = "Internal Server Errors.",
            content = @Content(schema = @Schema(
                    type = SchemaType.OBJECT,
                    implementation = InformativeResponse.class)))

    @GET
    @Produces(value = MediaType.APPLICATION_JSON)
    @AccessPermission(collection = Collection.Client, operation = Operation.READ)
    public Response getClients(@Parameter(name = "page", in = QUERY,
            description = "Indicates the page number. Page number must be >= 1.") @DefaultValue("1") @Min(value = 1, message = "Page number must be >= 1.") @QueryParam("page") int page,
                             @Parameter(name = "size", in = QUERY,
                                     description = "The page size.") @DefaultValue("10") @Min(value = 1, message = "Page size must be between 1 and 100.")
                             @Max(value = 100, message = "Page size must be between 1 and 100.") @QueryParam("size") int size,
                             @Context UriInfo uriInfo){

        var serverInfo = new AccountingUriInfo(serverUrl.concat(basePath).concat(uriInfo.getPath()));

        return Response.ok().entity(clientService.findAllClientsPageable(page-1, size, serverInfo)).build();
    }

    @Tag(name = "Client")
    @org.eclipse.microprofile.openapi.annotations.Operation(
            hidden = true,
            summary = "Assign one or more roles to a registered client.",
            description = "Using the unique identifier (voperson_id) of a registered client, you can assign roles to it.")
    @APIResponse(
            responseCode = "200",
            description = "The Roles have been successfully assigned to a registered client.",
            content = @Content(schema = @Schema(
                    type = SchemaType.OBJECT,
                    implementation = ClientResponseDto.class)))
    @APIResponse(
            responseCode = "400",
            description = "Bad Request.",
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
            responseCode = "403",
            description = "The authenticated client is not permitted to perform the requested operation.",
            content = @Content(schema = @Schema(
                    type = SchemaType.OBJECT,
                    implementation = InformativeResponse.class)))
    @APIResponse(
            responseCode = "404",
            description = "Role not found.",
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
    @Path("/{client_id}/assign-roles")
    @Produces(value = MediaType.APPLICATION_JSON)
    @Consumes(value = MediaType.APPLICATION_JSON)
    @AccessPermission(operation = Operation.ASSIGN_ROLE, collection = Collection.Client)
    public Response assignRole(@Valid @NotNull(message = "The request body is empty.") AssignRoleRequestDto assignRoleRequestDto,
                               @Parameter(
                                       description = "client_id is the unique identifier of a client.",
                                       required = true,
                                       example = "xyz@example.org",
                                       schema = @Schema(type = SchemaType.STRING))
                               @PathParam("client_id")
                               @Valid
                               @NotFoundEntity(repository = ClientRepository.class, id = String.class, message = "There is no registered Client with the following id:") String clientId){

        var response = clientService.assignRolesToRegisteredClient(clientId, assignRoleRequestDto.roles);

        return Response.ok().entity(response).build();
    }

    @Tag(name = "Client")
    @org.eclipse.microprofile.openapi.annotations.Operation(
            hidden = true,
            summary = "Detach one or more roles from a registered client.",
            description = "Using the unique identifier (voperson_id) of a registered client, you can detach roles from it.")
    @APIResponse(
            responseCode = "200",
            description = "The Roles have been successfully detached from a registered client.",
            content = @Content(schema = @Schema(
                    type = SchemaType.OBJECT,
                    implementation = ClientResponseDto.class)))
    @APIResponse(
            responseCode = "400",
            description = "Bad Request.",
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
            responseCode = "403",
            description = "The authenticated client is not permitted to perform the requested operation.",
            content = @Content(schema = @Schema(
                    type = SchemaType.OBJECT,
                    implementation = InformativeResponse.class)))
    @APIResponse(
            responseCode = "404",
            description = "Role not found.",
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
    @Path("/{client_id}/detach-roles")
    @Produces(value = MediaType.APPLICATION_JSON)
    @Consumes(value = MediaType.APPLICATION_JSON)
    @AccessPermission(operation = Operation.DETACH_ROLE, collection = Collection.Client)
    public Response detachRole(@Valid @NotNull(message = "The request body is empty.") DetachRoleRequestDto detachRoleRequestDto,
                               @Parameter(
                                       description = "client_id is the unique identifier of a client.",
                                       required = true,
                                       example = "xyz@example.org",
                                       schema = @Schema(type = SchemaType.STRING))
                               @PathParam("client_id")
                               @Valid
                               @NotFoundEntity(repository = ClientRepository.class, id = String.class, message = "There is no registered Client with the following id:") String clientId){

        var response = clientService.detachRolesFromRegisteredClient(clientId, detachRoleRequestDto.roles);

        return Response.ok().entity(response).build();
    }

    @Tag(name = "Client")
    @org.eclipse.microprofile.openapi.annotations.Operation(
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

    @GET
    @Path("/me")
    @Produces(value = MediaType.APPLICATION_JSON)
    @AccessPermission(collection = Collection.Client, operation = Operation.READ)
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
    @org.eclipse.microprofile.openapi.annotations.Operation(
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

    @GET
    @Path("/general-permissions")
    @Produces(value = MediaType.APPLICATION_JSON)
    @AccessPermission(collection = Collection.Client, operation = Operation.READ)
    public Response getClientGeneralPermissions(){

        return Response.ok().entity(clientService.getGeneralPermissions()).build();
    }

    public static class PageableClientResponseDto extends PageResource<ClientResponseDto> {

        private List<ClientResponseDto> content;

        @Override
        public List<ClientResponseDto> getContent() {
            return content;
        }

        @Override
        public void setContent(List<ClientResponseDto> content) {
            this.content = content;
        }
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