package com.jasify.schedule.appengine.spi.dm;

import com.jasify.schedule.appengine.TestHelper;
import com.jasify.schedule.appengine.model.activity.Activity;
import com.jasify.schedule.appengine.model.activity.RepeatDetails;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author wszarmach
 * @since 16/03/15.
 */
public class JasAddActivityRequestTest {

    @BeforeClass
    public static void initialise() {
        TestHelper.initializeDatastore();
    }

    @AfterClass
    public static void cleanup() {
        TestHelper.cleanupDatastore();
    }

    @Test
    public void testAddActivity() {
        JasAddActivityRequest addActivityRequest = new JasAddActivityRequest();
        assertNull(addActivityRequest.getActivity());
        Activity activity = new Activity();
        addActivityRequest.setActivity(activity);
        assertEquals(activity, addActivityRequest.getActivity());
    }

    @Test
    public void testRepeatDetails() {
        JasAddActivityRequest addActivityRequest = new JasAddActivityRequest();
        assertNull(addActivityRequest.getRepeatDetails());
        RepeatDetails repeatDetails = new RepeatDetails();
        addActivityRequest.setRepeatDetails(repeatDetails);
        assertEquals(repeatDetails, addActivityRequest.getRepeatDetails());
    }
}
