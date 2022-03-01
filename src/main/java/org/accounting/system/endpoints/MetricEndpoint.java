package org.accounting.system.endpoints;

import io.quarkus.security.Authenticated;
import org.accounting.system.constraints.MetricNotFound;
import org.accounting.system.dtos.InformativeResponse;
import org.accounting.system.dtos.MetricDefinitionResponseDto;
import org.accounting.system.dtos.MetricRequestDto;
import org.accounting.system.dtos.MetricResponseDto;
import org.accounting.system.dtos.UpdateMetricRequestDto;
import org.accounting.system.services.MetricService;
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
import org.jboss.resteasy.specimpl.ResteasyUriInfo;

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
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

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
    @SecurityRequirement(name = "Authentication")

    @POST
    @Produces(value = MediaType.APPLICATION_JSON)
    @Consumes(value = MediaType.APPLICATION_JSON)
    public Response save(@Valid @NotNull(message = "The request body is empty.") MetricRequestDto metricRequestDto, @Context UriInfo uriInfo) {

        MetricResponseDto response = metricService.save(metricRequestDto);

        UriInfo serverInfo = new ResteasyUriInfo(serverUrl.concat(basePath).concat(uriInfo.getPath()), basePath);

        return Response.created(serverInfo.getAbsolutePathBuilder().path(response.id).build()).entity(response).build();
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
    @SecurityRequirement(name = "Authentication")

    @GET
    @Path("/{id}")
    @Produces(value = MediaType.APPLICATION_JSON)
    public Response get(
            @Parameter(
                    description = "The Metric to be retrieved.",
                    required = true,
                    example = "507f1f77bcf86cd799439011",
                    schema = @Schema(type = SchemaType.STRING))
            @PathParam("id") @Valid @MetricNotFound String id){

        MetricResponseDto response = metricService.fetchMetric(id);

        return Response.ok().entity(response).build();
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
                    implementation = MetricDefinitionResponseDto.class)))
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
    public Response delete(@Parameter(
            description = "The Metric to be deleted.",
            required = true,
            example = "507f1f77bcf86cd799439011",
            schema = @Schema(type = SchemaType.STRING))
                           @PathParam("id") @Valid @MetricNotFound String id) {


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
            description = "User has not been authenticated.",
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
    public Response update(
            @Parameter(
                    description = "The Metric to be updated.",
                    required = true,
                    example = "61dbe3f10086512c9ff1197a",
                    schema = @Schema(type = SchemaType.STRING))
            @PathParam("id") @Valid @MetricNotFound String id, @Valid @NotNull(message = "The request body is empty.") UpdateMetricRequestDto updateMetricRequestDto){

        MetricResponseDto response = metricService.update(id, updateMetricRequestDto);
        return Response.ok().entity(response).build();
    }
}
