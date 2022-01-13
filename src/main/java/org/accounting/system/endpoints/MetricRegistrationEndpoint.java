package org.accounting.system.endpoints;

import org.accounting.system.dtos.InformativeResponse;
import org.accounting.system.dtos.MetricRegistrationDtoRequest;
import org.accounting.system.dtos.MetricRegistrationDtoResponse;
import org.accounting.system.dtos.UpdateMetricRegistrationDtoRequest;
import org.accounting.system.services.MetricRegistrationService;
import org.accounting.system.services.ReadPredefinedTypesService;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.PATCH;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.Objects;
import java.util.Optional;

@Path("/metric-registration")
public class MetricRegistrationEndpoint {

    @Inject
    private MetricRegistrationService metricRegistrationService;

    @Inject
    private ReadPredefinedTypesService readPredefinedTypesService;


    public MetricRegistrationEndpoint(MetricRegistrationService metricRegistrationService) {
        this.metricRegistrationService = metricRegistrationService;
    }


    @Tag(name = "Submit Metric Registration.")
    @Operation(
            operationId = "submit-metric-registration",
            summary = "Records a new Metric Registration.",
            description = "Retrieves and inserts a Metric Registration into the database. Typically, " +
                    "a Metric Registration is the metadata describing a Virtual Access Metric. " +
                    "It should be noted that the combination of unit_type and metric_name should be unique. " +
                    "If you execute a request with a unit_type and metric_name, which have already been generated, you receive an error response. " +
                    "The unit_type is a predefined value and you can retrieve " +
                    "[here](#/Get%20Unit%20Types./unit-type) the " +
                    "possible values of unit type. " +
                    "The metric_type is also a predefined value and you can retrieve " +
                    "[here](#/Get%20Metric%20Types./metric-type) the " +
                    "possible values of metric type.")
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
            responseCode = "404",
            description = "Unit/Metric type not found.",
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

        if(readPredefinedTypesService.searchForUnitType(metricRegistrationDtoRequest.unitType).isEmpty()){

            throw new NotFoundException("There is no unit type : " + metricRegistrationDtoRequest.unitType);
        }

        if(readPredefinedTypesService.searchForMetricType(metricRegistrationDtoRequest.metricType).isEmpty()){

            throw new NotFoundException("There is no metric type : " + metricRegistrationDtoRequest.metricType);
        }

        var response = metricRegistrationService.save(metricRegistrationDtoRequest);

        return Response.created(uriInfo.getAbsolutePathBuilder().path(response.id).build()).entity(response).build();
    }

    @Tag(name = "Retrieve all Metric Registrations.")
    @Operation(
            summary = "Returns the recorded Metric Registrations.",
            description = "This operation fetches all database records of Metric Registration.")
    @APIResponse(
            responseCode = "200",
            description = "Array of Metric Registrations.",
            content = @Content(schema = @Schema(
                    type = SchemaType.ARRAY,
                    implementation = MetricRegistrationDtoResponse.class)))
    @APIResponse(
            responseCode = "401",
            description = "User has not been authenticated.",
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
    @Produces(value = MediaType.APPLICATION_JSON)
    public Response getAll(){

        return Response.ok().entity(metricRegistrationService.fetchAllMetricRegistrations()).build();
    }

    @Tag(name = "Search a Metric Registration.")
    @Operation(
            summary = "Returns an existing Metric Registration.",
            description = "This operation accepts the id of a Metric Registration and fetches from the database the corresponding record.")
    @APIResponse(
            responseCode = "200",
            description = "The corresponding Metric Registration.",
            content = @Content(schema = @Schema(
                    type = SchemaType.OBJECT,
                    implementation = MetricRegistrationDtoResponse.class)))
    @APIResponse(
            responseCode = "401",
            description = "User has not been authenticated.",
            content = @Content(schema = @Schema(
                    type = SchemaType.OBJECT,
                    implementation = InformativeResponse.class)))
    @APIResponse(
            responseCode = "404",
            description = "Metric Registration has not been found.",
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
    public Response get(
            @Parameter(
                    description = "The Metric Registration to be retrieved.",
                    required = true,
                    example = "507f1f77bcf86cd799439011",
                    schema = @Schema(type = SchemaType.STRING))
            @PathParam("id") String id){

        Optional<MetricRegistrationDtoResponse> response = metricRegistrationService.fetchMetricRegistration(id);

        return response
                .map(dto->Response.ok().entity(response.get()).build())
                .orElseThrow(()-> new NotFoundException("The Metric Registration has not been found."));
    }

    @Tag(name = "Edit Metric Registration.")
    @Operation(
            summary = "Updates an existing Metric Registration.",
            description = "This operation lets you update only a part of a Metric Registration by updating the existing attributes.")
    @APIResponse(
            responseCode = "200",
            description = "Metric Registration was updated successfully.",
            content = @Content(schema = @Schema(
                    type = SchemaType.OBJECT,
                    implementation = MetricRegistrationDtoResponse.class)))
    @APIResponse(
            responseCode = "401",
            description = "User has not been authenticated.",
            content = @Content(schema = @Schema(
                    type = SchemaType.OBJECT,
                    implementation = InformativeResponse.class)))
    @APIResponse(
            responseCode = "404",
            description = "Metric Registration has not been found.",
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

    @PATCH
    @Path("/{id}")
    @Produces(value = MediaType.APPLICATION_JSON)
    @Consumes(value = MediaType.APPLICATION_JSON)
    public Response update(
            @Parameter(
                    description = "The Metric Registration to be updated.",
                    required = true,
                    example = "507f1f77bcf86cd799439011",
                    schema = @Schema(type = SchemaType.STRING))
            @PathParam("id") String id, UpdateMetricRegistrationDtoRequest updateMetricRegistrationRequest){


        MetricRegistrationDtoResponse response = metricRegistrationService.update(id, updateMetricRegistrationRequest);

        return Response.ok().entity(response).build();

    }

    @Tag(name = "Get Unit Types.")
    @Operation(
            operationId = "unit-type",
            summary = "Returns the unit types.",
            description = "The unit type is an attribute of Metric Registration and defines the unit of a Metric." +
                    " This operation reads a file containing the possible unit types and returns them as a JSON structure.")
    @APIResponse(
            responseCode = "200",
            description = "Successful operation.",
            content = @Content(schema = @Schema(
                    type = SchemaType.OBJECT,
                    implementation = String.class,
            example = "{\n" +
                    "    \"weight\": [\n" +
                    "        {\n" +
                    "            \"name\": \"kg\",\n" +
                    "            \"description\": \"kilogram\"\n" +
                    "        },\n" +
                    "        {\n" +
                    "            \"name\": \"gr\",\n" +
                    "            \"description\": \"gram\"\n" +
                    "        }\n" +
                    "    ],\n" +
                    "    \"time\": [\n" +
                    "        {\n" +
                    "            \"name\": \"s\",\n" +
                    "            \"description\": \"second\"\n" +
                    "        }\n" +
                    "    ]\n" +
                    "}")))
    @APIResponse(
            responseCode = "401",
            description = "User has not been authenticated.",
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
    @Path("/unit-types")
    @Produces(value = MediaType.APPLICATION_JSON)
    public Response getUnitTypes() {

        String json = readPredefinedTypesService.getUnitTypes();

        return Response.ok().entity(json).build();
    }

    @Tag(name = "Get Metric Types.")
    @Operation(
            operationId = "metric-type",
            summary = "Returns the metric types.",
            description = "The metric type is an attribute of Metric Registration and defines the metric type of a Metric." +
                    " This operation reads a file containing the possible metric types and returns them as a JSON structure.")
    @APIResponse(
            responseCode = "200",
            description = "Successful operation.",
            content = @Content(schema = @Schema(
                    type = SchemaType.OBJECT,
                    implementation = String.class,
                    example = "{\n" +
                            "    \"metric_types\": [\n" +
                            "        {\n" +
                            "            \"metric_type\": \"aggregated\"\n" +
                            "        },\n" +
                            "        {\n" +
                            "            \"metric_type\": \"count\"\n" +
                            "        }\n" +
                            "    ]\n" +
                            "}")))
    @APIResponse(
            responseCode = "401",
            description = "User has not been authenticated.",
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
    @Path("/metric-types")
    @Produces(value = MediaType.APPLICATION_JSON)
    public Response getMetricTypes() {

        String json = readPredefinedTypesService.getMetricTypes();

        return Response.ok().entity(json).build();
    }

}
