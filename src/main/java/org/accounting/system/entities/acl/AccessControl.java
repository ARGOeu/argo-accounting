package org.accounting.system.entities.acl;

import lombok.EqualsAndHashCode;
import org.accounting.system.entities.Entity;
import org.accounting.system.enums.Collection;
import org.accounting.system.enums.acl.AccessControlPermission;
import org.bson.types.ObjectId;

import java.util.Set;

/**
 * An access control (AC) is a list of rules that specifies which services/users are granted access to particular entities.
 * The combination of {@link #getWho() who}, {@link #getCollection() collection}, {@link #getEntity() entity should be unique.
 */

@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
public class AccessControl extends Entity {

    private ObjectId id;

    /**
     *{@link #getWho() who} is the id of a Service/User that the AccessControl grants access.
     */
    @EqualsAndHashCode.Include
    private String who;

    /**
     * {@link #getCollection() collection} is a collection name that the {@link #getEntity() entity} belongs to.
     */
    @EqualsAndHashCode.Include
    private Collection collection;

    /**
     * {@link #getEntity() entity} is the id of the entity to which the permissions apply.
     */
    @EqualsAndHashCode.Include
    private String entity;

    /**
     * The {@link #permissions permissions} component is a set of {@link AccessControlPermission permissions}.
     */
    private Set<AccessControlPermission> permissions;

    public String getWho() {
        return who;
    }

    public void setWho(String who) {
        this.who = who;
    }

    public Collection getCollection() {
        return collection;
    }

    public void setCollection(Collection collection) {
        this.collection = collection;
    }

    public String getEntity() {
        return entity;
    }

    public void setEntity(String entity) {
        this.entity = entity;
    }

    public Set<AccessControlPermission> getPermissions() {
        return permissions;
    }

    public void setPermissions(Set<AccessControlPermission> permissions) {
        this.permissions = permissions;
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }
}