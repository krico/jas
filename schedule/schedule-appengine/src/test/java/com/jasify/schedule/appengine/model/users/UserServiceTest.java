package com.jasify.schedule.appengine.model.users;

import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.ShortBlob;
import com.jasify.schedule.appengine.TestHelper;
import com.jasify.schedule.appengine.model.EntityNotFoundException;
import com.jasify.schedule.appengine.model.FieldValueException;
import org.apache.commons.lang3.RandomUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slim3.datastore.Datastore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Pattern;

import static junit.framework.TestCase.*;

public class UserServiceTest {
    private static final Logger log = LoggerFactory.getLogger(UserServiceTest.class);
    private UserService service;
    private List<User> createdUsers = new ArrayList<>();

    @Before
    public void initializeDatastore() {
        TestHelper.initializeJasify();
        service = UserServiceFactory.getUserService();
    }

    @After
    public void cleanupDatastore() {
        service = null;
        TestHelper.cleanupDatastore();
    }

    @Test
    public void testNewUser() {
        User user = service.newUser();
        log.info("User: {}", user);
        assertNotNull(user);
        assertNotNull(user.getId());
    }

    @Test
    public void testCreateWithPassword() throws Exception {
        User user1 = service.newUser();
        user1.setName("test");
        service.create(user1, "password");
        assertFalse(user1.isAdmin());
        ShortBlob password = user1.getPassword();
        assertNotNull("Password should be set", password);
        String pwFromBytes = new String(password.getBytes());
        assertNotSame("Password should be encrypted", "password", pwFromBytes);
        User user2 = service.newUser();
        user2.setName("krico1");
        service.create(user2, "password2");
        assertFalse(user2.isAdmin());
        ShortBlob password2 = user2.getPassword();
        assertNotNull("Password should be set", password2);
        String pwFromBytes2 = new String(password2.getBytes());
        assertNotSame("Password should be encrypted", "password2", pwFromBytes2);
        createdUsers.add(user1);
        createdUsers.add(user2);
        assertEquals(createdUsers.size(), service.getTotalUsers());
    }

    private UserLogin newGoogleLogin() {
        return new UserLogin("Google", "1234");
    }

    @Test
    public void testCreateWithUserLogin() throws Exception {
        User user1 = service.newUser();
        user1.setName("test");
        service.create(user1, newGoogleLogin());
        assertFalse(user1.isAdmin());
        assertNull("Password should not be set", user1.getPassword());
        User user2 = service.newUser();
        user2.setName("krico1");
        service.create(user2, new UserLogin("Google", "5678"));
        assertFalse(user2.isAdmin());
        assertNull("Password should not be set", user2.getPassword());
        createdUsers.add(user1);
        createdUsers.add(user2);
        assertEquals(createdUsers.size(), service.getTotalUsers());
    }

    @Test
    public void testGetUserLoginsWithOneLogin() throws Exception {
        User googleUser = service.newUser();
        googleUser.setName("test");
        UserLogin originalGoogleLogin = newGoogleLogin();
        googleUser = service.create(googleUser, originalGoogleLogin);

        List<UserLogin> googleLogins = service.getUserLogins(googleUser);
        assertNotNull(googleLogins);
        assertEquals(1, googleLogins.size());
        UserLogin userLogin = googleLogins.get(0);
        assertEquals(originalGoogleLogin.getProvider(), userLogin.getProvider());
        assertEquals(originalGoogleLogin.getUserId(), userLogin.getUserId());
        assertNotNull(originalGoogleLogin.getUserRef());
        assertNotNull(originalGoogleLogin.getUserRef().getModel());
        assertEquals(googleUser, originalGoogleLogin.getUserRef().getModel());
    }

    @Test
    public void testGetUserLoginsWithNoLogin() throws Exception {
        User user = service.newUser();
        user.setName("test");
        user = service.create(user, "password");

        List<UserLogin> logins = service.getUserLogins(user);
        assertNotNull(logins);
        assertEquals(0, logins.size());
    }

    @Test(expected = EntityNotFoundException.class)
    public void testAddUserLoginThrowsOnNotFound() throws Exception {
        User user = service.newUser();
        user.setName("test");
        service.addLogin(user, newGoogleLogin());
    }

    @Test(expected = UserLoginExistsException.class)
    public void testAddUserLoginThrowsOnDuplicate() throws Exception {
        User user = service.newUser();
        user.setName("test");
        user = service.create(user, "password");

        UserLogin login1 = newGoogleLogin();
        service.addLogin(user, login1);
        service.addLogin(user, login1);
    }

    @Test(expected = EntityNotFoundException.class)
    public void testRemoveLoginNotFoundThrowsEntityNotFoundException() throws Exception {
        service.removeLogin(Datastore.allocateId(UserLogin.class));
    }

    @Test
    public void testAddGetRemoveUserLogin() throws Exception {
        User user = service.newUser();
        user.setName("test");
        user = service.create(user, "password");

        UserLogin login1 = newGoogleLogin();
        service.addLogin(user, login1);

        List<UserLogin> logins = service.getUserLogins(user);
        assertNotNull(logins);
        assertEquals(1, logins.size());
        assertEquals(login1.getProvider(), logins.get(0).getProvider());
        assertEquals(login1.getUserId(), logins.get(0).getUserId());

        UserLogin login2 = new UserLogin("Google", "4321");
        service.addLogin(user, login2);

        logins = service.getUserLogins(user);
        assertNotNull(logins);
        assertEquals(2, logins.size());
        Collections.sort(logins);
        assertEquals(login1.getProvider(), logins.get(0).getProvider());
        assertEquals(login1.getUserId(), logins.get(0).getUserId());
        assertEquals(login2.getProvider(), logins.get(1).getProvider());
        assertEquals(login2.getUserId(), logins.get(1).getUserId());

        UserLogin login3 = new UserLogin("Facebook", "4321");
        service.addLogin(user, login3);

        logins = service.getUserLogins(user);
        assertNotNull(logins);
        assertEquals(3, logins.size());
        Collections.sort(logins);
        assertEquals(login3.getProvider(), logins.get(0).getProvider());
        assertEquals(login3.getUserId(), logins.get(0).getUserId());
        assertEquals(login1.getProvider(), logins.get(1).getProvider());
        assertEquals(login1.getUserId(), logins.get(1).getUserId());
        assertEquals(login2.getProvider(), logins.get(2).getProvider());
        assertEquals(login2.getUserId(), logins.get(2).getUserId());

        //Now remove
        UserLogin gotten = service.getLogin(logins.get(1).getId());
        assertNotNull(gotten);
        service.removeLogin(gotten.getId());

        logins = service.getUserLogins(user);
        assertNotNull(logins);
        assertEquals(2, logins.size());
        Collections.sort(logins);
        assertEquals(login3.getProvider(), logins.get(0).getProvider());
        assertEquals(login3.getUserId(), logins.get(0).getUserId());
        assertEquals(login2.getProvider(), logins.get(1).getProvider());
        assertEquals(login2.getUserId(), logins.get(1).getUserId());

        //ensure we released this login
        service.addLogin(user, new UserLogin(login1.getProvider(), login1.getUserId()));

        logins = service.getUserLogins(user);
        assertNotNull(logins);
        assertEquals(3, logins.size());
        Collections.sort(logins);
        assertEquals(login3.getProvider(), logins.get(0).getProvider());
        assertEquals(login3.getUserId(), logins.get(0).getUserId());
        assertEquals(login1.getProvider(), logins.get(1).getProvider());
        assertEquals(login1.getUserId(), logins.get(1).getUserId());
        assertEquals(login2.getProvider(), logins.get(2).getProvider());
        assertEquals(login2.getUserId(), logins.get(2).getUserId());

    }

    @Test(expected = UsernameExistsException.class)
    public void testCreateWithPasswordSameNameThrows() throws Exception {
        User user1 = service.newUser();
        user1.setName("test");
        service.create(user1, "password1");
        User user2 = service.newUser();
        user2.setName("test");
        service.create(user2, "password2");
    }

    @Test(expected = UsernameExistsException.class)
    public void testCreateWithUserLoginSameNameAsPasswordThrows() throws Exception {
        User user1 = service.newUser();
        user1.setName("test");
        service.create(user1, "password1");
        User user2 = service.newUser();
        user2.setName("test");
        service.create(user2, new UserLogin("Google", "123456"));
    }

    @Test(expected = UsernameExistsException.class)
    public void testCreateWithUserLoginSameNameAsUserLoginThrows() throws Exception {
        User user1 = service.newUser();
        user1.setName("test");
        service.create(user1, new UserLogin("Google", "123456"));
        User user2 = service.newUser();
        user2.setName("test");
        service.create(user2, "password");
    }

    @Test(expected = UserLoginExistsException.class)
    public void testCreateWithExistingUserLogin() throws Exception {
        User user1 = service.newUser();
        user1.setName("test1");
        service.create(user1, new UserLogin("Google", "123456"));
        User user2 = service.newUser();
        user2.setName("test2");
        service.create(user2, new UserLogin("Google", "123456"));
    }

    @Test(expected = UsernameExistsException.class)
    public void testCreateWithPasswordSameNameAndDifferentCaseThrows() throws Exception {
        User user1 = service.newUser();
        user1.setName("test");
        service.create(user1, "password1");
        User user2 = service.newUser();
        user2.setName("teSt");
        service.create(user2, "password2");
    }

    @Test(expected = UsernameExistsException.class)
    public void testCreateWithUserLoginSameNameAndDifferentCaseThrows() throws Exception {
        User user1 = service.newUser();
        user1.setName("test");
        service.create(user1, new UserLogin("Google", "1"));
        User user2 = service.newUser();
        user2.setName("teSt");
        service.create(user2, new UserLogin("Google", "2"));
    }


    @Test(expected = EmailExistsException.class)
    public void testCreateWithLoginSameEmailThrows() throws Exception {
        User user1 = service.newUser();
        user1.setName("test");
        user1.setEmail("test@test");
        service.create(user1, new UserLogin("Google", "1"));
        User user2 = service.newUser();
        user2.setName("foo");
        user2.setEmail(user1.getEmail());
        service.create(user2, new UserLogin("Google", "2"));
    }

    @Test(expected = EmailExistsException.class)
    public void testCreateWithPasswordSameEmailThrows() throws Exception {
        User user1 = service.newUser();
        user1.setName("test");
        user1.setEmail("test@test");
        service.create(user1, "pw");
        User user2 = service.newUser();
        user2.setName("foo");
        user2.setEmail(user1.getEmail());
        service.create(user2, "pw");
    }

    @Test
    public void testGet() throws Exception {
        testCreateWithPassword();
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
    public void testSetPassword() throws Exception {
        testCreateWithPassword();
        User login1 = service.login("test", "password");
        assertNotNull(login1);
        service.setPassword(login1, "newPassword");
        User login2 = service.login("test", "newPassword");
        assertNotNull(login2);
    }

    @Test(expected = EntityNotFoundException.class)
    public void testUserNotFoundThrowsEntityNotFoundException() throws Exception {
        User user = new User();
        user.setId(Datastore.allocateId(User.class));
        service.setPassword(user, "Password");
    }

    @Test
    public void testFindUserByName() throws Exception {
        testCreateWithPassword();
        User user = createdUsers.get(0);
        User krico = service.findByName(user.getName().toUpperCase());
        assertNotNull("Upper case", krico);
        assertEquals(user, krico);
        krico = service.findByName(user.getName().toLowerCase());
        assertNotNull(krico);
        assertEquals(user, krico);

        assertNull(service.findByName("sasquatch"));
        assertNull(service.findByName(null));
        assertNull(service.findByName(""));
    }

    @Test
    public void testFindUserByEmail() throws Exception {
        User user1 = service.newUser();
        user1.setName("test");
        user1.setEmail("mE@Foo.baR");
        service.create(user1, "password");
        User fetched = service.findByEmail("mE@Foo.baR".toUpperCase());
        assertNotNull("Upper case", fetched);
        assertEquals(user1, fetched);

        assertNull(service.findByEmail("sasquatch"));
        assertNull(service.findByEmail(null));
        assertNull(service.findByEmail(""));
    }

    @Test(expected = EntityNotFoundException.class)
    public void registerPasswordRecoveryThrowsNotFound() throws Exception {
        service.registerPasswordRecovery("mE@Foo.baR");
    }

    @Test
    public void registerPasswordRecovery() throws Exception {
        User user1 = service.newUser();
        user1.setName("test");
        user1.setEmail("mE@Foo.baR");
        service.create(user1, "password");
        PasswordRecovery passwordRecovery = service.registerPasswordRecovery("mE@Foo.baR".toUpperCase());
        assertNotNull(passwordRecovery);
        assertNotNull(passwordRecovery.getCode());
        String code = passwordRecovery.getCode().getName();
        assertNotNull(code);
        assertTrue(code.length() > 3);
    }

    @Test
    public void recoverPassword() throws Exception {
        User user1 = service.newUser();
        user1.setName("test");
        user1.setEmail("mE@Foo.baR");
        service.create(user1, "password");
        PasswordRecovery passwordRecovery = service.registerPasswordRecovery("mE@Foo.baR".toUpperCase());
        service.recoverPassword(passwordRecovery.getCode().getName(), "newPassword");
        service.login(user1.getName(), "newPassword");
    }

    @Test(expected=EntityNotFoundException.class)
    public void recoverPasswordTwiceFails() throws Exception {
        User user1 = service.newUser();
        user1.setName("test");
        user1.setEmail("mE@Foo.baR");
        service.create(user1, "password");
        PasswordRecovery passwordRecovery = service.registerPasswordRecovery("mE@Foo.baR".toUpperCase());
        service.recoverPassword(passwordRecovery.getCode().getName(), "newPassword");
        service.recoverPassword(passwordRecovery.getCode().getName(), "newerPassword");
    }

    @Test
    public void testFindUserByUserLogin() throws Exception {
        testCreateWithUserLogin();
        User user = createdUsers.get(0);
        UserLogin userLogin = newGoogleLogin();
        User krico = service.findByLogin(userLogin.getProvider(), userLogin.getUserId());
        assertNotNull(krico);
        assertEquals(user, krico);

        assertNull(service.findByLogin(userLogin.getProvider() + "x", userLogin.getUserId()));
        assertNull(service.findByLogin(userLogin.getProvider(), userLogin.getUserId() + "x"));
        assertNull(service.findByLogin(userLogin.getProvider(), null));
        assertNull(service.findByLogin(null, userLogin.getUserId()));

    }

    @Test
    public void testSaveUser() throws Exception {
        testCreateWithPassword();
        User user = service.get(createdUsers.get(0).getId().getId());
        String expectedAbout = "About me";
        user.setAbout(expectedAbout);
        String expectedEmail = "test@test.com";
        user.setEmail("teSt@tesT.com");
        user.setRealName("Real Name");
        user.setAdmin(true);
        service.save(user);
        User updated = service.get(user.getId().getId());
        assertNotNull(updated);
        assertEquals(expectedAbout, updated.getAbout());
        assertEquals(expectedEmail, updated.getEmail());
        assertEquals("Real Name", updated.getRealName());
        assertTrue(updated.isAdmin());
    }

    @Test(expected = EntityNotFoundException.class)
    public void testSaveEntityNotFound() throws Exception {
        service.save(service.newUser());
    }

    @Test(expected = FieldValueException.class)
    public void testSaveFieldValueExceptionWithChangeName() throws Exception {
        User user = service.newUser();
        user.setName("TesT");
        service.create(user, "password");
        user.setName("TesT1");
        service.save(user);
    }

    @Test
    public void testUserLogin() throws Exception {
        User user = service.newUser();
        user.setName("TesT");
        service.create(user, "password");
        User login1 = service.login(user.getName(), "password");
        assertNotNull(login1);
        assertEquals(user.getName(), login1.getName());
        User login2 = service.login(user.getName().toUpperCase(), "password");
        assertNotNull(login2);
        assertEquals(user.getName(), login2.getName());
        User login3 = service.login(user.getName().toLowerCase(), "password");
        assertNotNull(login3);
        assertEquals(user.getName(), login3.getName());

    }

    @Test(expected = LoginFailedException.class)
    public void testUserLoginWrongPasswordFails() throws Exception {
        User user = service.newUser();
        user.setName("TesT");
        service.create(user, "password");
        service.login(user.getName(), "passwordX");
    }

    @Test(expected = LoginFailedException.class)
    public void testUserWitLoginFailsToLoginWithPassword() throws Exception {
        User user = service.newUser();
        user.setName("TesT");
        service.create(user, new UserLogin("Google", "12345"));
        service.login(user.getName(), "password");
    }

    @Test(expected = NullPointerException.class)
    public void testUserWitLoginFailsToLoginWithNullPassword() throws Exception {
        User user = service.newUser();
        user.setName("TesT");
        service.create(user, new UserLogin("Google", "12345"));
        service.login(user.getName(), null);
    }

    @Test(expected = LoginFailedException.class)
    public void testUserLoginNonExistentFails() throws Exception {
        User user = service.newUser();
        user.setName("TesT");
        service.create(user, "password");
        service.login(user.getName() + "x", "password");
    }

    @Test(expected = LoginFailedException.class)
    public void testUserLoginEmptyNameFails() throws Exception {
        service.login("", "password");
    }

    @Test(expected = LoginFailedException.class)
    public void testUserLoginEmptyPasswordFails() throws Exception {
        service.login("", "");
    }

    @Test(expected = NullPointerException.class)
    public void testUserLoginNullFails() throws Exception {
        service.login(null, null);
    }

    @Test
    public void testList() throws Exception {
        int total = 200;
        int offset = 20;

        TestHelper.createUsers(total);

        List<User> allAsc = service.list(Query.SortDirection.ASCENDING, 0, -1);
        assertNotNull("null response", allAsc);
        assertEquals("I created " + total, total, allAsc.size());
        for (int i = 0; i < total; i++) {
            assertEquals(String.format("user%03d", i), allAsc.get(i).getName());
        }

        List<User> allAscLim = service.list(Query.SortDirection.ASCENDING, offset, -1);
        assertEquals(total - offset, allAscLim.size());
        for (int i = offset; i < total; i++) {
            assertEquals(String.format("user%03d", i), allAscLim.get(i - offset).getName());
        }

        List<User> allDesc = service.list(Query.SortDirection.DESCENDING, 0, -1);
        assertNotNull("null response", allDesc);
        assertEquals("I created " + total, total, allDesc.size());
        for (int i = 0; i < total; i++) {
            assertEquals(String.format("user%03d", total - (i + 1)), allDesc.get(i).getName());
        }

        int limit = 20;
        List<User> descLimOff = service.list(Query.SortDirection.DESCENDING, offset, limit);
        assertEquals(limit, descLimOff.size());
        for (int i = 0; i < limit; i++) {
            assertEquals(String.format("user%03d", total - 1 - offset - i), descLimOff.get(i).getName());
        }

    }

    @Test
    public void testSearchByNamePattern() throws Exception {
        int total = 200;

        TestHelper.createUsers(total);

        String name = String.format("user%03d", total - 1);
        List<User> directHit = service.searchByName(Pattern.compile(name), Query.SortDirection.ASCENDING, 0, 0);
        assertNotNull(directHit);
        assertEquals(1, directHit.size());
        assertEquals(name, directHit.get(0).getName());


        List<User> oneHundred = service.searchByName(Pattern.compile("user0[0-9]{2}"), Query.SortDirection.ASCENDING, 0, 0);
        assertEquals(100, oneHundred.size());
        for (int i = 0; i < 100; ++i) {
            assertEquals(String.format("user%03d", i), oneHundred.get(i).getName());
        }

        List<User> oneHundredDesc = service.searchByName(Pattern.compile("user0[0-9]{2}"), Query.SortDirection.DESCENDING, 0, 0);
        assertEquals(100, oneHundredDesc.size());
        for (int i = 0; i < 100; ++i) {
            assertEquals(String.format("user%03d", 100 - i - 1), oneHundredDesc.get(i).getName());
        }
        List<User> oneHundredDescLimOff = service.searchByName(Pattern.compile("user0[0-9]{2}"), Query.SortDirection.DESCENDING, 20, 20);
        assertEquals(20, oneHundredDescLimOff.size());
        for (int i = 0; i < 20; ++i) {
            assertEquals(String.format("user%03d", 80 - i - 1), oneHundredDescLimOff.get(i).getName());
        }
        oneHundredDescLimOff = service.searchByName(Pattern.compile(""), Query.SortDirection.DESCENDING, 20, 20);
        assertEquals(20, oneHundredDescLimOff.size());
        for (int i = 0; i < 20; ++i) {
            assertEquals(String.format("user%03d", total - 20 - i - 1), oneHundredDescLimOff.get(i).getName());
        }

        assertTrue(service.searchByName((Pattern) null, Query.SortDirection.DESCENDING, total, 1).isEmpty());
        assertTrue(service.searchByName(Pattern.compile("u"), Query.SortDirection.DESCENDING, total, 1).isEmpty());
    }

    @Test
    public void testSearchByNamePrefix() throws Exception {
        int total = 200;

        TestHelper.createUsers(total);

        String name = String.format("user%03d", total - 1);
        List<User> directHit = service.searchByName(name, Query.SortDirection.ASCENDING, 0, 0);
        assertNotNull(directHit);
        assertEquals(1, directHit.size());
        assertEquals(name, directHit.get(0).getName());


        List<User> ten = service.searchByName("user00", Query.SortDirection.ASCENDING, 0, 0);
        assertEquals(10, ten.size());
        for (int i = 0; i < 10; ++i) {
            assertEquals(String.format("user%03d", i), ten.get(i).getName());
        }

        List<User> oneHundredDesc = service.searchByName("user0", Query.SortDirection.DESCENDING, 0, 0);
        assertEquals(100, oneHundredDesc.size());
        for (int i = 0; i < 100; ++i) {
            assertEquals(String.format("user%03d", 100 - i - 1), oneHundredDesc.get(i).getName());
        }
        List<User> oneHundredDescLimOff = service.searchByName("user0", Query.SortDirection.DESCENDING, 20, 20);
        assertEquals(20, oneHundredDescLimOff.size());
        for (int i = 0; i < 20; ++i) {
            assertEquals(String.format("user%03d", 80 - i - 1), oneHundredDescLimOff.get(i).getName());
        }
        oneHundredDescLimOff = service.searchByName("", Query.SortDirection.DESCENDING, 20, 20);
        assertEquals(20, oneHundredDescLimOff.size());
        for (int i = 0; i < 20; ++i) {
            assertEquals(String.format("user%03d", total - 20 - i - 1), oneHundredDescLimOff.get(i).getName());
        }

        assertTrue(service.searchByName((String) null, Query.SortDirection.DESCENDING, total, 1).isEmpty());
        assertTrue(service.searchByName("u", Query.SortDirection.DESCENDING, total, 1).isEmpty());
    }

    @Test
    public void testSearchByEmailPattern() throws Exception {
        int total = 200;

        TestHelper.createUsers(total);

        String name = String.format("user%03d", total - 1);
        String email = name + '@';
        List<User> directHit = service.searchByEmail(Pattern.compile(email), Query.SortDirection.ASCENDING, 0, 0);
        assertNotNull(directHit);
        assertEquals(1, directHit.size());
        assertEquals(name, directHit.get(0).getName());


        List<User> oneHundred = service.searchByEmail(Pattern.compile("user0[0-9]{2}@"), Query.SortDirection.ASCENDING, 0, 0);
        assertEquals(100, oneHundred.size());
        for (int i = 0; i < 100; ++i) {
            assertEquals(String.format("user%03d", i), oneHundred.get(i).getName());
        }

        List<User> oneHundredDesc = service.searchByEmail(Pattern.compile("user0[0-9]{2}@"), Query.SortDirection.DESCENDING, 0, 0);
        assertEquals(100, oneHundredDesc.size());
        for (int i = 0; i < 100; ++i) {
            assertEquals(String.format("user%03d", 100 - i - 1), oneHundredDesc.get(i).getName());
        }
        List<User> oneHundredDescLimOff = service.searchByEmail(Pattern.compile("user0[0-9]{2}@"), Query.SortDirection.DESCENDING, 20, 20);
        assertEquals(20, oneHundredDescLimOff.size());
        for (int i = 0; i < 20; ++i) {
            assertEquals(String.format("user%03d", 80 - i - 1), oneHundredDescLimOff.get(i).getName());
        }
        oneHundredDescLimOff = service.searchByEmail(Pattern.compile(""), Query.SortDirection.DESCENDING, 20, 20);
        assertEquals(20, oneHundredDescLimOff.size());
        for (int i = 0; i < 20; ++i) {
            assertEquals(String.format("user%03d", total - 20 - i - 1), oneHundredDescLimOff.get(i).getName());
        }
        assertTrue(service.searchByEmail((Pattern) null, Query.SortDirection.DESCENDING, total, 1).isEmpty());
        assertTrue(service.searchByEmail(Pattern.compile("u"), Query.SortDirection.DESCENDING, total, 1).isEmpty());

    }

    @Test
    public void testSearchByEmailPrefix() throws Exception {
        int total = 200;

        TestHelper.createUsers(total);

        String name = String.format("user%03d", total - 1);
        String email = name + '@';
        List<User> directHit = service.searchByEmail(email, Query.SortDirection.ASCENDING, 0, 0);
        assertNotNull(directHit);
        assertEquals(1, directHit.size());
        assertEquals(name, directHit.get(0).getName());


        List<User> ten = service.searchByEmail("user00", Query.SortDirection.ASCENDING, 0, 0);
        assertEquals(10, ten.size());
        for (int i = 0; i < 10; ++i) {
            assertEquals(String.format("user%03d", i), ten.get(i).getName());
        }

        List<User> oneHundredDesc = service.searchByEmail("user0", Query.SortDirection.DESCENDING, 0, 0);
        assertEquals(100, oneHundredDesc.size());
        for (int i = 0; i < 100; ++i) {
            assertEquals(String.format("user%03d", 100 - i - 1), oneHundredDesc.get(i).getName());
        }
        List<User> oneHundredDescLimOff = service.searchByEmail("user0", Query.SortDirection.DESCENDING, 20, 20);
        assertEquals(20, oneHundredDescLimOff.size());
        for (int i = 0; i < 20; ++i) {
            assertEquals(String.format("user%03d", 80 - i - 1), oneHundredDescLimOff.get(i).getName());
        }
        oneHundredDescLimOff = service.searchByEmail("", Query.SortDirection.DESCENDING, 20, 20);
        assertEquals(20, oneHundredDescLimOff.size());
        for (int i = 0; i < 20; ++i) {
            assertEquals(String.format("user%03d", total - 20 - i - 1), oneHundredDescLimOff.get(i).getName());
        }
        assertTrue(service.searchByEmail((String) null, Query.SortDirection.DESCENDING, total, 1).isEmpty());
        assertTrue(service.searchByEmail("u", Query.SortDirection.DESCENDING, total, 1).isEmpty());

    }

    @Test
    public void testUsernameExists() throws Exception {
        User user = service.newUser();
        user.setName("aBcd");

        assertFalse(service.usernameExists("abcd"));
        service.create(user, "password");
        assertTrue(service.usernameExists("abcd"));
        assertTrue(service.usernameExists("aBCd"));
        assertFalse(service.usernameExists("abcde"));
    }

    @Test
    public void testEmailExists() throws Exception {
        User user = service.newUser();
        user.setName("abc");
        user.setEmail("aBc@dEf");

        assertFalse(service.emailExists("aBc@dEf"));
        service.create(user, "password");
        assertTrue(service.emailExists("aBc@dEf"));
        assertTrue(service.emailExists("abc@def"));
        assertFalse(service.emailExists("abc@defg"));
    }
}