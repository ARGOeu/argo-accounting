package org.accounting.system.entities.acl;

import lombok.EqualsAndHashCode;
import org.accounting.system.entities.Entity;
import org.accounting.system.enums.Collection;
import org.bson.types.ObjectId;

/**
 * An access control (AC) is a list of rules that specifies which clients are granted access to particular entities.
 * The combination of {@link #getWho() who}, {@link #getCollection() collection}, {@link #getEntity() entity should be unique.
 */

@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
public abstract class AccessControl extends Entity {

    private ObjectId id;

    /**
     *{@link #getWho() who} is the id of a client that the AccessControl grants access.
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

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }
}