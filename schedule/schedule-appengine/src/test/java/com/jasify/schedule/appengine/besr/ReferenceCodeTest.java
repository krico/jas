package com.jasify.schedule.appengine.besr;

import org.junit.Test;

import static junit.framework.TestCase.assertEquals;

public class ReferenceCodeTest {

    @Test(expected = NullPointerException.class)
    public void testToReferenceCodeWithNoInvoiceNr() throws Exception {
        new ReferenceCode("", null);
    }

    @Test
    public void testToReferenceCode() throws Exception {
        String expected = "120000000000234478943216899";
        assertEquals(expected, new ReferenceCode(null, "12000000000023447894321689").toReferenceCode());
        assertEquals(expected, new ReferenceCode("", "12000000000023447894321689").toReferenceCode());
        assertEquals(expected, new ReferenceCode("1", "2000000000023447894321689").toReferenceCode());
        assertEquals(expected, new ReferenceCode("12", "000000000023447894321689").toReferenceCode());
        assertEquals(expected, new ReferenceCode("12000", "000000023447894321689").toReferenceCode());
        assertEquals(expected, new ReferenceCode("120000000000", "23447894321689").toReferenceCode());
        assertEquals(expected, new ReferenceCode("1200000000002", "3447894321689").toReferenceCode());
    }
}