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
    public void testGetByNullOrganizationId() throws Exception {
        TestHelper.createActivityPackage(TestHelper.createOrganization(true), true);
        List<ActivityPackage> result = dao.getByOrganization(null);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testGetByUnknownOrganizationId() throws Exception {
        TestHelper.createActivityPackage(TestHelper.createOrganization(true), true);
        List<ActivityPackage> result = dao.getByOrganization(Datastore.allocateId(Organization.class));
        assertTrue(result.isEmpty());
    }

    @Test
    public void testGetByOrganizationId() throws Exception {
        Organization organization = TestHelper.createOrganization(true);
        TestHelper.createActivityPackage(organization, true);
        List<ActivityPackage> result = dao.getByOrganization(organization.getId());
        assertEquals(1, result.size());
    }
}
