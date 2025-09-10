package org.accounting.system.endpoints;

import io.quarkus.security.Authenticated;
import jakarta.inject.Inject;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import org.accounting.system.beans.RequestUserContext;
import org.accounting.system.dtos.InformativeResponse;
import org.accounting.system.dtos.pagination.PageResource;
import org.accounting.system.dtos.resource.ResourceResponse;
import org.accounting.system.services.ResourceService;
import org.accounting.system.util.AccountingUriInfo;
import org.eclipse.microprofile.config.inject.ConfigProperty;
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

import java.util.List;

import static org.eclipse.microprofile.openapi.annotations.enums.ParameterIn.QUERY;

@Path("/resources")
@Authenticated
@SecurityScheme(securitySchemeName = "Authentication",
        description = "JWT token",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER)
public class ResourceEndpoint {

    @Inject
    ResourceService resourceService;

    @Inject
    RequestUserContext requestInformation;

    @ConfigProperty(name = "quarkus.resteasy-reactive.path")
    String basePath;

    @ConfigProperty(name = "api.server.url")
    String serverUrl;


    @Tag(name = "Resource")
    @org.eclipse.microprofile.openapi.annotations.Operation(
            summary = "Returns all the Resources available on Accounting Service.",
            description = "Essentially, this operation returns all the Resources available on the Accounting Service. " +
                    "By default, the first page of 10 Resources will be returned. You can tune the default values by using " +
                    "the query parameters page and size.")
    @APIResponse(
            responseCode = "200",
            description = "Array of Eosc Resources.",
            content = @Content(schema = @Schema(
                    type = SchemaType.OBJECT,
                    implementation = PageableResourceResponse.class)))
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
    @Produces(value = MediaType.APPLICATION_JSON)
    public Response getAllEoscResources(@Parameter(name = "page", in = QUERY,
            description = "Indicates the page number. Page number must be >= 1.") @DefaultValue("1") @Min(value = 1, message = "Page number must be >= 1.") @QueryParam("page") int page,
                                        @Parameter(name = "size", in = QUERY,
                                   description = "The page size.") @DefaultValue("10") @Min(value = 1, message = "Page size must be between 1 and 100.")
                           @Max(value = 100, message = "Page size must be between 1 and 100.") @QueryParam("size") int size,
                                        @Context UriInfo uriInfo) {

        var serverInfo = new AccountingUriInfo(serverUrl.concat(basePath).concat(uriInfo.getPath()));

        return Response.ok().entity(resourceService.findAllResources(page - 1, size, serverInfo)).build();
    }

    public static class PageableResourceResponse extends PageResource<ResourceResponse> {

        private List<ResourceResponse> content;

        @Override
        public List<ResourceResponse> getContent() {
            return content;
        }

        @Override
        public void setContent(List<ResourceResponse> content) {
            this.content = content;
        }
    }
}
