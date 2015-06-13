package com.jasify.schedule.appengine.dao;

import com.google.appengine.api.datastore.*;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.util.ArrayList;
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

    private static void purgeCache(List<Key> elementKeys) {
        Set<String> cqmKeys = new HashSet<>(Lists.transform(elementKeys, keyToKindQueryMetadata));

        ArrayList<Object> deleteKeys = new ArrayList<Object>(cqmKeys);
        deleteKeys.addAll(elementKeys);

        DaoUtil.deleteAll(deleteKeys);
    }

    @PostPut
    void postPut(PutContext context) {
        // Handle batches only once
        if (context.getCurrentIndex() == 0) {
            List<Key> elementKeys = Lists.transform(context.getElements(), entityToKey);
            purgeCache(elementKeys);
        }
    }

    @PostDelete
    void postDelete(DeleteContext context) {
        // Handle batches only once
        if (context.getCurrentIndex() == 0) {
            List<Key> elementKeys = context.getElements();
            purgeCache(elementKeys);
        }
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
            return DaoQueryMetadata.kindToId(input.getKind());
        }
    }
}
