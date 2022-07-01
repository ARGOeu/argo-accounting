package org.accounting.system.entities.acl;

import lombok.EqualsAndHashCode;
import org.accounting.system.entities.authorization.Role;

import java.util.Set;


@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
public class RoleAccessControl extends AccessControl {

    /**
     * The {@link #roles permissions} component is a set of {@link Role roles}.
     */
    private Set<String> roles;

    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }
}