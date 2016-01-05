package com.jasify.schedule.appengine.model.multipass.filter;

import com.google.appengine.api.datastore.Key;
import com.jasify.schedule.appengine.TestHelper;
import com.jasify.schedule.appengine.model.activity.ActivityType;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slim3.datastore.Datastore;

import java.util.ArrayList;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

/**
 * @author wszarmach
 * @since 17/11/15.
 */
public class ActivityTypeFilterTest {

    @Before
    public void initializeDatastore() {
        TestHelper.initializeJasify();
    }

    @After
    public void cleanupDatastore() {
        TestHelper.cleanupDatastore();
    }

    @Test
    public void testActivityTypeKeys() {
        ActivityTypeFilter filter = new ActivityTypeFilter();
        assertTrue(filter.getActivityTypeIds().isEmpty());
        ArrayList<Key> keys = new ArrayList<>();
        keys.add(Datastore.allocateId(ActivityType.class));
        filter.setActivityTypeIds(keys);
        assertEquals(1, filter.getActivityTypeIds().size());
    }
}
