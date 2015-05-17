package com.jasify.schedule.appengine.spi;

import com.google.api.server.spi.auth.common.User;
import com.google.api.server.spi.config.*;
import com.google.api.server.spi.response.BadRequestException;
import com.google.api.server.spi.response.ForbiddenException;
import com.google.api.server.spi.response.NotFoundException;
import com.google.api.server.spi.response.UnauthorizedException;
import com.google.appengine.api.datastore.Key;
import com.google.common.base.Preconditions;
import com.jasify.schedule.appengine.meta.activity.ActivityPackageMeta;
import com.jasify.schedule.appengine.model.EntityNotFoundException;
import com.jasify.schedule.appengine.model.activity.Activity;
import com.jasify.schedule.appengine.model.activity.ActivityPackage;
import com.jasify.schedule.appengine.model.activity.ActivityService;
import com.jasify.schedule.appengine.model.activity.ActivityServiceFactory;
import com.jasify.schedule.appengine.model.cart.ShoppingCart;
import com.jasify.schedule.appengine.model.cart.ShoppingCartService;
import com.jasify.schedule.appengine.model.cart.ShoppingCartServiceFactory;
import com.jasify.schedule.appengine.spi.auth.JasifyAuthenticator;
import com.jasify.schedule.appengine.spi.auth.JasifyEndpointUser;
import com.jasify.schedule.appengine.spi.dm.JasActivityPackageSubscription;
import com.jasify.schedule.appengine.spi.dm.JasItemDetails;
import com.jasify.schedule.appengine.spi.transform.*;
import com.jasify.schedule.appengine.util.FormatUtil;
import com.jasify.schedule.appengine.util.KeyUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

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
                JasActivityPackageTransformer.class,
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

    @ApiMethod(name = "carts.clearUserCart", path = "carts/user", httpMethod = ApiMethod.HttpMethod.DELETE)
    public ShoppingCart clearUserCart(User caller) throws UnauthorizedException, ForbiddenException {
        JasifyEndpointUser jasUser = JasifyEndpoint.mustBeLoggedIn(caller);
        String cartId = KeyUtil.userIdToCartId(jasUser.getUserId());
        return ShoppingCartServiceFactory.getShoppingCartService().clearCart(cartId).calculate();
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

    @ApiMethod(name = "carts.addUserActivityPackage", path = "carts/user/activity-package/{activityPackageId}", httpMethod = ApiMethod.HttpMethod.POST)
    public ShoppingCart addUserActivityPackage(User caller, @Named("activityPackageId") Key activityPackageId, JasActivityPackageSubscription subscription) throws UnauthorizedException, ForbiddenException, NotFoundException, BadRequestException {
        JasifyEndpointUser jasUser = JasifyEndpoint.mustBeLoggedIn(caller);
        ShoppingCartService cartService = ShoppingCartServiceFactory.getShoppingCartService();
        ShoppingCart cart = cartService.getUserCart(jasUser.getUserId());
        ActivityPackage activityPackage;
        try {
            activityPackage = ActivityServiceFactory.getActivityService().getActivityPackage(activityPackageId);
        } catch (EntityNotFoundException e) {
            throw new NotFoundException("activityPackageId=" + activityPackageId);
        }
        if (StringUtils.isBlank(activityPackage.getName())) {
            throw new BadRequestException("Cannot add activityPackageId with no name to cart, activityPackageId=" + activityPackageId);
        }
        if (activityPackage.getPrice() == null) {
            throw new BadRequestException("Cannot add activityPackageId with no price to cart, activityPackageId=" + activityPackageId);
        }

        List<Key> activityIds = Preconditions.checkNotNull(subscription.getActivityIds());
        if (activityIds.isEmpty()) {
            throw new BadRequestException("You must have at least 1 activity, activityPackageId=" + activityPackageId);
        }


        Set<Key> uniqueKeys = new LinkedHashSet<>();
        for (Key activityId : activityIds) {
            uniqueKeys.add(activityId);
        }

        for (Key activityId : uniqueKeys) {
            try {
                //simply validate that it exists
                ActivityServiceFactory.getActivityService().getActivity(activityId);
            } catch (EntityNotFoundException e) {
                throw new NotFoundException("activityId=" + activityId);
            }
        }

        cart.getItems().add(new ShoppingCart.ItemBuilder()
                .activityPackage(activityPackage)
                .data(new ArrayList<Key>(uniqueKeys))
                .build());

        cartService.putCart(cart);

        return cart;
    }

    @ApiMethod(name = "carts.removeItem", path = "carts/{cartId}/{ordinal}", httpMethod = ApiMethod.HttpMethod.DELETE)
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

    @ApiMethod(name = "carts.getItem", path = "carts/{cartId}/{ordinal}", httpMethod = ApiMethod.HttpMethod.GET)
    public JasItemDetails getItem(User caller, @Named("cartId") String cartId, @Named("ordinal") Integer ordinal) throws UnauthorizedException, ForbiddenException, NotFoundException {
        JasifyEndpoint.mustBeLoggedIn(caller); //TODO: check that user owns this cart
        ShoppingCart cart = ShoppingCartServiceFactory.getShoppingCartService().getCart(cartId);
        if (cart == null) {
            throw new NotFoundException("Cart.id = " + cartId);
        }

        List<ShoppingCart.Item> items = cart.getItems();
        if (ordinal >= 0 && ordinal < items.size()) {
            ShoppingCart.Item item = items.get(ordinal);
            Key id = item.getItemId();

            try {
                if (ActivityPackageMeta.get().getKind().equals(id.getKind())) {

                    List<Key> subItemIds = new ArrayList<>();
                    List<String> subItems = new ArrayList<>();
                    List<String> subItemTypes = new ArrayList<>();

                    ActivityService activityService = ActivityServiceFactory.getActivityService();

                    //noinspection unchecked
                    for (Key key : (Iterable<Key>) item.getData()) {
                        Activity activity = activityService.getActivity(key);
                        subItemIds.add(key);
                        subItems.add(FormatUtil.toString(activity));
                        subItemTypes.add(key.getKind());
                    }
                    return new JasItemDetails(item, subItemIds, subItems, subItemTypes);
                }

                return new JasItemDetails(item);
            } catch (EntityNotFoundException e) {
                throw new NotFoundException(e);
            }

        } else {
            throw new NotFoundException("Item ordinal: " + ordinal);
        }
    }
}
