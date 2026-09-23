package org.grnet.creditmanagement;

import io.quarkus.security.Authenticated;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.ParameterIn;
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
import org.grnet.creditmanagement.dtos.CreditBalanceResponseDto;
import org.grnet.creditmanagement.dtos.CreditUsageReportResponseDto;
import org.grnet.creditmanagement.exceptions.RatingPolicyConflictExceptionMapper;
import org.grnet.creditmanagement.security.CreditManagementSecured;
import org.grnet.creditmanagement.services.CreditBalanceService;
import org.grnet.creditmanagement.services.CreditUsageReportService;

import java.time.LocalDate;

@Path("/projects")
@Authenticated
@SecurityScheme(securitySchemeName = "Authentication",
        description = "JWT token",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER)
public class CreditUsageReportEndpoint {

    @Inject
    CreditUsageReportService creditUsageReportService;

    @Inject
    CreditBalanceService creditBalanceService;

    @Tag(name = "Credit Management")
    @Operation(
            summary = "Retrieve a credit usage report for a Project over a time range.",
            description = "For every Installation under the given Project (optionally scoped to a single " +
                    "installation) and for every Metric Definition rated or reported on that Installation, " +
                    "returns the total consumed metric value and the corresponding credits within the " +
                    "reporting window, broken down into sub-periods whenever the applicable Rate Policy " +
                    "rate changed during the range, including sub-periods where no policy was in effect. " +
                    "The window runs from the start of 'from' (inclusive) to the end of 'to' (inclusive), " +
                    "both given as calendar dates, since Rate Policies always take effect at the start of " +
                    "a day and Metric events are daily. The optional user_id and/or group_id filters scope " +
                    "the underlying metric events included in the aggregation; when both are provided, " +
                    "events must match both. Installations or Metric Definitions with no events and no " +
                    "Rate Policy entries at all within scope are omitted from the response.")
    @APIResponse(
            responseCode = "200",
            description = "The credit usage report.",
            content = @Content(schema = @Schema(implementation = CreditUsageReportResponseDto.class)))
    @APIResponse(
            responseCode = "400",
            description = "The 'from'/'to' parameters are missing or 'from' is after 'to'.",
            content = @Content(schema = @Schema(type = SchemaType.OBJECT, implementation = RatingPolicyConflictExceptionMapper.ErrorResponse.class)))
    @APIResponse(
            responseCode = "404",
            description = "The Project does not exist, the given installation_id does not exist under this " +
                    "Project, or the given metric_definition_id does not exist.",
            content = @Content(schema = @Schema(type = SchemaType.OBJECT, implementation = RatingPolicyConflictExceptionMapper.ErrorResponse.class)))
    @APIResponse(
            responseCode = "500",
            description = "Internal Server Error.",
            content = @Content(schema = @Schema(type = SchemaType.OBJECT, implementation = RatingPolicyConflictExceptionMapper.ErrorResponse.class)))
    @SecurityRequirement(name = "Authentication")
    @GET
    @Path("/{project_id}/reports/credit-usage")
    @Produces(MediaType.APPLICATION_JSON)
    @CreditManagementSecured
    public Response getCreditUsageReport(

            @Parameter(name = "project_id", in = ParameterIn.PATH, description = "The project id.", required = true,
                    schema = @Schema(type = SchemaType.STRING, implementation = String.class, example = "707f1f77bcf86cd799439011"))
            @PathParam("project_id") String projectId,

            @Parameter(name = "from", description = "The first calendar date (inclusive) of the reporting window, format yyyy-MM-dd.", required = true,
                    schema = @Schema(type = SchemaType.STRING, example = "2026-07-01"))
            @QueryParam("from") LocalDate from,

            @Parameter(name = "to", description = "The last calendar date (inclusive) of the reporting window, format yyyy-MM-dd.", required = true,
                    schema = @Schema(type = SchemaType.STRING, example = "2026-07-31"))
            @QueryParam("to") LocalDate to,

            @Parameter(name = "installation_id", description = "Restrict the report to a single installation.",
                    schema = @Schema(type = SchemaType.STRING, example = "707f1f77bcf86cd799439013"))
            @QueryParam("installation_id") String installationId,

            @Parameter(name = "metric_definition_id", description = "Restrict the report to a single metric definition.",
                    schema = @Schema(type = SchemaType.STRING, example = "6a46466990abcf66c50422a6"))
            @QueryParam("metric_definition_id") String metricDefinitionId,

            @Parameter(name = "user_id", description = "Restrict the underlying metric events to those matching this user_id.",
                    schema = @Schema(type = SchemaType.STRING))
            @QueryParam("user_id") String userId,

            @Parameter(name = "group_id", description = "Restrict the underlying metric events to those matching this group_id.",
                    schema = @Schema(type = SchemaType.STRING))
            @QueryParam("group_id") String groupId) {

        var response = creditUsageReportService.generateReport(projectId, from, to, installationId, metricDefinitionId, userId, groupId);

        return Response.ok(response).build();
    }

    @Tag(name = "Credit Management")
    @Operation(
            summary = "Retrieve the credit balance of a group under a project as of a point in time.",
            description = "Computes balance as of the end of a given calendar date, 'at' (defaults to today " +
                    "if omitted) — resolved internally to the start of the day AFTER 'at' (00:00:00 UTC), so " +
                    "the entire 'at' day is included. The wallet resets with each new Credit Allocation: the " +
                    "balance is always based on the most recently started allocation whose valid_from is on " +
                    "or before that resolved instant — found by looking backwards in time, even if that " +
                    "allocation's own valid_to has already passed. allocated_credits is that allocation's " +
                    "full total_credits (0 if none was ever registered). consumed_credits is the total " +
                    "consumed by this group across all installations under the project, from that " +
                    "allocation's valid_from up to the resolved instant. balance = allocated_credits - " +
                    "consumed_credits, and can go negative: this endpoint does not prevent overdraft, it " +
                    "only reports it, with a reason: 'allocated credits exhausted' if an allocation was still " +
                    "in effect but fully consumed, or 'no allocation policy in effect' if none covered that " +
                    "point in time at all. When 'at' is explicitly provided, the response is flagged as a " +
                    "snapshot: it reflects only what had happened by then and does not represent the " +
                    "current, true balance.")
    @APIResponse(
            responseCode = "200",
            description = "The credit balance as of 'at'.",
            content = @Content(schema = @Schema(implementation = CreditBalanceResponseDto.class)))
    @APIResponse(
            responseCode = "404",
            description = "The Project does not exist.")
    @APIResponse(
            responseCode = "500",
            description = "Internal Server Error.")
    @SecurityRequirement(name = "Authentication")
    @GET
    @Path("/{project_id}/groups/{group_id}/balance")
    @Produces(MediaType.APPLICATION_JSON)
    @CreditManagementSecured
    public Response getBalance(

            @Parameter(name = "project_id", in = ParameterIn.PATH, description = "The project id.", required = true,
                    schema = @Schema(type = SchemaType.STRING, implementation = String.class, example = "707f1f77bcf86cd799439011"))
            @PathParam("project_id") String projectId,

            @Parameter(name = "group_id", in = ParameterIn.PATH, description = "The group id.", required = true,
                    schema = @Schema(type = SchemaType.STRING, implementation = String.class, example = "group-42"))
            @PathParam("group_id") String groupId,

            @Parameter(name = "at", description = "The calendar date to evaluate the balance as of (inclusive), format yyyy-MM-dd. Defaults to today if omitted.",
                    schema = @Schema(type = SchemaType.STRING, example = "2026-08-20"))
            @QueryParam("at") LocalDate at) {

        var response = creditBalanceService.getBalance(projectId, groupId, at);

        return Response.ok(response).build();
    }
}
