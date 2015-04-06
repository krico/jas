package com.jasify.schedule.appengine.model.cart;

import com.jasify.schedule.appengine.TestHelper;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slim3.datastore.Datastore;

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
        ShoppingCart.Item item = new ShoppingCart.Item("My desc", 1, 1.33d);
        item.setItemId(Datastore.allocateId("Foo"));
        cart.getItems().add(item);
        cart.calculate();
        TestHelper.assertSerializable(cart);
    }

}