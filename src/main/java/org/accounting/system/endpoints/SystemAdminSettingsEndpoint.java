package org.accounting.system.endpoints;

import io.quarkus.security.Authenticated;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import org.accounting.system.dtos.InformativeResponse;
import org.accounting.system.dtos.SettingMetadata;
import org.accounting.system.dtos.UpdateSetting;
import org.accounting.system.dtos.pagination.PageResource;
import org.accounting.system.enums.APISetting;
import org.accounting.system.interceptors.annotations.AccessResource;
import org.accounting.system.services.SettingService;
import org.accounting.system.util.AccountingUriInfo;
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

import java.util.List;

import static org.eclipse.microprofile.openapi.annotations.enums.ParameterIn.QUERY;

@Path("/admin/settings")
@Authenticated
@SecurityScheme(securitySchemeName = "Authentication",
        description = "JWT token",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER)
public class SystemAdminSettingsEndpoint {

    @ConfigProperty(name = "quarkus.rest.path")
    String basePath;

    @ConfigProperty(name = "api.server.url")
    String serverUrl;

    @Inject
    SettingService settingService;

    @Tag(name = "System Administrator")
    @Operation(summary = "Get metadata for all settings",
            description = "Returns a list of all configurable settings, their current values, defaults, and allowed options.")
    @APIResponse(
            responseCode = "200",
            description = "List of setting metadata",
            content = @Content(schema = @Schema(
                    type = SchemaType.OBJECT,
                    implementation = PageableSettingResponse.class))
    )
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
    @Path("/metadata")
    @AccessResource
    @Produces(value = MediaType.APPLICATION_JSON)
    public Response getMetadata(
            @Parameter(name = "page", in = QUERY,
                    description = "Indicates the page number. Page number must be >= 1.") @DefaultValue("1") @Min(value = 1, message = "Page number must be >= 1.") @QueryParam("page") int page,
            @Parameter(name = "size", in = QUERY,
                    description = "The page size.") @DefaultValue("10") @Min(value = 1, message = "Page size must be between 1 and 100.")
            @Max(value = 100, message = "Page size must be between 1 and 100.") @QueryParam("size") int size,
            @Context UriInfo uriInfo) {

        var serverInfo = new AccountingUriInfo(serverUrl.concat(basePath).concat(uriInfo.getPath()));

        var results =  settingService.getMetadata(page - 1, size, serverInfo);

        return Response.ok().entity(results).build();
    }

    @Tag(name = "System Administrator")
    @Operation(
            summary = "Update a setting value.",
            description = "Updates the value of a specific application setting.. " +
                    "The key corresponds to a known enum constant in APISetting. Only allowed values (as defined by each enum) are accepted.")
    @APIResponse(
            responseCode = "200",
            description = "Setting updated successfully.",
            content = @Content(schema = @Schema(
                    type = SchemaType.OBJECT,
                    implementation = InformativeResponse.class)))
    @APIResponse(
            responseCode = "400",
            description = "Invalid value provided for this setting.",
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
            responseCode = "500",
            description = "Internal Server Errors.",
            content = @Content(schema = @Schema(
                    type = SchemaType.OBJECT,
                    implementation = InformativeResponse.class)))
    @SecurityRequirement(name = "Authentication")
    @PUT
    @Path("/{key}")
    @Produces(value = MediaType.APPLICATION_JSON)
    @Consumes(value = MediaType.APPLICATION_JSON)
    @AccessResource
    public Response updateSetting(
            @Parameter(
                    description = "The key of the setting to update. Must match a valid APISetting enum constant.",
                    required = true,
                    example = "ENTITLEMENTS_MANAGEMENT",
                    schema = @Schema(type = SchemaType.STRING, implementation = APISetting.class)) @PathParam("key") APISetting key,
            @Valid @NotNull(message = "The request body is empty.") UpdateSetting setting) {

        settingService.update(key, setting.value);

        var response = new InformativeResponse();
        response.code = 200;
        response.message = "Setting updated successfully.";

        return Response.ok().entity(response).build();
    }

    public static class PageableSettingResponse extends PageResource<SettingMetadata> {

        private List<SettingMetadata> content;

        @Override
        public List<SettingMetadata> getContent() {
            return content;
        }

        @Override
        public void setContent(List<SettingMetadata> content) {
            this.content = content;
        }
    }
}
