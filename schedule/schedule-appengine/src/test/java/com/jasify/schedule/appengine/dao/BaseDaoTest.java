package com.jasify.schedule.appengine.dao;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Transaction;
import com.jasify.schedule.appengine.TestHelper;
import com.jasify.schedule.appengine.model.EntityNotFoundException;
import com.jasify.schedule.appengine.model.users.User;
import com.jasify.schedule.appengine.util.BeanUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slim3.datastore.Datastore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.jasify.schedule.appengine.dao.DaoUtilTest.createExample;
import static junit.framework.TestCase.*;

public class BaseDaoTest {
    protected BaseDao<Example> dao = createDao();
    private List<Transaction> transactions = new ArrayList<>();

    @BeforeClass
    public static void initializeTestHelper() {
        TestHelper.setSystemProperties();
    }

    BaseDao<Example> createDao() {
        return new ExampleDao();
    }

    @Before
    public void setup() {
        TestHelper.initializeDatastore();
    }

    @After
    public void cleanup() {
        for (Transaction tx : transactions) {
            if (tx.isActive()) tx.rollback();
        }
        TestHelper.cleanupDatastore();
    }

    Transaction beginTx() {
        Transaction tx = Datastore.beginTransaction();
        transactions.add(tx);
        return tx;
    }

    //If this test fails, you probably need to run mvn apt:test-process
    @Test
    public void rememberToRunAptTestProcess() {
        Example expected = createExample();
        String[] exclude = {"modified", "created", "id"};
        Map<Object, Object> expected1 = BeanUtil.beanMap(expected, exclude);

        Key id = Datastore.put(expected);
        Example entity = Datastore.get(Example.class, id);
        assertNotNull(entity);
        assertEquals(expected.getId(), entity.getId());
        assertEquals(expected1, BeanUtil.beanMap(entity, exclude));
    }

    @Test(expected = IllegalStateException.class)
    public void testConstructorInsideTransaction() throws Exception {
        beginTx();
        new ExampleDao();
    }

    @Test
    public void testGet() throws Exception {
        Example expected = createExample();
        Example fetched = dao.get(Datastore.put(expected));
        assertEquals(expected, fetched);
    }

    @Test(expected = EntityNotFoundException.class)
    public void testGetNotFound() throws Exception {
        dao.get(Datastore.allocateId(Example.class));
    }

    @Test
    public void testGetOrNull() throws Exception {
        final Key id = Datastore.allocateId(Example.class);
        final Example example = createExample();
        example.setId(id);
        assertNull(dao.getOrNull(id));
        dao.save(example);
        assertEquals(example, dao.getOrNull(id));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetWrongKind() throws Exception {
        dao.get(Datastore.put(new User()));
    }

    @Test
    public void testSave() throws Exception {
        Example example = createExample();
        Key id = dao.save(example);
        Example fetched = dao.get(id);
        assertEquals(example, fetched);
    }

    @Test
    public void testDelete() throws Exception {
        Key id = dao.save(createExample());
        dao.delete(id);
        assertNull(dao.getOrNull(id));
    }

    @Test
    public void testCurrentTransaction() throws Exception {

        final Key id = Datastore.allocateId(Example.class);
        Example example = createExample();
        example.setId(id);
        //This makes current transaction=tx1
        Transaction tx1 = beginTx();
        dao.save(example);

        //This makes current transaction=tx2
        Transaction tx2 = beginTx();
        assertNull(dao.getOrNull(id));
        tx2.rollback();
        tx1.commit();
        assertNotNull(dao.getOrNull(id));

        Transaction tx3 = beginTx();
        dao.delete(id);
        Transaction tx4 = beginTx();
        assertNotNull(dao.getOrNull(id));
        tx4.rollback();

        tx3.commit();
        assertNull(dao.getOrNull(id));

    }

    @Test
    public void testQuery() throws Exception {
        List<Example> examples = new ArrayList<>();

        for (int i = 0; i < 10; ++i) {
            Example example = createExample();
            example.setDataType(String.format("TYPE-%d", i % 3));
            dao.save(example);
            examples.add(example);
        }

        for (int M = 0; M < 3; ++M) {
            for (int i = 0; i < 3; ++i) {
                String type = String.format("TYPE-%d", i % 3);
                List<Example> expected = new ArrayList<>();
                for (Example example : examples) {
                    if (type.equals(example.getDataType())) {
                        expected.add(example);
                    }
                }
                assertFalse(expected.isEmpty());
                List<Example> queryResult = ((AnyExampleDao) dao).byDataType(type);
                assertNotNull(queryResult);
                assertEquals(expected.size(), queryResult.size());
                for (Example example : queryResult) {
                    assertTrue("Missing: " + example, expected.contains(example));
                }
            }
        }
    }

    @Test
    public void testQueryDetectsChanges() throws Exception {
        Example example1 = createExample();
        example1.setDataType("odd");
        dao.save(example1);
        Example example2 = createExample();
        example2.setDataType("even");
        dao.save(example2);
        Example example3 = createExample();
        example3.setDataType("odd");
        dao.save(example3);

        List<Example> odd = ((AnyExampleDao) dao).byDataType("odd");
        assertEquals(2, odd.size());
        assertTrue(odd.contains(example1));
        assertTrue(odd.contains(example3));
        List<Example> even = ((AnyExampleDao) dao).byDataType("even");
        assertEquals(1, even.size());
        assertTrue(even.contains(example2));

        Example example4 = createExample();
        example4.setDataType("even");
        dao.save(example4);

        odd = ((AnyExampleDao) dao).byDataType("odd");
        assertEquals(2, odd.size());
        assertTrue(odd.contains(example1));
        assertTrue(odd.contains(example3));

        even = ((AnyExampleDao) dao).byDataType("even");
        assertEquals("stale cache?", 2, even.size());
        assertTrue("stale cache?", even.contains(example2));
        assertTrue("stale cache?", even.contains(example4));


    }

    @Test
    public void testSaveList() throws Exception {
        List<Example> examples = new ArrayList<>();
        examples.add(createExample());
        examples.add(createExample());
        examples.add(createExample());
        List<Key> keys = dao.save(examples);
        assertNotNull(keys);
        assertEquals(examples.size(), keys.size());
        for (int M = 0; M < 3; ++M) {
            for (int i = 0; i < keys.size(); ++i) {
                Example example = dao.get(keys.get(i));
                assertEquals(examples.get(i), example);
            }
        }
    }

    @Test
    public void testSaveNoExList() throws Exception {
        List<Example> examples = new ArrayList<>();
        examples.add(createExample());
        examples.add(createExample());
        examples.add(createExample());
        List<Key> keys = dao.saveNoEx(examples);
        assertNotNull(keys);
        assertEquals(examples.size(), keys.size());
        for (int i = 0; i < keys.size(); ++i) {
            Example example = dao.get(keys.get(i));
            assertEquals(examples.get(i), example);
        }
    }

    @Test
    public void testGetList() throws Exception {
        List<Example> examples = new ArrayList<>();
        examples.add(createExample());
        examples.add(createExample());
        examples.add(createExample());
        List<Key> keys = dao.save(examples);
        for (int M = 0; M < 3; ++M) {

            List<Example> batch = dao.get(keys);
            assertEquals(keys.size(), batch.size());

            for (int i = 0; i < keys.size(); ++i) {
                assertEquals(examples.get(i), batch.get(i));
            }
        }
    }

    @Test
    public void testDeleteList() throws Exception {
        List<Example> examples = new ArrayList<>();
        examples.add(createExample());
        examples.add(createExample());
        examples.add(createExample());
        List<Key> keys = dao.save(examples);
        dao.delete(keys);

        for (Key key : keys) {
            assertNull(dao.getOrNull(key));
        }
    }
}