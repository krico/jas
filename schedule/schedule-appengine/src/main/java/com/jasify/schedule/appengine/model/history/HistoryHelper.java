package com.jasify.schedule.appengine.model.history;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Transaction;
import com.jasify.schedule.appengine.dao.common.ActivityDao;
import com.jasify.schedule.appengine.dao.common.SubscriptionDao;
import com.jasify.schedule.appengine.dao.history.HistoryDao;
import com.jasify.schedule.appengine.dao.users.UserDao;
import com.jasify.schedule.appengine.model.*;
import com.jasify.schedule.appengine.model.activity.Activity;
import com.jasify.schedule.appengine.model.activity.Subscription;
import com.jasify.schedule.appengine.model.users.PasswordRecovery;
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

    private static User getUser(Key userId) {
        try {
            if (userId != null) {
                UserDao userDao = new UserDao();
                return userDao.get(userId);
            }
        } catch (EntityNotFoundException | IllegalArgumentException e) {
            log.warn("Failed to get User", e);
        }
        return null;
    }

    private static Subscription getSubscription(Key subscriptionId) {
        try {
            if (subscriptionId != null) {
                SubscriptionDao subscriptionDao = new SubscriptionDao();
                return subscriptionDao.get(subscriptionId);
            }
        } catch (EntityNotFoundException | IllegalArgumentException e) {
            log.warn("Failed to get Subscription [{}]", subscriptionId, e);
        }
        return null;
    }

    private static Activity getActivity(Key activityId) {
        try {
            if (activityId != null) {
                ActivityDao activityDao = new ActivityDao();
                return activityDao.get(activityId);
            }
        } catch (EntityNotFoundException | IllegalArgumentException e) {
            log.warn("Failed to get Activity [{}]", activityId, e);
        }
        return null;
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

    public static void addLogin(User user, HttpServletRequest httpServletRequest, String comment) {
        AuthHistory history = new AuthHistory(HistoryTypeEnum.Login);
        history.getCurrentUserRef().setKey(user.getId());
        history.setName(user.getName());
        history.setRemoteAddress(httpServletRequest.getRemoteAddr());
        String description = "User: " + history.getName() + " logged in from: " + history.getRemoteAddress();
        if (StringUtils.isNotBlank(comment)) description += " comment: " + comment;
        history.setDescription(description);
        addHistory(history);
    }

    public static void addLogin(User user, HttpServletRequest httpServletRequest) {
        addLogin(user, httpServletRequest, null);
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

    public static void addAccountCreated(User user, HttpServletRequest httpServletRequest) {
        AccountCreationHistory history = new AccountCreationHistory(HistoryTypeEnum.AccountCreated);
        history.setReferrer(httpServletRequest.getHeader("referer")); // Yes, with the legendary misspelling.
        history.setRemoteAddress(httpServletRequest.getRemoteAddr());
        history.getCurrentUserRef().setModel(user);
        history.setName(user.getName());
        history.setDescription("New user: " + history.getName() +
                " with credentials: " + user.getEmail() +
                " from: " + history.getRemoteAddress());

        addHistory(history);
    }

    public static void addAccountCreationFailed(UserLogin userLogin, HttpServletRequest httpServletRequest, String reason) {
        AccountCreationHistory history = new AccountCreationHistory(HistoryTypeEnum.AccountCreationFailed);
        history.setReferrer(httpServletRequest.getHeader("referer")); // Yes, with the legendary misspelling.
        history.setRemoteAddress(httpServletRequest.getRemoteAddr());
        addUserLogin(userLogin, history);
        String description = "Failed to create account with OAuth credentials: " + history.toOAuthCredentialsString() +
                " from: " + history.getRemoteAddress();

        if (StringUtils.isNotBlank(reason)) description += " reason: " + reason;
        history.setDescription(description);

        addHistory(history);
    }

    public static void addForgottenPassword(PasswordRecovery recovery, HttpServletRequest httpServletRequest) {
        AuthHistory history = new AuthHistory(HistoryTypeEnum.PasswordForgotten);

        Key userId = recovery.getUserRef().getKey();
        if (userId != null) {
            try {
                UserDao userDao = new UserDao();
                User user = userDao.get(userId);
                history.setName(user.getName());
            } catch (EntityNotFoundException e) {
                log.warn("Failed to get User from PasswordRecovery", e);
            }
            history.getCurrentUserRef().setKey(userId);
        }

        history.setRemoteAddress(httpServletRequest.getRemoteAddr());

        history.setDescription("User: " + history.getName() + " forgot password from: " + history.getRemoteAddress());

        addHistory(history);

    }

    public static void addForgottenPasswordFailed(String email, HttpServletRequest httpServletRequest) {
        AuthHistory history = new AuthHistory(HistoryTypeEnum.PasswordForgottenFailed);

        history.setRemoteAddress(httpServletRequest.getRemoteAddr());
        history.setDescription("Email: " + email + " failed forgotten password from: " + history.getRemoteAddress());

        addHistory(history);
    }

    public static void addRecoveredPassword(PasswordRecovery recovery, HttpServletRequest httpServletRequest) {
        AuthHistory history = new AuthHistory(HistoryTypeEnum.PasswordRecovered);

        Key userId = recovery.getUserRef().getKey();
        if (userId != null) {
            try {
                UserDao userDao = new UserDao();
                User user = userDao.get(userId);
                history.setName(user.getName());
            } catch (EntityNotFoundException e) {
                log.warn("Failed to get User from PasswordRecovery", e);
            }
            history.getCurrentUserRef().setKey(userId);
        }

        history.setRemoteAddress(httpServletRequest.getRemoteAddr());

        history.setDescription("User: " + history.getName() + " recovered password from: " + history.getRemoteAddress());

        addHistory(history);
    }

    private static SubscriptionHistory createSubscriptionHistory(HistoryTypeEnum historyType, Key userId, Key activityId) {
        User user = getUser(userId);
        Activity activity = getActivity(activityId);
        return createSubscriptionHistory(historyType, null, user, activity);
    }

    private static SubscriptionHistory createSubscriptionHistory(HistoryTypeEnum historyType, Key subscriptionId) {
        Subscription subscription = getSubscription(subscriptionId);
        if (subscription != null) {
            User user = getUser(subscription.getUserRef().getKey());
            Activity activity = getActivity(subscription.getActivityRef().getKey());
            return createSubscriptionHistory(historyType, subscription, user, activity);
        } else {
            return createSubscriptionHistory(historyType, null, null, null);
        }
    }

    private static SubscriptionHistory createSubscriptionHistory(HistoryTypeEnum historyType, Subscription subscription, User user, Activity activity) {
        SubscriptionHistory history = new SubscriptionHistory(historyType, subscription);

        addCurrentUser(history);

        StringBuilder sb = new StringBuilder();
        sb.append("[User=");
        if (user != null) {
            sb.append(KeyUtil.keyToString(user.getId())).append(":").append(user.getEmail());
        } else {
            sb.append("?");
        }
        sb.append("] / [Activity=");

        if (activity != null) {
            sb.append(KeyUtil.keyToString(activity.getId())).append(":").append(activity.getName());
        } else {
            sb.append("?");
        }
        sb.append("]");
        history.setDescription(sb.toString());

        return history;
    }

    public static void addSubscriptionCreated(Key subscriptionId) {
        addHistory(createSubscriptionHistory(HistoryTypeEnum.SubscriptionCreated, subscriptionId));
    }

    public static void addSubscriptionCreationFailed(Key userId, Key activityId) {
        addHistory(createSubscriptionHistory(HistoryTypeEnum.SubscriptionCreationFailed, userId, activityId));
    }

    // TODO: Temporary method until we no longer delete entities - works with addSubscriptionCancelled
    public static History createTacticalSubscriptionCancelled(Key subscriptionId) {
        return createSubscriptionHistory(HistoryTypeEnum.SubscriptionCancelled, subscriptionId);
    }

    // TODO: Temporary method until we no longer delete entities - works with createSubscriptionCancelled
    public static void addTacticalSubscriptionCancelled(History history) {
        addHistory(history);
    }

    public static void addSubscriptionCancelled(Key subscriptionId) {
        addHistory(createSubscriptionHistory(HistoryTypeEnum.SubscriptionCancelled, subscriptionId));
    }

    public static void addSubscriptionCancellationFailed(Key subscriptionId) {
        addHistory(createSubscriptionHistory(HistoryTypeEnum.SubscriptionCancellationFailed, subscriptionId));
    }
}
