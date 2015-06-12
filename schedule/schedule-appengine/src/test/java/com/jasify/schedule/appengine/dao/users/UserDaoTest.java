package com.jasify.schedule.appengine.dao.users;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Transaction;
import com.jasify.schedule.appengine.TestHelper;
import com.jasify.schedule.appengine.model.ModelException;
import com.jasify.schedule.appengine.model.ModelOperation;
import com.jasify.schedule.appengine.model.TransactionOperator;
import com.jasify.schedule.appengine.model.common.Organization;
import com.jasify.schedule.appengine.model.users.EmailExistsException;
import com.jasify.schedule.appengine.model.users.User;
import com.jasify.schedule.appengine.model.users.UserLoginExistsException;
import com.jasify.schedule.appengine.model.users.UsernameExistsException;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertNull;

public class UserDaoTest {
    private UserDao dao;

    static User createExample() {
        return TestHelper.populateBean(User.class, "id", "detailRef");
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
        dao = new UserDao();
    }

    private Key save(final User example) throws ModelException {
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

    @Test(expected = UsernameExistsException.class)
    public void testSaveChecksUniquenessOfName() throws Exception {
        User example = createExample();
        User example2 = createExample();
        example2.setName(example.getName());
        save(example);
        save(example2);
    }

    @Test(expected = EmailExistsException.class)
    public void testSaveChecksUniquenessOfEmail() throws Exception {
        User example = createExample();
        User example2 = createExample();
        example2.setEmail(example.getEmail());
        save(example);
        save(example2);
    }

    @Test
    public void testIndicesAreInTransaction() throws Exception {
        final User example = createExample();
        User example2 = createExample();
        example2.setEmail(example.getEmail());
        example2.setName(example.getName());

        Key id = TransactionOperator.execute(new ModelOperation<Key>() {
            @Override
            public Key execute(Transaction tx) throws ModelException {
                return dao.save(example);
            }
        });

        assertNull(dao.getOrNull(id));

        //If this works the indices are free
        save(example2);
    }

    @Test
    public void testDelete() throws Exception {
        Key id = save(createExample());
        dao.delete(id);
    }

    @Test
    public void testDeleteFreesIndices() throws Exception {
        final User example = createExample();
        User example2 = createExample();
        example2.setName(example.getName());
        example2.setEmail(example.getEmail());
        save(example);

        dao.delete(example.getId());

        save(example2);
    }

    @Test
    public void testUpdateFixesNameIndex() throws Exception {
        User example = createExample();
        User example2 = createExample();

        save(example);

        example2.setName(example.getName());
        example.setName(example.getName() + "2");

        save(example);
        // make sure previous update cleared name index
        save(example2);
    }

    @Test
    public void testUpdateFixesEmailIndex() throws Exception {
        User example = createExample();
        User example2 = createExample();

        save(example);

        example2.setEmail(example.getEmail());
        example.setEmail(example.getEmail() + "2");

        save(example);
        // make sure previous update cleared name index
        save(example2);
    }

    @Test
    public void testUpdateFixesEmailIndexEvenIfEmailIsNull() throws Exception {
        User example = createExample();
        User example2 = createExample();

        save(example);

        example2.setEmail(example.getEmail());
        example.setEmail(null);

        save(example);
        // make sure previous update cleared name index
        save(example2);
    }

    @Test
    public void testUpdateFixesBothIndices() throws Exception {
        User example = createExample();
        User example2 = createExample();

        save(example);

        example2.setName(example.getName());
        example2.setEmail(example.getEmail());
        example.setName(example.getName() + "2");
        example.setEmail(example.getEmail() + "2");

        save(example);
        // make sure previous update cleared name index
        save(example2);
    }

    @Test(expected = UsernameExistsException.class)
    public void testBatchSaveUniqueIndex() throws Exception {
        User example1 = createExample();
        User example2 = createExample();
        User example3 = createExample();
        example3.setName(example1.getName());
        dao.save(Arrays.asList(example1, example2));
        save(example3);
    }

    @Test
    public void testBatchDeleteFreesIndex() throws Exception {
        User example1 = createExample();
        User example2 = createExample();
        User example3 = createExample();

        save(example1);
        save(example2);


        example3.setName(example1.getName());

        final List<Key> batchDelete = new ArrayList<>();
        batchDelete.add(example1.getId());
        batchDelete.add(example2.getId());

        TransactionOperator.execute(new ModelOperation<Void>() {
            @Override
            public Void execute(Transaction tx) throws ModelException {
                dao.delete(batchDelete);
                tx.commit();
                return null;
            }
        });
        save(example3);
    }}