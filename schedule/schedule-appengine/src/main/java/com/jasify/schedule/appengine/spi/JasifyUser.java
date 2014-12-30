package com.jasify.schedule.appengine.spi;

import com.google.api.server.spi.auth.common.User;
import com.jasify.schedule.appengine.model.UserSession;

/**
 * @author krico
 * @since 27/12/14.
 */
public class JasifyUser extends User {
    private final long userId;
    private final boolean admin;

    public JasifyUser(String email, long userId, boolean admin) {
        super(email);
        this.userId = userId;
        this.admin = admin;
    }

    public JasifyUser(UserSession userSession) {
        super(userSession.getUserId() + "@jasify.com");
        this.userId = userSession.getUserId();
        this.admin = userSession.isAdmin();
    }

    public long getUserId() {
        return userId;
    }

    public boolean isAdmin() {
        return admin;
    }
}
