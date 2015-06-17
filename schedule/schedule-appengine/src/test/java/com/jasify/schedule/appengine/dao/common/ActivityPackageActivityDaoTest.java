package com.jasify.schedule.appengine.dao.common;

import com.jasify.schedule.appengine.TestHelper;
import com.jasify.schedule.appengine.meta.activity.ActivityPackageActivityMeta;
import com.jasify.schedule.appengine.model.EntityNotFoundException;
import com.jasify.schedule.appengine.model.activity.Activity;
import com.jasify.schedule.appengine.model.activity.ActivityPackage;
import com.jasify.schedule.appengine.model.activity.ActivityPackageActivity;
import com.jasify.schedule.appengine.model.activity.ActivityType;
import com.jasify.schedule.appengine.model.common.Organization;
import org.junit.*;
import org.junit.rules.ExpectedException;
import org.slim3.datastore.Datastore;

import java.util.List;

import static junit.framework.TestCase.*;

/**
 * @author szarmawa
 * @since 18/06/15.
 */
public class ActivityPackageActivityDaoTest {
    private ActivityPackageActivityDao dao;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

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
        dao = new ActivityPackageActivityDao();
    }

    @Test
    public void testGetByNullActivityId() throws Exception {
        thrown.expect(NullPointerException.class);
        TestHelper.createActivityPackage(TestHelper.createOrganization(true), true);
        dao.getByActivityId(null);
    }

    @Test
    public void testGetByUnknownActivityId() throws Exception {
        thrown.expect(EntityNotFoundException.class);
        TestHelper.createActivityPackage(TestHelper.createOrganization(true), true);
        dao.getByActivityId(Datastore.allocateId(Activity.class));
    }

    @Test
    public void testGetByNullActivityTypeId() throws Exception {
        Activity activity = new Activity();
        Datastore.put(activity);
        List<ActivityPackageActivity> result = dao.getByActivityId(activity.getId());
        assertTrue(result.isEmpty());
    }

    @Test
    public void testGetByNullOrganizationId() throws Exception {
        ActivityType activityType = new ActivityType();
        Datastore.put(activityType);
        Activity activity = new Activity(activityType);
        Datastore.put(activity);
        List<ActivityPackageActivity> result = dao.getByActivityId(activity.getId());
        assertTrue(result.isEmpty());
    }

    @Test
    public void testGetByActivityId() throws Exception {
        Organization organization = TestHelper.createOrganization(true);
        ActivityType activityType = TestHelper.createActivityType(organization, true);
        Activity activity = TestHelper.createActivity(activityType, true);
        ActivityPackage activityPackage = TestHelper.createActivityPackage(organization, true);
        ActivityPackageActivity activityPackageActivity = new ActivityPackageActivity();
        activityPackageActivity.getActivityRef().setKey(activity.getId());
        activityPackageActivity.getActivityPackageRef().setKey(activityPackage.getId());
        activityPackageActivity.setId(Datastore.allocateId(organization.getId(), ActivityPackageActivityMeta.get()));
        Datastore.put(activityPackageActivity);
        List<ActivityPackageActivity> result = dao.getByActivityId(activity.getId());
        assertEquals(1, result.size());
    }
}
