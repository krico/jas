package com.jasify.schedule.appengine.dao.multipass;

import com.google.appengine.api.datastore.Key;
import com.jasify.schedule.appengine.TestHelper;
import com.jasify.schedule.appengine.model.FieldValueException;
import com.jasify.schedule.appengine.model.UniqueConstraintException;
import com.jasify.schedule.appengine.model.common.Organization;
import com.jasify.schedule.appengine.model.multipass.Multipass;
import org.junit.*;
import org.junit.rules.ExpectedException;
import org.slim3.datastore.Datastore;

import java.util.List;

import static junit.framework.TestCase.*;
import static junit.framework.TestCase.assertNotNull;

/**
 * @author wszarmach
 * @since 16/11/15.
 */
public class MultipassDaoTest {
    private MultipassDao dao;

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
        dao = new MultipassDao();
    }

    @Test
    public void testGetByOrganizationWithNullKey() throws Exception {
        thrown.expect(NullPointerException.class);
        dao.getByOrganization(null);
    }

    @Test
    public void testGetByOrganizationWithUnknownKey() throws Exception {
        List<Multipass> result = dao.getByOrganization(Datastore.allocateId(Organization.class));
        assertTrue(result.isEmpty());
    }

    @Test
    public void testGetByOrganizationWithNoMultipasses() throws Exception {
        List<Multipass> result = dao.getByOrganization(TestHelper.createOrganization(true).getId());
        assertTrue(result.isEmpty());
    }

    @Test
    public void testGetByOrganization() throws Exception {
        Organization organization1 = TestHelper.createOrganization(true);
        TestHelper.createMultipass(organization1, true);
        TestHelper.createMultipass(organization1, true);
        Organization organization2 = TestHelper.createOrganization(true);
        TestHelper.createMultipass(organization2, true);
        assertEquals(2, dao.getByOrganization(organization1.getId()).size());
        assertEquals(1, dao.getByOrganization(organization2.getId()).size());
        assertEquals(2, dao.getByOrganization(organization1.getId()).size());
    }

    @Test
    public void testExistsForSameOrganisation() throws Exception {
        Organization organization = TestHelper.createOrganization(true);
        Multipass multipass = TestHelper.createMultipass(organization, true);
        boolean result = dao.exists(multipass.getLcName(), organization.getId());
        assertTrue(result);
    }

    @Test
    public void testExistsForDifferentOrganisation() throws Exception {
        Multipass multipass = TestHelper.createMultipass(TestHelper.createOrganization(true), true);
        boolean result = dao.exists(multipass.getLcName(), TestHelper.createOrganization(true).getId());
        assertFalse(result);
    }

    @Test
    public void testSaveNewWithoutChecks() throws Exception {
        Multipass multipass = TestHelper.createMultipass(TestHelper.createOrganization(true), false);
        Key result = dao.save(multipass);
        assertNotNull(result);
    }

    @Test
    public void testSaveUpdateWithoutChecks() throws Exception {
        Multipass multipass = TestHelper.createMultipass(TestHelper.createOrganization(true), true);
        Key result = dao.save(multipass);
        assertNotNull(result);
    }

    @Test
    public void testSaveWithNullName() throws Exception {
        thrown.expect(FieldValueException.class);
        thrown.expectMessage("Multipass.name");
        Organization organization = TestHelper.createOrganization(true);
        Multipass multipass = TestHelper.createMultipass(organization, false);
        multipass.setName(null);
        dao.save(multipass, organization.getId());
    }

    @Test
    public void testSaveWithBlankName() throws Exception {
        thrown.expect(FieldValueException.class);
        thrown.expectMessage("Multipass.name");
        Organization organization = TestHelper.createOrganization(true);
        Multipass multipass = TestHelper.createMultipass(organization, false);
        multipass.setName("");
        dao.save(multipass, organization.getId());
    }

    @Test
    public void testSaveWithWhitespaceName() throws Exception {
        thrown.expect(FieldValueException.class);
        thrown.expectMessage("Multipass.name");
        Organization organization = TestHelper.createOrganization(true);
        Multipass multipass = TestHelper.createMultipass(organization, false);
        multipass.setName("    ");
        dao.save(multipass, organization.getId());
    }

    @Test
    public void testSaveNewWithDuplicateName() throws Exception {
        thrown.expect(UniqueConstraintException.class);
        Organization organization = TestHelper.createOrganization(true);
        Multipass multipass1 = TestHelper.createMultipass(organization, true);
        Multipass multipass2 = TestHelper.createMultipass(organization, false);
        multipass2.setName(multipass1.getName());
        thrown.expectMessage("Multipass.name=" + multipass1.getName() + ", Organization.id=" + organization.getId());
        dao.save(multipass2, organization.getId());
    }

    @Test
    public void testSaveNew() throws Exception {
        Organization organization = TestHelper.createOrganization(true);
        Multipass multipass = TestHelper.createMultipass(organization, false);
        Key result = dao.save(multipass, organization.getId());
        assertNotNull(result);
    }

    @Test
    public void testSaveUpdateWithDuplicateName() throws Exception {
        thrown.expect(UniqueConstraintException.class);
        Organization organization = TestHelper.createOrganization(true);
        Multipass multipass1 = TestHelper.createMultipass(organization, true);
        Multipass multipass2 = TestHelper.createMultipass(organization, true);
        multipass2.setName(multipass1.getName());
        thrown.expectMessage("Multipass.name=" + multipass1.getName() + ", Organization.id=" + organization.getId());
        dao.save(multipass2, organization.getId());
    }

    @Test
    public void testSaveUpdate() throws Exception {
        Organization organization = TestHelper.createOrganization(true);
        Multipass multipass = TestHelper.createMultipass(organization, true);
        Key result = dao.save(multipass, organization.getId());
        assertNotNull(result);
    }
}
