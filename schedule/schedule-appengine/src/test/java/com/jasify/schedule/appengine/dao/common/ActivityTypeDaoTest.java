package com.jasify.schedule.appengine.dao.common;

import com.google.appengine.api.datastore.Key;
import com.jasify.schedule.appengine.TestHelper;
import com.jasify.schedule.appengine.model.FieldValueException;
import com.jasify.schedule.appengine.model.UniqueConstraintException;
import com.jasify.schedule.appengine.model.activity.ActivityType;
import com.jasify.schedule.appengine.model.common.Organization;
import org.junit.*;
import org.junit.rules.ExpectedException;
import org.slim3.datastore.Datastore;

import java.util.ArrayList;
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
    public void testGetByOrganizationWithNullKey() throws Exception {
        thrown.expect(NullPointerException.class);
        dao.getByOrganization(null);
    }

    @Test
    public void testGetByOrganizationWithUnknownKey() throws Exception {
        List<ActivityType> result = dao.getByOrganization(Datastore.allocateId(Organization.class));
        assertTrue(result.isEmpty());
    }

    @Test
    public void testGetByOrganizationWithNoActivityTypes() throws Exception {
        List<ActivityType> result = dao.getByOrganization(TestHelper.createOrganization(true).getId());
        assertTrue(result.isEmpty());
    }

    @Test
    public void testGetByOrganization() throws Exception {
        Organization organization1 = TestHelper.createOrganization(true);
        TestHelper.createActivityType(organization1, true);
        TestHelper.createActivityType(organization1, true);
        Organization organization2 = TestHelper.createOrganization(true);
        TestHelper.createActivityType(organization2, true);
        assertEquals(2, dao.getByOrganization(organization1.getId()).size());
        assertEquals(1, dao.getByOrganization(organization2.getId()).size());
        assertEquals(2, dao.getByOrganization(organization1.getId()).size());
    }

    @Test
    public void testGetKeysByOrganizationWithNullKey() throws Exception {
        thrown.expect(NullPointerException.class);
        dao.getKeysByOrganization(null);
    }

    @Test
    public void testGetKeysByOrganizationWithUnknownKey() throws Exception {
        List<Key> result = dao.getKeysByOrganization(Datastore.allocateId(Organization.class));
        assertTrue(result.isEmpty());
    }

    @Test
    public void testGetKeysByOrganizationWithNoActivityTypes() throws Exception {
        List<Key> result = dao.getKeysByOrganization(TestHelper.createOrganization(true).getId());
        assertTrue(result.isEmpty());
    }

    @Test
    public void testKeysGetByOrganization() throws Exception {
        Organization organization1 = TestHelper.createOrganization(true);
        TestHelper.createActivityType(organization1, true);
        TestHelper.createActivityType(organization1, true);
        Organization organization2 = TestHelper.createOrganization(true);
        TestHelper.createActivityType(organization2, true);
        assertEquals(2, dao.getKeysByOrganization(organization1.getId()).size());
        assertEquals(1, dao.getKeysByOrganization(organization2.getId()).size());
        assertEquals(2, dao.getKeysByOrganization(organization1.getId()).size());
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
        boolean result = dao.exists(activityType.getLcName(), organization.getId());
        assertTrue(result);
    }

    @Test
    public void testExistsForDifferentOrganisation() throws Exception {
        ActivityType activityType = TestHelper.createActivityType(TestHelper.createOrganization(true), true);
        boolean result = dao.exists(activityType.getLcName(), TestHelper.createOrganization(true).getId());
        assertFalse(result);
    }

    @Test
    public void testSaveNewWithoutChecks() throws Exception {
        ActivityType activityType = TestHelper.createActivityType(TestHelper.createOrganization(true), false);
        Key result = dao.save(activityType);
        assertNotNull(result);
    }

    @Test
    public void testSaveUpdateWithoutChecks() throws Exception {
        ActivityType activityType = TestHelper.createActivityType(TestHelper.createOrganization(true), true);
        Key result = dao.save(activityType);
        assertNotNull(result);
    }

    @Test
    public void testSaveList() throws Exception {
        Organization organization = TestHelper.createOrganization(true);
        List<ActivityType> activityTypes = new ArrayList<>();
        activityTypes.add(TestHelper.createActivityType(organization, false));
        activityTypes.add(TestHelper.createActivityType(organization, false));
        List<Key> result = dao.save(activityTypes);
        assertNotNull(result);
        assertEquals(activityTypes.size(), result.size());
    }

    @Test
    public void testSaveWithNullName() throws Exception {
        thrown.expect(FieldValueException.class);
        thrown.expectMessage("ActivityType.name");
        Organization organization = TestHelper.createOrganization(true);
        ActivityType activityType = TestHelper.createActivityType(organization, false);
        activityType.setName(null);
        dao.save(activityType, organization.getId());
    }

    @Test
    public void testSaveWithBlankName() throws Exception {
        thrown.expect(FieldValueException.class);
        thrown.expectMessage("ActivityType.name");
        Organization organization = TestHelper.createOrganization(true);
        ActivityType activityType = TestHelper.createActivityType(organization, false);
        activityType.setName("");
        dao.save(activityType, organization.getId());
    }

    @Test
    public void testSaveWithWhitespaceName() throws Exception {
        thrown.expect(FieldValueException.class);
        thrown.expectMessage("ActivityType.name");
        Organization organization = TestHelper.createOrganization(true);
        ActivityType activityType = TestHelper.createActivityType(organization, false);
        activityType.setName("    ");
        dao.save(activityType, organization.getId());
    }

    @Test
    public void testSaveNewWithDuplicateName() throws Exception {
        thrown.expect(UniqueConstraintException.class);
        Organization organization = TestHelper.createOrganization(true);
        ActivityType activityType1 = TestHelper.createActivityType(organization, true);
        ActivityType activityType2 = TestHelper.createActivityType(organization, false);
        activityType2.setName(activityType1.getName());
        thrown.expectMessage("ActivityType.name=" + activityType1.getName() + ", Organization.id=" + organization.getId());
        dao.save(activityType2, organization.getId());
    }

    @Test
    public void testSaveNew() throws Exception {
        Organization organization = TestHelper.createOrganization(true);
        ActivityType activityType = TestHelper.createActivityType(organization, false);
        Key result = dao.save(activityType, organization.getId());
        assertNotNull(result);
    }

    @Test
    public void testSaveUpdateWithDuplicateName() throws Exception {
        thrown.expect(UniqueConstraintException.class);
        Organization organization = TestHelper.createOrganization(true);
        ActivityType activityType1 = TestHelper.createActivityType(organization, true);
        ActivityType activityType2 = TestHelper.createActivityType(organization, true);
        activityType2.setName(activityType1.getName());
        thrown.expectMessage("ActivityType.name=" + activityType1.getName() + ", Organization.id=" + organization.getId());
        dao.save(activityType2, organization.getId());
    }

    @Test
    public void testSaveUpdate() throws Exception {
        Organization organization = TestHelper.createOrganization(true);
        ActivityType activityType = TestHelper.createActivityType(organization, true);
        Key result = dao.save(activityType, organization.getId());
        assertNotNull(result);
    }
}
