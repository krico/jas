package com.jasify.schedule.appengine.dao;

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
        return query(new ExampleDaoQuery(meta, dataType));
    }
}
