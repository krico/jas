package com.jasify.schedule.appengine.model.cart;

import com.jasify.schedule.appengine.model.balance.BalanceService;

/**
 * @author krico
 * @since 20/02/15.
 */
public class ShoppingCartServiceFactory {
    private static ShoppingCartService instance;

    protected ShoppingCartServiceFactory() {
    }

    public static ShoppingCartService getShoppingCartService() {
        if (instance == null)
            return DefaultShoppingCartService.instance();
        return instance;
    }

    protected static void setInstance(ShoppingCartService instance) {
        ShoppingCartServiceFactory.instance = instance;
    }
}
