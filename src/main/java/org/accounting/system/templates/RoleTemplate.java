package org.accounting.system.templates;

import io.quarkus.qute.Template;
import org.accounting.system.services.authorization.RoleService;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * This endpoint is responsible for rendering the role.html.
 * The web page is created by processing template file. Templates are located under src/main/resources/templates.
 */
@Path("/role-template")
public class RoleTemplate {

    @Inject
    Template role;

    @Inject
    RoleService roleService;

    @GET
    @Produces(MediaType.TEXT_HTML)
    public String keycloakClient() {
        return role
                .data("roles", roleService.fetchRoles())
                .render();
    }
}
