package com.jasify.schedule.appengine.http.json;

import com.jasify.schedule.appengine.util.JSON;

import java.io.Reader;

/**
 * @author krico
 * @since 25/12/14.
 */
public class JsonOAuthDetail extends JsonObject {
    private String email;
    private String realName;
    private boolean loggedIn;

    public static JsonOAuthDetail parse(String data) {
        return JSON.fromJson(data, JsonOAuthDetail.class);
    }

    public static JsonOAuthDetail parse(Reader reader) {
        return JSON.fromJson(reader, JsonOAuthDetail.class);
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }

    public void setLoggedIn(boolean loggedIn) {
        this.loggedIn = loggedIn;
    }
}
