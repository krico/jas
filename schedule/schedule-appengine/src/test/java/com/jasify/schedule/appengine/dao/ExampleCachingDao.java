package com.jasify.schedule.appengine.dao;

/**
 * @author krico
 * @since 25/05/15.
 */
public class ExampleCachingDao extends BaseCachingDao<Example> {
    public ExampleCachingDao() {
        super(ExampleMeta.get());
    }
}
