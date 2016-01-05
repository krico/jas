package com.jasify.schedule.appengine.spi.dm;

import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class JasActivityTest {
    @Test
    public void testEntityWellDefined() {
        DmTestHelper.assertWellFormedEntity(JasActivity.class);
    }

    @Test
    public void testId() {
        String id = "test";
        JasActivity jasActivity = new JasActivity();
        assertNull(jasActivity.getId());
        jasActivity.setId(id);
        assertEquals(id, jasActivity.getId());
    }

    @Test
    public void testStart() {
        Date start = new Date();
        JasActivity jasActivity = new JasActivity();
        assertNull(jasActivity.getStart());
        jasActivity.setStart(start);
        assertEquals(start, jasActivity.getStart());
    }

    @Test
    public void testFinish() {
        Date finish = new Date();
        JasActivity jasActivity = new JasActivity();
        assertNull(jasActivity.getFinish());
        jasActivity.setFinish(finish);
        assertEquals(finish, jasActivity.getFinish());
    }

    @Test
    public void testPrice() {
        Double price = new Double("12.2");
        JasActivity jasActivity = new JasActivity();
        assertNull(jasActivity.getPrice());
        jasActivity.setPrice(price);
        assertEquals(price, jasActivity.getPrice());
    }

    @Test
    public void testActivityType() {
        JasActivityType activityType = new JasActivityType();
        JasActivity jasActivity = new JasActivity();
        assertNull(jasActivity.getActivityType());
        jasActivity.setActivityType(activityType);
        assertEquals(activityType, jasActivity.getActivityType());
    }

    @Test
    public void testCurrency() {
        String currency = "test";
        JasActivity jasActivity = new JasActivity();
        assertNull(jasActivity.getCurrency());
        jasActivity.setCurrency(currency);
        assertEquals(currency, jasActivity.getCurrency());
    }

    @Test
    public void testLocation() {
        String location = "test";
        JasActivity jasActivity = new JasActivity();
        assertNull(jasActivity.getLocation());
        jasActivity.setLocation(location);
        assertEquals(location, jasActivity.getLocation());
    }

    @Test
    public void testMaxSubscriptions() {
        int maxSubscriptions = 12;
        JasActivity jasActivity = new JasActivity();
        assertEquals(0, jasActivity.getMaxSubscriptions());
        jasActivity.setMaxSubscriptions(maxSubscriptions);
        assertEquals(maxSubscriptions, jasActivity.getMaxSubscriptions());
    }

    @Test
    public void testSubscriptionCount() {
        int subscriptionCount = 13;
        JasActivity jasActivity = new JasActivity();
        assertEquals(0, jasActivity.getSubscriptionCount());
        jasActivity.setSubscriptionCount(subscriptionCount);
        assertEquals(subscriptionCount, jasActivity.getSubscriptionCount());
    }

    @Test
    public void testBookItUrl() {
        String bookItUrl = "test";
        JasActivity jasActivity = new JasActivity();
        assertNull(jasActivity.getBookItUrl());
        jasActivity.setBookItUrl(bookItUrl);
        assertEquals(bookItUrl, jasActivity.getBookItUrl());
    }
}