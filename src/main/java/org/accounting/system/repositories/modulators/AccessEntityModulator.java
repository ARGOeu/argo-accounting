package org.accounting.system.repositories.modulators;

import org.accounting.system.entities.Entity;
import org.accounting.system.entities.acl.AccessControl;
import org.bson.types.ObjectId;

import java.util.List;

/**
 * This {@link AccessEntityModulator} stipulates that the entities in the various collections will be accessible
 * only to the one who created them. If entities are not accessible at this level,
 * accessibility is checked by {@link #accessControlModulator() AccessControlModulator}.
 *
 * @param <E> Generic class that represents a mongo collection.
 */
public abstract class AccessEntityModulator<E extends Entity> extends AccessModulator<E> {

    @Override
    public E fetchEntityById(ObjectId id) {

        E entity = findById(id);

        if (isIdentifiable(entity.getCreatorId())) {
            return entity;
        } else {
            return accessControlModulator().fetchEntityById(id);
        }
    }

    @Override
    public boolean deleteEntityById(ObjectId id){

        E entity = findById(id);

        if (isIdentifiable(entity.getCreatorId())) {
            return deleteById(id);
        } else {
            return accessControlModulator().deleteEntityById(id);
        }
    }

    @Override
    public E updateEntity(E entity) {

        if (isIdentifiable(entity.getCreatorId())) {
            update(entity);
        } else {
            return accessControlModulator().updateEntity(entity);
        }
        return entity;
    }

    @Override
    public List<E> getAllEntities() {

        List<E> fromCollection =  list("creatorId = ?1", getRequestInformation().getSubjectOfToken());
        List<E> fromAccessControl = accessControlModulator().getAllEntities();

        return combineTwoLists(fromCollection, fromAccessControl);
    }

    @Override
    public void grantPermission(AccessControl accessControl) {

        E entity = findById(new ObjectId(accessControl.getEntity()));

        if (isIdentifiable(entity.getCreatorId())) {
            getAccessControlRepository().persist(accessControl);
        } else {
            accessControlModulator().grantPermission(accessControl);
        }
    }

    @Override
    public void modifyPermission(AccessControl accessControl) {

        if (isIdentifiable(accessControl.getCreatorId())) {
            getAccessControlRepository().update(accessControl);
        } else {
            accessControlModulator().modifyPermission(accessControl);
        }
    }

    @Override
    public void deletePermission(AccessControl accessControl) {

        if (isIdentifiable(accessControl.getCreatorId())) {
             getAccessControlRepository().delete(accessControl);
        } else {
            accessControlModulator().deletePermission(accessControl);
        }
    }

    @Override
    public AccessControl getPermission(String entity, String who) {

        var accessControl = getAccessControlRepository().findByWhoAndCollectionAndEntity(who, collection(), entity);

        if(isIdentifiable(accessControl.getCreatorId())){
            return accessControl;
        } else {
            return accessControlModulator().getPermission(entity, who);
        }
    }

    @Override
    public List<AccessControl> getAllPermissions() {
        return getAccessControlRepository().findAllByCollectionAndCreatorId(collection(), getRequestInformation().getSubjectOfToken());
    }

    public abstract AccessControlModulator<E> accessControlModulator();

    /**
     * Checks if the given id can be identified by the subject of an access token.
     *
     * @param idToBeIdentified id to be identified
     * @return
     */
    public boolean isIdentifiable(String idToBeIdentified){
       return idToBeIdentified.equals(getRequestInformation().getSubjectOfToken());
    }
}
