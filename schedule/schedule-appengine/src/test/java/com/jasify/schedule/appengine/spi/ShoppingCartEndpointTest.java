package com.jasify.schedule.appengine.spi;

import com.jasify.schedule.appengine.TestHelper;
import com.jasify.schedule.appengine.model.cart.TestShoppingCartServiceFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ShoppingCartEndpointTest {
    private TestShoppingCartServiceFactory testShoppingCartServiceFactory = new TestShoppingCartServiceFactory();

    private ShoppingCartEndpoint endpoint = new ShoppingCartEndpoint();

    @Before
    public void datastore() {
        TestHelper.initializeMemcacheWithDatastore();
        testShoppingCartServiceFactory.setUp();

    }

    @After
    public void cleanupDatastore() {
        TestHelper.cleanupDatastore();
        testShoppingCartServiceFactory.tearDown();
    }

    @Test
    public void testGetCart() throws Exception {
        testShoppingCartServiceFactory.replay();
    }
}