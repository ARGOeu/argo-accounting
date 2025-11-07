package org.accounting.system.endpoints;

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
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import org.accounting.system.constraints.CheckDateFormat;
import org.accounting.system.constraints.NotFoundEntity;
import org.accounting.system.dtos.InformativeResponse;
import org.accounting.system.dtos.installation.InstallationResponseDto;
import org.accounting.system.dtos.installation.UpdateInstallationRequestDto;
import org.accounting.system.dtos.metric.MetricRequestDto;
import org.accounting.system.dtos.metric.MetricResponseDto;
import org.accounting.system.entities.projections.InstallationReportNew;
import org.accounting.system.repositories.metricdefinition.MetricDefinitionRepository;
import org.accounting.system.services.installation.InstallationService;
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

import java.time.LocalDate;

import static org.eclipse.microprofile.openapi.annotations.enums.ParameterIn.QUERY;

@Path("/installations/external")
@Authenticated
@SecurityScheme(securitySchemeName = "Authentication",
        description = "JWT token",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER)
public class ExternalInstallationEndpoint {

    @ConfigProperty(name = "quarkus.rest.path")
    String basePath;

    @ConfigProperty(name = "api.server.url")
    String serverUrl;

    @Inject
    InstallationService installationService;


    @Tag(name = "Installation")
    @Operation(
            summary = "Returns an existing Installation.",
            description = "This operation accepts the external id of an Installation and fetches from the database the corresponding record.")
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
    @Produces(value = MediaType.APPLICATION_JSON)
    public Response get(@Parameter(name = "externalId", in = QUERY, required = true, example = "installation-446655440000", allowReserved = true,
            description = "The external installation id.", schema = @Schema(type = SchemaType.STRING)) @QueryParam("externalId") @NotEmpty(message = "externalId may not be empty.") String externalId) {

        var response = installationService.fetchExternalInstallation(externalId);

        return Response.ok().entity(response).build();
    }

    @Tag(name = "Installation")
    @Operation(
            summary = "Deletes an existing external Installation.",
            description = "This operation deletes an existing external Installation registered through Accounting System API.")
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
    @DELETE
    @Produces(value = MediaType.APPLICATION_JSON)
    public Response delete(@Parameter(name = "externalId", in = QUERY, required = true, example = "installation-446655440000", allowReserved = true,
            description = "The external installation id.", schema = @Schema(type = SchemaType.STRING)) @QueryParam("externalId") @NotEmpty(message = "externalId may not be empty.") String externalId) {

        installationService.deleteExternalInstallation(externalId);

        var successResponse = new InformativeResponse();

        successResponse.code = 200;
        successResponse.message = "Installation has been deleted successfully.";

        return Response.ok().entity(successResponse).build();
    }

    @Tag(name = "Installation")
    @Operation(
            summary = "Updates an existing external Installation.",
            description = "This operation updates an existing external Installation registered through the Accounting System API. Finally, " +
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
    @Produces(value = MediaType.APPLICATION_JSON)
    @Consumes(value = MediaType.APPLICATION_JSON)
    public Response update(@Parameter(name = "externalId", in = QUERY, required = true, example = "installation-446655440000", allowReserved = true,
            description = "The external installation id.", schema = @Schema(type = SchemaType.STRING)) @QueryParam("externalId") @NotEmpty(message = "externalId may not be empty.") String externalId,
                           @Valid @NotNull(message = "The request body is empty.") UpdateInstallationRequestDto updateInstallationRequestDto) {

        var response = installationService.updateExternalInstallation(externalId, updateInstallationRequestDto);

        return Response.ok().entity(response).build();
    }

    @Tag(name = "Installation")
    @Operation(
            summary = "Get report of external installation with metrics.",
            description = "Returns a report for a specific external installation and time period, including aggregated metric values.")
    @APIResponse(
            responseCode = "200",
            description = "Installation report retrieved successfully.",
            content = @Content(schema = @Schema(
                    type = SchemaType.OBJECT,
                    implementation = InstallationReportNew.class)))
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
    @Path("/report")
    @Produces(value = MediaType.APPLICATION_JSON)
    public Response report(@Parameter(name = "externalId", in = QUERY, required = true, example = "installation-446655440000", allowReserved = true,
            description = "The external installation id.", schema = @Schema(type = SchemaType.STRING)) @QueryParam("externalId") @NotEmpty(message = "externalId may not be empty.") String externalId,
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

        var report = installationService.externalInstallationReport(externalId, start, end);

        return Response.ok().entity(report).build();
    }

    @Tag(name = "Metric")
    @Operation(
            summary = "Assigns a new Metric to a specific external Installation.",
            description = "Fundamentally, this operation creates a new Metric and assigns it to a specific external Installation. " +
                    "Metric is assigned to the given external Installation but belongs to the hierarchical structure Project -> Provider -> Installation.")
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
    @Path("/metrics")
    @Produces(value = MediaType.APPLICATION_JSON)
    @Consumes(value = MediaType.APPLICATION_JSON)
    public Response save(@Parameter(name = "externalId", in = QUERY, required = true, example = "installation-446655440000", allowReserved = true,
            description = "The external installation id.", schema = @Schema(type = SchemaType.STRING)) @QueryParam("externalId") @NotEmpty(message = "externalId may not be empty.") String externalId,
            @Valid @NotNull(message = "The request body is empty.") MetricRequestDto metricRequestDto, @Context UriInfo uriInfo) {

        var serverInfo = new AccountingUriInfo(serverUrl.concat(basePath).concat(uriInfo.getPath()));

        var response = installationService.assignMetricToExternalInstallation(externalId, metricRequestDto);

        return Response.created(serverInfo.getAbsolutePathBuilder().path(response.id).build()).entity(response).build();
    }

    @Tag(name = "Metric")
    @Operation(
            summary = "Returns all Metrics under a specific Installation by external id.",
            description = "This operation is responsible for fetching all Metrics under a specific Installation by external id. " +
                    "By passing the Project and Provider IDs to which the Installation belongs as well as the Installation ID, you can retrieve all Metrics that have been assigned to this specific Installation. By default, the first page of 10 Metrics will be returned. " +
                    "You can tune the default values by using the query parameters page and size.")
    @APIResponse(
            responseCode = "200",
            description = "All Metrics.",
            content = @Content(schema = @Schema(
                    type = SchemaType.OBJECT,
                    implementation = InstallationEndpoint.PageableMetricProjection.class)))
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
    @Path("/metrics")
    @Produces(value = MediaType.APPLICATION_JSON)
    public Response getAllMetricsUnderAnInstallation(@Parameter(name = "externalId", in = QUERY, required = true, example = "installation-446655440000", allowReserved = true,
            description = "The external installation id.", schema = @Schema(type = SchemaType.STRING)) @QueryParam("externalId") @NotEmpty(message = "externalId may not be empty.") String externalId,
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

        var response = installationService.fetchAllMetricsByExternalId(externalId, page - 1, size, serverInfo, start, end, metricDefinitionId);

        return Response.ok().entity(response).build();
    }
}
