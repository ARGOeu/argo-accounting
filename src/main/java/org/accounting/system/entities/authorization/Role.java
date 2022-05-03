package org.accounting.system.entities.authorization;

import lombok.EqualsAndHashCode;
import org.accounting.system.entities.Entity;
import org.bson.types.ObjectId;

import java.util.List;

/**
 * The Role class represents the Role collection stored in the mongo database.
 * Every instance of this class represents a record in that collection.
 */

@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
public class Role extends Entity {

    private ObjectId id;
    @EqualsAndHashCode.Include
    private String name;
    private List<CollectionPermission> collectionPermission;
    private String description;

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

    public List<CollectionPermission> getCollectionPermission() {
        return collectionPermission;
    }

    public void setCollectionPermission(List<CollectionPermission> collectionPermission) {
        this.collectionPermission = collectionPermission;
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }
}