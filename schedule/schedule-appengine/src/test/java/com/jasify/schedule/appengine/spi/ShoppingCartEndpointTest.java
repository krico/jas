package com.jasify.schedule.appengine.spi;

import com.google.api.server.spi.response.UnauthorizedException;
import com.jasify.schedule.appengine.TestHelper;
import com.jasify.schedule.appengine.dao.cart.ShoppingCartDao;
import com.jasify.schedule.appengine.model.activity.Activity;
import com.jasify.schedule.appengine.model.activity.ActivityPackage;
import com.jasify.schedule.appengine.model.cart.ShoppingCart;
import com.jasify.schedule.appengine.spi.auth.JasifyEndpointUser;
import com.jasify.schedule.appengine.spi.dm.JasActivityPackageSubscription;
import com.jasify.schedule.appengine.spi.dm.JasItemDetails;
import com.jasify.schedule.appengine.util.FormatUtil;
import com.jasify.schedule.appengine.util.KeyUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slim3.datastore.Datastore;

import java.util.Arrays;
import java.util.List;

import static com.jasify.schedule.appengine.spi.JasifyEndpointTest.newCaller;
import static junit.framework.TestCase.*;

public class ShoppingCartEndpointTest {
    private final ShoppingCartDao shoppingCartDao = new ShoppingCartDao();
    private ShoppingCartEndpoint endpoint = new ShoppingCartEndpoint();

    @Before
    public void datastore() {
        TestHelper.initializeMemcacheWithDatastore();
    }

    @After
    public void cleanupDatastore() {
        TestHelper.cleanupDatastore();
    }

    @Test(expected = UnauthorizedException.class)
    public void testGetCartUnauthorized() throws Exception {
        endpoint.getUserCart(null);
    }

    @Test
    public void testGetCart() throws Exception {
        JasifyEndpointUser caller = new JasifyEndpointUser("foo@bar", 22, false, false);
        ShoppingCart userCart = shoppingCartDao.getUserCart(caller.getUserId());
        userCart.setCurrency("BRL");
        shoppingCartDao.put(userCart);
        userCart = endpoint.getUserCart(caller);
        assertNotNull(userCart);
        assertEquals("BRL", userCart.getCurrency());
        assertEquals(KeyUtil.userIdToCartId(caller.getUserId()), userCart.getId());
    }

    @Test
    public void testClearUserCart() throws Exception {
        JasifyEndpointUser caller = new JasifyEndpointUser("foo@bar", 22, false, false);
        ShoppingCart userCart = shoppingCartDao.getUserCart(caller.getUserId());
        userCart.setCurrency("BRL");
        shoppingCartDao.put(userCart);
        ShoppingCart cleared = endpoint.clearUserCart(caller);
        assertNotNull(cleared);
        assertEquals("CHF", cleared.getCurrency());
    }

    @Test
    public void testRemoveItem() throws Exception {
        JasifyEndpointUser caller = new JasifyEndpointUser("foo@bar", 22, false, false);
        String cartId = KeyUtil.userIdToCartId(caller.getUserId());
        shoppingCartDao.addItem(cartId, new ShoppingCart.Item("a", 1, 2));
        ShoppingCart userCart = endpoint.removeItem(caller, cartId, 0);
        assertNotNull(userCart);
        assertTrue(userCart.getItems().isEmpty());
    }

    @Test
    public void testAddUserActivity() throws Exception {
        Activity activity = new Activity();
        activity.setName("An Activity");
        activity.setPrice(38.83);
        Datastore.put(activity);

        ShoppingCart cart = endpoint.addUserActivity(newCaller(55), activity.getId());
        assertNotNull(cart);
        List<ShoppingCart.Item> items = cart.getItems();
        assertEquals(1, items.size());
        ShoppingCart.Item item = items.get(0);
        assertEquals(FormatUtil.toString(activity), item.getDescription());
        assertEquals(38.83, item.getPrice());
    }

    @Test
    public void testAddUserActivityPackage() throws Exception {
        ActivityPackage activityPackage = new ActivityPackage();
        activityPackage.setName("An AP");
        activityPackage.setPrice(900.91);

        Activity activity = new Activity();
        activity.setName("An Activity");
        activity.setPrice(38.83);


        Datastore.put(activityPackage, activity);

        JasActivityPackageSubscription subscription = new JasActivityPackageSubscription();
        subscription.setActivityIds(Arrays.asList(activity.getId()));
        ShoppingCart cart = endpoint.addUserActivityPackage(newCaller(55), activityPackage.getId(), subscription);
        assertNotNull(cart);
        List<ShoppingCart.Item> items = cart.getItems();
        assertEquals(1, items.size());
        ShoppingCart.Item item = items.get(0);
        assertNotNull(item);
        assertEquals(900.91, item.getPrice());
        assertEquals(FormatUtil.toString(activityPackage), item.getDescription());
    }

    @Test
    public void testGetItemActivity() throws Exception {
        JasifyEndpointUser caller = new JasifyEndpointUser("foo@bar", 22, false, false);

        Activity activity = new Activity();
        activity.setName("An Activity");
        activity.setPrice(38.83);
        Datastore.put(activity);

        ShoppingCart cart = endpoint.addUserActivity(caller, activity.getId());

        JasItemDetails item = endpoint.getItem(caller, cart.getId(), 0);
        assertNotNull(item);
        assertEquals(activity.getPrice(), item.getPrice());
        assertEquals(activity.getId(), item.getItemId());
        assertEquals(FormatUtil.toString(activity), item.getDescription());
    }

    @Test
    public void testGetItemActivityPackage() throws Exception {
        JasifyEndpointUser caller = new JasifyEndpointUser("foo@bar", 22, false, false);
        ActivityPackage activityPackage = new ActivityPackage();
        activityPackage.setName("Any AP");
        activityPackage.setPrice(200d);
        activityPackage.setCurrency("USD");

        Activity activity = new Activity();

        Datastore.put(activityPackage, activity);


        JasActivityPackageSubscription subscription = new JasActivityPackageSubscription();
        subscription.setActivityIds(Arrays.asList(activity.getId()));
        ShoppingCart cart = endpoint.addUserActivityPackage(caller, activityPackage.getId(), subscription);


        JasItemDetails item = endpoint.getItem(caller, cart.getId(), 0);
        assertNotNull(item);
        assertEquals(activityPackage.getPrice(), item.getPrice());
        assertEquals(activityPackage.getId(), item.getItemId());
        assertEquals(FormatUtil.toString(activityPackage), item.getDescription());
        assertNotNull(item.getSubItemIds());
        assertNotNull(item.getSubItems());
        assertNotNull(item.getSubItemTypes());

        assertEquals(1, item.getSubItemIds().size());
        assertEquals(activity.getId(), item.getSubItemIds().get(0));
    }
}