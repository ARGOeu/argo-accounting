package org.accounting.system.endpoints;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.quarkus.security.Authenticated;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import org.accounting.system.beans.RequestUserContext;
import org.accounting.system.constraints.NotFoundEntity;
import org.accounting.system.dtos.CountDocumentResponse;
import org.accounting.system.dtos.InformativeResponse;
import org.accounting.system.dtos.VersionDto;
import org.accounting.system.dtos.admin.ProjectRegistrationRequest;
import org.accounting.system.dtos.pagination.PageResource;
import org.accounting.system.dtos.project.ProjectRequest;
import org.accounting.system.dtos.project.UpdateProjectRequest;
import org.accounting.system.dtos.resource.ResourceRequest;
import org.accounting.system.dtos.resource.ResourceResponse;
import org.accounting.system.dtos.tenant.OidcTenantConfigRequest;
import org.accounting.system.dtos.tenant.OidcTenantConfigResponse;
import org.accounting.system.dtos.tenant.UpdateOidcTenantConfig;
import org.accounting.system.entities.HierarchicalRelation;
import org.accounting.system.entities.projections.normal.ProjectProjection;
import org.accounting.system.interceptors.annotations.AccessResource;
import org.accounting.system.repositories.OidcTenantConfigRepository;
import org.accounting.system.repositories.ResourceRepository;
import org.accounting.system.repositories.project.ProjectRepository;
import org.accounting.system.services.ProjectService;
import org.accounting.system.services.SystemAdminService;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import static org.eclipse.microprofile.openapi.annotations.enums.ParameterIn.QUERY;

@Path("/admin")
@Authenticated
@SecurityScheme(securitySchemeName = "Authentication",
        description = "JWT token",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER)
public class SystemAdminEndpoint {

    @ConfigProperty(name = "quarkus.application.version")
    String version;

    @Inject
    SystemAdminService systemAdminService;

    @ConfigProperty(name = "quarkus.rest.path")
    String basePath;

    @ConfigProperty(name = "api.server.url")
    String serverUrl;

    @Inject
    ProjectService projectService;

    @Inject
    RequestUserContext requestUserContext;

    @Tag(name = "System Administrator")
    @Operation(
            summary = "Endpoint for registering Projects into the Accounting Service.",
            description = "This endpoint allows system administrators to register a new project into the Accounting Service. Access to this endpoint is restricted to system administrators only.")
    @APIResponse(
            responseCode = "200",
            description = "Informs about the success or failure of the registration process.",
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
            responseCode = "500",
            description = "Internal Server Errors.",
            content = @Content(schema = @Schema(
                    type = SchemaType.OBJECT,
                    implementation = InformativeResponse.class)))
    @SecurityRequirement(name = "Authentication")

    @POST
    @Path("/register-projects")
    @Produces(value = MediaType.APPLICATION_JSON)
    @Consumes(value = MediaType.APPLICATION_JSON)
    @AccessResource
    public Response assignProjects(@Valid @NotNull(message = "The request body is empty." ) ProjectRegistrationRequest request) {

        var response = systemAdminService.registerProjectsToAccountingService(request.projects, requestUserContext.getId());

        return Response.ok().entity(response).build();
    }

    @Tag(name = "System Administrator")
    @Operation(
            summary = "Create a new Project.",
            description = "Creates a new Project in the Accounting Service. Accessible only by system administrators.")
    @APIResponse(
            responseCode = "201",
            description = "Project created successfully.",
            content = @Content(schema = @Schema(
                    type = SchemaType.OBJECT,
                    implementation = ProjectProjection.class)))
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
            description = "Project already exists.",
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
    @Path("/projects")
    @Produces(value = MediaType.APPLICATION_JSON)
    @Consumes(value = MediaType.APPLICATION_JSON)
    @AccessResource
    public Response createProject(@Valid @NotNull(message = "The request body is empty." ) ProjectRequest request, @Context UriInfo uriInfo) {

        if(request.id.contains(HierarchicalRelation.PATH_SEPARATOR)){

            throw new BadRequestException("Project ID should not contain a dot character.");
        }

        var serverInfo = new AccountingUriInfo(serverUrl.concat(basePath).concat(uriInfo.getPath()));

        var response = systemAdminService.createProject(request, requestUserContext.getId());

        return Response.created(serverInfo.getAbsolutePathBuilder().path(response.id).build()).entity(response).build();
    }

    @Tag(name = "System Administrator")
    @Operation(
            summary = "Delete an existing Project.",
            description = "Deletes an existing Project in the Accounting Service. Accessible only by system administrators.")
    @APIResponse(
            responseCode = "200",
            description = "Project deleted successfully.",
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
            responseCode = "500",
            description = "Internal Server Errors.",
            content = @Content(schema = @Schema(
                    type = SchemaType.OBJECT,
                    implementation = InformativeResponse.class)))
    @SecurityRequirement(name = "Authentication")
    @DELETE
    @Path("/projects/{id}")
    @Produces(value = MediaType.APPLICATION_JSON)
    @AccessResource
    public Response deleteProject(@Parameter(
            description = "The Project to be deleted.",
            required = true,
            example = "447535",
            schema = @Schema(type = SchemaType.STRING))
                                       @PathParam("id") @Valid
                                      @NotFoundEntity(repository = ProjectRepository.class, id = String.class, message = "There is no Project with the following id:") String id) {

        systemAdminService.deleteProject(id);

        var response = new InformativeResponse();
        response.code = 200;
        response.message = "Project deleted successfully.";

        return Response.ok().entity(response).build();
    }

    @Tag(name = "System Administrator")
    @Operation(
            summary = "Create a new Resource.",
            description = "Creates a new Resource in the Accounting Service. Accessible only by system administrators.")
    @APIResponse(
            responseCode = "201",
            description = "Resource created successfully.",
            content = @Content(schema = @Schema(
                    type = SchemaType.OBJECT,
                    implementation = ResourceResponse.class)))
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
            description = "Resource already exists.",
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
    @Path("/resources")
    @Produces(value = MediaType.APPLICATION_JSON)
    @Consumes(value = MediaType.APPLICATION_JSON)
    @AccessResource
    public Response createResource(@Valid @NotNull(message = "The request body is empty." ) ResourceRequest request, @Context UriInfo uriInfo) {

        var serverInfo = new AccountingUriInfo(serverUrl.concat(basePath).concat(uriInfo.getPath()));

        var response = systemAdminService.createResource(request);

        return Response.created(serverInfo.getAbsolutePathBuilder().path(response.id).build()).entity(response).build();
    }

    @Tag(name = "System Administrator")
    @Operation(
            summary = "Delete an existing Resource.",
            description = "Deletes an existing Resource in the Accounting Service. Accessible only by system administrators.")
    @APIResponse(
            responseCode = "200",
            description = "Resource deleted successfully.",
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
            responseCode = "500",
            description = "Internal Server Errors.",
            content = @Content(schema = @Schema(
                    type = SchemaType.OBJECT,
                    implementation = InformativeResponse.class)))
    @SecurityRequirement(name = "Authentication")
    @DELETE
    @Path("/resources/{id}")
    @Produces(value = MediaType.APPLICATION_JSON)
    @AccessResource
    public Response deleteResource(@Parameter(
            description = "The Resource to be deleted.",
            required = true,
            example = "unitartu.ut.rocket",
            schema = @Schema(type = SchemaType.STRING))
                                  @PathParam("id") @Valid
                                  @NotFoundEntity(repository = ResourceRepository.class, id = String.class, message = "There is no Resource with the following id:") String id) {

        systemAdminService.deleteResource(id);

        var response = new InformativeResponse();
        response.code = 200;
        response.message = "Resource deleted successfully.";

        return Response.ok().entity(response).build();
    }

    @Tag(name = "System Administrator")
    @Operation(
            summary = "Updates an existing Project.",
            description = "In order to update the resource properties, the body of the request must contain an updated representation of Project. " +
                    "You can update a part or all attributes of Project. The empty or null values are ignored. ")
    @APIResponse(
            responseCode = "200",
            description = "Project was updated successfully.",
            content = @Content(schema = @Schema(
                    type = SchemaType.OBJECT,
                    implementation = ProjectProjection.class)))
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
    @PATCH
    @Path("/projects/{id}")
    @Produces(value = MediaType.APPLICATION_JSON)
    @Consumes(value = MediaType.APPLICATION_JSON)
    @AccessResource
    public Response update(
            @Parameter(
                    description = "The Project to be updated.",
                    required = true,
                    example = "445677",
                    schema = @Schema(type = SchemaType.STRING))
            @PathParam("id") @Valid @NotFoundEntity(repository = ProjectRepository.class, id = String.class, message = "There is no Project with the following id:") String id, @Valid @NotNull(message = "The request body is empty.") UpdateProjectRequest updateProjectRequest) {

        var response = systemAdminService.updateProject(updateProjectRequest, id);

        return Response.ok().entity(response).build();
    }

    @Tag(name = "System Administrator")
    @Operation(
            summary = "Returns the version of the Accounting Service API.",
            description = "Returns the version of the Accounting Service API.")
    @APIResponse(
            responseCode = "200",
            description = "The Accounting Service API version.",
            content = @Content(schema = @Schema(
                    type = SchemaType.OBJECT,
                    implementation = VersionDto.class)))
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
            description = "Internal Server Error.",
            content = @Content(schema = @Schema(
                    type = SchemaType.OBJECT,
                    implementation = InformativeResponse.class)))
    @SecurityRequirement(name = "Authentication")
    @GET
    @Path("/version")
    @Produces(value = MediaType.APPLICATION_JSON)
    @AccessResource
    public Response version() {

        var versionDto = new VersionDto();
        versionDto.version = version;

        return Response.status(Response.Status.OK).entity(versionDto).build();
    }

    @Tag(name = "System Administrator")
    @Operation(
            summary = "Get document count for a specific time period.",
            description = "Returns the number of documents inserted in Metric Definition, Metric, and User collections within the given start and end dates.")
    @APIResponse(
            responseCode = "200",
            description = "Document count retrieved successfully.",
            content = @Content(schema = @Schema(
                    type = SchemaType.OBJECT,
                    implementation = CountDocumentResponse.class)))
    @APIResponse(
            responseCode = "400",
            description = "Invalid date format.",
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
    @GET
    @Path("/metrics")
    @Produces(value = MediaType.APPLICATION_JSON)
    @AccessResource
    public Response getDocuments(@Parameter(description = "Start date in YYYY-MM-DD format.", required = true, example = "2024-03-01") @QueryParam("startDate") String start,
                                 @Parameter(description = "End date in YYYY-MM-DD format.", required = true, example = "2024-03-10") @QueryParam("endDate") String end){

        try {

            var sdf = new SimpleDateFormat("yyyy-MM-dd");
            var startDate = sdf.parse(start);
            var endDate = sdf.parse(end);

            // Validation: Start date should not be after the end date
            if (startDate.after(endDate)) {

                var error = new InformativeResponse();
                error.message = "Invalid date range. Start date must be before end date.";
                error.code = 400;

                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(error)
                        .build();
            }

            var response = new CountDocumentResponse();

            response.metricDefinitionCount = systemAdminService.countDocuments("MetricDefinition", startDate, endDate);
            response.metricCount = systemAdminService.countDocuments("Metric", startDate, endDate);

            return Response.ok(response).build();

        } catch (Exception e) {

            var error = new InformativeResponse();
            error.message = "Invalid date format. Use yyyy-MM-dd";
            error.code = 400;

            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(error)
                    .build();
        }
    }

    @Tag(name = "System Administrator")
    @Operation(
            summary = "Returns all Projects.",
            description = "Returns all Projects." )

    @APIResponse(
            responseCode = "200",
            description = "The corresponding Projects.",
            content = @Content(schema = @Schema(
                    type = SchemaType.OBJECT,
                    implementation = ProjectEndpoint.PageableProjectProjection.class)))
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
    @GET
    @Path("/projects")
    @Produces(value = MediaType.APPLICATION_JSON)
    @Consumes(value = MediaType.APPLICATION_JSON)
    @AccessResource
    public Response getAll(

            @Parameter(name = "page", in = QUERY,
                    description = "Indicates the page number. Page number must be >= 1.") @DefaultValue("1") @Min(value = 1, message = "Page number must be >= 1.") @QueryParam("page") int page,
            @Parameter(name = "size", in = QUERY,
                    description = "The page size.") @DefaultValue("10") @Min(value = 1, message = "Page size must be between 1 and 100.")
            @Max(value = 100, message = "Page size must be between 1 and 100.") @QueryParam("size") int size,
            @Context UriInfo uriInfo
    ) throws ParseException, NoSuchFieldException, org.json.simple.parser.ParseException, JsonProcessingException {

        var serverInfo = new AccountingUriInfo(serverUrl.concat(basePath).concat(uriInfo.getPath()));

        var results= projectService.getAllForSystemAdmin( page - 1, size, serverInfo);

        return Response.ok().entity(results).build();
    }

    @Tag(name = "System Administrator")
    @Operation(
            summary = "Create a new OIDC tenant configuration.",
            description = "Create a new OIDC tenant configuration. Accessible only by system administrators.")
    @APIResponse(
            responseCode = "201",
            description = "Tenant configuration created successfully.",
            content = @Content(schema = @Schema(
                    type = SchemaType.OBJECT,
                    implementation = OidcTenantConfigResponse.class)))
    @APIResponse(
            responseCode = "400",
            description = "Invalid tenant configuration.",
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
            description = "OIDC tenant configuration already exists.",
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
    @Path("/oidc-tenants")
    @Produces(value = MediaType.APPLICATION_JSON)
    @Consumes(value = MediaType.APPLICATION_JSON)
    @AccessResource
    public Response createOidcTenantConfig(@Valid @NotNull(message = "The request body is empty." ) OidcTenantConfigRequest request, @Context UriInfo uriInfo) {

        var serverInfo = new AccountingUriInfo(serverUrl.concat(basePath).concat(uriInfo.getPath()));

        var response = systemAdminService.createOidcTenantConfig(request);

        return Response.created(serverInfo.getAbsolutePathBuilder().path(response.id).build()).entity(response).build();
    }

    @Tag(name = "System Administrator")
    @Operation(
            summary = "Returns the available OIDC tenant configurations.",
            description = "This operation fetches the registered Accounting System OIDC tenant configurations. By default, the first page of 10 oidc tenant configurations will be returned. " +
                    "You can tune the default values by using the query parameters page and size.")
    @APIResponse(
            responseCode = "200",
            description = "Array of available OIDC tenant configurations.",
            content = @Content(schema = @Schema(
                    type = SchemaType.OBJECT,
                    implementation = PageableOidcTenantConfigResponseDto.class)))
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
    @Path("/oidc-tenants")
    @Produces(value = MediaType.APPLICATION_JSON)
    @AccessResource
    public Response getOidcTenantConfig(@Parameter(name = "page", in = QUERY,
            description = "Indicates the page number. Page number must be >= 1.") @DefaultValue("1") @Min(value = 1, message = "Page number must be >= 1.") @QueryParam("page") int page,
                               @Parameter(name = "size", in = QUERY,
                                       description = "The page size.") @DefaultValue("10") @Min(value = 1, message = "Page size must be between 1 and 100.")
                               @Max(value = 100, message = "Page size must be between 1 and 100.") @QueryParam("size") int size,
                               @Context UriInfo uriInfo){

        var serverInfo = new AccountingUriInfo(serverUrl.concat(basePath).concat(uriInfo.getPath()));

        return Response.ok().entity(systemAdminService.findAllOidcConfigPageable(page-1, size, serverInfo)).build();
    }

    @Tag(name = "System Administrator")
    @Operation(
            summary = "Returns an existing OIDC tenant configuration.",
            description = "This operation accepts the id of a OIDC tenant configuration and fetches from the database the corresponding record.")
    @APIResponse(
            responseCode = "200",
            description = "The corresponding OIDC tenant configuration.",
            content = @Content(schema = @Schema(
                    type = SchemaType.OBJECT,
                    implementation = OidcTenantConfigResponse.class)))
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
            description = "OIDC tenant configuration has not been found.",
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
    @Path("/oidc-tenants/{id}")
    @Produces(value = MediaType.APPLICATION_JSON)
    @AccessResource
    public Response get(
            @Parameter(
                    description = "The OIDC tenant configuration to be retrieved.",
                    required = true,
                    example = "507f1f77bcf86cd799439011",
                    schema = @Schema(type = SchemaType.STRING))
            @PathParam("id") @Valid @NotFoundEntity(repository = OidcTenantConfigRepository.class, message = "There is no OIDC tenant configuration with the following id:") String id) {

        var response = systemAdminService.fetchOidcTenantConfig(id);

        return Response.ok().entity(response).build();
    }

    @Tag(name = "System Administrator")
    @Operation(
            summary = "Delete an existing OIDC tenant configuration.",
            description = "Delete an existing OIDC tenant configuration.")
    @APIResponse(
            responseCode = "200",
            description = "OIDC tenant configuration deleted successfully.",
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
            responseCode = "500",
            description = "Internal Server Errors.",
            content = @Content(schema = @Schema(
                    type = SchemaType.OBJECT,
                    implementation = InformativeResponse.class)))
    @SecurityRequirement(name = "Authentication")
    @DELETE
    @Path("/oidc-tenants/{id}")
    @Produces(value = MediaType.APPLICATION_JSON)
    @AccessResource
    public Response deleteOidcTenantConfig(@Parameter(
            description = "The OIDC tenant configuration to be deleted.",
            required = true,
            example = "507f1f77bcf86cd799439011",
            schema = @Schema(type = SchemaType.STRING))
                                  @PathParam("id") @Valid
                                  @NotFoundEntity(repository = OidcTenantConfigRepository.class, message = "There is no OIDC tenant configuration with the following id:") String id) {

        systemAdminService.deleteOidcTenantConfig(id);

        var response = new InformativeResponse();
        response.code = 200;
        response.message = "OIDC tenant configuration deleted successfully.";

        return Response.ok().entity(response).build();
    }

    @Tag(name = "System Administrator")
    @Operation(
            summary = "Update an existing OIDC tenant configuration.",
            description = "Update an existing OIDC tenant configuration. " +
                    "You can update a part or all attributes of OIDC tenant configuration. The empty or null values are ignored. ")
    @APIResponse(
            responseCode = "200",
            description = "OIDC tenant configuration was updated successfully.",
            content = @Content(schema = @Schema(
                    type = SchemaType.OBJECT,
                    implementation = OidcTenantConfigResponse.class)))
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
            description = "OIDC tenant configuration has not been found.",
            content = @Content(schema = @Schema(
                    type = SchemaType.OBJECT,
                    implementation = InformativeResponse.class)))
    @APIResponse(
            responseCode = "409",
            description = "OIDC tenant configuration not allowed to be updated.",
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
    @Path("/oidc-tenants/{id}")
    @Produces(value = MediaType.APPLICATION_JSON)
    @Consumes(value = MediaType.APPLICATION_JSON)
    @AccessResource
    public Response updateOidcTenantConfig(
            @Parameter(
                    description = "The OIDC tenant configuration to be updated.",
                    required = true,
                    example = "507f1f77bcf86cd799439011",
                    schema = @Schema(type = SchemaType.STRING))
            @PathParam("id") @Valid @NotFoundEntity(repository = OidcTenantConfigRepository.class, message = "There is no OIDC tenant configuration with the following id:") String id,
            @Valid @NotNull(message = "The request body is empty.") UpdateOidcTenantConfig request) {

        var response = systemAdminService.updateOidcTenantConfig(request, id);

        return Response.ok().entity(response).build();
    }

    public static class PageableOidcTenantConfigResponseDto extends PageResource<OidcTenantConfigResponse> {

        private List<OidcTenantConfigResponse> content;

        @Override
        public List<OidcTenantConfigResponse> getContent() {
            return content;
        }

        @Override
        public void setContent(List<OidcTenantConfigResponse> content) {
            this.content = content;
        }
    }
}