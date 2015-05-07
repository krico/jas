package com.jasify.schedule.appengine.model;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Transaction;
import com.jasify.schedule.appengine.TestHelper;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.slim3.datastore.Datastore;

import java.util.ConcurrentModificationException;

import static junit.framework.TestCase.*;

public class TransactionOperatorTest {
    @Rule
    public TestName name = new TestName();

    @Before
    public void initializeDatastore() {
        TestHelper.initializeJasify();
    }

    @After
    public void cleanupDatastore() {
        TestHelper.cleanupDatastore();
    }

    @Test
    public void testExecuteDirect() throws Exception {
        TestOperation operation = new TestOperation();
        assertExecute(operation, 1);
    }

    @Test
    public void testExecuteOneConcurrentModificationException() throws Exception {
        TestOperation operation = new TestOperation();
        operation.concurrencyExceptionCount = 1;
        assertExecute(operation, 2);
    }

    @Test
    public void testExecuteMaxConcurrentModificationException() throws Exception {
        TestOperation operation = new TestOperation();
        operation.concurrencyExceptionCount = TransactionOperator.DEFAULT_RETRY_COUNT;
        assertExecute(operation, TransactionOperator.DEFAULT_RETRY_COUNT + 1);
    }

    @Test(expected = ConcurrentModificationException.class)
    public void testExecuteTooManyConcurrentModificationException() throws Exception {
        TestOperation operation = new TestOperation();
        operation.concurrencyExceptionCount = TransactionOperator.DEFAULT_RETRY_COUNT + 1;
        TransactionOperator.execute(operation);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testExecuteThrowsTransactionOperationException() throws Exception {
        TestOperation operation = new TestOperation();
        operation.toThrow = new IllegalArgumentException();
        TransactionOperator.execute(operation);
    }

    @Test
    public void testExecuteThrowsTransactionOperationExceptionWithCorrectCause() {
        TestOperation operation = new TestOperation();
        operation.toThrow = new IllegalArgumentException();
        try {
            TransactionOperator.execute(operation);
        } catch (Exception e) {
            assertEquals(operation.toThrow, e);
            return;
        }
        fail("Should have thrown");
    }

    @Test
    public void testExecuteSilently() {
        TestOperation operation = new TestOperation();
        operation.concurrencyExceptionCount = 2;
        Key key = TransactionOperator.executeNoEx(operation);
        assertNotNull(key);
        Entity entity = Datastore.get(key);
        assertNotNull(entity);
        Number executeCount = (Number) entity.getProperty("executeCount");
        assertEquals(3, executeCount.intValue());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testExecuteSilentlyPropagates() {
        TestOperation operation = new TestOperation();
        operation.toThrow = new IllegalArgumentException();
        TransactionOperator.executeNoEx(operation);
    }

    private void assertExecute(TestOperation operation, int expectedExecutions) throws Exception {
        TransactionOperator.execute(operation);
        assertEquals(expectedExecutions, operation.executeCount);
        assertNotNull(operation.id);
        Entity entity = Datastore.get(operation.id);
        assertNotNull(entity);
        assertTrue(entity.hasProperty("executeCount"));
        Number executeCount = (Number) entity.getProperty("executeCount");
        assertEquals(expectedExecutions, executeCount.intValue());
    }

    private class TestOperation implements TransactionOperation<Key, Exception> {
        private Exception toThrow;
        private int concurrencyExceptionCount = 0;
        private int executeCount = 0;
        private Key id;

        @Override
        public Key execute(Transaction tx) throws Exception {
            ++executeCount;
            Entity entity = new Entity(name.getMethodName());
            entity.setProperty("executeCount", executeCount);
            Key aux = Datastore.put(tx, entity);
            if (concurrencyExceptionCount-- > 0)
                throw new ConcurrentModificationException();
            if (toThrow != null) throw toThrow;
            tx.commit();
            this.id = aux;
            return this.id;
        }
    }
}