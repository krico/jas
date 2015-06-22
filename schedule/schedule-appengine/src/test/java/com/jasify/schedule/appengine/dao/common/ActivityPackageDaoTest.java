package com.jasify.schedule.appengine.dao.common;

import com.jasify.schedule.appengine.TestHelper;
import com.jasify.schedule.appengine.model.activity.ActivityPackage;
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
public class ActivityPackageDaoTest {
    private ActivityPackageDao dao;

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
        dao = new ActivityPackageDao();
    }

    @Test
    public void testGetByOrganizationWithNullKey() throws Exception {
        thrown.expect(NullPointerException.class);
        dao.getByOrganization(null);
    }

    @Test
    public void testGetByOrganizationWithUnknownKey() throws Exception {
        TestHelper.createActivityPackage(TestHelper.createOrganization(true), true);
        List<ActivityPackage> result = dao.getByOrganization(Datastore.allocateId(Organization.class));
        assertTrue(result.isEmpty());
    }

    @Test
    public void testGetByOrganization() throws Exception {
        Organization organization1 = TestHelper.createOrganization(true);
        TestHelper.createActivityPackage(organization1, true);

        Organization organization2 = TestHelper.createOrganization(true);
        TestHelper.createActivityPackage(organization2, true);
        TestHelper.createActivityPackage(organization2, true);

        assertEquals(1, dao.getByOrganization(organization1.getId()).size());
        assertEquals(2, dao.getByOrganization(organization2.getId()).size());
        assertEquals(1, dao.getByOrganization(organization1.getId()).size());
    }
}
