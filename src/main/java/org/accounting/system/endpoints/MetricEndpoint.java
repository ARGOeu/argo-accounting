package org.accounting.system.endpoints;

import io.quarkus.security.Authenticated;
import org.accounting.system.constraints.NotFoundEntity;
import org.accounting.system.dtos.InformativeResponse;
import org.accounting.system.dtos.metric.MetricResponseDto;
import org.accounting.system.dtos.metric.UpdateMetricRequestDto;
import org.accounting.system.dtos.metricdefinition.MetricDefinitionResponseDto;
import org.accounting.system.enums.Operation;
import org.accounting.system.enums.Collection;
import org.accounting.system.interceptors.annotations.AccessPermission;
import org.accounting.system.repositories.metric.MetricRepository;
import org.accounting.system.services.MetricService;
import org.eclipse.microprofile.config.inject.ConfigProperty;
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
import org.json.simple.parser.ParseException;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PATCH;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/metrics")
@Authenticated
@SecurityScheme(securitySchemeName = "Authentication",
        description = "JWT token",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER)

public class MetricEndpoint {

    @ConfigProperty(name = "quarkus.resteasy.path")
    String basePath;

    @ConfigProperty(name = "server.url")
    String serverUrl;

    @Inject
    MetricService metricService;

    public MetricEndpoint(MetricService metricService) {
        this.metricService = metricService;
    }


    @Tag(name = "Metric")
    @org.eclipse.microprofile.openapi.annotations.Operation(
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
    @Path("/{id}")
    @Produces(value = MediaType.APPLICATION_JSON)
    @AccessPermission(collection = Collection.Metric, operation = Operation.READ)
    public Response get(
            @Parameter(
                    description = "The Metric to be retrieved.",
                    required = true,
                    example = "507f1f77bcf86cd799439011",
                    schema = @Schema(type = SchemaType.STRING))
            @PathParam("id") @Valid @NotFoundEntity(repository = MetricRepository.class, message = "There is no Metric with the following id:") String id){

        MetricResponseDto response = metricService.fetchMetric(id);

        return Response.ok().entity(response).build();
    }

    @Tag(name = "Metric")
    @org.eclipse.microprofile.openapi.annotations.Operation(
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
                    type = SchemaType.ARRAY,
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
    @AccessPermission(collection = Collection.Metric, operation = Operation.DELETE)
    public Response delete(@Parameter(
            description = "The Metric to be deleted.",
            required = true,
            example = "507f1f77bcf86cd799439011",
            schema = @Schema(type = SchemaType.STRING))
                           @PathParam("id") @Valid @NotFoundEntity(repository = MetricRepository.class, message = "There is no Metric with the following id:") String id) {


        boolean success = metricService.delete(id);

        var successResponse = new InformativeResponse();

        if(success){
            successResponse.code = 200;
            successResponse.message = "Metric has been deleted successfully.";
        } else {
            successResponse.code = 500;
            successResponse.message = "Metric cannot be deleted due to a server issue. Please try again.";
        }
        return Response.ok().entity(successResponse).build();
    }

    @Tag(name = "Metric")
    @org.eclipse.microprofile.openapi.annotations.Operation(
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
    @Path("/{id}")
    @Produces(value = MediaType.APPLICATION_JSON)
    @Consumes(value = MediaType.APPLICATION_JSON)
    @AccessPermission(collection = Collection.Metric, operation = Operation.UPDATE)
    public Response update(
            @Parameter(
                 description = "The Metric to be updated.",
                    required = true,
                    example = "61dbe3f10086512c9ff1197a",
                    schema = @Schema(type = SchemaType.STRING))
            @PathParam("id") @Valid @NotFoundEntity(repository = MetricRepository.class, message = "There is no Metric with the following id:") String id, @Valid @NotNull(message = "The request body is empty.") UpdateMetricRequestDto updateMetricRequestDto){

        MetricResponseDto response = metricService.update(id, updateMetricRequestDto);
        return Response.ok().entity(response).build();
    }




    @Tag(name = "Metric")
    @org.eclipse.microprofile.openapi.annotations.Operation(
            operationId = "search-metric",
            summary = "Searches a new Metric.",
            description = "Searches a metric ")
    @APIResponse(
            responseCode = "201",
            description = "Metric  has been filtered successfully.",
            content = @Content(schema = @Schema(
                    type = SchemaType.OBJECT,
                    implementation = MetricDefinitionResponseDto.class)))
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

    public Response search(@Valid @NotNull(message = "The request body is empty.") @RequestBody(            content = @Content(
            schema = @Schema(implementation = String.class),
            mediaType = MediaType.APPLICATION_JSON,
            examples = {
                    @ExampleObject(
                            name = "An example request of a search on metrics",
                            value ="{\n" +
                                    "           \"type\":\"query\",\n" +
                                    "           \"field\": \"time_period_start\",\n" +
                                    "           \"values\": \"2022-01-05T09:13:07Z\",\n" +
                                    "           \"operand\": \"gte\"\n" +
                                    "}\n",
                            summary = "A simple search on a  specific field of the metric"),
                    @ExampleObject(
                            name = "An example request with a combination of criteria of a search on metrics",
                            value = "{\n" +
                                    "  \"type\": \"filter\",\n" +
                                    "  \"operator\": \"OR\",\n" +
                                    "  \"criteria\": [\n" +
                                    "    {\n" +
                                    "      \"type\": \"query\",\n" +
                                    "      \"field\": \"value\",\n" +
                                    "      \"values\": 60,\n" +
                                    "      \"operand\": lt\n" +
                                    "    },\n" +
                                    "    {\n" +
                                    "      \"type\": \"filter\",\n" +
                                    "      \"operator\": \"AND\",\n" +
                                    "      \"criteria\": [\n" +
                                    "        {\n" +
                                    "          \"type\": \"query\",\n" +
                                    "          \"field\": \"time_period_start\",\n" +
                                    "          \"values\": \"2022-01-05T09:13:07Z\",\n" +
                                    "          \"operand\": \"gte\"\n" +
                                    "        },\n" +
                                    "        {\n" +
                                    "          \"type\": \"query\",\n" +
                                    "          \"field\": \"time_period_end\",\n" +
                                    "          \"values\": \"2022-10-05T09:15:07Z\",\n" +
                                    "          \"operand\": \"lt\"\n" +
                                    "        }\n" +
                                    "      ]\n" +
                                    "    }\n" +
                                    "  ]\n" +
                                    "}\n",
                            summary = "A complex search on Metrics ") })
    ) String json) throws  NoSuchFieldException, ParseException {



        var list=metricService.searchMetric(json);
        return Response.ok().entity(list).build();

    }


}
