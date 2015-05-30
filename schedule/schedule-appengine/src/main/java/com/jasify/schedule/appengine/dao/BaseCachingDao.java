package com.jasify.schedule.appengine.dao;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.memcache.Expiration;
import com.google.common.base.Optional;
import com.jasify.schedule.appengine.model.EntityNotFoundException;
import org.slim3.datastore.Datastore;
import org.slim3.datastore.ModelMeta;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.concurrent.TimeUnit;

/**
 * @author krico
 * @since 25/05/15.
 */
public class BaseCachingDao<T> extends BaseDao<T> {
    public static final int DEFAULT_CACHE_EXPIRY_SECONDS = (int) TimeUnit.MINUTES.toSeconds(30);
    private final int cacheExpirySeconds;

    protected BaseCachingDao(ModelMeta<T> meta, int cacheExpirySeconds) {
        super(meta);
        this.cacheExpirySeconds = cacheExpirySeconds;
    }

    protected BaseCachingDao(ModelMeta<T> meta) {
        this(meta, DEFAULT_CACHE_EXPIRY_SECONDS);
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

        return DaoUtil.cachePut(id, meta, super.get(id), Expiration.byDeltaSeconds(cacheExpirySeconds));
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

        return DaoUtil.cachePut(id, meta, super.getOrNull(id), Expiration.byDeltaSeconds(cacheExpirySeconds));
    }
}
