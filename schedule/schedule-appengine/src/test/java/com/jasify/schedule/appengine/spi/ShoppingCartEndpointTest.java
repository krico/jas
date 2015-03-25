package com.jasify.schedule.appengine.spi;

import com.google.api.server.spi.response.UnauthorizedException;
import com.jasify.schedule.appengine.TestHelper;
import com.jasify.schedule.appengine.model.cart.ShoppingCart;
import com.jasify.schedule.appengine.model.cart.TestShoppingCartServiceFactory;
import com.jasify.schedule.appengine.spi.auth.JasifyEndpointUser;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;

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

    @Test(expected = UnauthorizedException.class)
    public void testGetCartUnauthorized() throws Exception {
        testShoppingCartServiceFactory.replay();
        endpoint.getUserCart(null);
    }

    @Test
    public void testGetCart() throws Exception {
        JasifyEndpointUser caller = new JasifyEndpointUser("foo@bar", 22, false);
        ShoppingCart value = new ShoppingCart();
        EasyMock.expect(testShoppingCartServiceFactory.getShoppingCartServiceMock().getUserCart(caller.getUserId()))
                .andReturn(value);
        testShoppingCartServiceFactory.replay();
        ShoppingCart userCart = endpoint.getUserCart(caller);
        assertNotNull(userCart);
        assertEquals(value, userCart);
    }
}