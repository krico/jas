package com.jasify.schedule.appengine.model.dao;

import com.jasify.schedule.appengine.meta.dao.ExampleMeta;

/**
 * @author krico
 * @since 25/05/15.
 */
public class ExampleCachingDao extends BaseCachingDao<Example> {
    public ExampleCachingDao() {
        super(ExampleMeta.get());
    }
}
