package com.jasify.schedule.appengine;

import org.junit.Test;

public class TestHelperTest {

    @Test
    public void testAssertUtilityClassWellDefined() throws Exception {
        TestHelper.assertUtilityClassWellDefined(TestHelper.class);
    }
}