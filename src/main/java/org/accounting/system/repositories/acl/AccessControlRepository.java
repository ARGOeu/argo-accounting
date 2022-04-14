package org.accounting.system.repositories.acl;

import io.quarkus.mongodb.panache.PanacheMongoRepository;
import org.accounting.system.entities.acl.AccessControl;
import org.accounting.system.enums.Collection;
import org.accounting.system.enums.acl.AccessControlPermission;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.NotFoundException;
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
     * @param who The one to whom the permission may be granted
     * @param collection The name of the Collection
     * @param entity The entity id
     * @param permission Access control permission
     * @return |The Access Control that may grant access to a service/user in a particular entity
     */
    public Optional<AccessControl> findByWhoAndCollectionAndEntityAndPermission(String who, Collection collection, String entity, AccessControlPermission permission){

        return find("who = ?1 and collection = ?2 and entity = ?3 and permissions in ?4", who, collection, entity, permission).stream().findAny();
    }

    /**
     * Returns all Collection entities to which a service/user has {permission} access
     * @param who The one to whom the permissions have been granted
     * @param collection The name of the Collection
     * @param permission Access control permission
     * @return The available access controls that grant access to a service/user in a Collection
     */
    public List<AccessControl> findAllByWhoAndCollection(String who, Collection collection, AccessControlPermission permission){

        return list("who = ?1 and collection = ?2 and permissions in ?3", who, collection, permission);
    }

    /**
     * Returns a specific Collection entity to which a service/user may has access.
     *
     * @param who the one to whom the permission may be granted
     * @param collection The name of the Collection
     * @param entity The entity id
     * @return the Access Control that may grant access to a service/user in a particular entity
     */
    public AccessControl findByWhoAndCollectionAndEntity(String who, Collection collection, String entity){

        var optional =  find("who = ?1 and collection = ?2 and entity = ?3", who, collection, entity).stream().findAny();

        return optional.orElseThrow(()->new NotFoundException("There is no assigned permission for the "+who+" to control access to the "+entity));
    }

    /**
     * Returns all Access Controls that have been created for given collection
     * @param collection The name of the Collection
     * @return The available access controls that grant access to a service/user in a Collection
     */
    public List<AccessControl> findAllByCollection(Collection collection){

        return list("collection = ?1" , collection);
    }

    /**
     * Returns all Access Controls that have been created for given collection and created by given creatorId
     * @param collection The name of the Collection
     * @param creatorId  The creator id
     * @return The available access controls that grant access to a service/user in a Collection
     */
    public List<AccessControl> findAllByCollectionAndCreatorId(Collection collection, String creatorId){

        return list("collection = ?1 and creatorId = ?2" , collection, creatorId);
    }
}