package org.accounting.system.endpoints;

import io.quarkus.security.Authenticated;
import org.accounting.system.dtos.InformativeResponse;
import org.accounting.system.dtos.PageResource;
import org.accounting.system.services.ProviderService;
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
import javax.ws.rs.BadRequestException;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

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

    @ConfigProperty(name = "quarkus.resteasy.path")
    String basePath;

    @ConfigProperty(name = "server.url")
    String serverUrl;

    @Inject
    ProviderService providerService;


    @Tag(name = "Provider")
    @Operation(
            summary = "Get a list of all Providers in the Accounting System.",
            description = "Essentially, this operation returns all Providers available on the EOSC-Portal as well as all Providers registered through the Accounting System API. " +
                    "By default, the first page of 10 Providers will be returned. You can tune the default values by using " +
                    "the query parameters page and size.")
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
    @SecurityRequirement(name = "Authentication")

    public Response get(@Parameter(name = "page", in = QUERY,
                                description = "Indicates the page number. Page number must be >= 1.") @DefaultValue("1") @QueryParam("page") int page,
                        @Parameter(name = "size", in = QUERY,
                                description = "The page size.") @DefaultValue("10") @QueryParam("size") int size,
                        @Parameter(name = "id", in = QUERY,
                                description = "The Provider ID.") @DefaultValue("") @QueryParam("id") String id,
                        @Context UriInfo uriInfo) {

        if(page <1){
            throw new BadRequestException("Page number must be >= 1.");
        }

        var serverInfo = new ResteasyUriInfo(serverUrl.concat(basePath).concat(uriInfo.getPath()), basePath);

        var providers = providerService.findAllProvidersPageable(page-1, size, id, serverInfo);

        return Response.ok().entity(providers).build();
    }
}
