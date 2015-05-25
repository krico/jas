package com.jasify.schedule.appengine.spi.dm;

import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
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
        jasActivity.setId(id);
        assertEquals(id, jasActivity.getId());
    }

    @Test
    public void testStart() {
        Date start = new Date();
        JasActivity jasActivity = new JasActivity();
        jasActivity.setStart(start);
        assertEquals(start, jasActivity.getStart());
    }

    @Test
    public void testFinish() {
        Date finish = new Date();
        JasActivity jasActivity = new JasActivity();
        jasActivity.setFinish(finish);
        assertEquals(finish, jasActivity.getFinish());
    }

    @Test
    public void testPrice() {
        Double price = new Double("12.2");
        JasActivity jasActivity = new JasActivity();
        jasActivity.setPrice(price);
        assertEquals(price, jasActivity.getPrice());
    }

    @Test
    public void testActivityType() {
        JasActivityType activityType = new JasActivityType();
        JasActivity jasActivity = new JasActivity();
        jasActivity.setActivityType(activityType);
        assertEquals(activityType, jasActivity.getActivityType());
    }

    @Test
    public void testCurrency() {
        String currency = "test";
        JasActivity jasActivity = new JasActivity();
        jasActivity.setCurrency(currency);
        assertEquals(currency, jasActivity.getCurrency());
    }

    @Test
    public void testLocation() {
        String location = "test";
        JasActivity jasActivity = new JasActivity();
        jasActivity.setLocation(location);
        assertEquals(location, jasActivity.getLocation());
    }

    @Test
    public void testMaxSubscriptions() {
        int maxSubscriptions = 12;
        JasActivity jasActivity = new JasActivity();
        jasActivity.setMaxSubscriptions(maxSubscriptions);
        assertEquals(maxSubscriptions, jasActivity.getMaxSubscriptions());
    }

    @Test
    public void testSubscriptionCount() {
        int subscriptionCount = 13;
        JasActivity jasActivity = new JasActivity();
        jasActivity.setSubscriptionCount(subscriptionCount);
        assertEquals(subscriptionCount, jasActivity.getSubscriptionCount());
    }

    @Test
    public void testBookItUrl() {
        String bookItUrl = "test";
        JasActivity jasActivity = new JasActivity();
        jasActivity.setBookItUrl(bookItUrl);
        assertEquals(bookItUrl, jasActivity.getBookItUrl());
    }


    @Test
    public void testColourTag() {
        String colourTag = "test";
        JasActivity jasActivity = new JasActivity();
        assertNull(jasActivity.getColourTag());
        jasActivity.setColourTag(colourTag);
        assertEquals(colourTag, jasActivity.getColourTag());
    }
}