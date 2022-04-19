package org.accounting.system.repositories.modulators;

import org.accounting.system.entities.Entity;
import org.accounting.system.entities.acl.AccessControl;
import org.accounting.system.enums.acl.AccessControlPermission;
import org.bson.types.ObjectId;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * This {@link AccessControlModulator} provides access only to entities
 * explicitly declared in the {@link AccessControl} collection.
 *
 * @param <E> Generic class that represents a mongo collection.
 */
public abstract class AccessControlModulator<E extends Entity, I> extends AccessModulator<E, I> {

    @Override
    public E fetchEntityById(I id) {

        var optional = getAccessControl(id, AccessControlPermission.READ);

        if(optional.isPresent()){
            return findById(id);
        } else {
            return super.fetchEntityById(id);
        }
    }

    @Override
    public boolean deleteEntityById(I id) {
        var optional = getAccessControl(id, AccessControlPermission.DELETE);

        if(optional.isPresent()){
            return deleteById(id);
        } else {
            return super.deleteEntityById(id);
        }
    }

    @Override
    public E updateEntity(E entity, I id) {
        var optional = getAccessControl(id, AccessControlPermission.UPDATE);
        if(optional.isPresent()){
            update(entity);
        } else {
            return super.updateEntity(entity, id);
        }
        return entity;
    }

    @Override
    public List<E> getAllEntities() {

        List<ObjectId> entities = getAccessControlRepository().findAllByWhoAndCollection(getRequestInformation().getSubjectOfToken(), collection(), AccessControlPermission.READ)
                .stream()
                .map(AccessControl::getEntity)
                .map(ObjectId::new)
                .collect(Collectors.toList());

        return list("_id in ?1", List.of(entities));
    }


    @Override
    public void grantPermission(AccessControl accessControl) {
        super.grantPermission(accessControl);
    }

    @Override
    public void modifyPermission(AccessControl accessControl) {
        super.modifyPermission(accessControl);
    }

    @Override
    public void deletePermission(AccessControl accessControl) {
        super.deletePermission(accessControl);
    }

    @Override
    public AccessControl getPermission(String entity, String who) {
        return super.getPermission(entity, who);
    }

    private <ID> Optional<AccessControl> getAccessControl(ID id, AccessControlPermission permission){

        return getAccessControlRepository().findByWhoAndCollectionAndEntityAndPermission(getRequestInformation().getSubjectOfToken(), collection(), id.toString(), permission);
    }
}