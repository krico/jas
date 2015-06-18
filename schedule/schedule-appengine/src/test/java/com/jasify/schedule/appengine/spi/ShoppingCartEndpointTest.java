package com.jasify.schedule.appengine.spi;

import com.google.api.server.spi.response.UnauthorizedException;
import com.google.appengine.api.datastore.Key;
import com.jasify.schedule.appengine.TestHelper;
import com.jasify.schedule.appengine.meta.activity.ActivityMeta;
import com.jasify.schedule.appengine.model.activity.Activity;
import com.jasify.schedule.appengine.model.activity.ActivityPackage;
import com.jasify.schedule.appengine.model.activity.ActivityType;
import com.jasify.schedule.appengine.model.activity.TestActivityServiceFactory;
import com.jasify.schedule.appengine.model.cart.ShoppingCart;
import com.jasify.schedule.appengine.model.cart.TestShoppingCartServiceFactory;
import com.jasify.schedule.appengine.model.common.Organization;
import com.jasify.schedule.appengine.spi.auth.JasifyEndpointUser;
import com.jasify.schedule.appengine.spi.dm.JasItemDetails;
import com.jasify.schedule.appengine.util.FormatUtil;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slim3.datastore.Datastore;

import java.util.ArrayList;

import static junit.framework.TestCase.*;

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
        JasifyEndpointUser caller = new JasifyEndpointUser("foo@bar", 22, false, false);
        ShoppingCart value = new ShoppingCart();
        EasyMock.expect(testShoppingCartServiceFactory.getShoppingCartServiceMock().getUserCart(caller.getUserId()))
                .andReturn(value);
        testShoppingCartServiceFactory.replay();
        ShoppingCart userCart = endpoint.getUserCart(caller);
        assertNotNull(userCart);
        assertEquals(value, userCart);
    }

    @Test
    public void testRemoveItem() throws Exception {
        JasifyEndpointUser caller = new JasifyEndpointUser("foo@bar", 22, false, false);
        ShoppingCart cart = new ShoppingCart();
        EasyMock.expect(testShoppingCartServiceFactory.getShoppingCartServiceMock().removeItem(cart.getId(), 0))
                .andReturn(cart);

        testShoppingCartServiceFactory.replay();
        ShoppingCart userCart = endpoint.removeItem(caller, cart.getId(), 0);
        assertNotNull(userCart);
        assertEquals(cart, userCart);
    }

    @Test
    public void testGetItemActivity() throws Exception {
        JasifyEndpointUser caller = new JasifyEndpointUser("foo@bar", 22, false, false);
        ShoppingCart cart = new ShoppingCart();
        cart.setId("FF");
        Activity activity = new Activity();
        activity.setPrice(200d);
        activity.setCurrency("USD");
        activity.setId(Datastore.allocateId(Activity.class));

        cart.getItems().add(new ShoppingCart.ItemBuilder().activity(activity).build());

        EasyMock.expect(testShoppingCartServiceFactory.getShoppingCartServiceMock().getCart(cart.getId()))
                .andReturn(cart);

        testShoppingCartServiceFactory.replay();

        JasItemDetails item = endpoint.getItem(caller, cart.getId(), 0);
        assertNotNull(item);
        assertEquals(activity.getPrice(), item.getPrice());
        assertEquals(activity.getId(), item.getItemId());
        assertEquals(FormatUtil.toString(activity), item.getDescription());
    }

    @Test
    public void testGetItemActivityPackage() throws Exception {
        TestActivityServiceFactory testActivityServiceFactory = new TestActivityServiceFactory();
        try {
            testActivityServiceFactory.setUp();

            JasifyEndpointUser caller = new JasifyEndpointUser("foo@bar", 22, false, false);
            ShoppingCart cart = new ShoppingCart();
            cart.setId("FF");
            ActivityPackage activityPackage = new ActivityPackage();
            activityPackage.setPrice(200d);
            activityPackage.setCurrency("USD");
            activityPackage.setId(Datastore.allocateId(ActivityPackage.class));

            Organization organization = new Organization();
            Datastore.put(organization);
            ActivityType activityType = new ActivityType();
            activityType.getOrganizationRef().setModel(organization);
            Datastore.put(activityType);
            Activity activity = new Activity(activityType);
            activity.setPrice(200d);
            activity.setCurrency("USD");
            activity.setId(Datastore.allocateId(activityType.getOrganizationRef().getKey(), ActivityMeta.get()));
            Datastore.put(activity);

            ArrayList<Key> data = new ArrayList<Key>();
            data.add(activity.getId());
            cart.getItems().add(new ShoppingCart.ItemBuilder()
                    .activityPackage(activityPackage)
                    .data(data)
                    .build());

            testActivityServiceFactory.replay();

            EasyMock.expect(testShoppingCartServiceFactory.getShoppingCartServiceMock().getCart(cart.getId()))
                    .andReturn(cart);

            testShoppingCartServiceFactory.replay();

            JasItemDetails item = endpoint.getItem(caller, cart.getId(), 0);
            assertNotNull(item);
            assertEquals(activityPackage.getPrice(), item.getPrice());
            assertEquals(activityPackage.getId(), item.getItemId());
            assertEquals(FormatUtil.toString(activityPackage), item.getDescription());
            assertNotNull(item.getSubItemIds());
            assertNotNull(item.getSubItems());
            assertNotNull(item.getSubItemTypes());

            assertEquals(1, item.getSubItemIds().size());
            assertEquals("" + item.getSubItems(), 1, item.getSubItems().size());
            assertEquals(1, item.getSubItemTypes().size());
            assertEquals(activity.getId(), item.getSubItemIds().get(0));
            assertEquals(FormatUtil.toString(activity), item.getSubItems().get(0));
            assertEquals(ActivityMeta.get().getKind(), item.getSubItemTypes().get(0));
        } finally {
            testActivityServiceFactory.tearDown();
        }
    }
}