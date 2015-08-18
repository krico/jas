package com.jasify.schedule.appengine.model;

import com.google.appengine.api.datastore.Key;

/**
 * @author krico
 * @since 11/11/14.
 */
public interface UserSession {
    /**
     * Invalidates this session.  After this user needs to login again to get a session.
     */
    void invalidate();

    long getUserId();

    Key getUserIdKey();

    String getSessionId();

    boolean isAdmin();

    boolean isOrgMember();
}
