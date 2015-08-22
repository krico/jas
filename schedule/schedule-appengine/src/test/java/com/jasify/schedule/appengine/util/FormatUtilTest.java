package com.jasify.schedule.appengine.util;

import com.google.appengine.repackaged.org.joda.time.DateTime;
import com.google.appengine.repackaged.org.joda.time.DateTimeZone;
import com.google.appengine.repackaged.org.joda.time.format.DateTimeFormat;
import com.google.appengine.repackaged.org.joda.time.format.DateTimeFormatter;
import com.jasify.schedule.appengine.TestHelper;
import com.jasify.schedule.appengine.model.activity.Activity;
import com.jasify.schedule.appengine.model.balance.Account;
import com.jasify.schedule.appengine.model.balance.OrganizationAccount;
import com.jasify.schedule.appengine.model.balance.UserAccount;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;

public class FormatUtilTest {


    @Before
    public void before() {
        // Your wondering why... well initializeDatastore sets the default timezone to UTC.
        // FormatUtil.toString is called indirectly by other tests and the SimpleDateFormat is initialized with default timezone
        // By the time we get here its UTC but your pc default timezone is probably not UTC so the tests will fail
        TestHelper.initializeDatastore();
    }

    @After
    public void after() {
        TestHelper.cleanupDatastore();
    }

    @Test
    public void wellDefined() throws Exception {
        TestHelper.assertUtilityClassWellDefined(FormatUtil.class);
    }

    @Test
    public void testToStringActivity() throws Exception {
        Activity activity = new Activity();
        activity.setName("The Activity");
        GregorianCalendar calendar = new GregorianCalendar();
        // App Engine Timezone is UTC
        calendar.setTimeZone(TimeZone.getDefault());
        calendar.set(1976, Calendar.JULY, 15, 22, 10);
        activity.setStart(calendar.getTime());
        calendar.add(Calendar.HOUR, 1);
        activity.setFinish(calendar.getTime());
        String formatted = FormatUtil.toString(activity);
        assertNotNull(formatted);
        // The formatted time will be in Europe/Zurich
        DateTimeFormatter dtfStart = DateTimeFormat.forPattern(" [EEE, d MMM HH:mm").withZone(DateTimeZone.forID("Europe/Zurich"));
        DateTimeFormatter dtfFinish = DateTimeFormat.forPattern(" - HH:mm]").withZone(DateTimeZone.forID("Europe/Zurich"));
        String description = "The Activity" + new DateTime(activity.getStart()).toString(dtfStart) + new DateTime(activity.getFinish()).toString(dtfFinish);
        assertEquals(description, formatted);
    }

    @Test
    public void testToStringActivityNoDates() throws Exception {
        Activity activity = new Activity();
        activity.setName("The Activity");
        String formatted = FormatUtil.toString(activity);
        assertNotNull(formatted);
        assertEquals("The Activity", formatted);
    }

    @Test
    public void testToStringAccount() throws Exception {
        Account account = new Account();
        assertEquals("NULL", FormatUtil.toString(account));
        UserAccount userAccount = new UserAccount();
        assertEquals(FormatUtil.toString(userAccount), FormatUtil.toString((Account) userAccount));
        OrganizationAccount organizationAccount = new OrganizationAccount();
        assertEquals(FormatUtil.toString(organizationAccount), FormatUtil.toString((Account) organizationAccount));
    }
}