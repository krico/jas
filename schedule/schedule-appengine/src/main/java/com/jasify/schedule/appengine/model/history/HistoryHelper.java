package com.jasify.schedule.appengine.model.history;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Transaction;
import com.jasify.schedule.appengine.dao.history.HistoryDao;
import com.jasify.schedule.appengine.model.*;
import com.jasify.schedule.appengine.model.users.User;

import javax.servlet.http.HttpServletRequest;

/**
 * @author krico
 * @since 09/08/15.
 */
public final class HistoryHelper {
    private static final HistoryDao historyDao = new HistoryDao();

    private HistoryHelper() {
    }

    public static void addMessage(String message) {
        History history = new History(HistoryTypeEnum.Message);
        history.setMessage(message);

        addHistory(history);
    }

    public static void addLogin(User user, HttpServletRequest httpServletRequest) {
        //TODO: Create LoginHistory @Model
        History history = new History(HistoryTypeEnum.Login);
        history.getCurrentUserRef().setKey(user.getId());
        history.setMessage("User: " + user.getName() + " from: " + httpServletRequest.getRemoteAddr());
        addHistory(history);
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
}
