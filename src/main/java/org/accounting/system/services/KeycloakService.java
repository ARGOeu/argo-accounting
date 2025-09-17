package org.accounting.system.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.accounting.system.services.clients.Group;
import org.accounting.system.services.clients.GroupMembership;
import org.accounting.system.services.clients.GroupRequest;
import org.accounting.system.services.clients.KeycloakGroupClient;
import org.accounting.system.services.clients.KeycloakTokenClient;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class KeycloakService {

    @Inject
    @RestClient
    KeycloakTokenClient tokenClient;

    @Inject
    @RestClient
    KeycloakGroupClient groupClient;

    @ConfigProperty(name = "api.accounting.manage.groups.client.id")
    String clientId;

    @ConfigProperty(name = "api.accounting.manage.groups.client.secret")
    String clientSecret;

    public String getAccessToken(){

        var response = tokenClient.getToken("client_credentials", clientId, clientSecret);

        return response.access_token;
    }

    private Map<String, String> groupsToMap(String group){

        var groups = groupClient.getGroups(group);

        return flattenGroupsToMap(groups.results);
    }

    public String getValueByKey(String key){

        return groupsToMap("accounting").get(key);
    }

    public void createSubGroup(String id, GroupRequest groupRequest){

        groupClient.createSubGroup(id, groupRequest);
    }

    public void deleteGroup(String id){

        groupClient.deleteGroup(id);
    }

    public void addRole(String id, String role){

        groupClient.addRole(id, role);
    }

    public GroupMembership getConfiguration(String groupId, String configurationId){

        return groupClient.getConfiguration(groupId, configurationId);
    }

    public void updateConfiguration(String groupId, GroupMembership groupMembership){

        groupClient.updateConfiguration(groupId, groupMembership);
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

}
