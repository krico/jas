package com.jasify.schedule.appengine.model.history;

import com.google.appengine.api.datastore.Key;
import com.jasify.schedule.appengine.TestHelper;
import com.jasify.schedule.appengine.dao.history.HistoryDao;
import com.jasify.schedule.appengine.http.HttpUserSession;
import com.jasify.schedule.appengine.model.UserContext;
import com.jasify.schedule.appengine.model.users.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slim3.datastore.Datastore;

import java.util.Date;
import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;

public class HistoryHelperTest {
    private HistoryDao historyDao;

    @Before
    public void setup() {
        TestHelper.initializeDatastore();
        historyDao = new HistoryDao();
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

    @Test
    public void testAddMessage() throws Exception {
        Date when = new Date();
        String message = "Test event";
        HistoryHelper.addMessage(message);


        List<History> all = historyDao.listSince(when);
        assertNotNull(all);
        assertEquals(1, all.size());
        History history = all.get(0);
        assertEquals(message, history.getMessage());
        assertEquals(HistoryTypeEnum.Message, history.getType());
    }

    @Test
    public void testAddMessageCurrentUserIsSet() throws Exception {
        User user = new User();
        Key userId = Datastore.allocateId(User.class);
        user.setId(userId);
        UserContext.setCurrentUser(new HttpUserSession(user, false));

        Date when = new Date();
        HistoryHelper.addMessage("Test event");

        List<History> all = historyDao.listSince(when);
        assertEquals(userId, all.get(0).getCurrentUserRef().getKey());
    }

}