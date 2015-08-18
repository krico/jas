package com.jasify.schedule.appengine.besr;

import org.junit.Test;

import static junit.framework.TestCase.assertEquals;

public class PaymentSlipBuilderTest {

    private PaymentSlipBuilder expected = new PaymentSlipBuilder();
    private PaymentSlipBuilder builder = new PaymentSlipBuilder();

    @Test
    public void testIsrChf() throws Exception {
        expected.slipType = SlipTypeEnum.ISR_CHF;
        assertEquals(expected, PaymentSlipBuilder.isrChf());
    }

    @Test
    public void testSlipType() throws Exception {
        expected.slipType = SlipTypeEnum.ISR_EUR;
        assertEquals(expected, builder.slipType(SlipTypeEnum.ISR_EUR));
    }

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
    public void testReferenceCodeIsGeneratedWhenNotSpecified() throws Exception {
        PaymentSlip slip = PaymentSlipBuilder.isrChf()
                .account("1-00016-2")
                .identificationNumber("120000")
                .invoiceNumber("23447894321689")
                .amount(20.12d)
                .build();

        assertEquals(CheckDigit.complete("12000000000023447894321689"), slip.getReferenceCode());
    }

    @Test
    public void testCodeLineIsGeneratedWhenNotSpecified() throws Exception {
        PaymentSlip slip = PaymentSlipBuilder.isrChf()
                .account("1-00016-2")
                .referenceCode("12000000000023447894321689")
                .amount(20.12d)
                .build();

        assertEquals("0100000020124>120000000000234478943216899+ 010001628>", slip.getCodeLine());
    }

    @Test
    public void testCodeLineIsGeneratedWithSubscriberNumberWhenNotSpecified() throws Exception {
        PaymentSlip slip = PaymentSlipBuilder.isrChf()
                .account("00016-2")
                .subscriber("010001628")
                .referenceCode("12000000000023447894321689")
                .amount(20.12d)
                .build();

        assertEquals("0100000020124>120000000000234478943216899+ 010001628>", slip.getCodeLine());
    }

    @Test
    public void testCodeLineWithSzarmachData() throws Exception {
        PaymentSlip slip = PaymentSlipBuilder.isrPlusChf()
                .account("01-145-6")
                .subscriber("010001456")
                .referenceCode("302926004930400052107000021")
                .build();

        assertEquals("042>302926004930400052107000021+ 010001456>", slip.getCodeLine());
    }

    @Test
    public void testCodeLineWithSzarmachDataComplete() throws Exception {
        PaymentSlip slip = PaymentSlipBuilder.isrPlusChf()
                .account("01-145-6")
                .subscriber("01000145")
                .referenceCode("302926004930400052107000021")
                .build();

        assertEquals("042>302926004930400052107000021+ 010001456>", slip.getCodeLine());
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

    @Test
    public void testAmountAsDouble() throws Exception {
        expected.amount = "100";
        assertEquals(expected, builder.amount(1d));
        expected.amount = "1000";
        assertEquals(expected, builder.amount(10d));
        expected.amount = "123400";
        assertEquals(expected, builder.amount(1234d));
        expected.amount = "101";
        assertEquals(expected, builder.amount(1.01d));
        expected.amount = "110";
        assertEquals(expected, builder.amount(1.1d));
        assertEquals(expected, builder.amount(1.10d));
        expected.amount = "001";
        assertEquals(expected, builder.amount(0.01d));
        expected.amount = "010";
        assertEquals(expected, builder.amount(0.10d));
        expected.amount = "987654";
        assertEquals(expected, builder.amount(9876.54d));
        expected.amount = "123";
        assertEquals(expected, builder.amount(1.23d));
        expected.amount = "034";
        assertEquals(expected, builder.amount(0.34d));
        expected.amount = "12345678910";
        assertEquals(expected, builder.amount(123456789.10d));
    }

    @Test
    public void testAmountAsDoubleWeirdCase() throws Exception {
        expected.amount = "1234";
        assertEquals(expected, builder.amount(12.34d));
    }
}