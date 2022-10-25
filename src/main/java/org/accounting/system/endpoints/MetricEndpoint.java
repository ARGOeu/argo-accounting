package org.accounting.system.endpoints;

import io.quarkus.security.Authenticated;
import org.accounting.system.dtos.InformativeResponse;
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
import org.jboss.resteasy.specimpl.ResteasyUriInfo;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.text.ParseException;

import static org.eclipse.microprofile.openapi.annotations.enums.ParameterIn.QUERY;

@Path("/metrics")
@Authenticated
@SecurityScheme(securitySchemeName = "Authentication",
        description = "JWT token",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER)

public class MetricEndpoint {

    @Inject
    MetricService metricService;

    @ConfigProperty(name = "quarkus.resteasy.path")
    String basePath;

    @ConfigProperty(name = "server.url")
    String serverUrl;

    public MetricEndpoint(MetricService metricService) {
        this.metricService = metricService;
    }

    @Tag(name = "Metric")
    @org.eclipse.microprofile.openapi.annotations.Operation(
            operationId = "search-metric",
            summary = "Search for Metrics.",
            description = "Search for metrics ")
    @APIResponse(
            responseCode = "201",
            description = "Metrics  have been filtered successfully.",
            content = @Content(schema = @Schema(
                    type = SchemaType.OBJECT,
                    implementation = ProjectEndpoint.PageableMetricProjection.class)))
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
    ) String json, @Parameter(name = "page", in = QUERY,
            description = "Indicates the page number. Page number must be >= 1.") @DefaultValue("1") @Min(value = 1, message = "Page number must be >= 1.") @QueryParam("page") int page,
                           @Parameter(name = "size", in = QUERY,
                                   description = "The page size.") @DefaultValue("10") @Min(value = 1, message = "Page size must be between 1 and 100.")
                           @Max(value = 100, message = "Page size must be between 1 and 100.")  @QueryParam("size") int size, @Context UriInfo uriInfo) throws NoSuchFieldException, ParseException, org.json.simple.parser.ParseException {

        var serverInfo = new ResteasyUriInfo(serverUrl.concat(basePath).concat(uriInfo.getPath()), basePath);

        var list=metricService.searchMetrics(json, page - 1, size, serverInfo);
        return Response.ok().entity(list).build();

    }
}
