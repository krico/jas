package com.jasify.schedule.appengine.dao.common;

import com.jasify.schedule.appengine.TestHelper;
import com.jasify.schedule.appengine.model.activity.ActivityType;
import com.jasify.schedule.appengine.model.common.Organization;
import org.junit.*;
import org.junit.rules.ExpectedException;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

/**
 * @author szarmawa
 * @since 09/06/15.
 */
public class ActivityDaoTest {

    private ActivityDao dao;

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
        dao = new ActivityDao();
    }

    @Test
    public void testGetByOrganizationWithNullKey() throws Exception {
        thrown.expect(NullPointerException.class);
        dao.getByOrganizationId(null);
    }

    @Test
    public void testGetByOrganization() throws Exception {
        Organization organization1 = TestHelper.createOrganization(true);
        ActivityType activityType1 = TestHelper.createActivityType(organization1, true);
        for (int i = 0; i < 2; i++) {
            TestHelper.createActivity(activityType1, true);
        }

        Organization organization2 = TestHelper.createOrganization(true);
        ActivityType activityType2 = TestHelper.createActivityType(organization2, true);
        for (int i = 0; i < 4; i++) {
            TestHelper.createActivity(activityType2, true);
        }
        assertEquals(2, dao.getByOrganizationId(organization1.getId()).size());
        assertEquals(4, dao.getByOrganizationId(organization2.getId()).size());
        assertEquals(2, dao.getByOrganizationId(organization1.getId()).size());
    }

    @Test
    public void testGetByActivityTypeWithNullKey() throws Exception {
        assertTrue(dao.getByActivityTypeId(null).isEmpty());
    }

    @Test
    public void testGetByActivityType() throws Exception {
        Organization organization1 = TestHelper.createOrganization(true);
        ActivityType activityType1 = TestHelper.createActivityType(organization1, true);
        for (int i = 0; i < 2; i++) {
            TestHelper.createActivity(activityType1, true);
        }

        Organization organization2 = TestHelper.createOrganization(true);
        ActivityType activityType2 = TestHelper.createActivityType(organization2, true);
        for (int i = 0; i < 4; i++) {
            TestHelper.createActivity(activityType2, true);
        }

        assertEquals(2, dao.getByOrganizationId(organization1.getId()).size());
        assertEquals(4, dao.getByOrganizationId(organization2.getId()).size());
        assertEquals(2, dao.getByOrganizationId(organization1.getId()).size());
    }

    @Test
    public void testGetCachedValue() throws Exception {
        Organization organization = TestHelper.createOrganization(true);
        ActivityType activityType = TestHelper.createActivityType(organization, true);
        for (int i = 0; i < 5; i++) {
            TestHelper.createActivity(activityType, true);
        }
        assertEquals(5, dao.getByActivityTypeId(activityType.getId()).size());
        assertEquals(5, dao.getByActivityTypeId(activityType.getId()).size());
    }
}
