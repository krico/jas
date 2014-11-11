package com.jasify.schedule.appengine.model;

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
}
