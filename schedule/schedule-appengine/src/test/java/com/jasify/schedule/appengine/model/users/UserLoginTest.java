package com.jasify.schedule.appengine.model.users;

import com.google.appengine.api.datastore.Key;
import com.jasify.schedule.appengine.TestHelper;
import com.jasify.schedule.appengine.oauth2.OAuth2Info;
import com.jasify.schedule.appengine.oauth2.OAuth2ProviderEnum;
import com.jasify.schedule.appengine.util.TypeUtil;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slim3.datastore.Datastore;

import java.util.HashSet;

import static junit.framework.TestCase.*;

public class UserLoginTest {
    private static final Logger log = LoggerFactory.getLogger(UserLoginTest.class);

    @Before
    public void initializeDatastore() {
        TestHelper.initializeDatastore();
    }

    @After
    public void cleanupDatastore() {
        TestHelper.cleanupDatastore();
    }

    @Test
    public void testIdIsUniqueWithDifferentAncestors() {
        User user1 = new User();
        Datastore.put(user1);
        log.info("{}", user1);
        User user2 = new User();
        Datastore.put(user2);
        log.info("{}", user2);

        Key loginKey1 = Datastore.createKey(user1.getId(), UserLogin.class, 1);
        log.info("{}", loginKey1);
        Key loginKey2 = Datastore.createKey(user2.getId(), UserLogin.class, 1);
        log.info("{}", loginKey2);

        UserLogin login1 = new UserLogin();
        login1.setId(loginKey1);
        Datastore.put(login1);
        log.info("{}", login1);

        UserLogin login2 = new UserLogin();
        login2.setId(loginKey2);
        Datastore.put(login2);
        log.info("{}", login2);
    }


    @Test
    public void testHashCode() throws Exception {
        HashSet<UserLogin> loginSet = new HashSet<>();
        UserLogin login1 = new UserLogin();
        assertTrue(loginSet.add(login1));
        assertFalse(loginSet.add(new UserLogin()));
        UserLogin login2 = new UserLogin();
        Key id = Datastore.allocateId(UserLogin.class);
        login2.setId(id);
        assertTrue(loginSet.add(login2));
        UserLogin e = new UserLogin();
        e.setId(id);
        assertFalse(loginSet.add(e));
    }

    @Test
    public void testCompare() {
        UserLogin u1 = new UserLogin("a", "1");
        UserLogin u2 = new UserLogin("a", "2");
        UserLogin u3 = new UserLogin("b", "1");

        assertEquals(0, u1.compareTo(u1));
        assertEquals(0, u2.compareTo(u2));
        assertEquals(0, u3.compareTo(u3));
        assertEquals(-1, u1.compareTo(u3));
        assertEquals(1, u3.compareTo(u1));
        assertEquals(-1, u1.compareTo(u2));
        assertEquals(1, u2.compareTo(u1));
        assertEquals(-1, u2.compareTo(u3));
        assertEquals(1, u3.compareTo(u2));
    }

    @Test
    public void testEmptySave() {
        UserLogin ul = new UserLogin();
        Datastore.put(ul);
    }

    @Test(expected = NullPointerException.class)
    public void testOwnerSaveWithNoIdThrows() {
        new UserLogin(new User());
    }

    @Test
    public void testOwnerSaveWithId() {
        User user = new User();
        user.setId(Datastore.allocateId(User.class));
        UserLogin userLogin = new UserLogin(user);
        assertNotNull(userLogin.getUserRef().getModel());
    }

    @Test
    public void testCreateWithId() {
        Key key = Datastore.allocateId(UserLogin.class);
        UserLogin userLogin = new UserLogin(key);
        assertEquals(key, userLogin.getId());
    }

    @Test
    public void testCreateFromOAuth2Info() {
        OAuth2Info info = new OAuth2Info(OAuth2ProviderEnum.Google, "TEST");
        info.setUserId(RandomStringUtils.randomAlphabetic(8));
        info.setAvatar(RandomStringUtils.randomAlphabetic(20));
        info.setProfile(RandomStringUtils.randomAlphabetic(20));
        info.setEmail(RandomStringUtils.randomAlphabetic(6));
        info.setRealName(RandomStringUtils.randomAlphabetic(18));

        UserLogin userLogin = new UserLogin(info);
        assertEquals(info.getProvider().name(), userLogin.getProvider());
        assertEquals(info.getUserId(), userLogin.getUserId());
        assertEquals(info.getAvatar(), TypeUtil.toString(userLogin.getAvatar()));
        assertEquals(info.getProfile(), TypeUtil.toString(userLogin.getProfile()));
        assertEquals(info.getEmail(), userLogin.getEmail());
        assertEquals(info.getRealName(), userLogin.getRealName());
    }
}
