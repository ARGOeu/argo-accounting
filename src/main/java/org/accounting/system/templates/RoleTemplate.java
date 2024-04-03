package org.accounting.system.templates;

import io.quarkus.qute.Template;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.accounting.system.repositories.authorization.RoleRepository;
import org.eclipse.microprofile.openapi.annotations.Operation;

/**
 * This endpoint is responsible for rendering the role.html.
 * The web page is created by processing template file. Templates are located under src/main/resources/templates.
 */
@Path("/role-template")
public class RoleTemplate {

    @Inject
    Template role;

    @Inject
    RoleRepository roleRepository;

    @GET
    @Produces(MediaType.TEXT_HTML)
    @Operation(hidden = true)
    public String keycloakClient() {
        return role
                .data("roles", roleRepository.find("system = ?1", false).list())
                .render();
    }
}
