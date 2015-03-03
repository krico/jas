package com.jasify.schedule.appengine.spi;

import com.google.api.server.spi.auth.common.User;
import com.google.api.server.spi.config.*;
import com.google.api.server.spi.response.ForbiddenException;
import com.google.api.server.spi.response.NotFoundException;
import com.google.api.server.spi.response.UnauthorizedException;
import com.google.appengine.api.datastore.Key;
import com.jasify.schedule.appengine.spi.auth.JasifyAuthenticator;
import com.jasify.schedule.appengine.spi.auth.JasifyEndpointUser;
import com.jasify.schedule.appengine.spi.dm.JasApiInfo;
import com.jasify.schedule.appengine.spi.transform.*;

/**
 * @author krico
 * @since 27/12/14.
 */
@Api(name = "jasify", /* WARN: Its LAME but you have to copy & paste this section to all *Endpoint classes in this package */
        version = "v1",
        defaultVersion = AnnotationBoolean.TRUE,
        description = "Jasify Schedule",
        authenticators = {JasifyAuthenticator.class},
        authLevel = AuthLevel.NONE,
        transformers = {JasRepeatDetailsTransformer.class, JasAccountTransformer.class, JasTransactionTransformer.class, JasUserLoginTransformer.class, JasUserTransformer.class, JasKeyTransformer.class, JasActivityTypeTransformer.class, JasActivityTransformer.class, JasOrganizationTransformer.class, JasGroupTransformer.class, JasSubscriptionTransformer.class},
        auth = @ApiAuth(allowCookieAuth = AnnotationBoolean.TRUE /* todo: I don't know another way :-( */),
        namespace = @ApiNamespace(ownerDomain = "jasify.com",
                ownerName = "Jasify",
                packagePath = ""))
public class JasifyEndpoint {
    static JasifyEndpointUser mustBeLoggedIn(User caller) throws UnauthorizedException {
        if (caller instanceof JasifyEndpointUser) return (JasifyEndpointUser) caller;
        throw new UnauthorizedException("Only authenticated users can call this method");
    }

    static JasifyEndpointUser mustBeAdmin(User caller) throws ForbiddenException, UnauthorizedException {
        JasifyEndpointUser jasifyEndpointUser = mustBeLoggedIn(caller);
        if (jasifyEndpointUser.isAdmin()) return jasifyEndpointUser;
        throw new ForbiddenException("Must be admin");
    }

    static JasifyEndpointUser mustBeSameUserOrAdmin(User caller, Key userId) throws UnauthorizedException, ForbiddenException, NotFoundException {
        checkFound(userId);
        return mustBeSameUserOrAdmin(caller, userId.getId());
    }

    static JasifyEndpointUser mustBeSameUserOrAdmin(User caller, long userId) throws UnauthorizedException, ForbiddenException {
        JasifyEndpointUser jasifyEndpointUser = mustBeLoggedIn(caller);
        if (jasifyEndpointUser.getUserId() != userId) return mustBeAdmin(caller);
        return jasifyEndpointUser;
    }

    static <T> T checkFound(T e) throws NotFoundException {
        return checkFound(e, "Not found");
    }

    static <T> T checkFound(T e, String message) throws NotFoundException {
        if (e == null) throw new NotFoundException(message);
        return e;
    }

    @ApiMethod(name = "apiInfo", path = "api-info", httpMethod = ApiMethod.HttpMethod.GET)
    public JasApiInfo getApiInfo(User caller) {
        JasApiInfo info = new JasApiInfo(this);
        if (caller instanceof JasifyEndpointUser) {
            info.setAuthenticated(true);
            info.setAdmin(((JasifyEndpointUser) caller).isAdmin());
        }
        return info;
    }
}
