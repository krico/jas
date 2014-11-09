package com.jasify.schedule.appengine;

/**
 * Created by krico on 09/11/14.
 */
public final class Constants {
    public static final String APPLICATION_NAME_PROPERTY = "jasify.application.name";
    public static final String DEFAULT_APPLICATION_NAME = "Jasify";
    public static final String APPLICATION_NAME = System.getProperty(APPLICATION_NAME_PROPERTY, DEFAULT_APPLICATION_NAME);

    private Constants() {
    }
}
