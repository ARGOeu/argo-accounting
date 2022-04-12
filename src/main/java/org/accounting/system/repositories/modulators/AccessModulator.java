package org.accounting.system.repositories.modulators;

import io.quarkus.mongodb.panache.PanacheMongoRepository;
import org.accounting.system.beans.RequestInformation;
import org.accounting.system.entities.Entity;
import org.accounting.system.entities.acl.AccessControl;
import org.accounting.system.repositories.acl.AccessControlRepository;
import org.bson.types.ObjectId;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * This {@link AccessModulator} defines the operations that determine the degree of accessibility to Collection Entities. Each
 * class that extends {@link AccessModulator} must specify which entities will be accessible.
 * Since it implements {@link PanacheMongoRepository}, it also provides access to mongo collections. Because every Accounting
 * System API class that extends {@link Entity}, it represents a mongo collection.
 *
 * @param <E> Generic class that represents a mongo collection.
 */
public abstract class AccessModulator<E extends Entity> implements PanacheMongoRepository<E> {

    @Inject
    RequestInformation requestInformation;

    @Inject
    AccessControlRepository accessControlRepository;

    public abstract E fetchEntityById(ObjectId id);

    public abstract boolean deleteEntityById(ObjectId id);

    public abstract E updateEntity(E entity);

    public abstract List<E> getAllEntities();

    /**
     * This method is responsible fοr granting permissions to specific entity within a generic collection
     * @param accessControl It essentially expresses the permissions that will be granted
     */
    public abstract void grantPermission(AccessControl accessControl);

    /**
     * This method is responsible fοr updating an existing permissions which have already been granted to specific entity
     * @param accessControl It essentially expresses the permissions that will be modified
     */
    public abstract void modifyPermission(AccessControl accessControl);

    /**
     * This method is responsible fοr deleting an existing permissions which have already been granted to specific entity
     * @param accessControl It essentially expresses the permissions that will be deleted
     */
    public abstract void deletePermission(AccessControl accessControl);

    public List<E> combineTwoLists(List<E> a, List<E> b){

        // We wanna avoid duplicates
        Set<E> setA = new HashSet<>(a);
        Set<E> setB = new HashSet<>(b);

        setA.addAll(setB);

        return new ArrayList<>(setA);
    }

    public RequestInformation getRequestInformation() {
        return requestInformation;
    }

    public AccessControlRepository getAccessControlRepository() {
        return accessControlRepository;
    }
}