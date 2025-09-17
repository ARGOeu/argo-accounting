package org.accounting.system.services.clients;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RegisterRestClient(configKey = "keycloak-group-client")
@Path("/agm/account")
@RegisterProvider(BearerTokenRequestFilter.class)
@RegisterProvider(KeycloakExceptionMapper.class)
public interface KeycloakGroupClient {

    @GET
    @Path("/group-admin/groups")
    GroupResponse getGroups(@QueryParam("search") String group);

    @POST
    @Path("/group-admin/group/{id}/children")
    @Consumes(MediaType.APPLICATION_JSON)
    void createSubGroup(@PathParam("id") String id, GroupRequest request);

    @DELETE
    @Path("/group-admin/group/{id}")
    void deleteGroup(@PathParam("id") String id);

    @POST
    @Path("/group-admin/group/{id}/roles")
    void addRole(@PathParam("id") String id, @QueryParam("name") String role);

    @POST
    @Path("/group-admin/group/{id}/configuration")
    @Consumes(MediaType.APPLICATION_JSON)
    void updateConfiguration(@PathParam("id") String id, GroupMembership groupMembership);

    @GET
    @Path("group-admin/group/{groupId}/configuration/{configurationId}")
    GroupMembership getConfiguration(@PathParam("groupId") String groupId, @PathParam("configurationId") String configurationId);
}