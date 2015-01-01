package com.jasify.schedule.appengine.spi.dm;

import com.jasify.schedule.appengine.http.json.JsonUser;

/**
 * @author krico
 * @since 01/01/15.
 */
public class JasLoginResponse implements JasEndpointEntity {
    private String sessionId; /* Session id */
    private long userId;
    private String name;

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
