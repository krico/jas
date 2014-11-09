package com.jasify.schedule.appengine.model.users;

import com.jasify.schedule.appengine.TestHelper;
import com.jasify.schedule.appengine.model.application.ApplicationData;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.TestCase.assertNotNull;

public class UserServiceFactoryTest {
    @Before
    public void initializeDatastore() {
        TestHelper.initializeJasify();
    }

    @After
    public void cleanupDatastore() {
        TestHelper.cleanupDatastore();
    }

    @Test
    public void testAssertUtilityClassWellDefined() throws Exception {
        TestHelper.assertUtilityClassWellDefined(UserServiceFactory.class);
    }


    @Test
    public void testGetUserService() throws Exception {
        assertNotNull(UserServiceFactory.getUserService());
    }
}