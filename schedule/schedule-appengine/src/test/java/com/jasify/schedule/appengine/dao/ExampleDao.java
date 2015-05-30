package com.jasify.schedule.appengine.dao;

import com.google.appengine.api.datastore.Query;
import org.slim3.datastore.Datastore;

import java.util.List;

/**
 * @author krico
 * @since 23/05/15.
 */
public class ExampleDao extends BaseDao<Example> implements AnyExampleDao {
    public ExampleDao() {
        super(ExampleMeta.get());
    }

    @Override
    public List<Example> byDataType(String dataType) {
        ExampleMeta meta = getMeta();
        return Datastore
                .query(meta)
                .filter(meta.dataType.getName(), Query.FilterOperator.EQUAL, dataType)
                .asList();
    }
}
