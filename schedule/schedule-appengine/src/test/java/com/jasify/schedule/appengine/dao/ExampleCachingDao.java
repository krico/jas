package com.jasify.schedule.appengine.dao;

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

}
