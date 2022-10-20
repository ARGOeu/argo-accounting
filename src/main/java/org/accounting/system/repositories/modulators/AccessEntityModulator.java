package org.accounting.system.repositories.modulators;

import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import org.accounting.system.entities.Entity;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.List;

/**
 * This {@link AccessEntityModulator} stipulates that the entities in the various collections will be accessible
 * only to the one who created them. If entities are not accessible at this level,
 * accessibility is checked by {@link #accessControlModulator() AccessControlModulator}.
 *
 * @param <E> Generic class that represents a mongo collection.
 */
public abstract class AccessEntityModulator<E extends Entity, I> extends AccessModulator<E, I> {

    @Override
    public void save(E entity) {
        super.save(entity);
    }

    @Override
    public E fetchEntityById(I id) {

        E entity = findById(id);

        if (isIdentifiable(entity.getCreatorId())) {
            return entity;
        } else {
            return accessControlModulator().fetchEntityById(id);
        }
    }

    @Override
    public boolean deleteEntityById(I id){

        E entity = findById(id);

        if (isIdentifiable(entity.getCreatorId())) {
            return deleteById(id);
        } else {
            return accessControlModulator().deleteEntityById(id);
        }
    }

    @Override
    public E updateEntity(E entity, I id) {

        if (isIdentifiable(entity.getCreatorId())) {
            update(entity);
        } else {
            return accessControlModulator().updateEntity(entity, id);
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
    public <T> T lookUpEntityById(String from, String localField, String foreignField, String as, Class<T> projection, I id) {

        Bson bson = Aggregates.lookup(from, localField, foreignField, as);

        List<T> projections = getMongoCollection()
                .aggregate(List.of(bson, Aggregates.match(Filters.and(Filters.eq("_id", id), Filters.eq("creatorId", getRequestInformation().getSubjectOfToken())))), projection)
                .into(new ArrayList<>());

        if(projections.isEmpty()){
            return accessControlModulator().lookUpEntityById(from, localField, foreignField, as, projection, id);
        } else {
            return projections.get(0);
        }
    }

    public abstract AccessControlModulator<E, I> accessControlModulator();

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