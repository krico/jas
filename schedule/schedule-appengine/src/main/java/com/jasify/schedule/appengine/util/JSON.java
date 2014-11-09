package com.jasify.schedule.appengine.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Created by krico on 09/11/14.
 */
public final class JSON {
    public static final String CONTENT_TYPE = "application/json";
    private static final ThreadLocal<Gson> GSON = new ThreadLocal<Gson>() {
        @Override
        protected Gson initialValue() {
            return new GsonBuilder().create();
        }
    };

    private JSON() {
    }

    public static String toJson(Object object) {
        return GSON.get().toJson(object);
    }

    public static <T> T fromJson(String data, Class<T> klass) {
        return GSON.get().fromJson(data, klass);
    }
}
