package com.jasify.schedule.appengine.spi.transform;

import com.google.appengine.api.datastore.KeyFactory;
import com.jasify.schedule.appengine.TestHelper;
import com.jasify.schedule.appengine.model.users.User;
import com.jasify.schedule.appengine.spi.dm.JasUser;
import com.jasify.schedule.appengine.util.TypeUtil;
import org.junit.*;
import org.slim3.datastore.Datastore;

import java.util.Date;

import static junit.framework.TestCase.*;

public class JasUserTransformerTest {
    private JasUserTransformer transformer = new JasUserTransformer();

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
        User internal = new User();
        internal.setId(Datastore.createKey(User.class, 55));
        internal.setName("user");
        internal.setEmail("a@b");
        internal.setEmailVerified(true);
        internal.setAdmin(true);
        internal.setRealName("Real Name");
        internal.setPassword(TypeUtil.toShortBlob(new byte[]{1, 2, 3, 4}));
        internal.setCreated(new Date(1)); //readonly
        internal.setModified(new Date(2)); //readonly


        JasUser transformed = transformer.transformTo(internal);
        assertNotNull(transformed);
        assertEquals(KeyFactory.keyToString(internal.getId()), transformed.getId());
        assertEquals(internal.getName(), transformed.getName());
        assertEquals(internal.getEmail(), transformed.getEmail());
        assertEquals(internal.isEmailVerified(), transformed.isEmailVerified());
        assertEquals(internal.isAdmin(), transformed.isAdmin());
        assertEquals(internal.getRealName(), transformed.getRealName());
        assertEquals(internal.getCreated(), transformed.getCreated());
        assertEquals(internal.getModified(), transformed.getModified());
    }

    @Test
    public void testTransformFrom() throws Exception {
        JasUser external = new JasUser();
        external.setId(KeyFactory.keyToString(Datastore.createKey(User.class, 55)));
        external.setName("user");
        external.setEmail("a@b");
        external.setEmailVerified(true);
        external.setAdmin(true);
        external.setRealName("Real Name");
        external.setCreated(new Date(1)); //readonly
        external.setModified(new Date(2)); //readonly

        User transformed = transformer.transformFrom(external);
        assertNotNull(transformed);
        assertEquals(external.getId(), KeyFactory.keyToString(transformed.getId()));
        assertEquals(external.getName(), transformed.getName());
        assertEquals(external.getEmail(), transformed.getEmail());
        assertEquals(external.isEmailVerified(), transformed.isEmailVerified());
        assertEquals(external.isAdmin(), transformed.isAdmin());
        assertEquals(external.getRealName(), transformed.getRealName());
        assertNull(transformed.getCreated());
        assertNull(transformed.getModified());
    }

}