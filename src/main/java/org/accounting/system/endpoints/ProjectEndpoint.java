package org.accounting.system.endpoints;

import io.quarkus.security.Authenticated;
import org.accounting.system.constraints.NotFoundEntity;
import org.accounting.system.dtos.InformativeResponse;
import org.accounting.system.dtos.metric.MetricRequestDto;
import org.accounting.system.dtos.metric.MetricResponseDto;
import org.accounting.system.dtos.project.ProjectResponseDto;
import org.accounting.system.entities.HierarchicalRelation;
import org.accounting.system.entities.projections.MetricProjection;
import org.accounting.system.repositories.ProjectRepository;
import org.accounting.system.repositories.installation.InstallationRepository;
import org.accounting.system.repositories.provider.ProviderRepository;
import org.accounting.system.services.HierarchicalRelationService;
import org.accounting.system.services.ProjectService;
import org.accounting.system.services.installation.InstallationService;
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
import org.jboss.resteasy.specimpl.ResteasyUriInfo;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.BadRequestException;
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
    InstallationService installationService;

    @Inject
    HierarchicalRelationService hierarchicalRelationService;

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
            description = "User/Service has not been authenticated.",
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
    @Path("/{id}")
    @Produces(value = MediaType.APPLICATION_JSON)
    @SecurityRequirement(name = "Authentication")

    public Response get(
            @Parameter(
                    description = "The Project to be registered.",
                    required = true,
                    example = "447535",
                    schema = @Schema(type = SchemaType.STRING))
            @PathParam("id") String id){

        var response = projectService.getById(id);

        return Response.ok().entity(response).build();
    }

    @Tag(name = "Creating and assigning a Metric")
    @Operation(
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
            description = "User/Service has not been authenticated.",
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
    @Path("/{projectId}/providers/{providerId}/installations/{installationId}/metrics")
    @Produces(value = MediaType.APPLICATION_JSON)
    @Consumes(value = MediaType.APPLICATION_JSON)
    public Response save(
            @Parameter(
                    description = "The Project which is the root of the hierarchical structure.",
                    required = true,
                    example = "702645",
                    schema = @Schema(type = SchemaType.STRING))
            @PathParam("projectId") @Valid @NotFoundEntity(repository = ProjectRepository.class, id = String.class, message = "There is no Project with the following id:") String projectId,
            @Parameter(
                    description = "The Provider which belongs to a Project and is the intermediate node of the hierarchical structure.",
                    required = true,
                    example = "grnet",
                    schema = @Schema(type = SchemaType.STRING))
            @PathParam("providerId") @Valid @NotFoundEntity(repository = ProviderRepository.class, id = String.class, message = "There is no Provider with the following id:") String providerId,
            @Parameter(
                    description = "The Installation to which Metric will be assigned. Typically, is a leaf of the hierarchical structure.",
                    required = true,
                    example = "507f1f77bcf86cd799439011",
                    schema = @Schema(type = SchemaType.STRING))
            @PathParam("installationId") @Valid @NotFoundEntity(repository = InstallationRepository.class, message = "There is no Installation with the following id:") String installationId,
            @Valid @NotNull(message = "The request body is empty.") MetricRequestDto metricRequestDto, @Context UriInfo uriInfo) {

        var serverInfo = new ResteasyUriInfo(serverUrl.concat(basePath).concat("/metrics"), basePath);

        installationService.checkIfInstallationBelongsToProvider(installationId, providerId);

        var response = hierarchicalRelationService.assign(projectId, providerId, installationId, metricRequestDto);

        return Response.created(serverInfo.getAbsolutePathBuilder().path(response.id).build()).entity(response).build();
    }

    @Tag(name = "Fetching Metrics for different levels")
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
            description = "User/Service has not been authenticated.",
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

        var response = hierarchicalRelationService.fetchAllMetrics(id, page-1, size, uriInfo);

//        SimpleModule module = new SimpleModule();
//        module.addSerializer(HierarchicalRelationProjection.class, new HierarchicalRelationSerializer());
//        objectMapper.registerModule(module);
//
//        String serialized = objectMapper.writeValueAsString(response);

        return Response.ok().entity(response).build();
    }

    @Tag(name = "Fetching Metrics for different levels")
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
            description = "User/Service has not been authenticated.",
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

        var response = hierarchicalRelationService.fetchAllMetrics(projectId + HierarchicalRelation.PATH_SEPARATOR + providerId, page-1, size, uriInfo);

        return Response.ok().entity(response).build();
    }

    @Tag(name = "Fetching Metrics for different levels")
    @Operation(
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
            description = "User/Service has not been authenticated.",
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
    @Path("/{projectId}/providers/{providerId}/installations/{installationId}/metrics")
    @Produces(value = MediaType.APPLICATION_JSON)
    public Response getAllMetricsUnderAnInstallation(
            @Parameter(
                    description = "Τhe Project id to which the Provider belongs.",
                    required = true,
                    example = "704029",
                    schema = @Schema(type = SchemaType.STRING))
            @PathParam("projectId") @Valid @NotFoundEntity(repository = ProjectRepository.class, id = String.class, message = "There is no Project with the following id:") String projectId,
            @Parameter(
                    description = "Τhe Provider id to which the Installation belongs.",
                    required = true,
                    example = "grnet",
                    schema = @Schema(type = SchemaType.STRING))
            @PathParam("providerId") @Valid @NotFoundEntity(repository = ProviderRepository.class, id = String.class, message = "There is no Provider with the following id:") String providerId,
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

        var response = hierarchicalRelationService.fetchAllMetrics(projectId + HierarchicalRelation.PATH_SEPARATOR + providerId + HierarchicalRelation.PATH_SEPARATOR + installationId, page-1, size, uriInfo);

        return Response.ok().entity(response).build();
    }
}
