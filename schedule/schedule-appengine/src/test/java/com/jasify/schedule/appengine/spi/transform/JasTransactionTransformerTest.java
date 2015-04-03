package com.jasify.schedule.appengine.spi.transform;

import com.google.appengine.api.datastore.Key;
import com.jasify.schedule.appengine.TestHelper;
import com.jasify.schedule.appengine.model.balance.Account;
import com.jasify.schedule.appengine.model.balance.Transaction;
import com.jasify.schedule.appengine.model.balance.Transfer;
import com.jasify.schedule.appengine.spi.dm.JasTransaction;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slim3.datastore.Datastore;

import java.util.Date;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;

public class JasTransactionTransformerTest {
    private JasTransactionTransformer transformer = new JasTransactionTransformer();

    @BeforeClass
    public static void datastore() {
        TestHelper.initializeDatastore();
    }

    @AfterClass
    public static void cleanup() {
        TestHelper.cleanupDatastore();
    }

    @Test
    public void testTransformTo() throws Exception {
        Transaction internal = new Transaction();
        Key id = Datastore.createKey(Transaction.class, 1);
        internal.setId(id);
        internal.setAmount(25.15d);
        internal.setCurrency("USD");
        internal.setCreated(new Date(1000));
        internal.setDescription("A transaction");
        internal.setReference("REF");
        Key accountId = Datastore.createKey(Account.class, 5);
        internal.getAccountRef().setKey(accountId);
        Key transferId = Datastore.createKey(Transfer.class, 105);
        internal.getTransferRef().setKey(transferId);

        JasTransaction external = transformer.transformTo(internal);
        assertNotNull(external);
        assertEquals(25.15d, external.getAmount());
        assertEquals("USD", external.getCurrency());
        assertEquals(new Date(1000), external.getCreated());
        assertEquals("A transaction", external.getDescription());
        JasKeyTransformer keyTransformer = new JasKeyTransformer();
        assertEquals(keyTransformer.transformTo(accountId), external.getAccountRef());
        assertEquals(keyTransformer.transformTo(transferId), external.getTransferRef());
    }
}