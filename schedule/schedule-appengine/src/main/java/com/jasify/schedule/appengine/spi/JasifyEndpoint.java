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
import com.jasify.schedule.appengine.model.activity.Activity;
import com.jasify.schedule.appengine.model.activity.ActivityType;
import com.jasify.schedule.appengine.model.users.*;
import com.jasify.schedule.appengine.spi.auth.JasifyAuthenticator;
import com.jasify.schedule.appengine.spi.auth.JasifyEndpointUser;
import com.jasify.schedule.appengine.spi.dm.*;
import com.jasify.schedule.appengine.spi.transform.*;
import com.jasify.schedule.appengine.util.DigestUtil;
import com.jasify.schedule.appengine.util.TypeUtil;
import com.jasify.schedule.appengine.validators.UsernameValidator;
import com.jasify.schedule.appengine.validators.Validator;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slim3.datastore.Datastore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.*;
import java.util.concurrent.TimeUnit;
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
        transformers = {JasUserLoginTransformer.class, JasUserTransformer.class, JasKeyTransformer.class, JasActivityTypeTransformer.class, JasActivityTransformer.class},
        auth = @ApiAuth(allowCookieAuth = AnnotationBoolean.TRUE /* todo: I don't know another way :-( */),
        namespace = @ApiNamespace(ownerDomain = "jasify.com",
                ownerName = "Jasify",
                packagePath = ""))
public class JasifyEndpoint {
    private static final Logger log = LoggerFactory.getLogger(JasifyEndpoint.class);
    private static final Random random = new Random();

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

        com.jasify.schedule.appengine.model.users.User user = UserServiceFactory.getUserService().get(request.getUserId());
        if (user == null) throw new NotFoundException("No user");

        if (jasCaller.getUserId() == request.getUserId().getId()) { //change your own password
            ShortBlob dbPassword = Preconditions.checkNotNull(user.getPassword());
            String oldPassword = Preconditions.checkNotNull(StringUtils.trimToNull(request.getOldPassword()));
            if (!DigestUtil.verify(TypeUtil.toBytes(dbPassword), oldPassword)) {
                throw new ForbiddenException("Password does not match");
            }
        }

        log.info("User {} changing password of {}", caller, request.getUserId());
        UserServiceFactory.getUserService().setPassword(user, newPassword);
    }

    @ApiMethod(name = "auth.login", path = "auth/login", httpMethod = ApiMethod.HttpMethod.POST)
    public JasLoginResponse login(HttpServletRequest httpServletRequest, JasLoginRequest request) throws UnauthorizedException, BadRequestException {

        Preconditions.checkNotNull(request);

        if (StringUtils.isAnyBlank(request.getUsername(), request.getPassword())) {
            throw new BadRequestException("Login failed");
        }

        try {
            com.jasify.schedule.appengine.model.users.User user = UserServiceFactory.getUserService().login(request.getUsername(), request.getPassword());
            HttpUserSession userSession = new HttpUserSession(user).put(httpServletRequest);
            log.info("[{}] user={} logged in!", httpServletRequest.getRemoteAddr(), user.getName());

            JasLoginResponse response = new JasLoginResponse();
            response.setUserId(KeyFactory.keyToString(user.getId()));
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


    @ApiMethod(name = "users.query", path = "users", httpMethod = ApiMethod.HttpMethod.GET)
    public JasUserList getUsers(User caller,
                                @Nullable @Named("offset") Integer offset,
                                @Nullable @Named("limit") Integer limit,
                                @Nullable @Named("query") String query,
                                @Nullable @Named("field") String field,
                                @Nullable @Named("orderBy") String orderBy,
                                @Nullable @Named("order") Query.SortDirection order) throws UnauthorizedException, ForbiddenException {
        mustBeAdmin(caller);
        JasUserList users = new JasUserList();

        if (offset == null) offset = 0;
        if (limit == null) limit = Constants.DEFAULT_LIMIT;
        if (order == null) order = Constants.DEFAULT_ORDER;

        UserService svc = UserServiceFactory.getUserService();

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
        return checkFound(UserServiceFactory.getUserService().get(id));
    }

    @ApiMethod(name = "users.update", path = "users/{id}", httpMethod = ApiMethod.HttpMethod.PUT)
    public com.jasify.schedule.appengine.model.users.User updateUser(User caller, @Named("id") Key id, com.jasify.schedule.appengine.model.users.User user) throws NotFoundException, UnauthorizedException, ForbiddenException, FieldValueException {
        mustBeSameUserOrAdmin(caller, id);
        user.setId(id);
        try {
            return UserServiceFactory.getUserService().save(user);
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

        if (!(caller instanceof JasifyEndpointUser && ((JasifyEndpointUser) caller).isAdmin())) {
            request.getUser().setAdmin(false); //Only admin can create an admin
        }

        com.jasify.schedule.appengine.model.users.User ret;

        HttpSession session = servletRequest.getSession();
        if (session != null && session.getAttribute(HttpUserSession.OAUTH_USER_LOGIN_KEY) != null) {

            UserLogin login = (UserLogin) session.getAttribute(HttpUserSession.OAUTH_USER_LOGIN_KEY);
            session.removeAttribute(HttpUserSession.OAUTH_USER_LOGIN_KEY);
            ret = UserServiceFactory.getUserService().create(request.getUser(), login);

        } else {

            String pw = Preconditions.checkNotNull(StringUtils.trimToNull(request.getPassword()), "NULL password");
            ret = UserServiceFactory.getUserService().create(request.getUser(), pw);

        }

        if (caller == null)
            new HttpUserSession(ret).put(servletRequest); //login

        return ret;

    }



    @ApiMethod(name = "activityTypes.query", path = "activity-types", httpMethod = ApiMethod.HttpMethod.GET)
    public List<ActivityType> getActivityTypes(User caller,
                                               @Nullable @Named("partner") String partner) {
        ActivityType type = new ActivityType();
        type.setId(Datastore.createKey(ActivityType.class, 101));
        type.setName("Meta FIT");
        type.setDescription("This activity is called Meta FIT.\nIt is an activity with lorem ipsum no nono.\nAnd is lorem ipsum porem tutor.");
        return Collections.singletonList(type);
    }

    @ApiMethod(name = "activities.query", path = "activities", httpMethod = ApiMethod.HttpMethod.GET)
    public List<Activity> getActivities(User caller,
                                        @Nullable @Named("partner") String partner,
                                        @Nullable @Named("activityTypeId") Key activityTypeId,
                                        @Nullable @Named("fromDate") Date fromDate,
                                        @Nullable @Named("toDate") Date toDate,
                                        @Nullable @Named("offset") Integer offset,
                                        @Nullable @Named("limit") Integer limit) {

        if (fromDate == null) fromDate = new Date();
        if (toDate == null) toDate = new Date(fromDate.getTime() + TimeUnit.DAYS.toMillis(7));

        ArrayList<Activity> ret = new ArrayList<>();
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTime(fromDate);
        calendar.set(GregorianCalendar.HOUR_OF_DAY, 9);
        calendar.set(GregorianCalendar.MINUTE, 0);
        calendar.set(GregorianCalendar.SECOND, 0);
        calendar.set(GregorianCalendar.MILLISECOND, 0);
        int startId = random.nextInt(1000000);

        ActivityType activityType = getActivityTypes(null, null).get(0); //big hack :-)

        for (int i = 0; i < 20; i++) {
            Activity activity = new Activity();
            ret.add(activity);
            activity.setId(Datastore.createKey(ActivityType.class, startId + i));
            activity.getActivityTypeRef().setModel(activityType);
            activity.setCurrency("CHF");
            if (i % 3 == 0) {
                activity.setLocation("Some gym");
                activity.setDescription("This is Meta FIT at some gym.  It's going to be very nice.");
            } else {
                activity.setLocation("Another gym");
                activity.setDescription("This is Meta FIT at another gym.  It's going to be slow and steady.");
            }

            activity.setStart(calendar.getTime());
            if (random.nextInt() % 3 == 0) {
                calendar.add(GregorianCalendar.MINUTE, 45);
            } else {
                calendar.add(GregorianCalendar.HOUR, 1);
            }
            activity.setFinish(calendar.getTime());
            activity.setPrice(50.00);
            activity.setMaxSubscriptions(1 + random.nextInt(25));
            activity.setSubscriptionCount(random.nextInt(activity.getMaxSubscriptions()));

            calendar.set(GregorianCalendar.MINUTE, 0);
            calendar.add(GregorianCalendar.HOUR, random.nextInt(3) + 1);
        }

        if (limit == null) limit = 10;
        if (offset == null) offset = 0;


        if (offset > 0 || limit > 0) {
            if (offset < ret.size()) {
                if (limit <= 0) limit = ret.size();
                return new ArrayList<>(ret.subList(offset, Math.min(offset + limit, ret.size())));
            } else {
                return Collections.emptyList();
            }
        }

        return ret;
    }


}
