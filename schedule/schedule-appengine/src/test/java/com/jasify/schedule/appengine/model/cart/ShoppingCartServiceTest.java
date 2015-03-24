package com.jasify.schedule.appengine.model.cart;

import com.jasify.schedule.appengine.TestHelper;
import com.jasify.schedule.appengine.model.users.User;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slim3.datastore.Datastore;

import static junit.framework.TestCase.*;

public class ShoppingCartServiceTest {
    private ShoppingCartService shoppingCartService;

    @Before
    public void initializeDatastore() {
        TestHelper.initializeJasify();
        shoppingCartService = ShoppingCartServiceFactory.getShoppingCartService();
    }

    @After
    public void cleanupDatastore() {
        TestHelper.cleanupDatastore();
    }

    @Test
    public void testInstance() {
        assertNotNull(shoppingCartService);
    }

    @Test
    public void getGetUserCart() {
        ShoppingCart cart = shoppingCartService.getUserCart(25);
        assertNotNull(cart);
        assertNotNull(cart.getId());
    }

    @Test
    public void getGetUserCartWithKey() {
        ShoppingCart cart = shoppingCartService.getUserCart(Datastore.createKey(User.class, 25));
        assertNotNull(cart);
        assertNotNull(cart.getId());
    }

    @Test
    public void getGetCartNonExistants() {
        assertNull(shoppingCartService.getCart(RandomStringUtils.randomAlphabetic(32)));
    }

    @Test
    public void getPutCart() {
        ShoppingCart create = new ShoppingCart();
        String id = shoppingCartService.putCart(create);
        assertNotNull(id);
        assertNotNull(create.getId());
        assertEquals(id, create.getId());
    }

    @Test
    public void getPutCartWithId() {
        final String expectedId = "sasquatch";
        ShoppingCart create = new ShoppingCart(expectedId);
        String id = shoppingCartService.putCart(create);
        assertNotNull(id);
        assertNotNull(create.getId());
        assertEquals(id, create.getId());
        assertEquals(expectedId, create.getId());
    }

    @Test
    public void getPutExistingCartWithId() {
        ShoppingCart create = shoppingCartService.getUserCart(25);
        final String expectedId = create.getId();
        String id = shoppingCartService.putCart(create);
        assertNotNull(id);
        assertNotNull(create.getId());
        assertEquals(id, create.getId());
        assertEquals(expectedId, create.getId());
    }

    @Test
    public void getGetCart() {
        ShoppingCart expected = shoppingCartService.getUserCart(109);
        ShoppingCart cart = shoppingCartService.getCart(expected.getId());
        assertNotNull(cart);
        assertEquals(expected.getId(), cart.getId());
    }


}