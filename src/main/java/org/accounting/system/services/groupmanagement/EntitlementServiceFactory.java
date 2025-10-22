package org.accounting.system.services.groupmanagement;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.accounting.system.beans.RequestUserContext;

@ApplicationScoped
public class EntitlementServiceFactory {

    @Inject
    DatabaseEntitlementService databaseEntitlementService;

    @Inject
    OIDCEntitlementService oidcEntitlementService;

    @Inject
    RequestUserContext requestUserContext;

    public EntitlementService from(){

        if(requestUserContext.entitlementManagement().equalsIgnoreCase("database")){

            return databaseEntitlementService;
        } else if (requestUserContext.entitlementManagement().equalsIgnoreCase("oidc")){

            return oidcEntitlementService;
        } else {

            return databaseEntitlementService;
        }
    }
}
