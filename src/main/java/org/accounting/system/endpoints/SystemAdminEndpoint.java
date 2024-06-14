package org.accounting.system.endpoints;

import io.quarkus.security.Authenticated;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import org.accounting.system.constraints.NotFoundEntity;
import org.accounting.system.dtos.InformativeResponse;
import org.accounting.system.dtos.admin.ProjectRegistrationRequest;
import org.accounting.system.dtos.project.ProjectRequest;
import org.accounting.system.dtos.project.UpdateProjectRequest;
import org.accounting.system.dtos.resource.ResourceRequest;
import org.accounting.system.dtos.resource.ResourceResponse;
import org.accounting.system.entities.projections.normal.ProjectProjection;
import org.accounting.system.interceptors.annotations.SystemAdmin;
import org.accounting.system.repositories.project.ProjectRepository;
import org.accounting.system.services.SystemAdminService;
import org.accounting.system.util.AccountingUriInfo;
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

@Path("/admin")
@Authenticated
@SecurityScheme(securitySchemeName = "Authentication",
        description = "JWT token",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER)

public class SystemAdminEndpoint {

    @Inject
    SystemAdminService systemAdminService;

    @Inject
    Utility utility;

    @ConfigProperty(name = "quarkus.resteasy-reactive.path")
    String basePath;

    @ConfigProperty(name = "api.server.url")
    String serverUrl;


    @Tag(name = "System Administrator")
    @Operation(
            summary = "Endpoint for registering Projects into the Accounting Service.",
            description = "This endpoint allows system administrators to register a new project into the Accounting Service. Access to this endpoint is restricted to system administrators only.")
    @APIResponse(
            responseCode = "200",
            description = "Informs about the success or failure of the registration process.",
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
    @Path("/register-projects")
    @Produces(value = MediaType.APPLICATION_JSON)
    @Consumes(value = MediaType.APPLICATION_JSON)
    @SystemAdmin
    public Response assignProjects(@Valid @NotNull(message = "The request body is empty." ) ProjectRegistrationRequest request) {

        var response = systemAdminService.registerProjectsToAccountingService(request.projects, utility.getClientVopersonId());

        return Response.ok().entity(response).build();
    }

    @Tag(name = "System Administrator")
    @Operation(
            summary = "Create a new Project.",
            description = "Creates a new Project in the Accounting Service. Accessible only by system administrators.")
    @APIResponse(
            responseCode = "201",
            description = "Project created successfully.",
            content = @Content(schema = @Schema(
                    type = SchemaType.OBJECT,
                    implementation = ProjectProjection.class)))
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
            responseCode = "409",
            description = "Project already exists.",
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
    @Path("/projects")
    @Produces(value = MediaType.APPLICATION_JSON)
    @Consumes(value = MediaType.APPLICATION_JSON)
    @SystemAdmin
    public Response createProject(@Valid @NotNull(message = "The request body is empty." ) ProjectRequest request, @Context UriInfo uriInfo) {

        var serverInfo = new AccountingUriInfo(serverUrl.concat(basePath).concat(uriInfo.getPath()));

        var response = systemAdminService.createProject(request, utility.getClientVopersonId());

        return Response.created(serverInfo.getAbsolutePathBuilder().path(response.id).build()).entity(response).build();
    }

    @Tag(name = "System Administrator")
    @Operation(
            summary = "Create a new Resource.",
            description = "Creates a new Resource in the Accounting Service. Accessible only by system administrators.")
    @APIResponse(
            responseCode = "201",
            description = "Resource created successfully.",
            content = @Content(schema = @Schema(
                    type = SchemaType.OBJECT,
                    implementation = ResourceResponse.class)))
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
            responseCode = "409",
            description = "Resource already exists.",
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
    @Path("/resources")
    @Produces(value = MediaType.APPLICATION_JSON)
    @Consumes(value = MediaType.APPLICATION_JSON)
    @SystemAdmin
    public Response createResource(@Valid @NotNull(message = "The request body is empty." ) ResourceRequest request, @Context UriInfo uriInfo) {

        var serverInfo = new AccountingUriInfo(serverUrl.concat(basePath).concat(uriInfo.getPath()));

        var response = systemAdminService.createResource(request);

        return Response.created(serverInfo.getAbsolutePathBuilder().path(response.id).build()).entity(response).build();
    }

    @Tag(name = "System Administrator")
    @Operation(
            summary = "Updates an existing Project.",
            description = "In order to update the resource properties, the body of the request must contain an updated representation of Project. " +
                    "You can update a part or all attributes of Project. The empty or null values are ignored. ")
    @APIResponse(
            responseCode = "200",
            description = "Project was updated successfully.",
            content = @Content(schema = @Schema(
                    type = SchemaType.OBJECT,
                    implementation = ProjectProjection.class)))
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
            description = "Project has not been found.",
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
    @Path("/projects/{id}")
    @Produces(value = MediaType.APPLICATION_JSON)
    @Consumes(value = MediaType.APPLICATION_JSON)
    @SystemAdmin
    public Response update(
            @Parameter(
                    description = "The Project to be updated.",
                    required = true,
                    example = "445677",
                    schema = @Schema(type = SchemaType.STRING))
            @PathParam("id") @Valid @NotFoundEntity(repository = ProjectRepository.class, id = String.class, message = "There is no Project with the following id:") String id, @Valid @NotNull(message = "The request body is empty.") UpdateProjectRequest updateProjectRequest) {

        var response = systemAdminService.updateProject(updateProjectRequest, id);

        return Response.ok().entity(response).build();
    }
}