package com.jasify.schedule.appengine.model;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalMemcacheServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;

/**
 * Created by krico on 08/11/14.
 */
public class ModelTestHelper {
    private final LocalServiceTestHelper datastoreHelper = new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());
    private final LocalServiceTestHelper memcacheHelper = new LocalServiceTestHelper(new LocalMemcacheServiceTestConfig());


    public static void initializeDatastore() {
        Singleton.INSTANCE.datastoreHelper.setUp();
    }

    public static void cleanupDatastore() {
        Singleton.INSTANCE.datastoreHelper.tearDown();
    }

    public static void initializeMemcache() {
        Singleton.INSTANCE.memcacheHelper.setUp();
    }

    public static void cleanupMemcache() {
        Singleton.INSTANCE.memcacheHelper.tearDown();
    }

    private static class Singleton {
        private static final ModelTestHelper INSTANCE = new ModelTestHelper();
    }

}
