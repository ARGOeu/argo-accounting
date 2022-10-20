package org.accounting.system.repositories.modulators;

import io.quarkus.mongodb.panache.PanacheMongoRepository;
import io.quarkus.mongodb.panache.PanacheQuery;
import io.quarkus.panache.common.Page;
import org.accounting.system.entities.Entity;
import org.accounting.system.enums.ApiMessage;
import org.bson.Document;
import org.bson.conversions.Bson;

import javax.ws.rs.ForbiddenException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * This {@link AccessModulator} defines the operations that determine the degree of accessibility to Collection Entities. Each
 * class that extends {@link AccessModulator} must specify which entities will be accessible.
 * Since it implements {@link PanacheMongoRepository}, it also provides access to mongo collections. Because every Accounting
 * System API class that extends {@link Entity}, it represents a mongo collection.
 *
 * @param <E> Generic class that represents a mongo collection.
 */
public abstract class AccessModulator<E extends Entity, I> extends AbstractAccessModulator<E, I> {


    public void save(E entity) {
        throw new ForbiddenException(ApiMessage.NO_PERMISSION.message);
    }

    public E fetchEntityById(I id) {
        throw new ForbiddenException(ApiMessage.NO_PERMISSION.message);
    }

    public boolean deleteEntityById(I id) {
        throw new ForbiddenException(ApiMessage.NO_PERMISSION.message);
    }

    public E updateEntity(E entity, I id) {
        throw new ForbiddenException(ApiMessage.NO_PERMISSION.message);
    }

    public List<E> getAllEntities() {
        return Collections.emptyList();
    }

    /**
     * Executes a query in mongo database and returns the paginated results.
     * The page parameter indicates the requested page number, and the size parameter the number of entities by page.
     *
     * @param page The page to be retrieved
     * @param size The requested size of page
     * @return An object represents the paginated results
     */
    public PanacheQuery<E> findAllPageable(int page, int size) {
        throw new ForbiddenException(ApiMessage.NO_PERMISSION.message);
    }

    public <T> PanacheQuery<T> lookup(String from, String localField, String foreignField, String as, int page, int size, Class<T> projection) {
        throw new ForbiddenException(ApiMessage.NO_PERMISSION.message);
    }

    public List<E> combineTwoLists(List<E> a, List<E> b) {

        // We wanna avoid duplicates
        Set<E> setA = new HashSet<>(a);
        Set<E> setB = new HashSet<>(b);

        setA.addAll(setB);

        return new ArrayList<>(setA);
    }

    public <T> T lookUpEntityById(String from, String localField, String foreignField, String as, Class<T> projection, I id) {
        throw new ForbiddenException(ApiMessage.NO_PERMISSION.message);
    }

    public PanacheQuery<E> search(Bson query, int page, int size) {
        return find(Document.parse(query.toBsonDocument().toJson())).page(Page.of(page, size));
    }

    public PanacheQuery<E> search(Bson query) {
        return find(Document.parse(query.toBsonDocument().toJson()));
    }
}