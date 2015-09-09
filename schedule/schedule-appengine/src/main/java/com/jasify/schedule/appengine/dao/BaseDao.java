package com.jasify.schedule.appengine.dao;

import com.google.api.client.repackaged.com.google.common.base.Throwables;
import com.google.appengine.api.datastore.Key;
import com.jasify.schedule.appengine.model.EntityNotFoundException;
import com.jasify.schedule.appengine.model.ModelException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slim3.datastore.Datastore;
import org.slim3.datastore.EntityNotFoundRuntimeException;
import org.slim3.datastore.ModelMeta;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

/**
 * @author krico
 * @since 23/05/15.
 */
public abstract class BaseDao<T> {
    private static final Logger log = LoggerFactory.getLogger(BaseDao.class);

    protected final ModelMeta<T> meta;

    protected BaseDao(@Nonnull ModelMeta<T> meta) {
        this.meta = meta;
    }

    //Syntax sugar
    @SuppressWarnings("unchecked")
    protected <X extends ModelMeta<T>> X getMeta() {
        return (X) meta;
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
     * @throws com.jasify.schedule.appengine.model.ModelException in case of exceptions
     */
    @CurrentTransaction
    @Nonnull
    public Key save(@Nonnull T entity) throws ModelException {
        return Datastore.put(entity);
    }

    /**
     * Creates or updates the entity (propagates exceptions as runtime)
     *
     * @param entity to be created or updated
     * @return the primaryKey of the entity
     */
    @CurrentTransaction
    @Nonnull
    public Key saveNoEx(@Nonnull T entity) {
        try {
            return save(entity);
        } catch (ModelException e) {
            throw Throwables.propagate(e);
        }
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

    @CurrentTransaction
    @Nonnull
    public List<T> get(@Nonnull List<Key> ids) throws EntityNotFoundException, IllegalArgumentException {
        try {
            return Datastore.get(meta, ids);
        } catch (EntityNotFoundRuntimeException e) {
            throw new EntityNotFoundException(e);
        }
    }

    @CurrentTransaction
    @Nonnull
    public List<Key> save(@Nonnull List<T> entities) throws ModelException {
        return Datastore.put(entities);
    }

    @CurrentTransaction
    @Nonnull
    public List<Key> saveNoEx(@Nonnull List<T> entities) {
        try {
            return save(entities);
        } catch (ModelException e) {
            throw Throwables.propagate(e);
        }
    }

    @CurrentTransaction
    public void delete(@Nonnull List<Key> ids) {
        Datastore.delete(ids);
    }

    protected List<Key> queryKeys(@Nonnull DaoQuery query) {
        return query.execute();
    }

    protected List<T> query(@Nonnull DaoQuery query) {
        List<Key> ids = queryKeys(query);
        if (ids.isEmpty()) return Collections.emptyList();
        try {
            return get(ids);
        } catch (EntityNotFoundException e) {
            log.warn("A Query returned ids for non-existing entities", e);
            throw Throwables.propagate(e);
        }
    }
}
