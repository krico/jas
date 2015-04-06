package com.jasify.schedule.appengine.spi.transform;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.jasify.schedule.appengine.TestHelper;
import com.jasify.schedule.appengine.meta.users.UserMeta;
import com.jasify.schedule.appengine.util.KeyUtil;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static junit.framework.TestCase.*;

public class JasKeyTransformerTest {
    private JasKeyTransformer transformer = new JasKeyTransformer();

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
        Key internal = KeyFactory.createKey(UserMeta.get().getKind(), 1);
        String transformed = transformer.transformTo(internal);
        assertNotNull(transformed);
        assertEquals(internal, KeyUtil.parseHumanReadableString(transformed));
    }

    @Test
    public void testTransformFrom() throws Exception {
        String external = KeyUtil.toHumanReadableString(KeyFactory.createKey(UserMeta.get().getKind(), 1));
        Key transformed = transformer.transformFrom(external);
        assertNotNull(transformed);
        assertEquals(external, KeyUtil.toHumanReadableString(transformed));
    }

    @Test
    public void testTransformFromInvalidStringReturnsNull() throws Exception {
        String external = KeyUtil.PREFIXES.get(UserMeta.get().getKind());
        assertNull(transformer.transformFrom(external));
    }

    @Test
    public void testTransformToKeyFactory() throws Exception {
        Key internal = KeyFactory.createKey("T", 1);
        String transformed = transformer.transformTo(internal);
        assertNotNull(transformed);
        assertEquals(internal, KeyFactory.stringToKey(transformed));
    }

    @Test
    public void testTransformFromKeyFactory() throws Exception {
        String external = KeyFactory.keyToString(KeyFactory.createKey("T", 1));
        Key transformed = transformer.transformFrom(external);
        assertNotNull(transformed);
        assertEquals(external, KeyFactory.keyToString(transformed));
    }

    @Test
    public void testTransformFromInvalidStringReturnsNullKeyFactory() throws Exception {
        String external = "ABC";
        assertNull(transformer.transformFrom(external));
    }
}