package com.jasify.schedule.appengine.model.payment;

import com.jasify.schedule.appengine.model.FieldValueException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static junit.framework.TestCase.assertEquals;

public class PaymentTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testValidateNoCurrency() throws Exception {
        thrown.expect(NullPointerException.class);
        thrown.expectMessage("currency");
        Payment payment = new Payment();
        payment.setAmount(1d);
        payment.validate();
    }

    @Test
    public void testValidateNoAmount() throws Exception {
        thrown.expect(NullPointerException.class);
        thrown.expectMessage("amount");
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

    @Test
    public void testValidateMultipleItemsWithInvalidQuantity() throws Exception {
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage("Amount expected: 14.0, actual: 15.0");
        Payment payment = new Payment();
        payment.setCurrency("CHF");
        payment.addItem("It", 2, 5);
        payment.addItem("It2", 2, 2);
        payment.setAmount(15d);
        payment.validate();
    }

    @Test
    public void testValidateMultipleItemsWithMissingDesc() throws Exception {
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage("Please use addItem method");
        Payment payment = new Payment();
        payment.setCurrency("CHF");
        payment.addItem(null, 2, 5);
        payment.addItem(null, 2, 2);
        payment.getItemDescriptions().remove(0);
        payment.validate();
    }

    @Test
    public void testValidateMultipleItemsWithMissingUnit() throws Exception {
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage("Please use addItem method");
        Payment payment = new Payment();
        payment.setCurrency("CHF");
        payment.addItem("It", 2, 5);
        payment.addItem("It2", 2, 2);
        payment.getItemUnits().remove(0);
        payment.validate();
    }

    @Test
    public void testValidateMultipleItemsWithMissingPrice() throws Exception {
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage("Please use addItem method");
        Payment payment = new Payment();
        payment.setCurrency("CHF");
        payment.addItem("It", 2, 5);
        payment.addItem("It2", 2, 2);
        payment.getItemPrices().remove(0);
        payment.validate();
    }

    @Test
    public void testValidateWithNullPrice() throws Exception {
        thrown.expect(NullPointerException.class);
        thrown.expectMessage("itemPrices");
        Payment payment = new Payment();
        payment.setCurrency("CHF");
        payment.addItem("It", 2, 5);
        payment.setItemPrices(null);
        payment.validate();
    }

    @Test
    public void testValidateWithNullUnits() throws Exception {
        thrown.expect(NullPointerException.class);
        thrown.expectMessage("itemUnits");
        Payment payment = new Payment();
        payment.setCurrency("CHF");
        payment.addItem("It", 2, 5);
        payment.setItemUnits(null);
        payment.validate();
    }

    @Test
    public void testValidateWithNullDescriptions() throws Exception {
        thrown.expect(NullPointerException.class);
        thrown.expectMessage("itemDescriptions");
        Payment payment = new Payment();
        payment.setCurrency("CHF");
        payment.addItem("It", 2, 5);
        payment.setItemDescriptions(null);
        payment.validate();
    }

    @Test
    public void testDescribe() {
        Payment payment = new Payment();
        payment.setCurrency("CHF");
        payment.addItem("Squash", 2, 5);
        assertEquals("[Squash]", payment.describe());
    }
}