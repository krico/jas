package com.jasify.schedule.appengine.model;

import com.jasify.schedule.appengine.Constants;
import com.jasify.schedule.appengine.meta.users.UserMeta;
import com.jasify.schedule.appengine.model.application.ApplicationData;
import com.jasify.schedule.appengine.model.users.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slim3.datastore.Datastore;

import static junit.framework.TestCase.*;

public class UniqueConstraintTest {

    @Before
    public void initializeDatastore() {
        ModelTestHelper.initializeDatastore();
        ApplicationData.instance().reload();
    }

    @After
    public void cleanupDatastore() {
        ModelTestHelper.cleanupDatastore();
    }

    @Test
    public void testConstructorHasRightPrefixAndIsPersistent() throws Exception {
        UniqueConstraint uc = new UniqueConstraint(UserMeta.get(), "name");
        assertNotNull(uc.getUniqueKind());
        assertTrue(uc.getUniqueKind().startsWith(Constants.UNIQUE_CONSTRAINT_PREFIX));
        UniqueConstraint uc2 = new UniqueConstraint(UserMeta.get(), "name");
        //They should be the same
        assertNotNull(uc2.getUniqueKind());

        assertEquals("UC should be persistent", uc.getUniqueKind(), uc2.getUniqueKind());
    }

    @Test(expected = UniqueConstraintException.class)
    public void testConstructorThrowsUniqueConstraintException() throws Exception {
        User u = new User();
        u.setName("user");
        User u2 = new User();
        u2.setName("user");
        Datastore.put(u);
        Datastore.put(u2);
        new UniqueConstraint(UserMeta.get(), "name");
    }

    @Test
    public void testReserve() throws Exception {
        UniqueConstraint uc = new UniqueConstraint(UserMeta.get(), "name");
        uc.reserve("krico");
        uc.release("krico");
        uc.reserve("krico");
    }

    @Test
    public void testRelease() throws Exception {
        UniqueConstraint uc = new UniqueConstraint(UserMeta.get(), "name");
        uc.reserve("krico");
    }

    @Test(expected = UniqueConstraintException.class)
    public void testReserveThrows() throws Exception {
        UniqueConstraint uc = new UniqueConstraint(UserMeta.get(), "name");
        uc.reserve("krico");
        uc.reserve("krico");
    }

}