package com.jasify.schedule.appengine.dao.common;

import com.jasify.schedule.appengine.TestHelper;
import com.jasify.schedule.appengine.meta.activity.ActivityTypeMeta;
import com.jasify.schedule.appengine.model.activity.ActivityType;
import com.jasify.schedule.appengine.model.common.Organization;
import org.junit.*;
import org.junit.rules.ExpectedException;
import org.slim3.datastore.Datastore;

import java.util.List;

import static junit.framework.Assert.assertFalse;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

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

    @Test
    public void testGetByOrganizationWithNullOrganization() throws Exception {
        thrown.expect(NullPointerException.class);
        dao.getBy(null);
    }

    @Test
    public void testGetByOrganizationWithNullId() throws Exception {
        thrown.expect(NullPointerException.class);
        dao.getBy(new Organization());
    }

    @Test
    public void testGetByOrganizationWithUnknownlId() throws Exception {
        Organization organization = new Organization();
        organization.setId(Datastore.allocateId(Organization.class));
        List<ActivityType> result = dao.getBy(organization);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testGetByOrganizationWithNoActivities() throws Exception {
        Organization organization = createOrganization();
        List<ActivityType> result = dao.getBy(organization);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testGetByOrganizationWithActivities() throws Exception {
        Organization organization = createOrganization();
        createActivityType(organization);
        createActivityType(organization);
        createActivityType(createOrganization());
        List<ActivityType> result = dao.getBy(organization);
        assertEquals(2, result.size());
    }

    @Test
    public void testGetAllWhenNoActivityTypes() throws Exception {
        List<ActivityType> result = dao.getAll();
        assertTrue(result.isEmpty());
    }

    @Test
    public void testGetAll() throws Exception {
        createActivityType(createOrganization());
        createActivityType(createOrganization());
        List<ActivityType> result = dao.getAll();
        assertEquals(2, result.size());
    }

    @Test
    public void testExistsForSameOrganisation() throws Exception {
        Organization organization = createOrganization();
        ActivityType activityType = createActivityType(organization);
        boolean result = dao.exists(activityType.getLcName(), organization);
        assertTrue(result);
    }

    @Test
    public void testExistsForDifferentOrganisation() throws Exception {
        ActivityType activityType = createActivityType(createOrganization());
        boolean result = dao.exists(activityType.getLcName(), createOrganization());
        assertFalse(result);
    }
}
