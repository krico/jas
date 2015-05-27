package com.jasify.schedule.appengine.dao;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.common.base.Optional;
import com.jasify.schedule.appengine.memcache.Memcache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slim3.datastore.ModelMeta;

import javax.annotation.Nonnull;

/**
 * @author krico
 * @since 25/05/15.
 */
public final class DaoUtil {
    private static final Logger log = LoggerFactory.getLogger(DaoUtil.class);

    private DaoUtil() {
    }

    public static <T> T cachePut(@Nonnull Key id, @Nonnull ModelMeta<T> meta, T model) {
        try {
            Entity entity = model == null ? null : meta.modelToEntity(model);
            Memcache.put(id, entity);
        } catch (IllegalArgumentException e) {
            log.warn("Memcache exception IGNORED", e);
        }
        return model;
    }

    public static <T> T cacheGet(@Nonnull Key id, @Nonnull ModelMeta<T> meta) {
        T ret = null;
        try {
            Entity entity = Memcache.get(id);
            if (entity != null) {
                ret = meta.entityToModel(entity);
            }
        } catch (IllegalArgumentException e) {
            log.warn("Memcache exception IGNORED", e);
        }
        return ret;
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
        Entity entity = Memcache.get(id);
        if (entity == null) {
            if (Memcache.contains(id)) {
                return Optional.absent();
            } else {
                return null;
            }
        }
        return Optional.of(meta.entityToModel(entity));
    }
}
