package com.jasify.schedule.appengine.dao;

import com.jasify.schedule.appengine.model.UniqueConstraint;
import com.jasify.schedule.appengine.model.UniqueConstraintException;
import org.slim3.datastore.ModelMeta;
import org.slim3.datastore.StringAttributeMeta;

import java.util.HashMap;
import java.util.Map;

/**
 * @author krico
 * @since 29/05/15.
 */
public final class UniqueIndexCache {
    private static final Map<String, UniqueIndex> uniqueIndexCache = new HashMap<>();
    private static final Map<String, UniquePairIndex> uniquePairIndexCache = new HashMap<>();

    private UniqueIndexCache() {
    }

    public static void clear() {
        synchronized (uniqueIndexCache) {
            uniqueIndexCache.clear();
        }
        synchronized (uniquePairIndexCache) {
            uniquePairIndexCache.clear();
        }
    }

    public static <T> UniqueIndex get(String indexName, ModelMeta<T> meta, StringAttributeMeta<T> uniqueProperty,
                                      boolean allowNullValues) {
        synchronized (uniqueIndexCache) {
            if (!uniqueIndexCache.containsKey(indexName)) {
                uniqueIndexCache.put(indexName, new UniqueIndexImpl(meta, uniqueProperty, allowNullValues));
            }
            return uniqueIndexCache.get(indexName);
        }
    }

    public static <T> UniquePairIndex get(String indexName, ModelMeta<T> meta, StringAttributeMeta<T> uniqueProperty,
                                          StringAttributeMeta<T> uniqueClassifierProperty, boolean allowNullValues) {
        synchronized (uniquePairIndexCache) {
            if (!uniquePairIndexCache.containsKey(indexName)) {
                UniquePairIndexImpl index = new UniquePairIndexImpl(meta, uniqueProperty, uniqueClassifierProperty, allowNullValues);
                uniquePairIndexCache.put(indexName, index);
            }
            return uniquePairIndexCache.get(indexName);
        }
    }

    private static class UniqueIndexImpl implements UniqueIndex {
        private final UniqueConstraint constraint;

        public <T> UniqueIndexImpl(ModelMeta<T> meta, StringAttributeMeta<T> uniqueProperty, boolean allowNullValues) {
            constraint = UniqueConstraint.create(meta, uniqueProperty, null, allowNullValues);
        }

        @Override
        public void reserve(String uniqueValue) throws UniqueConstraintException {
            constraint.reserveInCurrentTransaction(uniqueValue);
        }

        @Override
        public void release(String uniqueValue) {
            constraint.releaseInCurrentTransaction(uniqueValue);
        }
    }

    private static class UniquePairIndexImpl implements UniquePairIndex {
        private final UniqueConstraint constraint;

        public <T> UniquePairIndexImpl(ModelMeta<T> meta, StringAttributeMeta<T> uniqueProperty, StringAttributeMeta<T> uniqueClassifierProperty, boolean allowNullValue) {
            constraint = UniqueConstraint.create(meta, uniqueProperty, uniqueClassifierProperty, allowNullValue);
        }

        @Override
        public void reserve(String uniqueValue, String classifier) throws UniqueConstraintException {
            constraint.reserveInCurrentTransaction(uniqueValue, classifier);
        }

        @Override
        public void release(String uniqueValue, String classifier) {
            constraint.releaseInCurrentTransaction(uniqueValue, classifier);
        }
    }
}
