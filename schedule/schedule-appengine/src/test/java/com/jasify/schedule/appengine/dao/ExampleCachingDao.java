package com.jasify.schedule.appengine.dao;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Query;
import org.slim3.datastore.Datastore;

import java.io.Serializable;
import java.util.List;

/**
 * @author krico
 * @since 25/05/15.
 */
public class ExampleCachingDao extends BaseCachingDao<Example> implements AnyExampleDao {

    public ExampleCachingDao() {
        super(ExampleMeta.get());
    }

    @Override
    public List<Example> byDataType(String dataType) {
        return query(new ByDataTypeQuery(dataType));
    }

    private class ByDataTypeQuery implements CachedQuery {
        private final Parameters parameters;

        private ByDataTypeQuery(String dataType) {
            this.parameters = Parameters.of(new Serializable[]{dataType});
        }

        @Override
        public String key() {
            return getClass().getSimpleName() + "#" + parameters;
        }

        @Override
        public Parameters parameters() {
            return parameters;
        }

        @Override
        public List<Key> execute() {
            ExampleMeta meta = getMeta();
            return Datastore
                    .query(meta)
                    .filter(meta.dataType.getName(), Query.FilterOperator.EQUAL, parameters.get(0))
                    .asKeyList();
        }
    }

}
