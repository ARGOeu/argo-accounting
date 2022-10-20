package org.accounting.system.repositories.modulators;

import io.quarkus.mongodb.panache.PanacheQuery;
import org.accounting.system.entities.Entity;
import org.accounting.system.enums.ApiMessage;

import javax.ws.rs.ForbiddenException;
import java.util.List;

/**
 * This {@link AbstractModulator} determines which {@link AccessModulator} will take over
 * the execution of queries and also delegates their execution to the corresponding {@link AccessModulator}.
 *
 * @param <E> Generic class that represents a mongo collection.
 */
public abstract class AbstractModulator<E extends Entity, I> extends AccessModulator<E, I>{

    @Override
    public void save(E entity) {
        get().save(entity);
    }

    @Override
    public  E fetchEntityById(I id){
        return get().fetchEntityById(id);
    }

    @Override
    public  boolean deleteEntityById(I id){
        return get().deleteEntityById(id);
    }

    @Override
    public E updateEntity(E entity, I id){
        return get().updateEntity(entity, id);
    }

    @Override
    public  List<E> getAllEntities(){
        return get().getAllEntities();
    }

    @Override
    public PanacheQuery<E> findAllPageable(int page, int size) {
        return get().findAllPageable(page, size);
    }

    @Override
    public <T> PanacheQuery<T> lookup(String from, String localField, String foreignField, String as, int page, int size, Class<T> projection) {
        return get().lookup(from, localField, foreignField, as, page, size, projection);
    }

    @Override
    public <T> T lookUpEntityById(String from, String localField, String foreignField, String as, Class<T> projection, I id) {
        return get().lookUpEntityById(from, localField, foreignField, as, projection, id);
    }

    public abstract AccessAlwaysModulator<E, I> always();

    public abstract AccessEntityModulator<E, I> entity();

    public AccessModulator<E, I> get(){

        switch (getRequestInformation().getAccessType()){
            case ALWAYS:
                return always();
            case ENTITY:
                return entity();
            default:
                throw new ForbiddenException(ApiMessage.NO_PERMISSION.message);
        }
    }

}