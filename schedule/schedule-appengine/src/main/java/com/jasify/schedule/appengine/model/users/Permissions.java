package com.jasify.schedule.appengine.model.users;

import com.google.appengine.api.datastore.Category;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by krico on 08/11/14.
 */
public final class Permissions {
    public static final Category USER = new Category("User");
    public static final Category ADMINISTRATOR = new Category("Admin");
    public static final Category SYSTEM_ADMINISTRATOR = new Category("SysAdmin");

    public static final List<Category> ALL = Collections.unmodifiableList(Arrays.asList(
            USER, ADMINISTRATOR, SYSTEM_ADMINISTRATOR
    ));

    private Permissions() {
    }
}
