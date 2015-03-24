package com.jasify.schedule.appengine.model.cart;

import java.io.Serializable;

/**
 * @author krico
 * @since 23/03/15.
 */
public class ShoppingCart implements Serializable {
    private String id;

    public ShoppingCart() {
    }

    public ShoppingCart(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
