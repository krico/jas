package com.jasify.schedule.appengine.dao;

import com.google.appengine.api.datastore.*;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.jasify.schedule.appengine.memcache.Memcache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author krico
 * @since 25/05/15.
 */
class DaoDatastoreCallbacks {
    private static final Logger log = LoggerFactory.getLogger(DaoDatastoreCallbacks.class);
    private static final EntityToKey entityToKey = new EntityToKey();
    private static final KeyToKindQueryMetadata keyToKindQueryMetadata = new KeyToKindQueryMetadata();

    @PostPut
    void postPut(PutContext context) {
        // Handle batches only once
        if (context.getCurrentIndex() == 0) {
            List<Key> keys = Lists.transform(context.getElements(), entityToKey);
            Memcache.deleteAll(keys);
            purgeQueryCache(keys);
        }
    }

    @PostDelete
    void postDelete(DeleteContext context) {
        // Handle batches only once
        if (context.getCurrentIndex() == 0) {
            List<Key> keys = context.getElements();
            Memcache.deleteAll(keys);
            purgeQueryCache(keys);
        }
    }

    void purgeQueryCache(List<Key> keys) {
        Set<String> cqmKeys = new HashSet<>(Lists.transform(keys, keyToKindQueryMetadata));
        Memcache.deleteAll(cqmKeys);
    }

    private static class EntityToKey implements Function<Entity, Key> {
        @Nullable
        @Override
        public Key apply(Entity entity) {
            return entity.getKey();
        }
    }

    private static class KeyToKindQueryMetadata implements Function<Key, String> {
        @Nullable
        @Override
        public String apply(Key input) {
            return CachedQueryMetadata.kindToId(input.getKind());
        }
    }
}
