package org.accounting.system.repositories.authorization;

import org.accounting.system.beans.RequestInformation;
import org.accounting.system.entities.Entity;
import org.accounting.system.enums.AccessType;
import org.bson.types.ObjectId;

import javax.enterprise.inject.spi.CDI;
import javax.ws.rs.ForbiddenException;
import java.util.List;

/**
 * This class is responsible for the degree of accessibility to the various collections.
 * Public methods define the degree of accessibility.
 * Protected methods must be implemented by repositories which determine how to communicate with the database.
 *
 * Consequently, public methods determine which records will be returned or not and protected ones define the query
 * in the database.
 *
 */
public abstract class AccessEntityRepository<E extends Entity>{

    private RequestInformation requestInformation;

    public AccessEntityRepository(){
        requestInformation = CDI.current().select(RequestInformation.class).get();
    }

    protected abstract E getEntityById(ObjectId id);

    public E fetchEntityById(ObjectId id){

        AccessType accessType = requestInformation.getAccessType();

        switch (accessType) {
            case ALWAYS:
                return getEntityById(id);
            case ENTITY:
                E entity = getEntityById(id);
                if(entity.getCreatorId().equals(requestInformation.getSubjectOfToken())){
                    return entity;
                } else {
                        throw new ForbiddenException("You have no access to this entity : "+entity.getId().toString());
                }
            default:
                throw new ForbiddenException("You cannot perform this operation.");
        }
    }

    protected abstract boolean deleteEntity(ObjectId id);

    public boolean deleteEntityById(ObjectId id) {

        fetchEntityById(id);
        return deleteEntity(id);
    }

    protected abstract <U> E updateEntity(ObjectId id, U updateDto, E entity);

    public <U> E updateEntityById(ObjectId id, U updateDto) {

        E entity = fetchEntityById(id);

        return updateEntity(id, updateDto, entity);
    }

    protected abstract List<E> getAll();

    protected abstract List<E> getAllByCreatorId(String creatorId);

    public List<E> getAllEntities(){

        AccessType accessType = requestInformation.getAccessType();

        switch (accessType) {
            case ALWAYS:
                return getAll();
            case ENTITY:
                return getAllByCreatorId(requestInformation.getSubjectOfToken());
            default:
                throw new ForbiddenException("You cannot perform this operation.");
        }
    }
}
