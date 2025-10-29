package org.accounting.system.services.groupmanagement;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.accounting.system.beans.RequestUserContext;

@ApplicationScoped
public class GroupManagementSelection {

    @Inject
    DatabaseGroupManagementFactory databaseGroupManagementFactory;

    @Inject
    OIDCGroupManagementFactory oidcGroupManagementFactory;

    @Inject
    RequestUserContext requestUserContext;

    public GroupManagementFactoryI from(){

        if(requestUserContext.entitlementManagement().equalsIgnoreCase("database")){

            return databaseGroupManagementFactory;
        } else if (requestUserContext.entitlementManagement().equalsIgnoreCase("oidc")){

            return oidcGroupManagementFactory;
        } else {

            return databaseGroupManagementFactory;
        }
    }
}
