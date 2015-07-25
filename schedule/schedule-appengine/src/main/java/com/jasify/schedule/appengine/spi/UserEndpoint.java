package com.jasify.schedule.appengine.spi;

import com.google.api.server.spi.auth.common.User;
import com.google.api.server.spi.config.*;
import com.google.api.server.spi.response.ForbiddenException;
import com.google.api.server.spi.response.NotFoundException;
import com.google.api.server.spi.response.UnauthorizedException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Query;
import com.google.common.base.Preconditions;
import com.jasify.schedule.appengine.Constants;
import com.jasify.schedule.appengine.dao.common.OrganizationDao;
import com.jasify.schedule.appengine.http.HttpUserSession;
import com.jasify.schedule.appengine.model.EntityNotFoundException;
import com.jasify.schedule.appengine.model.FieldValueException;
import com.jasify.schedule.appengine.model.users.*;
import com.jasify.schedule.appengine.spi.auth.JasifyAuthenticator;
import com.jasify.schedule.appengine.spi.auth.JasifyEndpointUser;
import com.jasify.schedule.appengine.spi.dm.JasAddUserRequest;
import com.jasify.schedule.appengine.spi.dm.JasUserList;
import com.jasify.schedule.appengine.spi.transform.*;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.regex.Pattern;

import static com.jasify.schedule.appengine.spi.JasifyEndpoint.*;

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
public class UserEndpoint {

    private final OrganizationDao organizationDao = new OrganizationDao();

    /*
     * A reminder on REST
     * GET /users       (java:getUsers, js: users.query)
     * GET /users/{id}  (java:getUser, js: users.get) 404 if not found
     * PUT /users/{id}  (java:updateUser, js: users.update) 404 if not found
     * POST /users      (java:addUser, js: users.add)
     * DELETE /users/{id} (java: removeUser, js: users.remove) 404 if not found
     */

    @ApiMethod(name = "users.query", path = "users", httpMethod = ApiMethod.HttpMethod.GET)
    public JasUserList getUsers(User caller,
                                @Nullable @Named("offset") Integer offset,
                                @Nullable @Named("limit") Integer limit,
                                @Nullable @Named("query") String query,
                                @Nullable @Named("field") String field,
                                @Nullable @Named("orderBy") String orderBy,
                                @Nullable @Named("order") Query.SortDirection order) throws UnauthorizedException, ForbiddenException {
        mustBeAdminOrOrgMember(caller); // TODO: Once we have a clean way to get organization clients this should be changed to mustBeOrgAdmin(callser);
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
    public com.jasify.schedule.appengine.model.users.User addUser(User caller, JasAddUserRequest request, HttpServletRequest servletRequest) throws UserLoginExistsException, UsernameExistsException, EmailExistsException {

        Preconditions.checkNotNull(request);
        Preconditions.checkNotNull(request.getUser());

        if (!(caller instanceof JasifyEndpointUser && ((JasifyEndpointUser) caller).isAdmin())) {
            request.getUser().setAdmin(false); //Only admin can create an admin
        }

        com.jasify.schedule.appengine.model.users.User user;

        HttpSession session = servletRequest.getSession();
        if (session != null && session.getAttribute(HttpUserSession.OAUTH_USER_LOGIN_KEY) != null) {
            UserLogin login = (UserLogin) session.getAttribute(HttpUserSession.OAUTH_USER_LOGIN_KEY);
            session.removeAttribute(HttpUserSession.OAUTH_USER_LOGIN_KEY);
            user = UserServiceFactory.getUserService().create(request.getUser(), login);
        } else {
            String pw = Preconditions.checkNotNull(StringUtils.trimToNull(request.getPassword()), "NULL password");
            user = UserServiceFactory.getUserService().create(request.getUser(), pw);
        }


        if (caller == null) {
            boolean isOrgMember = organizationDao.isUserMemberOfAnyOrganization(user.getId());
            new HttpUserSession(user, isOrgMember).put(servletRequest); //login
        }

        return user;

    }
}
