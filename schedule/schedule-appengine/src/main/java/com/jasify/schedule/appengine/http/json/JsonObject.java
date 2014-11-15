package com.jasify.schedule.appengine.http.json;

import com.jasify.schedule.appengine.util.JSON;

import java.io.Reader;
import java.io.Writer;

/**
 * @author krico
 * @since 09/11/14.
 */
public class JsonObject {

    public static JsonObject parse(String data) {
        return JSON.fromJson(data, JsonObject.class);
    }

    public static JsonObject parse(Reader reader) {
        return JSON.fromJson(reader, JsonObject.class);
    }

    public void toJson(Writer writer) {
        JSON.toJson(writer, this);
    }

    public String toJson() {
        return JSON.toJson(this);
    }

    @Override
    public String toString() {
        return toJson();
    }
}
