package com.jasify.schedule.appengine.spi.transform;

import com.google.appengine.api.datastore.KeyFactory;
import com.jasify.schedule.appengine.TestHelper;
import com.jasify.schedule.appengine.model.users.UserLogin;
import com.jasify.schedule.appengine.spi.dm.JasUserLogin;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;

public class JasUserLoginTransformerTest {
    private JasUserLoginTransformer transformer = new JasUserLoginTransformer();

    @Before
    public void datastore() {
        TestHelper.initializeDatastore();
    }

    @After
    public void cleanup() {
        TestHelper.cleanupDatastore();
    }

    @Test
    public void testTransformTo() throws Exception {
        UserLogin internal = new UserLogin();
        internal.setId(KeyFactory.createKey(UserLogin.class.getSimpleName(), 55));
        internal.setEmail("a@b");
        internal.setProvider("Google");
        JasUserLogin transformed = transformer.transformTo(internal);
        assertNotNull(transformed);
        assertEquals(KeyFactory.keyToString(internal.getId()), transformed.getId());
        assertEquals(internal.getEmail(), transformed.getEmail());
        assertEquals(internal.getProvider(), transformed.getProvider());
    }

    @Test
    public void testTransformFrom() throws Exception {
        JasUserLogin external = new JasUserLogin();
        external.setId(KeyFactory.keyToString(KeyFactory.createKey(UserLogin.class.getSimpleName(), 55)));
        external.setEmail("a@b");
        external.setProvider("Google");
        UserLogin transformed = transformer.transformFrom(external);
        assertNotNull(transformed);
        assertEquals(external.getId(), KeyFactory.keyToString(transformed.getId()));
        assertEquals(external.getEmail(), transformed.getEmail());
        assertEquals(external.getProvider(), transformed.getProvider());
    }
}