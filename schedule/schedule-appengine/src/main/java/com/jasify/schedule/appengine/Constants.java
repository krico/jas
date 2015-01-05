package com.jasify.schedule.appengine;

import com.google.appengine.api.datastore.Query;

/**
 * @author krico
 * @since 09/11/14.
 */
public final class Constants {
    public static final String SCHEMA_VERSION_NAME = "SV";

    public static final String APPLICATION_NAME_PROPERTY = "jasify.application.name";
    public static final String DEFAULT_APPLICATION_NAME = "Jasify";
    public static final String APPLICATION_NAME = System.getProperty(APPLICATION_NAME_PROPERTY, DEFAULT_APPLICATION_NAME);

    public static final String UNIQUE_CONSTRAINT_PREFIX_PROPERTY = "jasify.application.UniqueConstraint.prefix";
    public static final String DEFAULT_UNIQUE_CONSTRAINT_PREFIX = "UC_";
    public static final String UNIQUE_CONSTRAINT_PREFIX = System.getProperty(UNIQUE_CONSTRAINT_PREFIX_PROPERTY, DEFAULT_UNIQUE_CONSTRAINT_PREFIX);

    public static final String DATASTORE_HIGH_VALUE = "\ufffd";

    public static final int DEFAULT_LIMIT = 10;
    public static final Query.SortDirection DEFAULT_ORDER = Query.SortDirection.ASCENDING;

    private Constants() {
    }
}
