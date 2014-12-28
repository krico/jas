package com.jasify.schedule.appengine.endpoints;

import com.google.api.server.spi.auth.common.User;
import com.google.api.server.spi.config.*;
import com.google.api.server.spi.response.ForbiddenException;
import com.google.api.server.spi.response.UnauthorizedException;
import com.jasify.schedule.appengine.model.users.UserLogin;
import com.jasify.schedule.appengine.model.users.UserServiceFactory;

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
        authLevel = AuthLevel.REQUIRED,
        namespace = @ApiNamespace(ownerDomain = "jasify.com",
                ownerName = "Jasify",
                packagePath = ""))
public class JasifyEndpoint {

    private static User checkLoggedIn(User caller) throws UnauthorizedException {
        if (caller == null) throw new UnauthorizedException("Only authenticated users can call this method");
        return caller;
    }

    private static User checkAdminOrSameUser(User caller, long userId) throws UnauthorizedException, ForbiddenException {
        caller = checkLoggedIn(caller);
        if (caller instanceof JasifyUser) {
            JasifyUser jasifyUser = (JasifyUser) caller;
            if (jasifyUser.isAdmin() || jasifyUser.getUserId() == userId) {
                return caller;
            }
        }
        throw new ForbiddenException("Must be admin or same user");
    }

    @ApiMethod(name = "settings", authLevel = AuthLevel.OPTIONAL)
    public Settings settings(User caller) {
        Settings settings = new Settings();
        settings.setVersion("1");
        settings.setAuthenticated(caller != null);
        return settings;
    }

    @ApiMethod(name = "logins.list")
    public List<UserLogin> listLogins(User caller, @Named("userId") long userId) throws UnauthorizedException, ForbiddenException {
        checkAdminOrSameUser(caller, userId);
        return UserServiceFactory.getUserService().getUserLogins(userId);
    }


}
