package com.jasify.schedule.appengine.http.json;

import com.jasify.schedule.appengine.util.JSON;

/**
 * Created by krico on 09/11/14.
 */
public class JsonObject {

    public static JsonObject parse(String data) {
        return JSON.fromJson(data, JsonObject.class);
    }

    @Override
    public String toString() {
        return toJson();
    }

    public String toJson() {
        return JSON.toJson(this);
    }
}
