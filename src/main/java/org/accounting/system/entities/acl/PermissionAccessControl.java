package org.accounting.system.entities.acl;

import lombok.EqualsAndHashCode;
import org.accounting.system.enums.acl.AccessControlPermission;

import java.util.Set;


@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
public class PermissionAccessControl extends AccessControl {

    /**
     * The {@link #permissions permissions} component is a set of {@link AccessControlPermission permissions}.
     */
    private Set<AccessControlPermission> permissions;

    public Set<AccessControlPermission> getPermissions() {
        return permissions;
    }

    public void setPermissions(Set<AccessControlPermission> permissions) {
        this.permissions = permissions;
    }
}