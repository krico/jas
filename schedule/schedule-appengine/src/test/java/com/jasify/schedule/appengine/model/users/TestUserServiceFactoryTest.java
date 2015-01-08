package com.jasify.schedule.appengine.model.users;

import com.jasify.schedule.appengine.TestHelper;
import org.easymock.EasyMockRunner;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.TestCase.*;

@RunWith(EasyMockRunner.class)
public class TestUserServiceFactoryTest {
    private TestUserServiceFactory testUserServiceFactory = new TestUserServiceFactory();

    @BeforeClass
    public static void initializeDatastore() {
        TestHelper.initializeDatastore();
    }

    @AfterClass
    public static void cleanupDatastore() {
        TestHelper.cleanupDatastore();
    }


    @Test
    public void testExplicitSetter() throws Exception {

        assertNotNull(UserServiceFactory.getUserService());
        assertTrue(UserServiceFactory.getUserService() instanceof DefaultUserService);

        testUserServiceFactory.setUp();
        testUserServiceFactory.replay();

        assertNotNull(UserServiceFactory.getUserService());
        assertEquals(testUserServiceFactory.getUserServiceMock(), UserServiceFactory.getUserService());

        testUserServiceFactory.tearDown();

        assertNotNull(UserServiceFactory.getUserService());
        assertTrue(UserServiceFactory.getUserService() instanceof DefaultUserService);
    }

}