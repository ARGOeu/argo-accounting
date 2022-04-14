package org.accounting.system.repositories.authorization;

import org.accounting.system.entities.authorization.Role;
import org.accounting.system.repositories.modulators.AccessControlModulator;
import org.accounting.system.repositories.modulators.AccessEntityModulator;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class RoleAccessEntityRepository extends AccessEntityModulator<Role> {

    @Inject
    RoleAccessControlRepository roleAccessControlRepository;

    @Override
    public AccessControlModulator<Role> accessControlModulator() {
        return roleAccessControlRepository;
    }
}
