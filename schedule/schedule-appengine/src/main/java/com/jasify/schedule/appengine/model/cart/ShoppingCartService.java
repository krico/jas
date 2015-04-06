package com.jasify.schedule.appengine.model.cart;

import com.google.appengine.api.datastore.Key;

import javax.annotation.Nonnull;

/**
 * @author krico
 * @since 24/03/15.
 */
public interface ShoppingCartService {

    /**
     * Gets or creates a shopping cart for the user.
     *
     * @param userId the id
     * @return the cart for this user
     */
    @Nonnull
    ShoppingCart getUserCart(long userId);

    /**
     * Same as calling {@code getUserCart(userId.getId())}
     *
     * @param userId the id
     * @return the cart
     */
    @Nonnull
    ShoppingCart getUserCart(Key userId);

    /**
     * Persists the shopping cart
     *
     * @param cart to be persisted
     * @return the previously or newly allocated id
     */
    @Nonnull
    String putCart(@Nonnull ShoppingCart cart);

    /**
     * Get the cart with id <code>cartId</code>
     *
     * @param cartId the id
     * @return the cart or null if there is no cart with that id
     */
    ShoppingCart getCart(@Nonnull String cartId);

    /**
     * Clear the cart with id <code>cartId</code>
     *
     * @param cartId the id
     * @return the empty cart
     */
    ShoppingCart clearCart(@Nonnull String cartId);

}
