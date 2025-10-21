package org.accounting.system.endpoints;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.quarkus.security.Authenticated;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.ForbiddenException;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotFoundException;
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
import org.accounting.system.beans.RequestUserContext;
import org.accounting.system.constraints.CheckDateFormat;
import org.accounting.system.constraints.NotFoundEntity;
import org.accounting.system.dtos.InformativeResponse;
import org.accounting.system.dtos.pagination.PageResource;
import org.accounting.system.dtos.provider.ProviderRequestDto;
import org.accounting.system.dtos.provider.ProviderResponseDto;
import org.accounting.system.dtos.provider.UpdateProviderRequestDto;
import org.accounting.system.entities.projections.GenericProviderReport;
import org.accounting.system.entities.projections.ProviderProjectionWithProjectInfo;
import org.accounting.system.interceptors.annotations.AccessResource;
import org.accounting.system.repositories.provider.ProviderRepository;
import org.accounting.system.services.EntitlementService;
import org.accounting.system.services.ProviderService;
import org.accounting.system.util.AccountingUriInfo;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.openapi.annotations.Operation;
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

import java.text.ParseException;
import java.time.LocalDate;
import java.util.List;

import static org.eclipse.microprofile.openapi.annotations.enums.ParameterIn.QUERY;

@Path("/providers")
@Authenticated
@SecurityScheme(securitySchemeName = "Authentication",
        description = "JWT token",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER)

public class ProviderEndpoint {

    @ConfigProperty(name = "quarkus.rest.path")
    String basePath;

    @ConfigProperty(name = "api.server.url")
    String serverUrl;

    @Inject
    ProviderService providerService;

    @Inject
    EntitlementService entitlementService;

    @Inject
    RequestUserContext requestInformation;

    @Tag(name = "Provider")
    @Operation(
            operationId = "providers-from-eosc-portal",
            summary = "Get a list of all Providers in the Accounting System.",
            description = "This operation returns all Providers available on Accounting System API. " +
                    "By default, the first page of 10 Providers will be returned. You can tune the default values by using " +
                    "the query parameters page and size.")
    @APIResponse(
            responseCode = "200",
            description = "Success operation.",
            content = @Content(schema = @Schema(
                    type = SchemaType.OBJECT,
                    implementation = PageableProviderResponseDto.class)))
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
            responseCode = "404",
            description = "Not Found.",
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
    @AccessResource(roles = {"admin", "viewer"})
    public Response get(@Parameter(name = "page", in = QUERY,
            description = "Indicates the page number. Page number must be >= 1.") @DefaultValue("1") @Min(value = 1, message = "Page number must be >= 1.") @QueryParam("page") int page,
                        @Parameter(name = "size", in = QUERY,
                                description = "The page size.") @DefaultValue("10") @Min(value = 1, message = "Page size must be between 1 and 100.")
                        @Max(value = 100, message = "Page size must be between 1 and 100.") @QueryParam("size") int size,
                        @Context UriInfo uriInfo) {

        var serverInfo = new AccountingUriInfo(serverUrl.concat(basePath).concat(uriInfo.getPath()));

        var providers = providerService.findAllProvidersPageable(page - 1, size, serverInfo);

        return Response.ok().entity(providers).build();
    }

    @Tag(name = "Provider")
    @Operation(
            operationId = "register-a-new-provider",
            summary = "Registers a new Provider.",
            description = "A Client can use this functionality to create a new Provider.")
    @APIResponse(
            responseCode = "201",
            description = "Provider has been created successfully.",
            content = @Content(schema = @Schema(
                    type = SchemaType.OBJECT,
                    implementation = ProviderResponseDto.class)))
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
    @Produces(value = MediaType.APPLICATION_JSON)
    @Consumes(value = MediaType.APPLICATION_JSON)
    @AccessResource(roles = {"admin"})
    public Response save(@Valid @NotNull(message = "The request body is empty.") ProviderRequestDto providerRequestDto, @Context UriInfo uriInfo) {

        var serverInfo = new AccountingUriInfo(serverUrl.concat(basePath).concat(uriInfo.getPath()));

        providerService.existByName(providerRequestDto.name);

        var response = providerService.save(providerRequestDto, requestInformation.getId());

        return Response.created(serverInfo.getAbsolutePathBuilder().path(response.id).build()).entity(response).build();
    }

    @Tag(name = "Provider")
    @Operation(
            summary = "Deletes an existing Provider.",
            description = "This operation deletes an existing Provider registered through the [Accounting System API](#/Provider/register-a-new-provider). " +
                    "Deleting Providers which derive from the [EOSC-Portal](#/Provider/providers-from-eosc-portal) is not allowed. " +
                    "Bear in mind that you cannot delete a Provider which belongs to a Project.")
    @APIResponse(
            responseCode = "200",
            description = "Provider has been deleted successfully.",
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
            description = "It is not permitted to perform the requested operation.",
            content = @Content(schema = @Schema(
                    type = SchemaType.OBJECT,
                    implementation = InformativeResponse.class)))
    @APIResponse(
            responseCode = "404",
            description = "Provider has not been found.",
            content = @Content(schema = @Schema(
                    type = SchemaType.OBJECT,
                    implementation = InformativeResponse.class)))
    @APIResponse(
            responseCode = "409",
            description = "Provider is not allowed to be deleted.",
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
    @DELETE()
    @Path("/{id}")
    @Produces(value = MediaType.APPLICATION_JSON)
    @AccessResource(roles = {"admin"})
    public Response delete(@Parameter(
            description = "The Provider to be deleted.",
            required = true,
            example = "sites",
            schema = @Schema(type = SchemaType.STRING))
                           @PathParam("id") @Valid @NotFoundEntity(repository = ProviderRepository.class, id = String.class, message = "There is no Provider with the following id:") String id) {

        var success = providerService.delete(id);

        var successResponse = new InformativeResponse();

        if (success) {
            successResponse.code = 200;
            successResponse.message = "Provider has been deleted successfully.";
        } else {
            successResponse.code = 500;
            successResponse.message = "Provider cannot be deleted due to a server issue. Please try again.";
        }
        return Response.ok().entity(successResponse).build();
    }

    @Tag(name = "Provider")
    @Operation(
            summary = "Updates an existing Provider.",
            description = "This operation updates an existing Provider registered through the [Accounting System API](#/Provider/register-a-new-provider). " +
                    "Updating Providers which derive from the [EOSC-Portal](#/Provider/providers-from-eosc-portal) is not allowed. Finally, " +
                    "you can update a part or all attributes of Provider. The empty or null values are ignored. " +
                    "Bear in mind that you cannot update a Provider which belongs to a Project.")
    @APIResponse(
            responseCode = "200",
            description = "Provider was updated successfully.",
            content = @Content(schema = @Schema(
                    type = SchemaType.OBJECT,
                    implementation = ProviderResponseDto.class)))
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
            description = "It is not permitted to perform the requested operation.",
            content = @Content(schema = @Schema(
                    type = SchemaType.OBJECT,
                    implementation = InformativeResponse.class)))
    @APIResponse(
            responseCode = "404",
            description = "Provider has not been found.",
            content = @Content(schema = @Schema(
                    type = SchemaType.OBJECT,
                    implementation = InformativeResponse.class)))
    @APIResponse(
            responseCode = "409",
            description = "The Provider already exists.",
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
    @AccessResource(roles = {"admin"})
    public Response update(
            @Parameter(
                    description = "The Provider to be updated.",
                    required = true,
                    example = "sites",
                    schema = @Schema(type = SchemaType.STRING))
            @PathParam("id") @Valid @NotFoundEntity(repository = ProviderRepository.class, id = String.class, message = "There is no Provider with the following id:") String id, @Valid @NotNull(message = "The request body is empty.") UpdateProviderRequestDto updateProviderRequestDto) {

        var response = providerService.update(id, updateProviderRequestDto);

        return Response.ok().entity(response).build();
    }

    @Tag(name = "Provider")
    @Operation(
            summary = "Returns an existing Provider.",
            description = "This operation accepts the id of a Provider and fetches from the database the corresponding record.")
    @APIResponse(
            responseCode = "200",
            description = "The corresponding Provider.",
            content = @Content(schema = @Schema(
                    type = SchemaType.OBJECT,
                    implementation = ProviderResponseDto.class)))
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
            responseCode = "404",
            description = "Provider has not been found.",
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
    @AccessResource(roles = {"admin", "viewer"})
    public Response get(
            @Parameter(
                    description = "The Provider to be retrieved.",
                    required = true,
                    example = "sites",
                    schema = @Schema(type = SchemaType.STRING))
            @PathParam("id") @Valid @NotFoundEntity(repository = ProviderRepository.class, id = String.class, message = "There is no Provider with the following id:") String id) {

        var response = providerService.fetchProvider(id);

        return Response.ok().entity(response).build();
    }

    @Tag(name = "Provider")
    @Operation(
            summary = "Returns an existing Provider by external id.",
            description = "This operation accepts the external id of a Provider and fetches from the database the corresponding record.")
    @APIResponse(
            responseCode = "200",
            description = "The corresponding Provider.",
            content = @Content(schema = @Schema(
                    type = SchemaType.OBJECT,
                    implementation = ProviderResponseDto.class)))
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
            responseCode = "404",
            description = "Provider has not been found.",
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
    @Path("/external")
    @Produces(value = MediaType.APPLICATION_JSON)
    @AccessResource(roles = {"admin", "viewer"})
    public Response getByExternalId(
            @Parameter(name = "externalId", in = QUERY, required = true, example = "sites", allowReserved = true,
                    description = "The external provider id.", schema = @Schema(type = SchemaType.STRING)) @QueryParam("externalId") @NotEmpty(message = "externalId may not be empty.") String externalId) {

        var response = providerService.fetchProviderByExternal(externalId);

        return Response.ok().entity(response).build();
    }

    @Tag(name = "Provider")
    @Operation(
            summary = "Search",
            description = "Search")
    @APIResponse(
            responseCode = "200",
            description = "The corresponding Providers.",
            content = @Content(schema = @Schema(
                    type = SchemaType.OBJECT,
                    implementation = PageableProviderResponseDto.class)))
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
    @AccessResource(roles = {"admin", "viewer"})
    public Response search(
            @Valid @NotNull(message = "The request body is empty.") @RequestBody(content = @Content(
                    schema = @Schema(implementation = String.class),
                    mediaType = MediaType.APPLICATION_JSON,
                    examples = {
                            @ExampleObject(
                                    name = "An example request of a search on providers",
                                    value = "",
                                    summary = "A complex search on Providers ")})
            ) String json, @Parameter(name = "page", in = QUERY,
            description = "Indicates the page number. Page number must be >= 1.") @DefaultValue("1") @Min(value = 1, message = "Page number must be >= 1.") @QueryParam("page") int page,
            @Parameter(name = "size", in = QUERY,
                    description = "The page size.") @DefaultValue("10") @Min(value = 1, message = "Page size must be between 1 and 100.")
            @Max(value = 100, message = "Page size must be between 1 and 100.") @QueryParam("size") int size, @Context UriInfo uriInfo) throws ParseException, NoSuchFieldException, org.json.simple.parser.ParseException, JsonProcessingException {

        var serverInfo = new AccountingUriInfo(serverUrl.concat(basePath).concat(uriInfo.getPath()));

        var response = providerService.searchProviders(json, page - 1, size, serverInfo);

        return Response.ok().entity(response).build();
    }

    @Tag(name = "Provider")
    @Operation(
            summary = "Get all the assigned providers",
            description = "Get All assigned Providers")
    @APIResponse(
            responseCode = "200",
            description = "The corresponding Providers.",
            content = @Content(schema = @Schema(
                    type = SchemaType.OBJECT,
                    implementation = ProviderProjectionWithProjectInfo.class)))
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
            responseCode = "500",
            description = "Internal Server Errors.",
            content = @Content(schema = @Schema(
                    type = SchemaType.OBJECT,
                    implementation = InformativeResponse.class)))
    @SecurityRequirement(name = "Authentication")
    @GET
    @Path("/all")
    @Produces(value = MediaType.APPLICATION_JSON)
    @Consumes(value = MediaType.APPLICATION_JSON)
    @AccessResource(roles = {"admin", "viewer"})
    public Response getProvidersAssigned(

            @Parameter(name = "page", in = QUERY,
                    description = "Indicates the page number. Page number must be >= 1.") @DefaultValue("1") @Min(value = 1, message = "Page number must be >= 1.") @QueryParam("page") int page,
            @Parameter(name = "size", in = QUERY,
                    description = "The page size.") @DefaultValue("10") @Min(value = 1, message = "Page size must be between 1 and 100.")
            @Max(value = 100, message = "Page size must be between 1 and 100.") @QueryParam("size") int size, @Context UriInfo uriInfo) {

        var serverInfo = new AccountingUriInfo(serverUrl.concat(basePath).concat(uriInfo.getPath()));

        var response = providerService.getSystemProviders( page - 1, size, serverInfo);

        return Response.ok().entity(response).build();
    }

    @Tag(name = "Provider")
    @Operation(
            summary = "Get Provider report with metrics.",
            description = "Returns a report for a specific Provider and time period, including aggregated metric values.")
    @APIResponse(
            responseCode = "200",
            description = "Provider report retrieved successfully.",
            content = @Content(schema = @Schema(
                    type = SchemaType.OBJECT,
                    implementation = GenericProviderReport.class)))
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
            responseCode = "404",
            description = "Not found.",
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
    @Path("/{provider_id}/report")
    @Produces(value = MediaType.APPLICATION_JSON)
    public Response providerReport(
            @Parameter(
                    description = "Τhe Provider id.",
                    required = true,
                    example = "grnet",
                    schema = @Schema(type = SchemaType.STRING))
            @PathParam("provider_id") @Valid @NotFoundEntity(repository = ProviderRepository.class, id = String.class, message = "There is no Provider with the following id:") String providerId,
            @Parameter(
                    name = "start",
                    description = "Start date in yyyy-MM-dd format",
                    required = true,
                    example = "2024-01-01"
            )
            @QueryParam("start")
            @Valid
            @NotEmpty(message = "start may not be empty.")
            @CheckDateFormat(pattern = "yyyy-MM-dd", message = "Valid date format is yyyy-MM-dd.") String start,

            @Parameter(
                    name = "end",
                    description = "End date in yyyy-MM-dd format",
                    required = true,
                    example = "2024-12-31"
            )
            @QueryParam("end")
            @Valid
            @NotEmpty(message = "end may not be empty.")
            @CheckDateFormat(pattern = "yyyy-MM-dd", message = "Valid date format is yyyy-MM-dd.") String end) {

        if (LocalDate.parse(start).isAfter(LocalDate.parse(end))) {

            throw new BadRequestException("Start date must be before or equal to end date.");
        }

        if(!entitlementService.hasAccess(requestInformation.getParent(),"admin", List.of("roles", "provider", providerId))){

            throw new ForbiddenException("You have no access to execute this operation.");
        }

        var response = providerService.providerReport(providerId, start, end);

        return Response.ok().entity(response).build();
    }

    @Tag(name = "Provider")
    @Operation(
            summary = "Get Provider report with metrics by external id.",
            description = "Returns a report for a specific Provider and time period, including aggregated metric values by external id.")
    @APIResponse(
            responseCode = "200",
            description = "Provider report retrieved successfully.",
            content = @Content(schema = @Schema(
                    type = SchemaType.OBJECT,
                    implementation = GenericProviderReport.class)))
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
            responseCode = "404",
            description = "Not found.",
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
    @Path("/report/external")
    @Produces(value = MediaType.APPLICATION_JSON)
    public Response providerReportByExternalId(
            @Parameter(name = "externalId", in = QUERY, required = true, example = "sites", allowReserved = true,
                    description = "The external provider id.", schema = @Schema(type = SchemaType.STRING)) @QueryParam("externalId") @NotEmpty(message = "externalId may not be empty.") String externalId,
            @Parameter(
                    name = "start",
                    description = "Start date in yyyy-MM-dd format",
                    required = true,
                    example = "2024-01-01"
            )
            @QueryParam("start")
            @Valid
            @NotEmpty(message = "start may not be empty.")
            @CheckDateFormat(pattern = "yyyy-MM-dd", message = "Valid date format is yyyy-MM-dd.") String start,
            @Parameter(
                    name = "end",
                    description = "End date in yyyy-MM-dd format",
                    required = true,
                    example = "2024-12-31"
            )
            @QueryParam("end")
            @Valid
            @NotEmpty(message = "end may not be empty.")
            @CheckDateFormat(pattern = "yyyy-MM-dd", message = "Valid date format is yyyy-MM-dd.") String end) {

        if (LocalDate.parse(start).isAfter(LocalDate.parse(end))) {
            throw new BadRequestException("Start date must be before or equal to end date.");
        }

        var optional = providerService.fetchByExternalId(externalId);

        if(optional.isEmpty()){

            throw new NotFoundException("There is no Provider with external id : "+externalId);
        }

        var provider = optional.get();

        if(!entitlementService.hasAccess(requestInformation.getParent(),"admin", List.of("roles", "provider", provider.getId()))){

            throw new ForbiddenException("You have no access to execute this operation.");
        }

        var response = providerService.providerReport(provider.getId(), start, end);

        return Response.ok().entity(response).build();
    }

    public static class PageableProviderResponseDto extends PageResource<ProviderResponseDto> {

        private List<ProviderResponseDto> content;

        @Override
        public List<ProviderResponseDto> getContent() {
            return content;
        }

        @Override
        public void setContent(List<ProviderResponseDto> content) {
            this.content = content;
        }
    }
}