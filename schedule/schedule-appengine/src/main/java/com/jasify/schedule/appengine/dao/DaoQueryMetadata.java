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
public class DaoQueryMetadata implements Serializable {
    public static final String SUFFIX = ".cqm";
    private HashMap<Pair<? extends Class<? extends DaoQuery>, QueryParameters>, List<Key>> resultCache = new HashMap<>();

    public static String kindToId(String kind) {
        return kind + SUFFIX;
    }

    public static CacheQueryTransaction cacheQueryTransaction(DaoQuery query, String kind, Expiration expiration) {
        return new CacheQueryTransaction(query, kind, expiration);
    }

    private static Pair<? extends Class<? extends DaoQuery>, QueryParameters> key(DaoQuery query) {
        return Pair.of(query.getClass(), query.parameters());
    }

    private boolean containsQueryResult(DaoQuery query) {
        return resultCache.containsKey(key(query));
    }

    public List<Key> getQueryResult(DaoQuery query) {
        return resultCache.get(key(query));
    }

    private void putQueryResult(DaoQuery query, List<Key> results) {
        resultCache.put(key(query), results);
    }

    protected static class CacheQueryTransaction extends BaseMemcacheTransaction<DaoQueryMetadata> {
        private final DaoQuery query;
        private List<Key> results;
        private boolean skip = false;

        public CacheQueryTransaction(DaoQuery query, String kind, Expiration expiration) {
            super(kindToId(kind), expiration);
            this.query = query;
        }

        @Override
        public boolean skipMemcacheUpdate() {
            return skip;
        }

        @Nonnull
        @Override
        public DaoQueryMetadata execute(@Nullable MemcacheService.IdentifiableValue identifiable) {
            DaoQueryMetadata metadata = identifiable == null ? new DaoQueryMetadata() : (DaoQueryMetadata) identifiable.getValue();
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
