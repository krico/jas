package com.jasify.schedule.appengine.spi;

import com.google.api.server.spi.auth.common.User;
import com.google.api.server.spi.config.*;
import com.google.api.server.spi.response.BadRequestException;
import com.google.api.server.spi.response.ForbiddenException;
import com.google.api.server.spi.response.NotFoundException;
import com.google.api.server.spi.response.UnauthorizedException;
import com.google.appengine.api.datastore.Key;
import com.google.common.base.Preconditions;
import com.jasify.schedule.appengine.dao.cart.ShoppingCartDao;
import com.jasify.schedule.appengine.dao.common.ActivityDao;
import com.jasify.schedule.appengine.dao.common.ActivityPackageDao;
import com.jasify.schedule.appengine.memcache.Memcache;
import com.jasify.schedule.appengine.meta.activity.ActivityPackageMeta;
import com.jasify.schedule.appengine.model.EntityNotFoundException;
import com.jasify.schedule.appengine.model.activity.Activity;
import com.jasify.schedule.appengine.model.activity.ActivityPackage;
import com.jasify.schedule.appengine.model.cart.ShoppingCart;
import com.jasify.schedule.appengine.spi.auth.JasifyAuthenticator;
import com.jasify.schedule.appengine.spi.auth.JasifyEndpointUser;
import com.jasify.schedule.appengine.spi.dm.JasActivityPackageSubscription;
import com.jasify.schedule.appengine.spi.dm.JasItemDetails;
import com.jasify.schedule.appengine.spi.dm.JasNewShoppingCartRequest;
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

    private final ActivityDao activityDao = new ActivityDao();
    private final ActivityPackageDao activityPackageDao = new ActivityPackageDao();
    private final ShoppingCartDao shoppingCartDao = new ShoppingCartDao();

    private Activity validateAndGetActivity(Key activityId) throws NotFoundException, BadRequestException {
        Activity activity;
        try {
            activity = activityDao.get(activityId);
        } catch (EntityNotFoundException e) {
            throw new NotFoundException("activityId=" + activityId);
        }
        if (StringUtils.isBlank(activity.getName())) {
            throw new BadRequestException("Cannot add activity with no name to cart, id=" + activityId);
        }
        if (activity.getPrice() == null) {
            throw new BadRequestException("Cannot add activity with no price to cart, id=" + activityId);
        }
        return activity;
    }

    private ActivityPackage validateAndGetActivityPackage(Key activityPackageId) throws NotFoundException, BadRequestException {
        if (activityPackageId == null) {
            throw new BadRequestException("activityPackageId is NULL");
        }
        ActivityPackage activityPackage;
        try {
            activityPackage = activityPackageDao.get(activityPackageId);
        } catch (EntityNotFoundException e) {
            throw new NotFoundException("activityPackageId=" + activityPackageId);
        }
        if (StringUtils.isBlank(activityPackage.getName())) {
            throw new BadRequestException("Cannot add activityPackageId with no name to cart, activityPackageId=" + activityPackageId);
        }
        if (activityPackage.getPrice() == null) {
            throw new BadRequestException("Cannot add activityPackageId with no price to cart, activityPackageId=" + activityPackageId);
        }
        return activityPackage;
    }

    private Set<Key> validateSubscriptionActivities(Key activityPackageId, JasActivityPackageSubscription subscription) throws BadRequestException, NotFoundException {
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
                activityDao.get(activityId);
            } catch (EntityNotFoundException e) {
                throw new NotFoundException("activityId=" + activityId);
            }
        }
        return uniqueKeys;
    }

    @ApiMethod(name = "carts.get", path = "carts/anonymous/{id}", httpMethod = ApiMethod.HttpMethod.GET)
    public ShoppingCart getCart(User caller, @Named("id") String cartId) throws NotFoundException, BadRequestException {
        return shoppingCartDao.get(cartId).calculate();
    }

    @ApiMethod(name = "carts.createAnonymousCart", path = "carts/anonymous", httpMethod = ApiMethod.HttpMethod.POST)
    public ShoppingCart createAnonymousCart(User caller, JasNewShoppingCartRequest request) throws NotFoundException, BadRequestException {
        ShoppingCart cart = new ShoppingCart();
        List<Key> activityIds = request.getActivityIds();
        if (activityIds != null) {
            for (Key activityId : activityIds) {
                Activity activity = validateAndGetActivity(activityId);
                cart.getItems().add(new ShoppingCart.ItemBuilder()
                        .activity(activity)
                        .build());
            }
        }
        List<JasNewShoppingCartRequest.ActivityPackageSubscription> activityPackageSubscriptions = request.getActivityPackageSubscriptions();
        if (activityPackageSubscriptions != null) {
            for (JasNewShoppingCartRequest.ActivityPackageSubscription subscription : activityPackageSubscriptions) {
                ActivityPackage activityPackage = validateAndGetActivityPackage(subscription.getActivityPackageId());
                Set<Key> uniqueKeys = validateSubscriptionActivities(activityPackage.getId(), subscription);
                cart.getItems().add(new ShoppingCart.ItemBuilder()
                        .activityPackage(activityPackage)
                        .data(new ArrayList<>(uniqueKeys))
                        .build());
            }
        }
        shoppingCartDao.put(cart);
        return cart.calculate();
    }

    @ApiMethod(name = "carts.anonymousCartToUserCart", path = "carts/anonymous/{id}", httpMethod = ApiMethod.HttpMethod.PUT)
    public void anonymousCartToUserCart(User caller, @Named("id") String anonymousCartId) throws NotFoundException, BadRequestException, UnauthorizedException {
        JasifyEndpointUser jasUser = JasifyEndpoint.mustBeLoggedIn(caller);
        ShoppingCart shoppingCart = shoppingCartDao.get(anonymousCartId);
        if (shoppingCart == null) {
            throw new NotFoundException("Cart not found! (id=" + anonymousCartId + ")");
        }
        shoppingCart.setId(KeyUtil.userIdToCartId(jasUser.getUserId()));
        shoppingCartDao.put(shoppingCart);
        Memcache.delete(anonymousCartId);
    }

    @ApiMethod(name = "carts.getUserCart", path = "carts/user", httpMethod = ApiMethod.HttpMethod.GET)
    public ShoppingCart getUserCart(User caller) throws UnauthorizedException, ForbiddenException {
        JasifyEndpointUser jasUser = JasifyEndpoint.mustBeLoggedIn(caller);
        return shoppingCartDao.getUserCart(jasUser.getUserId()).calculate();
    }

    @ApiMethod(name = "carts.clearUserCart", path = "carts/user", httpMethod = ApiMethod.HttpMethod.DELETE)
    public ShoppingCart clearUserCart(User caller) throws UnauthorizedException, ForbiddenException {
        JasifyEndpointUser jasUser = JasifyEndpoint.mustBeLoggedIn(caller);
        String cartId = KeyUtil.userIdToCartId(jasUser.getUserId());
        ShoppingCart cleanCart = new ShoppingCart(cartId);
        shoppingCartDao.put(cleanCart);
        return cleanCart;
    }

    @ApiMethod(name = "carts.addUserActivity", path = "carts/user/activity/{activityId}", httpMethod = ApiMethod.HttpMethod.POST)
    public ShoppingCart addUserActivity(User caller, @Named("activityId") Key activityId) throws UnauthorizedException, ForbiddenException, NotFoundException, BadRequestException {
        JasifyEndpointUser jasUser = JasifyEndpoint.mustBeLoggedIn(caller);

        Activity activity = validateAndGetActivity(activityId);
        String cartId = KeyUtil.userIdToCartId(jasUser.getUserId());
        return shoppingCartDao.addItem(cartId, new ShoppingCart.ItemBuilder()
                .activity(activity)
                .build())
                .calculate();
    }

    @ApiMethod(name = "carts.addUserActivityPackage", path = "carts/user/activity-package/{activityPackageId}", httpMethod = ApiMethod.HttpMethod.POST)
    public ShoppingCart addUserActivityPackage(User caller, @Named("activityPackageId") Key activityPackageId, JasActivityPackageSubscription subscription) throws UnauthorizedException, ForbiddenException, NotFoundException, BadRequestException {
        JasifyEndpointUser jasUser = JasifyEndpoint.mustBeLoggedIn(caller);
        ActivityPackage activityPackage = validateAndGetActivityPackage(activityPackageId);

        Set<Key> uniqueKeys = validateSubscriptionActivities(activityPackageId, subscription);

        String cartId = KeyUtil.userIdToCartId(jasUser.getUserId());
        return shoppingCartDao.addItem(cartId, new ShoppingCart.ItemBuilder()
                .activityPackage(activityPackage)
                .data(new ArrayList<>(uniqueKeys))
                .build())
                .calculate();
    }

    @ApiMethod(name = "carts.removeItem", path = "carts/{cartId}/{ordinal}", httpMethod = ApiMethod.HttpMethod.DELETE)
    public ShoppingCart removeItem(User caller, @Named("cartId") String cartId, @Named("ordinal") Integer ordinal) throws UnauthorizedException, ForbiddenException, NotFoundException {
        JasifyEndpoint.mustBeLoggedIn(caller); //TODO: check that user owns this cart
        return shoppingCartDao.removeItem(cartId, ordinal);
    }

    @ApiMethod(name = "carts.getItem", path = "carts/{cartId}/{ordinal}", httpMethod = ApiMethod.HttpMethod.GET)
    public JasItemDetails getItem(User caller, @Named("cartId") String cartId, @Named("ordinal") Integer ordinal) throws UnauthorizedException, ForbiddenException, NotFoundException {
        JasifyEndpoint.mustBeLoggedIn(caller); //TODO: check that user owns this cart
        ShoppingCart cart = shoppingCartDao.get(cartId);
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

                    //noinspection unchecked
                    for (Key key : (Iterable<Key>) item.getData()) {
                        Activity activity = activityDao.get(key);
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
