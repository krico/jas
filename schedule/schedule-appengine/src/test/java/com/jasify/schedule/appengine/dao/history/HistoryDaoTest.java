package com.jasify.schedule.appengine.dao.history;

import com.google.appengine.api.datastore.Key;
import com.jasify.schedule.appengine.TestHelper;
import com.jasify.schedule.appengine.model.consistency.ImmutableEntityException;
import com.jasify.schedule.appengine.model.history.History;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static junit.framework.TestCase.*;

public class HistoryDaoTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private HistoryDao historyDao;

    @Before
    public void setup() {
        TestHelper.initializeDatastore();
        historyDao = new HistoryDao();
    }

    @After
    public void cleanup() {
        TestHelper.cleanupDatastore();
    }

    @Test
    public void testAdd() throws Exception {
        Key id = historyDao.save(new History());
        assertNotNull(id);
    }

    @Test
    public void testAddChecksImmutability() throws Exception {
        thrown.expect(ImmutableEntityException.class);
        thrown.expectMessage("One cannot change history");
        History h = new History();
        historyDao.save(h);
        historyDao.save(h);
    }

    @Test
    public void testAddList() throws Exception {
        List<Key> ids = historyDao.save(Arrays.asList(new History(), new History()));
        assertNotNull(ids);
        assertEquals(2, ids.size());
    }

    @Test
    public void testAddListChecksImmutability() throws Exception {
        thrown.expect(ImmutableEntityException.class);
        thrown.expectMessage("One cannot change history");
        History history = new History();
        historyDao.save(history);
        historyDao.save(Arrays.asList(new History(), history));
    }

    @Test
    public void testListSince() throws Exception {
        Date created1 = new Date(19760715);
        Date created2 = new Date(created1.getTime() + 2);
        History history1 = new History();
        history1.setCreated(created1);
        Key id1 = historyDao.save(history1);

        List<History> histories1 = historyDao.listSince(created1);
        assertNotNull(histories1);
        assertEquals(1, histories1.size());
        assertEquals(id1, histories1.get(0).getId());

        assertTrue(historyDao.listSince(created2).isEmpty());

        History history2 = new History();
        history2.setCreated(created2);
        Key id2 = historyDao.save(history2);

        List<History> histories2 = historyDao.listSince(created2);
        assertNotNull(histories2);
        assertEquals(1, histories2.size());
        assertEquals(id2, histories2.get(0).getId());

        List<History> after = historyDao.listSince(created1);
        assertEquals(2, after.size());
        assertEquals(id1, after.get(0).getId());
        assertEquals(id2, after.get(1).getId());
    }

    @Test
    public void testListSinceSorts() throws Exception {
        Date created1 = new Date(19760715);
        Date created2 = new Date(created1.getTime() + 2);
        Date created3 = new Date(created1.getTime() + 1);

        History history1 = new History();
        history1.setCreated(created1);
        Key id1 = historyDao.save(history1);

        History history2 = new History();
        history2.setCreated(created2);
        Key id2 = historyDao.save(history2);

        History history3 = new History();
        history3.setCreated(created3);
        Key id3 = historyDao.save(history3);

        List<History> histories1 = historyDao.listSince(created1);
        assertNotNull(histories1);
        assertEquals(3, histories1.size());
        assertEquals(id1, histories1.get(0).getId());
        assertEquals(id3, histories1.get(1).getId());
        assertEquals(id2, histories1.get(2).getId());
    }

    @Test
    public void testListBetween() throws Exception {
        Date created1 = new Date(19760715);
        Date created2 = new Date(created1.getTime() + 2);
        History history1 = new History();
        history1.setCreated(created1);
        Key id1 = historyDao.save(history1);

        History history2 = new History();
        history2.setCreated(created2);
        Key id2 = historyDao.save(history2);

        List<History> histories1 = historyDao.listBetween(created1, created2);
        assertNotNull(histories1);
        assertEquals(2, histories1.size());
        assertEquals(id1, histories1.get(0).getId());
        assertEquals(id2, histories1.get(1).getId());

        List<History> histories2 = historyDao.listBetween(created1, created1);
        assertNotNull(histories2);
        assertEquals(1, histories2.size());
        assertEquals(id1, histories2.get(0).getId());

        List<History> histories3 = historyDao.listBetween(created2, created2);
        assertNotNull(histories3);
        assertEquals(1, histories3.size());
        assertEquals(id2, histories3.get(0).getId());

        List<History> histories4 = historyDao.listBetween(created1, new Date(created1.getTime() + 1));
        assertNotNull(histories4);
        assertEquals(1, histories4.size());
        assertEquals(id1, histories4.get(0).getId());
    }
}