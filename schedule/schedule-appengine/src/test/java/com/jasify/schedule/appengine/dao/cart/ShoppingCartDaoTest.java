package com.jasify.schedule.appengine.dao.cart;

import com.jasify.schedule.appengine.TestHelper;
import com.jasify.schedule.appengine.memcache.Memcache;
import com.jasify.schedule.appengine.model.cart.ShoppingCart;
import com.jasify.schedule.appengine.model.users.User;
import com.jasify.schedule.appengine.util.KeyUtil;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slim3.datastore.Datastore;

import java.util.List;

import static junit.framework.TestCase.*;

public class ShoppingCartDaoTest {
    public static final String TEST_CART_ID = "any";
    private ShoppingCartDao dao = new ShoppingCartDao();

    @Before
    public void initializeDatastore() {
        TestHelper.initializeJasify();
        dao = new ShoppingCartDao();
    }

    @After
    public void cleanupDatastore() {
        TestHelper.cleanupDatastore();
    }

    @Test
    public void testGetUserCart() throws Exception {
        long userId = 19;
        ShoppingCart userCart = dao.getUserCart(userId);
        assertNotNull(userCart);
        String expectedKey = KeyUtil.userIdToCartId(userId);
        assertEquals(expectedKey, userCart.getId());
        userCart.setCurrency("USD");
        Memcache.put(userCart.getId(), userCart);
        userCart = dao.getUserCart(Datastore.createKey(User.class, userId));
        assertNotNull(userCart);
        assertEquals(expectedKey, userCart.getId());
        assertEquals("USD", userCart.getCurrency());
    }

    @Test
    public void testGetCartReturnsNull() throws Exception {
        assertNull(dao.get(RandomStringUtils.randomAlphabetic(12)));
    }

    @Test
    public void testPutGetCart() throws Exception {
        ShoppingCart cart = new ShoppingCart();
        cart.setCurrency("BRL");
        String id = dao.put(cart);
        assertTrue(StringUtils.isNotBlank(id));
        ShoppingCart shoppingCart = dao.get(id);
        assertNotNull(shoppingCart);
        assertEquals("BRL", shoppingCart.getCurrency());
    }

    @Test
    public void testAddItem() {
        ShoppingCart shoppingCart = dao.addItem(TEST_CART_ID, new ShoppingCart.Item("test", 2, 5.321));
        assertNotNull(shoppingCart);
        List<ShoppingCart.Item> items = shoppingCart.getItems();
        assertEquals(1, items.size());
        ShoppingCart.Item item = items.get(0);
        assertNotNull(item);
        assertEquals("test", item.getDescription());
        assertEquals(2, item.getUnits());
        assertEquals(5.321, item.getPrice());

        shoppingCart = dao.addItem(TEST_CART_ID, new ShoppingCart.Item("test2", 3, 6.321));
        items = shoppingCart.getItems();
        assertEquals(2, items.size());
        item = items.get(0);
        assertNotNull(item);
        assertEquals("test", item.getDescription());
        assertEquals(2, item.getUnits());
        assertEquals(5.321, item.getPrice());
        item = items.get(1);
        assertNotNull(item);
        assertEquals("test2", item.getDescription());
        assertEquals(3, item.getUnits());
        assertEquals(6.321, item.getPrice());
    }

    @Test
    public void testRemoveItem() {
        testAddItem();
        ShoppingCart cart = dao.get(TEST_CART_ID);
        List<ShoppingCart.Item> items = cart.getItems();
        ShoppingCart.Item item1 = items.get(0);
        ShoppingCart.Item item2 = items.get(1);
        ShoppingCart removed = dao.removeItem(TEST_CART_ID, 1);
        List<ShoppingCart.Item> itemsAfter = removed.getItems();
        assertEquals(1, itemsAfter.size());
        ShoppingCart.Item after = itemsAfter.get(0);
        assertEquals(item2.getItemId(), after.getItemId());
    }
}