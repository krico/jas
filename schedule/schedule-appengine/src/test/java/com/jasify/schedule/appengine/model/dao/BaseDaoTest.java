package com.jasify.schedule.appengine.model.dao;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Transaction;
import com.jasify.schedule.appengine.TestHelper;
import com.jasify.schedule.appengine.model.EntityNotFoundException;
import com.jasify.schedule.appengine.model.users.User;
import com.jasify.schedule.appengine.util.BeanUtil;
import io.github.benas.jpopulator.api.Populator;
import io.github.benas.jpopulator.impl.PopulatorBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slim3.datastore.Datastore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static junit.framework.TestCase.*;

public class BaseDaoTest {
    private ExampleDao dao = new ExampleDao();
    private List<Transaction> transactions = new ArrayList<>();

    private static Example createExample() {
        Populator populator = new PopulatorBuilder().build();
        return populator.populateBean(Example.class, "id");
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

    private Transaction beginTx() {
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
    }

}