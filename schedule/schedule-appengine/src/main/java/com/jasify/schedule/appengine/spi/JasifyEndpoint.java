package com.jasify.schedule.appengine.spi;

import com.google.api.server.spi.auth.common.User;
import com.google.api.server.spi.config.*;
import com.google.api.server.spi.response.*;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.ShortBlob;
import com.google.common.base.Preconditions;
import com.jasify.schedule.appengine.model.EntityNotFoundException;
import com.jasify.schedule.appengine.model.users.UserLogin;
import com.jasify.schedule.appengine.model.users.UserService;
import com.jasify.schedule.appengine.model.users.UserServiceFactory;
import com.jasify.schedule.appengine.spi.auth.JasifyAuthenticator;
import com.jasify.schedule.appengine.spi.auth.JasifyEndpointUser;
import com.jasify.schedule.appengine.spi.dm.JasChangePasswordRequest;
import com.jasify.schedule.appengine.spi.transform.JasUserLoginTransformer;
import com.jasify.schedule.appengine.util.DigestUtil;
import com.jasify.schedule.appengine.util.TypeUtil;
import com.jasify.schedule.appengine.validators.UsernameValidator;
import com.jasify.schedule.appengine.validators.Validator;
import org.apache.commons.lang3.StringUtils;
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
    private Validator<String> usernameValidator;

    static JasifyEndpointUser mustBeLoggedIn(User caller) throws UnauthorizedException {
        if (caller instanceof JasifyEndpointUser) return (JasifyEndpointUser) caller;
        throw new UnauthorizedException("Only authenticated users can call this method");
    }

    static JasifyEndpointUser mustBeSameUserOrAdmin(User caller, long userId) throws UnauthorizedException, ForbiddenException {
        JasifyEndpointUser jasifyEndpointUser = mustBeLoggedIn(caller);
        if (jasifyEndpointUser.isAdmin() || jasifyEndpointUser.getUserId() == userId) {
            return jasifyEndpointUser;
        }
        throw new ForbiddenException("Must be admin or same user");
    }

    Validator<String> getUsernameValidator() {
        if (usernameValidator == null) {
            usernameValidator = UsernameValidator.INSTANCE;
        }
        return usernameValidator;
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
        if (caller instanceof JasifyEndpointUser) {
            info.setAuthenticated(true);
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
    public void removeLogin(User caller, @Named("loginId") String loginId) throws UnauthorizedException, BadRequestException, ForbiddenException, EntityNotFoundException {
        caller = mustBeLoggedIn(caller);

        Key loginKey = KeyFactory.stringToKey(Preconditions.checkNotNull(loginId));
        UserLogin login = getUserService().getLogin(loginKey);
        if (login == null) {
            //nothing to do
            return;
        }
        Key userKey = Preconditions.checkNotNull(login.getUserRef().getKey());
        caller = mustBeSameUserOrAdmin(caller, userKey.getId());

        getUserService().removeLogin(loginKey);
        log.info("Removed user: {}, login: {}", caller, login);
    }

    @ApiMethod(name = "username.check", path = "username-check/{username}", httpMethod = ApiMethod.HttpMethod.GET)
    public void checkUsername(@Named("username") String username) throws ConflictException {
        Preconditions.checkNotNull(StringUtils.trimToNull(username));
        List<String> reasons = getUsernameValidator().validate(username);
        if (!reasons.isEmpty()) {
            throw new ConflictException(StringUtils.join(reasons, ", "));
        }
    }

    @ApiMethod(name = "auth.changePassword", path = "auth/change-password", httpMethod = ApiMethod.HttpMethod.POST)
    public void changePassword(User caller, JasChangePasswordRequest request)
            throws UnauthorizedException, ForbiddenException, BadRequestException, NotFoundException, EntityNotFoundException {
        Preconditions.checkNotNull(request);
        JasifyEndpointUser jasCaller = mustBeSameUserOrAdmin(caller, request.getUserId());

        String newPassword = Preconditions.checkNotNull(StringUtils.trimToNull(request.getNewPassword()));

        com.jasify.schedule.appengine.model.users.User user = getUserService().get(request.getUserId());
        if (user == null) throw new NotFoundException("No user");

        if (jasCaller.getUserId() == request.getUserId()) { //change your own password
            ShortBlob dbPassword = Preconditions.checkNotNull(user.getPassword());
            String oldPassword = Preconditions.checkNotNull(StringUtils.trimToNull(request.getOldPassword()));
            if (!DigestUtil.verify(TypeUtil.toBytes(dbPassword), oldPassword)) {
                throw new ForbiddenException("Password does not match");
            }
        }

        log.info("User {} changing password of {}", caller, request.getUserId());
        userService.setPassword(user, newPassword);
    }

}
