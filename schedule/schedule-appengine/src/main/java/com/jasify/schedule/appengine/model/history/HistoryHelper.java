package com.jasify.schedule.appengine.model.history;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Transaction;
import com.jasify.schedule.appengine.dao.history.HistoryDao;
import com.jasify.schedule.appengine.dao.users.UserDao;
import com.jasify.schedule.appengine.model.*;
import com.jasify.schedule.appengine.model.users.User;
import com.jasify.schedule.appengine.model.users.UserLogin;
import com.jasify.schedule.appengine.util.KeyUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;

/**
 * @author krico
 * @since 09/08/15.
 */
public final class HistoryHelper {
    private static final Logger log = LoggerFactory.getLogger(HistoryHelper.class);

    private static final HistoryDao historyDao = new HistoryDao();

    private HistoryHelper() {
    }

    private static void addCurrentUser(History history) {
        UserSession currentUser = UserContext.getCurrentUser();
        if (currentUser != null) {
            Key userId = currentUser.getUserIdKey();
            if (userId != null) {
                history.getCurrentUserRef().setKey(userId);
            }
        }
    }

    private static void addHistory(final History history) {
        if (history.getCurrentUserRef().getKey() == null)
            addCurrentUser(history);

        //This could be done async
        TransactionOperator.executeNoEx(new ModelOperation<History>() {
            @Override
            public History execute(Transaction tx) throws ModelException {
                historyDao.save(history);
                tx.commit();
                return history;
            }
        });
    }

    private static void addUserLogin(UserLogin userLogin, AuthHistory history) {
        history.setProvider(userLogin.getProvider());
        history.setProviderUserId(userLogin.getUserId());
        history.setProviderUserEmail(userLogin.getEmail());
    }

    public static void addMessage(String message) {
        History history = new History(HistoryTypeEnum.Message);
        history.setDescription(message);

        addHistory(history);
    }

    public static void addLogin(UserLogin userLogin, HttpServletRequest httpServletRequest, String comment) {
        AuthHistory history = new AuthHistory(HistoryTypeEnum.Login);
        try {
            Key userId = userLogin.getUserRef().getKey();
            if (userId != null) {
                UserDao userDao = new UserDao();
                User user = userDao.get(userId);
                history.setName(user.getName());
            }
        } catch (Exception e) {
            log.warn("Failed to get User from userLogin", e);
        }
        addUserLogin(userLogin, history);
        history.setRemoteAddress(httpServletRequest.getRemoteAddr());

        String description = "User: " + history.getName() +
                " Credentials: " + history.toOAuthCredentialsString() +
                " logged in from: " + history.getRemoteAddress();

        if (StringUtils.isNotBlank(comment)) description += " comment: " + comment;

        history.setDescription(description);

        addHistory(history);
    }

    public static void addLogin(User user, HttpServletRequest httpServletRequest) {
        AuthHistory history = new AuthHistory(HistoryTypeEnum.Login);
        history.getCurrentUserRef().setKey(user.getId());
        history.setName(user.getName());
        history.setRemoteAddress(httpServletRequest.getRemoteAddr());
        history.setDescription("User: " + history.getName() + " logged in from: " + history.getRemoteAddress());

        addHistory(history);
    }

    public static void addLogout(HttpServletRequest httpServletRequest) {
        AuthHistory history = new AuthHistory(HistoryTypeEnum.Logout);
        history.setRemoteAddress(httpServletRequest.getRemoteAddr());
        addCurrentUser(history);
        Key userId = history.getCurrentUserRef().getKey();
        if (userId == null) {
            log.warn("addLogout called without currentUser");
            return;
        }
        history.setDescription("User: " + KeyUtil.toHumanReadableString(userId) + " logged out from: " + history.getRemoteAddress());

        addHistory(history);
    }

    public static void addLoginFailed(UserLogin userLogin, HttpServletRequest httpServletRequest, String reason) {
        AuthHistory history = new AuthHistory(HistoryTypeEnum.LoginFailed);
        addUserLogin(userLogin, history);
        history.setRemoteAddress(httpServletRequest.getRemoteAddr());

        addCurrentUser(history);
        Key userId = history.getCurrentUserRef().getKey();

        StringBuilder builder = new StringBuilder();

        builder.append("OAuth login failed!  Credentials: ").append(history.toOAuthCredentialsString())
                .append(" from: ").append(history.getRemoteAddress());

        if (userId != null) {
            builder.append(" Authenticated user: ").append(KeyUtil.toHumanReadableString(userId));
        }

        if (StringUtils.isNotBlank(reason)) builder.append(" reason: ").append(reason);
        history.setDescription(builder.toString());

        addHistory(history);
    }

    public static void addLoginFailed(String username, HttpServletRequest httpServletRequest) {
        AuthHistory history = new AuthHistory(HistoryTypeEnum.LoginFailed);
        history.setName(username);
        history.setRemoteAddress(httpServletRequest.getRemoteAddr());
        history.setDescription("Failed login attempt username: " + history.getName() + " from: " + history.getRemoteAddress());

        addHistory(history);
    }

    public static void addPasswordChanged(User user, HttpServletRequest httpServletRequest) {
        AuthHistory history = new AuthHistory(HistoryTypeEnum.PasswordChanged);
        history.setName(user.getName());
        history.setRemoteAddress(httpServletRequest.getRemoteAddr());
        addCurrentUser(history);
        Key currentUserId = history.getCurrentUserRef().getKey();
        if (user.getId().equals(currentUserId)) {
            history.setDescription("User: " + user.getName() + " changed his password from: " + history.getRemoteAddress());
        } else {
            String who = "UNKNOWN";
            if (currentUserId != null) who = KeyUtil.toHumanReadableString(currentUserId);
            history.setDescription("Password of: " + user.getName() + " changed by: " + who + " from: " + history.getRemoteAddress());
        }
        addHistory(history);
    }

    public static void addAccountCreated(User user, UserLogin userLogin, HttpServletRequest httpServletRequest) {
        AccountCreationHistory history = new AccountCreationHistory(HistoryTypeEnum.AccountCreated);
        history.setReferrer(httpServletRequest.getHeader("referer")); // Yes, with the legendary misspelling.
        history.setRemoteAddress(httpServletRequest.getRemoteAddr());
        history.getCurrentUserRef().setModel(user);
        history.setName(user.getName());
        addUserLogin(userLogin, history);
        history.setDescription("New user: " + history.getName() +
                " with credentials: " + history.toOAuthCredentialsString() +
                " from: " + history.getRemoteAddress());

        addHistory(history);
    }

    public static void addAccountCreationFailed(UserLogin userLogin, HttpServletRequest httpServletRequest, String reason) {
        AccountCreationHistory history = new AccountCreationHistory(HistoryTypeEnum.AccountCreationFailed);
        history.setReferrer(httpServletRequest.getHeader("referer")); // Yes, with the legendary misspelling.
        history.setRemoteAddress(httpServletRequest.getRemoteAddr());
        addUserLogin(userLogin, history);
        history.setDescription("Failed to create account with OAuth credentials: " + history.toOAuthCredentialsString() +
                " from: " + history.getRemoteAddress());

        addHistory(history);
    }
}
