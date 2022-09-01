package org.accounting.system.endpoints;

import io.quarkus.security.Authenticated;
import org.accounting.system.constraints.NotFoundEntity;
import org.accounting.system.dtos.InformativeResponse;
import org.accounting.system.dtos.authorization.request.RoleRequestDto;
import org.accounting.system.dtos.authorization.response.RoleResponseDto;
import org.accounting.system.dtos.authorization.update.UpdateRoleRequestDto;
import org.accounting.system.dtos.pagination.PageResource;
import org.accounting.system.enums.ApiMessage;
import org.accounting.system.enums.Collection;
import org.accounting.system.enums.Operation;
import org.accounting.system.interceptors.annotations.AccessPermission;
import org.accounting.system.repositories.authorization.RoleRepository;
import org.accounting.system.services.acl.AccessControlService;
import org.accounting.system.services.authorization.RoleService;
import org.accounting.system.util.Utility;
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
import org.jboss.resteasy.specimpl.ResteasyUriInfo;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.PATCH;
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

import static org.eclipse.microprofile.openapi.annotations.enums.ParameterIn.QUERY;

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

    @Inject
    Utility utility;

    @Inject
    AccessControlService accessControlService;

    @Inject
    RoleRepository roleRepository;

    public RoleEndpoint(RoleService roleService) {
        this.roleService = roleService;
    }

    @Tag(name = "Role")
    @org.eclipse.microprofile.openapi.annotations.Operation(
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
    @AccessPermission(collection = Collection.Role, operation = Operation.CREATE)
    public Response save(@Valid @NotNull(message = "The request body is empty.") RoleRequestDto roleRequestDto, @Context UriInfo uriInfo){

        utility.collectionHasTheAppropriatePermissions(roleRequestDto);

        UriInfo serverInfo = new ResteasyUriInfo(serverUrl.concat(basePath).concat(uriInfo.getPath()), basePath);

        var response = roleService.save(roleRequestDto);

        return Response.created(serverInfo.getAbsolutePathBuilder().path(response.id).build()).entity(response).build();
    }

    @Tag(name = "Role")
    @org.eclipse.microprofile.openapi.annotations.Operation(
            summary = "Deletes an existing Role.",
            description = "You can delete an existing role by its id.")
    @APIResponse(
            responseCode = "200",
            description = "Role has been deleted successfully.",
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
            description = "Role has not been found.",
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

    @DELETE()
    @Path("/{id}")
    @Produces(value = MediaType.APPLICATION_JSON)
    @AccessPermission(collection = Collection.Role, operation = Operation.DELETE)
    public Response delete(@Parameter(
            description = "The Role to be deleted.",
            required = true,
            example = "507f1f77bcf86cd799439011",
            schema = @Schema(type = SchemaType.STRING))
                           @PathParam("id") @Valid @NotFoundEntity(repository = RoleRepository.class, message = "There is no Role with the following id:") String id) {


        var success = roleService.delete(id);

        var successResponse = new InformativeResponse();

        if(success){
            successResponse.code = 200;
            successResponse.message = "Role has been deleted successfully.";
        } else {
            successResponse.code = 500;
            successResponse.message = "Role cannot be deleted due to a server issue. Please try again.";
        }
        return Response.ok().entity(successResponse).build();
    }

    @Tag(name = "Role")
    @org.eclipse.microprofile.openapi.annotations.Operation(
            summary = "Returns the available roles.",
            description = "This operation fetches the registered Accounting System roles. By default, the first page of 10 Providers will be returned. " +
                    "You can tune the default values by using the query parameters page and size.")
    @APIResponse(
            responseCode = "200",
            description = "Array of available roles.",
            content = @Content(schema = @Schema(
                    type = SchemaType.OBJECT,
                    implementation = PageableRoleResponseDto.class)))
    @APIResponse(
            responseCode = "500",
            description = "Internal Server Errors.",
            content = @Content(schema = @Schema(
                    type = SchemaType.OBJECT,
                    implementation = InformativeResponse.class)))

    @GET
    @Produces(value = MediaType.APPLICATION_JSON)
    @AccessPermission(collection = Collection.Role, operation = Operation.READ)
    public Response getRoles(@Parameter(name = "page", in = QUERY,
            description = "Indicates the page number. Page number must be >= 1.") @DefaultValue("1") @QueryParam("page") int page,
                             @Parameter(name = "size", in = QUERY,
                                     description = "The page size.") @DefaultValue("10") @QueryParam("size") int size,
                             @Context UriInfo uriInfo){
        if(page <1){
            throw new BadRequestException(ApiMessage.PAGE_NUMBER.message);
        }

        return Response.ok().entity(roleService.findAllRolesPageable(page-1, size, uriInfo)).build();
    }

    @Tag(name = "Role")
    @org.eclipse.microprofile.openapi.annotations.Operation(
            summary = "Returns an existing Role.",
            description = "This operation accepts the id of a Role and fetches from the database the corresponding record.")
    @APIResponse(
            responseCode = "200",
            description = "The corresponding Role.",
            content = @Content(schema = @Schema(
                    type = SchemaType.OBJECT,
                    implementation = RoleResponseDto.class)))
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
            description = "Role has not been found.",
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

    @GET
    @Path("/{id}")
    @Produces(value = MediaType.APPLICATION_JSON)
    @AccessPermission(collection = Collection.Role, operation = Operation.READ)
    public Response get(
            @Parameter(
                    description = "The Role to be retrieved.",
                    required = true,
                    example = "507f1f77bcf86cd799439011",
                    schema = @Schema(type = SchemaType.STRING))
            @PathParam("id") @Valid @NotFoundEntity(repository = RoleRepository.class, message = "There is no Role with the following id:") String id){

        RoleResponseDto response = roleService.fetchRole(id);

        return Response.ok().entity(response).build();
    }

    @Tag(name = "Role")
    @org.eclipse.microprofile.openapi.annotations.Operation(
            summary = "Updates an existing Role.",
            description = "In order to update the resource properties, the body of the request must contain an updated representation of Role. " +
                    "You can update a part or all attributes of the Role except for its id. The empty or null values are ignored.")
    @APIResponse(
            responseCode = "200",
            description = "Role was updated successfully.",
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
            description = "Role has not been found.",
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

    @PATCH
    @Path("/{id}")
    @Produces(value = MediaType.APPLICATION_JSON)
    @Consumes(value = MediaType.APPLICATION_JSON)
    @AccessPermission(collection = Collection.Role, operation = Operation.UPDATE)
    public Response update(
            @Parameter(
                    description = "The Role to be updated.",
                    required = true,
                    example = "507f1f77bcf86cd799439011",
                    schema = @Schema(type = SchemaType.STRING))
            @PathParam("id") @Valid @NotFoundEntity(repository = RoleRepository.class, message = "There is no Role with the following id:") String id, @Valid @NotNull(message = "The request body is empty.") UpdateRoleRequestDto updateRoleRequestDto) {


        RoleResponseDto response = roleService.update(id, updateRoleRequestDto);

        return Response.ok().entity(response).build();
    }

//    @Tag(name = "Role")
//    @org.eclipse.microprofile.openapi.annotations.Operation(
//            summary = "Generates a new Access Control Entry.",
//            description = "This endpoint is responsible for generating a new Access Control Entry. " +
//                    "Access Control Entry rules specify which clients are granted or denied access to particular Role entities. " +
//                    "It should be noted that the combination {who, collection, entity} is unique. Therefore, only one Access Control entry can be created for each client and each entity.")
//    @APIResponse(
//            responseCode = "200",
//            description = "Access Control entry has been created successfully.",
//            content = @Content(schema = @Schema(
//                    type = SchemaType.OBJECT,
//                    implementation = InformativeResponse.class)))
//    @APIResponse(
//            responseCode = "400",
//            description = "Bad Request.",
//            content = @Content(schema = @Schema(
//                    type = SchemaType.OBJECT,
//                    implementation = InformativeResponse.class)))
//    @APIResponse(
//            responseCode = "401",
//            description = "Client has not been authenticated.",
//            content = @Content(schema = @Schema(
//                    type = SchemaType.OBJECT,
//                    implementation = InformativeResponse.class)))
//    @APIResponse(
//            responseCode = "403",
//            description = "The authenticated client is not permitted to perform the requested operation.",
//            content = @Content(schema = @Schema(
//                    type = SchemaType.OBJECT,
//                    implementation = InformativeResponse.class)))
//    @APIResponse(
//            responseCode = "404",
//            description = "Role has not been found.",
//            content = @Content(schema = @Schema(
//                    type = SchemaType.OBJECT,
//                    implementation = InformativeResponse.class)))
//    @APIResponse(
//            responseCode = "409",
//            description = "There is an Access Control Entry with this {who, collection, entity}.",
//            content = @Content(schema = @Schema(
//                    type = SchemaType.OBJECT,
//                    implementation = InformativeResponse.class)))
//    @APIResponse(
//            responseCode = "415",
//            description = "Cannot consume content type.",
//            content = @Content(schema = @Schema(
//                    type = SchemaType.OBJECT,
//                    implementation = InformativeResponse.class)))
//    @APIResponse(
//            responseCode = "500",
//            description = "Internal Server Errors.",
//            content = @Content(schema = @Schema(
//                    type = SchemaType.OBJECT,
//                    implementation = InformativeResponse.class)))
//    @SecurityRequirement(name = "Authentication")
//
//    @POST
//    @Path("/{role_id}/acl/{who}")
//    @Produces(value = MediaType.APPLICATION_JSON)
//    @Consumes(value = MediaType.APPLICATION_JSON)
//    @AccessPermission(collection = Collection.Role, operation = ACL)
//    public Response createAccessControl(@Valid @NotNull(message = "The request body is empty.") PermissionAccessControlRequestDto permissionAccessControlRequestDto,
//                                        @Parameter(
//                                                description = "role_id is the id of the entity to which the permissions apply.",
//                                                required = true,
//                                                example = "507f1f77bcf86cd799439011",
//                                                schema = @Schema(type = SchemaType.STRING))
//                                        @PathParam("role_id")
//                                        @Valid
//                                        @NotFoundEntity(repository = RoleRepository.class, message = "There is no Role with the following id:") String id,
//                                        @Parameter(
//                                                description = "who is the id of a Client that the Access Control grants access.",
//                                                required = true,
//                                                example = "fbdb4e4a-6e93-4b08-a1e7-0b7bd08520a6",
//                                                schema = @Schema(type = SchemaType.STRING))
//                                        @PathParam("who") String who) {
//
//
//        var informativeResponse = accessControlService.grantPermission(id, who, permissionAccessControlRequestDto, Collection.Role, roleRepository);
//
//        return Response.ok().entity(informativeResponse).build();
//    }
//
//    @Tag(name = "Role")
//    @org.eclipse.microprofile.openapi.annotations.Operation(
//            summary = "Modify an existing Access Control Entry.",
//            description = "This endpoint is responsible for updating an existing Access Control Entry. It will modify a specific Access Control Entry " +
//                    "which has granted permissions on a Role to a specific client." +
//                    "You can update a part or all attributes of the Access Control entity.")
//    @APIResponse(
//            responseCode = "200",
//            description = "Access Control entry has been updated successfully.",
//            content = @Content(schema = @Schema(
//                    type = SchemaType.OBJECT,
//                    implementation = InformativeResponse.class)))
//    @APIResponse(
//            responseCode = "400",
//            description = "Bad Request.",
//            content = @Content(schema = @Schema(
//                    type = SchemaType.OBJECT,
//                    implementation = InformativeResponse.class)))
//    @APIResponse(
//            responseCode = "401",
//            description = "Client has not been authenticated.",
//            content = @Content(schema = @Schema(
//                    type = SchemaType.OBJECT,
//                    implementation = InformativeResponse.class)))
//    @APIResponse(
//            responseCode = "403",
//            description = "The authenticated client is not permitted to perform the requested operation.",
//            content = @Content(schema = @Schema(
//                    type = SchemaType.OBJECT,
//                    implementation = InformativeResponse.class)))
//    @APIResponse(
//            responseCode = "404",
//            description = "Role has not been found.",
//            content = @Content(schema = @Schema(
//                    type = SchemaType.OBJECT,
//                    implementation = InformativeResponse.class)))
//    @APIResponse(
//            responseCode = "409",
//            description = "There is an Access Control Entry with this {who, collection, entity}.",
//            content = @Content(schema = @Schema(
//                    type = SchemaType.OBJECT,
//                    implementation = InformativeResponse.class)))
//    @APIResponse(
//            responseCode = "415",
//            description = "Cannot consume content type.",
//            content = @Content(schema = @Schema(
//                    type = SchemaType.OBJECT,
//                    implementation = InformativeResponse.class)))
//    @APIResponse(
//            responseCode = "500",
//            description = "Internal Server Errors.",
//            content = @Content(schema = @Schema(
//                    type = SchemaType.OBJECT,
//                    implementation = InformativeResponse.class)))
//    @SecurityRequirement(name = "Authentication")
//
//    @PATCH
//    @Path("/{role_id}/acl/{who}")
//    @Produces(value = MediaType.APPLICATION_JSON)
//    @Consumes(value = MediaType.APPLICATION_JSON)
//    @AccessPermission(collection = Collection.Role, operation = ACL)
//    public Response modifyAccessControl(@Valid @NotNull(message = "The request body is empty.") PermissionAccessControlUpdateDto permissionAccessControlUpdateDto,
//                                        @Parameter(
//                                                description = "role_id in which permissions have been granted.",
//                                                required = true,
//                                                example = "507f1f77bcf86cd799439011",
//                                                schema = @Schema(type = SchemaType.STRING))
//                                        @PathParam("role_id")
//                                        @Valid
//                                        @NotFoundEntity(repository = RoleRepository.class, message = "There is no Role with the following id:") String id,
//                                        @Parameter(
//                                                description = "who is the client to whom the permissions have been granted.",
//                                                required = true,
//                                                example = "fbdb4e4a-6e93-4b08-a1e7-0b7bd08520a6",
//                                                schema = @Schema(type = SchemaType.STRING))
//                                        @PathParam("who") String who) {
//
//
//        var response = accessControlService.modifyPermission(id, who, permissionAccessControlUpdateDto, Collection.Role, roleRepository);
//
//        return Response.ok().entity(response).build();
//    }
//
//    @Tag(name = "Role")
//    @org.eclipse.microprofile.openapi.annotations.Operation(
//            summary = "Deletes an existing Access Control entry.",
//            description = "You can delete the permissions that a client can access to manage a specific Role.")
//    @APIResponse(
//            responseCode = "200",
//            description = "Access Control entry has been deleted successfully.",
//            content = @Content(schema = @Schema(
//                    type = SchemaType.OBJECT,
//                    implementation = InformativeResponse.class)))
//    @APIResponse(
//            responseCode = "401",
//            description = "Client has not been authenticated.",
//            content = @Content(schema = @Schema(
//                    type = SchemaType.OBJECT,
//                    implementation = InformativeResponse.class)))
//    @APIResponse(
//            responseCode = "403",
//            description = "The authenticated client is not permitted to perform the requested operation.",
//            content = @Content(schema = @Schema(
//                    type = SchemaType.OBJECT,
//                    implementation = InformativeResponse.class)))
//    @APIResponse(
//            responseCode = "404",
//            description = "Role has not been found.",
//            content = @Content(schema = @Schema(
//                    type = SchemaType.ARRAY,
//                    implementation = InformativeResponse.class)))
//    @APIResponse(
//            responseCode = "500",
//            description = "Internal Server Errors.",
//            content = @Content(schema = @Schema(
//                    type = SchemaType.OBJECT,
//                    implementation = InformativeResponse.class)))
//    @SecurityRequirement(name = "Authentication")
//
//    @DELETE()
//    @Path("/{role_id}/acl/{who}")
//    @Produces(value = MediaType.APPLICATION_JSON)
//    @AccessPermission(collection = Collection.Role, operation = ACL)
//    public Response deleteAccessControl(@Parameter(
//            description = "role_id in which permissions have been granted.",
//            required = true,
//            example = "507f1f77bcf86cd799439011",
//            schema = @Schema(type = SchemaType.STRING))
//                                        @PathParam("role_id")
//                                        @Valid
//                                        @NotFoundEntity(repository = RoleRepository.class, message = "There is no Role with the following id:") String id,
//                                        @Parameter(
//                                                description = "who is the client to whom the permissions have been granted.",
//                                                required = true,
//                                                example = "fbdb4e4a-6e93-4b08-a1e7-0b7bd08520a6",
//                                                schema = @Schema(type = SchemaType.STRING))
//                                        @PathParam("who") String who) {
//
//
//        var successResponse = accessControlService.deletePermission(id, who, Collection.Role, roleRepository);
//
//        return Response.ok().entity(successResponse).build();
//    }
//
//    @Tag(name = "Role")
//    @org.eclipse.microprofile.openapi.annotations.Operation(
//            summary = "Returns an existing Access Control entry.",
//            description = "This operation returns the Access Control entry created for a client upon a Role entity.")
//    @APIResponse(
//            responseCode = "200",
//            description = "The corresponding Access Control entry.",
//            content = @Content(schema = @Schema(
//                    type = SchemaType.OBJECT,
//                    implementation = PermissionAccessControlResponseDto.class)))
//    @APIResponse(
//            responseCode = "401",
//            description = "Client has not been authenticated.",
//            content = @Content(schema = @Schema(
//                    type = SchemaType.OBJECT,
//                    implementation = InformativeResponse.class)))
//    @APIResponse(
//            responseCode = "403",
//            description = "The authenticated client is not permitted to perform the requested operation.",
//            content = @Content(schema = @Schema(
//                    type = SchemaType.OBJECT,
//                    implementation = InformativeResponse.class)))
//    @APIResponse(
//            responseCode = "404",
//            description = "Access Control entry has not been found.",
//            content = @Content(schema = @Schema(
//                    type = SchemaType.OBJECT,
//                    implementation = InformativeResponse.class)))
//    @APIResponse(
//            responseCode = "500",
//            description = "Internal Server Errors.",
//            content = @Content(schema = @Schema(
//                    type = SchemaType.OBJECT,
//                    implementation = InformativeResponse.class)))
//    @SecurityRequirement(name = "Authentication")
//
//    @GET
//    @Path("/{role_id}/acl/{who}")
//    @Produces(value = MediaType.APPLICATION_JSON)
//    @AccessPermission(collection = Collection.Role, operation = ACL)
//    public Response getAccessControl(
//            @Parameter(
//                    description = "role_id in which permissions have been granted.",
//                    required = true,
//                    example = "507f1f77bcf86cd799439011",
//                    schema = @Schema(type = SchemaType.STRING))
//            @PathParam("role_id")
//            @Valid
//            @NotFoundEntity(repository = RoleRepository.class, message = "There is no Role with the following id:") String id,
//            @Parameter(
//                    description = "who is the client to whom the permissions have been granted.",
//                    required = true,
//                    example = "fbdb4e4a-6e93-4b08-a1e7-0b7bd08520a6",
//                    schema = @Schema(type = SchemaType.STRING))
//            @PathParam("who") String who){
//
//        var response = accessControlService.fetchPermission(id, who, roleRepository);
//
//        return Response.ok().entity(response).build();
//    }
//
//    @Tag(name = "Role")
//    @org.eclipse.microprofile.openapi.annotations.Operation(
//            summary = "Returns all Access Control entries that have been created for Role collection.",
//            description = "Returns all Access Control entries that have been created for Role collection.")
//    @APIResponse(
//            responseCode = "200",
//            description = "The corresponding Access Control entries.",
//            content = @Content(schema = @Schema(
//                    type = SchemaType.ARRAY,
//                    implementation = PermissionAccessControlResponseDto.class)))
//    @APIResponse(
//            responseCode = "401",
//            description = "Client has not been authenticated.",
//            content = @Content(schema = @Schema(
//                    type = SchemaType.OBJECT,
//                    implementation = InformativeResponse.class)))
//    @APIResponse(
//            responseCode = "403",
//            description = "The authenticated client is not permitted to perform the requested operation.",
//            content = @Content(schema = @Schema(
//                    type = SchemaType.OBJECT,
//                    implementation = InformativeResponse.class)))
//    @APIResponse(
//            responseCode = "500",
//            description = "Internal Server Errors.",
//            content = @Content(schema = @Schema(
//                    type = SchemaType.OBJECT,
//                    implementation = InformativeResponse.class)))
//    @SecurityRequirement(name = "Authentication")
//
//    @GET
//    @Path("/acl")
//    @Produces(value = MediaType.APPLICATION_JSON)
//    @AccessPermission(collection = Collection.Role, operation = ACL)
//    public Response getAllAccessControl(){
//
//        var response = accessControlService.fetchAllPermissions(roleRepository);
//
//        return Response.ok().entity(response).build();
//    }

    public static class PageableRoleResponseDto extends PageResource<RoleResponseDto> {

        private List<RoleResponseDto> content;

        @Override
        public List<RoleResponseDto> getContent() {
            return content;
        }

        @Override
        public void setContent(List<RoleResponseDto> content) {
            this.content = content;
        }
    }
}