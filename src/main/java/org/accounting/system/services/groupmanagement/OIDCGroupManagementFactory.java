package org.accounting.system.services.groupmanagement;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.accounting.system.beans.RequestUserContext;

@ApplicationScoped
public class OIDCGroupManagementFactory implements GroupManagementFactoryI {

    @Inject
    RequestUserContext requestUserContext;

    @Inject
    AAIGroupManagement aaiGroupManagement;

    @Inject
    TenantGroupManagement tenantGroupManagement;

    public GroupManagement choose(){

        var optional = requestUserContext.getOidcTenantConfig();

        if(optional.isEmpty()){

            return aaiGroupManagement;
        } else {

            return tenantGroupManagement;
        }
    }
}
