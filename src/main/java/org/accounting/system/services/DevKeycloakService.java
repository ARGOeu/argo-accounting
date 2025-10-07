package org.accounting.system.services;

import io.quarkus.arc.profile.IfBuildProfile;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Alternative;
import org.accounting.system.services.clients.GroupMembership;
import org.accounting.system.services.clients.GroupRequest;

@Alternative
@IfBuildProfile("dev")
@ApplicationScoped
public class DevKeycloakService extends KeycloakService {

    @Override
    public String getAccessToken(){

        return "access_token";
    }

    @Override
    public String getValueByKey(String key){

        return "dev_key";
    }

    @SuppressWarnings("java:S1186")
    @Override
    public void createSubGroup(String id, GroupRequest groupRequest){
        // intentionally left blank
    }

    @SuppressWarnings("java:S1186")
    @Override
    public void deleteGroup(String id){
        // intentionally left blank
    }

    @SuppressWarnings("java:S1186")
    @Override
    public void addRole(String id, String role){
        // intentionally left blank
    }

    @SuppressWarnings("java:S1186")
    @Override
    public void updateConfiguration(String groupId, GroupMembership groupMembership){
        // intentionally left blank
    }

    @Override
    public GroupMembership getConfiguration(String groupId, String configurationId){

        return new GroupMembership();
    }
}