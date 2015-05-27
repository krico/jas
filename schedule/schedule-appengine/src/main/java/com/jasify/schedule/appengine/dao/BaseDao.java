package com.jasify.schedule.appengine.dao;

import com.google.appengine.api.datastore.Key;
import com.jasify.schedule.appengine.model.EntityNotFoundException;
import org.slim3.datastore.Datastore;
import org.slim3.datastore.EntityNotFoundRuntimeException;
import org.slim3.datastore.ModelMeta;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author krico
 * @since 23/05/15.
 */
public abstract class BaseDao<T> {
    protected final ModelMeta<T> meta;

    protected BaseDao(@Nonnull ModelMeta<T> meta) {
        this.meta = meta;
    }

    /**
     * Get an entity
     *
     * @param id primaryKey of the entity to get
     * @return the entity with primaryKey == id
     * @throws EntityNotFoundException  if no entity with primaryKey id exists
     * @throws IllegalArgumentException if the entity with primaryKey==id is not of type <code>T</code>
     */
    @CurrentTransaction
    @Nonnull
    public T get(@Nonnull Key id) throws EntityNotFoundException, IllegalArgumentException {
        try {
            return Datastore.get(meta, id);
        } catch (EntityNotFoundRuntimeException e) {
            throw new EntityNotFoundException(e);
        }
    }

    /**
     * Get an entity or null
     *
     * @param id primaryKey of the entity to get
     * @return the entity with primaryKey == id or null
     * @throws IllegalArgumentException if the entity with primaryKey==id is not null and not of type <code>T</code>
     */
    @CurrentTransaction
    @Nullable
    public T getOrNull(@Nonnull Key id) throws IllegalArgumentException {
        return Datastore.getOrNull(meta, id);
    }

    /**
     * Creates or updates the entity
     *
     * @param entity to be created or updated
     * @return the primaryKey of the entity
     */
    @CurrentTransaction
    @Nonnull
    public Key save(@Nonnull T entity) {
        return Datastore.put(entity);
    }

    /**
     * Deletes the entity with primaryKey <code>id</code>
     *
     * @param id is the primary key of the entity to be deleted
     */
    @CurrentTransaction
    public void delete(@Nonnull Key id) {
        Datastore.delete(id);
    }
}
