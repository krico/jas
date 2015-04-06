package com.jasify.schedule.appengine.util;

import com.jasify.schedule.appengine.TestHelper;
import com.jasify.schedule.appengine.model.activity.Activity;
import org.junit.Test;

import java.util.Calendar;
import java.util.GregorianCalendar;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;

public class FormatUtilTest {

    @Test
    public void wellDefined() throws Exception {
        TestHelper.assertUtilityClassWellDefined(FormatUtil.class);
    }

    @Test
    public void testToStringActivity() throws Exception {
        Activity activity = new Activity();
        activity.setName("The Activity");
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.set(1976, Calendar.JULY, 15, 22, 10);
        activity.setStart(calendar.getTime());
        calendar.add(Calendar.HOUR, 1);
        activity.setFinish(calendar.getTime());
        String formatted = FormatUtil.toString(activity);
        assertNotNull(formatted);
        assertEquals("The Activity [Thu, 15 Jul 22:10 - 23:10]", formatted);
    }
}