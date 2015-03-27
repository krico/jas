package com.jasify.schedule.appengine.model.cart;

import com.jasify.schedule.appengine.TestHelper;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;

public class ShoppingCartTest {
    @BeforeClass
    public static void initializeDatastore() {
        TestHelper.initializeJasify();
    }

    @AfterClass
    public static void cleanupDatastore() {
        TestHelper.cleanupDatastore();
    }

    @Test
    public void testIsSerializable() throws Exception {
        ShoppingCart cart = new ShoppingCart();
        cart.getItems().add(new ShoppingCart.Item("My desc", 1, 1.33d));
        cart.calculate();
        new ObjectOutputStream(new ByteArrayOutputStream()).writeObject(cart);
    }
}