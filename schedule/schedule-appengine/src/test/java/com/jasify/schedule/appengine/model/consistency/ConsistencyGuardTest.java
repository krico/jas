package com.jasify.schedule.appengine.model.consistency;

import com.jasify.schedule.appengine.TestHelper;
import com.jasify.schedule.appengine.dao.common.OrganizationDao;
import com.jasify.schedule.appengine.dao.common.OrganizationDaoTest;
import com.jasify.schedule.appengine.dao.common.OrganizationMemberDao;
import com.jasify.schedule.appengine.model.common.Organization;
import com.jasify.schedule.appengine.model.common.OrganizationMember;
import com.jasify.schedule.appengine.model.users.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slim3.datastore.Datastore;

public class ConsistencyGuardTest {

    @Before
    public void setupDatastore() {
        TestHelper.initializeDatastore();
    }

    @After
    public void cleanupDatastore() {
        TestHelper.cleanupDatastore();
    }

    @Test
    public void wellDefined() throws Exception {
        TestHelper.assertUtilityClassWellDefined(ConsistencyGuard.class);
    }

    @Test
    public void testInitialize() throws InconsistentModelStateException {
        ConsistencyGuard.initialize();
    }

    @Test
    public void testBeforeDeleteOrganization() throws Exception {
        OrganizationDao organizationDao = new OrganizationDao();
        Organization organization = OrganizationDaoTest.createExample();
        organizationDao.save(organization);
        ConsistencyGuard.beforeDelete(organization);
    }

    @Test(expected = InconsistentModelStateException.class)
    public void testBeforeDeleteOrganizationWithMembers() throws Exception {
        ConsistencyGuard.beforeDelete(Organization.class, Datastore.allocateId(Organization.class));
        OrganizationDao organizationDao = new OrganizationDao();
        OrganizationMemberDao organizationMemberDao = new OrganizationMemberDao();
        Organization organization = OrganizationDaoTest.createExample();
        organizationDao.save(organization);
        OrganizationMember m = new OrganizationMember();
        m.getOrganizationRef().setKey(organization.getId());
        m.getUserRef().setKey(Datastore.allocateId(User.class));
        organizationMemberDao.save(m);

        ConsistencyGuard.beforeDelete(organization);
    }
}