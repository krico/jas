package com.jasify.schedule.appengine.model;

import com.google.appengine.api.datastore.Category;
import com.jasify.schedule.appengine.TestHelper;
import com.jasify.schedule.appengine.meta.users.UserMeta;
import com.jasify.schedule.appengine.model.application.ApplicationData;
import com.jasify.schedule.appengine.model.users.User;
import com.jasify.schedule.appengine.model.users.UserDetail;
import com.jasify.schedule.appengine.model.users.User_v0;
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
        assertEquals("hIm", migratedHim.getNameWithCase());
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
        assertEquals("hEr", migratedHer.getNameWithCase());
        assertTrue(Objects.deepEquals(new byte[]{1, 1, 1}, TypeUtil.toBytes(migratedHer.getPassword())));
        assertFalse(migratedHer.isAdmin());
        UserDetail herDetail = Datastore.query(UserDetail.class, migratedHer.getId()).asSingle();
        assertNull(herDetail);

        assertEquals(0, SchemaMigration.instance().migrateUser_v0_to_User_v1());
    }
}