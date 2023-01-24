package org.accounting.system.repositories.modulators;

import io.quarkus.mongodb.panache.PanacheQuery;
import io.quarkus.panache.common.Page;
import org.accounting.system.entities.Entity;
import org.accounting.system.enums.ApiMessage;

import javax.ws.rs.ForbiddenException;
import java.util.List;

/**
 * This {@link AccessibleModulator} defines the operations that determine the degree of accessibility to Collection Entities. Each
 * class that extends {@link AccessibleModulator} must specify which entities will be accessible.
 *
 * @param <E> Generic class that represents a mongo collection.
 */
public abstract class AccessibleModulator<E extends Entity, I> extends AccessModulator<E, I> {

    @Override
    public void save(E entity) {

        switch (getRequestInformation().getAccessType()){
            case ALWAYS:
                persist(entity);
                break;
            case ENTITY:
                if(isIdentifiable(entity.getCreatorId())){
                    persist(entity);
                    break;
                } else{
                    throw new ForbiddenException(ApiMessage.NO_PERMISSION.message);
                }
            default:
                throw new ForbiddenException(ApiMessage.NO_PERMISSION.message);
        }
    }

    @Override
    public E fetchEntityById(I id) {

        E entity = findById(id);

        switch (getRequestInformation().getAccessType()){
            case ALWAYS:
                return entity;
            case ENTITY:
                if(isIdentifiable(entity.getCreatorId())){
                    return entity;
                } else{
                    throw new ForbiddenException(ApiMessage.NO_PERMISSION.message);
                }
            default:
                throw new ForbiddenException(ApiMessage.NO_PERMISSION.message);
        }
    }

    @Override
    public boolean deleteEntityById(I id) {

        switch (getRequestInformation().getAccessType()){
            case ALWAYS:
                return deleteById(id);
            case ENTITY:
                E entity = findById(id);
                if(isIdentifiable(entity.getCreatorId())){
                    return deleteById(id);
                } else{
                    throw new ForbiddenException(ApiMessage.NO_PERMISSION.message);
                }
            default:
                throw new ForbiddenException(ApiMessage.NO_PERMISSION.message);
        }
    }

    @Override
    public E updateEntity(E entity, I id) {

        switch (getRequestInformation().getAccessType()){
            case ALWAYS:
                update(entity);
                return entity;
            case ENTITY:
                if(isIdentifiable(entity.getCreatorId())){
                    update(entity);
                    return entity;
                } else{
                    throw new ForbiddenException(ApiMessage.NO_PERMISSION.message);
                }
            default:
                throw new ForbiddenException(ApiMessage.NO_PERMISSION.message);
        }
    }

    @Override
    public List<E> getAllEntities() {
        switch (getRequestInformation().getAccessType()){
            case ALWAYS:
                return findAll().list();
            case ENTITY:
                return list("creatorId = ?1", getRequestInformation().getSubjectOfToken());
            default:
                throw new ForbiddenException(ApiMessage.NO_PERMISSION.message);
        }
    }

    @Override
    public PanacheQuery<E> findAllPageable(int page, int size) {
        switch (getRequestInformation().getAccessType()){
            case ALWAYS:
                return findAll().page(Page.of(page, size));
            case ENTITY:
                return find("creatorId = ?1", getRequestInformation().getSubjectOfToken()).page(Page.of(page, size));
            default:
                throw new ForbiddenException(ApiMessage.NO_PERMISSION.message);
        }
    }
}