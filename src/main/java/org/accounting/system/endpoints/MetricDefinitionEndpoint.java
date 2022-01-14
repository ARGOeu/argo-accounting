package org.accounting.system.endpoints;

import org.accounting.system.dtos.InformativeResponse;
import org.accounting.system.dtos.MetricDefinitionDtoRequest;
import org.accounting.system.dtos.MetricDefinitionDtoResponse;
import org.accounting.system.dtos.UpdateMetricDefinitionDtoRequest;
import org.accounting.system.mappers.MetricDefinitionMapper;
import org.accounting.system.services.MetricDefinitionService;
import org.accounting.system.services.ReadPredefinedTypesService;
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
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.PATCH;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.Optional;

@Path("/metric-definition")
public class MetricDefinitionEndpoint {

    @Inject
    MetricDefinitionService metricDefinitionService;

    @Inject
    ReadPredefinedTypesService readPredefinedTypesService;

    @Inject
    Predicates predicates;


    public MetricDefinitionEndpoint(MetricDefinitionService metricDefinitionService, ReadPredefinedTypesService readPredefinedTypesService, Predicates predicates) {
        this.metricDefinitionService = metricDefinitionService;
        this.readPredefinedTypesService = readPredefinedTypesService;
        this.predicates = predicates;
    }


    @Tag(name = "Submit Metric Definition.")
    @Operation(
            operationId = "submit-metric-definition",
            summary = "Records a new Metric Definition.",
            description = "Retrieves and inserts a Metric Definition into the database. Typically, " +
                    "a Metric Definition is the metadata describing a Virtual Access Metric. " +
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
            description = "Metric Definition has been created successfully.",
            content = @Content(schema = @Schema(
                    type = SchemaType.OBJECT,
                    implementation = MetricDefinitionDtoResponse.class)))
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

    @POST
    @Produces(value = MediaType.APPLICATION_JSON)
    @Consumes(value = MediaType.APPLICATION_JSON)
    public Response save(@Valid MetricDefinitionDtoRequest metricDefinitionDtoRequest, @Context UriInfo uriInfo) {

        predicates
                .emptyRequestBody(metricDefinitionDtoRequest)
                .andThen(predicates::exist)
                .andThen(predicates::noAvailableUnitType)
                .andThen(predicates::noAvailableMetricType)
                .accept(metricDefinitionDtoRequest);

        var response = metricDefinitionService.save(metricDefinitionDtoRequest);

        return Response.created(uriInfo.getAbsolutePathBuilder().path(response.id).build()).entity(response).build();
    }

    @Tag(name = "Retrieve all Metric Definitions.")
    @Operation(
            summary = "Returns the recorded Metric Definitions.",
            description = "This operation fetches all database records of Metric Definition.")
    @APIResponse(
            responseCode = "200",
            description = "Array of Metric Definitions.",
            content = @Content(schema = @Schema(
                    type = SchemaType.ARRAY,
                    implementation = MetricDefinitionDtoResponse.class)))
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

        return Response.ok().entity(metricDefinitionService.fetchAllMetricDefinitions()).build();
    }

    @Tag(name = "Search a Metric Definition.")
    @Operation(
            summary = "Returns an existing Metric Definition.",
            description = "This operation accepts the id of a Metric Definition and fetches from the database the corresponding record.")
    @APIResponse(
            responseCode = "200",
            description = "The corresponding Metric Definition.",
            content = @Content(schema = @Schema(
                    type = SchemaType.OBJECT,
                    implementation = MetricDefinitionDtoResponse.class)))
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
                    description = "The Metric Definition to be retrieved.",
                    required = true,
                    example = "507f1f77bcf86cd799439011",
                    schema = @Schema(type = SchemaType.STRING))
            @PathParam("id") String id){

        Optional<MetricDefinitionDtoResponse> response = metricDefinitionService.fetchMetricDefinition(id);

        return response
                .map(dto->Response.ok().entity(response.get()).build())
                .orElseThrow(()-> new NotFoundException("The Metric Definition has not been found."));
    }

    @Tag(name = "Edit Metric Definition.")
    @Operation(
            summary = "Updates an existing Metric Definition.",
            description = "This operation lets you update only a part of a Metric Definition by updating the existing attributes. " +
                    "The empty or null values are ignored.")
    @APIResponse(
            responseCode = "200",
            description = "Metric Definition was updated successfully.",
            content = @Content(schema = @Schema(
                    type = SchemaType.OBJECT,
                    implementation = MetricDefinitionDtoResponse.class)))
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

    @PATCH
    @Path("/{id}")
    @Produces(value = MediaType.APPLICATION_JSON)
    @Consumes(value = MediaType.APPLICATION_JSON)
    public Response update(
            @Parameter(
                    description = "The Metric Definition to be updated.",
                    required = true,
                    example = "507f1f77bcf86cd799439011",
                    schema = @Schema(type = SchemaType.STRING))
            @PathParam("id") String id, UpdateMetricDefinitionDtoRequest updateMetricDefinitionRequest){

        var transformedUpdateDto = MetricDefinitionMapper.INSTANCE.updateRequestToMetricDefinitionDtoRequest(updateMetricDefinitionRequest);

        predicates
                .emptyRequestBody(transformedUpdateDto)
                .andThen(predicates::noAvailableUnitType)
                .andThen(predicates::noAvailableMetricType)
                .accept(transformedUpdateDto);

        MetricDefinitionDtoResponse response = metricDefinitionService.update(id, updateMetricDefinitionRequest);

        return Response.ok().entity(response).build();
    }

    @Tag(name = "Delete Metric Definition.")
    @Operation(
            summary = "Deletes an existing Metric Definition.",
            description = "You can delete only a Metric Definition that doesn’t have any assigned Metrics to it. If the Metric Definition has no Metrics, you can safely delete it.")
    @APIResponse(
            responseCode = "200",
            description = "Metric Definition has been deleted successfully.",
            content = @Content(schema = @Schema(
                    type = SchemaType.OBJECT,
                    implementation = MetricDefinitionDtoResponse.class)))
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
                    type = SchemaType.ARRAY,
                    implementation = InformativeResponse.class)))
    @APIResponse(
            responseCode = "500",
            description = "Internal Server Errors.",
            content = @Content(schema = @Schema(
                    type = SchemaType.OBJECT,
                    implementation = InformativeResponse.class)))

    @DELETE()
    @Path("/{id}")
    @Produces(value = MediaType.APPLICATION_JSON)
    public Response delete(@Parameter(
            description = "The Metric Definition to be deleted.",
            required = true,
            example = "507f1f77bcf86cd799439011",
            schema = @Schema(type = SchemaType.STRING))
            @PathParam("id") String id) {

        boolean success = metricDefinitionService.delete(id);

        var successResponse = new InformativeResponse();

        if(success){
            successResponse.code = 200;
            successResponse.message = "Metric Definition has been deleted successfully.";
        } else {
            successResponse.code = 500;
            successResponse.message = "Metric Definition cannot be deleted due to a server issue. Please try again.";
        }
        return Response.ok().entity(successResponse).build();
    }

    @Tag(name = "Get Unit Types.")
    @Operation(
            operationId = "unit-type",
            summary = "Returns the unit types.",
            description = "The unit type is an attribute of Metric Definition and defines the unit of a Metric." +
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
            description = "The metric type is an attribute of Metric Definition and defines the metric type of a Metric." +
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
