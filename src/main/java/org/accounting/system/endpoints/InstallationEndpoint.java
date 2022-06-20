package org.accounting.system.endpoints;

import io.quarkus.security.Authenticated;
import org.accounting.system.constraints.NotFoundEntity;
import org.accounting.system.dtos.InformativeResponse;
import org.accounting.system.dtos.acl.AccessControlRequestDto;
import org.accounting.system.dtos.acl.AccessControlResponseDto;
import org.accounting.system.dtos.acl.AccessControlUpdateDto;
import org.accounting.system.dtos.installation.InstallationRequestDto;
import org.accounting.system.dtos.installation.InstallationResponseDto;
import org.accounting.system.dtos.installation.UpdateInstallationRequestDto;
import org.accounting.system.dtos.metric.MetricRequestDto;
import org.accounting.system.dtos.metric.MetricResponseDto;
import org.accounting.system.entities.projections.MetricProjection;
import org.accounting.system.enums.Collection;
import org.accounting.system.enums.Operation;
import org.accounting.system.interceptors.annotations.AccessPermission;
import org.accounting.system.interceptors.annotations.AccessPermissions;
import org.accounting.system.repositories.installation.InstallationRepository;
import org.accounting.system.services.HierarchicalRelationService;
import org.accounting.system.services.acl.AccessControlService;
import org.accounting.system.services.installation.InstallationService;
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

import static org.accounting.system.enums.Operation.ACCESS_PROJECT;
import static org.accounting.system.enums.Operation.ACL;
import static org.eclipse.microprofile.openapi.annotations.enums.ParameterIn.QUERY;

@Path("/installations")
@Authenticated
@SecurityScheme(securitySchemeName = "Authentication",
        description = "JWT token",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER)

public class InstallationEndpoint {

    @ConfigProperty(name = "quarkus.resteasy.path")
    String basePath;

    @ConfigProperty(name = "server.url")
    String serverUrl;

    @Inject
    InstallationService installationService;

    @Inject
    HierarchicalRelationService hierarchicalRelationService;

    @Inject
    AccessControlService accessControlService;

    @Inject
    InstallationRepository installationRepository;

    @Tag(name = "Installation")
    @org.eclipse.microprofile.openapi.annotations.Operation(
            summary = "Generates a new Installation.",
            description = "This operation is responsible for generating and storing in the Accounting System database " +
                    "a new Installation.")
    @APIResponse(
            responseCode = "201",
            description = "Installation has been created successfully.",
            content = @Content(schema = @Schema(
                    type = SchemaType.OBJECT,
                    implementation = InstallationResponseDto.class)))
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
            description = "There is a Metric Definition with that unit_type and metric_name.",
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
    @AccessPermissions({
            @AccessPermission(collection = Collection.Installation, operation = Operation.CREATE, precedence = 1),
            @AccessPermission(collection = Collection.Project, operation = ACCESS_PROJECT, precedence = 2)
    })
    public Response save(@Valid @NotNull(message = "The request body is empty.") InstallationRequestDto installationRequestDto, @Context UriInfo uriInfo) {

        var serverInfo = new ResteasyUriInfo(serverUrl.concat(basePath).concat(uriInfo.getPath()), basePath);

        var belongs = hierarchicalRelationService.providerBelongsToProject(installationRequestDto.project, installationRequestDto.organisation);

        if(!belongs){
            String message = String.format("There is no relationship between Project {%s} and Provider {%s}", installationRequestDto.project, installationRequestDto.organisation);
            throw new BadRequestException(message);
        }

        var response = installationService.save(installationRequestDto);

        return Response.created(serverInfo.getAbsolutePathBuilder().path(response.id).build()).entity(response).build();
    }

    @Tag(name = "Installation")
    @org.eclipse.microprofile.openapi.annotations.Operation(
            summary = "Returns all Installations.",
            description = "Essentially, this operation returns all Installations which have been added to Accounting System. By default, the first page of 10 Installations will be returned. " +
                    "You can tune the default values by using the query parameters page and size.")
    @APIResponse(
            responseCode = "200",
            description = "Array of Installations.",
            content = @Content(schema = @Schema(
                    type = SchemaType.ARRAY,
                    implementation = InstallationResponseDto.class)))
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

    @GET
    @Produces(value = MediaType.APPLICATION_JSON)
    @AccessPermissions({
            @AccessPermission(collection = Collection.Installation, operation = Operation.READ, precedence = 1),
            @AccessPermission(collection = Collection.Project, operation = ACCESS_PROJECT, precedence = 2)
    })    public Response getAllPagination(@Parameter(name = "page", in = QUERY,
            description = "Indicates the page number. Page number must be >= 1.") @DefaultValue("1") @QueryParam("page") int page,
                           @Parameter(name = "size", in = QUERY,
                                   description = "The page size.") @DefaultValue("10") @QueryParam("size") int size,
                           @Context UriInfo uriInfo){

        if(page <1){
            throw new BadRequestException("Page number must be >= 1.");
        }

        return Response.ok().entity(installationService.findAllInstallationsPageable(page-1, size, uriInfo)).build();
    }

    @Tag(name = "Installation")
    @org.eclipse.microprofile.openapi.annotations.Operation(
            summary = "Deletes an existing Installation.",
            description = "This operation deletes an existing Installation registered through Accounting System API.")
    @APIResponse(
            responseCode = "200",
            description = "Installation has been deleted successfully.",
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
            description = "Installation has not been found.",
            content = @Content(schema = @Schema(
                    type = SchemaType.ARRAY,
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
    @AccessPermissions({
            @AccessPermission(collection = Collection.Installation, operation = Operation.DELETE, precedence = 1),
            @AccessPermission(collection = Collection.Project, operation = ACCESS_PROJECT, precedence = 2)
    })
    public Response delete(@Parameter(
            description = "The Installation to be deleted.",
            required = true,
            example = "507f1f77bcf86cd799439011",
            schema = @Schema(type = SchemaType.STRING))
                           @PathParam("id") @Valid @NotFoundEntity(repository = InstallationRepository.class, message = "There is no Installation with the following id:") String id) {

        var success = installationService.delete(id);

        var successResponse = new InformativeResponse();

        if(success){
            successResponse.code = 200;
            successResponse.message = "Installation has been deleted successfully.";
        } else {
            successResponse.code = 500;
            successResponse.message = "Installation cannot be deleted due to a server issue. Please try again.";
        }
        return Response.ok().entity(successResponse).build();
    }

    @Tag(name = "Installation")
    @org.eclipse.microprofile.openapi.annotations.Operation(
            summary = "Returns an existing Installation.",
            description = "This operation accepts the id of an Installation and fetches from the database the corresponding record.")
    @APIResponse(
            responseCode = "200",
            description = "The corresponding Installation.",
            content = @Content(schema = @Schema(
                    type = SchemaType.OBJECT,
                    implementation = InstallationResponseDto.class)))
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
            description = "Installation has not been found.",
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
    @AccessPermissions({
            @AccessPermission(collection = Collection.Installation, operation = Operation.READ, precedence = 1),
            @AccessPermission(collection = Collection.Project, operation = ACCESS_PROJECT, precedence = 2)
    })
    public Response get(
            @Parameter(
                    description = "The Installation to be retrieved.",
                    required = true,
                    example = "507f1f77bcf86cd799439011",
                    schema = @Schema(type = SchemaType.STRING))
            @PathParam("id") @Valid @NotFoundEntity(repository = InstallationRepository.class, message = "There is no Installation with the following id:") String id){

        var response = installationService.fetchInstallation(id);

        return Response.ok().entity(response).build();
    }

    @Tag(name = "Installation")
    @org.eclipse.microprofile.openapi.annotations.Operation(
            summary = "Updates an existing Installation.",
            description = "This operation updates an existing Installation registered through the Accounting System API. Finally, " +
                    "you can update a part or all attributes of Installation. The empty or null values are ignored.")
    @APIResponse(
            responseCode = "200",
            description = "Installation was updated successfully.",
            content = @Content(schema = @Schema(
                    type = SchemaType.OBJECT,
                    implementation = InstallationResponseDto.class)))
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
            description = "It is not permitted to perform the requested operation.",
            content = @Content(schema = @Schema(
                    type = SchemaType.OBJECT,
                    implementation = InformativeResponse.class)))
    @APIResponse(
            responseCode = "404",
            description = "Installation has not been found.",
            content = @Content(schema = @Schema(
                    type = SchemaType.OBJECT,
                    implementation = InformativeResponse.class)))
    @APIResponse(
            responseCode = "409",
            description = "The Installation already exists.",
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
    @AccessPermissions({
            @AccessPermission(collection = Collection.Installation, operation = Operation.UPDATE, precedence = 1),
            @AccessPermission(collection = Collection.Project, operation = ACCESS_PROJECT, precedence = 2)
    })    public Response update(
            @Parameter(
                    description = "The Installation to be updated.",
                    required = true,
                    example = "507f1f77bcf86cd799439011",
                    schema = @Schema(type = SchemaType.STRING))
            @PathParam("id") @Valid @NotFoundEntity(repository = InstallationRepository.class, message = "There is no Installation with the following id:") String id, @Valid @NotNull(message = "The request body is empty.") UpdateInstallationRequestDto updateInstallationRequestDto){

        var response = installationService.update(id, updateInstallationRequestDto);

        return Response.ok().entity(response).build();
    }

    @Tag(name = "Installation")
    @org.eclipse.microprofile.openapi.annotations.Operation(
            summary = "Generates a new Access Control Entry.",
            description = "This endpoint is responsible for generating a new Access Control Entry. " +
                    "Access Control Entry rules specify which clients are granted or denied access to particular Installation entities. " +
                    "It should be noted that the combination {who, collection, entity} is unique. Therefore, only one Access Control entry can be created for each client and each entity.")
    @APIResponse(
            responseCode = "200",
            description = "Access Control entry has been created successfully.",
            content = @Content(schema = @Schema(
                    type = SchemaType.OBJECT,
                    implementation = InformativeResponse.class)))
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
            description = "Installation has not been found.",
            content = @Content(schema = @Schema(
                    type = SchemaType.OBJECT,
                    implementation = InformativeResponse.class)))
    @APIResponse(
            responseCode = "409",
            description = "There is an Access Control Entry with this {who, collection, entity}.",
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
    @Path("/{installation_id}/acl/{who}")
    @Produces(value = MediaType.APPLICATION_JSON)
    @Consumes(value = MediaType.APPLICATION_JSON)
    @AccessPermission(collection = Collection.Installation, operation = ACL)
    public Response createAccessControl(@Valid @NotNull(message = "The request body is empty.") AccessControlRequestDto accessControlRequestDto,
                                        @Parameter(
                                                description = "installation_id is the id of the entity to which the permissions apply.",
                                                required = true,
                                                example = "507f1f77bcf86cd799439011",
                                                schema = @Schema(type = SchemaType.STRING))
                                        @PathParam("installation_id")
                                        @Valid
                                        @NotFoundEntity(repository = InstallationRepository.class, message = "There is no Installation with the following id:") String installationId,
                                        @Parameter(
                                                description = "who is the id of a Client that the Access Control grants access.",
                                                required = true,
                                                example = "fbdb4e4a-6e93-4b08-a1e7-0b7bd08520a6",
                                                schema = @Schema(type = SchemaType.STRING))
                                        @PathParam("who") String who) {


        var informativeResponse = accessControlService.grantPermission(installationId, who, accessControlRequestDto, Collection.Installation, installationRepository);

        return Response.ok().entity(informativeResponse).build();
    }

    @Tag(name = "Installation")
    @org.eclipse.microprofile.openapi.annotations.Operation(
            summary = "Modify an existing Access Control Entry.",
            description = "This endpoint is responsible for updating an existing Access Control Entry. It will modify a specific Access Control Entry " +
                    "which has granted permissions on an Installation to a specific client." +
                    "You can update a part or all attributes of the Access Control entity.")
    @APIResponse(
            responseCode = "200",
            description = "Access Control entry has been updated successfully.",
            content = @Content(schema = @Schema(
                    type = SchemaType.OBJECT,
                    implementation = InformativeResponse.class)))
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
            description = "Installation has not been found.",
            content = @Content(schema = @Schema(
                    type = SchemaType.OBJECT,
                    implementation = InformativeResponse.class)))
    @APIResponse(
            responseCode = "409",
            description = "There is an Access Control Entry with this {who, collection, entity}.",
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
    @Path("/{installation_id}/acl/{who}")
    @Produces(value = MediaType.APPLICATION_JSON)
    @Consumes(value = MediaType.APPLICATION_JSON)
    @AccessPermission(collection = Collection.Installation, operation = ACL)
    public Response modifyAccessControl(@Valid @NotNull(message = "The request body is empty.") AccessControlUpdateDto accessControlUpdateDto,
                                        @Parameter(
                                                description = "installation_id in which permissions have been granted.",
                                                required = true,
                                                example = "507f1f77bcf86cd799439011",
                                                schema = @Schema(type = SchemaType.STRING))
                                        @PathParam("installation_id")
                                        @Valid
                                        @NotFoundEntity(repository = InstallationRepository.class, message = "There is no Installation with the following id:") String installationId,
                                        @Parameter(
                                                description = "who is the client to whom the permissions have been granted.",
                                                required = true,
                                                example = "fbdb4e4a-6e93-4b08-a1e7-0b7bd08520a6",
                                                schema = @Schema(type = SchemaType.STRING))
                                        @PathParam("who") String who) {


        var response = accessControlService.modifyPermission(installationId, who, accessControlUpdateDto, Collection.Installation, installationRepository);

        return Response.ok().entity(response).build();
    }

    @Tag(name = "Installation")
    @org.eclipse.microprofile.openapi.annotations.Operation(
            summary = "Deletes an existing Access Control entry.",
            description = "You can delete the permissions that a client can access to manage a specific Installation.")
    @APIResponse(
            responseCode = "200",
            description = "Access Control entry has been deleted successfully.",
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
            description = "Installation has not been found.",
            content = @Content(schema = @Schema(
                    type = SchemaType.ARRAY,
                    implementation = InformativeResponse.class)))
    @APIResponse(
            responseCode = "500",
            description = "Internal Server Errors.",
            content = @Content(schema = @Schema(
                    type = SchemaType.OBJECT,
                    implementation = InformativeResponse.class)))
    @SecurityRequirement(name = "Authentication")

    @DELETE()
    @Path("/{installation_id}/acl/{who}")
    @Produces(value = MediaType.APPLICATION_JSON)
    @AccessPermission(collection = Collection.Installation, operation = ACL)
    public Response deleteAccessControl(@Parameter(
            description = "installation_id in which permissions have been granted.",
            required = true,
            example = "507f1f77bcf86cd799439011",
            schema = @Schema(type = SchemaType.STRING))
                                        @PathParam("installation_id")
                                        @Valid
                                        @NotFoundEntity(repository = InstallationRepository.class, message = "There is no Installation with the following id:") String installationId,
                                        @Parameter(
                                                description = "who is the client to whom the permissions have been granted.",
                                                required = true,
                                                example = "fbdb4e4a-6e93-4b08-a1e7-0b7bd08520a6",
                                                schema = @Schema(type = SchemaType.STRING))
                                        @PathParam("who") String who) {


        var successResponse = accessControlService.deletePermission(installationId, who, Collection.Installation, installationRepository);

        return Response.ok().entity(successResponse).build();
    }

    @Tag(name = "Installation")
    @org.eclipse.microprofile.openapi.annotations.Operation(
            summary = "Returns an existing Access Control entry.",
            description = "This operation returns the Access Control entry created for a client upon an Installation entity.")
    @APIResponse(
            responseCode = "200",
            description = "The corresponding Access Control entry.",
            content = @Content(schema = @Schema(
                    type = SchemaType.OBJECT,
                    implementation = AccessControlResponseDto.class)))
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
            description = "Access Control entry has not been found.",
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
    @Path("/{installation_id}/acl/{who}")
    @Produces(value = MediaType.APPLICATION_JSON)
    @AccessPermission(collection = Collection.Installation, operation = ACL)
    public Response getAccessControl(
            @Parameter(
                    description = "installation_id in which permissions have been granted.",
                    required = true,
                    example = "507f1f77bcf86cd799439011",
                    schema = @Schema(type = SchemaType.STRING))
            @PathParam("installation_id")
            @Valid
            @NotFoundEntity(repository = InstallationRepository.class, message = "There is no Installation with the following id:") String installationId,
            @Parameter(
                    description = "who is the client to whom the permissions have been granted.",
                    required = true,
                    example = "fbdb4e4a-6e93-4b08-a1e7-0b7bd08520a6",
                    schema = @Schema(type = SchemaType.STRING))
            @PathParam("who") String who){

        var response = accessControlService.fetchPermission(installationId, who, installationRepository);

        return Response.ok().entity(response).build();
    }

    @Tag(name = "Installation")
    @org.eclipse.microprofile.openapi.annotations.Operation(
            summary = "Returns all Access Control entries that have been created for Installation collection.",
            description = "Returns all Access Control entries that have been created for Installation collection.")
    @APIResponse(
            responseCode = "200",
            description = "The corresponding Access Control entries.",
            content = @Content(schema = @Schema(
                    type = SchemaType.ARRAY,
                    implementation = AccessControlResponseDto.class)))
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

    @GET
    @Path("/acl")
    @Produces(value = MediaType.APPLICATION_JSON)
    @AccessPermission(collection = Collection.Installation, operation = ACL)
    public Response getAllAccessControl(){

        var response = accessControlService.fetchAllPermissions(installationRepository);

        return Response.ok().entity(response).build();
    }

    @Tag(name = "Creating and assigning a Metric")
    @org.eclipse.microprofile.openapi.annotations.Operation(
            summary = "Assigns a new Metric to a specific Installation.",
            description = "Fundamentally, this operation creates a new Metric and assigns it to a specific Installation. Essentially, a hierarchical structure is created " +
                    "with the given Project as the root, the Provider as an intermediate node and the Installation as a leaf. " +
                    "Metric is assigned to the given Installation but belongs to the hierarchical structure Project -> Provider -> Installation.")
    @APIResponse(
            responseCode = "201",
            description = "Metric has been successfully created and assigned.",
            content = @Content(schema = @Schema(
                    type = SchemaType.OBJECT,
                    implementation = MetricResponseDto.class)))
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
            responseCode = "404",
            description = "Metric Definition has not been found.",
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
    @Path("/{installationId}/metrics")
    @Produces(value = MediaType.APPLICATION_JSON)
    @Consumes(value = MediaType.APPLICATION_JSON)
    @AccessPermissions({
            @AccessPermission(collection = Collection.Project, operation = ACCESS_PROJECT, precedence = 1)
    })
    public Response save(
            @Parameter(
                    description = "The Installation to which Metric will be assigned.",
                    required = true,
                    example = "507f1f77bcf86cd799439011",
                    schema = @Schema(type = SchemaType.STRING))
            @PathParam("installationId") @Valid @NotFoundEntity(repository = InstallationRepository.class, message = "There is no Installation with the following id:") String installationId,
            @Valid @NotNull(message = "The request body is empty.") MetricRequestDto metricRequestDto, @Context UriInfo uriInfo) {

        var serverInfo = new ResteasyUriInfo(serverUrl.concat(basePath).concat("/metrics"), basePath);

        var response = installationService.assignMetric(installationId, metricRequestDto);

        return Response.created(serverInfo.getAbsolutePathBuilder().path(response.id).build()).entity(response).build();
    }

    @Tag(name = "Fetching Metrics from different levels")
    @org.eclipse.microprofile.openapi.annotations.Operation(
            summary = "Returns all Metrics under a specific Installation.",
            description = "This operation is responsible for fetching all Metrics under a specific Installation. "+
                    "By passing the Project and Provider IDs to which the Installation belongs as well as the Installation ID, you can retrieve all Metrics that have been assigned to this specific Installation. By default, the first page of 10 Metrics will be returned. " +
                    "You can tune the default values by using the query parameters page and size.")
    @APIResponse(
            responseCode = "200",
            description = "All Metrics.",
            content = @Content(schema = @Schema(
                    type = SchemaType.ARRAY,
                    implementation = MetricProjection.class)))
    @APIResponse(
            responseCode = "401",
            description = "Client has not been authenticated.",
            content = @Content(schema = @Schema(
                    type = SchemaType.OBJECT,
                    implementation = InformativeResponse.class)))
    @APIResponse(
            responseCode = "404",
            description = "Not found.",
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
    @Path("/{installationId}/metrics")
    @Produces(value = MediaType.APPLICATION_JSON)
    @AccessPermissions({
            @AccessPermission(collection = Collection.Project, operation = ACCESS_PROJECT, precedence = 1)
    })
    public Response getAllMetricsUnderAnInstallation(
            @Parameter(
                    description = "Τhe Installation id with which you can request all the Metrics that have been assigned to it.",
                    required = true,
                    example = "grnet",
                    schema = @Schema(type = SchemaType.STRING))
            @PathParam("installationId") @Valid @NotFoundEntity(repository = InstallationRepository.class, message = "There is no Installation with the following id:") String installationId,
            @Parameter(name = "page", in = QUERY,
                    description = "Indicates the page number. Page number must be >= 1.") @DefaultValue("1") @QueryParam("page") int page,
            @Parameter(name = "size", in = QUERY,
                    description = "The page size.") @DefaultValue("10") @QueryParam("size") int size,
            @Context UriInfo uriInfo) {

        if(page <1){
            throw new BadRequestException("Page number must be >= 1.");
        }

        var response = hierarchicalRelationService.fetchAllMetricsUnderAnInstallation(installationId, page-1, size, uriInfo);

        return Response.ok().entity(response).build();
    }
}
