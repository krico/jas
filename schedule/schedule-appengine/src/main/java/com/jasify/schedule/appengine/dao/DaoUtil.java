package com.jasify.schedule.appengine.dao;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.memcache.Expiration;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.labs.repackaged.com.google.common.base.Function;
import com.google.appengine.labs.repackaged.com.google.common.collect.Maps;
import com.google.common.base.Optional;
import com.jasify.schedule.appengine.memcache.Memcache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slim3.datastore.ModelMeta;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author krico
 * @since 25/05/15.
 */
public final class DaoUtil {
    private static final Logger log = LoggerFactory.getLogger(DaoUtil.class);

    private DaoUtil() {
    }

    private static <T> Serializable marshall(@Nonnull ModelMeta<T> meta, T model) {
        if (model == null) return null;
        return meta.modelToJson(model);
    }

    private static <T> T unMarshall(@Nonnull ModelMeta<T> meta, Serializable cached) {
        return meta.jsonToModel((String) cached);
    }

    public static <T> T cachePut(@Nonnull Key id, @Nonnull ModelMeta<T> meta, T model, Expiration expiration) {
        try {
            /* This works together with DaoDatastoreCallbacks.purgeCache DEFAULT_MILLIS_NO_RE_ADD */
            Memcache.put(id, marshall(meta, model), expiration, MemcacheService.SetPolicy.ADD_ONLY_IF_NOT_PRESENT);
        } catch (IllegalArgumentException e) {
            log.warn("Memcache exception IGNORED", e);
        }
        return model;
    }

    public static <T> T cachePut(@Nonnull Key id, @Nonnull ModelMeta<T> meta, T model) {
        return cachePut(id, meta, model, null);
    }

    public static <T> void cachePutAll(@Nonnull Map<Key, T> kvp, @Nonnull final ModelMeta<T> meta) {
        cachePutAll(kvp, meta, null);
    }

    public static <T> void cachePutAll(@Nonnull Map<Key, T> kvp, @Nonnull final ModelMeta<T> meta, Expiration expiration) {
        try {
            Map<Key, Serializable> keyEntityMap = Maps.transformValues(kvp, new Function<T, Serializable>() {
                @Nullable
                @Override
                public Serializable apply(@Nullable T model) {
                    return marshall(meta, model);
                }
            });
            /* This works together with DaoDatastoreCallbacks.purgeCache DEFAULT_MILLIS_NO_RE_ADD */
            Memcache.putAll(keyEntityMap, expiration, MemcacheService.SetPolicy.ADD_ONLY_IF_NOT_PRESENT);
        } catch (IllegalArgumentException e) {
            log.warn("Memcache exception IGNORED", e);
        }
    }

    public static <T> T cacheGet(@Nonnull Key id, @Nonnull ModelMeta<T> meta) {
        T ret = null;
        try {
            Serializable cached = Memcache.get(id);
            if (cached != null) {
                ret = unMarshall(meta, cached);
            }
        } catch (IllegalArgumentException e) {
            log.warn("Memcache exception IGNORED", e);
        }
        return ret;
    }

    public static <T> Map<Key, T> cacheGet(@Nonnull List<Key> ids, @Nonnull final ModelMeta<T> meta) {
        Map<Key, T> modelMap = Maps.newHashMap();
        try {
            Map<Key, Serializable> cached = Memcache.getAll(ids);
            modelMap = Maps.transformValues(cached, new Function<Serializable, T>() {
                @Nullable
                @Override
                public T apply(Serializable cached) {
                    return unMarshall(meta, cached);
                }
            });
        } catch (IllegalArgumentException e) {
            log.warn("Memcache exception IGNORED", e);
        }
        return modelMap;
    }

    /**
     * Cache method that remembers if <code>id</code> mapped to null
     *
     * @param id   to search by
     * @param meta to transform
     * @param <T>  entity type
     * @return An Optional holding a reference to the entity, or no reference if the key was present mapped to a null value,
     * if no key was present, null is returned
     */
    public static <T> Optional<T> cacheGetOrNull(@Nonnull Key id, @Nonnull ModelMeta<T> meta) {
        Serializable cached = Memcache.get(id);
        if (cached == null) {
            if (Memcache.contains(id)) {
                return Optional.absent();
            } else {
                return null;
            }
        }
        return Optional.of(unMarshall(meta, cached));
    }
}
