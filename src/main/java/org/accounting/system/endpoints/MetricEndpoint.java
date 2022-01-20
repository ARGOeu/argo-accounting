package org.accounting.system.endpoints;

import org.accounting.system.dtos.InformativeResponse;
import org.accounting.system.dtos.MetricRequestDto;
import org.accounting.system.dtos.MetricResponseDto;
import org.accounting.system.services.MetricService;
import org.accounting.system.util.Predicates;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.Optional;

@Path("/metrics")
public class MetricEndpoint {

    @Inject
    private MetricService metricService;

    @Inject
    private Predicates predicates;

    public MetricEndpoint(MetricService metricService, Predicates predicates) {
        this.metricService = metricService;
        this.predicates = predicates;
    }


    @Tag(name = "Submit Metric.")
    @Operation(
            summary = "Registers a new Metric.",
            description = "Retrieves and inserts a Metric into the database. " +
                    "The Metric is assigned to the Metric Definition with id {metric_definition_id}.")
    @APIResponse(
            responseCode = "201",
            description = "Metric has been created successfully.",
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
            description = "User has not been authenticated.",
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

    @POST
    @Produces(value = MediaType.APPLICATION_JSON)
    @Consumes(value = MediaType.APPLICATION_JSON)
    public Response save(@Valid MetricRequestDto metricRequestDto, @Context UriInfo uriInfo) {

        predicates
                .emptyRequestBody(metricRequestDto)
                .accept(metricRequestDto);

        MetricResponseDto response = metricService.save(metricRequestDto);

        return Response.created(uriInfo.getAbsolutePathBuilder().path(response.id).build()).entity(response).build();
    }

    @Tag(name = "Search Metric.")
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
            description = "User has not been authenticated.",
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

    @GET
    @Path("/{metric_id}")
    @Produces(value = MediaType.APPLICATION_JSON)
    public Response get(
            @Parameter(
                    description = "The Metric to be retrieved.",
                    required = true,
                    example = "507f1f77bcf86cd799439011",
                    schema = @Schema(type = SchemaType.STRING))
            @PathParam("metric_id") String metricId){

        Optional<MetricResponseDto> response = metricService.fetchMetric(metricId);

        return response
                .map(dto->Response.ok().entity(response.get()).build())
                .orElseThrow(()-> new NotFoundException("The Metric has not been found."));
    }
}
