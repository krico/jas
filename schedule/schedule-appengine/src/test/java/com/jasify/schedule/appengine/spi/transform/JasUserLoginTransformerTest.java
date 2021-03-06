package com.jasify.schedule.appengine.spi.transform;

import com.jasify.schedule.appengine.TestHelper;
import com.jasify.schedule.appengine.model.users.UserLogin;
import com.jasify.schedule.appengine.spi.dm.JasUserLogin;
import com.jasify.schedule.appengine.util.KeyUtil;
import org.junit.*;
import org.slim3.datastore.Datastore;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;

public class JasUserLoginTransformerTest {
    private JasUserLoginTransformer transformer = new JasUserLoginTransformer();

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
        UserLogin internal = new UserLogin();
        internal.setId(Datastore.createKey(UserLogin.class, 55));
        internal.setEmail("a@b");
        internal.setProvider("Google");
        JasUserLogin transformed = transformer.transformTo(internal);
        assertNotNull(transformed);
        assertEquals(KeyUtil.keyToString(internal.getId()), transformed.getId());
        assertEquals(internal.getEmail(), transformed.getEmail());
        assertEquals(internal.getProvider(), transformed.getProvider());
    }

    @Test
    public void testTransformFrom() throws Exception {
        JasUserLogin external = new JasUserLogin();
        external.setId(KeyUtil.keyToString(Datastore.createKey(UserLogin.class, 55)));
        external.setEmail("a@b");
        external.setProvider("Google");
        UserLogin transformed = transformer.transformFrom(external);
        assertNotNull(transformed);
        assertEquals(external.getId(), KeyUtil.keyToString(transformed.getId()));
        assertEquals(external.getEmail(), transformed.getEmail());
        assertEquals(external.getProvider(), transformed.getProvider());
    }
}