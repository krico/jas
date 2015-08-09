package com.jasify.schedule.appengine.spi;

import com.jasify.schedule.appengine.TestHelper;
import com.jasify.schedule.appengine.dao.history.HistoryDao;
import com.jasify.schedule.appengine.model.history.History;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

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

    @Test
    public void testGetHistories() throws Exception {
        Date inScope = new Date(System.currentTimeMillis() - TimeUnit.DAYS.toMillis(7) + 1000);
        Date outOfScope = new Date(System.currentTimeMillis() - TimeUnit.DAYS.toMillis(7) - 1000);
        History h1 = new History();
        h1.setCreated(inScope);
        History h2 = new History();
        h2.setCreated(outOfScope);
        dao.save(Arrays.asList(h1, h2));
        List<History> histories = endpoint.getHistories(JasifyEndpointTest.newAdminCaller(99), null);
        assertNotNull(histories);
        assertEquals(1, histories.size());
        assertEquals(h1.getId(), histories.get(0).getId());
        List<History> histories2 = endpoint.getHistories(JasifyEndpointTest.newAdminCaller(99), outOfScope);
        assertNotNull(histories2);
        assertEquals(2, histories2.size());
        assertEquals(h2.getId(), histories2.get(0).getId());
        assertEquals(h1.getId(), histories2.get(1).getId());
    }
}