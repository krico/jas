package com.jasify.schedule.appengine.model.users;

import com.google.appengine.api.datastore.Category;

/**
 * Created by krico on 08/11/14.
 */
public final class Permissions {
    public final static Category USER = new Category("User");
    public final static Category ADMINISTRATOR = new Category("Admin");
    public final static Category SYSTEM_ADMINISTRATOR = new Category("SysAdmin");

    private Permissions() {
    }
}
