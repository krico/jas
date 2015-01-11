package com.jasify.schedule.appengine.model.users;

import com.jasify.schedule.appengine.TestHelper;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static junit.framework.TestCase.assertNotNull;

public class UserServiceFactoryTest {
    @BeforeClass
    public static void initializeDatastore() {
        TestHelper.initializeJasify();
    }

    @AfterClass
    public static void cleanupDatastore() {
        TestHelper.cleanupDatastore();
    }

    @Test
    public void testGetUserService() throws Exception {
        assertNotNull(UserServiceFactory.getUserService());
    }
}