package com.jasify.schedule.appengine.model.dao;

import com.google.appengine.api.datastore.Key;
import com.google.common.base.Optional;
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

    @Nonnull
    @Override
    public T get(@Nonnull Key id) throws EntityNotFoundException, IllegalArgumentException {
        if (!canCache()) {
            return super.get(id);
        }

        T ret = DaoUtil.cacheGet(id, meta);

        if (ret != null) {
            return ret;
        }

        return DaoUtil.cachePut(id, meta, super.get(id));
    }

    @Nullable
    @Override
    public T getOrNull(@Nonnull Key id) throws IllegalArgumentException {
        if (!canCache()) {
            return super.getOrNull(id);
        }

        Optional<T> optional = DaoUtil.cacheGetOrNull(id, meta);
        if (optional != null) {
            return optional.isPresent() ? optional.get() : null;
        }

        return DaoUtil.cachePut(id, meta, super.getOrNull(id));
    }
}
