package com.jasify.schedule.appengine.http.json;

import com.jasify.schedule.appengine.util.JSON;

import java.io.Reader;

/**
 * @author krico
 * @since 11/11/14.
 */
public class JsonSignUpUser extends JsonUser {
    private String password;

    public static JsonSignUpUser parse(String data) {
        return JSON.fromJson(data, JsonSignUpUser.class);
    }

    public static JsonSignUpUser parse(Reader reader) {
        return JSON.fromJson(reader, JsonSignUpUser.class);
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
