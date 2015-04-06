package com.jasify.schedule.appengine.model.cart;

import com.google.appengine.api.datastore.Key;
import com.google.common.base.Preconditions;
import com.jasify.schedule.appengine.meta.users.UserMeta;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slim3.memcache.Memcache;

import javax.annotation.Nonnull;
import java.util.UUID;

/**
 * @author krico
 * @since 24/03/15.
 */
public class DefaultShoppingCartService implements ShoppingCartService {
    private static final Logger log = LoggerFactory.getLogger(DefaultShoppingCartService.class);

    private DefaultShoppingCartService() {
    }

    static ShoppingCartService instance() {
        return Singleton.INSTANCE;
    }

    private static String allocateCartId() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    private static String userIdToCartId(long userId) {
        return String.format("U%016d", userId);
    }

    @Nonnull
    @Override
    public ShoppingCart getUserCart(long userId) {
        String key = userIdToCartId(userId);
        ShoppingCart cart = Memcache.get(key);
        if (cart == null) {
            cart = new ShoppingCart(key);
            Memcache.put(key, cart);
        }
        return cart;
    }

    @Nonnull
    @Override
    public ShoppingCart getUserCart(Key userId) {
        Preconditions.checkNotNull(userId);
        Preconditions.checkArgument(UserMeta.get().getKind().equals(userId.getKind()));
        return getUserCart(userId.getId());
    }

    @Nonnull
    @Override
    public String putCart(@Nonnull ShoppingCart cart) {
        if (StringUtils.isBlank(cart.getId())) {
            cart.setId(allocateCartId());
        }
        Memcache.put(cart.getId(), cart);
        return cart.getId();
    }

    @Override
    public ShoppingCart getCart(@Nonnull String cartId) {
        return Memcache.get(cartId);
    }

    @Override
    public ShoppingCart clearCart(@Nonnull String cartId) {
        ShoppingCart value = new ShoppingCart(cartId);
        Memcache.put(cartId, value);
        return value;
    }

    private static class Singleton {
        private static final ShoppingCartService INSTANCE = new DefaultShoppingCartService();
    }

}
