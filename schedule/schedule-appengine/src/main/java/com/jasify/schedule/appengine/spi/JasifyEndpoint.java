package com.jasify.schedule.appengine.spi;

import com.google.api.server.spi.auth.common.User;
import com.google.api.server.spi.config.*;
import com.google.api.server.spi.response.*;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.ShortBlob;
import com.google.common.base.Preconditions;
import com.jasify.schedule.appengine.Constants;
import com.jasify.schedule.appengine.http.HttpUserSession;
import com.jasify.schedule.appengine.model.EntityNotFoundException;
import com.jasify.schedule.appengine.model.FieldValueException;
import com.jasify.schedule.appengine.model.UserContext;
import com.jasify.schedule.appengine.model.UserSession;
import com.jasify.schedule.appengine.model.users.*;
import com.jasify.schedule.appengine.spi.auth.JasifyAuthenticator;
import com.jasify.schedule.appengine.spi.auth.JasifyEndpointUser;
import com.jasify.schedule.appengine.spi.dm.*;
import com.jasify.schedule.appengine.spi.transform.JasKeyTransformer;
import com.jasify.schedule.appengine.spi.transform.JasUserLoginTransformer;
import com.jasify.schedule.appengine.spi.transform.JasUserTransformer;
import com.jasify.schedule.appengine.util.DigestUtil;
import com.jasify.schedule.appengine.util.TypeUtil;
import com.jasify.schedule.appengine.validators.UsernameValidator;
import com.jasify.schedule.appengine.validators.Validator;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.regex.Pattern;

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
        transformers = {JasUserLoginTransformer.class, JasUserTransformer.class, JasKeyTransformer.class},
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

    @ApiMethod(name = "auth.login", path = "auth/login", httpMethod = ApiMethod.HttpMethod.POST)
    public JasLoginResponse login(HttpServletRequest httpServletRequest, JasLoginRequest request) throws UnauthorizedException, BadRequestException {

        Preconditions.checkNotNull(request);

        if (StringUtils.isAnyBlank(request.getUsername(), request.getPassword())) {
            throw new BadRequestException("Login failed");
        }

        try {
            com.jasify.schedule.appengine.model.users.User user = getUserService().login(request.getUsername(), request.getPassword());
            HttpUserSession userSession = new HttpUserSession(user).put(httpServletRequest);
            log.info("[{}] user={} logged in!", httpServletRequest.getRemoteAddr(), user.getName());

            JasLoginResponse response = new JasLoginResponse();
            response.setUserId(userSession.getUserId());
            response.setSessionId(userSession.getSessionId());
            response.setName(user.getName());
            response.setAdmin(user.isAdmin());
            return response;
        } catch (LoginFailedException e) {
            log.info("[{}] user={} login failed!", httpServletRequest.getRemoteAddr(), request.getUsername(), e);
            throw new UnauthorizedException("Login failed");
        }
    }

    @ApiMethod(name = "auth.logout", path = "auth/logout", httpMethod = ApiMethod.HttpMethod.POST)
    public void logout(User caller) {
        UserSession userSession = UserContext.getCurrentUser();
        if (userSession != null) {
            userSession.invalidate();
        }
        log.info("Logged out {}", caller);
    }



    /*
    TODO: user.$save, User.get User.save, Users.query,
     */

    /*
     * A reminder on REST
     * GET /users       (java:getUsers, js: users.query)
     * GET /users/{id}  (java:getUser, js: users.get) 404 if not found
     * PUT /users/{id}  (java:updateUser, js: users.update) 404 if not found
     * POST /users      (java:addUser, js: users.add)
     * DELETE /users/{id} (java: removeUser, js: users.remove) 404 if not found
     */

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


    @ApiMethod(name = "users.query", path = "users", httpMethod = ApiMethod.HttpMethod.GET)
    public JasUserList getUsers(User caller,
                                @Named("offset") Integer offset,
                                @Named("limit") Integer limit,
                                @Named("query") String query,
                                @Named("field") String field,
                                @Named("orderBy") String orderBy,
                                @Named("order") Query.SortDirection order) throws UnauthorizedException, ForbiddenException {
        mustBeAdmin(caller);
        JasUserList users = new JasUserList();

        if (offset == null) offset = 0;
        if (limit == null) limit = Constants.DEFAULT_LIMIT;
        if (order == null) order = Constants.DEFAULT_ORDER;

        UserService svc = getUserService();

        users.setTotal(svc.getTotalUsers());

        if ("email".equals(field)) {
            users.addAll(svc.searchByEmail(query == null ? null : Pattern.compile(query), order, offset, limit));
        } else if ("name".equals(field)) {
            users.addAll(svc.searchByName(query == null ? null : Pattern.compile(query), order, offset, limit));
        } else {
            users.addAll(svc.list(order, offset, limit));
        }
        return users;
    }

    @ApiMethod(name = "users.get", path = "users/{id}", httpMethod = ApiMethod.HttpMethod.GET)
    public com.jasify.schedule.appengine.model.users.User getUser(User caller, @Named("id") Key id) throws NotFoundException, UnauthorizedException, ForbiddenException {
        mustBeSameUserOrAdmin(caller, id);
        return checkFound(getUserService().get(id));
    }

    @ApiMethod(name = "users.update", path = "users/{id}", httpMethod = ApiMethod.HttpMethod.PUT)
    public com.jasify.schedule.appengine.model.users.User updateUser(User caller, @Named("id") Key id, com.jasify.schedule.appengine.model.users.User user) throws NotFoundException, UnauthorizedException, ForbiddenException, FieldValueException {
        mustBeSameUserOrAdmin(caller, id);
        user.setId(id);
        try {
            return getUserService().save(user);
        } catch (EntityNotFoundException e) {
            throw new NotFoundException("User not found");
        }
    }

    /* TODO: not supported
    @ApiMethod(name = "users.remove", path = "users/{id}", httpMethod = ApiMethod.HttpMethod.DELETE)
    public void removeUser(User caller, @Named("id") Key id) throws NotFoundException, UnauthorizedException, ForbiddenException {
        mustBeAdmin(caller);
        checkFound(id);
    }
    */

    @ApiMethod(name = "users.add", path = "users", httpMethod = ApiMethod.HttpMethod.POST)
    public com.jasify.schedule.appengine.model.users.User addUser(User caller, JasAddUserRequest request, HttpServletRequest servletRequest) throws UserLoginExistsException, UsernameExistsException {

        Preconditions.checkNotNull(request);
        Preconditions.checkNotNull(request.getUser());

        HttpSession session = servletRequest.getSession();
        if (session != null && session.getAttribute(HttpUserSession.OAUTH_USER_LOGIN_KEY) != null) {

            UserLogin login = (UserLogin) session.getAttribute(HttpUserSession.OAUTH_USER_LOGIN_KEY);
            session.removeAttribute(HttpUserSession.OAUTH_USER_LOGIN_KEY);
            return userService.create(request.getUser(), login);

        } else {

            String pw = Preconditions.checkNotNull(StringUtils.trimToNull(request.getPassword()), "NULL password");
            return userService.create(request.getUser(), pw);

        }
    }

}
