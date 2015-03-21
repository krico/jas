package com.jasify.schedule.appengine.spi.transform;

import com.google.appengine.api.datastore.KeyFactory;
import com.jasify.schedule.appengine.TestHelper;
import com.jasify.schedule.appengine.model.activity.RepeatDetails;
import com.jasify.schedule.appengine.model.activity.RepeatDetails.RepeatType;
import com.jasify.schedule.appengine.model.activity.RepeatDetails.RepeatUntilType;
import com.jasify.schedule.appengine.spi.dm.JasRepeatDetails;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slim3.datastore.Datastore;

import java.util.Date;

import static junit.framework.TestCase.assertEquals;

/**
 * @author wszarmach
 * @since 14/03/15.
 */
public class JasRepeatDetailsTransformerTest {
    private JasRepeatDetailsTransformer transformer = new JasRepeatDetailsTransformer();

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
        RepeatDetails internal = new RepeatDetails();
        internal.setId(Datastore.allocateId(RepeatDetails.class));
        internal.setRepeatEvery(5);
        internal.setMondayEnabled(true);
        internal.setRepeatType(RepeatType.Daily);
        internal.setUntilCount(7);
        internal.setUntilDate(new Date());
        internal.setRepeatUntilType(RepeatUntilType.Count);

        JasRepeatDetails external = transformer.transformTo(internal);

        assertEquals(internal.getId(), KeyFactory.stringToKey(external.getId()));
        assertEquals(internal.isMondayEnabled(), external.isMondayEnabled());
        assertEquals(internal.isTuesdayEnabled(), external.isTuesdayEnabled());
        assertEquals(internal.isWednesdayEnabled(), external.isWednesdayEnabled());
        assertEquals(internal.isThursdayEnabled(), external.isThursdayEnabled());
        assertEquals(internal.isFridayEnabled(), external.isFridayEnabled());
        assertEquals(internal.isSaturdayEnabled(), external.isSaturdayEnabled());
        assertEquals(internal.isSundayEnabled(), external.isSundayEnabled());
        assertEquals(internal.getRepeatEvery(), external.getRepeatEvery());
        assertEquals(internal.getRepeatType(), external.getRepeatType());
        assertEquals(internal.getUntilCount(), external.getUntilCount());
        assertEquals(internal.getUntilDate(), external.getUntilDate());
        assertEquals(internal.getRepeatUntilType(), external.getRepeatUntilType());
    }

    @Test
    public void testTransformFrom() throws Exception {
        JasRepeatDetails external = new JasRepeatDetails();
        external.setId(KeyFactory.keyToString(Datastore.allocateId(RepeatDetails.class)));
        external.setRepeatEvery(5);
        external.setFridayEnabled(true);
        external.setRepeatType(RepeatType.Daily);
        external.setUntilCount(7);
        external.setUntilDate(new Date());
        external.setRepeatUntilType(RepeatUntilType.Count);

        RepeatDetails internal = transformer.transformFrom(external);

        assertEquals(KeyFactory.stringToKey(external.getId()), internal.getId());
        assertEquals(external.isMondayEnabled(), internal.isMondayEnabled());
        assertEquals(external.isTuesdayEnabled(), internal.isTuesdayEnabled());
        assertEquals(external.isWednesdayEnabled(), internal.isWednesdayEnabled());
        assertEquals(external.isThursdayEnabled(), internal.isThursdayEnabled());
        assertEquals(external.isFridayEnabled(), internal.isFridayEnabled());
        assertEquals(external.isSaturdayEnabled(), internal.isSaturdayEnabled());
        assertEquals(external.isSundayEnabled(), internal.isSundayEnabled());
        assertEquals(external.getRepeatEvery(), internal.getRepeatEvery());
        assertEquals(external.getRepeatType(), internal.getRepeatType());
        assertEquals(external.getUntilCount(), internal.getUntilCount());
        assertEquals(external.getUntilDate(), internal.getUntilDate());
        assertEquals(external.getRepeatUntilType(), internal.getRepeatUntilType());
    }
}
