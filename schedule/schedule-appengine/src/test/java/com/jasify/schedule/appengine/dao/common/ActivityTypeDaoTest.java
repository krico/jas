package com.jasify.schedule.appengine.dao.common;

import com.jasify.schedule.appengine.TestHelper;
import com.jasify.schedule.appengine.model.activity.ActivityType;
import com.jasify.schedule.appengine.model.common.Organization;
import org.junit.*;
import org.junit.rules.ExpectedException;
import org.slim3.datastore.Datastore;

import java.util.List;

import static junit.framework.TestCase.*;

/**
 * @author szarmawa
 * @since 16/06/15.
 */
public class ActivityTypeDaoTest {

    private ActivityTypeDao dao;

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
        dao = new ActivityTypeDao();
    }

    @Test
    public void testGetByOrganizationWithNullOrganization() throws Exception {
        thrown.expect(NullPointerException.class);
        dao.getByOrganization(null);
    }

    @Test
    public void testGetByOrganizationWithUnknownlId() throws Exception {
        List<ActivityType> result = dao.getByOrganization(Datastore.allocateId(Organization.class));
        assertTrue(result.isEmpty());
    }

    @Test
    public void testGetByOrganizationWithNoActivities() throws Exception {
        List<ActivityType> result = dao.getByOrganization(TestHelper.createOrganization(true).getId());
        assertTrue(result.isEmpty());
    }

    @Test
    public void testGetByOrganizationWithActivities() throws Exception {
        Organization organization = TestHelper.createOrganization(true);
        TestHelper.createActivityType(organization, true);
        TestHelper.createActivityType(organization, true);
        TestHelper.createActivityType(TestHelper.createOrganization(true), true);
        List<ActivityType> result = dao.getByOrganization(organization.getId());
        assertEquals(2, result.size());
    }

    @Test
    public void testGetAllWhenNoActivityTypes() throws Exception {
        List<ActivityType> result = dao.getAll();
        assertTrue(result.isEmpty());
    }

    @Test
    public void testGetAll() throws Exception {
        TestHelper.createActivityType(TestHelper.createOrganization(true), true);
        TestHelper.createActivityType(TestHelper.createOrganization(true), true);
        List<ActivityType> result = dao.getAll();
        assertEquals(2, result.size());
    }

    @Test
    public void testExistsForSameOrganisation() throws Exception {
        Organization organization = TestHelper.createOrganization(true);
        ActivityType activityType = TestHelper.createActivityType(organization, true);
        boolean result = dao.exists(activityType.getLcName(), organization);
        assertTrue(result);
    }

    @Test
    public void testExistsForDifferentOrganisation() throws Exception {
        ActivityType activityType = TestHelper.createActivityType(TestHelper.createOrganization(true), true);
        boolean result = dao.exists(activityType.getLcName(), TestHelper.createOrganization(true));
        assertFalse(result);
    }
}