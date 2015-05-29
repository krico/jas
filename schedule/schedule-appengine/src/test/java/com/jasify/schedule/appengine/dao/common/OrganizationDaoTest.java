package com.jasify.schedule.appengine.dao.common;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Transaction;
import com.jasify.schedule.appengine.TestHelper;
import com.jasify.schedule.appengine.model.ModelException;
import com.jasify.schedule.appengine.model.ModelOperation;
import com.jasify.schedule.appengine.model.TransactionOperator;
import com.jasify.schedule.appengine.model.UniqueConstraintException;
import com.jasify.schedule.appengine.model.common.Organization;
import io.github.benas.jpopulator.api.Populator;
import io.github.benas.jpopulator.impl.PopulatorBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static junit.framework.TestCase.assertNotNull;


public class OrganizationDaoTest {
    private OrganizationDao dao;

    static Organization createExample() {
        Populator populator = new PopulatorBuilder().build();
        return populator.populateBean(Organization.class, "id", "lcName");
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
}