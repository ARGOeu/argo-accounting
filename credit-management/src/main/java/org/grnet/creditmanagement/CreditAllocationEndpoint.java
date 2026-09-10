package org.grnet.creditmanagement;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.Consumes;
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
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.ParameterIn;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.grnet.creditmanagement.dtos.CreditAllocationRequestDto;
import org.grnet.creditmanagement.dtos.CreditAllocationResponseDto;
import org.grnet.creditmanagement.dtos.CreditAllocationUpdateRequestDto;
import org.grnet.creditmanagement.exceptions.RatingPolicyConflictExceptionMapper;
import org.grnet.creditmanagement.pagination.PageResource;
import org.grnet.creditmanagement.security.CreditManagementSecured;
import org.grnet.creditmanagement.services.CreditAllocationService;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;

@Path("/projects")
public class CreditAllocationEndpoint {

    @Inject
    CreditAllocationService creditAllocationService;

    @Tag(name = "Credit Management")
    @Operation(
            summary = "Allocate a total number of credits to a group under a project for a specific period.",
            description = "Creates a new Credit Allocation for the given project_id and group_id, granting " +
                    "total_credits to be consumed across all Installations under that project, equally, " +
                    "during [valid_from, valid_to). Both valid_from and valid_to are automatically rounded " +
                    "down to the start of the day (00:00:00 UTC), consistent with Rate Policy entries, " +
                    "since credit accounting in this system is always day-aligned. Unlike Rate Policy rates, " +
                    "an allocation represents a closed budget for a bounded period, so both a start and an " +
                    "end date are required. A stored allocation is never modified; changing an allocation " +
                    "always means creating a new entry. For a given project_id/group_id pair, allocation " +
                    "periods must never overlap with each other — overlap is checked against the rounded " +
                    "valid_from/valid_to values.")
    @APIResponse(
            responseCode = "200",
            description = "The Credit Allocation has been successfully created.",
            content = @Content(schema = @Schema(implementation = CreditAllocationResponseDto.class)))
    @APIResponse(
            responseCode = "400",
            description = "valid_from is not strictly earlier than valid_to, total_credits is not greater " +
                    "than zero, or the requested period overlaps with an existing allocation for the same " +
                    "project_id and group_id.",
            content = @Content(schema = @Schema(type = SchemaType.OBJECT, implementation = RatingPolicyConflictExceptionMapper.ErrorResponse.class)))
    @APIResponse(
            responseCode = "404",
            description = "The Project does not exist.",
            content = @Content(schema = @Schema(type = SchemaType.OBJECT, implementation = RatingPolicyConflictExceptionMapper.ErrorResponse.class)))
    @APIResponse(
            responseCode = "500",
            description = "Internal Server Error.",
            content = @Content(schema = @Schema(type = SchemaType.OBJECT, implementation = RatingPolicyConflictExceptionMapper.ErrorResponse.class)))
    @SecurityRequirement(name = "Authentication")
    @POST
    @Path("/{project_id}/groups/{group_id}/allocations")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @CreditManagementSecured
    public Response createAllocation(

            @Parameter(name = "project_id", in = ParameterIn.PATH, description = "The project id.", required = true,
                    schema = @Schema(type = SchemaType.STRING, implementation = String.class, example = "707f1f77bcf86cd799439011"))
            @PathParam("project_id") String projectId,

            @Parameter(name = "group_id", in = ParameterIn.PATH, description = "The group id.", required = true,
                    schema = @Schema(type = SchemaType.STRING, implementation = String.class, example = "group-42"))
            @PathParam("group_id") String groupId,

            @RequestBody(description = "The allocation period and total credits.",
                    content = @Content(schema = @Schema(implementation = CreditAllocationRequestDto.class)))
            @Valid @NotNull(message = "The request body is empty.") CreditAllocationRequestDto request) {

        var response = creditAllocationService.createAllocation(projectId, groupId, request);

        return Response.ok(response).build();
    }

    @Tag(name = "Credit Management")
    @Operation(
            summary = "Retrieve the Credit Allocation effective at a specific point in time.",
            description = "Same logic as the 'current' endpoint, but evaluated at the given 'at' timestamp " +
                    "instead of the present moment. Returns the single allocation whose [valid_from, valid_to) " +
                    "range covers 'at'.")
    @APIResponse(
            responseCode = "200",
            description = "The Credit Allocation effective at the given timestamp.",
            content = @Content(schema = @Schema(implementation = CreditAllocationResponseDto.class)))
    @APIResponse(
            responseCode = "400",
            description = "The 'at' query parameter is missing or is not a valid ISO-8601 timestamp.",
            content = @Content(schema = @Schema(type = SchemaType.OBJECT, implementation = RatingPolicyConflictExceptionMapper.ErrorResponse.class)))
    @APIResponse(
            responseCode = "404",
            description = "The Project does not exist, or there is no allocation effective at the given timestamp for this project_id/group_id.",
            content = @Content(schema = @Schema(type = SchemaType.OBJECT, implementation = RatingPolicyConflictExceptionMapper.ErrorResponse.class)))
    @APIResponse(
            responseCode = "500",
            description = "Internal Server Error.",
            content = @Content(schema = @Schema(type = SchemaType.OBJECT, implementation = RatingPolicyConflictExceptionMapper.ErrorResponse.class)))
    @SecurityRequirement(name = "Authentication")
    @GET
    @Path("/{project_id}/groups/{group_id}/allocations/effective")
    @Produces(MediaType.APPLICATION_JSON)
    @CreditManagementSecured
    public Response getEffectiveAllocation(
            @Parameter(name = "project_id", in = ParameterIn.PATH, description = "The project id.", required = true,
                    schema = @Schema(type = SchemaType.STRING, implementation = String.class, example = "707f1f77bcf86cd799439011"))
            @PathParam("project_id") String projectId,

            @Parameter(name = "group_id", in = ParameterIn.PATH, description = "The group id.", required = true,
                    schema = @Schema(type = SchemaType.STRING, implementation = String.class, example = "group-42"))
            @PathParam("group_id") String groupId,

            @Parameter(name = "at", description = "The point in time to evaluate, ISO-8601 date.", required = true,
                    schema = @Schema(type = SchemaType.STRING, format = "date", example = "2026-08-15"))
            @QueryParam("at") LocalDate at) {

        if (at == null) {
            throw new BadRequestException("The 'at' query parameter is required and must be a valid ISO-8601 timestamp.");
        }

        var instant = at.atStartOfDay(ZoneOffset.UTC).toInstant();

        var response = creditAllocationService.getEffectiveAllocation(projectId, groupId, instant);

        return Response.ok(response).build();
    }

    @Tag(name = "Credit Management")
    @Operation(
            summary = "Retrieve the currently active Credit Allocation for a group under a project.",
            description = "Returns the single allocation whose [valid_from, valid_to) range covers the " +
                    "present moment. Equivalent to the 'effective' endpoint evaluated at the current time.")
    @APIResponse(
            responseCode = "200",
            description = "The currently active Credit Allocation.",
            content = @Content(schema = @Schema(implementation = CreditAllocationResponseDto.class)))
    @APIResponse(
            responseCode = "404",
            description = "The Project does not exist, or there is no allocation currently active for this project_id/group_id.",
            content = @Content(schema = @Schema(type = SchemaType.OBJECT, implementation = RatingPolicyConflictExceptionMapper.ErrorResponse.class)))
    @APIResponse(
            responseCode = "500",
            description = "Internal Server Error.",
            content = @Content(schema = @Schema(type = SchemaType.OBJECT, implementation = RatingPolicyConflictExceptionMapper.ErrorResponse.class)))
    @SecurityRequirement(name = "Authentication")
    @GET
    @Path("/{project_id}/groups/{group_id}/allocations/current")
    @Produces(MediaType.APPLICATION_JSON)
    @CreditManagementSecured
    public Response getCurrentAllocation(
            @Parameter(name = "project_id", in = ParameterIn.PATH, description = "The project id.", required = true,
                    schema = @Schema(type = SchemaType.STRING, implementation = String.class, example = "707f1f77bcf86cd799439011"))
            @PathParam("project_id") String projectId,

            @Parameter(name = "group_id", in = ParameterIn.PATH, description = "The group id.", required = true,
                    schema = @Schema(type = SchemaType.STRING, implementation = String.class, example = "group-42"))
            @PathParam("group_id") String groupId) {

        var response = creditAllocationService.getCurrentAllocation(projectId, groupId);

        return Response.ok(response).build();
    }

    @Tag(name = "Credit Management")
    @Operation(
            summary = "Retrieve the full Credit Allocation history for a group under a project.",
            description = "Returns all allocation entries ever created for the given project_id/group_id " +
                    "pair, regardless of whether they are past, current, or future-dated, ordered by " +
                    "valid_from descending (most recent first).")
    @APIResponse(
            responseCode = "200",
            description = "A paginated list of Credit Allocation entries.",
            content = @Content(schema = @Schema(implementation = PageableCreditAllocationResponseDto.class)))
    @APIResponse(
            responseCode = "404",
            description = "The Project does not exist.",
            content = @Content(schema = @Schema(type = SchemaType.OBJECT, implementation = RatingPolicyConflictExceptionMapper.ErrorResponse.class)))
    @APIResponse(
            responseCode = "500",
            description = "Internal Server Error.",
            content = @Content(schema = @Schema(type = SchemaType.OBJECT, implementation = RatingPolicyConflictExceptionMapper.ErrorResponse.class)))
    @SecurityRequirement(name = "Authentication")
    @GET
    @Path("/{project_id}/groups/{group_id}/allocations/history")
    @Produces(MediaType.APPLICATION_JSON)
    @CreditManagementSecured
    public Response getAllocationHistory(
            @Parameter(name = "project_id", in = ParameterIn.PATH, description = "The project id.", required = true,
                    schema = @Schema(type = SchemaType.STRING, implementation = String.class, example = "707f1f77bcf86cd799439011"))
            @PathParam("project_id") String projectId,

            @Parameter(name = "group_id", in = ParameterIn.PATH, description = "The group id.", required = true,
                    schema = @Schema(type = SchemaType.STRING, implementation = String.class, example = "group-42"))
            @PathParam("group_id") String groupId,

            @Parameter(name = "page", description = "The page number. Must be >= 1.")
            @Min(value = 1, message = "Page size must be between 1 and 100.")
            @DefaultValue("1") @QueryParam("page") int page,

            @Parameter(name = "size", description = "The page size.")
            @Max(value = 100, message = "Page size must be between 1 and 100.")
            @DefaultValue("10") @QueryParam("size") int size,
            @Context UriInfo uriInfo) {

        var response = creditAllocationService.getAllocationHistory(projectId, groupId, page, size, uriInfo);

        return Response.ok(response).build();
    }

    @Tag(name = "Credit Management")
    @Operation(
            summary = "Update an existing Credit Allocation in place.",
            description = "Updates any combination of valid_from, valid_to, and total_credits on an existing " +
                    "Credit Allocation.")
    @APIResponse(
            responseCode = "200",
            description = "The Credit Allocation has been updated.",
            content = @Content(schema = @Schema(implementation = CreditAllocationResponseDto.class)))
    @APIResponse(
            responseCode = "400",
            description = "The resulting valid_from is not strictly earlier than valid_to, the resulting " +
                    "total_credits is not greater than zero, or the resulting period overlaps with another " +
                    "existing allocation for the same project_id and group_id.",
            content = @Content(schema = @Schema(type = SchemaType.OBJECT, implementation = RatingPolicyConflictExceptionMapper.ErrorResponse.class)))
    @APIResponse(
            responseCode = "404",
            description = "The Project does not exist, or there is no Credit Allocation with the given id for " +
                    "this project_id/group_id.",
            content = @Content(schema = @Schema(type = SchemaType.OBJECT, implementation = RatingPolicyConflictExceptionMapper.ErrorResponse.class)))
    @APIResponse(
            responseCode = "500",
            description = "Internal Server Error.",
            content = @Content(schema = @Schema(type = SchemaType.OBJECT, implementation = RatingPolicyConflictExceptionMapper.ErrorResponse.class)))
    @SecurityRequirement(name = "Authentication")
    @PATCH
    @Path("/{project_id}/groups/{group_id}/allocations/{allocation_id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateAllocation(

            @Parameter(name = "project_id", in = ParameterIn.PATH, description = "The project id.", required = true,
                    schema = @Schema(type = SchemaType.STRING, implementation = String.class, example = "707f1f77bcf86cd799439011"))
            @PathParam("project_id") String projectId,

            @Parameter(name = "group_id", in = ParameterIn.PATH, description = "The group id.", required = true,
                    schema = @Schema(type = SchemaType.STRING, implementation = String.class, example = "group-42"))
            @PathParam("group_id") String groupId,

            @Parameter(name = "allocation_id", in = ParameterIn.PATH, description = "The Credit Allocation id.", required = true,
                    schema = @Schema(type = SchemaType.STRING, implementation = String.class, example = "64f1a2b3c4d5e6f7a8b9c0d1"))
            @PathParam("allocation_id") String allocationId,

            @RequestBody(description = "The fields to update. Omitted fields are left unchanged.",
                    content = @Content(schema = @Schema(implementation = CreditAllocationUpdateRequestDto.class)))
            @Valid @NotNull(message = "The request body is empty.") CreditAllocationUpdateRequestDto request) {

        var response = creditAllocationService.updateAllocation(projectId, groupId, allocationId, request);

        return Response.ok(response).build();
    }

    public static class PageableCreditAllocationResponseDto extends PageResource<CreditAllocationResponseDto> {

        private List<CreditAllocationResponseDto> content;

        @Override
        public List<CreditAllocationResponseDto> getContent() {
            return content;
        }

        @Override
        public void setContent(List<CreditAllocationResponseDto> content) {
            this.content = content;
        }
    }
}
