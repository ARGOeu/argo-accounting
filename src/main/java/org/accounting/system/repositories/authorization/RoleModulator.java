package org.accounting.system.repositories.authorization;

import org.accounting.system.entities.authorization.Role;
import org.accounting.system.repositories.modulators.AbstractModulator;

import javax.inject.Inject;

public class RoleModulator extends AbstractModulator<Role> {

    @Inject
    RoleAccessAlwaysRepository roleAccessAlwaysRepository;

    @Inject
    RoleAccessEntityRepository roleAccessEntityRepository;


    @Override
    public RoleAccessAlwaysRepository always() {
        return roleAccessAlwaysRepository;
    }

    @Override
    public RoleAccessEntityRepository entity() {
        return roleAccessEntityRepository;
    }
}
