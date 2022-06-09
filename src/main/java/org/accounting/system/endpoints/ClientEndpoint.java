package org.accounting.system.endpoints;

import io.quarkus.security.Authenticated;
import org.accounting.system.dtos.InformativeResponse;
import org.accounting.system.dtos.client.ClientResponseDto;
import org.accounting.system.enums.Collection;
import org.accounting.system.interceptors.annotations.Permission;
import org.accounting.system.services.client.ClientService;
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

import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import static org.eclipse.microprofile.openapi.annotations.enums.ParameterIn.QUERY;

@Path("/clients")
@Authenticated
@SecurityScheme(securitySchemeName = "Authentication",
        description = "JWT token",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER)

public class ClientEndpoint {

    @Inject
    ClientService clientService;


    @Tag(name = "Client")
    @Operation(
            hidden = true,
            summary = "Registers a client into the Accounting System.",
            description = "Passing the acquired access token to this operation, any client can register itself into the Accounting System.")
    @APIResponse(
            responseCode = "200",
            description = "Client has been successfully registered.",
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

    @POST
    @Produces(value = MediaType.APPLICATION_JSON)
    public Response userRegistration() {

        var response = clientService.register();

        return Response.ok().entity(response).build();
    }

    @Tag(name = "Client")
    @Operation(
            hidden = true,
            summary = "Returns the available clients.",
            description = "This operation fetches the registered Accounting System clients. By default, the first page of 10 Clients will be returned. " +
                    "You can tune the default values by using the query parameters page and size.")
    @APIResponse(
            responseCode = "200",
            description = "Array of available clients.",
            content = @Content(schema = @Schema(
                    type = SchemaType.ARRAY,
                    implementation = ClientResponseDto.class)))
    @APIResponse(
            responseCode = "500",
            description = "Internal Server Errors.",
            content = @Content(schema = @Schema(
                    type = SchemaType.OBJECT,
                    implementation = InformativeResponse.class)))

    @GET
    @Produces(value = MediaType.APPLICATION_JSON)
    @Permission(collection = Collection.Client, operation = org.accounting.system.enums.Operation.READ)
    public Response getRoles(@Parameter(name = "page", in = QUERY,
            description = "Indicates the page number. Page number must be >= 1.") @DefaultValue("1") @QueryParam("page") int page,
                             @Parameter(name = "size", in = QUERY,
                                     description = "The page size.") @DefaultValue("10") @QueryParam("size") int size,
                             @Context UriInfo uriInfo){
        if(page <1){
            throw new BadRequestException("Page number must be >= 1.");
        }

        return Response.ok().entity(clientService.findAllClientsPageable(page-1, size, uriInfo)).build();
    }
}
