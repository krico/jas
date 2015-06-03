package com.jasify.schedule.appengine.dao;

import com.google.api.client.util.Maps;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Transaction;
import com.jasify.schedule.appengine.model.EntityNotFoundException;
import org.junit.Test;
import org.slim3.datastore.Datastore;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static com.jasify.schedule.appengine.dao.DaoUtilTest.META;
import static com.jasify.schedule.appengine.dao.DaoUtilTest.createExample;
import static junit.framework.TestCase.*;

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
        Example example = createExample();
        example.setId(Datastore.allocateId(Example.class));
        DaoUtil.cachePut(example.getId(), ExampleMeta.get(), example);
        Example actual = dao.get(example.getId());
        assertEquals(example, actual);
    }

    @Test
    public void testSaveClearsCache() throws Exception {
        Example example = createExample();
        example.setId(Datastore.allocateId(Example.class));
        DaoUtil.cachePut(example.getId(), ExampleMeta.get(), example);
        dao.save(example);
        assertNull(DaoUtil.cacheGet(example.getId(), ExampleMeta.get()));
    }

    @Test
    public void testDeleteClearsCache() throws Exception {
        Example example = createExample();
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
            examples[i] = createExample();
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
            examples[i] = createExample();
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

    @Test
    public void testGetAllUsesCache() throws Exception {
        Example example1 = createExample();
        example1.setId(Datastore.allocateId(Example.class));
        Example example2 = createExample();
        example2.setId(Datastore.allocateId(Example.class));
        Map<Key, Example> cache = Maps.newHashMap();
        cache.put(example1.getId(), example1);
        cache.put(example2.getId(), example2);
        DaoUtil.cachePutAll(cache, META);
        List<Example> fetched = dao.get(Arrays.asList(example1.getId(), example2.getId()));
        assertNotNull(fetched);
        assertEquals(2, fetched.size());
        assertEquals(example1, fetched.get(0));
        assertEquals(example2, fetched.get(1));
    }

    @Test(expected = EntityNotFoundException.class)
    public void testGetAllCombinesCacheThrowsIfMissing() throws Exception {
        Example example1 = createExample();
        example1.setId(Datastore.allocateId(Example.class));
        Example example2 = createExample();
        example2.setId(Datastore.allocateId(Example.class));

        DaoUtil.cachePut(example1.getId(), META, example1);

        dao.get(Arrays.asList(example1.getId(), example2.getId()));
    }

    @Test
    public void testGetAllCombinesCache() throws Exception {
        Example example1 = createExample();
        example1.setId(Datastore.allocateId(Example.class));
        Example example2 = createExample();
        example2.setId(Datastore.allocateId(Example.class));

        DaoUtil.cachePut(example1.getId(), META, example1);
        dao.save(example2);

        for (int i = 0; i < 3; ++i) {
            List<Example> fetched = dao.get(Arrays.asList(example1.getId(), example2.getId()));
            assertNotNull(fetched);
            assertEquals(2, fetched.size());
            assertEquals(example1, fetched.get(0));
            assertEquals(example2, fetched.get(1));
        }
    }

    @Test
    public void testGetAllIgnoresCacheInTransaction() throws Exception {
        Example example1 = createExample();
        example1.setId(Datastore.allocateId(Example.class));
        Example example2 = createExample();
        example2.setId(Datastore.allocateId(Example.class));

        dao.save(example1);
        dao.save(example2);

        // Put bad things in the cache
        DaoUtil.cachePut(example1.getId(), META, createExample());
        DaoUtil.cachePut(example1.getId(), META, createExample());


        for (int i = 0; i < 3; ++i) {
            Transaction tx = beginTx();
            try {
                List<Example> fetched = dao.get(Arrays.asList(example1.getId(), example2.getId()));
                assertNotNull(fetched);
                assertEquals(2, fetched.size());
                assertEquals(example1, fetched.get(0));
                assertEquals(example2, fetched.get(1));
                tx.commit();
            } finally {
                if (tx.isActive()) tx.rollback();
            }
        }
    }

}
