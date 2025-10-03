package org.accounting.system.endpoints;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.quarkus.security.Authenticated;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
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
import org.accounting.system.constraints.AccessInstallation;
import org.accounting.system.constraints.AccessProvider;
import org.accounting.system.constraints.CheckDateFormat;
import org.accounting.system.constraints.NotFoundEntity;
import org.accounting.system.dtos.InformativeResponse;
import org.accounting.system.dtos.installation.InstallationRequestDto;
import org.accounting.system.dtos.installation.InstallationResponseDto;
import org.accounting.system.dtos.installation.UpdateInstallationRequestDto;
import org.accounting.system.dtos.metric.MetricRequestDto;
import org.accounting.system.dtos.metric.MetricResponseDto;
import org.accounting.system.dtos.metric.UpdateMetricRequestDto;
import org.accounting.system.dtos.pagination.PageResource;
import org.accounting.system.entities.HierarchicalRelation;
import org.accounting.system.entities.projections.InstallationReport;
import org.accounting.system.entities.projections.MetricProjection;
import org.accounting.system.interceptors.annotations.AccessResource;
import org.accounting.system.repositories.metric.MetricRepository;
import org.accounting.system.repositories.metricdefinition.MetricDefinitionRepository;
import org.accounting.system.services.HierarchicalRelationService;
import org.accounting.system.services.MetricService;
import org.accounting.system.services.installation.InstallationService;
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

@Path("/installations")
@Authenticated
@SecurityScheme(securitySchemeName = "Authentication",
        description = "JWT token",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER)
public class InstallationEndpoint {

    @ConfigProperty(name = "quarkus.resteasy-reactive.path")
    String basePath;

    @ConfigProperty(name = "api.server.url")
    String serverUrl;

    @Inject
    InstallationService installationService;

    @Inject
    HierarchicalRelationService hierarchicalRelationService;

    @Inject
    MetricService metricService;

    @Inject
    RequestUserContext requestUserContext;

    @Tag(name = "Installation")
    @Operation(
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
    @APIResponse(
            responseCode = "501",
            description = "Not Supported.",
            content = @Content(schema = @Schema(
                    type = SchemaType.OBJECT,
                    implementation = InformativeResponse.class)))
    @SecurityRequirement(name = "Authentication")
    @POST
    @Produces(value = MediaType.APPLICATION_JSON)
    @Consumes(value = MediaType.APPLICATION_JSON)
    @AccessProvider(roles = {"admin"})
    public Response save(InstallationRequestDto installationRequestDto, @Context UriInfo uriInfo) {

        var serverInfo = new AccountingUriInfo(serverUrl.concat(basePath).concat(uriInfo.getPath()));

        var belongs = hierarchicalRelationService.providerBelongsToProject(installationRequestDto.project, installationRequestDto.organisation);

        if (!belongs) {
            String message = String.format("There is no relationship between Project {%s} and Provider {%s}", installationRequestDto.project, installationRequestDto.organisation);
            throw new BadRequestException(message);
        }

        var response = installationService.save(installationRequestDto, requestUserContext.getId());

        return Response.created(serverInfo.getAbsolutePathBuilder().path(response.id).build()).entity(response).build();
    }

    @Tag(name = "Installation")
    @Operation(
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
    public Response delete(@Parameter(
            description = "The Installation to be deleted.",
            required = true,
            example = "507f1f77bcf86cd799439011",
            schema = @Schema(type = SchemaType.STRING))
                           @PathParam("id")
                               @Valid
                               @AccessInstallation(roles = {"admin"}) String id) {

        installationService.delete(id);

        var successResponse = new InformativeResponse();

        successResponse.code = 200;
        successResponse.message = "Installation has been deleted successfully.";

        return Response.ok().entity(successResponse).build();
    }

    @Tag(name = "Installation")
    @Operation(
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
    public Response get(
            @Parameter(
                    description = "The Installation to be retrieved.",
                    required = true,
                    example = "507f1f77bcf86cd799439011",
                    schema = @Schema(type = SchemaType.STRING))
            @PathParam("id") @Valid
            @AccessInstallation(roles = {"admin", "viewer"}) String id) {

        var response = installationService.installationToResponse(id);

        return Response.ok().entity(response).build();
    }

    @Tag(name = "Installation")
    @Operation(
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
    @APIResponse(
            responseCode = "501",
            description = "Not Supported.",
            content = @Content(schema = @Schema(
                    type = SchemaType.OBJECT,
                    implementation = InformativeResponse.class)))
    @SecurityRequirement(name = "Authentication")

    @PATCH
    @Path("/{id}")
    @Produces(value = MediaType.APPLICATION_JSON)
    @Consumes(value = MediaType.APPLICATION_JSON)
    public Response update(
            @Parameter(
                    description = "The Installation to be updated.",
                    required = true,
                    example = "507f1f77bcf86cd799439011",
                    schema = @Schema(type = SchemaType.STRING))
            @PathParam("id") @Valid
            @AccessInstallation(roles = {"admin"}) String id, @Valid @NotNull(message = "The request body is empty.") UpdateInstallationRequestDto updateInstallationRequestDto) {

        var response = installationService.update(id, updateInstallationRequestDto);

        return Response.ok().entity(response).build();
    }

    @Tag(name = "Metric")
    @Operation(
            summary = "Assigns a new Metric to a specific Installation.",
            description = "Fundamentally, this operation creates a new Metric and assigns it to a specific Installation. " +
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
    @Path("/{installation_id}/metrics")
    @Timed(name = "checksMetricInsertion", description = "A measure of how long it takes to insert a new Metric.", unit = MetricUnits.MILLISECONDS)
    @Produces(value = MediaType.APPLICATION_JSON)
    @Consumes(value = MediaType.APPLICATION_JSON)
    public Response save(
            @Parameter(
                    description = "The Installation to which Metric will be assigned.",
                    required = true,
                    example = "507f1f77bcf86cd799439011",
                    schema = @Schema(type = SchemaType.STRING))
            @PathParam("installation_id")
            @Valid
            @AccessInstallation(roles = {"admin"}) String installationId,
            @Valid @NotNull(message = "The request body is empty.") MetricRequestDto metricRequestDto, @Context UriInfo uriInfo) {

        var serverInfo = new AccountingUriInfo(serverUrl.concat(basePath).concat(uriInfo.getPath()));

        var response = installationService.assignMetric(installationId, metricRequestDto);

        return Response.created(serverInfo.getAbsolutePathBuilder().path(response.id).build()).entity(response).build();
    }

    @Tag(name = "Metric")
    @Operation(
            summary = "Deletes an existing Metric.",
            description = "Deletes an existing Metric.")
    @APIResponse(
            responseCode = "200",
            description = "Metric has been deleted successfully.",
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
            description = "Metric has not been found.",
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
    @Path("/{installation_id}/metrics/{id}")
    @Produces(value = MediaType.APPLICATION_JSON)
    public Response delete(
            @Parameter(
                    description = "The Installation to which the Metric belongs.",
                    required = true,
                    example = "507f1f77bcf86cd799439011",
                    schema = @Schema(type = SchemaType.STRING))
            @PathParam("installation_id")
            @Valid
            @AccessInstallation(roles = {"admin"}) String installationId,
            @Parameter(
                    description = "The Metric to be deleted.",
                    required = true,
                    example = "507f1f77bcf86cd799439011",
                    schema = @Schema(type = SchemaType.STRING))
            @PathParam("id") @Valid @NotFoundEntity(repository = MetricRepository.class, message = "There is no Metric with the following id:") String id) {


        boolean success = metricService.delete(id, installationId);

        var successResponse = new InformativeResponse();

        if (success) {
            successResponse.code = 200;
            successResponse.message = "Metric has been deleted successfully.";
        } else {
            successResponse.code = 500;
            successResponse.message = "Metric cannot be deleted due to a server issue. Please try again.";
        }
        return Response.ok().entity(successResponse).build();
    }

    @Tag(name = "Metric")
    @Operation(
            summary = "Updates an existing Metric.",
            description = "In order to update the resource properties, the body of the request must contain an updated representation of Metric. " +
                    "You can update a part or all attributes of the Metric except for metric_id. The empty or null values are ignored.")
    @APIResponse(
            responseCode = "200",
            description = "Metric was updated successfully.",
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
            description = "Metric/Metric Definition has not been found.",
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
    @Path("/{installation_id}/metrics/{id}")
    @Produces(value = MediaType.APPLICATION_JSON)
    @Consumes(value = MediaType.APPLICATION_JSON)
    public Response update(
            @Parameter(
                    description = "The Installation to which the Metric belongs.",
                    required = true,
                    example = "507f1f77bcf86cd799439011",
                    schema = @Schema(type = SchemaType.STRING))
            @PathParam("installation_id")
            @Valid
            @AccessInstallation(roles = {"admin"}) String installationId,
            @Parameter(
                    description = "The Metric to be updated.",
                    required = true,
                    example = "61dbe3f10086512c9ff1197a",
                    schema = @Schema(type = SchemaType.STRING))
            @PathParam("id") @Valid @NotFoundEntity(repository = MetricRepository.class, message = "There is no Metric with the following id:") String id, @Valid @NotNull(message = "The request body is empty.") UpdateMetricRequestDto updateMetricRequestDto) {

        MetricResponseDto response = metricService.update(id, updateMetricRequestDto);
        return Response.ok().entity(response).build();
    }

    @Tag(name = "Metric")
    @Operation(
            summary = "Returns an existing Metric.",
            description = "This operation accepts the id of a Metric and fetches from the database the corresponding record.")
    @APIResponse(
            responseCode = "200",
            description = "The corresponding Metric.",
            content = @Content(schema = @Schema(
                    type = SchemaType.OBJECT,
                    implementation = MetricResponseDto.class)))
    @APIResponse(
            responseCode = "401",
            description = "Client has not been authenticated.",
            content = @Content(schema = @Schema(
                    type = SchemaType.OBJECT,
                    implementation = InformativeResponse.class)))
    @APIResponse(
            responseCode = "404",
            description = "Metric has not been found.",
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
    @Path("/{installation_id}/metrics/{id}")
    @Produces(value = MediaType.APPLICATION_JSON)
    public Response getMetric(
            @Parameter(
                    description = "The Installation to which the Metric belongs.",
                    required = true,
                    example = "507f1f77bcf86cd799439011",
                    schema = @Schema(type = SchemaType.STRING))
            @PathParam("installation_id")
            @Valid
            @AccessInstallation(roles = {"admin"}) String installationId,
            @Parameter(
                    description = "The Metric to be retrieved.",
                    required = true,
                    example = "507f1f77bcf86cd799439011",
                    schema = @Schema(type = SchemaType.STRING))
            @PathParam("id") @Valid @NotFoundEntity(repository = MetricRepository.class, message = "There is no Metric with the following id:") String id) {

        MetricResponseDto response = metricService.fetchMetric(id);

        return Response.ok().entity(response).build();
    }

    @Tag(name = "Metric")
    @Operation(
            summary = "Returns all Metrics under a specific Installation.",
            description = "This operation is responsible for fetching all Metrics under a specific Installation. " +
                    "By passing the Project and Provider IDs to which the Installation belongs as well as the Installation ID, you can retrieve all Metrics that have been assigned to this specific Installation. By default, the first page of 10 Metrics will be returned. " +
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
    @Path("/{installation_id}/metrics")
    @Timed(name = "checksMetricRetrievalOfInstallation", description = "A measure of how long it takes to retrieve Metrics under an Installation.", unit = MetricUnits.MILLISECONDS)
    @Produces(value = MediaType.APPLICATION_JSON)
    public Response getAllMetricsUnderAnInstallation(
            @Parameter(
                    description = "Τhe Installation id with which you can request all the Metrics that have been assigned to it.",
                    required = true,
                    example = "507f1f77bcf86cd799439011",
                    schema = @Schema(type = SchemaType.STRING))
            @PathParam("installation_id") @Valid @AccessInstallation(roles = {"admin", "viewer"}) String installationId,
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

        var installation = installationService.fetchInstallation(installationId);

        var response = metricService.fetchAllMetrics(installation.getProject() + HierarchicalRelation.PATH_SEPARATOR + installation.getOrganisation() + HierarchicalRelation.PATH_SEPARATOR + installationId, page - 1, size, serverInfo, start, end, metricDefinitionId);

        return Response.ok().entity(response).build();
    }

    @Tag(name = "Installation")
    @Operation(
            summary = "Returns Installation's Metric Definitions.",
            description = "This operation is responsible for returning Installation's Metric Definitions. " +
                    "By default, the first page of 10 Metric Definitions will be returned. " +
                    "You can tune the default values by using the query parameters page and size.")
    @APIResponse(
            responseCode = "200",
            description = "All Installation's Metric Definitions.",
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
    @Path("/{installation_id}/metric-definitions")
    @Produces(value = MediaType.APPLICATION_JSON)
    public Response getAllInstallationMetricDefinitions(
            @Parameter(
                    description = "Τhe Installation id.",
                    required = true,
                    example = "507f1f77bcf86cd799439011",
                    schema = @Schema(type = SchemaType.STRING))
            @PathParam("installation_id") @Valid @AccessInstallation(roles = {"admin", "viewer"}) String id,
            @Parameter(name = "page", in = QUERY,
                    description = "Indicates the page number. Page number must be >= 1.") @DefaultValue("1") @Min(value = 1, message = "Page number must be >= 1.") @QueryParam("page") int page,
            @Parameter(name = "size", in = QUERY,
                    description = "The page size.") @DefaultValue("10") @Min(value = 1, message = "Page size must be between 1 and 100.")
            @Max(value = 100, message = "Page size must be between 1 and 100.") @QueryParam("size") int size, @Context UriInfo uriInfo) {

        var serverInfo = new AccountingUriInfo(serverUrl.concat(basePath).concat(uriInfo.getPath()));

        var response = installationService.fetchAllMetricDefinitions(id, page - 1, size, serverInfo);

        return Response.ok().entity(response).build();
    }

    @Tag(name = "Installation")
    @Operation(
            operationId = "search-installation",
            summary = "Searches an installation",
            description = "Search Installation")

    @APIResponse(
            responseCode = "200",
            description = "The corresponding Installations.",
            content = @Content(schema = @Schema(
                    type = SchemaType.STRING,
                    implementation = PageableInstallationResponseDto.class)))
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
                                    name = "An example request of a search on installations",
                                    value = "{\n" +
                                            "            \"type\":\"query\",\n" +
                                            "            \"field\": \"installation\",\n" +
                                            "            \"values\": \"GRNET-KNS-3\",\n" +
                                            "            \"operand\": \"eq\"          \n" +
                                            "\n" +
                                            "}",
                                    summary = "A simple search on Installations "),
                            @ExampleObject(
                                    name = "An example request of a complex search on installations",
                                    value = "\n" +
                                            "{\n" +
                                            "  \"type\": \"filter\",\n" +
                                            "  \"operator\": \"OR\",\n" +
                                            "  \"criteria\": [\n" +
                                            " \n" +
                                            "    {\n" +
                                            "      \"type\": \"filter\",\n" +
                                            "      \"operator\": \"OR\",\n" +
                                            "      \"criteria\": [{\n" +
                                            "            \"type\":\"query\",\n" +
                                            "            \"field\": \"installation\",\n" +
                                            "            \"values\": \"GRNET-KNS-3\",\n" +
                                            "            \"operand\": \"eq\"          \n" +
                                            "\n" +
                                            "},{\n" +
                                            "            \"type\":\"query\",\n" +
                                            "            \"field\": \"organisation\",\n" +
                                            "            \"values\": \"grnet\",\n" +
                                            "            \"operand\": \"eq\"          \n" +
                                            "\n" +
                                            "}]\n" +
                                            "    }]}",
                                    summary = "A complex search on Installations ")})
            ) String json,
            @Parameter(name = "page", in = QUERY,
                    description = "Indicates the page number. Page number must be >= 1.") @DefaultValue("1")@Min(value = 1, message = "Page number must be >= 1.") @QueryParam("page") int page,
            @Parameter(name = "size", in = QUERY,
                    description = "The page size.") @DefaultValue("10") @Min(value = 1, message = "Page size must be between 1 and 100.")
            @Max(value = 100, message = "Page size must be between 1 and 100.") @QueryParam("size") int size,
            @Context UriInfo uriInfo
    ) throws ParseException, NoSuchFieldException, org.json.simple.parser.ParseException, JsonProcessingException {

        var serverInfo = new AccountingUriInfo(serverUrl.concat(basePath).concat(uriInfo.getPath()));

        if (json.equals("")) {
            throw new BadRequestException("not empty body permitted");
        }
        var response= installationService.searchInstallation(json, page - 1, size, serverInfo);
       return Response.ok(response).build();
    }

    @Tag(name = "Installation")
    @Operation(
            summary = "get all installations",
            description = "Get All Installations")
    @APIResponse(
            responseCode = "200",
            description = "The corresponding Installations.",
            content = @Content(schema = @Schema(
                    type = SchemaType.ARRAY,
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
    @Path("/all")
    @Produces(value = MediaType.APPLICATION_JSON)
    @Consumes(value = MediaType.APPLICATION_JSON)
    @AccessResource
    public Response getAll(
            @Parameter(name = "page", in = QUERY,
                    description = "Indicates the page number. Page number must be >= 1.") @DefaultValue("1") @Min(value = 1, message = "Page number must be >= 1.") @QueryParam("page") int page,
            @Parameter(name = "size", in = QUERY,
                    description = "The page size.") @DefaultValue("10") @Min(value = 1, message = "Page size must be between 1 and 100.")
            @Max(value = 100, message = "Page size must be between 1 and 100.") @QueryParam("size") int size, @Context UriInfo uriInfo) throws ParseException, NoSuchFieldException, org.json.simple.parser.ParseException, JsonProcessingException {

        var serverInfo = new AccountingUriInfo(serverUrl.concat(basePath).concat(uriInfo.getPath()));

        var response = installationService.getAllInstallations( page - 1, size, serverInfo);

        return Response.ok().entity(response).build();
    }

    @Tag(name = "Installation")
    @Operation(
            summary = "Get installation report with metrics.",
            description = "Returns a report for a specific installation and time period, including aggregated metric values.")
    @APIResponse(
            responseCode = "200",
            description = "Installation report retrieved successfully.",
            content = @Content(schema = @Schema(
                    type = SchemaType.OBJECT,
                    implementation = InstallationReport.class)))
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
    @Path("/{installation_id}/report")
    @Produces(value = MediaType.APPLICATION_JSON)
    public Response report(
            @Parameter(
                    description = "The ID of the installation.",
                    required = true,
                    example = "507f1f77bcf86cd799439011",
                    schema = @Schema(type = SchemaType.STRING))
            @PathParam("installation_id") @Valid @AccessInstallation(roles = {"admin", "viewer"}) String installationId,

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

        var report = installationService.installationReport(installationId, start, end);

        return Response.ok().entity(report).build();
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
}