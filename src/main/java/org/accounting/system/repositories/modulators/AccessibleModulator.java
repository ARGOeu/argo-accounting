package org.accounting.system.repositories.modulators;

import io.quarkus.mongodb.panache.PanacheQuery;
import io.quarkus.panache.common.Page;
import org.accounting.system.entities.Entity;

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
}