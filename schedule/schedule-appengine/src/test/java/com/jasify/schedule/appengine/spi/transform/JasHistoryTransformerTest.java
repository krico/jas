package com.jasify.schedule.appengine.spi.transform;

import com.google.appengine.api.datastore.Key;
import com.jasify.schedule.appengine.TestHelper;
import com.jasify.schedule.appengine.model.history.History;
import com.jasify.schedule.appengine.model.history.HistoryTypeEnum;
import com.jasify.schedule.appengine.model.users.User;
import com.jasify.schedule.appengine.spi.dm.JasHistory;
import com.jasify.schedule.appengine.util.KeyUtil;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slim3.datastore.Datastore;

import java.util.Date;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;

public class JasHistoryTransformerTest {
    private JasHistoryTransformer transformer = new JasHistoryTransformer();

    @BeforeClass
    public static void datastore() {
        TestHelper.initializeDatastore();
    }

    @AfterClass
    public static void cleanup() {
        TestHelper.cleanupDatastore();
    }

    private void assertEqualsHistory(History expected, JasHistory actual) {
        assertEquals(transformedKeyOrNull(expected.getId()), actual.getId());
        assertEquals(expected.getCreated(), actual.getCreated());
        assertEquals(expected.getMessage(), actual.getMessage());
        assertEquals(expected.getType(), actual.getType());
        assertEquals(transformedKeyOrNull(expected.getCurrentUserRef().getKey()), actual.getCurrentUserId());
    }

    private String transformedKeyOrNull(Key id) {
        if (id == null) return null;
        return KeyUtil.toHumanReadableString(id);
    }

    @Test
    public void testTransformToEmpty() throws Exception {
        History internal = new History();
        JasHistory external = transformer.transformTo(internal);
        assertNotNull(external);
        assertEqualsHistory(internal, external);
    }

    @Test
    public void testTransformTo() throws Exception {
        History internal = new History();
        internal.setId(Datastore.allocateId(History.class));
        internal.setCreated(new Date());
        internal.setMessage("Message");
        internal.setType(HistoryTypeEnum.Message);
        internal.getCurrentUserRef().setKey(Datastore.allocateId(User.class));
        JasHistory external = transformer.transformTo(internal);
        assertNotNull(external);
        assertEqualsHistory(internal, external);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testTransformFrom() throws Exception {
        transformer.transformFrom(new JasHistory());
    }
}