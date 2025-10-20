package org.accounting.system.endpoints;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.quarkus.security.Authenticated;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import org.accounting.system.constraints.AccessProject;
import org.accounting.system.constraints.AccessProvider;
import org.accounting.system.constraints.CheckDateFormat;
import org.accounting.system.constraints.NotFoundEntity;
import org.accounting.system.dtos.InformativeResponse;
import org.accounting.system.dtos.installation.InstallationResponseDto;
import org.accounting.system.dtos.pagination.PageResource;
import org.accounting.system.entities.HierarchicalRelation;
import org.accounting.system.entities.projections.MetricProjection;
import org.accounting.system.entities.projections.ProjectReport;
import org.accounting.system.entities.projections.ProviderReport;
import org.accounting.system.entities.projections.normal.ProjectProjection;
import org.accounting.system.interceptors.annotations.AccessResource;
import org.accounting.system.repositories.metricdefinition.MetricDefinitionRepository;
import org.accounting.system.repositories.provider.ProviderRepository;
import org.accounting.system.services.MetricService;
import org.accounting.system.services.ProjectService;
import org.accounting.system.services.ProviderService;
import org.accounting.system.util.AccountingUriInfo;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.metrics.MetricUnits;
import org.eclipse.microprofile.metrics.annotation.Timed;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.enums.SecuritySchemeIn;
import org.eclipse.microprofile.openapi.annotations.enums.SecuritySchemeType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.ExampleObject;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.eclipse.microprofile.openapi.annotations.security.SecurityScheme;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.text.ParseException;
import java.time.LocalDate;
import java.util.List;

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
    ProviderService providerService;

    @Inject
    MetricService metricService;

    @ConfigProperty(name = "quarkus.rest.path")
    String basePath;

    @ConfigProperty(name = "api.server.url")
    String serverUrl;

    @Tag(name = "Metric")
    @Operation(
            summary = "Returns all Metrics under a specific Project.",
            description = "This operation is responsible for fetching all Metrics under a specific Project. " +
                    "By passing the Project ID, you can retrieve all Metrics that have been assigned to it. By default, the first page of 10 Metrics will be returned. " +
                    "You can tune the default values by using the query parameters page and size.")
    @APIResponse(
            responseCode = "200",
            description = "All Metrics.",
            content = @Content(schema = @Schema(
                    type = SchemaType.OBJECT,
                    implementation = PageableMetricProjection.class)))
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
    @Path("/{project_id}/metrics")
    @Timed(name = "checksMetricRetrievalOfProject", description = "A measure of how long it takes to retrieve Metrics under a Project.", unit = MetricUnits.MILLISECONDS)
    @Produces(value = MediaType.APPLICATION_JSON)
    public Response getAllMetricsUnderAProject(
            @Parameter(
                    description = "Τhe Project id with which you can request all the Metrics that have been assigned to it.",
                    required = true,
                    example = "704029",
                    schema = @Schema(type = SchemaType.STRING))
            @PathParam("project_id")
            @Valid
            @AccessProject(roles = {"admin", "viewer"}) String id,
            @Parameter(name = "page", in = QUERY,
                    description = "Indicates the page number. Page number must be >= 1.") @DefaultValue("1") @Min(value = 1, message = "Page number must be >= 1.") @QueryParam("page") int page,
            @Parameter(name = "size", in = QUERY,
                    description = "The page size.") @DefaultValue("10") @Min(value = 1, message = "Page size must be between 1 and 100.")
            @Max(value = 100, message = "Page size must be between 1 and 100.") @QueryParam("size") int size,
            @Parameter(name = "metric-definition-id", in = QUERY, schema = @Schema(type = SchemaType.STRING, defaultValue = ""),
                    description = "The Metric Definition that the Metrics are related to.")  @QueryParam("metric-definition-id") @NotFoundEntity(repository = MetricDefinitionRepository.class, message = "There is no Metric Definition with the following id:") String metricDefinitionId,
            @Parameter(name = "start", in = QUERY, schema = @Schema(type = SchemaType.STRING, defaultValue = ""),
                    description = "The inclusive start date for the query in the format YYYY-MM-DD. Cannot be after end.")  @QueryParam("start") String start,
            @Parameter(name = "end", in = QUERY, schema = @Schema(type = SchemaType.STRING, defaultValue = ""),
                    description = "The inclusive end date for the query in the format YYYY-MM-DD. Cannot be before start.") @QueryParam("end") String end,
            @Context UriInfo uriInfo) {

        var serverInfo = new AccountingUriInfo(serverUrl.concat(basePath).concat(uriInfo.getPath()));

        var response = metricService.fetchAllMetrics(id, page - 1, size, serverInfo, start, end, metricDefinitionId);

        return Response.ok().entity(response).build();
    }

    @Tag(name = "Metric")
    @Operation(
            summary = "Get all metrics under a specific project related to a specific group id.",
            description = "Retrieves a list of all metrics under the specified project and group id. By default, the first page of 10 Metrics will be returned. " +
                    "You can tune the default values by using the query parameters page and size.")
    @APIResponse(
            responseCode = "200",
            description = "A list of metrics related to the specified project and group id.",
            content = @Content(schema = @Schema(
                    type = SchemaType.OBJECT,
                    implementation = PageableMetricProjection.class)))
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
    @Path("/projects/{project_id}/groups/{group_id}/metrics")
    @Produces(value = MediaType.APPLICATION_JSON)
    public Response getAllMetricsUnderSpecificProjectRelatedToSpecificGroup(
            @Parameter(
                    description = "The ID of the project",
                    required = true,
                    example = "704029",
                    schema = @Schema(type = SchemaType.STRING))
            @PathParam("project_id")
            @Valid
            @AccessProject(roles = {"admin", "viewer"}) String id,
            @Parameter(
                    description = "The ID of the group.",
                    required = true,
                    example = "group-id",
                    schema = @Schema(type = SchemaType.STRING))
            @PathParam("group_id") String groupId,
            @Parameter(name = "page", in = QUERY,
                    description = "Indicates the page number. Page number must be >= 1.") @DefaultValue("1") @Min(value = 1, message = "Page number must be >= 1.") @QueryParam("page") int page,
            @Parameter(name = "size", in = QUERY,
                    description = "The page size.") @DefaultValue("10") @Min(value = 1, message = "Page size must be between 1 and 100.")
            @Max(value = 100, message = "Page size must be between 1 and 100.") @QueryParam("size") int size,
            @Parameter(name = "start", in = QUERY, schema = @Schema(type = SchemaType.STRING, defaultValue = ""),
                    description = "The inclusive start date for the query in the format YYYY-MM-DD. Cannot be after end.")  @QueryParam("start") String start,
            @Parameter(name = "end", in = QUERY, schema = @Schema(type = SchemaType.STRING, defaultValue = ""),
                    description = "The inclusive end date for the query in the format YYYY-MM-DD. Cannot be before start.") @QueryParam("end") String end,
            @Context UriInfo uriInfo) {

        var serverInfo = new AccountingUriInfo(serverUrl.concat(basePath).concat(uriInfo.getPath()));

        var response = metricService.getMetricsByProjectAndGroup(id, groupId,page - 1, size, start, end, serverInfo);

        return Response.ok().entity(response).build();
    }

    @Tag(name = "Metric")
    @Operation(
            summary = "Get all metrics under a specific project related to a specific user id.",
            description = "Retrieves a list of all metrics under the specified project and user id. By default, the first page of 10 Metrics will be returned. " +
                    "You can tune the default values by using the query parameters page and size.")
    @APIResponse(
            responseCode = "200",
            description = "A list of metrics related to the specified project and user id.",
            content = @Content(schema = @Schema(
                    type = SchemaType.OBJECT,
                    implementation = PageableMetricProjection.class)))
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
    @Path("/projects/{project_id}/users/{user_id}/metrics")
    @Produces(value = MediaType.APPLICATION_JSON)
    public Response getAllMetricsUnderSpecificProjectRelatedToSpecificUser(
            @Parameter(
                    description = "The ID of the project",
                    required = true,
                    example = "704029",
                    schema = @Schema(type = SchemaType.STRING))
            @PathParam("project_id")
            @Valid
            @AccessProject(roles = {"admin", "viewer"}) String id,
            @Parameter(
                    description = "The ID of the user.",
                    required = true,
                    example = "user-id",
                    schema = @Schema(type = SchemaType.STRING))
            @PathParam("user_id") String userId,
            @Parameter(name = "page", in = QUERY,
                    description = "Indicates the page number. Page number must be >= 1.") @DefaultValue("1") @Min(value = 1, message = "Page number must be >= 1.") @QueryParam("page") int page,
            @Parameter(name = "size", in = QUERY,
                    description = "The page size.") @DefaultValue("10") @Min(value = 1, message = "Page size must be between 1 and 100.")
            @Max(value = 100, message = "Page size must be between 1 and 100.") @QueryParam("size") int size,
            @Parameter(name = "start", in = QUERY, schema = @Schema(type = SchemaType.STRING, defaultValue = ""),
                    description = "The inclusive start date for the query in the format YYYY-MM-DD. Cannot be after end.")  @QueryParam("start") String start,
            @Parameter(name = "end", in = QUERY, schema = @Schema(type = SchemaType.STRING, defaultValue = ""),
                    description = "The inclusive end date for the query in the format YYYY-MM-DD. Cannot be before start.") @QueryParam("end") String end,
            @Context UriInfo uriInfo) {

        var serverInfo = new AccountingUriInfo(serverUrl.concat(basePath).concat(uriInfo.getPath()));

        var response = metricService.getMetricsByProjectAndUser(id, userId,page - 1, size, start, end, serverInfo);

        return Response.ok().entity(response).build();
    }

    @Tag(name = "Project")
    @Operation(
            summary = "Returns Project's Metric Definitions.",
            description = "This operation is responsible for returning Project's Metric Definitions. " +
                    "By default, the first page of 10 Metric Definitions will be returned. " +
                    "You can tune the default values by using the query parameters page and size.")
    @APIResponse(
            responseCode = "200",
            description = "All Project's Metric Definitions.",
            content = @Content(schema = @Schema(
                    type = SchemaType.OBJECT,
                    implementation = MetricDefinitionEndpoint.PageableMetricDefinitionResponseDto.class)))
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

    @GET
    @Path("/{project_id}/metric-definitions")
    @Produces(value = MediaType.APPLICATION_JSON)
    public Response getAllProjectMetricDefinitions(
            @Parameter(
                    description = "Τhe Project id.",
                    required = true,
                    example = "704029",
                    schema = @Schema(type = SchemaType.STRING))
            @PathParam("project_id")
            @Valid
            @AccessProject(roles = {"admin", "viewer"}) String id,
            @Parameter(name = "page", in = QUERY,
                    description = "Indicates the page number. Page number must be >= 1.") @DefaultValue("1") @Min(value = 1, message = "Page number must be >= 1.") @QueryParam("page") int page,
            @Parameter(name = "size", in = QUERY,
                    description = "The page size.") @DefaultValue("10") @Min(value = 1, message = "Page size must be between 1 and 100.")
            @Max(value = 100, message = "Page size must be between 1 and 100.") @QueryParam("size") int size, @Context UriInfo uriInfo) {

        var serverInfo = new AccountingUriInfo(serverUrl.concat(basePath).concat(uriInfo.getPath()));

        var response = projectService.fetchAllMetricDefinitions(id, page - 1, size, serverInfo);

        return Response.ok().entity(response).build();
    }

    @Tag(name = "Metric")
    @Operation(
            summary = "Returns all Metrics under a specific Provider.",
            description = "This operation is responsible for fetching all Metrics under a specific Provider. " +
                    "By passing the Project ID to which the Provider belongs as well as the Provider ID, you can retrieve all Metrics that have been assigned to this specific Provider. By default, the first page of 10 Metrics will be returned. " +
                    "You can tune the default values by using the query parameters page and size.")
    @APIResponse(
            responseCode = "200",
            description = "All Metrics.",
            content = @Content(schema = @Schema(
                    type = SchemaType.OBJECT,
                    implementation = PageableMetricProjection.class)))
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
    @Path("/{project_id}/providers/{provider_id}/metrics")
    @Timed(name = "checksMetricRetrievalOfProvider", description = "A measure of how long it takes to retrieve Metrics under a Provider.", unit = MetricUnits.MILLISECONDS)
    @Produces(value = MediaType.APPLICATION_JSON)
    @AccessProvider(roles = {"admin", "viewer"})
    public Response getAllMetricsUnderAProvider(
            @Parameter(
                    description = "Τhe Project id to which the Provider belongs.",
                    required = true,
                    example = "704029",
                    schema = @Schema(type = SchemaType.STRING))
            @PathParam("project_id") String projectId,
            @Parameter(
                    description = "Τhe Provider id with which you can request all the Metrics that have been assigned to it.",
                    required = true,
                    example = "grnet",
                    schema = @Schema(type = SchemaType.STRING))
            @PathParam("provider_id") String providerId,
            @Parameter(name = "page", in = QUERY,
                    description = "Indicates the page number. Page number must be >= 1.") @DefaultValue("1") @Min(value = 1, message = "Page number must be >= 1.") @QueryParam("page") int page,
            @Parameter(name = "size", in = QUERY,
                    description = "The page size.") @DefaultValue("10") @Min(value = 1, message = "Page size must be between 1 and 100.")
            @Max(value = 100, message = "Page size must be between 1 and 100.") @QueryParam("size") int size,
            @Parameter(name = "metric-definition-id", in = QUERY, schema = @Schema(type = SchemaType.STRING, defaultValue = ""),
                    description = "The Metric Definition that the Metrics are related to.")  @QueryParam("metric-definition-id") @NotFoundEntity(repository = MetricDefinitionRepository.class, message = "There is no Metric Definition with the following id:") String metricDefinitionId,
            @Parameter(name = "start", in = QUERY, schema = @Schema(type = SchemaType.STRING, defaultValue = ""),
                    description = "The inclusive start date for the query in the format YYYY-MM-DD. Cannot be after end.")  @QueryParam("start") String start,
            @Parameter(name = "end", in = QUERY, schema = @Schema(type = SchemaType.STRING, defaultValue = ""),
                    description = "The inclusive end date for the query in the format YYYY-MM-DD. Cannot be before start.") @QueryParam("end") String end,
            @Context UriInfo uriInfo) {

        var serverInfo = new AccountingUriInfo(serverUrl.concat(basePath).concat(uriInfo.getPath()));

        var response = metricService.fetchAllMetrics(projectId + HierarchicalRelation.PATH_SEPARATOR + providerId, page - 1, size, serverInfo, start, end, metricDefinitionId);

        return Response.ok().entity(response).build();
    }

    @Tag(name = "Provider")
    @Operation(
            summary = "Get Provider report with metrics.",
            description = "Returns a report for a specific Provider and time period, including aggregated metric values.")
    @APIResponse(
            responseCode = "200",
            description = "Provider report retrieved successfully.",
            content = @Content(schema = @Schema(
                    type = SchemaType.OBJECT,
                    implementation = ProviderReport.class)))
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
    @Path("/{project_id}/providers/{provider_id}/report")
    @Produces(value = MediaType.APPLICATION_JSON)
    @AccessProvider(roles = {"admin", "viewer"})
    public Response providerReport(
            @Parameter(
                    description = "Τhe Project id.",
                    required = true,
                    example = "704029",
                    schema = @Schema(type = SchemaType.STRING))
            @PathParam("project_id") String projectId,
            @Parameter(
                    description = "Τhe Provider id.",
                    required = true,
                    example = "grnet",
                    schema = @Schema(type = SchemaType.STRING))
            @PathParam("provider_id") String providerId,
            @Parameter(
                    name = "start",
                    description = "Start date in yyyy-MM-dd format",
                    required = true,
                    example = "2024-01-01"
            )
            @QueryParam("start")
            @Valid
            @NotEmpty(message = "start may not be empty.")
            @CheckDateFormat(pattern = "yyyy-MM-dd", message = "Valid date format is yyyy-MM-dd.") String start,

            @Parameter(
                    name = "end",
                    description = "End date in yyyy-MM-dd format",
                    required = true,
                    example = "2024-12-31"
            )
            @QueryParam("end")
            @Valid
            @NotEmpty(message = "end may not be empty.")
            @CheckDateFormat(pattern = "yyyy-MM-dd", message = "Valid date format is yyyy-MM-dd.") String end) {

        if (LocalDate.parse(start).isAfter(LocalDate.parse(end))) {
            throw new BadRequestException("Start date must be before or equal to end date.");
        }

        var response = providerService.providerReport(projectId, providerId, start, end);

        return Response.ok().entity(response).build();
    }

    @Tag(name = "Provider")
    @Operation(
            summary = "Get Provider report with metrics.",
            description = "Returns a report for a specific Provider and time period, including aggregated metric values.")
    @APIResponse(
            responseCode = "200",
            description = "Provider report retrieved successfully.",
            content = @Content(schema = @Schema(
                    type = SchemaType.OBJECT,
                    implementation = ProviderReport.class)))
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
    @Path("/{project_id}/providers/external/report")
    @Produces(value = MediaType.APPLICATION_JSON)
    public Response providerReportByExternalId(
            @Parameter(
                    description = "Τhe Project id.",
                    required = true,
                    example = "704029",
                    schema = @Schema(type = SchemaType.STRING))
            @PathParam("project_id") String projectId,
            @Parameter(name = "externalProviderId", in = QUERY, required = true, example = "sites", allowReserved = true,
                    description = "The external provider id.", schema = @Schema(type = SchemaType.STRING)) @QueryParam("externalProviderId") @NotEmpty(message = "externalProviderId may not be empty.") String externalProviderId,
            @Parameter(
                    name = "start",
                    description = "Start date in yyyy-MM-dd format",
                    required = true,
                    example = "2024-01-01"
            )
            @QueryParam("start")
            @Valid
            @NotEmpty(message = "start may not be empty.")
            @CheckDateFormat(pattern = "yyyy-MM-dd", message = "Valid date format is yyyy-MM-dd.") String start,
            @Parameter(
                    name = "end",
                    description = "End date in yyyy-MM-dd format",
                    required = true,
                    example = "2024-12-31"
            )
            @QueryParam("end")
            @Valid
            @NotEmpty(message = "end may not be empty.")
            @CheckDateFormat(pattern = "yyyy-MM-dd", message = "Valid date format is yyyy-MM-dd.") String end) {

        if (LocalDate.parse(start).isAfter(LocalDate.parse(end))) {
            throw new BadRequestException("Start date must be before or equal to end date.");
        }

        var response = providerService.providerReportByExternalId(projectId, externalProviderId, start, end);

        return Response.ok().entity(response).build();
    }

    @Tag(name = "Metric")
    @Operation(
            summary = "Returns all Metrics under a specific Provider by external Provider ID.",
            description = "This operation is responsible for fetching all Metrics under a specific Provider. " +
                    "By passing the Project ID to which the Provider belongs as well as the Provider external ID, you can retrieve all Metrics that have been assigned to this specific Provider. By default, the first page of 10 Metrics will be returned. " +
                    "You can tune the default values by using the query parameters page and size.")
    @APIResponse(
            responseCode = "200",
            description = "All Metrics.",
            content = @Content(schema = @Schema(
                    type = SchemaType.OBJECT,
                    implementation = PageableMetricProjection.class)))
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
    @Path("/{project_id}/providers/external/metrics")
    @Produces(value = MediaType.APPLICATION_JSON)
    public Response getAllMetricsUnderAProviderByExternalId(
            @Parameter(
                    description = "Τhe Project id to which the Provider belongs.",
                    required = true,
                    example = "704029",
                    schema = @Schema(type = SchemaType.STRING))
            @PathParam("project_id") String projectId,
            @Parameter(name = "externalProviderId", in = QUERY, required = true, example = "sites", allowReserved = true,
                    description = "The external provider id.", schema = @Schema(type = SchemaType.STRING)) @QueryParam("externalProviderId") @NotEmpty(message = "externalProviderId may not be empty.") String externalProviderId,
            @Parameter(name = "page", in = QUERY,
                    description = "Indicates the page number. Page number must be >= 1.") @DefaultValue("1") @Min(value = 1, message = "Page number must be >= 1.") @QueryParam("page") int page,
            @Parameter(name = "size", in = QUERY,
                    description = "The page size.") @DefaultValue("10") @Min(value = 1, message = "Page size must be between 1 and 100.")
            @Max(value = 100, message = "Page size must be between 1 and 100.") @QueryParam("size") int size,
            @Parameter(name = "metric-definition-id", in = QUERY, schema = @Schema(type = SchemaType.STRING, defaultValue = ""),
                    description = "The Metric Definition that the Metrics are related to.")  @QueryParam("metric-definition-id") @NotFoundEntity(repository = MetricDefinitionRepository.class, message = "There is no Metric Definition with the following id:") String metricDefinitionId,
            @Parameter(name = "start", in = QUERY, schema = @Schema(type = SchemaType.STRING, defaultValue = ""),
                    description = "The inclusive start date for the query in the format YYYY-MM-DD. Cannot be after end.")  @QueryParam("start") String start,
            @Parameter(name = "end", in = QUERY, schema = @Schema(type = SchemaType.STRING, defaultValue = ""),
                    description = "The inclusive end date for the query in the format YYYY-MM-DD. Cannot be before start.") @QueryParam("end") String end,
            @Context UriInfo uriInfo) {

        var serverInfo = new AccountingUriInfo(serverUrl.concat(basePath).concat(uriInfo.getPath()));

        var response = providerService.fetchAllMetricsByExternalProviderId(projectId, externalProviderId, page - 1, size, serverInfo, start, end, metricDefinitionId);

        return Response.ok().entity(response).build();
    }

    @Tag(name = "Provider")
    @Operation(
            summary = "Returns Provider's Metric Definitions.",
            description = "This operation is responsible for returning Provider's Metric Definitions. " +
                    "By default, the first page of 10 Metric Definitions will be returned. " +
                    "You can tune the default values by using the query parameters page and size.")
    @APIResponse(
            responseCode = "200",
            description = "All Provider's Metric Definitions.",
            content = @Content(schema = @Schema(
                    type = SchemaType.OBJECT,
                    implementation = MetricDefinitionEndpoint.PageableMetricDefinitionResponseDto.class)))
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
    @GET
    @Path("/{project_id}/providers/{provider_id}/metric-definitions")
    @Produces(value = MediaType.APPLICATION_JSON)
    @AccessProvider(roles = {"admin", "viewer"})
    public Response getAllProviderMetricDefinitions(
            @Parameter(
                    description = "Τhe Project id to which the Provider belongs.",
                    required = true,
                    example = "704029",
                    schema = @Schema(type = SchemaType.STRING))
            @PathParam("project_id") String projectId,
            @Parameter(
                    description = "Τhe Provider id with which you can request all the Metrics that have been assigned to it.",
                    required = true,
                    example = "grnet",
                    schema = @Schema(type = SchemaType.STRING))
            @PathParam("provider_id") String providerId,
            @Parameter(name = "page", in = QUERY,
                    description = "Indicates the page number. Page number must be >= 1.") @DefaultValue("1") @Min(value = 1, message = "Page number must be >= 1.") @QueryParam("page") int page,
            @Parameter(name = "size", in = QUERY,
                    description = "The page size.") @DefaultValue("10") @Min(value = 1, message = "Page size must be between 1 and 100.")
            @Max(value = 100, message = "Page size must be between 1 and 100.") @QueryParam("size") int size, @Context UriInfo uriInfo) {

        var serverInfo = new AccountingUriInfo(serverUrl.concat(basePath).concat(uriInfo.getPath()));

        var response = providerService.fetchAllMetricDefinitions(projectId, providerId, page - 1, size, serverInfo);

        return Response.ok().entity(response).build();
    }

    @Tag(name = "Project")
    @Operation(
            summary = "Associate a Project with a Provider.",
            description = "There is a hierarchical relation between Project and Providers which can be expressed as follows: a Project can have a number of different Providers. " +
                    "You can associate a Provider with a specific Project. Finally, it should be noted that any Provider can belong to more than one Project.")
    @APIResponse(
            responseCode = "200",
            description = "Provider has been successfully associated with the Project.",
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
    @Path("/{id}/associate/provider/{provider_id}")
    @Produces(value = MediaType.APPLICATION_JSON)
    @Consumes(value = MediaType.APPLICATION_JSON)
    public Response associateProjectWithProviders(
            @Parameter(
                    description = "The Project in which the Provider will be associated with.",
                    required = true,
                    example = "888743",
                    schema = @Schema(type = SchemaType.STRING))
            @PathParam("id")
            @Valid
            @AccessProject(roles = {"admin"}) String id,
            @Parameter(
            description = "The Provider to be associated.",
            required = true,
            example = "sites",
            schema = @Schema(type = SchemaType.STRING)) @PathParam("provider_id") @Valid @NotFoundEntity(repository = ProviderRepository.class, id = String.class, message = "There is no Provider with the following id:") String providerId) {

        projectService.associateProjectWithProvider(id, providerId);

        InformativeResponse response = new InformativeResponse();
        response.code = 200;
        response.message = "The following provider " + providerId + " has been associated with Project " + id;

        return Response.ok().entity(response).build();
    }

    @Tag(name = "Project")
    @Operation(
            summary = "Dissociate a Provider from a Project.",
            description = "There is a hierarchical relation between Project and Providers which can be expressed as follows: a Project can have a number of different Providers. " +
                    "You can dissociate a Provider from a specific Project.")
    @APIResponse(
            responseCode = "200",
            description = "Provider has been successfully dissociated from the Project.",
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
    //TODO We have to generate a PATCH method as well
    @POST
    @Path("/{id}/dissociate/provider/{provider_id}")
    @Produces(value = MediaType.APPLICATION_JSON)
    @Consumes(value = MediaType.APPLICATION_JSON)
    public Response dissociateProvidersFromProject(
            @Parameter(
                    description = "The Project from which the Providers will be dissociated.",
                    required = true,
                    example = "447535",
                    schema = @Schema(type = SchemaType.STRING))
            @PathParam("id") @Valid
            @AccessProject(roles= {"admin"}) String id,
            @Parameter(
                    description = "The Provider to be associated.",
                    required = true,
                    example = "sites",
                    schema = @Schema(type = SchemaType.STRING)) @PathParam("provider_id") @Valid @NotFoundEntity(repository = ProviderRepository.class, id = String.class, message = "There is no Provider with the following id:") String providerId) {

        projectService.dissociateProviderFromProject(id, providerId);

        InformativeResponse response = new InformativeResponse();
        response.code = 200;
        response.message = "The following provider " + providerId + " has been dissociated from Project " + id;

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
                    implementation = ProjectProjection.class)))
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
    @Path("/{id}")
    @Produces(value = MediaType.APPLICATION_JSON)
    @SecurityRequirement(name = "Authentication")
    public Response getProjectHierarchicalStructure(
            @Parameter(
                    description = "The Project ID.",
                    required = true,
                    example = "447535",
                    schema = @Schema(type = SchemaType.STRING))
            @Valid
            @AccessProject(roles = {"admin", "viewer"})
            @PathParam("id") String id) {

        var response = projectService.getById(id);

        return Response.ok().entity(response).build();
    }

    @Tag(name = "Project")
    @Operation(
            summary = "Returns all Project Installations.",
            description = "Essentially, this operation returns all Project Installations which have been added to Accounting System. By default, the first page of 10 Installations will be returned. " +
                    "You can tune the default values by using the query parameters page and size.")
    @APIResponse(
            responseCode = "200",
            description = "Array of Installations.",
            content = @Content(schema = @Schema(
                    type = SchemaType.OBJECT,
                    implementation = PageableInstallationResponseDto.class)))
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
    @Path("/{project_id}/installations")
    @Produces(value = MediaType.APPLICATION_JSON)
    public Response getAllProjectInstallations(
            @Parameter(
                    description = "The Project that the Installations belongs to.",
                    required = true,
                    example = "704567",
                    schema = @Schema(type = SchemaType.STRING))
            @Valid
            @AccessProject(roles = {"admin", "viewer"})
            @PathParam("project_id") String projectId,
            @Parameter(name = "page", in = QUERY,
                    description = "Indicates the page number. Page number must be >= 1.") @DefaultValue("1") @Min(value = 1, message = "Page number must be >= 1.") @QueryParam("page") int page,
            @Parameter(name = "size", in = QUERY,
                    description = "The page size.") @DefaultValue("10") @Min(value = 1, message = "Page size must be between 1 and 100.")
            @Max(value = 100, message = "Page size must be between 1 and 100.") @QueryParam("size") int size,
            @Context UriInfo uriInfo) {

        var serverInfo = new AccountingUriInfo(serverUrl.concat(basePath).concat(uriInfo.getPath()));

        return Response.ok().entity(projectService.getInstallationsByProject(projectId, page - 1, size, serverInfo)).build();
    }

    @Tag(name = "Provider")
    @Operation(
            summary = "Returns all Provider Installations.",
            description = "Essentially, this operation returns all Provider Installations which have been added to Accounting System. By default, the first page of 10 Installations will be returned. " +
                    "You can tune the default values by using the query parameters page and size.")
    @APIResponse(
            responseCode = "200",
            description = "Array of Installations.",
            content = @Content(schema = @Schema(
                    type = SchemaType.OBJECT,
                    implementation = PageableInstallationResponseDto.class)))
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
    @Path("/{project_id}/providers/{provider_id}/installations")
    @Produces(value = MediaType.APPLICATION_JSON)
    @AccessProvider(roles = {"admin", "viewer"})
    public Response getAllProviderInstallations(
            @Parameter(
                    description = "The Project ID that the Provider belongs to.",
                    required = true,
                    example = "704567",
                    schema = @Schema(type = SchemaType.STRING))
            @PathParam("project_id") String projectId,
            @Parameter(
                    description = "The Provider that the installations belongs to.",
                    required = true,
                    example = "sites",
                    schema = @Schema(type = SchemaType.STRING))
            @PathParam("provider_id") String providerId,
            @Parameter(name = "page", in = QUERY,
                    description = "Indicates the page number. Page number must be >= 1.") @DefaultValue("1") @Min(value = 1, message = "Page number must be >= 1.") @QueryParam("page") int page,
            @Parameter(name = "size", in = QUERY,
                    description = "The page size.") @DefaultValue("10") @Min(value = 1, message = "Page size must be between 1 and 100.")
            @Max(value = 100, message = "Page size must be between 1 and 100.") @QueryParam("size") int size,
            @Context UriInfo uriInfo) {

        var serverInfo = new AccountingUriInfo(serverUrl.concat(basePath).concat(uriInfo.getPath()));

        return Response.ok().entity(providerService.findInstallationsByProvider(projectId, providerId, page - 1, size, serverInfo)).build();
    }

    @Tag(name = "Project")
    @Operation(
            operationId = "search-project",
            summary = "Searches a project",
            description = "Search Project" )

    @APIResponse(
            responseCode = "200",
            description = "The corresponding Projects.",
            content = @Content(schema = @Schema(
                    type = SchemaType.OBJECT,
                    implementation = PageableProjectProjection.class)))
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
    @Path("/search")
    @Produces(value = MediaType.APPLICATION_JSON)
    @Consumes(value = MediaType.APPLICATION_JSON)
    @AccessResource
    public Response search(
            @NotEmpty(message = "The request body is empty.") @RequestBody(content = @Content(
                    schema = @Schema(implementation = String.class),
                    mediaType = MediaType.APPLICATION_JSON,
                    examples = {
                            @ExampleObject(
                                    name = "A simple example search request on projects",
                                    value = "{\n" +
                                            "            \"type\":\"query\",\n" +
                                            "            \"field\": \"acronym\",\n" +
                                            "            \"values\": \"Test1\",\n" +
                                            "            \"operand\": \"eq\"\n" +
                                            "\n" +
                                            "}",
                                    summary = "A simple search on Projects "),
                            @ExampleObject(
                                    name = "A complex example search request on projects",
                                    value = "{\n" +
                                            "  \"type\": \"filter\",\n" +
                                            "  \"operator\": \"OR\",\n" +
                                            "  \"criteria\": [{\n" +
                                            "            \"type\":\"query\",\n" +
                                            "            \"field\": \"title\",\n" +
                                            "            \"values\": \"Test2\",\n" +
                                            "            \"operand\": \"eq\"\n" +
                                            "\n" +
                                            "},{\n" +
                                            "            \"type\":\"query\",\n" +
                                            "            \"field\": \"acronym\",\n" +
                                            "            \"values\": \"Test Project\",\n" +
                                            "            \"operand\": \"eq\"\n" +
                                            "\n" +
                                            "}]}",
                                    summary = "A complex search on Projects ")})
            )  String json,
            @Parameter(name = "page", in = QUERY,
                    description = "Indicates the page number. Page number must be >= 1.") @DefaultValue("1")  @Min(value = 1, message = "Page number must be >= 1.") @QueryParam("page") int page,
            @Parameter(name = "size", in = QUERY,
                    description = "The page size.") @DefaultValue("10") @Min(value = 1, message = "Page size must be between 1 and 100.")
            @Max(value = 100, message = "Page size must be between 1 and 100.") @QueryParam("size") int size,
            @Context UriInfo uriInfo
    ) throws ParseException, NoSuchFieldException, org.json.simple.parser.ParseException, JsonProcessingException {

        var serverInfo = new AccountingUriInfo(serverUrl.concat(basePath).concat(uriInfo.getPath()));

        if(json.equals("")){
            throw  new BadRequestException("not empty body permitted");
        }
        var results=projectService.searchProject(json, page - 1, size, serverInfo);

        return Response.ok().entity(results).build();
    }

//    @Tag(name = "Project")
//    @Operation(
//            operationId = "get-all-projects",
//            summary = "Returns all Projects to which a client has access.",
//            description = "Returns all Projects to which a client has access." )
//
//    @APIResponse(
//            responseCode = "200",
//            description = "The corresponding Projects.",
//            content = @Content(schema = @Schema(
//                    type = SchemaType.OBJECT,
//                    implementation = PageableProjectProjection.class)))
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
//    @GET
//    @Produces(value = MediaType.APPLICATION_JSON)
//    @Consumes(value = MediaType.APPLICATION_JSON)
//    public Response getAll(
//
//            @Parameter(name = "page", in = QUERY,
//                    description = "Indicates the page number. Page number must be >= 1.") @DefaultValue("1") @Min(value = 1, message = "Page number must be >= 1.") @QueryParam("page") int page,
//            @Parameter(name = "size", in = QUERY,
//                    description = "The page size.") @DefaultValue("10") @Min(value = 1, message = "Page size must be between 1 and 100.")
//            @Max(value = 100, message = "Page size must be between 1 and 100.") @QueryParam("size") int size,
//            @Context UriInfo uriInfo
//    ) throws ParseException, NoSuchFieldException, org.json.simple.parser.ParseException, JsonProcessingException {
//
//        var serverInfo = new AccountingUriInfo(serverUrl.concat(basePath).concat(uriInfo.getPath()));
//
//        var results= projectService.getAll( page - 1, size, serverInfo);
//
//        return Response.ok().entity(results).build();
//    }

    @Tag(name = "Project")
    @Operation(
            summary = "Get Project report with metrics.",
            description = "Returns a report for a specific Project and time period, including aggregated metric values.")
    @APIResponse(
            responseCode = "200",
            description = "Project report retrieved successfully.",
            content = @Content(schema = @Schema(
                    type = SchemaType.OBJECT,
                    implementation = ProjectReport.class)))
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
    @Path("/{project_id}/report")
    @Produces(value = MediaType.APPLICATION_JSON)
    public Response projectReport(
            @Parameter(
                    description = "Τhe Project id.",
                    required = true,
                    example = "704029",
                    schema = @Schema(type = SchemaType.STRING))
            @PathParam("project_id")
            @Valid
            @AccessProject(roles = {"admin", "viewer"}) String projectId,
            @Parameter(
                    name = "start",
                    description = "Start date in yyyy-MM-dd format",
                    required = true,
                    example = "2024-01-01"
            )
            @QueryParam("start")
            @Valid
            @NotEmpty(message = "start may not be empty.")
            @CheckDateFormat(pattern = "yyyy-MM-dd", message = "Valid date format is yyyy-MM-dd.") String start,

            @Parameter(
                    name = "end",
                    description = "End date in yyyy-MM-dd format",
                    required = true,
                    example = "2024-12-31"
            )
            @QueryParam("end")
            @Valid
            @NotEmpty(message = "end may not be empty.")
            @CheckDateFormat(pattern = "yyyy-MM-dd", message = "Valid date format is yyyy-MM-dd.") String end) {

        if (LocalDate.parse(start).isAfter(LocalDate.parse(end))) {
            throw new BadRequestException("Start date must be before or equal to end date.");
        }

        var response = projectService.projectReport(projectId, start, end);

        return Response.ok().entity(response).build();
    }

    public static class PageableProjectProjection extends PageResource<ProjectProjection> {

        private List<ProjectProjection> content;

        @Override
        public List<ProjectProjection> getContent() {
            return content;
        }

        @Override
        public void setContent(List<ProjectProjection> content) {
            this.content = content;
        }
    }

    public static class PageableMetricProjection extends PageResource<MetricProjection> {

        private List<MetricProjection> content;

        @Override
        public List<MetricProjection> getContent() {
            return content;
        }

        @Override
        public void setContent(List<MetricProjection> content) {
            this.content = content;
        }
    }

    public static class PageableInstallationResponseDto extends PageResource<InstallationResponseDto> {

        private List<InstallationResponseDto> content;

        @Override
        public List<InstallationResponseDto> getContent() {
            return content;
        }

        @Override
        public void setContent(List<InstallationResponseDto> content) {
            this.content = content;
        }
    }
}