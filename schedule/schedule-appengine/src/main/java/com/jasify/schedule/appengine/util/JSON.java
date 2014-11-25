package com.jasify.schedule.appengine.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;

/**
 * @author krico
 * @since 09/11/14.
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

    public static void toJson(Writer writer, Object object) {
        GSON.get().toJson(object, writer);
    }

    public static <T> T fromJson(String data, Class<T> klass) {
        return GSON.get().fromJson(data, klass);
    }

    public static <T> T fromJson(Reader reader, Class<T> klass) {
        return GSON.get().fromJson(reader, klass);
    }

    public static <T> T fromJson(String data, Type typeOfT) {
        return GSON.get().fromJson(data, typeOfT);
    }

    public static <T> T fromJson(Reader reader, Type typeOfT) {
        return GSON.get().fromJson(reader, typeOfT);
    }
}
