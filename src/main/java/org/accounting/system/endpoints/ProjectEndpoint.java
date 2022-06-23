package org.accounting.system.endpoints;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import io.quarkus.security.Authenticated;
import org.accounting.system.constraints.NotFoundEntity;
import org.accounting.system.dtos.InformativeResponse;
import org.accounting.system.dtos.acl.AccessControlRequestDto;
import org.accounting.system.dtos.acl.AccessControlResponseDto;
import org.accounting.system.dtos.acl.AccessControlUpdateDto;
import org.accounting.system.dtos.project.AssociateProjectProviderRequestDto;
import org.accounting.system.dtos.project.DissociateProjectProviderRequestDto;
import org.accounting.system.dtos.project.ProjectResponseDto;
import org.accounting.system.entities.projections.HierarchicalRelationProjection;
import org.accounting.system.entities.projections.MetricProjection;
import org.accounting.system.enums.Collection;
import org.accounting.system.interceptors.annotations.AccessPermission;
import org.accounting.system.interceptors.annotations.AccessPermissions;
import org.accounting.system.repositories.project.ProjectRepository;
import org.accounting.system.repositories.provider.ProviderRepository;
import org.accounting.system.serializer.HierarchicalRelationSerializer;
import org.accounting.system.services.HierarchicalRelationService;
import org.accounting.system.services.ProjectService;
import org.accounting.system.services.acl.AccessControlService;
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

import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
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
import static org.eclipse.microprofile.openapi.annotations.enums.ParameterIn.QUERY;

@Path("/projects")
@Authenticated
@SecurityScheme(securitySchemeName = "Authentication",
        description = "JWT token",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER)

public class ProjectEndpoint {

    @Inject
    ProjectService projectService;

    @Inject
    ProjectRepository projectRepository;

    @Inject
    HierarchicalRelationService hierarchicalRelationService;

    @Inject
    ObjectMapper objectMapper;

    @Inject
    AccessControlService accessControlService;

    @Inject
    ProviderRepository providerRepository;

    @ConfigProperty(name = "quarkus.resteasy.path")
    String basePath;

    @ConfigProperty(name = "server.url")
    String serverUrl;

    @Tag(name = "Project")
    @Operation(
            summary = "Registration of a Project in the Accounting System API.",
            description = "The Accounting System communicates with Open Aire to retrieve the necessary information of a Project. " +
                    "Subsequently, it keeps specific information from the Open Aire response and stores that information in the database. This operation returns " +
                    "a Project as it is stored in the Accounting System API.")
    @APIResponse(
            responseCode = "200",
            description = "The corresponding Project.",
            content = @Content(schema = @Schema(
                    type = SchemaType.OBJECT,
                    implementation = ProjectResponseDto.class)))
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
            description = "Project has not been found.",
            content = @Content(schema = @Schema(
                    type = SchemaType.OBJECT,
                    implementation = InformativeResponse.class)))
    @APIResponse(
            responseCode = "500",
            description = "Internal Server Errors.",
            content = @Content(schema = @Schema(
                    type = SchemaType.OBJECT,
                    implementation = InformativeResponse.class)))

    @POST
    @Path("/{id}")
    @Produces(value = MediaType.APPLICATION_JSON)
    @SecurityRequirement(name = "Authentication")
    @AccessPermission(collection = Collection.Project, operation = ACCESS_PROJECT)
    public Response save(
            @Parameter(
                    description = "The Project to be registered.",
                    required = true,
                    example = "447535",
                    schema = @Schema(type = SchemaType.STRING))
            @PathParam("id") String id){

        var response = projectService.save(id);

        return Response.ok().entity(response).build();
    }

    @Tag(name = "Fetching Metrics from different levels")
    @Operation(
            summary = "Returns all Metrics under a specific Project.",
            description = "This operation is responsible for fetching all Metrics under a specific Project. "+
                    "By passing the Project ID, you can retrieve all Metrics that have been assigned to it. By default, the first page of 10 Metrics will be returned. " +
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
    @Path("/{projectId}/metrics")
    @Produces(value = MediaType.APPLICATION_JSON)
    @AccessPermission(collection = Collection.Project, operation = ACCESS_PROJECT)
    public Response getAllMetricsUnderAProject(
            @Parameter(
                    description = "Τhe Project id with which you can request all the Metrics that have been assigned to it.",
                    required = true,
                    example = "704029",
                    schema = @Schema(type = SchemaType.STRING))
            @PathParam("projectId") @Valid @NotFoundEntity(repository = ProjectRepository.class, id = String.class, message = "There is no Project with the following id:") String id,
            @Parameter(name = "page", in = QUERY,
                    description = "Indicates the page number. Page number must be >= 1.") @DefaultValue("1") @QueryParam("page") int page,
            @Parameter(name = "size", in = QUERY,
                    description = "The page size.") @DefaultValue("10") @QueryParam("size") int size,
            @Context UriInfo uriInfo) {

        if(page <1){
            throw new BadRequestException("Page number must be >= 1.");
        }

        var response = hierarchicalRelationService.fetchAllMetricsByProjectId(id, page-1, size, uriInfo);

        return Response.ok().entity(response).build();
    }

    @Tag(name = "Fetching Metrics from different levels")
    @Operation(
            summary = "Returns all Metrics under a specific Provider.",
            description = "This operation is responsible for fetching all Metrics under a specific Provider. "+
                    "By passing the Project ID to which the Provider belongs as well as the Provider ID, you can retrieve all Metrics that have been assigned to this specific Provider. By default, the first page of 10 Metrics will be returned. " +
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
    @Path("/{projectId}/providers/{providerId}/metrics")
    @Produces(value = MediaType.APPLICATION_JSON)
    @AccessPermissions({
            @AccessPermission(collection = Collection.Project, operation = ACCESS_PROJECT, precedence = 1),
            @AccessPermission(collection = Collection.Provider, operation = org.accounting.system.enums.Operation.ACCESS_PROVIDER, precedence = 2)
    })
    public Response getAllMetricsUnderAProvider(
            @Parameter(
                    description = "Τhe Project id to which the Provider belongs.",
                    required = true,
                    example = "704029",
                    schema = @Schema(type = SchemaType.STRING))
            @PathParam("projectId") @Valid @NotFoundEntity(repository = ProjectRepository.class, id = String.class, message = "There is no Project with the following id:") String projectId,
            @Parameter(
                    description = "Τhe Provider id with which you can request all the Metrics that have been assigned to it.",
                    required = true,
                    example = "grnet",
                    schema = @Schema(type = SchemaType.STRING))
            @PathParam("providerId") @Valid @NotFoundEntity(repository = ProviderRepository.class, id = String.class, message = "There is no Provider with the following id:") String providerId,
            @Parameter(name = "page", in = QUERY,
                    description = "Indicates the page number. Page number must be >= 1.") @DefaultValue("1") @QueryParam("page") int page,
            @Parameter(name = "size", in = QUERY,
                    description = "The page size.") @DefaultValue("10") @QueryParam("size") int size,
            @Context UriInfo uriInfo) {

        if(page <1){
            throw new BadRequestException("Page number must be >= 1.");
        }

        var response = hierarchicalRelationService.fetchAllMetricsUnderAProvider(projectId, providerId, page-1, size, uriInfo);

        return Response.ok().entity(response).build();
    }

    @Tag(name = "Project")
    @Operation(
            summary = "Associate a Project with different Providers.",
            description = "There is a hierarchical relation between Project and Providers which can be expressed as follows: a Project can have a number of different Providers. " +
                    "By passing a list of Provider ids to this operation, you can associate those Providers with a specific Project. Finally, it should be noted that any Provider can belong to more than one Project.")
    @APIResponse(
            responseCode = "200",
            description = "Providers have been successfully associated with the Project.",
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
            description = "Project has not been found.",
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
    @Path("/{id}/associate")
    @Produces(value = MediaType.APPLICATION_JSON)
    @Consumes(value = MediaType.APPLICATION_JSON)
    @AccessPermission(collection = Collection.Project, operation = ACCESS_PROJECT)
    public Response associateProjectWithProviders(
            @Parameter(
                    description = "The Project in which the Providers will be associated with.",
                    required = true,
                    example = "447535",
                    schema = @Schema(type = SchemaType.STRING))
            @PathParam("id") @Valid @NotFoundEntity(repository = ProjectRepository.class, id = String.class, message = "There is no Project with the following id:") String id, @Valid @NotNull(message = "The request body is empty.") AssociateProjectProviderRequestDto request) {

        projectService.associateProjectWithProviders(id, request.providers);

        InformativeResponse response = new InformativeResponse();
        response.code = 200;
        response.message = "The following providers " +request.providers.toString() + " have been associated with Project "+id;

        return Response.ok().entity(response).build();
    }

    @Tag(name = "Project")
    @Operation(
            summary = "Dissociate Providers from a Project.",
            description = "There is a hierarchical relation between Project and Providers which can be expressed as follows: a Project can have a number of different Providers. " +
                    "By passing a list of Provider ids to this operation, you can correlate those Providers with a specific Project. Finally, it should be noted that any Provider can belong to more than one Project.")
    @APIResponse(
            responseCode = "200",
            description = "Providers have been successfully dissociated from the Project.",
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
            description = "Project has not been found.",
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
    @Path("/{id}/dissociate")
    @Produces(value = MediaType.APPLICATION_JSON)
    @Consumes(value = MediaType.APPLICATION_JSON)
    @AccessPermission(collection = Collection.Project, operation = ACCESS_PROJECT)
    public Response dissociateProvidersFromProject(
            @Parameter(
                    description = "The Project from which the Providers will be dissociated.",
                    required = true,
                    example = "447535",
                    schema = @Schema(type = SchemaType.STRING))
            @PathParam("id") @Valid @NotFoundEntity(repository = ProjectRepository.class, id = String.class, message = "There is no Project with the following id:") String id, @Valid @NotNull(message = "The request body is empty.") DissociateProjectProviderRequestDto request) {

        projectService.dissociateProviderFromProject(id, request.providers);

        InformativeResponse response = new InformativeResponse();
        response.code = 200;
        response.message = "The following providers " +request.providers.toString() + " have been dissociated from Project "+id;

        return Response.ok().entity(response).build();
    }

    @Tag(name = "Project")
    @Operation(
            summary = "Returns the Project hierarchical structure.",
            description = "Basically, this operations returns the providers and installations associated with a specific Project.")
    @APIResponse(
            responseCode = "200",
            description = "The corresponding Project hierarchical structure.",
            content = @Content(schema = @Schema(
                    type = SchemaType.OBJECT,
                    implementation = Object.class),
            example = "{\n" +
                    "    \"project\": \"101017567\",\n" +
                    "    \"providers\": [\n" +
                    "        {\n" +
                    "            \"provider\": \"bioexcel\",\n" +
                    "            \"installations\": []\n" +
                    "        },\n" +
                    "        {\n" +
                    "            \"provider\": \"grnet\",\n" +
                    "            \"installations\": [\n" +
                    "                {\n" +
                    "                    \"installation\": \"629eff3a7b0f5d02ab734879\"\n" +
                    "                }\n" +
                    "            ]\n" +
                    "        },\n" +
                    "        {\n" +
                    "            \"provider\": \"osmooc\",\n" +
                    "            \"installations\": []\n" +
                    "        },\n" +
                    "        {\n" +
                    "            \"provider\": \"sites\",\n" +
                    "            \"installations\": []\n" +
                    "        }\n" +
                    "    ]\n" +
                    "}"))
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
            description = "Project has not been found.",
            content = @Content(schema = @Schema(
                    type = SchemaType.OBJECT,
                    implementation = InformativeResponse.class)))
    @APIResponse(
            responseCode = "500",
            description = "Internal Server Errors.",
            content = @Content(schema = @Schema(
                    type = SchemaType.OBJECT,
                    implementation = InformativeResponse.class)))

    @GET
    @Path("/{id}/hierarchical-structure")
    @Produces(value = MediaType.APPLICATION_JSON)
    @SecurityRequirement(name = "Authentication")
    @AccessPermission(collection = Collection.Project, operation = ACCESS_PROJECT)
    public Response getProjectHierarchicalStructure(
            @Parameter(
                    description = "The Project ID.",
                    required = true,
                    example = "447535",
                    schema = @Schema(type = SchemaType.STRING))
            @Valid @NotFoundEntity(repository = ProjectRepository.class, id = String.class, message = "There is no Project with the following id:") @PathParam("id") String id) throws JsonProcessingException {

        var response = projectService.hierarchicalStructure(id);

        if(response.isEmpty()){
            throw new NotFoundException("There are no any Provider or Installation correlating with the Project: "+id);
        }

        SimpleModule module = new SimpleModule();
        module.addSerializer(new HierarchicalRelationSerializer(HierarchicalRelationProjection.class));
        objectMapper.registerModule(module);

        String serialized = objectMapper.writeValueAsString(response.get(0));

        return Response.ok().entity(serialized).build();
    }

    @Tag(name = "Project")
    @Operation(
            summary = "Generates a new Access Control Entry.",
            description = "This endpoint is responsible for generating a new Access Control Entry. " +
                    "Access Control Entry rules specify which clients are granted or denied access to particular Project entities. " +
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
            description = "Project has not been found.",
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
    @Path("/{project_id}/acl/{who}")
    @Produces(value = MediaType.APPLICATION_JSON)
    @Consumes(value = MediaType.APPLICATION_JSON)
    @AccessPermission(collection = Collection.Project, operation = org.accounting.system.enums.Operation.ACL)
    public Response createAccessControl(@Valid @NotNull(message = "The request body is empty.") AccessControlRequestDto accessControlRequestDto,
                                        @Parameter(
                                                description = "project_id is the id of the entity to which the permissions apply.",
                                                required = true,
                                                example = "704567",
                                                schema = @Schema(type = SchemaType.STRING))
                                        @PathParam("project_id") String projectId,
                                        @Parameter(
                                                description = "who is the id of a Client that the Access Control grants access.",
                                                required = true,
                                                example = "fbdb4e4a-6e93-4b08-a1e7-0b7bd08520a6",
                                                schema = @Schema(type = SchemaType.STRING))
                                        @PathParam("who") String who) {


        var informativeResponse = accessControlService.grantPermission(projectId, who, accessControlRequestDto, Collection.Project, projectRepository);

        return Response.ok().entity(informativeResponse).build();
    }

    @Tag(name = "Project")
    @Operation(
            summary = "Modify an existing Access Control Entry.",
            description = "This endpoint is responsible for updating an existing Access Control Entry. It will modify a specific Access Control Entry " +
                    "which has granted permissions on a Project to a specific client." +
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
            description = "Project has not been found.",
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
    @Path("/{project_id}/acl/{who}")
    @Produces(value = MediaType.APPLICATION_JSON)
    @Consumes(value = MediaType.APPLICATION_JSON)
    @AccessPermission(collection = Collection.Project, operation = org.accounting.system.enums.Operation.ACL)
    public Response modifyAccessControl(@Valid @NotNull(message = "The request body is empty.") AccessControlUpdateDto accessControlUpdateDto,
                                        @Parameter(
                                                description = "project_id in which permissions have been granted.",
                                                required = true,
                                                example = "704567",
                                                schema = @Schema(type = SchemaType.STRING))
                                        @PathParam("project_id") String projectId,
                                        @Parameter(
                                                description = "who is the client to whom the permissions have been granted.",
                                                required = true,
                                                example = "fbdb4e4a-6e93-4b08-a1e7-0b7bd08520a6",
                                                schema = @Schema(type = SchemaType.STRING))
                                        @PathParam("who") String who) {


        var response = accessControlService.modifyPermission(projectId, who, accessControlUpdateDto, Collection.Project, projectRepository);

        return Response.ok().entity(response).build();
    }

    @Tag(name = "Project")
    @Operation(
            summary = "Deletes an existing Access Control entry.",
            description = "You can delete the permissions that a client can access to manage a specific Project.")
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
            description = "Project has not been found.",
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
    @Path("/{project_id}/acl/{who}")
    @Produces(value = MediaType.APPLICATION_JSON)
    @AccessPermission(collection = Collection.Project, operation = org.accounting.system.enums.Operation.ACL)
    public Response deleteAccessControl(@Parameter(
            description = "project_id in which permissions have been granted.",
            required = true,
            example = "704567",
            schema = @Schema(type = SchemaType.STRING))
                                        @PathParam("project_id") String projectId,
                                        @Parameter(
                                                description = "who is the client to whom the permissions have been granted.",
                                                required = true,
                                                example = "fbdb4e4a-6e93-4b08-a1e7-0b7bd08520a6",
                                                schema = @Schema(type = SchemaType.STRING))
                                        @PathParam("who") String who) {


        var successResponse = accessControlService.deletePermission(projectId, who, Collection.Project, projectRepository);

        return Response.ok().entity(successResponse).build();
    }

    @Tag(name = "Project")
    @Operation(
            summary = "Returns an existing Access Control entry.",
            description = "This operation returns the Access Control entry created for a client upon an Project entity.")
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
    @Path("/{project_id}/acl/{who}")
    @Produces(value = MediaType.APPLICATION_JSON)
    @AccessPermission(collection = Collection.Project, operation = org.accounting.system.enums.Operation.ACL)
    public Response getAccessControl(
            @Parameter(
                    description = "project_id in which permissions have been granted.",
                    required = true,
                    example = "704567",
                    schema = @Schema(type = SchemaType.STRING))
            @PathParam("project_id") String projectId,
            @Parameter(
                    description = "who is the client to whom the permissions have been granted.",
                    required = true,
                    example = "fbdb4e4a-6e93-4b08-a1e7-0b7bd08520a6",
                    schema = @Schema(type = SchemaType.STRING))
            @PathParam("who") String who){

        var response = accessControlService.fetchPermission(projectId, who, projectRepository);

        return Response.ok().entity(response).build();
    }

    @Tag(name = "Project")
    @Operation(
            summary = "Returns all Access Control entries that have been created for Project collection.",
            description = "Returns all Access Control entries that have been created for Project collection.")
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
    @AccessPermission(collection = Collection.Project, operation = org.accounting.system.enums.Operation.ACL)
    public Response getAllAccessControl(){

        var response = accessControlService.fetchAllPermissions(projectRepository);

        return Response.ok().entity(response).build();
    }

    @Tag(name = "Provider")
    @Operation(
            summary = "Generates a new Access Control Entry.",
            description = "This endpoint is responsible for generating a new Access Control Entry. " +
                    "Access Control Entry rules specify which clients are granted access to particular Provider of a specific Project. " +
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
            description = "Not Found.",
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
    @Path("/{project_id}/providers/{provider_id}/acl/{who}")
    @Produces(value = MediaType.APPLICATION_JSON)
    @Consumes(value = MediaType.APPLICATION_JSON)
    @AccessPermission(collection = Collection.Project, operation = ACCESS_PROJECT)
    public Response createProviderAccessControl(@Valid @NotNull(message = "The request body is empty.") AccessControlRequestDto accessControlRequestDto,
                                                @Parameter(
                                                        description = "The Project ID that the Provider belongs to.",
                                                        required = true,
                                                        example = "704567",
                                                        schema = @Schema(type = SchemaType.STRING))
                                                @PathParam("project_id")
                                                @Valid
                                                @NotFoundEntity(repository = ProjectRepository.class, id = String.class, message = "There is no Project with the following id:") String projectId,
                                                @Parameter(
                                                description = "provider_id is the id of the entity to which the permissions apply.",
                                                required = true,
                                                example = "sites",
                                                schema = @Schema(type = SchemaType.STRING))
                                        @PathParam("provider_id")
                                        @Valid
                                        @NotFoundEntity(repository = ProviderRepository.class, id = String.class, message = "There is no Provider with the following id:") String providerId,
                                        @Parameter(
                                                description = "who is the id of a Client that the Access Control grants access.",
                                                required = true,
                                                example = "fbdb4e4a-6e93-4b08-a1e7-0b7bd08520a6",
                                                schema = @Schema(type = SchemaType.STRING))
                                        @PathParam("who") String who) {

        var belongs = hierarchicalRelationService.providerBelongsToProject(projectId, providerId);

        if(!belongs){
            String message = String.format("There is no relationship between Project {%s} and Provider {%s}", projectId, providerId);
            throw new BadRequestException(message);
        }

        var informativeResponse = projectService.grantPermissionToProvider(projectId, providerId, who, accessControlRequestDto, Collection.Provider);

        return Response.ok().entity(informativeResponse).build();
    }

    @Tag(name = "Provider")
    @Operation(
            summary = "Modify an existing Access Control Entry.",
            description = "This endpoint is responsible for updating an existing Access Control Entry. " +
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
            description = "Not Found.",
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
    @Path("/{project_id}/providers/{provider_id}/acl/{who}")
    @Produces(value = MediaType.APPLICATION_JSON)
    @Consumes(value = MediaType.APPLICATION_JSON)
    @AccessPermission(collection = Collection.Project, operation = ACCESS_PROJECT)
    public Response modifyProviderAccessControl(@Valid @NotNull(message = "The request body is empty.") AccessControlUpdateDto accessControlUpdateDto,
                                        @Parameter(
                                                description = "The Project ID that the Provider belongs to.",
                                                required = true,
                                                example = "704567",
                                                schema = @Schema(type = SchemaType.STRING))
                                        @PathParam("project_id")
                                        @Valid
                                        @NotFoundEntity(repository = ProjectRepository.class, id = String.class, message = "There is no Project with the following id:") String projectId,
                                        @Parameter(
                                                description = "provider_id in which permissions have been granted.",
                                                required = true,
                                                example = "sites",
                                                schema = @Schema(type = SchemaType.STRING))
                                        @PathParam("provider_id")
                                        @Valid
                                        @NotFoundEntity(repository = ProviderRepository.class, id = String.class, message = "There is no Provider with the following id:") String providerId,
                                        @Parameter(
                                                description = "who is the client to whom the permissions have been granted.",
                                                required = true,
                                                example = "fbdb4e4a-6e93-4b08-a1e7-0b7bd08520a6",
                                                schema = @Schema(type = SchemaType.STRING))
                                        @PathParam("who") String who) {

        var belongs = hierarchicalRelationService.providerBelongsToProject(projectId, providerId);

        if(!belongs){
            String message = String.format("There is no relationship between Project {%s} and Provider {%s}", projectId, providerId);
            throw new BadRequestException(message);
        }

        var response = projectService.modifyProviderPermission(projectId, providerId, who, accessControlUpdateDto, Collection.Provider);

        return Response.ok().entity(response).build();
    }

    @Tag(name = "Provider")
    @Operation(
            summary = "Deletes an existing Access Control entry.",
            description = "You can delete the permissions that a client can access to manage a specific Provider.")
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
            description = "Provider has not been found.",
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
    @Path("/{project_id}/providers/{provider_id}/acl/{who}")
    @Produces(value = MediaType.APPLICATION_JSON)
    @AccessPermission(collection = Collection.Project, operation = ACCESS_PROJECT)
    public Response deleteProviderAccessControl(
            @Parameter(
                    description = "The Project ID that the Provider belongs to.",
                    required = true,
                    example = "704567",
                    schema = @Schema(type = SchemaType.STRING))
            @PathParam("project_id")
            @Valid
            @NotFoundEntity(repository = ProjectRepository.class, id = String.class, message = "There is no Project with the following id:") String projectId,
            @Parameter(
            description = "provider_id in which permissions have been granted.",
            required = true,
            example = "sites",
            schema = @Schema(type = SchemaType.STRING))
                                        @PathParam("provider_id")
                                        @Valid
                                        @NotFoundEntity(repository = ProviderRepository.class, id = String.class, message = "There is no Provider with the following id:") String providerId,
                                        @Parameter(
                                                description = "who is the client to whom the permissions have been granted.",
                                                required = true,
                                                example = "fbdb4e4a-6e93-4b08-a1e7-0b7bd08520a6",
                                                schema = @Schema(type = SchemaType.STRING))
                                        @PathParam("who") String who) {

        var belongs = hierarchicalRelationService.providerBelongsToProject(projectId, providerId);

        if(!belongs){
            String message = String.format("There is no relationship between Project {%s} and Provider {%s}", projectId, providerId);
            throw new BadRequestException(message);
        }

        var successResponse = projectService.deleteProviderPermission(projectId, providerId, who, Collection.Provider);

        return Response.ok().entity(successResponse).build();
    }

    @Tag(name = "Provider")
    @Operation(
            summary = "Returns an existing Access Control entry.",
            description = "This operation returns the Access Control entry created for a client upon a Provider entity.")
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
    @Path("/{project_id}/providers/{provider_id}/acl/{who}")
    @Produces(value = MediaType.APPLICATION_JSON)
    @AccessPermission(collection = Collection.Project, operation = ACCESS_PROJECT)
    public Response getProviderAccessControl(
            @Parameter(
                    description = "The Project ID that the Provider belongs to.",
                    required = true,
                    example = "704567",
                    schema = @Schema(type = SchemaType.STRING))
            @PathParam("project_id")
            @Valid
            @NotFoundEntity(repository = ProjectRepository.class, id = String.class, message = "There is no Project with the following id:") String projectId,

            @Parameter(
                    description = "provider_id in which permissions have been granted.",
                    required = true,
                    example = "sites",
                    schema = @Schema(type = SchemaType.STRING))
            @PathParam("provider_id")
            @Valid
            @NotFoundEntity(repository = ProviderRepository.class, id = String.class, message = "There is no Provider with the following id:") String providerId,
            @Parameter(
                    description = "who is the client to whom the permissions have been granted.",
                    required = true,
                    example = "fbdb4e4a-6e93-4b08-a1e7-0b7bd08520a6",
                    schema = @Schema(type = SchemaType.STRING))
            @PathParam("who") String who){

        var belongs = hierarchicalRelationService.providerBelongsToProject(projectId, providerId);

        if(!belongs){
            String message = String.format("There is no relationship between Project {%s} and Provider {%s}", projectId, providerId);
            throw new BadRequestException(message);
        }

        var response = projectService.fetchProviderPermission(projectId, providerId, who);

        return Response.ok().entity(response).build();
    }
}
