package com.jasify.schedule.appengine.spi;

import com.google.api.server.spi.auth.common.User;
import com.google.api.server.spi.config.*;
import com.google.api.server.spi.response.BadRequestException;
import com.google.api.server.spi.response.ForbiddenException;
import com.google.api.server.spi.response.NotFoundException;
import com.google.api.server.spi.response.UnauthorizedException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.common.base.Preconditions;
import com.jasify.schedule.appengine.model.EntityNotFoundException;
import com.jasify.schedule.appengine.model.users.UserLogin;
import com.jasify.schedule.appengine.model.users.UserServiceFactory;
import com.jasify.schedule.appengine.spi.auth.JasifyAuthenticator;
import com.jasify.schedule.appengine.spi.transform.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static com.jasify.schedule.appengine.spi.JasifyEndpoint.mustBeLoggedIn;
import static com.jasify.schedule.appengine.spi.JasifyEndpoint.mustBeSameUserOrAdmin;

/**
 * @author krico
 * @since 11/01/15.
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
public class UserLoginEndpoint {
    private static final Logger log = LoggerFactory.getLogger(UserLoginEndpoint.class);

    @ApiMethod(name = "userLogins.list", path = "user-logins/{userId}", httpMethod = ApiMethod.HttpMethod.GET)
    public List<UserLogin> listLogins(User caller, @Named("userId") Key userId) throws UnauthorizedException, ForbiddenException, NotFoundException {
        mustBeSameUserOrAdmin(caller, userId);
        return UserServiceFactory.getUserService().getUserLogins(userId);
    }

    @ApiMethod(name = "userLogins.remove", path = "user-logins/{loginId}", httpMethod = ApiMethod.HttpMethod.DELETE)
    public void removeLogin(User caller, @Named("loginId") String loginId) throws UnauthorizedException, BadRequestException, ForbiddenException, EntityNotFoundException {
        caller = mustBeLoggedIn(caller);

        Key loginKey = KeyFactory.stringToKey(Preconditions.checkNotNull(loginId));
        UserLogin login = UserServiceFactory.getUserService().getLogin(loginKey);
        if (login == null) {
            //nothing to do
            return;
        }
        Key userKey = Preconditions.checkNotNull(login.getUserRef().getKey());
        caller = mustBeSameUserOrAdmin(caller, userKey.getId());

        UserServiceFactory.getUserService().removeLogin(loginKey);
        log.info("Removed user: {}, login: {}", caller, login);
    }
}
