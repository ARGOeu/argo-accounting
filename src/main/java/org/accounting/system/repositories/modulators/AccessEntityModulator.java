package org.accounting.system.repositories.modulators;

import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import org.accounting.system.entities.Entity;
import org.accounting.system.entities.acl.AccessControl;
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
public abstract class AccessEntityModulator<E extends Entity, I, A extends AccessControl> extends AccessModulator<E, I, A> {

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

//    @Override
//    public PanacheQuery<E> findAllPageable(int page, int size) {
//
//        List<String> entities = accessControlModulator().getAccessControlEntities();
//
//        return find("creatorId = ?1 or _id in ?2", getRequestInformation().getSubjectOfToken(), Utility.transformIdsToSpecificClassType(getIdentity(), entities)).page(page, size);
//    }
//
//    @Override
//    public <T> ProjectionQuery<T> lookup(String from, String localField, String foreignField, String as, int page, int size, Class<T> projection) {
//
//        Bson bson = Aggregates.lookup(from, localField, foreignField, as);
//
//        List<String> entities = accessControlModulator().getAccessControlEntities();
//
//        List<T> projections = getMongoCollection()
//                .aggregate(List
//                        .of(bson,
//                                Aggregates.skip(size * (page)),
//                                Aggregates.limit(size),
//                                Aggregates.match(Filters.or(Filters.eq("creatorId", getRequestInformation().getSubjectOfToken()), Filters.in("_id",  Utility.transformIdsToSpecificClassType(getIdentity(), entities))))
//                        ), projection)
//                .into(new ArrayList<>());
//
//        Document totalDocuments = getMongoCollection()
//                .aggregate(List
//                        .of(Aggregates.match(Filters.or(Filters.eq("creatorId", getRequestInformation().getSubjectOfToken()), Filters.in("_id",  Utility.transformIdsToSpecificClassType(getIdentity(), entities)))), Aggregates.count()))
//                .first();
//
//        var projectionQuery = new ProjectionQuery<T>();
//
//        projectionQuery.list = projections;
//        projectionQuery.index = page;
//        projectionQuery.size = size;
//        projectionQuery.count = Long.parseLong(totalDocuments.get("count").toString());
//
//        return projectionQuery;
//    }

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

//    @Override
//    public void grantPermission(A accessControl) {
//
//        E entity = findById(Utility.transformIdToSpecificClassType(getIdentity(), accessControl.getEntity()));
//
//        if (isIdentifiable(entity.getCreatorId())) {
//            getAccessControlRepository().persist(accessControl);
//        } else {
//            accessControlModulator().grantPermission(accessControl);
//        }
//    }
//
//    @Override
//    public void modifyPermission(A accessControl) {
//
//        if (isIdentifiable(accessControl.getCreatorId())) {
//            getAccessControlRepository().update(accessControl);
//        } else {
//            accessControlModulator().modifyPermission(accessControl);
//        }
//    }
//
//    @Override
//    public void deletePermission(A accessControl) {
//
//        if (isIdentifiable(accessControl.getCreatorId())) {
//            getAccessControlRepository().delete(accessControl);
//        } else {
//            accessControlModulator().deletePermission(accessControl);
//        }
//    }
//
//    @Override
//    public A getPermission(String entity, String who) {
//
//        var accessControl = getAccessControlRepository().findByWhoAndCollectionAndEntity(who, collection(), entity);
//
//        if(isIdentifiable(accessControl.getCreatorId())){
//            return accessControl;
//        } else {
//            return accessControlModulator().getPermission(entity, who);
//        }
//    }
//
//    @Override
//    public List<A> getAllPermissions() {
//        return getAccessControlRepository().findAllByCollectionAndCreatorId(collection(), getRequestInformation().getSubjectOfToken());
//    }

    public abstract AccessControlModulator<E, I, A> accessControlModulator();

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