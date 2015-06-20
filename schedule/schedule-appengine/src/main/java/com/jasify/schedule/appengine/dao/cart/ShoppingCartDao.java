package com.jasify.schedule.appengine.dao.cart;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.common.base.Preconditions;
import com.jasify.schedule.appengine.memcache.BaseMemcacheTransaction;
import com.jasify.schedule.appengine.memcache.Memcache;
import com.jasify.schedule.appengine.memcache.MemcacheOperator;
import com.jasify.schedule.appengine.meta.users.UserMeta;
import com.jasify.schedule.appengine.model.cart.ShoppingCart;
import com.jasify.schedule.appengine.util.KeyUtil;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

/**
 * <b>WARNING:</b> This is not a Datastore dao!  It is a pure memcache dao!
 *
 * @author krico
 * @since 20/06/15.
 */
public class ShoppingCartDao {
    private final ShoppingCartUpdateAction IDENTITY = new ShoppingCartUpdateAction() {
        @Nonnull
        @Override
        public ShoppingCart update(@Nonnull ShoppingCart current) {
            return current;
        }
    };

    public static String allocateCartId() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    @Nonnull
    public ShoppingCart getUserCart(@Nonnull Key userId) {
        Preconditions.checkNotNull(userId);
        Preconditions.checkArgument(UserMeta.get().getKind().equals(userId.getKind()));
        Preconditions.checkArgument(userId.getId() != 0L);
        return getUserCart(userId.getId());
    }

    @Nonnull
    public ShoppingCart getUserCart(long userId) {
        String key = KeyUtil.userIdToCartId(userId);
        return update(key, IDENTITY);
    }

    public ShoppingCart update(String cartId, final ShoppingCartUpdateAction updateAction) {
        return MemcacheOperator.update(new BaseMemcacheTransaction<ShoppingCart>(cartId) {
            @Nonnull
            @Override
            public ShoppingCart execute(@Nullable MemcacheService.IdentifiableValue identifiable) {
                ShoppingCart cart = identifiable == null ? new ShoppingCart(key().toString()) : (ShoppingCart) identifiable.getValue();
                ShoppingCart newVal = updateAction.update(cart);
                newVal.setId(key().toString());
                return newVal;
            }
        });
    }

    @Nonnull
    public String put(@Nonnull final ShoppingCart cart) {
        if (StringUtils.isBlank(cart.getId())) {
            cart.setId(ShoppingCartDao.allocateCartId());
        }
        return update(cart.getId(), new ShoppingCartUpdateAction() {
            @Nonnull
            @Override
            public ShoppingCart update(@Nonnull ShoppingCart current) {
                return cart;
            }
        }).getId();
    }

    public ShoppingCart get(@Nonnull String id) {
        return Memcache.get(id);
    }

    @Nonnull
    public ShoppingCart addItem(@Nonnull final String cartId, @Nonnull final ShoppingCart.Item item) {
        return update(cartId, new ShoppingCartUpdateAction() {
            @Nonnull
            @Override
            public ShoppingCart update(@Nonnull ShoppingCart current) {
                current.getItems().add(item);
                return current;
            }
        });
    }

    @Nonnull
    public ShoppingCart removeItem(@Nonnull final String cartId, final int ordinal) {
        return update(cartId, new ShoppingCartUpdateAction() {
            @Nonnull
            @Override
            public ShoppingCart update(@Nonnull ShoppingCart cart) {
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

    public static interface ShoppingCartUpdateAction {
        @Nonnull
        ShoppingCart update(@Nonnull ShoppingCart current);
    }
}
