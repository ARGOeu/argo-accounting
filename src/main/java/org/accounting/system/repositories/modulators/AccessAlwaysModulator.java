package org.accounting.system.repositories.modulators;

import org.accounting.system.entities.Entity;
import org.accounting.system.entities.acl.AccessControl;

import java.util.List;

/**
 * This {@link AccessAlwaysModulator} stipulates that all available operations in the various collections
 * will always be accessible without any restrictions. As a result, all collection entities are accessible.
 *
 * @param <E> Generic class that represents a mongo collection.
 */
public abstract class AccessAlwaysModulator<E extends Entity, I> extends AccessModulator<E, I> {


    @Override
    public E fetchEntityById(I id) {
        return findById(id);
    }

    @Override
    public boolean deleteEntityById(I id) {
        return deleteById(id);
    }

    @Override
    public E updateEntity(E entity, I id) {

        update(entity);
        return entity;
    }

    @Override
    public List<E> getAllEntities() {
        return findAll().list();
    }


    @Override
    public void grantPermission(AccessControl accessControl) {
        getAccessControlRepository().persist(accessControl);
    }

    @Override
    public void modifyPermission(AccessControl accessControl) {
        getAccessControlRepository().update(accessControl);
    }

    @Override
    public void deletePermission(AccessControl accessControl) {
        getAccessControlRepository().delete(accessControl);
    }

    @Override
    public AccessControl getPermission(String entity, String who) {
        return getAccessControlRepository().findByWhoAndCollectionAndEntity(who, collection(), entity);
    }

    @Override
    public List<AccessControl> getAllPermissions() {
        return getAccessControlRepository().findAllByCollection(collection());
    }
}