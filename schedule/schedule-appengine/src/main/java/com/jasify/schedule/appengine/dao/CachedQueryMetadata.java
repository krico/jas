package com.jasify.schedule.appengine.dao;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.memcache.Expiration;
import com.google.appengine.api.memcache.MemcacheService;
import com.jasify.schedule.appengine.memcache.BaseMemcacheTransaction;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

/**
 * @author krico
 * @since 02/06/15.
 */
public class CachedQueryMetadata implements Serializable {
    public static final String SUFFIX = ".cqm";
    private HashMap<Pair<? extends Class<? extends CachedQuery>, CachedQuery.Parameters>, List<Key>> resultCache = new HashMap<>();

    public static String kindToId(String kind) {
        return kind + SUFFIX;
    }

    public static CacheQueryTransaction cacheQueryTransaction(CachedQuery query, String kind, Expiration expiration) {
        return new CacheQueryTransaction(query, kind, expiration);
    }

    private static Pair<? extends Class<? extends CachedQuery>, CachedQuery.Parameters> key(CachedQuery query) {
        return Pair.of(query.getClass(), query.parameters());
    }

    private boolean containsQueryResult(CachedQuery query) {
        return resultCache.containsKey(key(query));
    }

    public List<Key> getQueryResult(CachedQuery query) {
        return resultCache.get(key(query));
    }

    private void putQueryResult(CachedQuery query, List<Key> results) {
        resultCache.put(key(query), results);
    }

    protected static class CacheQueryTransaction extends BaseMemcacheTransaction<CachedQueryMetadata> {
        private final CachedQuery query;
        private List<Key> results;
        private boolean skip = false;

        public CacheQueryTransaction(CachedQuery query, String kind, Expiration expiration) {
            super(kindToId(kind), expiration);
            this.query = query;
        }

        @Override
        public boolean skipMemcacheUpdate() {
            return skip;
        }

        @Nonnull
        @Override
        public CachedQueryMetadata execute(@Nullable MemcacheService.IdentifiableValue identifiable) {
            CachedQueryMetadata metadata = identifiable == null ? new CachedQueryMetadata() : (CachedQueryMetadata) identifiable.getValue();
            if (metadata.containsQueryResult(query)) {
                skip = true;
                return metadata;
            }
            skip = false;
            if (results == null) { //we don't want to run the query twice
                results = query.execute();
            }
            metadata.putQueryResult(query, results);
            return metadata;
        }
    }
}
