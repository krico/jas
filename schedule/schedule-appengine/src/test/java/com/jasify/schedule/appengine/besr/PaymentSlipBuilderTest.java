package com.jasify.schedule.appengine.besr;

import org.junit.Test;

import static junit.framework.TestCase.assertEquals;

public class PaymentSlipBuilderTest {

    private PaymentSlipBuilder expected = new PaymentSlipBuilder();
    private PaymentSlipBuilder builder = new PaymentSlipBuilder();

    @Test
    public void testAccount() throws Exception {
        expected.account = "1-23456-7";
        assertEquals(expected, builder.account(expected.account));
    }

    @Test
    public void testCodeLine() throws Exception {
        expected.codeLine = "1-23456-7";
        assertEquals(expected, builder.codeLine(expected.codeLine));
    }

    @Test
    public void testReferenceCode() throws Exception {
        expected.referenceCode = "1-23456-7";
        assertEquals(expected, builder.referenceCode(expected.referenceCode));
    }

    @Test
    public void testCurrency() throws Exception {
        expected.currency = "1-23456-7";
        assertEquals(expected, builder.currency(expected.currency));
    }

    @Test
    public void testRecipient() throws Exception {
        expected.recipient = "1-23456-7";
        assertEquals(expected, builder.recipient(expected.recipient));
    }

    @Test
    public void testAmount() throws Exception {
        expected.amount = "1-23456-7";
        assertEquals(expected, builder.amount(expected.amount));
    }
}