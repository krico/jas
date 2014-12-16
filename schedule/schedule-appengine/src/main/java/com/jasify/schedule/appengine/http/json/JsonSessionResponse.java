package com.jasify.schedule.appengine.http.json;

import com.jasify.schedule.appengine.http.HttpUserSession;
import com.jasify.schedule.appengine.model.users.User;
import com.jasify.schedule.appengine.util.JSON;

import java.io.Reader;

/**
 * @author krico
 * @since 16/12/14.
 */
public class JsonSessionResponse extends JsonObject {
    private String id; /* Session id */
    private long userId;
    private JsonUser user;

    public JsonSessionResponse() {
    }

    public JsonSessionResponse(User user, HttpUserSession userSession) {
        this.id = userSession.getSessionId();
        userId = userSession.getUserId();
        this.user = new JsonUser(user);
    }

    public static JsonSessionResponse parse(String data) {
        return JSON.fromJson(data, JsonSessionResponse.class);
    }

    public static JsonSessionResponse parse(Reader reader) {
        return JSON.fromJson(reader, JsonSessionResponse.class);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public JsonUser getUser() {
        return user;
    }

    public void setUser(JsonUser user) {
        this.user = user;
    }
}
