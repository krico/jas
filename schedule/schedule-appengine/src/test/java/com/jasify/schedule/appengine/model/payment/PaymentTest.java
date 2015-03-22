package com.jasify.schedule.appengine.model.payment;

import org.junit.Test;

import static junit.framework.TestCase.assertEquals;

public class PaymentTest {

    @Test(expected = NullPointerException.class)
    public void testValidateNoCurrency() throws Exception {
        Payment payment = new Payment();
        payment.setAmount(1d);
        payment.validate();
    }

    @Test(expected = NullPointerException.class)
    public void testValidateNoAmount() throws Exception {
        Payment payment = new Payment();
        payment.setCurrency("CHF");
        payment.validate();
    }

    @Test
    public void testValidateMinimum() throws Exception {
        Payment payment = new Payment();
        payment.setCurrency("CHF");
        payment.setAmount(1d);
        payment.validate();
    }

    @Test
    public void testValidateSingleItem() throws Exception {
        Payment payment = new Payment();
        payment.setCurrency("CHF");
        payment.addItem("It", 1, 5);
        payment.validate();
        assertEquals(5d, payment.getAmount());
    }

    @Test
    public void testValidateSingleItemWithQuantity() throws Exception {
        Payment payment = new Payment();
        payment.setCurrency("CHF");
        payment.addItem("It", 2, 5);
        payment.validate();
        assertEquals(10d, payment.getAmount());
    }

    @Test
    public void testValidateMultipleItemsWithQuantity() throws Exception {
        Payment payment = new Payment();
        payment.setCurrency("CHF");
        payment.addItem("It", 2, 5);
        payment.addItem("It2", 2, 2);
        payment.validate();
        assertEquals(14d, payment.getAmount());
    }

    @Test(expected = IllegalStateException.class)
    public void testValidateMultipleItemsWithInvalidQuantity() throws Exception {
        Payment payment = new Payment();
        payment.setCurrency("CHF");
        payment.addItem("It", 2, 5);
        payment.addItem("It2", 2, 2);
        payment.setAmount(15d);
        payment.validate();
    }

    @Test(expected = IllegalStateException.class)
    public void testValidateMultipleItemsWithMissingDesc() throws Exception {
        Payment payment = new Payment();
        payment.setCurrency("CHF");
        payment.addItem("It", 2, 5);
        payment.addItem("It2", 2, 2);
        payment.getItemDescriptions().remove(0);
        payment.validate();
    }

    @Test(expected = IllegalStateException.class)
    public void testValidateMultipleItemsWithMissingUnit() throws Exception {
        Payment payment = new Payment();
        payment.setCurrency("CHF");
        payment.addItem("It", 2, 5);
        payment.addItem("It2", 2, 2);
        payment.getItemUnits().remove(0);
        payment.validate();
    }

    @Test(expected = IllegalStateException.class)
    public void testValidateMultipleItemsWithMissingPrice() throws Exception {
        Payment payment = new Payment();
        payment.setCurrency("CHF");
        payment.addItem("It", 2, 5);
        payment.addItem("It2", 2, 2);
        payment.getItemPrices().remove(0);
        payment.validate();
    }
}