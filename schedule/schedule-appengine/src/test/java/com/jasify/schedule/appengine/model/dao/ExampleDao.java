package com.jasify.schedule.appengine.model.dao;

import com.jasify.schedule.appengine.meta.dao.ExampleMeta;

/**
 * @author krico
 * @since 23/05/15.
 */
public class ExampleDao extends BaseDao<Example> {
    public ExampleDao() {
        super(ExampleMeta.get());
    }
}
