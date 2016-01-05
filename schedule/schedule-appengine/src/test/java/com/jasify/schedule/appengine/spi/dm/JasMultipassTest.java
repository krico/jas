package com.jasify.schedule.appengine.spi.dm;

import com.jasify.schedule.appengine.model.multipass.filter.ActivityTypeFilter;
import com.jasify.schedule.appengine.model.multipass.filter.DayFilter;
import com.jasify.schedule.appengine.model.multipass.filter.TimeFilter;
import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * @author wszarmach
 * @since 30/11/15.
 */
public class JasMultipassTest {
    @Test
    public void testEntityWellDefined() {
        DmTestHelper.assertWellFormedEntity(JasMultipass.class);
    }

    @Test
    public void testId() {
        String id = "test";
        JasMultipass jasMultipass = new JasMultipass();
        assertNull(jasMultipass.getId());
        jasMultipass.setId(id);
        assertEquals(id, jasMultipass.getId());
    }

    @Test
    public void testCreated() {
        JasMultipass jasMultipass = new JasMultipass();
        assertNull(jasMultipass.getCreated());
        Date date = new Date();
        jasMultipass.setCreated(date);
        assertEquals(date, jasMultipass.getCreated());
    }

    @Test
    public void testModified() {
        JasMultipass jasMultipass = new JasMultipass();
        assertNull(jasMultipass.getModified());
        Date date = new Date();
        jasMultipass.setModified(date);
        assertEquals(date, jasMultipass.getModified());
    }

    @Test
    public void testPrice() {
        JasMultipass jasMultipass = new JasMultipass();
        assertNull(jasMultipass.getPrice());
        Double price = 15d;
        jasMultipass.setPrice(price);
        assertEquals(price, jasMultipass.getPrice());
    }

    @Test
    public void testCurrency() {
        JasMultipass jasMultipass = new JasMultipass();
        assertNull(jasMultipass.getCurrency());
        String currency = "EUR";
        jasMultipass.setCurrency(currency);
        assertEquals(currency, jasMultipass.getCurrency());
    }

    @Test
    public void testDescription() {
        JasMultipass jasMultipass = new JasMultipass();
        assertNull(jasMultipass.getDescription());
        String description = "test";
        jasMultipass.setDescription(description);
        assertEquals(description, jasMultipass.getDescription());
    }

    @Test
    public void testName() {
        JasMultipass jasMultipass = new JasMultipass();
        assertNull(jasMultipass.getName());
        String name = "test";
        jasMultipass.setName(name);
        assertEquals(name, jasMultipass.getName());
    }

    @Test
    public void testExpiresAfter() {
        JasMultipass jasMultipass = new JasMultipass();
        assertNull(jasMultipass.getExpiresAfter());
        Integer expiresAfter = 4;
        jasMultipass.setExpiresAfter(expiresAfter);
        assertEquals(expiresAfter, jasMultipass.getExpiresAfter());
    }

    @Test
    public void testUses() {
        JasMultipass jasMultipass = new JasMultipass();
        assertNull(jasMultipass.getUses());
        Integer uses = 4;
        jasMultipass.setUses(uses);
        assertEquals(uses, jasMultipass.getUses());
    }

    @Test
    public void testActivityTypeFilter() {
        JasMultipass jasMultipass = new JasMultipass();
        assertNull(jasMultipass.getActivityTypeFilter());
        ActivityTypeFilter activityTypeFilter = new ActivityTypeFilter();
        jasMultipass.setActivityTypeFilter(activityTypeFilter);
        assertEquals(activityTypeFilter, jasMultipass.getActivityTypeFilter());
    }

    @Test
    public void testDayFilter() {
        JasMultipass jasMultipass = new JasMultipass();
        assertNull(jasMultipass.getDayFilter());
        DayFilter dayFilter = new DayFilter();
        jasMultipass.setDayFilter(dayFilter);
        assertEquals(dayFilter, jasMultipass.getDayFilter());
    }

    @Test
    public void testTimeFilter() {
        JasMultipass jasMultipass = new JasMultipass();
        assertNull(jasMultipass.getTimeFilter());
        TimeFilter timeFilter = new TimeFilter();
        jasMultipass.setTimeFilter(timeFilter);
        assertEquals(timeFilter, jasMultipass.getTimeFilter());
    }

    @Test
    public void testOrganizationId() {
        JasMultipass jasMultipass = new JasMultipass();
        assertNull(jasMultipass.getOrganizationId());
        String organizationId = "Test";
        jasMultipass.setOrganizationId(organizationId);
        assertEquals(organizationId, jasMultipass.getOrganizationId());
    }
}
