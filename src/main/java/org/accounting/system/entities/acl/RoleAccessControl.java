package org.accounting.system.entities.acl;

import lombok.EqualsAndHashCode;
import org.accounting.system.entities.authorization.Role;

import java.util.HashSet;
import java.util.Set;


@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
public class RoleAccessControl extends AccessControl {

    /**
     * The {@link #roles permissions} component is a set of {@link Role roles}.
     */
    private Set<Role> roles = new HashSet<>();

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }
}