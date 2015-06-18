package com.jasify.schedule.appengine.dao;

import com.google.api.client.repackaged.com.google.common.base.Throwables;
import com.google.api.client.util.Lists;
import com.google.api.client.util.Maps;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.memcache.Expiration;
import com.google.common.base.Optional;
import com.jasify.schedule.appengine.memcache.MemcacheOperator;
import com.jasify.schedule.appengine.model.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slim3.datastore.Datastore;
import org.slim3.datastore.ModelMeta;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author krico
 * @since 25/05/15.
 */
public class BaseCachingDao<T> extends BaseDao<T> {
    public static final int DEFAULT_CACHE_EXPIRY_SECONDS = (int) TimeUnit.MINUTES.toSeconds(30);
    private static final Logger log = LoggerFactory.getLogger(BaseCachingDao.class);
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

        return DaoUtil.cachePut(id, meta, super.get(id), expiration());
    }

    @Nonnull
    @Override
    public List<T> get(@Nonnull List<Key> ids) throws EntityNotFoundException, IllegalArgumentException {
        if (!canCache()) {
            return super.get(ids);
        }

        List<T> ret = Lists.newArrayList();

        Map<Key, T> cached = DaoUtil.cacheGet(ids, meta);

        List<Key> missing = Lists.newArrayList();
        for (Key id : ids) {
            if (cached.containsKey(id)) continue;
            missing.add(id);
        }

        if (missing.isEmpty()) {
            // ALL CACHED
            for (Key id : ids) {
                ret.add(cached.get(id));
            }
        } else {
            // Go fetch from datastore
            List<T> retrieved = super.get(missing);

            //Cache it
            Map<Key, T> toCache = Maps.newHashMap();
            for (int i = 0; i < missing.size(); ++i) {
                toCache.put(missing.get(i), retrieved.get(i));
            }
            DaoUtil.cachePutAll(toCache, meta, expiration());

            //Combine previously cached and newly fetched
            for (Key id : ids) {
                if (cached.containsKey(id)) {
                    ret.add(cached.get(id));
                } else {
                    ret.add(toCache.get(id));
                }
            }
        }

        return ret;
    }

    private Expiration expiration() {
        return Expiration.byDeltaSeconds(cacheExpirySeconds);
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

        return DaoUtil.cachePut(id, meta, super.getOrNull(id), expiration());
    }

    @Override
    protected List<Key> queryKeys(@Nonnull DaoQuery query) {
        if (!canCache()) {
            return super.queryKeys(query);
        }

        DaoQueryMetadata.CacheQueryTransaction operation = DaoQueryMetadata
                .cacheQueryTransaction(query, meta.getKind(), expiration());

        DaoQueryMetadata metadata = MemcacheOperator.update(operation);
        return metadata.getQueryResult(query);
    }
}
