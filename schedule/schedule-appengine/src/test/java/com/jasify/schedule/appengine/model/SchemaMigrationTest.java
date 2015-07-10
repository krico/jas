package com.jasify.schedule.appengine.model;

import com.google.appengine.api.datastore.Category;
import com.google.appengine.api.datastore.Entity;
import com.jasify.schedule.appengine.TestHelper;
import com.jasify.schedule.appengine.meta.activity.ActivityTypeMeta;
import com.jasify.schedule.appengine.meta.users.UserMeta;
import com.jasify.schedule.appengine.meta.users.User_v1Meta;
import com.jasify.schedule.appengine.model.activity.ActivityType;
import com.jasify.schedule.appengine.model.application.ApplicationData;
import com.jasify.schedule.appengine.model.common.Organization;
import com.jasify.schedule.appengine.model.users.User;
import com.jasify.schedule.appengine.model.users.UserDetail;
import com.jasify.schedule.appengine.model.users.User_v0;
import com.jasify.schedule.appengine.model.users.User_v1;
import com.jasify.schedule.appengine.util.TypeUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slim3.datastore.Datastore;

import java.util.List;
import java.util.Objects;

import static junit.framework.TestCase.*;

public class SchemaMigrationTest {
    @Before
    public void initializeDatastore() {
        TestHelper.initializeDatastore();
        ApplicationData.instance().reload();
        UniqueConstraints.ensureAllConstraintsExist();
    }

    @After
    public void cleanupDatastore() {
        TestHelper.cleanupDatastore();
    }

    @Test
    public void testExecutePendingMigrations() throws Exception {
        assertTrue(SchemaMigration.instance().executePendingMigrations());
        assertFalse(SchemaMigration.instance().executePendingMigrations());
    }

    @Test
    public void testMigrateActivityType_v0_to_ActivityType_v1() throws Exception {
        Organization o1 = new Organization("Org1");
        o1.setId(Datastore.allocateId(Organization.class));
        ActivityType at1 = new ActivityType("Test1");
        at1.setId(Datastore.allocateId(o1.getId(), ActivityType.class));
        Entity entity = ActivityTypeMeta.get().modelToEntity(at1);
        entity.removeProperty("SV");
        entity.removeProperty(ActivityTypeMeta.get().organizationRef.getName());
        Datastore.put(o1, entity);

        ActivityType at1Fetched = Datastore.get(ActivityTypeMeta.get(), at1.getId());
        assertNotNull(at1Fetched);
        assertNull(at1Fetched.getOrganizationRef().getKey());

        assertEquals(1, SchemaMigration.instance().migrateActivityType_v0_to_ActivityType_v1());

        at1Fetched = Datastore.get(ActivityTypeMeta.get(), at1.getId());
        assertNotNull(at1Fetched);
        assertNotNull(at1Fetched.getOrganizationRef().getKey());
        assertEquals(o1.getId(), at1Fetched.getOrganizationRef().getKey());

        assertEquals(0, SchemaMigration.instance().migrateActivityType_v0_to_ActivityType_v1());
    }

    @Test
    public void testMigrateUser_v0_to_User_v1() throws Exception {
        User_v0 him = new User_v0();
        him.setId(Datastore.createKey(User_v0.class, 55));
        him.setAbout(TypeUtil.toText("About him..."));
        him.setEmail(TypeUtil.toEmail("hIm@hiM.com"));
        him.setName("him");
        him.setNameWithCase("hIm");
        him.setPassword(TypeUtil.toShortBlob(new byte[]{1, 2, 3, 4}));
        him.addPermission(new Category("Admin"));

        User_v0 her = new User_v0();
        her.setId(Datastore.createKey(User_v0.class, 56));
        her.setEmail(TypeUtil.toEmail("hEr@hiM.com"));
        her.setName("her");
        her.setNameWithCase("hEr");
        her.setPassword(TypeUtil.toShortBlob(new byte[]{1, 1, 1}));

        Datastore.put(him, her);

        assertEquals(2, SchemaMigration.instance().migrateUser_v0_to_User_v1());

        List<User> users = Datastore.query(UserMeta.get()).sort(UserMeta.get().id.asc).asList();
        assertNotNull(users);
        assertEquals(2, users.size());
        User migratedHim = users.get(0);
        assertEquals(55, migratedHim.getId().getId());
        assertEquals("him@him.com", migratedHim.getEmail());
        assertEquals("him", migratedHim.getName());
        assertTrue(Objects.deepEquals(new byte[]{1, 2, 3, 4}, TypeUtil.toBytes(migratedHim.getPassword())));
        assertTrue(migratedHim.isAdmin());
        UserDetail himDetail = Datastore.query(UserDetail.class, migratedHim.getId()).asSingle();
        assertNotNull(himDetail);
        assertEquals("About him...", TypeUtil.toString(himDetail.getAbout()));
        assertEquals("Link not working", himDetail, migratedHim.getDetailRef().getModel());

        User migratedHer = users.get(1);
        assertEquals(56, migratedHer.getId().getId());
        assertEquals("her@him.com", migratedHer.getEmail());
        assertEquals("her", migratedHer.getName());
        assertTrue(Objects.deepEquals(new byte[]{1, 1, 1}, TypeUtil.toBytes(migratedHer.getPassword())));
        assertFalse(migratedHer.isAdmin());
        UserDetail herDetail = Datastore.query(UserDetail.class, migratedHer.getId()).asSingle();
        assertNull(herDetail);

        assertEquals(0, SchemaMigration.instance().migrateUser_v0_to_User_v1());
    }

    @Test
    public void testMigrateUser_v1_to_User_v2() throws Exception {
        List<Entity> entities = Datastore.query(UserMeta.get()).sort(UserMeta.get().id.asc).asEntityList();
        for (Entity entity : entities) {
            assertTrue(entity.hasProperty(User_v1Meta.get().nameWithCase.getName()));
        }

        User_v1 him = new User_v1();
        him.setId(Datastore.createKey(User_v1.class, 55));
        him.setAbout("About him...");
        him.setEmail("him@him.com");
        him.setName("him");
        him.setNameWithCase("hIm");
        him.setPassword(TypeUtil.toShortBlob(new byte[]{1, 2, 3, 4}));
        him.setAdmin(true);

        User_v1 her = new User_v1();
        her.setId(Datastore.createKey(User_v1.class, 56));
        her.setEmail("her@him.com");
        her.setName("her");
        her.setNameWithCase("hEr");
        her.setPassword(TypeUtil.toShortBlob(new byte[]{1, 1, 1}));

        Datastore.put(him, her, him.getDetailRef().getModel());

        assertEquals(2, SchemaMigration.instance().migrateUser_v1_to_User_v2());

        List<User> users = Datastore.query(UserMeta.get()).sort(UserMeta.get().id.asc).asList();
        assertNotNull(users);
        assertEquals(2, users.size());
        User migratedHim = users.get(0);
        assertEquals(55, migratedHim.getId().getId());
        assertEquals("him@him.com", migratedHim.getEmail());
        assertEquals("him", migratedHim.getName());
        assertTrue(Objects.deepEquals(new byte[]{1, 2, 3, 4}, TypeUtil.toBytes(migratedHim.getPassword())));
        assertTrue(migratedHim.isAdmin());
        UserDetail himDetail = Datastore.query(UserDetail.class, migratedHim.getId()).asSingle();
        assertNotNull(himDetail);
        assertEquals("About him...", TypeUtil.toString(himDetail.getAbout()));
        assertEquals("Link not working", himDetail, migratedHim.getDetailRef().getModel());

        User migratedHer = users.get(1);
        assertEquals(56, migratedHer.getId().getId());
        assertEquals("her@him.com", migratedHer.getEmail());
        assertEquals("her", migratedHer.getName());
        assertTrue(Objects.deepEquals(new byte[]{1, 1, 1}, TypeUtil.toBytes(migratedHer.getPassword())));
        assertFalse(migratedHer.isAdmin());
        UserDetail herDetail = Datastore.query(UserDetail.class, migratedHer.getId()).asSingle();
        assertNull(herDetail);

        assertEquals(0, SchemaMigration.instance().migrateUser_v0_to_User_v1());

        entities = Datastore.query(UserMeta.get()).sort(UserMeta.get().id.asc).asEntityList();
        for (Entity entity : entities) {
            assertFalse(entity.hasProperty(User_v1Meta.get().nameWithCase.getName()));
        }

    }
}