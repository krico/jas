package com.jasify.schedule.appengine.spi.dm;

import com.google.appengine.api.datastore.Key;
import com.jasify.schedule.appengine.TestHelper;
import com.jasify.schedule.appengine.model.activity.ActivityType;
import com.jasify.schedule.appengine.model.common.Organization;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slim3.datastore.Datastore;

import static org.junit.Assert.assertEquals;

/**
 * Created by wszarmach on 19/01/15.
 */
public class JasAddActivityTypeRequestTest {

    @Before
    public void before() {
        TestHelper.initializeDatastore();
    }

    @After
    public void after() {
        TestHelper.cleanupDatastore();
    }

    @Test
    public void testUserId() {
        Key organization = Datastore.allocateId(Organization.class);
        JasAddActivityTypeRequest jasAddActivityTypeRequest = new JasAddActivityTypeRequest();
        jasAddActivityTypeRequest.setOrganizationId(organization);
        assertEquals(organization, jasAddActivityTypeRequest.getOrganizationId());
    }

    @Test
    public void testActivityType() {
        JasAddActivityTypeRequest jasAddActivityTypeRequest = new JasAddActivityTypeRequest();
        ActivityType activityType = new ActivityType();
        jasAddActivityTypeRequest.setActivityType(activityType);
        assertEquals(activityType, jasAddActivityTypeRequest.getActivityType());
    }
}
