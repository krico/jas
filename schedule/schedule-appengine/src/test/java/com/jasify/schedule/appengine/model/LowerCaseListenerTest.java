package com.jasify.schedule.appengine.model;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class LowerCaseListenerTest {

    @Test
    public void testPrePut() throws Exception {
        LowerCaseListener listener = new LowerCaseListener();
        assertNull(listener.prePut(null));
        String str = "aBc";
        assertEquals(str.toLowerCase(), listener.prePut(str));
    }
}