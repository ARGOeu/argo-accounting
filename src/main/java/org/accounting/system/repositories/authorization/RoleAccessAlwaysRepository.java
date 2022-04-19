package org.accounting.system.repositories.authorization;

import org.accounting.system.entities.authorization.Role;
import org.accounting.system.repositories.modulators.AccessAlwaysModulator;
import org.bson.types.ObjectId;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class RoleAccessAlwaysRepository extends AccessAlwaysModulator<Role, ObjectId> {
}
