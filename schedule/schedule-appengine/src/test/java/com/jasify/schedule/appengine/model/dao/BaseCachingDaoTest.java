package com.jasify.schedule.appengine.model.dao;

/**
 * @author krico
 * @since 25/05/15.
 */
public class BaseCachingDaoTest extends BaseDaoTest {
    @Override
    BaseDao<Example> createDao() {
        return new ExampleCachingDao();
    }
}
