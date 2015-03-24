package com.jasify.schedule.appengine.model.cart;

import com.jasify.schedule.appengine.TestService;
import org.easymock.EasyMock;

/**
 * @author krico
 * @since 08/01/15.
 */
public class TestShoppingCartServiceFactory extends ShoppingCartServiceFactory implements TestService {
    private ShoppingCartService shoppingCartServiceMock;

    public void setUp() {
        shoppingCartServiceMock = EasyMock.createMock(ShoppingCartService.class);
        setInstance(shoppingCartServiceMock);
    }

    public void tearDown() {
        setInstance(null);
        EasyMock.verify(shoppingCartServiceMock);
        shoppingCartServiceMock = null;
    }

    public ShoppingCartService getShoppingCartServiceMock() {
        return shoppingCartServiceMock;
    }

    public void replay() {
        EasyMock.replay(shoppingCartServiceMock);
    }
}