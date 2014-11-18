package com.jasify.schedule.appengine.model.users;

import com.google.appengine.api.datastore.Category;
import com.jasify.schedule.appengine.TestHelper;
import org.junit.Test;

import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;

public class PermissionsTest {
    @Test
    public void testAssertUtilityClassWellDefined() throws Exception {
        TestHelper.assertUtilityClassWellDefined(Permissions.class);
    }

    @Test
    public void testAdmin() {
        User user = new User();
        assertFalse(Permissions.isAdmin(user));
        user.getPermissions().add(Permissions.ADMINISTRATOR);
        assertTrue(Permissions.isAdmin(user));
        user.getPermissions().remove(new Category(Permissions.ADMINISTRATOR.getCategory()));
        assertFalse(Permissions.isAdmin(user));
        user.getPermissions().add(new Category(Permissions.ADMINISTRATOR.getCategory()));
        assertTrue(Permissions.isAdmin(user));
    }
}