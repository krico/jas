package com.jasify.schedule.appengine.dao.common;

import com.jasify.schedule.appengine.TestHelper;
import com.jasify.schedule.appengine.meta.activity.ActivityMeta;
import com.jasify.schedule.appengine.meta.activity.ActivityTypeMeta;
import com.jasify.schedule.appengine.model.activity.Activity;
import com.jasify.schedule.appengine.model.activity.ActivityType;
import com.jasify.schedule.appengine.model.common.Organization;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slim3.datastore.Datastore;

import java.util.Date;

import static junit.framework.TestCase.assertEquals;

/**
 * @author szarmawa
 * @since 09/06/15.
 */
public class ActivityDaoTest {

    private ActivityDao dao;
    private Organization organization;
    private ActivityType activityType;

    @BeforeClass
    public static void beforeClass() {
        TestHelper.setSystemProperties();
    }

    @After
    public void after() {
        TestHelper.cleanupDatastore();
    }

    @Before
    public void before() {
        TestHelper.initializeDatastore();
        dao = new ActivityDao();
        organization = createOrganization();
        activityType = createActivityType(organization);
    }

    private Organization createOrganization() {
        Organization organization = new Organization("OrgName");
        Datastore.put(organization);
        return organization;
    }

    private ActivityType createActivityType(Organization organization) {
        ActivityType activityType = new ActivityType("ActType");
        activityType.getOrganizationRef().setModel(organization);
        activityType.setId(Datastore.allocateId(organization.getId(), ActivityTypeMeta.get()));
        Datastore.put(activityType);
        return activityType;
    }

    private Activity createActivity(ActivityType activityType) {
        Activity activity = new Activity(activityType);
        activity.setName("Name");
        activity.setStart(new Date());
        activity.setFinish(new Date());
        activity.setPrice(22.2);
        activity.setCurrency("CHF");
        activity.setMaxSubscriptions(2);
        activity.setId(Datastore.allocateId(activityType.getOrganizationRef().getKey(), ActivityMeta.get()));
        Datastore.put(activity);
        return activity;
    }

    @Test
    public void testGetByOrganization() throws Exception {
        for (int i = 0; i < 5; i++) {
            createActivity(activityType);
        }
        assertEquals(5, dao.getBy(organization).size());
    }

    @Test
    public void testGetByActivityType() throws Exception {
        for (int i = 0; i < 5; i++) {
            createActivity(activityType);
        }
        assertEquals(5, dao.getBy(activityType).size());
    }


    @Test
    public void testGetCachedValue() throws Exception {
        for (int i = 0; i < 5; i++) {
            createActivity(activityType);
        }
        assertEquals(5, dao.getBy(activityType).size());
        assertEquals(5, dao.getBy(activityType).size());
    }
}
