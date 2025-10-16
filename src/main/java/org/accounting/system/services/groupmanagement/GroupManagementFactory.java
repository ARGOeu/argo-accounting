package org.accounting.system.services.groupmanagement;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.accounting.system.beans.RequestUserContext;
import org.accounting.system.repositories.OidcTenantConfigRepository;

@ApplicationScoped
public class GroupManagementFactory {

    @Inject
    OidcTenantConfigRepository oidcTenantConfigRepository;

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
