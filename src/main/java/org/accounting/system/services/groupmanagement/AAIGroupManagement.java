package org.accounting.system.services.groupmanagement;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.accounting.system.beans.RequestUserContext;
import org.accounting.system.services.clients.Group;
import org.accounting.system.services.clients.GroupMembership;
import org.accounting.system.services.clients.GroupRequest;
import org.accounting.system.services.clients.KeycloakGroupClient;
import org.accounting.system.services.clients.KeycloakTokenClient;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class AAIGroupManagement implements GroupManagement{

    private static final Logger LOG = Logger.getLogger(AAIGroupManagement.class);

    @Inject
    @RestClient
    KeycloakTokenClient tokenClient;

    @Inject
    @RestClient
    KeycloakGroupClient groupClient;

    @Inject
    RequestUserContext requestUserContext;

    @ConfigProperty(name = "api.accounting.manage.groups.client.id")
    String clientId;

    @ConfigProperty(name = "api.accounting.manage.groups.client.secret")
    String clientSecret;

    @Override
    public void createProjectGroup(String projectId) {

        var groupRequest = new GroupRequest();
        groupRequest.name = projectId;

        GroupRequest.Attributes attrs = new GroupRequest.Attributes();
        attrs.description = List.of(projectId);

        groupRequest.attributes = attrs;

        groupClient.createSubGroup(getValueByKey(getParentPath()), groupRequest);

        var id = getValueByKey(getParentPath()+ "/" + projectId);

        addRole(id, "admin");
        addRole(id, "viewer");

        var defaultConfigurationId = getValueByKey(id);
        var defaultConfiguration = getConfiguration(id, defaultConfigurationId);
        var groupRoles = List.of("admin", "viewer");
        defaultConfiguration.setGroupRoles(groupRoles);
        updateConfiguration(id, defaultConfiguration);
    }

    @Override
    public void createProviderGroup(String providerId) {

        var groupRequest = new GroupRequest();
        groupRequest.name = providerId;

        var attrs = new GroupRequest.Attributes();
        attrs.description = List.of("Group for Provider "+providerId);

        groupRequest.attributes = attrs;

        groupClient.createSubGroup(getValueByKey(getParentPath()+"/roles/provider"), groupRequest);
        var id = getValueByKey(getValueByKey(getParentPath()+"/roles/provider" + providerId));

        addRole(id, "admin");
        addRole(id, "viewer");

        var defaultConfigurationId = getValueByKey(id);
        var defaultConfiguration = getConfiguration(id, defaultConfigurationId);
        var groupRoles = List.of("admin", "viewer");
        defaultConfiguration.setGroupRoles(groupRoles);
        updateConfiguration(id, defaultConfiguration);
    }

    @Override
    public void createInstallationGroup(String projectId, String providerId, String installationId) {

        var groupRequest = new GroupRequest();
        groupRequest.name = installationId;

        GroupRequest.Attributes attrs = new GroupRequest.Attributes();
        attrs.description = List.of(String.format("Installation %s created in Project - %s and Provider - %s", installationId,projectId, providerId));

        groupRequest.attributes = attrs;
        groupClient.createSubGroup(getValueByKey(getParentPath() + String.format("/%s/%s", projectId, providerId)), groupRequest);
        var id = getValueByKey(getParentPath() + String.format("/%s/%s/%s", projectId, providerId, installationId));
        addRole(id, "admin");
        addRole(id, "viewer");

        var defaultConfigurationId = getValueByKey(id);
        var defaultConfiguration = getConfiguration(id, defaultConfigurationId);
        var groupRoles = List.of("admin", "viewer");
        defaultConfiguration.setGroupRoles(groupRoles);
        updateConfiguration(id, defaultConfiguration);
    }

    @Override
    public void createAssociationGroup(String projectId, String providerId) {

        var groupRequest = new GroupRequest();
        groupRequest.name = providerId;

        GroupRequest.Attributes attrs = new GroupRequest.Attributes();
        attrs.description = List.of(String.format("Provider %s associated with Project %s",providerId, projectId));

        groupRequest.attributes = attrs;
        groupClient.createSubGroup(getValueByKey(getParentPath() + "/" + projectId), groupRequest);
        var id = getValueByKey(getParentPath() + String.format("/%s/%s", projectId, providerId));
        addRole(id, "admin");
        addRole(id, "viewer");

        var defaultConfigurationId = getValueByKey(id);
        var defaultConfiguration = getConfiguration(id, defaultConfigurationId);
        var groupRoles = List.of("admin", "viewer");
        defaultConfiguration.setGroupRoles(groupRoles);
        updateConfiguration(id, defaultConfiguration);
    }

    @Override
    public void deleteProjectGroup(String projectId) {

        var key = getParentPath() + "/" + projectId;

        try{

            groupClient.deleteGroup(getValueByKey(key));
        } catch (Exception e){

            LOG.error(String.format("Group deletion %s failed with error : %s", key, e.getMessage()));
        }
    }

    @Override
    public void deleteProviderGroup(String providerId) {

        var key = getParentPath() + "/roles/provider/"+providerId;

        try{

            groupClient.deleteGroup(getValueByKey(key));
        } catch (Exception e){

            LOG.error(String.format("Group deletion %s failed with error : %s", key, e.getMessage()));
        }

    }

    @Override
    public void deleteInstallationGroup(String projectId, String providerId, String installationId) {

        var key = getParentPath() + String.format("/%s/%s/%s", projectId, providerId, installationId);
        try{

            groupClient.deleteGroup(getValueByKey(key));
        } catch (Exception e){

            LOG.error(String.format("Group deletion %s failed with error : %s", key, e.getMessage()));
        }
    }

    @Override
    public void deleteAssociationGroup(String projectId, String providerId) {

        var key = getParentPath() + String.format("/%s/%s", projectId, providerId);

        try{

            groupClient.deleteGroup(getValueByKey(key));
        } catch (Exception e){

            LOG.error(String.format("Group deletion %s failed with error : %s", key, e.getMessage()));
        }
    }

    private void addRole(String id, String role){

        groupClient.addRole(id, role);
    }

    private GroupMembership getConfiguration(String groupId, String configurationId){

        return groupClient.getConfiguration(groupId, configurationId);
    }

    private void updateConfiguration(String groupId, GroupMembership groupMembership){

        groupClient.updateConfiguration(groupId, groupMembership);
    }

    private String getValueByKey(String key){

        return groupsToMap(requestUserContext.getParent()).get(key);
    }

    private Map<String, String> groupsToMap(String group){

        var groups = groupClient.getGroups(group);

        return flattenGroupsToMap(groups.results);
    }

    private Map<String, String> flattenGroupsToMap(List<Group> groups) {
        Map<String, String> result = new HashMap<>();
        for (Group g : groups) {
            collect(g, result);
        }
        return result;
    }

    private void collect(Group g, Map<String, String> result) {
        result.put(g.path, g.id);
        result.put(g.id, g.attributes.defaultConfiguration.get(0));
        if (g.extraSubGroups != null) {
            for (Group child : g.extraSubGroups) {
                collect(child, result);
            }
        }
    }

    public String getAccessToken(){

        var response = tokenClient.getToken("client_credentials", clientId, clientSecret);

        return response.access_token;
    }

    private String getParentPath(){

        return "/" +requestUserContext.getParent();
    }
}
