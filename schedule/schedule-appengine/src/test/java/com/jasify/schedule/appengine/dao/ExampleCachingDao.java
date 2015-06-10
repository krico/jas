package com.jasify.schedule.appengine.dao;

import com.google.appengine.api.datastore.Key;

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
        ExampleMeta meta = getMeta();
        return query(new ExampleDaoQuery(meta, dataType));
    }

    @Override
    public List<Example> byAncestor(Key ancestor) {
        ExampleMeta meta = getMeta();
        return query(new ExampleDaoAncestorQuery(meta, ancestor));
    }
}
