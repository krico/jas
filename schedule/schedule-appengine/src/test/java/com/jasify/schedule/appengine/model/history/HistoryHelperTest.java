package com.jasify.schedule.appengine.model.history;

import com.google.appengine.api.datastore.Key;
import com.jasify.schedule.appengine.TestHelper;
import com.jasify.schedule.appengine.dao.history.HistoryDao;
import com.jasify.schedule.appengine.dao.users.UserDao;
import com.jasify.schedule.appengine.http.HttpUserSession;
import com.jasify.schedule.appengine.model.UserContext;
import com.jasify.schedule.appengine.model.users.PasswordRecovery;
import com.jasify.schedule.appengine.model.users.User;
import com.jasify.schedule.appengine.model.users.UserLogin;
import com.jasify.schedule.appengine.util.KeyUtil;
import org.easymock.EasyMock;
import org.easymock.EasyMockRunner;
import org.easymock.Mock;
import org.easymock.MockType;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slim3.datastore.Datastore;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

import static junit.framework.TestCase.*;

@RunWith(EasyMockRunner.class)
public class HistoryHelperTest {
    public static final String TEST_USER_NAME = "torvalds";
    public static final String TEST_REMOTE_ADDR = "123.456.789.10";
    public static final String TEST_REFERRER = "http://google.com";
    private HistoryDao historyDao;

    @Mock(type = MockType.NICE)
    private HttpServletRequest httpServletRequest;

    private User user = new User();
    private Key userId;

    @Before
    public void setup() {
        TestHelper.initializeDatastore();
        historyDao = new HistoryDao();
        EasyMock.expect(httpServletRequest.getRemoteAddr()).andReturn(TEST_REMOTE_ADDR).anyTimes();
        EasyMock.expect(httpServletRequest.getHeader("referer")).andReturn(TEST_REFERRER).anyTimes();
        EasyMock.replay(httpServletRequest);

        userId = Datastore.allocateId(User.class);
        user.setId(userId);
        user.setName(TEST_USER_NAME);
    }

    @After
    public void cleanup() {
        UserContext.clearContext();
        TestHelper.cleanupDatastore();
    }

    @Test
    public void testWellDefined() throws Exception {
        TestHelper.assertUtilityClassWellDefined(HistoryHelper.class);
    }

    private History getLastHistory() {
        List<History> histories = historyDao.listSince(new Date(System.currentTimeMillis() - 5000));
        assertFalse(histories.isEmpty());
        return histories.get(histories.size() - 1);
    }

    @Test
    public void testAddMessage() throws Exception {
        String message = "Test event";
        HistoryHelper.addMessage(message);


        History history = getLastHistory();
        assertEquals(message, history.getDescription());
        assertEquals(HistoryTypeEnum.Message, history.getType());
    }

    @Test
    public void testAddMessageCurrentUserIsSet() throws Exception {
        UserContext.setCurrentUser(new HttpUserSession(user, false));

        HistoryHelper.addMessage("Test event");

        assertEquals(userId, getLastHistory().getCurrentUserRef().getKey());
    }

    @Test
    public void testAddLogin() throws Exception {

        HistoryHelper.addLogin(user, httpServletRequest);

        EasyMock.verify(httpServletRequest);

        History history = getLastHistory();
        assertEquals(HistoryTypeEnum.Login, history.getType());
        assertEquals(userId, history.getCurrentUserRef().getKey());
        assertTrue(history instanceof AuthHistory);
        AuthHistory ah = (AuthHistory) history;
        assertEquals(TEST_REMOTE_ADDR, ah.getRemoteAddress());
        assertEquals(user.getName(), ah.getName());
        String message = ah.getDescription();
        assertNotNull(message);
        assertTrue(message.contains(user.getName()));
        assertTrue(message.contains(TEST_REMOTE_ADDR));
    }

    @Test
    public void testAddLogout() throws Exception {
        UserContext.setCurrentUser(new HttpUserSession(user, false));
        HistoryHelper.addLogout(httpServletRequest);

        EasyMock.verify(httpServletRequest);

        History history = getLastHistory();
        assertEquals(HistoryTypeEnum.Logout, history.getType());
        assertEquals(userId, history.getCurrentUserRef().getKey());
        assertTrue(history instanceof AuthHistory);
        AuthHistory ah = (AuthHistory) history;
        assertEquals(TEST_REMOTE_ADDR, ah.getRemoteAddress());
        String message = ah.getDescription();
        assertNotNull(message);
        assertTrue(message.contains(KeyUtil.toHumanReadableString(userId)));
        assertTrue(message.contains(TEST_REMOTE_ADDR));
    }

    @Test
    public void testAddLoginFailed() throws Exception {

        HistoryHelper.addLoginFailed(TEST_USER_NAME, httpServletRequest);

        EasyMock.verify(httpServletRequest);

        History history = getLastHistory();
        assertTrue(history instanceof AuthHistory);
        AuthHistory ah = (AuthHistory) history;
        assertEquals(HistoryTypeEnum.LoginFailed, ah.getType());
        assertEquals(TEST_USER_NAME, ah.getName());
        assertEquals(TEST_REMOTE_ADDR, ah.getRemoteAddress());
        assertNull(ah.getCurrentUserRef().getKey());
        assertTrue(ah.getDescription().contains(TEST_USER_NAME));
        assertTrue(ah.getDescription().contains(TEST_REMOTE_ADDR));
    }

    @Test
    public void testAddPasswordChangedByUser() throws Exception {
        UserContext.setCurrentUser(new HttpUserSession(user, false));
        HistoryHelper.addPasswordChanged(user, httpServletRequest);

        EasyMock.verify(httpServletRequest);

        History history = getLastHistory();
        assertTrue(history instanceof AuthHistory);
        AuthHistory ah = (AuthHistory) history;
        assertEquals(HistoryTypeEnum.PasswordChanged, ah.getType());
        assertEquals(user.getName(), ah.getName());
        assertEquals(TEST_REMOTE_ADDR, ah.getRemoteAddress());
        assertEquals(user.getId(), ah.getCurrentUserRef().getKey());
        assertTrue(ah.getDescription().contains(user.getName()));
        assertTrue(ah.getDescription().contains(TEST_REMOTE_ADDR));
        assertTrue(ah.getDescription().contains("changed his password"));
    }

    @Test
    public void testAddPasswordChangedByAnotherUser() throws Exception {
        User anotherUser = new User();
        anotherUser.setId(Datastore.allocateId(User.class));
        anotherUser.setName("another");
        UserContext.setCurrentUser(new HttpUserSession(anotherUser, false));
        HistoryHelper.addPasswordChanged(user, httpServletRequest);

        EasyMock.verify(httpServletRequest);

        History history = getLastHistory();
        assertTrue(history instanceof AuthHistory);
        AuthHistory ah = (AuthHistory) history;
        assertEquals(HistoryTypeEnum.PasswordChanged, ah.getType());
        assertEquals(user.getName(), ah.getName());
        assertEquals(TEST_REMOTE_ADDR, ah.getRemoteAddress());
        assertEquals(anotherUser.getId(), ah.getCurrentUserRef().getKey());
        assertTrue(ah.getDescription().contains(user.getName()));
        assertTrue(ah.getDescription().contains(KeyUtil.toHumanReadableString(anotherUser.getId())));
        assertTrue(ah.getDescription().contains(TEST_REMOTE_ADDR));
        assertTrue(ah.getDescription().contains("changed by:"));
    }

    @Test
    public void testAddLoginOAuth() throws Exception {
        UserContext.setCurrentUser(new HttpUserSession(user, false));
        new UserDao().save(user);

        String comment = "a comment";

        EasyMock.verify(httpServletRequest);

        UserLogin userLogin = new UserLogin("Google", "123456789");
        userLogin.setEmail("who@you.com");
        userLogin.getUserRef().setModel(user);
        HistoryHelper.addLogin(userLogin, httpServletRequest, comment);

        EasyMock.verify(httpServletRequest);

        History history = getLastHistory();
        assertTrue(history instanceof AuthHistory);
        AuthHistory ah = (AuthHistory) history;
        assertEquals(HistoryTypeEnum.Login, ah.getType());
        assertEquals(user.getName(), ah.getName());
        assertEquals(TEST_REMOTE_ADDR, ah.getRemoteAddress());
        assertEquals(user.getId(), ah.getCurrentUserRef().getKey());
        assertTrue(ah.getDescription().contains(user.getName()));
        assertTrue(ah.getDescription().contains(TEST_REMOTE_ADDR));
        assertTrue(ah.getDescription().contains(userLogin.getProvider()));
        assertTrue(ah.getDescription().contains(userLogin.getUserId()));
        assertTrue(ah.getDescription().contains(userLogin.getEmail()));
    }

    @Test
    public void testAddLoginOAuthBadUser() throws Exception {
        UserContext.setCurrentUser(new HttpUserSession(user, false));

        String comment = "a comment";

        EasyMock.verify(httpServletRequest);

        UserLogin userLogin = new UserLogin("Google", "123456789");
        userLogin.setEmail("who@you.com");
        userLogin.getUserRef().setModel(user);
        HistoryHelper.addLogin(userLogin, httpServletRequest, comment);

        EasyMock.verify(httpServletRequest);

        History history = getLastHistory();
        assertTrue(history instanceof AuthHistory);
        AuthHistory ah = (AuthHistory) history;
        assertEquals(HistoryTypeEnum.Login, ah.getType());
        assertNull(ah.getName());
        assertEquals(TEST_REMOTE_ADDR, ah.getRemoteAddress());
        assertEquals(user.getId(), ah.getCurrentUserRef().getKey());
        assertTrue(ah.getDescription().contains(TEST_REMOTE_ADDR));
        assertTrue(ah.getDescription().contains(userLogin.getProvider()));
        assertTrue(ah.getDescription().contains(userLogin.getUserId()));
        assertTrue(ah.getDescription().contains(userLogin.getEmail()));
    }

    @Test
    public void testAddLoginFailedOAuth() throws Exception {
        UserContext.setCurrentUser(new HttpUserSession(user, false));

        String reason = "a reason";

        EasyMock.verify(httpServletRequest);

        UserLogin userLogin = new UserLogin("Google", "123456789");
        userLogin.setEmail("who@you.com");
        userLogin.getUserRef().setModel(user);
        HistoryHelper.addLoginFailed(userLogin, httpServletRequest, reason);

        EasyMock.verify(httpServletRequest);

        History history = getLastHistory();
        assertTrue(history instanceof AuthHistory);
        AuthHistory ah = (AuthHistory) history;
        assertEquals(HistoryTypeEnum.LoginFailed, ah.getType());
        assertNull(ah.getName());
        assertEquals(TEST_REMOTE_ADDR, ah.getRemoteAddress());
        assertEquals(user.getId(), ah.getCurrentUserRef().getKey());
        assertTrue(ah.getDescription().contains(TEST_REMOTE_ADDR));
        assertTrue(ah.getDescription().contains(userLogin.getProvider()));
        assertTrue(ah.getDescription().contains(userLogin.getUserId()));
        assertTrue(ah.getDescription().contains(userLogin.getEmail()));
    }

    @Test
    public void testAddAccountCreatedOAuth() throws Exception {
        EasyMock.verify(httpServletRequest);

        UserLogin userLogin = new UserLogin("Google", "123456789");
        userLogin.setEmail("who@you.com");
        userLogin.getUserRef().setModel(user);

        HistoryHelper.addAccountCreated(user, userLogin, httpServletRequest);

        EasyMock.verify(httpServletRequest);

        History history = getLastHistory();
        assertTrue(history instanceof AccountCreationHistory);
        AccountCreationHistory ah = (AccountCreationHistory) history;
        assertEquals(HistoryTypeEnum.AccountCreated, ah.getType());
        assertEquals(user.getName(), ah.getName());
        assertEquals(TEST_REMOTE_ADDR, ah.getRemoteAddress());
        assertEquals(TEST_REFERRER, ah.getReferrer());
        assertEquals(user.getId(), ah.getCurrentUserRef().getKey());
        assertTrue(ah.getDescription().contains(TEST_REMOTE_ADDR));
        assertTrue(ah.getDescription().contains(userLogin.getProvider()));
        assertTrue(ah.getDescription().contains(userLogin.getUserId()));
        assertTrue(ah.getDescription().contains(userLogin.getEmail()));
    }

    @Test
    public void testAddAccountCreationFailedOAuth() throws Exception {

        UserLogin userLogin = new UserLogin("Google", "123456789");
        userLogin.setEmail("who@you.com");
        userLogin.getUserRef().setModel(user);

        String reason = "a reason";
        HistoryHelper.addAccountCreationFailed(userLogin, httpServletRequest, reason);

        EasyMock.verify(httpServletRequest);

        History history = getLastHistory();
        assertTrue(history instanceof AccountCreationHistory);
        AccountCreationHistory ah = (AccountCreationHistory) history;
        assertEquals(HistoryTypeEnum.AccountCreationFailed, ah.getType());
        assertNull(ah.getName());
        assertEquals(TEST_REMOTE_ADDR, ah.getRemoteAddress());
        assertEquals(TEST_REFERRER, ah.getReferrer());
        assertNull(ah.getCurrentUserRef().getKey());
        assertTrue(ah.getDescription().contains(TEST_REMOTE_ADDR));
        assertTrue(ah.getDescription().contains(userLogin.getProvider()));
        assertTrue(ah.getDescription().contains(userLogin.getUserId()));
        assertTrue(ah.getDescription().contains(userLogin.getEmail()));
        assertTrue(ah.getDescription().contains(reason));
    }

    @Test
    public void testAddForgottenPassword() throws Exception {
        new UserDao().save(user);

        PasswordRecovery recovery = new PasswordRecovery();
        recovery.setCode(Datastore.createKey(PasswordRecovery.class, "ABC"));
        recovery.getUserRef().setModel(user);
        HistoryHelper.addForgottenPassword(recovery, httpServletRequest);
        EasyMock.verify(httpServletRequest);
        History history = getLastHistory();
        assertTrue(history instanceof AuthHistory);
        AuthHistory ah = (AuthHistory) history;
        assertEquals(HistoryTypeEnum.PasswordForgotten, ah.getType());
        assertEquals(TEST_REMOTE_ADDR, ah.getRemoteAddress());
        assertEquals(user.getId(), ah.getCurrentUserRef().getKey());
        assertEquals(user.getName(), ah.getName());
        assertNotNull(ah.getDescription());
        assertFalse("Code in description would be security leak", ah.getDescription().contains(recovery.getCode().getName()));
        assertTrue(ah.getDescription().contains(TEST_REMOTE_ADDR));
        assertTrue(ah.getDescription().contains(user.getName()));
    }

    @Test
    public void testAddPasswordRecovered() throws Exception {
        new UserDao().save(user);

        PasswordRecovery recovery = new PasswordRecovery();
        recovery.setCode(Datastore.createKey(PasswordRecovery.class, "ABC"));
        recovery.getUserRef().setKey(user.getId());
        HistoryHelper.addRecoveredPassword(recovery, httpServletRequest);
        EasyMock.verify(httpServletRequest);
        History history = getLastHistory();
        assertTrue(history instanceof AuthHistory);
        AuthHistory ah = (AuthHistory) history;
        assertEquals(HistoryTypeEnum.PasswordRecovered, ah.getType());
        assertEquals(TEST_REMOTE_ADDR, ah.getRemoteAddress());
        assertEquals(user.getId(), ah.getCurrentUserRef().getKey());
        assertEquals(user.getName(), ah.getName());
        assertNotNull(ah.getDescription());
        assertTrue(ah.getDescription().contains(TEST_REMOTE_ADDR));
        assertTrue(ah.getDescription().contains(user.getName()));
    }

    @Test
    public void testAddForgottenPasswordNoUser() throws Exception {
        PasswordRecovery recovery = new PasswordRecovery();
        recovery.setCode(Datastore.createKey(PasswordRecovery.class, "ABC"));
        recovery.getUserRef().setModel(user);
        HistoryHelper.addForgottenPassword(recovery, httpServletRequest);
        EasyMock.verify(httpServletRequest);
        History history = getLastHistory();
        assertTrue(history instanceof AuthHistory);
        AuthHistory ah = (AuthHistory) history;
        assertEquals(HistoryTypeEnum.PasswordForgotten, ah.getType());
        assertEquals(TEST_REMOTE_ADDR, ah.getRemoteAddress());
        assertEquals(user.getId(), ah.getCurrentUserRef().getKey());
        assertNull(ah.getName());
        assertNotNull(ah.getDescription());
        assertFalse("Code in description would be security leak", ah.getDescription().contains(recovery.getCode().getName()));
        assertTrue(ah.getDescription().contains(TEST_REMOTE_ADDR));
    }

    @Test
    public void testAddForgottenPasswordFailed() throws Exception {
        String email = "foo@bar.com";
        HistoryHelper.addForgottenPasswordFailed(email, httpServletRequest);
        EasyMock.verify(httpServletRequest);
        History history = getLastHistory();
        assertTrue(history instanceof AuthHistory);
        AuthHistory ah = (AuthHistory) history;
        assertEquals(HistoryTypeEnum.PasswordForgottenFailed, ah.getType());
        assertEquals(TEST_REMOTE_ADDR, ah.getRemoteAddress());
        assertNull(ah.getCurrentUserRef().getKey());
        assertNull(ah.getName());
        assertNotNull(ah.getDescription());
        assertTrue(ah.getDescription().contains(TEST_REMOTE_ADDR));
        assertTrue(ah.getDescription().contains(email));
    }
}