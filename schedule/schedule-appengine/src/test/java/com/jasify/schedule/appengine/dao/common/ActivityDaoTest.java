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
        organization = TestHelper.createOrganization(true);
        activityType = TestHelper.createActivityType(organization, true);
    }

    @Test
    public void testGetByOrganization() throws Exception {
        for (int i = 0; i < 5; i++) {
            TestHelper.createActivity(activityType, true);
        }
        assertEquals(5, dao.getByOrganizationId(organization.getId()).size());
    }

    @Test
    public void testGetByActivityType() throws Exception {
        for (int i = 0; i < 5; i++) {
            TestHelper.createActivity(activityType, true);
        }
        assertEquals(5, dao.getByActivityTypeId(activityType.getId()).size());
    }

    @Test
    public void testGetCachedValue() throws Exception {
        for (int i = 0; i < 5; i++) {
            TestHelper.createActivity(activityType, true);
        }
        assertEquals(5, dao.getByActivityTypeId(activityType.getId()).size());
        assertEquals(5, dao.getByActivityTypeId(activityType.getId()).size());
    }
}
