package com.jasify.schedule.appengine.model.payment;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;

public class InvoicePaymentTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private InvoicePayment validPayment;

    @Test
    public void testType() {
        InvoicePayment invoicePayment = new InvoicePayment();
        assertEquals(PaymentTypeEnum.Invoice, invoicePayment.getType());
    }

    @Test
    public void testDescribe() {
        InvoicePayment invoicePayment = new InvoicePayment();
        invoicePayment.setReferenceCode("ReferenceCode");
        assertEquals(" (Invoice REF=ReferenceCode)", invoicePayment.describe());
    }

    @Test
    public void testToString() {
        InvoicePayment invoicePayment = new InvoicePayment();
        assertNotNull(invoicePayment.toString());
    }

    @Test
    public void testValidateValid() {
        InvoicePayment invoicePayment = new InvoicePayment();
        invoicePayment.setCurrency("USD");
        invoicePayment.setReferenceCode("REF");
        invoicePayment.setAccount("ACC");
        invoicePayment.setRecipient("Jasify INC\nSome street\nSwitzerland");
        invoicePayment.addItem("Item desc", 1, 1d);
        invoicePayment.validate();
        validPayment = invoicePayment;
    }

    @Test
    public void testValidateAccount() {
        testValidateValid();
        validPayment.setAccount(null);
        thrown.expectMessage("InvoicePayment.account");
        thrown.expect(NullPointerException.class);
        validPayment.validate();
    }

    @Test
    public void testValidateReferenceCode() {
        testValidateValid();
        validPayment.setReferenceCode(null);
        thrown.expectMessage("InvoicePayment.referenceCode");
        thrown.expect(NullPointerException.class);
        validPayment.validate();
    }

    @Test
    public void testValidateRecipient() {
        testValidateValid();
        validPayment.setRecipient(null);
        thrown.expectMessage("InvoicePayment.recipient");
        thrown.expect(NullPointerException.class);
        validPayment.validate();
    }

}