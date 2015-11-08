package com.jasify.schedule.appengine.spi.transform;

import com.jasify.schedule.appengine.TestHelper;
import com.jasify.schedule.appengine.model.message.ContactMessage;
import com.jasify.schedule.appengine.spi.dm.JasContactMessage;
import com.jasify.schedule.appengine.util.KeyUtil;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;

/**
 * @author wszarmach
 * @since 08/11/15.
 */
public class JasContactMessageTransformerTest {
    private JasContactMessageTransformer transformer = new JasContactMessageTransformer();

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
        ContactMessage internal = TestHelper.createContactMessage(true);
        JasContactMessage external = transformer.transformTo(internal);
        assertNotNull(external);
        assertEquals(internal.getCreated(), external.getCreated());
        assertEquals(internal.getEmail(), external.getEmail());
        assertEquals(internal.getFirstName(), external.getFirstName());
        assertEquals(KeyUtil.keyToString(internal.getId()), external.getId());
        assertEquals(internal.getMessage(), external.getMessage());
        assertEquals(internal.getSubject(), external.getSubject());
    }

    @Test
    public void testTransformFrom() throws Exception {
        JasContactMessage external = com.jasify.schedule.appengine.TestHelper.populateBean(JasContactMessage.class);
        ContactMessage internal = transformer.transformFrom(external);
        assertEquals(external.getCreated(), internal.getCreated());
        assertEquals(external.getEmail(), internal.getEmail());
        assertEquals(external.getFirstName(), internal.getFirstName());
        assertEquals(KeyUtil.stringToKey(external.getId()), internal.getId());
        assertEquals(external.getMessage(), internal.getMessage());
        assertEquals(external.getSubject(), internal.getSubject());
    }
}
