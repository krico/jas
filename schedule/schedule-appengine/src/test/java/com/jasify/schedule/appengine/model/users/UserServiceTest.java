package com.jasify.schedule.appengine.model.users;

import com.google.appengine.api.datastore.Email;
import com.google.appengine.api.datastore.ShortBlob;
import com.google.appengine.api.datastore.Text;
import com.jasify.schedule.appengine.model.EntityNotFoundException;
import com.jasify.schedule.appengine.model.ModelTestHelper;
import com.jasify.schedule.appengine.model.application.ApplicationData;
import org.apache.commons.lang3.RandomUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static junit.framework.TestCase.*;

public class UserServiceTest {
    private static final Logger log = LoggerFactory.getLogger(UserServiceTest.class);
    private UserService service;
    private List<User> createdUsers = new ArrayList<>();

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
        createdUsers.add(user1);
        createdUsers.add(user2);
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

    @Test(expected = UsernameExistsException.class)
    public void testCreateUserWithSameNameAndDifferentCaseThrows() throws Exception {
        User user1 = service.newUser();
        user1.setName("krico");
        service.create(user1, "password1");
        User user2 = service.newUser();
        user2.setName("kriCo");
        service.create(user2, "password2");
    }

    @Test
    public void testGet() throws Exception {
        testCreateUser();
        HashSet<Long> usedIds = new HashSet<>();
        User expected1 = createdUsers.get(0);
        long id1 = expected1.getId().getId();
        User user1 = service.get(id1);
        assertEquals("\nE:" + expected1.debugString() + "\nV:" + user1.debugString(), expected1, user1);
        User expected2 = createdUsers.get(1);
        long id2 = expected2.getId().getId();
        User user2 = service.get(id2);
        assertEquals(expected2, user2);
        usedIds.add(id1);
        usedIds.add(id2);
        assertEquals(2, usedIds.size());
        for (int i = 0; i < 1000; ++i) {
            long id;
            do {
                id = RandomUtils.nextLong(0, 2000000);
            } while (usedIds.contains(id));
            assertNull(service.get(id));
        }
    }

    @Test
    public void testFindUserByName() throws Exception {
        testCreateUser();
        User user = createdUsers.get(0);
        User krico = service.findByName(user.getName().toUpperCase());
        assertNotNull("Upper case", krico);
        assertEquals(user, krico);
        krico = service.findByName(user.getName().toLowerCase());
        assertNotNull(krico);
        assertEquals(user, krico);

        assertNull(service.findByName("sasquatch"));
    }

    @Test
    public void testSaveUser() throws Exception {
        testCreateUser();
        User user = service.get(createdUsers.get(0).getId().getId());
        Text expectedAbout = new Text("About me");
        user.setAbout(expectedAbout);
        Email expectedEmail = new Email("test@test.com");
        user.setEmail(expectedEmail);
        user.addPermission(Permissions.USER);
        service.save(user);
        User updated = service.get(user.getId().getId());
        assertNotNull(updated);
        assertEquals(expectedAbout, updated.getAbout());
        assertEquals(expectedEmail, updated.getEmail());
        assertEquals(1, updated.getPermissions().size());
        assertTrue(updated.hasPermission(Permissions.USER));
    }

    @Test(expected = EntityNotFoundException.class)
    public void testSaveNonExistentUserThrows() throws Exception {
        service.save(service.newUser());
    }
}