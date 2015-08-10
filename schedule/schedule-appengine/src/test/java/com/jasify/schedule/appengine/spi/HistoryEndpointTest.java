package com.jasify.schedule.appengine.spi;

import com.google.api.server.spi.response.ForbiddenException;
import com.google.api.server.spi.response.UnauthorizedException;
import com.google.appengine.api.datastore.Key;
import com.jasify.schedule.appengine.TestHelper;
import com.jasify.schedule.appengine.dao.history.HistoryDao;
import com.jasify.schedule.appengine.model.history.History;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;

public class HistoryEndpointTest {

    private HistoryEndpoint endpoint = new HistoryEndpoint();
    private HistoryDao dao = new HistoryDao();

    @Before
    public void datastore() {
        TestHelper.initializeDatastore();
    }

    @After
    public void cleanupDatastore() {
        TestHelper.cleanupDatastore();
    }

    @Test(expected = UnauthorizedException.class)
    public void testGetHistoriesAnonymousIsUnauthorized() throws Exception {
        endpoint.getHistories(null, null, null);
    }

    @Test(expected = ForbiddenException.class)
    public void testGetHistoriesUserIsForbidden() throws Exception {
        endpoint.getHistories(JasifyEndpointTest.newCaller(55), null, null);
    }

    @Test(expected = ForbiddenException.class)
    public void testGetHistoriesOrgAdminIsForbidden() throws Exception {
        endpoint.getHistories(JasifyEndpointTest.newOrgMemberCaller(55), null, null);
    }

    @Test
    public void testGetHistoriesDefaultsToTimeWindow() throws Exception {
        long now = System.currentTimeMillis();
        Date start = new Date(now - HistoryEndpoint.DEFAULT_TIME_WINDOW_MILLIS + 1000);
        Date outside = new Date(now - HistoryEndpoint.DEFAULT_TIME_WINDOW_MILLIS - 1000);

        History inScope = new History();
        inScope.setCreated(start);

        History outOfScope = new History();
        outOfScope.setCreated(outside);

        List<Key> ids = dao.save(Arrays.asList(inScope, outOfScope));
        assertEquals(inScope.getId(), ids.get(0));
        assertEquals(outOfScope.getId(), ids.get(1));

        List<History> histories = endpoint.getHistories(JasifyEndpointTest.newAdminCaller(99), null, null);
        assertNotNull(histories);
        assertEquals(1, histories.size());
        assertEquals(inScope.getId(), histories.get(0).getId());
    }

    @Test
    public void testGetHistoriesToDateDefaultsToTimeWindowBefore() throws Exception {
        long then = 19760715;
        Date start = new Date(then - HistoryEndpoint.DEFAULT_TIME_WINDOW_MILLIS + 1000);
        Date outside = new Date(then - HistoryEndpoint.DEFAULT_TIME_WINDOW_MILLIS - 1000);

        History inScope = new History();
        inScope.setCreated(start);

        History outOfScope = new History();
        outOfScope.setCreated(outside);

        List<Key> ids = dao.save(Arrays.asList(inScope, outOfScope));
        assertEquals(inScope.getId(), ids.get(0));
        assertEquals(outOfScope.getId(), ids.get(1));

        List<History> histories = endpoint.getHistories(JasifyEndpointTest.newAdminCaller(99), null, new Date(then));
        assertNotNull(histories);
        assertEquals(1, histories.size());
        assertEquals(inScope.getId(), histories.get(0).getId());
    }

    @Test
    public void testGetHistoriesFromDate() throws Exception {
        long then = 19760715;
        Date start = new Date(then - HistoryEndpoint.DEFAULT_TIME_WINDOW_MILLIS + 1000);
        Date outside = new Date(then - HistoryEndpoint.DEFAULT_TIME_WINDOW_MILLIS - 1000);

        History inScope = new History();
        inScope.setCreated(start);

        History outOfScope = new History();
        outOfScope.setCreated(outside);

        List<Key> ids = dao.save(Arrays.asList(inScope, outOfScope));
        assertEquals(inScope.getId(), ids.get(0));
        assertEquals(outOfScope.getId(), ids.get(1));

        List<History> histories = endpoint.getHistories(JasifyEndpointTest.newAdminCaller(99), start, null);
        assertNotNull(histories);
        assertEquals(1, histories.size());
        assertEquals(inScope.getId(), histories.get(0).getId());
    }

    @Test
    public void testGetHistoriesBetween() throws Exception {
        long then = 19760715;
        Date start = new Date(then - HistoryEndpoint.DEFAULT_TIME_WINDOW_MILLIS - 1000);
        Date finish = new Date(then - HistoryEndpoint.DEFAULT_TIME_WINDOW_MILLIS + 1000);

        History second = new History();
        second.setCreated(finish);

        History first = new History();
        first.setCreated(start);

        List<Key> ids = dao.save(Arrays.asList(second, first));
        assertEquals(second.getId(), ids.get(0));
        assertEquals(first.getId(), ids.get(1));

        List<History> histories = endpoint.getHistories(JasifyEndpointTest.newAdminCaller(99), start, finish);
        assertNotNull(histories);
        assertEquals(2, histories.size());
        assertEquals(first.getId(), histories.get(0).getId());
        assertEquals(second.getId(), histories.get(1).getId());
    }

}