package com.jasify.schedule.appengine.spi.auth;

import com.google.api.server.spi.auth.common.User;
import com.jasify.schedule.appengine.model.UserSession;

/**
 * @author krico
 * @since 27/12/14.
 */
public class JasifyEndpointUser extends User {
    private final long userId;
    private final boolean admin;
    private final boolean orgMember;

    public JasifyEndpointUser(String email, long userId, boolean admin, boolean orgMember) {
        super(email);
        this.userId = userId;
        this.admin = admin;
        this.orgMember = orgMember;
    }

    public JasifyEndpointUser(UserSession userSession) {
        super(userSession.getUserId() + "@jasify.com");
        this.userId = userSession.getUserId();
        this.admin = userSession.isAdmin();
        this.orgMember = userSession.isOrgMember();
    }

    public long getUserId() {
        return userId;
    }

    public boolean isAdmin() {
        return admin;
    }

    public boolean isOrgMember() {
        return orgMember;
    }

    @Override
    public String toString() {
        return "JasifyEndpointUser{" +
                "userId=" + userId +
                ", admin=" + admin +
                '}';
    }
}
