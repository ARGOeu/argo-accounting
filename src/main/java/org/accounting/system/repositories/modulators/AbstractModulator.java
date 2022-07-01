package org.accounting.system.repositories.modulators;

import com.mongodb.MongoWriteException;
import io.quarkus.mongodb.panache.PanacheQuery;
import org.accounting.system.entities.Entity;
import org.accounting.system.entities.acl.AccessControl;
import org.accounting.system.entities.projections.ProjectionQuery;
import org.accounting.system.exceptions.ConflictException;

import javax.ws.rs.ForbiddenException;
import java.util.List;

/**
 * This {@link AbstractModulator} determines which {@link AccessModulator} will take over
 * the execution of queries and also delegates their execution to the corresponding {@link AccessModulator}.
 *
 * @param <E> Generic class that represents a mongo collection.
 */
public abstract class AbstractModulator<E extends Entity, I, A extends AccessControl> extends AccessModulator<E, I, A>{

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
    public void grantPermission(A accessControl){
        try{
            get().grantPermission(accessControl);
        } catch (MongoWriteException e){
            throw new ConflictException("There is already an Access Control Entry with this {who, collection, entity} : {" + accessControl.getWho()+", "+accessControl.getCollection()+", "+accessControl.getEntity()+"}");
        }
    }

    @Override
    public void modifyPermission(A accessControl) {
        get().modifyPermission(accessControl);
    }

    @Override
    public void deletePermission(A accessControl) {
        get().deletePermission(accessControl);
    }

    @Override
    public A getPermission(String entity, String who) {
        return get().getPermission(entity, who);
    }

    @Override
    public List<A> getAllPermissions() {
        return get().getAllPermissions();
    }

    @Override
    public <T> ProjectionQuery<T> lookup(String from, String localField, String foreignField, String as, int page, int size, Class<T> projection) {
        return get().lookup(from, localField, foreignField, as, page, size, projection);
    }

    @Override
    public <T> T lookUpEntityById(String from, String localField, String foreignField, String as, Class<T> projection, I id) {
        return get().lookUpEntityById(from, localField, foreignField, as, projection, id);
    }

    public abstract AccessAlwaysModulator<E, I, A> always();

    public abstract AccessEntityModulator<E, I, A> entity();

    public AccessModulator<E, I, A> get(){

        switch (getRequestInformation().getAccessType()){
            case ALWAYS:
                return always();
            case ENTITY:
                return entity();
            default:
                throw new ForbiddenException("The authenticated client is not permitted to perform the requested operation.");
        }
    }

}