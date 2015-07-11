package com.jasify.schedule.appengine.model;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.jasify.schedule.appengine.Constants;
import com.jasify.schedule.appengine.TestHelper;
import com.jasify.schedule.appengine.meta.users.UserMeta;
import com.jasify.schedule.appengine.model.application.Application;
import com.jasify.schedule.appengine.model.application.ApplicationData;
import com.jasify.schedule.appengine.model.users.User;
import org.apache.commons.lang3.StringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slim3.datastore.Datastore;

import java.util.List;
import java.util.Objects;

import static junit.framework.TestCase.*;

public class UniqueConstraintTest {

    @Before
    public void initializeDatastore() {
        TestHelper.initializeJasify();
        Key name = Datastore.createKey(Application.class, Constants.APPLICATION_NAME);
        for (Entity entity : Datastore.query(name).asList()) {
            Key key = entity.getKey();
            if(StringUtils.startsWith(key.getName(), "jasify.UniqueConstraint.")){
                Datastore.delete(key);
            }
        };
        ApplicationData.instance().reload();
    }

    @After
    public void cleanupDatastore() {
        TestHelper.cleanupDatastore();
    }

    @Test
    public void testConstructorHasRightPrefixAndIsPersistent() throws Exception {
        UniqueConstraint uc = new UniqueConstraintBuilder().createIfMissing(true).forMeta(UserMeta.get()).withUniquePropertyName(UserMeta.get().name.getName()).create();
        assertNotNull(uc.getUniqueKind());
        assertTrue(uc.getUniqueKind().startsWith(Constants.UNIQUE_CONSTRAINT_PREFIX));
        UniqueConstraint uc2 = new UniqueConstraintBuilder().forMeta(UserMeta.get()).withUniquePropertyName(UserMeta.get().name.getName()).create();
        //They should be the same
        assertNotNull(uc2.getUniqueKind());

        assertEquals("UC should be persistent", uc.getUniqueKind(), uc2.getUniqueKind());
    }

    @Test
    public void testConstructorWithClassifierHasRightPrefixAndIsPersistentAndDoesNotClashWithNoClassifiedConstraint() throws Exception {
        UniqueConstraint uc = new UniqueConstraintBuilder()
                .forMeta(UserMeta.get())
                .withUniquePropertyName("name")
                .withUniqueClassifierPropertyName("realName")
                .createIfMissing(true)
                .create();
        assertNotNull(uc.getUniqueKind());
        assertTrue(uc.getUniqueKind().startsWith(Constants.UNIQUE_CONSTRAINT_PREFIX));
        UniqueConstraint uc2 = new UniqueConstraintBuilder().forMeta(UserMeta.get()).withUniquePropertyName("name").withUniqueClassifierPropertyName("realName").create();
        //They should be the same
        assertNotNull(uc2.getUniqueKind());

        assertEquals("UC should be persistent", uc.getUniqueKind(), uc2.getUniqueKind());

        UniqueConstraint uc3 = new UniqueConstraintBuilder().createIfMissing(true).forMeta(UserMeta.get()).withUniquePropertyName(UserMeta.get().name.getName()).create();
        assertNotSame("UC classified cannot have same kind as unclassified", uc.getUniqueKind(), uc3.getUniqueKind());
        UniqueConstraint uc4 = new UniqueConstraintBuilder()
                .forMeta(UserMeta.get())
                .withUniquePropertyName("name")
                .withUniqueClassifierPropertyName("email")
                .createIfMissing(true)
                .create();
        assertNotSame("Different classifier prop cannot have same kind", uc.getUniqueKind(), uc4.getUniqueKind());
        assertNotSame("UC classified cannot have same kind as unclassified", uc3.getUniqueKind(), uc4.getUniqueKind());
    }

    @Test(expected = UniqueConstraintException.class)
    public void testConstructorThrowsUniqueConstraintException() throws Exception {
        User u = new User();
        u.setName("user");
        User u2 = new User();
        u2.setName("user");
        Datastore.put(u);
        Datastore.put(u2);
        new UniqueConstraintBuilder().forMeta(UserMeta.get()).withUniquePropertyName(UserMeta.get().name.getName()).create();
    }

    @Test(expected = UniqueConstraintException.class)
    public void testConstructorThrowsUniqueConstraintExceptionWithNullValues() throws Exception {
        User u = new User();
        u.setEmail(null);
        User u2 = new User();
        u2.setEmail(null);
        Datastore.put(u, u2);
        new UniqueConstraintBuilder().forMeta(UserMeta.get()).withUniquePropertyName(UserMeta.get().email.getName()).create();
    }

    @Test
    public void testConstructorCanIgnoresNullValues() throws Exception {
        User u = new User();
        u.setEmail(null);
        User u2 = new User();
        u2.setEmail(null);
        Datastore.put(u, u2);
        new UniqueConstraintBuilder().createIfMissing(true).forMeta(UserMeta.get()).withUniquePropertyName(UserMeta.get().email.getName()).ignoreNullValues(true).create();
    }

    @Test
    public void testConstructorThrowsUniqueConstraintExceptionAndDeletesIndex() throws Exception {
        User u = new User();
        u.setName("user");
        User u2 = new User();
        u2.setName("user");
        Datastore.put(u);
        Datastore.put(u2);
        boolean threw;
        try {
            new UniqueConstraintBuilder().forMeta(UserMeta.get()).withUniquePropertyName(UserMeta.get().name.getName()).create();
            threw = false;
        } catch (UniqueConstraintException e) {
            threw = true;
        }
        assertTrue(threw);
        List<Key> keys = Datastore.query().asKeyList();
        for (Key key : keys) {
            assertFalse("Garbage left behind: " + key, key.getKind().startsWith(Constants.UNIQUE_CONSTRAINT_PREFIX));
        }
    }

    @Test(expected = UniqueConstraintException.class)
    public void testConstructorWithClassifierThrowsUniqueConstraintException() throws Exception {
        User u = new User();
        u.setName("user");
        u.setRealName("User Name");
        User u2 = new User();
        u2.setName("user");
        u2.setRealName("User Name");
        Datastore.put(u);
        Datastore.put(u2);
        new UniqueConstraintBuilder().forMeta(UserMeta.get()).withUniquePropertyName(UserMeta.get().name.getName()).withUniqueClassifierPropertyName(UserMeta.get().realName.getName()).create();
    }

    @Test
    public void testConstructorClassifiedThrowsUniqueConstraintExceptionAndDeletesIndex() throws Exception {
        User u = new User();
        u.setName("user");
        u.setRealName("Christian1");
        User u2 = new User();
        u2.setName("user");
        u2.setRealName("Christian2");
        User u3 = new User();
        u3.setName("user");
        u3.setRealName("Christian1");
        Datastore.put(u, u2, u3);
        boolean threw;
        try {
            new UniqueConstraintBuilder().forMeta(UserMeta.get()).withUniquePropertyName(UserMeta.get().name.getName()).withUniqueClassifierPropertyName(UserMeta.get().realName.getName()).create();
            threw = false;
        } catch (UniqueConstraintException e) {
            threw = true;
        }
        assertTrue(threw);
        List<Key> keys = Datastore.query().asKeyList();
        for (Key key : keys) {
            assertFalse("Garbage left behind: " + key, key.getKind().startsWith(Constants.UNIQUE_CONSTRAINT_PREFIX));
        }
    }

    @Test(expected = RuntimeException.class)
    public void testCreateThrowsUniqueConstraintException() throws Exception {
        User u = new User();
        u.setName("user");
        User u2 = new User();
        u2.setName("user");
        Datastore.put(u);
        Datastore.put(u2);
        UniqueConstraint.create(UserMeta.get(), UserMeta.get().name);
    }

    @Test(expected = RuntimeException.class)
    public void testCreateWithClassifierThrowsUniqueConstraintException() throws Exception {
        User u = new User();
        u.setName("user");
        u.setRealName("User Name");
        User u2 = new User();
        u2.setName("user");
        u2.setRealName("User Name");
        Datastore.put(u);
        Datastore.put(u2);
        UniqueConstraint.create(UserMeta.get(), UserMeta.get().name, UserMeta.get().realName);
    }

    @Test
    public void testReserveReleaseReserve() throws Exception {
        UniqueConstraint uc = new UniqueConstraintBuilder().forMeta(UserMeta.get()).withUniquePropertyName(UserMeta.get().name.getName()).createIfMissing(true).create();
        uc.reserve("krico");
        uc.release("krico");
        uc.reserve("krico");
    }

    @Test
    public void testReserveWithClassifierReleaseReserve() throws Exception {
        UniqueConstraint uc = new UniqueConstraintBuilder()
                .forMeta(UserMeta.get())
                .withUniquePropertyName(UserMeta.get().name.getName())
                .withUniqueClassifierPropertyName(UserMeta.get().realName.getName())
                .createIfMissing(true)
                .create();
        uc.reserve("krico", "Christian1");
        uc.reserve("krico", "Christian2");
        uc.release("krico", "Christian2");
        uc.reserve("krico", "Christian2");
    }

    @Test(expected = UniqueConstraintException.class)
    public void testReserveThrows() throws Exception {
        UniqueConstraint uc = new UniqueConstraintBuilder().forMeta(UserMeta.get()).withUniquePropertyName(UserMeta.get().name.getName()).create();
        uc.reserve("krico");
        uc.reserve("krico");
    }

    @Test(expected = UniqueConstraintException.class)
    public void testReserveWithClassifierThrows() throws Exception {
        UniqueConstraint uc = new UniqueConstraintBuilder().forMeta(UserMeta.get()).withUniquePropertyName(UserMeta.get().name.getName()).withUniqueClassifierPropertyName(UserMeta.get().realName.getName()).create();
        uc.reserve("krico", "Christian");
        uc.reserve("krico", "Christian");
    }

    @Test
    public void testReserveWithDifferentClassifierThrows() throws Exception {
        UniqueConstraint uc = new UniqueConstraintBuilder()
                .forMeta(UserMeta.get())
                .withUniquePropertyName(UserMeta.get().name.getName())
                .withUniqueClassifierPropertyName(UserMeta.get().realName.getName())
                .createIfMissing(true)
                .create();
        uc.reserve("krico", "Christian1");
        uc.reserve("krico", "Christian2");
        try {
            uc.reserve("krico", "Christian1");
        } catch (UniqueConstraintException e) {
            return;
        }
        fail("Should have thrown");
    }

    @Test(expected = UniqueConstraintException.class)
    public void testReserveNullThrows() throws Exception {
        UniqueConstraint uc = new UniqueConstraintBuilder().forMeta(UserMeta.get()).withUniquePropertyName(UserMeta.get().name.getName()).create();
        uc.reserve(null);
    }

    @Test(expected = UniqueConstraintException.class)
    public void testReserveClassifiedNullKeyThrows() throws Exception {
        UniqueConstraint uc = new UniqueConstraintBuilder().forMeta(UserMeta.get()).withUniquePropertyName(UserMeta.get().name.getName()).withUniqueClassifierPropertyName(UserMeta.get().realName.getName()).create();
        uc.reserve(null, "something");
    }

    @Test(expected = UniqueConstraintException.class)
    public void testReserveClassifiedNullClassifierThrows() throws Exception {
        UniqueConstraint uc = new UniqueConstraintBuilder().forMeta(UserMeta.get()).withUniquePropertyName(UserMeta.get().name.getName()).withUniqueClassifierPropertyName(UserMeta.get().realName.getName()).create();
        uc.reserve("user", null);
    }

    @Test(expected = UniqueConstraintException.class)
    public void testReserveClassifiedNullThrows() throws Exception {
        UniqueConstraint uc = new UniqueConstraintBuilder().forMeta(UserMeta.get()).withUniquePropertyName(UserMeta.get().name.getName()).withUniqueClassifierPropertyName(UserMeta.get().realName.getName()).create();
        uc.reserve(null, null);
    }

    @Test
    public void testReserveNullNoBreak() throws Exception {
        UniqueConstraint uc = new UniqueConstraintBuilder().createIfMissing(true).forMeta(UserMeta.get()).withUniquePropertyName(UserMeta.get().email.getName()).create();
        try {
            uc.reserve(null);
        } catch (UniqueConstraintException e) {
            //ok
        }
        uc.reserve("newName");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testReserveClassifiedWithNoClassifierThrows() throws Exception {
        UniqueConstraint uc = new UniqueConstraintBuilder()
                .forMeta(UserMeta.get())
                .withUniquePropertyName(UserMeta.get().name.getName())
                .withUniqueClassifierPropertyName(UserMeta.get().realName.getName())
                .createIfMissing(true)
                .create();
        uc.reserve("krico"); //Should call with classifier
    }

    @Test(expected = IllegalArgumentException.class)
    public void testReserveNonClassifiedWithClassifierThrows() throws Exception {
        UniqueConstraint uc = new UniqueConstraintBuilder().createIfMissing(true).forMeta(UserMeta.get()).withUniquePropertyName(UserMeta.get().name.getName()).create();
        uc.reserve("krico", "Christian"); //Should call without classifier
    }

    @Test(expected = IllegalArgumentException.class)
    public void testReleaseClassifiedWithNoClassifierThrows() throws Exception {
        UniqueConstraint uc = new UniqueConstraintBuilder()
                .forMeta(UserMeta.get())
                .withUniquePropertyName(UserMeta.get().name.getName())
                .withUniqueClassifierPropertyName(UserMeta.get().realName.getName())
                .createIfMissing(true)
                .create();
        uc.release("krico"); //Should call with classifier
    }

    @Test(expected = IllegalArgumentException.class)
    public void testReleaseNonClassifiedWithClassifierThrows() throws Exception {
        UniqueConstraint uc = new UniqueConstraintBuilder().createIfMissing(true).forMeta(UserMeta.get()).withUniquePropertyName(UserMeta.get().name.getName()).create();
        uc.release("krico", "Christian"); //Should call without classifier
    }

    @Test(expected = UniqueConstraintException.class)
    public void testConstructorWillFailUnlessCreateIfMissing() throws Exception {
        new UniqueConstraintBuilder().forMeta(UserMeta.get()).withUniquePropertyName(UserMeta.get().name.getName()).createIfMissing(false).create();
    }

}