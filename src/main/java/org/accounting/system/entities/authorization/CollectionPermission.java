package org.accounting.system.entities.authorization;

import org.accounting.system.enums.Collection;

import java.util.List;

public class CollectionPermission {

    /**
     * A list of permissions
     */
    public List<Permission> permissions;

    /**
     * The name of the collection to which the permissions apply
     */
    public Collection collection;
}