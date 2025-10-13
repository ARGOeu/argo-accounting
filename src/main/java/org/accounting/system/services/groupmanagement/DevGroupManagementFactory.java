package org.accounting.system.services.groupmanagement;

import io.quarkus.arc.profile.IfBuildProfile;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Alternative;
import jakarta.inject.Inject;

@Alternative
@IfBuildProfile(anyOf = {"test", "dev"})
@ApplicationScoped
public class DevGroupManagementFactory extends GroupManagementFactory {

    @Inject
    TenantGroupManagement tenantGroupManagement;

    @Override
    public GroupManagement choose(){

        return tenantGroupManagement;
    }
}
