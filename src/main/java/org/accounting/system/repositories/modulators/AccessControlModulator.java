package org.accounting.system.repositories.modulators;

import net.jodah.typetools.TypeResolver;
import org.accounting.system.entities.Entity;
import org.accounting.system.entities.acl.AccessControl;
import org.accounting.system.enums.Collection;
import org.accounting.system.enums.acl.AccessControlPermission;
import org.accounting.system.repositories.acl.AccessControlRepository;
import org.bson.types.ObjectId;

import javax.ws.rs.ForbiddenException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * This {@link AccessControlModulator} provides access only to entities
 * explicitly declared in the {@link AccessControl} collection.
 *
 * Child class must provide an implementation of {@link AccessControlRepository} as well as the
 * Collection that the child class entities belong to.
 *
 * @param <E> Generic class that represents a mongo collection.
 */
public abstract class AccessControlModulator<E extends Entity> extends AccessModulator<E> {

    protected Class<E> clazz;

    public AccessControlModulator(){
        Class<?> type = TypeResolver.resolveRawArgument(AccessControlModulator.class, getClass());
        clazz = (Class<E>) type;
    }

    @Override
    public E fetchEntityById(ObjectId id) {

        var optional = getAccessControl(id, AccessControlPermission.READ);

        if(optional.isPresent()){
            return findById(id);
        } else {
            throw new ForbiddenException("You have no access to this entity : " + id.toString());
        }
    }

    @Override
    public boolean deleteEntityById(ObjectId id) {
        var optional = getAccessControl(id, AccessControlPermission.DELETE);

        if(optional.isPresent()){
            return deleteById(id);
        } else {
            throw new ForbiddenException("You have no access to this entity : " + id.toString());
        }
    }

    @Override
    public E updateEntity(E entity) {
        var optional = getAccessControl(entity.getId(), AccessControlPermission.UPDATE);
        if(optional.isPresent()){
             update(entity);
        } else {
            throw new ForbiddenException("You have no access to this entity : " + entity.getId().toString());
        }
        return entity;
    }

    @Override
    public List<E> getAllEntities() {

        List<AccessControl> accessControlList = getAccessControlRepository().findByWhoAndCollection(getRequestInformation().getSubjectOfToken(), collection(), AccessControlPermission.READ);

        List<ObjectId> entities = accessControlList
                .stream()
                .map(AccessControl::getEntity)
                .map(ObjectId::new)
                .collect(Collectors.toList());

        return list("_id in ?1", List.of(entities));
    }

    @Override
    public void grantPermission(AccessControl accessControl) {

        throw new ForbiddenException("You have no access to this entity : " + accessControl.getEntity());
    }

    @Override
    public void modifyPermission(AccessControl accessControl) {

        throw new ForbiddenException("You have no access to modify these permissions.");
    }

    private Optional<AccessControl> getAccessControl(ObjectId id, AccessControlPermission permission){

        return getAccessControlRepository().findByWhoAndCollectionAndEntityAndPermission(getRequestInformation().getSubjectOfToken(), collection(), id.toString(), permission);
    }

    public Collection collection(){
        return Collection.valueOf(clazz.getSimpleName());
    }
}
