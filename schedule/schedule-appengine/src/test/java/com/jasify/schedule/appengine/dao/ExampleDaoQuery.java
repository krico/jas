package com.jasify.schedule.appengine.dao;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Query;
import org.slim3.datastore.Datastore;

import java.io.Serializable;
import java.util.List;

/**
 * @author krico
 * @since 03/06/15.
 */
class ExampleDaoQuery extends BaseDaoQuery<Example, ExampleMeta> {

    ExampleDaoQuery(ExampleMeta meta, String dataType) {
        super(meta, new Serializable[]{dataType});
    }

    @Override
    public List<Key> execute() {
        return Datastore
                .query(meta)
                .filter(meta.dataType.getName(), Query.FilterOperator.EQUAL, parameters.get(0))
                .asKeyList();
    }
}
