package com.jasify.schedule.appengine.spi;

import com.google.api.server.spi.auth.common.User;
import com.google.api.server.spi.config.*;
import com.google.api.server.spi.response.BadRequestException;
import com.google.api.server.spi.response.ForbiddenException;
import com.google.api.server.spi.response.NotFoundException;
import com.google.api.server.spi.response.UnauthorizedException;
import com.google.appengine.api.datastore.Key;
import com.jasify.schedule.appengine.model.EntityNotFoundException;
import com.jasify.schedule.appengine.model.activity.Activity;
import com.jasify.schedule.appengine.model.activity.ActivityServiceFactory;
import com.jasify.schedule.appengine.model.cart.ShoppingCart;
import com.jasify.schedule.appengine.model.cart.ShoppingCartService;
import com.jasify.schedule.appengine.model.cart.ShoppingCartServiceFactory;
import com.jasify.schedule.appengine.spi.auth.JasifyAuthenticator;
import com.jasify.schedule.appengine.spi.auth.JasifyEndpointUser;
import com.jasify.schedule.appengine.spi.transform.*;
import com.jasify.schedule.appengine.util.KeyUtil;
import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * @author krico
 * @since 23/03/15.
 */
@Api(name = "jasify", /* WARN: Its LAME but you have to copy & paste this section to all *Endpoint classes in this package */
        version = "v1",
        defaultVersion = AnnotationBoolean.TRUE,
        description = "Jasify Schedule",
        authenticators = {JasifyAuthenticator.class},
        authLevel = AuthLevel.NONE,
        transformers = {
                /* one per line in alphabetical order to avoid merge conflicts */
                JasAccountTransformer.class,
                JasActivityTransformer.class,
                JasActivityTypeTransformer.class,
                JasGroupTransformer.class,
                JasKeyTransformer.class,
                JasOrganizationTransformer.class,
                JasRepeatDetailsTransformer.class,
                JasSubscriptionTransformer.class,
                JasTransactionTransformer.class,
                JasUserLoginTransformer.class,
                JasUserTransformer.class
        },
        auth = @ApiAuth(allowCookieAuth = AnnotationBoolean.TRUE /* todo: I don't know another way :-( */),
        namespace = @ApiNamespace(ownerDomain = "jasify.com",
                ownerName = "Jasify",
                packagePath = ""))
public class ShoppingCartEndpoint {

    @ApiMethod(name = "carts.getUserCart", path = "carts/user", httpMethod = ApiMethod.HttpMethod.GET)
    public ShoppingCart getUserCart(User caller) throws UnauthorizedException, ForbiddenException {
        JasifyEndpointUser jasUser = JasifyEndpoint.mustBeLoggedIn(caller);
        ShoppingCart cart = ShoppingCartServiceFactory.getShoppingCartService().getUserCart(jasUser.getUserId());
        cart.calculate();
        return cart;
    }

    @ApiMethod(name = "carts.addUserActivity", path = "carts/user/activity/{activityId}", httpMethod = ApiMethod.HttpMethod.POST)
    public ShoppingCart addUserActivity(User caller, @Named("activityId") Key activityId) throws UnauthorizedException, ForbiddenException, NotFoundException, BadRequestException {
        JasifyEndpointUser jasUser = JasifyEndpoint.mustBeLoggedIn(caller);
        ShoppingCartService cartService = ShoppingCartServiceFactory.getShoppingCartService();
        ShoppingCart cart = cartService.getUserCart(jasUser.getUserId());
        Activity activity;
        try {
            activity = ActivityServiceFactory.getActivityService().getActivity(activityId);
        } catch (EntityNotFoundException e) {
            throw new NotFoundException("activityId=" + activityId);
        }
        if (StringUtils.isBlank(activity.getName())) {
            throw new BadRequestException("Cannot add activity with no name to cart, id=" + activityId);
        }
        if (activity.getPrice() == null) {
            throw new BadRequestException("Cannot add activity with no price to cart, id=" + activityId);
        }

        cart.getItems().add(new ShoppingCart.ItemBuilder().activity(activity).build());
        cartService.putCart(cart);
        return cart;
    }

    @ApiMethod(name = "carts.removeItem", path = "carts/{cartId}/{ordinal}", httpMethod = ApiMethod.HttpMethod.GET)
    public ShoppingCart removeItem(User caller, @Named("cartId") String cartId, @Named("ordinal") Integer ordinal) throws UnauthorizedException, ForbiddenException, NotFoundException {
        JasifyEndpoint.mustBeLoggedIn(caller); //TODO: check that user owns this cart
        ShoppingCart cart = ShoppingCartServiceFactory.getShoppingCartService().getCart(cartId);
        if (cart == null) {
            throw new NotFoundException("Cart.id = " + cartId);
        }

        List<ShoppingCart.Item> items = cart.getItems();
        if (ordinal >= 0 && ordinal < items.size()) {
            items.remove(ordinal.intValue());
        } else {
            throw new NotFoundException("Item ordinal: " + ordinal);
        }

        cart.calculate();

        ShoppingCartServiceFactory.getShoppingCartService().putCart(cart);

        return cart;
    }
}