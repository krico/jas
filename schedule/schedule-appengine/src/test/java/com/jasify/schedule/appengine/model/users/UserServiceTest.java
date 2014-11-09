package com.jasify.schedule.appengine.model.users;

import com.google.appengine.api.datastore.ShortBlob;
import com.jasify.schedule.appengine.model.ModelTestHelper;
import com.jasify.schedule.appengine.model.application.ApplicationData;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertNotSame;

public class UserServiceTest {
    private static final Logger log = LoggerFactory.getLogger(UserServiceTest.class);
    private UserService service;

    @Before
    public void initializeDatastore() {
        ModelTestHelper.initializeDatastore();
        service = UserServiceFactory.getUserService();
        ApplicationData.instance().reload();
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

    @Test
    public void testCreateUser() throws Exception {
        User user1 = service.newUser();
        user1.setName("krico");
        service.create(user1, "password");
        ShortBlob password = user1.getPassword();
        assertNotNull("Password should be set", password);
        String pwFromBytes = new String(password.getBytes());
        assertNotSame("Password should be encrypted", "password", pwFromBytes);
        User user2 = service.newUser();
        user2.setName("krico1");
        service.create(user2, "password2");
        ShortBlob password2 = user2.getPassword();
        assertNotNull("Password should be set", password2);
        String pwFromBytes2 = new String(password2.getBytes());
        assertNotSame("Password should be encrypted", "password2", pwFromBytes);
    }

    @Test(expected = UsernameExistsException.class)
    public void testCreateUserWithSameNameThrows() throws Exception {
        User user1 = service.newUser();
        user1.setName("krico");
        service.create(user1, "password1");
        User user2 = service.newUser();
        user2.setName("krico");
        service.create(user2, "password2");
    }
}