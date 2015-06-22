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

    private void createActivityPackageActivity(Organization organization, Activity activity, ActivityPackage activityPackage) {
        ActivityPackageActivity activityPackageActivity = new ActivityPackageActivity();
        activityPackageActivity.getActivityRef().setModel(activity);
        activityPackageActivity.getActivityPackageRef().setModel(activityPackage);
        activityPackageActivity.setId(Datastore.allocateId(organization.getId(), ActivityPackageActivityMeta.get()));
        Datastore.put(activityPackageActivity);
    }

    @Test
    public void testGetByActivityWithNullKey() throws Exception {
        thrown.expect(NullPointerException.class);
        TestHelper.createActivityPackage(TestHelper.createOrganization(true), true);
        dao.getByActivityId(null);
    }

    @Test
    public void testGetByActivityWithUnknownKey() throws Exception {
        thrown.expect(EntityNotFoundException.class);
        TestHelper.createActivityPackage(TestHelper.createOrganization(true), true);
        dao.getByActivityId(Datastore.allocateId(Activity.class));
    }

    @Test
    public void testGetByActivityTypeWithNullKey() throws Exception {
        Activity activity = new Activity();
        Datastore.put(activity);
        List<ActivityPackageActivity> result = dao.getByActivityId(activity.getId());
        assertTrue(result.isEmpty());
    }

    @Test
    public void testGetByOrganizationWithNullKey() throws Exception {
        ActivityType activityType = new ActivityType();
        Datastore.put(activityType);
        Activity activity = new Activity(activityType);
        Datastore.put(activity);
        List<ActivityPackageActivity> result = dao.getByActivityId(activity.getId());
        assertTrue(result.isEmpty());
    }

    @Test
    public void testGetByActivity() throws Exception {
        Organization organization1 = TestHelper.createOrganization(true);
        ActivityType activityType1 = TestHelper.createActivityType(organization1, true);
        Activity activity1 = TestHelper.createActivity(activityType1, true);
        createActivityPackageActivity(organization1, activity1, TestHelper.createActivityPackage(organization1, true));

        Organization organization2 = TestHelper.createOrganization(true);
        ActivityType activityType2 = TestHelper.createActivityType(organization2, true);
        Activity activity2 = TestHelper.createActivity(activityType2, true);
        createActivityPackageActivity(organization2, activity2, TestHelper.createActivityPackage(organization2, true));
        createActivityPackageActivity(organization2, activity2, TestHelper.createActivityPackage(organization2, true));
        createActivityPackageActivity(organization2, activity2, TestHelper.createActivityPackage(organization2, true));

        assertEquals(1, dao.getByActivityId(activity1.getId()).size());
        assertEquals(3, dao.getByActivityId(activity2.getId()).size());
        assertEquals(1, dao.getByActivityId(activity1.getId()).size());
    }
}
