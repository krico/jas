package com.jasify.schedule.appengine.dao;

import com.google.appengine.api.datastore.Transaction;
import org.junit.Test;
import org.slim3.datastore.Datastore;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertNull;

/**
 * @author krico
 * @since 25/05/15.
 */
public class BaseCachingDaoTest extends BaseDaoTest {
    @Override
    BaseDao<Example> createDao() {
        return new ExampleCachingDao();
    }

    @Test
    public void testGetUsesCachedValue() throws Exception {
        Example example = DaoUtilTest.createExample();
        example.setId(Datastore.allocateId(Example.class));
        DaoUtil.cachePut(example.getId(), ExampleMeta.get(), example);
        Example actual = dao.get(example.getId());
        assertEquals(example, actual);
    }

    @Test
    public void testSaveClearsCache() throws Exception {
        Example example = DaoUtilTest.createExample();
        example.setId(Datastore.allocateId(Example.class));
        DaoUtil.cachePut(example.getId(), ExampleMeta.get(), example);
        dao.save(example);
        assertNull(DaoUtil.cacheGet(example.getId(), ExampleMeta.get()));
    }

    @Test
    public void testDeleteClearsCache() throws Exception {
        Example example = DaoUtilTest.createExample();
        example.setId(Datastore.allocateId(Example.class));
        DaoUtil.cachePut(example.getId(), ExampleMeta.get(), example);
        dao.delete(example.getId());
        assertNull(DaoUtil.cacheGet(example.getId(), ExampleMeta.get()));
    }

    @Test
    public void testGetDoesNotCacheInTransaction() throws Exception {
        Transaction tx = beginTx();
        Example[] examples = new Example[5];
        for (int i = 0; i < examples.length; i++) {
            examples[i] = DaoUtilTest.createExample();
            examples[i].setId(Datastore.createKey(Example.class, i + 1));

            DaoUtil.cachePut(examples[i].getId(), ExampleMeta.get(), examples[i]); //put on cache

            assertNull(dao.getOrNull(examples[i].getId()));
            dao.save(examples[i]);
            assertNull(dao.getOrNull(examples[i].getId()));
        }
        tx.commit();
        for (Example example : examples) {
            assertEquals(example, dao.getOrNull(example.getId()));
            assertEquals(example, DaoUtil.cacheGet(example.getId(), ExampleMeta.get()));
        }
    }

    @Test
    public void testRollbackDoesNotPurgeCache() throws Exception {
        Transaction tx = beginTx();
        Example[] examples = new Example[5];
        for (int i = 0; i < examples.length; i++) {
            examples[i] = DaoUtilTest.createExample();
            examples[i].setId(Datastore.createKey(Example.class, i + 1));
            DaoUtil.cachePut(examples[i].getId(), ExampleMeta.get(), examples[i]); //put on cache
            dao.save(examples[i]);
        }
        tx.rollback();
        for (Example example : examples) {
            Example cached = dao.getOrNull(example.getId());
            assertEquals(cached, DaoUtil.cacheGet(example.getId(), ExampleMeta.get()));
            assertFalse(example.equals(cached));
        }
    }

}
