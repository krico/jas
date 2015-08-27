package com.jasify.schedule.appengine.communication;

import com.jasify.schedule.appengine.TestHelper;
import com.jasify.schedule.appengine.model.activity.Activity;
import com.jasify.schedule.appengine.util.InternationalizationUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Calendar;
import java.util.GregorianCalendar;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;

public class ModelUtilTest {
    private ApplicationContextImpl.ModelUtil mut;

    @Before
    public void initialize() {
        TestHelper.initializeDatastore();
        mut = new ApplicationContextImpl.ModelUtil(TestHelper.createApplicationContextApp());
    }

    @After
    public void cleanup() {
        TestHelper.cleanupDatastore();
    }

    @Test
    public void testFormatPeriodShort() throws Exception {
        Activity activity = new Activity();

        GregorianCalendar calendar = new GregorianCalendar();
        calendar.clear();
        calendar.setTimeZone(InternationalizationUtil.ZURICH_TIME_ZONE);
        calendar.set(1976, Calendar.JULY, 15, 22, 10);
        activity.setStart(calendar.getTime());
        calendar.set(Calendar.MINUTE, 45);
        activity.setFinish(calendar.getTime());

        String formatted = mut.formatPeriodShort(activity);
        assertNotNull(formatted);
        assertEquals("15/07/76 [22:10 - 22:45]", formatted);
    }
}