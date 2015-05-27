package com.jasify.schedule.appengine.dao;

/**
 * @author krico
 * @since 23/05/15.
 */
public class ExampleDao extends BaseDao<Example> {
    public ExampleDao() {
        super(ExampleMeta.get());
    }
}
