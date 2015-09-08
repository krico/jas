package com.jasify.schedule.appengine.spi;

import com.jasify.schedule.appengine.TestHelper;
import com.jasify.schedule.appengine.model.activity.Activity;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static junit.framework.TestCase.*;

/**
 * @author wszarmach
 * @since 03/09/15.
 */
public class ActivityFilterTest {

    @Test
    public void testSingleActivityBeforeFrom() {
        Activity activity = TestHelper.createActivity(false);
        Date fromDate = new Date(activity.getStart().getTime() + 1);
        assertFalse(ActivityFilter.filtered(activity, fromDate, null));
    }

    @Test
    public void testSingleActivityAfterFrom() {
        Activity activity = TestHelper.createActivity(false);
        Date fromDate = new Date(activity.getStart().getTime() - 1);
        assertTrue(ActivityFilter.filtered(activity, fromDate, null));
    }

    @Test
    public void testSingleActivityEqualsFrom() {
        Activity activity = TestHelper.createActivity(false);
        Date fromDate = new Date(activity.getStart().getTime());
        assertTrue(ActivityFilter.filtered(activity, fromDate, null));
    }

    @Test
    public void testSingleActivityBeforeTo() {
        Activity activity = TestHelper.createActivity(false);
        Date toDate = new Date(activity.getFinish().getTime() + 1);
        assertTrue(ActivityFilter.filtered(activity, null, toDate));
    }

    @Test
    public void testSingleActivityAfterTo() {
        Activity activity = TestHelper.createActivity(false);
        Date toDate = new Date(activity.getFinish().getTime() - 1);
        assertFalse(ActivityFilter.filtered(activity, null, toDate));
    }

    @Test
    public void testSingleActivityEqualsTo() {
        Activity activity = TestHelper.createActivity(false);
        Date toDate = new Date(activity.getFinish().getTime());
        assertTrue(ActivityFilter.filtered(activity, null, toDate));
    }

    @Test
    public void testFilterOffset() throws Exception {
        List<Activity> input = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            input.add(TestHelper.createActivity(false));
        }
        List<Activity> result = new ActivityFilter().filter(input, null, null, 3, null);
        assertEquals(2, result.size());
    }

    @Test
    public void testFilterOffsetGreaterSize() throws Exception {
        List<Activity> input = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            input.add(TestHelper.createActivity(false));
        }
        List<Activity> result = new ActivityFilter().filter(input, null, null, 7, null);
        assertEquals(0, result.size());
    }

    @Test
    public void testFilterLimit() throws Exception {
        List<Activity> input = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            input.add(TestHelper.createActivity(false));
        }
        List<Activity> result = new ActivityFilter().filter(input, null, null, null, 3);
        assertEquals(3, result.size());
    }
}
