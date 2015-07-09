package com.jasify.schedule.appengine.model;

import com.jasify.schedule.appengine.TestHelper;
import org.junit.Test;

public class UniqueConstraintsTest {
    @Test
    public void testWellDefined() throws Exception {
        TestHelper.assertUtilityClassWellDefined(UniqueConstraints.class);
    }

}