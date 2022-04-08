package org.accounting.system.repositories.authorization;

import org.accounting.system.entities.authorization.Role;
import org.accounting.system.repositories.modulators.AccessEntityModulator;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class RoleAccessEntityRepository extends AccessEntityModulator<Role> {

}
