package com.jasify.schedule.appengine.model.dao;

import com.google.appengine.api.datastore.Key;
import com.jasify.schedule.appengine.memcache.Memcache;
import com.jasify.schedule.appengine.model.EntityNotFoundException;
import org.slim3.datastore.Datastore;
import org.slim3.datastore.ModelMeta;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author krico
 * @since 25/05/15.
 */
public class BaseCachingDao<T> extends BaseDao<T> {
    protected BaseCachingDao(ModelMeta<T> meta) {
        super(meta);
    }

    protected boolean canCache() {
        return Datastore.getCurrentTransaction() == null;
    }

    private T cacheGet(@Nonnull Key id) {
        return Memcache.get(id);
    }

    private T cachePut(@Nonnull Key id, T entity) {
        Memcache.put(id, meta.modelToEntity(entity));
        return entity;
    }

    @Nonnull
    @Override
    public T get(@Nonnull Key id) throws EntityNotFoundException, IllegalArgumentException {
        if (!canCache()) {
            return super.get(id);
        }

        T ret = cacheGet(id);

        if (ret == null) {
            ret = cachePut(id, super.get(id));
        }
        return ret;
    }

    @Nullable
    @Override
    public T getOrNull(@Nonnull Key id) throws IllegalArgumentException {
        if (!canCache()) {
            return super.getOrNull(id);
        }

        T ret = cacheGet(id);

        if (ret == null) {
            ret = super.getOrNull(id);
            if (ret == null) {
                return null;
            }
            ret = cachePut(id, ret);
        }
        return ret;
    }
}
