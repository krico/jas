package com.jasify.schedule.appengine.model.multipass.filter;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNull;

/**
 * @author wszarmach
 * @since 17/11/15.
 */
public class TimeFilterTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testComparisonType() {
        TimeFilter filter = new TimeFilter();
        assertNull(filter.getComparisonType());
        filter.setComparisonType(TimeFilter.ComparisonTypeEnum.Before);
        assertEquals(TimeFilter.ComparisonTypeEnum.Before, filter.getComparisonType());
    }

    @Test
    public void testHour() {
        TimeFilter filter = new TimeFilter();
        assertEquals(0, filter.getHour());
        filter.setHour(5);
        assertEquals(5, filter.getHour());
    }

    @Test
    public void testHourTooSmall() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Invalid hour: -1");
        new TimeFilter().setHour(-1);
    }

    @Test
    public void testHourTooLarge() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Invalid hour: 30");
        new TimeFilter().setHour(30);
    }

    @Test
    public void testMinute() {
        TimeFilter filter = new TimeFilter();
        assertEquals(0, filter.getMinute());
        filter.setMinute(5);
        assertEquals(5, filter.getMinute());
    }


    @Test
    public void testMinuteTooSmall() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Invalid minute: -1");
        new TimeFilter().setMinute(-1);
    }

    @Test
    public void testMinuteTooLarge() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Invalid minute: 90");
        new TimeFilter().setMinute(90);
    }
}
