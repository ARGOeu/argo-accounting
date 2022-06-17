package org.accounting.system.entities.authorization;

import lombok.EqualsAndHashCode;
import org.accounting.system.enums.Collection;
import org.bson.codecs.pojo.annotations.BsonProperty;

import java.util.List;

@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class CollectionPermission {

    /**
     * A list of permissions
     */
    @BsonProperty("access_permissions")
    @EqualsAndHashCode.Include
    public List<AccessPermission> accessPermissions;

    /**
     * The name of the collection to which the permissions apply
     */
    @EqualsAndHashCode.Include
    public Collection collection;
}