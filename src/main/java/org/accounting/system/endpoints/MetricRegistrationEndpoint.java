package org.accounting.system.endpoints;

import org.accounting.system.dtos.InformativeResponse;
import org.accounting.system.dtos.MetricRegistrationDtoRequest;
import org.accounting.system.dtos.MetricRegistrationDtoResponse;
import org.accounting.system.services.MetricRegistrationService;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.Objects;

@Path("/metric-registration")
public class MetricRegistrationEndpoint {

    @Inject
    private MetricRegistrationService metricRegistrationService;


    public MetricRegistrationEndpoint(MetricRegistrationService metricRegistrationService) {
        this.metricRegistrationService = metricRegistrationService;
    }


    @Tag(name = "Submit Metric Registration.")
    @Operation(
            summary = "Records a new Metric Registration.",
            description = "Retrieves and inserts a Metric Registration into the database. Typically, " +
                    "a Metric Registration is the metadata describing a Virtual Access Metric. " +
                    "It should be noted that the combination of unit_type and metric_name should be unique. " +
                    "If you execute a request with a unit_type and metric_name, which have already been generated, you receive an error response.")
    @APIResponse(
            responseCode = "201",
            description = "Metric Registration has been created successfully.",
            content = @Content(schema = @Schema(
                    type = SchemaType.OBJECT,
                    implementation = MetricRegistrationDtoResponse.class)))
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
            responseCode = "409",
            description = "There is a Metric Registration with that unit_type and metric_name.",
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
    public Response save(@Valid MetricRegistrationDtoRequest metricRegistrationDtoRequest, @Context UriInfo uriInfo) {

        if(Objects.isNull(metricRegistrationDtoRequest)){

            throw new BadRequestException("The request body is empty.");
        }

        metricRegistrationService.exist(metricRegistrationDtoRequest.unitType, metricRegistrationDtoRequest.metricName);

        var response = metricRegistrationService.save(metricRegistrationDtoRequest);

        return Response.created(uriInfo.getAbsolutePathBuilder().path(response.id).build()).entity(response).build();
    }
}
