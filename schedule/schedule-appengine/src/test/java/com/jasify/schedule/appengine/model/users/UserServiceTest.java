package com.jasify.schedule.appengine.model.users;

import com.jasify.schedule.appengine.model.ModelTestHelper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static junit.framework.TestCase.assertNotNull;

public class UserServiceTest {
    private static final Logger log = LoggerFactory.getLogger(UserServiceTest.class);
    private UserService service;

    @Before
    public void initializeDatastore() {
        ModelTestHelper.initializeDatastore();
        service = UserServiceFactory.getUserService();
    }

    @After
    public void cleanupDatastore() {
        service = null;
        ModelTestHelper.cleanupDatastore();
    }

    @Test
    public void testNewUser() {
        User user = service.newUser();
        log.info("User: {}", user);
        assertNotNull(user);
        assertNotNull(user.getId());
    }
}