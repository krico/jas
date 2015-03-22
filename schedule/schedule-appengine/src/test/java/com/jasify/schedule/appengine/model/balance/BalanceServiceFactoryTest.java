package com.jasify.schedule.appengine.model.balance;

import com.jasify.schedule.appengine.TestHelper;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static junit.framework.TestCase.assertNotNull;

public class BalanceServiceFactoryTest {

    @BeforeClass
    public static void initializeDatastore() {
        TestHelper.initializeJasify();
    }

    @AfterClass
    public static void cleanupDatastore() {
        TestHelper.cleanupDatastore();
    }

    @Test
    public void testGetActivityService() {
        assertNotNull(BalanceServiceFactory.getBalanceService());
    }}