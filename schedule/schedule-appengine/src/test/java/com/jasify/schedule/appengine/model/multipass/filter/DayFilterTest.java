package com.jasify.schedule.appengine.model.multipass.filter;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

/**
 * @author wszarmach
 * @since 17/11/15.
 */
public class DayFilterTest {
    @Test
    public void testDaysOfWeek() {
        DayFilter filter = new DayFilter();
        assertTrue(filter.getDaysOfWeek().isEmpty());
        List<DayFilter.DayOfWeekEnum> daysOfWeek = new ArrayList<>();
        daysOfWeek.add(DayFilter.DayOfWeekEnum.Monday);
        daysOfWeek.add(DayFilter.DayOfWeekEnum.Thursday);
        filter.setDaysOfWeek(daysOfWeek);
        assertEquals(2, filter.getDaysOfWeek().size());
    }
}
