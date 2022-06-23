package org.accounting.system.repositories.authorization;

import org.accounting.system.entities.acl.PermissionAccessControl;
import org.accounting.system.entities.authorization.Role;
import org.accounting.system.repositories.modulators.AccessControlModulator;
import org.bson.types.ObjectId;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class RoleAccessControlRepository extends AccessControlModulator<Role, ObjectId, PermissionAccessControl> {
}
