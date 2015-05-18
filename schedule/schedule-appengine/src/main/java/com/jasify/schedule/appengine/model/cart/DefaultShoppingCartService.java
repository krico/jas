package com.jasify.schedule.appengine.model.cart;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.common.base.Preconditions;
import com.jasify.schedule.appengine.memcache.BaseMemcacheTransaction;
import com.jasify.schedule.appengine.memcache.MemcacheOperator;
import com.jasify.schedule.appengine.meta.users.UserMeta;
import com.jasify.schedule.appengine.util.KeyUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slim3.memcache.Memcache;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
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

    @Nonnull
    @Override
    public ShoppingCart getUserCart(long userId) {
        String key = KeyUtil.userIdToCartId(userId);
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

    @Nonnull
    @Override
    public ShoppingCart addItem(@Nonnull final String cartId, @Nonnull final ShoppingCart.Item item) {
        return MemcacheOperator.update(new BaseMemcacheTransaction<ShoppingCart>(cartId) {
            @Nonnull
            @Override
            public ShoppingCart execute(@Nullable MemcacheService.IdentifiableValue identifiable) {
                ShoppingCart cart = identifiable == null ? new ShoppingCart(cartId) : (ShoppingCart) identifiable.getValue();
                cart.getItems().add(item);
                return cart;
            }
        });
    }

    @Nonnull
    @Override
    public ShoppingCart removeItem(@Nonnull final String cartId, final int ordinal) {
        return MemcacheOperator.update(new BaseMemcacheTransaction<ShoppingCart>(cartId) {
            @Nonnull
            @Override
            public ShoppingCart execute(@Nullable MemcacheService.IdentifiableValue identifiable) {
                ShoppingCart cart = identifiable == null ? new ShoppingCart(cartId) : (ShoppingCart) identifiable.getValue();
                List<ShoppingCart.Item> items = cart.getItems();
                if (ordinal >= 0 && ordinal < items.size()) {
                    items.remove(ordinal);
                } else {
                    throw new IllegalArgumentException("Item ordinal: " + ordinal);
                }
                return cart;
            }
        });
    }

    private static class Singleton {
        private static final ShoppingCartService INSTANCE = new DefaultShoppingCartService();
    }

}
