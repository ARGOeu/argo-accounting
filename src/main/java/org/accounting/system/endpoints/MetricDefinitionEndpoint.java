package org.accounting.system.endpoints;

import io.quarkus.security.Authenticated;
import org.accounting.system.constraints.NotFoundEntity;
import org.accounting.system.dtos.InformativeResponse;
import org.accounting.system.dtos.MetricDefinitionRequestDto;
import org.accounting.system.dtos.MetricDefinitionResponseDto;
import org.accounting.system.dtos.PageResource;
import org.accounting.system.dtos.UpdateMetricDefinitionRequestDto;
import org.accounting.system.dtos.acl.AccessControlRequestDto;
import org.accounting.system.dtos.acl.AccessControlUpdateDto;
import org.accounting.system.enums.Collection;
import org.accounting.system.exceptions.UnprocessableException;
import org.accounting.system.interceptors.annotations.Permission;
import org.accounting.system.repositories.metricdefinition.MetricDefinitionRepository;
import org.accounting.system.services.MetricDefinitionService;
import org.accounting.system.services.ReadPredefinedTypesService;
import org.accounting.system.util.Utility;
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
import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.PATCH;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import static org.eclipse.microprofile.openapi.annotations.enums.ParameterIn.QUERY;

@Path("/metric-definition")
@Authenticated
@SecurityScheme(securitySchemeName = "Authentication",
        description = "JWT token",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER)

public class MetricDefinitionEndpoint {

    @ConfigProperty(name = "quarkus.resteasy.path")
    String basePath;

    @ConfigProperty(name = "server.url")
    String serverUrl;

    @Inject
    MetricDefinitionService metricDefinitionService;

    @Inject
    ReadPredefinedTypesService readPredefinedTypesService;

    @Inject
    Utility utility;

    public MetricDefinitionEndpoint(MetricDefinitionService metricDefinitionService, ReadPredefinedTypesService readPredefinedTypesService, Utility utility) {
        this.metricDefinitionService = metricDefinitionService;
        this.readPredefinedTypesService = readPredefinedTypesService;
        this.utility = utility;
    }


    @Tag(name = "Metric Definition")
    @Operation(
            operationId = "submit-metric-definition",
            summary = "Records a new Metric Definition.",
            description = "Retrieves and inserts a Metric Definition into the database. Typically, " +
                    "a Metric Definition is the metadata describing a Virtual Access Metric. " +
                    "It should be noted that the combination of unit_type and metric_name should be unique. " +
                    "If you execute a request with a unit_type and metric_name, which have already been generated, you receive an error response. " +
                    "The unit_type is a predefined value and you can retrieve " +
                    "[here](#/Unit%20Type/unit-type) the " +
                    "possible values of unit type. " +
                    "The metric_type is also a predefined value and you can retrieve " +
                    "[here](#/Metric%20Type/metric-type) the " +
                    "possible values of metric type.")
    @APIResponse(
            responseCode = "201",
            description = "Metric Definition has been created successfully.",
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
            description = "User/Service has not been authenticated.",
            content = @Content(schema = @Schema(
                    type = SchemaType.OBJECT,
                    implementation = InformativeResponse.class)))
    @APIResponse(
            responseCode = "403",
            description = "The authenticated user/service is not permitted to perform the requested operation.",
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
    @SecurityRequirement(name = "Authentication")

    @POST
    @Produces(value = MediaType.APPLICATION_JSON)
    @Consumes(value = MediaType.APPLICATION_JSON)
    @Permission(collection = Collection.MetricDefinition, operation = org.accounting.system.enums.Operation.CREATE)
    public Response save(@Valid @NotNull(message = "The request body is empty.") MetricDefinitionRequestDto metricDefinitionRequestDto, @Context UriInfo uriInfo) {

        UriInfo serverInfo = new ResteasyUriInfo(serverUrl.concat(basePath).concat(uriInfo.getPath()), basePath);

        utility.exist(metricDefinitionRequestDto);

        var response = metricDefinitionService.save(metricDefinitionRequestDto);

        return Response.created(serverInfo.getAbsolutePathBuilder().path(response.id).build()).entity(response).build();
    }

    @Tag(name = "Metric Definition")
    @Operation(
            summary = "Returns the recorded Metric Definitions.",
            description = "This operation fetches all database records of Metric Definition.")
    @APIResponse(
            responseCode = "200",
            description = "Array of Metric Definitions.",
            content = @Content(schema = @Schema(
                    type = SchemaType.ARRAY,
                    implementation = MetricDefinitionResponseDto.class)))
    @APIResponse(
            responseCode = "401",
            description = "User/Service has not been authenticated.",
            content = @Content(schema = @Schema(
                    type = SchemaType.OBJECT,
                    implementation = InformativeResponse.class)))
    @APIResponse(
            responseCode = "403",
            description = "The authenticated user/service is not permitted to perform the requested operation.",
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
    @Permission(collection = Collection.MetricDefinition, operation = org.accounting.system.enums.Operation.READ)
    public Response getAll(){

        return Response.ok().entity(metricDefinitionService.fetchAllMetricDefinitions()).build();
    }

    @Tag(name = "Metric Definition")
    @Operation(
            summary = "Returns an existing Metric Definition.",
            description = "This operation accepts the id of a Metric Definition and fetches from the database the corresponding record.")
    @APIResponse(
            responseCode = "200",
            description = "The corresponding Metric Definition.",
            content = @Content(schema = @Schema(
                    type = SchemaType.OBJECT,
                    implementation = MetricDefinitionResponseDto.class)))
    @APIResponse(
            responseCode = "401",
            description = "User/Service has not been authenticated.",
            content = @Content(schema = @Schema(
                    type = SchemaType.OBJECT,
                    implementation = InformativeResponse.class)))
    @APIResponse(
            responseCode = "403",
            description = "The authenticated user/service is not permitted to perform the requested operation.",
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
    @SecurityRequirement(name = "Authentication")

    @GET
    @Path("/{id}")
    @Produces(value = MediaType.APPLICATION_JSON)
    @Permission(collection = Collection.MetricDefinition, operation = org.accounting.system.enums.Operation.READ)
    public Response get(
            @Parameter(
                    description = "The Metric Definition to be retrieved.",
                    required = true,
                    example = "507f1f77bcf86cd799439011",
                    schema = @Schema(type = SchemaType.STRING))
            @PathParam("id") @Valid @NotFoundEntity(repository = MetricDefinitionRepository.class, message = "There is no Metric Definition with the following id:") String id){

        MetricDefinitionResponseDto response = metricDefinitionService.fetchMetricDefinition(id);

        return Response.ok().entity(response).build();
    }

    @Tag(name = "Metric Definition")
    @Operation(
            summary = "Updates an existing Metric Definition.",
            description = "In order to update the resource properties, the body of the request must contain an updated representation of Metric Definition. " +
                    "You can update a part or all attributes of the Metric Definition except for metric_definition_id. The empty or null values are ignored.")
    @APIResponse(
            responseCode = "200",
            description = "Metric Definition was updated successfully.",
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
            description = "User/Service has not been authenticated.",
            content = @Content(schema = @Schema(
                    type = SchemaType.OBJECT,
                    implementation = InformativeResponse.class)))
    @APIResponse(
            responseCode = "403",
            description = "The authenticated user/service is not permitted to perform the requested operation.",
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
    @SecurityRequirement(name = "Authentication")

    @PATCH
    @Path("/{id}")
    @Produces(value = MediaType.APPLICATION_JSON)
    @Consumes(value = MediaType.APPLICATION_JSON)
    @Permission(collection = Collection.MetricDefinition, operation = org.accounting.system.enums.Operation.UPDATE)
    public Response update(
            @Parameter(
                    description = "The Metric Definition to be updated.",
                    required = true,
                    example = "507f1f77bcf86cd799439011",
                    schema = @Schema(type = SchemaType.STRING))
            @PathParam("id") @Valid @NotFoundEntity(repository = MetricDefinitionRepository.class, message = "There is no Metric Definition with the following id:") String id, @Valid @NotNull(message = "The request body is empty.") UpdateMetricDefinitionRequestDto updateMetricDefinitionRequest){

        MetricDefinitionResponseDto response = metricDefinitionService.update(id, updateMetricDefinitionRequest);

        return Response.ok().entity(response).build();
    }

    @Tag(name = "Metric Definition")
    @Operation(
            summary = "Deletes an existing Metric Definition.",
            description = "You can delete only a Metric Definition that doesn’t have any assigned Metrics to it. If the Metric Definition has no Metrics, you can safely delete it.")
    @APIResponse(
            responseCode = "200",
            description = "Metric Definition has been deleted successfully.",
            content = @Content(schema = @Schema(
                    type = SchemaType.OBJECT,
                    implementation = InformativeResponse.class)))
    @APIResponse(
            responseCode = "401",
            description = "User/Service has not been authenticated.",
            content = @Content(schema = @Schema(
                    type = SchemaType.OBJECT,
                    implementation = InformativeResponse.class)))
    @APIResponse(
            responseCode = "403",
            description = "The authenticated user/service is not permitted to perform the requested operation.",
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
    @SecurityRequirement(name = "Authentication")

    @DELETE()
    @Path("/{id}")
    @Produces(value = MediaType.APPLICATION_JSON)
    @Permission(collection = Collection.MetricDefinition, operation = org.accounting.system.enums.Operation.DELETE)
    public Response delete(@Parameter(
            description = "The Metric Definition to be deleted.",
            required = true,
            example = "507f1f77bcf86cd799439011",
            schema = @Schema(type = SchemaType.STRING))
            @PathParam("id") @Valid @NotFoundEntity(repository = MetricDefinitionRepository.class, message = "There is no Metric Definition with the following id:") String id) {

        metricDefinitionService.hasChildren(id);

        var success = metricDefinitionService.delete(id);

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

    @Tag(name = "Unit Type")
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
            description = "User/Service has not been authenticated.",
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
    @Path("/unit-types")
    @Produces(value = MediaType.APPLICATION_JSON)
    public Response getUnitTypes() {

        var json = readPredefinedTypesService.getUnitTypes();

        return Response.ok().entity(json).build();
    }

    @Tag(name = "Metric Type")
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
            description = "User/Service has not been authenticated.",
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
    @Path("/metric-types")
    @Produces(value = MediaType.APPLICATION_JSON)
    public Response getMetricTypes() {

        var json = readPredefinedTypesService.getMetricTypes();

        return Response.ok().entity(json).build();
    }

    @Tag(name = "Metric Definition")
    @Operation(
            summary = "Retrieves Metrics for specific Metric Registration.",
            description = "This operation returns the Metrics assigned to a Metric Registration. " +
                    "By default, the first page of 10 entities will be returned. You can tune the default values by using " +
                    "the query parameters page and size. Finally, you cannot request more than 100 items.")
    @APIResponse(
            responseCode = "200",
            description = "Success operation.",
            content = @Content(schema = @Schema(
                    type = SchemaType.OBJECT,
                    implementation = PageResource.class)))
    @APIResponse(
            responseCode = "400",
            description = "Bad Request.",
            content = @Content(schema = @Schema(
                    type = SchemaType.OBJECT,
                    implementation = InformativeResponse.class)))
    @APIResponse(
            responseCode = "401",
            description = "User/Service has not been authenticated.",
            content = @Content(schema = @Schema(
                    type = SchemaType.OBJECT,
                    implementation = InformativeResponse.class)))
    @APIResponse(
            responseCode = "403",
            description = "The authenticated user/service is not permitted to perform the requested operation.",
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
            responseCode = "422",
            description = "You cannot request more than 100 items.",
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
    @Path("/{metric_definition_id}/metrics")
    @Produces(value = MediaType.APPLICATION_JSON)
    @Permission(operation = org.accounting.system.enums.Operation.READ, collection = Collection.MetricDefinition)
    public Response get(@Parameter(
            description = "The Metric Definition id.",
            required = true,
            example = "507f1f77bcf86cd799439011",
            schema = @Schema(type = SchemaType.STRING))
                        @PathParam("metric_definition_id") @Valid @NotFoundEntity(repository = MetricDefinitionRepository.class, message = "There is no Metric Definition with the following id:") String metricDefinitionId,
                        @Parameter(name = "page", in = QUERY,
                                description = "Indicates the page number.") @DefaultValue("1") @QueryParam("page") int page,
                        @Parameter(name = "size", in = QUERY,
                                description = "The page size.") @DefaultValue("10") @QueryParam("size") int size, @Context UriInfo uriInfo) {

        if(page <1){
            throw new BadRequestException("Page index must be >= 1.");
        }

        if(size > 100){
            throw new UnprocessableException("You cannot request more than 100 items.");
        }

        UriInfo serverInfo = new ResteasyUriInfo(serverUrl.concat(basePath).concat(uriInfo.getPath()), basePath);

        var metrics = metricDefinitionService.findMetricsByMetricDefinitionIdPageable(metricDefinitionId, page-1, size, serverInfo);

        return Response.ok().entity(metrics).build();
    }

    @Tag(name = "Metric Definition")
    @Operation(
            summary = "Generates a new Access Control Entry.",
            description = "This endpoint is responsible for generating a new Access Control Entry. " +
                    "Access Control Entry rules specify which services/users are granted or denied access to particular Metric Definition entities. " +
                    "It should be noted that the combination {who, collection, entity} is unique. Therefore, only one Access Control entry can be created for each service/user and each entity.")
    @APIResponse(
            responseCode = "200",
            description = "Access Control entry has been created successfully.",
            content = @Content(schema = @Schema(
                    type = SchemaType.OBJECT,
                    implementation = InformativeResponse.class)))
    @APIResponse(
            responseCode = "400",
            description = "Bad Request.",
            content = @Content(schema = @Schema(
                    type = SchemaType.OBJECT,
                    implementation = InformativeResponse.class)))
    @APIResponse(
            responseCode = "401",
            description = "User/Service has not been authenticated.",
            content = @Content(schema = @Schema(
                    type = SchemaType.OBJECT,
                    implementation = InformativeResponse.class)))
    @APIResponse(
            responseCode = "403",
            description = "The authenticated user/service is not permitted to perform the requested operation.",
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
            description = "There is an Access Control Entry with this {who, collection, entity}.",
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
    @Path("/{metric_definition_id}/acl")
    @Produces(value = MediaType.APPLICATION_JSON)
    @Consumes(value = MediaType.APPLICATION_JSON)
    @Permission(collection = Collection.MetricDefinition, operation = org.accounting.system.enums.Operation.ACL)
    public Response createAccessControl(@Valid @NotNull(message = "The request body is empty.") AccessControlRequestDto accessControlRequestDto,
                                        @Parameter(
                                                description = "metric_definition_id is the id of the entity to which the permissions apply.",
                                                required = true,
                                                example = "507f1f77bcf86cd799439011",
                                                schema = @Schema(type = SchemaType.STRING))
                                        @PathParam("metric_definition_id")
                                        @Valid
                                        @NotFoundEntity(repository = MetricDefinitionRepository.class, message = "There is no Metric Definition with the following id:") String metricDefinitionId) {


        metricDefinitionService.grantPermission(metricDefinitionId, accessControlRequestDto);

        var informativeResponse = new InformativeResponse();
        informativeResponse.message = "Access Control entry has been created successfully";
        informativeResponse.code = 200;

        return Response.ok().entity(informativeResponse).build();
    }

    @Tag(name = "Metric Definition")
    @Operation(
            summary = "Modify an existing Access Control Entry.",
            description = "This endpoint is responsible for updating an existing Access Control Entry. It will modify a specific Access Control Entry " +
                    "which has granted permissions on a Metric Definition to a specific service/user." +
                    "You can update a part or all attributes of the Access Control entity.")
    @APIResponse(
            responseCode = "200",
            description = "Access Control entry has been updated successfully.",
            content = @Content(schema = @Schema(
                    type = SchemaType.OBJECT,
                    implementation = InformativeResponse.class)))
    @APIResponse(
            responseCode = "400",
            description = "Bad Request.",
            content = @Content(schema = @Schema(
                    type = SchemaType.OBJECT,
                    implementation = InformativeResponse.class)))
    @APIResponse(
            responseCode = "401",
            description = "User/Service has not been authenticated.",
            content = @Content(schema = @Schema(
                    type = SchemaType.OBJECT,
                    implementation = InformativeResponse.class)))
    @APIResponse(
            responseCode = "403",
            description = "The authenticated user/service is not permitted to perform the requested operation.",
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
            description = "There is an Access Control Entry with this {who, collection, entity}.",
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
    @Path("/{metric_definition_id}/acl/{who}")
    @Produces(value = MediaType.APPLICATION_JSON)
    @Consumes(value = MediaType.APPLICATION_JSON)
    @Permission(collection = Collection.MetricDefinition, operation = org.accounting.system.enums.Operation.ACL)
    public Response modifyAccessControl(@Valid @NotNull(message = "The request body is empty.") AccessControlUpdateDto accessControlUpdateDto,
                                        @Parameter(
                                                description = "metric_definition_id in which permissions have been granted.",
                                                required = true,
                                                example = "507f1f77bcf86cd799439011",
                                                schema = @Schema(type = SchemaType.STRING))
                                        @PathParam("metric_definition_id")
                                        @Valid
                                        @NotFoundEntity(repository = MetricDefinitionRepository.class, message = "There is no Metric Definition with the following id:") String metricDefinitionId,
                                        @Parameter(
                                                description = "who is the service/user to whom the permissions have been granted.",
                                                required = true,
                                                example = "fbdb4e4a-6e93-4b08-a1e7-0b7bd08520a6",
                                                schema = @Schema(type = SchemaType.STRING))
                                            @PathParam("who") String who) {


        metricDefinitionService.modifyPermission(metricDefinitionId, who, accessControlUpdateDto);

        var informativeResponse = new InformativeResponse();
        informativeResponse.message = "Access Control entry has been updated successfully.";
        informativeResponse.code = 200;

        return Response.ok().entity(informativeResponse).build();
    }

    @Tag(name = "Metric Definition")
    @Operation(
            summary = "Deletes an existing Access Control entry.",
            description = "You can delete the permissions that a service/user can access to manage a specific Metric Definition.")
    @APIResponse(
            responseCode = "200",
            description = "Access Control entry has been deleted successfully.",
            content = @Content(schema = @Schema(
                    type = SchemaType.OBJECT,
                    implementation = InformativeResponse.class)))
    @APIResponse(
            responseCode = "401",
            description = "User/Service has not been authenticated.",
            content = @Content(schema = @Schema(
                    type = SchemaType.OBJECT,
                    implementation = InformativeResponse.class)))
    @APIResponse(
            responseCode = "403",
            description = "The authenticated user/service is not permitted to perform the requested operation.",
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
    @SecurityRequirement(name = "Authentication")

    @DELETE()
    @Path("/{metric_definition_id}/acl/{who}")
    @Produces(value = MediaType.APPLICATION_JSON)
    @Permission(collection = Collection.MetricDefinition, operation = org.accounting.system.enums.Operation.ACL)
    public Response deleteAccessControl(@Parameter(
            description = "metric_definition_id in which permissions have been granted.",
            required = true,
            example = "507f1f77bcf86cd799439011",
            schema = @Schema(type = SchemaType.STRING))
                                            @PathParam("metric_definition_id")
                                            @Valid
                                            @NotFoundEntity(repository = MetricDefinitionRepository.class, message = "There is no Metric Definition with the following id:") String metricDefinitionId,
                                        @Parameter(
                                                description = "who is the service/user to whom the permissions have been granted.",
                                                required = true,
                                                example = "fbdb4e4a-6e93-4b08-a1e7-0b7bd08520a6",
                                                schema = @Schema(type = SchemaType.STRING))
                                            @PathParam("who") String who) {


        metricDefinitionService.deletePermission(metricDefinitionId, who);

        var successResponse = new InformativeResponse();

        successResponse.code = 200;
        successResponse.message = "Access Control entry has been deleted successfully.";

        return Response.ok().entity(successResponse).build();
    }
}