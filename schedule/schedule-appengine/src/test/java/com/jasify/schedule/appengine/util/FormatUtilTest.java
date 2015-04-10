package com.jasify.schedule.appengine.util;

import com.jasify.schedule.appengine.TestHelper;
import com.jasify.schedule.appengine.model.activity.Activity;
import com.jasify.schedule.appengine.model.balance.Account;
import com.jasify.schedule.appengine.model.balance.OrganizationAccount;
import com.jasify.schedule.appengine.model.balance.UserAccount;
import org.junit.Test;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Objects;
import java.util.TimeZone;

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
        calendar.setTimeZone(TimeZone.getDefault());
        calendar.set(1976, Calendar.JULY, 15, 22, 10);
        activity.setStart(calendar.getTime());
        calendar.add(Calendar.HOUR, 1);
        activity.setFinish(calendar.getTime());
        String formatted = FormatUtil.toString(activity);
        assertNotNull(formatted);
        assertEquals("The Activity [Thu, 15 Jul 22:10 - 23:10]", formatted);
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
        assertEquals("NULL - account", FormatUtil.toString(account));
        UserAccount userAccount = new UserAccount();
        assertEquals(FormatUtil.toString(userAccount), FormatUtil.toString((Account) userAccount));
        OrganizationAccount organizationAccount = new OrganizationAccount();
        assertEquals(FormatUtil.toString(organizationAccount), FormatUtil.toString((Account) organizationAccount));
    }
}