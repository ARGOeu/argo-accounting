package org.accounting.system.entities.authorization;

import lombok.EqualsAndHashCode;
import org.accounting.system.entities.Entity;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bson.types.ObjectId;

import java.util.HashSet;
import java.util.Set;

/**
 * The Role class represents the Role collection stored in the mongo database.
 * Every instance of this class represents a record in that collection.
 */

@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
public class Role extends Entity {

    private ObjectId id;
    @EqualsAndHashCode.Include
    private String name;
    @BsonProperty("collections_access_permissions")
    private Set<CollectionPermission> collectionsAccessPermissions = new HashSet<>();
    private String description;
    private boolean system;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<CollectionPermission> getCollectionsAccessPermissions() {
        return collectionsAccessPermissions;
    }

    public void setCollectionsAccessPermissions(Set<CollectionPermission> collectionsAccessPermissions) {
        this.collectionsAccessPermissions = collectionsAccessPermissions;
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public boolean isSystem() {
        return system;
    }

    public void setSystem(boolean system) {
        this.system = system;
    }
}