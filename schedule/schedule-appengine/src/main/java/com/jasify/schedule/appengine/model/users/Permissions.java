package com.jasify.schedule.appengine.model.users;

import com.google.appengine.api.datastore.Category;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by krico on 08/11/14.
 */
public final class Permissions {
    public static final Category ADMINISTRATOR = new Category("Admin");

    public static final List<Category> ALL = Collections.unmodifiableList(Arrays.asList(
            ADMINISTRATOR
    ));

    private Permissions() {
    }

    public static boolean isAdmin(User user) {
        return user.getPermissions().contains(ADMINISTRATOR);
    }
}
