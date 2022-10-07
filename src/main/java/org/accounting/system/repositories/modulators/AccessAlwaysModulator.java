package org.accounting.system.repositories.modulators;

import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import io.quarkus.mongodb.panache.PanacheQuery;
import io.quarkus.panache.common.Page;
import org.accounting.system.entities.Entity;
import org.accounting.system.entities.acl.AccessControl;
import org.accounting.system.entities.projections.MongoQuery;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.List;

/**
 * This {@link AccessAlwaysModulator} stipulates that all available operations in the various collections
 * will always be accessible without any restrictions. As a result, all collection entities are accessible.
 *
 * @param <E> Generic class that represents a mongo collection.
 */
public abstract class AccessAlwaysModulator<E extends Entity, I, A extends AccessControl> extends AccessModulator<E, I, A> {

    @Override
    public void save(E entity) {
        persist(entity);
    }

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
    public PanacheQuery<E> findAllPageable(int page, int size) {
        return findAll().page(Page.of(page, size));
    }

    @Override
    public <T> PanacheQuery<T> lookup(String from, String localField, String foreignField, String as, int page, int size, Class<T> projection) {

        Bson bson = Aggregates.lookup(from, localField, foreignField, as);

        List<T> projections = getMongoCollection().aggregate(List.of(bson, Aggregates.skip(size * (page)), Aggregates.limit(size)), projection).into(new ArrayList<>());

        var projectionQuery = new MongoQuery<T>();

        projectionQuery.list = projections;
        projectionQuery.index = page;
        projectionQuery.size = size;
        projectionQuery.count = getMongoCollection().countDocuments();
        projectionQuery.page = Page.of(page, size);

        return projectionQuery;
    }

    @Override
    public <T> T lookUpEntityById(String from, String localField, String foreignField, String as, Class<T> projection, I id) {

        Bson bson = Aggregates.lookup(from, localField, foreignField, as);
        List<T> projections = getMongoCollection().aggregate(List.of(bson, Aggregates.match(Filters.eq("_id", id))), projection).into(new ArrayList<>());
        return projections.get(0);
    }

//    @Override
//    public void grantPermission(A accessControl) {
//        getAccessControlRepository().persist(accessControl);
//    }
//
//    @Override
//    public void modifyPermission(A accessControl) {
//        getAccessControlRepository().update(accessControl);
//    }
//
//    @Override
//    public void deletePermission(A accessControl) {
//        getAccessControlRepository().delete(accessControl);
//    }
//
//    @Override
//    public A getPermission(String entity, String who) {
//        return getAccessControlRepository().findByWhoAndCollectionAndEntity(who, collection(), entity);
//    }
//
//    @Override
//    public List<A> getAllPermissions() {
//        return getAccessControlRepository().findAllByCollection(collection());
//    }
}