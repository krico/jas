package com.jasify.schedule.appengine.model.dao;

import com.google.appengine.api.datastore.*;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.jasify.schedule.appengine.memcache.Memcache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.util.List;

/**
 * @author krico
 * @since 25/05/15.
 */
class DaoDatastoreCallbacks {
    private static final Logger log = LoggerFactory.getLogger(DaoDatastoreCallbacks.class);
    private static final EntityToKey entityToKey = new EntityToKey();

    @PostPut
    void postPut(PutContext context) {
        // Handle batches only once
        if (context.getCurrentIndex() == 0) {
            List<Key> keys = Lists.transform(context.getElements(), entityToKey);
            log.info("POST-PUT index: {} keys: {}", context.getCurrentIndex(), keys);
            Memcache.deleteAll(keys);
        }
    }

    @PostDelete
    void postDelete(DeleteContext context) {
        // Handle batches only once
        if (context.getCurrentIndex() == 0) {
            List<Key> keys = context.getElements();
            log.info("POST-PUT index: {} keys: {}", context.getCurrentIndex(), keys);
            Memcache.deleteAll(keys);
        }
    }

    private static class EntityToKey implements Function<Entity, Key> {
        @Nullable
        @Override
        public Key apply(Entity entity) {
            return entity.getKey();
        }
    }
}
