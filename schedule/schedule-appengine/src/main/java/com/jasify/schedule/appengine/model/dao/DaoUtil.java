package com.jasify.schedule.appengine.model.dao;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
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

    public static <T> T cachePut(@Nonnull Key id, @Nonnull ModelMeta<T> meta, @Nonnull T entity) {
        try {
            Memcache.put(id, meta.modelToEntity(entity));
        } catch (IllegalArgumentException e) {
            log.warn("Memcache exception IGNORED", e);
        }
        return entity;
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
}
