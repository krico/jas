package com.jasify.schedule.appengine.spi;

import com.google.api.server.spi.auth.common.User;
import com.google.api.server.spi.config.*;
import com.google.api.server.spi.response.BadRequestException;
import com.google.api.server.spi.response.ForbiddenException;
import com.google.api.server.spi.response.UnauthorizedException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.common.base.Preconditions;
import com.jasify.schedule.appengine.model.EntityNotFoundException;
import com.jasify.schedule.appengine.model.users.UserLogin;
import com.jasify.schedule.appengine.model.users.UserService;
import com.jasify.schedule.appengine.model.users.UserServiceFactory;
import com.jasify.schedule.appengine.spi.auth.JasifyAuthenticator;
import com.jasify.schedule.appengine.spi.auth.JasifyEndpointUser;
import com.jasify.schedule.appengine.spi.transform.JasUserLoginTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author krico
 * @since 27/12/14.
 */
@Api(name = "jasify",
        version = "v1",
        defaultVersion = AnnotationBoolean.TRUE,
        description = "Jasify Schedule",
        authenticators = {JasifyAuthenticator.class},
        authLevel = AuthLevel.NONE,
        transformers = {JasUserLoginTransformer.class},
        auth = @ApiAuth(allowCookieAuth = AnnotationBoolean.TRUE /* todo: I don't know another way :-( */),
        namespace = @ApiNamespace(ownerDomain = "jasify.com",
                ownerName = "Jasify",
                packagePath = ""))
public class JasifyEndpoint {
    private static final Logger log = LoggerFactory.getLogger(JasifyEndpoint.class);

    private UserService userService;

    static User mustBeLoggedIn(User caller) throws UnauthorizedException {
        if (caller == null) throw new UnauthorizedException("Only authenticated users can call this method");
        return caller;
    }

    static User mustBeSameUserOrAdmin(User caller, long userId) throws UnauthorizedException, ForbiddenException {
        caller = mustBeLoggedIn(caller);
        if (caller instanceof JasifyEndpointUser) {
            JasifyEndpointUser jasifyUser = (JasifyEndpointUser) caller;
            if (jasifyUser.isAdmin() || jasifyUser.getUserId() == userId) {
                return caller;
            }
        }
        throw new ForbiddenException("Must be admin or same user");
    }

    UserService getUserService() {
        if (userService == null) {
            userService = UserServiceFactory.getUserService();
        }
        return userService;
    }

    @ApiMethod(name = "apiInfo", path = "api-info", httpMethod = ApiMethod.HttpMethod.GET)
    public ApiInfo getApiInfo(User caller) {
        ApiInfo info = new ApiInfo();
        info.setVersion("1");
        if (caller != null) {
            info.setAuthenticated(true);
        }
        if (caller instanceof JasifyEndpointUser) {
            info.setAdmin(((JasifyEndpointUser) caller).isAdmin());
        }
        return info;
    }

    @ApiMethod(name = "userLogins.list", path = "user-logins/{userId}", httpMethod = ApiMethod.HttpMethod.GET)
    public List<UserLogin> listLogins(User caller, @Named("userId") long userId) throws UnauthorizedException, ForbiddenException {
        mustBeSameUserOrAdmin(caller, userId);
        return getUserService().getUserLogins(userId);
    }

    @ApiMethod(name = "userLogins.remove", path = "user-logins/{loginId}", httpMethod = ApiMethod.HttpMethod.DELETE)
    public void removeLogin(User caller, @Named("loginId") String loginId) throws UnauthorizedException, BadRequestException, ForbiddenException {
        caller = mustBeLoggedIn(caller);

        Key loginKey = KeyFactory.stringToKey(Preconditions.checkNotNull(loginId));
        UserLogin login = getUserService().getLogin(loginKey);
        if (login == null) {
            //nothing to do
            return;
        }
        Key userKey = Preconditions.checkNotNull(login.getUserRef().getKey());
        caller = mustBeSameUserOrAdmin(caller, userKey.getId());

        try {
            getUserService().removeLogin(loginKey);
        } catch (EntityNotFoundException e) {
            log.error("Failed to remove login user: {} login: {}", userKey, login);
            throw new BadRequestException("Failed to remove login");
        }
        log.info("Removed user: {}, login: {}", caller, login);
    }

}
