package com.jasify.schedule.appengine.http.json;

import com.jasify.schedule.appengine.util.JSON;

import java.io.Reader;

/**
 * @author krico
 * @since 10/11/14.
 */
public class JsonLoginRequest extends JsonObject {
    private String name;
    private String password;

    public JsonLoginRequest() {
    }

    public JsonLoginRequest(String name, String password) {
        this.name = name;
        this.password = password;
    }

    public static JsonLoginRequest parse(String data) {
        return JSON.fromJson(data, JsonLoginRequest.class);
    }

    public static JsonLoginRequest parse(Reader reader) {
        return JSON.fromJson(reader, JsonLoginRequest.class);
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
