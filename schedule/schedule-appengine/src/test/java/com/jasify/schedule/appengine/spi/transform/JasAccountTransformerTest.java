package com.jasify.schedule.appengine.spi.transform;

import com.google.appengine.api.datastore.Key;
import com.jasify.schedule.appengine.TestHelper;
import com.jasify.schedule.appengine.model.balance.Account;
import com.jasify.schedule.appengine.model.balance.UserAccount;
import com.jasify.schedule.appengine.spi.dm.JasAccount;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slim3.datastore.Datastore;

import java.util.Date;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;

public class JasAccountTransformerTest {
    private JasAccountTransformer transformer = new JasAccountTransformer();

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
        UserAccount internal = new UserAccount();
        Key id = Datastore.createKey(Account.class, 1);
        internal.setId(id);
        internal.setBalance(25.15d);
        internal.setCurrency("USD");
        internal.setCreated(new Date(1000));
        internal.setModified(new Date(10000));

        JasAccount external = transformer.transformTo(internal);
        assertNotNull(external);
        assertEquals(25.15d, external.getBalance());
        assertEquals("USD", external.getCurrency());
        assertEquals(new Date(1000), external.getCreated());
        assertEquals(new Date(10000), external.getModified());
        assertEquals(internal.getClass().getSimpleName(), external.getType());
    }
}