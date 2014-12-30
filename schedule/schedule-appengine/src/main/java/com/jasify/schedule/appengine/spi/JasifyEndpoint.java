package com.jasify.schedule.appengine.spi;

import com.google.api.client.repackaged.com.google.common.base.Preconditions;
import com.google.api.server.spi.auth.common.User;
import com.google.api.server.spi.config.*;
import com.google.api.server.spi.response.BadRequestException;
import com.google.api.server.spi.response.ForbiddenException;
import com.google.api.server.spi.response.UnauthorizedException;
import com.jasify.schedule.appengine.model.EntityNotFoundException;
import com.jasify.schedule.appengine.model.users.UserLogin;
import com.jasify.schedule.appengine.model.users.UserService;
import com.jasify.schedule.appengine.model.users.UserServiceFactory;
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
        if (caller instanceof JasifyUser) {
            JasifyUser jasifyUser = (JasifyUser) caller;
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

    @ApiMethod(name = "api.info", httpMethod = ApiMethod.HttpMethod.GET)
    public JasifyInfo apiInfo(User caller) {
        JasifyInfo info = new JasifyInfo();
        info.setVersion("1");
        if (caller != null) {
            info.setAuthenticated(true);
        }
        if (caller instanceof JasifyUser) {
            info.setAdmin(((JasifyUser) caller).isAdmin());
        }
        return info;
    }

    @ApiMethod(name = "logins.list")
    public List<UserLogin> listLogins(User caller, @Named("userId") long userId) throws UnauthorizedException, ForbiddenException {
        mustBeSameUserOrAdmin(caller, userId);
        return getUserService().getUserLogins(userId);
    }

    @ApiMethod(name = "logins.remove")
    public void removeLogin(User caller, @Named("userId") long userId, @Named("loginId") long loginId) throws UnauthorizedException, BadRequestException, ForbiddenException {
        caller = mustBeSameUserOrAdmin(caller, userId);

        com.jasify.schedule.appengine.model.users.User user = Preconditions.checkNotNull(userService.get(userId));
        UserLogin login = getUserService().getLogin(userId, loginId);
        if (login == null) {
            //nothing to do
            return;
        }
        try {
            getUserService().removeLogin(user, login);
        } catch (EntityNotFoundException e) {
            log.error("Failed to remove login user: {} login: {}", user, login);
            throw new BadRequestException("Failed to remove login");
        }
        log.info("Removed user: {}, login: {}", caller, login);
    }


}
