package com.jasify.schedule.appengine.http.json;

import com.jasify.schedule.appengine.http.HttpUserSession;
import com.jasify.schedule.appengine.model.users.User;
import com.jasify.schedule.appengine.spi.transform.JasKeyTransformer;
import com.jasify.schedule.appengine.util.JSON;

import java.io.Reader;

/**
 * @author krico
 * @since 16/12/14.
 */
public class JsonSessionResponse extends JsonObject {
    private String id; /* Session id */
    private String userId;
    private JsonUser user;

    public JsonSessionResponse() {
    }

    public JsonSessionResponse(User user, HttpUserSession userSession) {
        this.id = userSession.getSessionId();
        this.userId = new JasKeyTransformer().transformTo(user.getId());
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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public JsonUser getUser() {
        return user;
    }

    public void setUser(JsonUser user) {
        this.user = user;
    }
}
