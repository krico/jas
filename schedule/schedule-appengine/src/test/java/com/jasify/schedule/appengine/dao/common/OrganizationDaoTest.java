package com.jasify.schedule.appengine.dao.common;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Transaction;
import com.google.appengine.labs.repackaged.com.google.common.collect.Lists;
import com.jasify.schedule.appengine.TestHelper;
import com.jasify.schedule.appengine.model.ModelException;
import com.jasify.schedule.appengine.model.ModelOperation;
import com.jasify.schedule.appengine.model.TransactionOperator;
import com.jasify.schedule.appengine.model.UniqueConstraintException;
import com.jasify.schedule.appengine.model.common.Organization;
import com.jasify.schedule.appengine.model.common.OrganizationMember;
import com.jasify.schedule.appengine.model.common.OrganizationServiceFactory;
import com.jasify.schedule.appengine.model.users.User;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slim3.datastore.Datastore;

import java.util.List;

import static junit.framework.TestCase.*;


public class OrganizationDaoTest {
    private OrganizationDao dao;

    static Organization createExample() {
        return TestHelper.populateBean(Organization.class, "id", "lcName", "organizationMemberListRef");
    }

    @BeforeClass
    public static void initialize() {
        TestHelper.setSystemProperties();
    }

    @After
    public void cleanup() {
        TestHelper.cleanupDatastore();
    }

    @Before
    public void resetCache() {
        TestHelper.initializeDatastore();
        dao = new OrganizationDao();
    }

    private Key save(final Organization example) throws ModelException {
        return TransactionOperator.execute(new ModelOperation<Key>() {
            @Override
            public Key execute(Transaction tx) throws ModelException {
                Key key = dao.save(example);
                tx.commit();
                return key;
            }
        });
    }

    @Test
    public void testSave() throws Exception {
        Key id = save(createExample());
        assertNotNull(id);
    }

    @Test(expected = UniqueConstraintException.class)
    public void testSaveChecksUniquenessOfName() throws Exception {
        Organization example = createExample();
        Organization example2 = createExample();
        example2.setName(example.getName());
        save(example);
        save(example2);
    }

    @Test
    public void testUpdateWithNoNameChange() throws Exception {
        final Key id = save(createExample());
        Organization organization = dao.get(id);
        assertNotNull(organization);
        organization.setDescription("New desc");
        save(organization);
    }

    @Test
    public void testSaveUpdatesUniqueIndex() throws Exception {
        Organization example = createExample();
        Organization example2 = createExample();
        example2.setName(example.getName());
        save(example);
        example.setName("Another Name");
        save(example);//this should update the index
        save(example2);
    }

    @Test
    public void testDelete() throws Exception {
        Key id = save(createExample());
        dao.delete(id);
    }

    @Test
    public void testDeleteFreesIndex() throws Exception {
        final Organization example = createExample();
        Organization example2 = createExample();
        example2.setName(example.getName());
        save(example);
        TransactionOperator.execute(new ModelOperation<Void>() {
            @Override
            public Void execute(Transaction tx) throws ModelException {
                dao.delete(example.getId());
                tx.commit();
                return null;
            }
        });
        save(example2);
    }

    @Test
    public void testForUser() throws Exception {
        Organization org1 = createExample();
        Organization org2 = createExample();
        Organization org3 = createExample();
        User user = TestHelper.populateBean(User.class, "id", "lcName", "detailRef", "created");
        assertNotNull(org1);
        assertNotNull(org2);
        assertNotNull(org3);
        assertNotNull(user);
        Datastore.put(org1, org2, org3, user);
        OrganizationMember om1 = new OrganizationMember(org1, user);
        OrganizationMember om3 = new OrganizationMember(org3, user);
        Datastore.put(om1, om3);

        for (int M = 0; M < 3; ++M) {
            List<Organization> organizations = dao.forUser(user.getId());
            assertEquals(2, organizations.size());
            assertTrue(organizations.get(0).getId().equals(org1.getId()) || organizations.get(0).getId().equals(org3.getId()));
            assertTrue(organizations.get(1).getId().equals(org1.getId()) || organizations.get(1).getId().equals(org3.getId()));

            //TODO: This is only temporary as a proof of concept
            List<Organization> organizationsForUser = OrganizationServiceFactory.getOrganizationService().getOrganizationsForUser(user.getId());
            assertEquals(organizations.size(), organizationsForUser.size());
            assertTrue(organizations.get(0).getId().equals(organizationsForUser.get(0).getId()) || organizations.get(0).getId().equals(organizationsForUser.get(1).getId()));
            assertTrue(organizations.get(1).getId().equals(organizationsForUser.get(0).getId()) || organizations.get(1).getId().equals(organizationsForUser.get(1).getId()));
        }
    }
}