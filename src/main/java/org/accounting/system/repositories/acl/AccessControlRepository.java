package org.accounting.system.repositories.acl;

import io.quarkus.mongodb.panache.PanacheMongoRepository;
import org.accounting.system.entities.acl.AccessControl;
import org.accounting.system.enums.Collection;
import org.accounting.system.enums.acl.AccessControlPermission;

import javax.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.Optional;

/**
 * This repository {@link AccessControlRepository} encapsulates the logic required to access
 * {@link AccessControl} data stored in the mongo database. More specifically, it encapsulates the queries
 * that can be performed on the {@link AccessControl} collection. It is also responsible for mapping
 * the data from the storage format to the {@link AccessControl}.
 *
 */
@ApplicationScoped
public class AccessControlRepository implements PanacheMongoRepository<AccessControl> {

    /**
     * Returns a specific Collection entity to which a service/user may has {permission} access.
     *
     * @param who Owner id
     * @param collection The name of the Collection
     * @param entity Entity id
     * @param permission access control permission
     * @return The Access Control that grant access to a service/user in a particular entity
     */
    public Optional<AccessControl> findByWhoAndCollectionAndEntityAndPermission(String who, Collection collection, String entity, AccessControlPermission permission){

        return find("who = ?1 and collection = ?2 and entity = ?3 and permissions in ?4", who, collection, entity, permission).stream().findAny();
    }

    /**
     * Returns all Collection entities to which a service/user has {permission} access
     * @param who Owner id
     * @param collection The name of the Collection
     * @param permission access control permission
     * @return The available access controls that grant access to a service/user in a Collection
     */
    public List<AccessControl> findByWhoAndCollection(String who, Collection collection, AccessControlPermission permission){

        return list("who = ?1 and collection = ?2 and permissions in ?3", who, collection, permission);
    }
}