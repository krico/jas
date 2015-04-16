package com.jasify.schedule.appengine.spi.dm;

import com.jasify.schedule.appengine.model.UserSession;
import com.jasify.schedule.appengine.model.users.User;
import com.jasify.schedule.appengine.util.KeyUtil;

/**
 * @author krico
 * @since 01/01/15.
 */
public class JasLoginResponse implements JasEndpointEntity {
    private String sessionId; /* Session id */
    private String userId;
    private String name;
    private boolean admin;
    private User user;
    private boolean orgMember;

    public JasLoginResponse() {
    }

    public JasLoginResponse(User user, UserSession userSession) {
        setUserId(KeyUtil.keyToString(user.getId()));
        setSessionId(userSession.getSessionId());
        setName(user.getName());
        setAdmin(user.isAdmin());
        setUser(user);
        setOrgMember(userSession.isOrgMember());
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    public boolean isOrgMember() {
        return orgMember;
    }

    public void setOrgMember(boolean orgMember) {
        this.orgMember = orgMember;
    }
}
