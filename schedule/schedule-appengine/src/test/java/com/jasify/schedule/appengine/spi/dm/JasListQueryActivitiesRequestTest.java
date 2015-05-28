package com.jasify.schedule.appengine.spi.dm;

import com.jasify.schedule.appengine.TestHelper;
import com.jasify.schedule.appengine.model.activity.ActivityType;
import com.jasify.schedule.appengine.model.common.Organization;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slim3.datastore.Datastore;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author szarmawa
 * @since 27/05/15.
 */
public class JasListQueryActivitiesRequestTest {

    @BeforeClass
    public static void initialise() {
        TestHelper.initializeDatastore();
    }

    @AfterClass
    public static void cleanup() {
        TestHelper.cleanupDatastore();
    }

    @Test
    public void testActivityTypes() {
        JasListQueryActivitiesRequest jasListQueryActivitiesRequest = new JasListQueryActivitiesRequest();
        assertTrue(jasListQueryActivitiesRequest.getActivityTypeIds().isEmpty());
        jasListQueryActivitiesRequest.getActivityTypeIds().add(Datastore.allocateId(ActivityType.class));
        assertEquals(1, jasListQueryActivitiesRequest.getActivityTypeIds().size());
    }

    @Test
    public void testOrganizations() {
        JasListQueryActivitiesRequest jasListQueryActivitiesRequest = new JasListQueryActivitiesRequest();
        assertTrue(jasListQueryActivitiesRequest.getOrganizationIds().isEmpty());
        jasListQueryActivitiesRequest.getOrganizationIds().add(Datastore.allocateId(Organization.class));
        assertEquals(1, jasListQueryActivitiesRequest.getOrganizationIds().size());
    }
}
