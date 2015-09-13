package com.jasify.schedule.appengine.spi;

import com.google.api.client.http.GenericUrl;
import com.google.api.server.spi.auth.common.User;
import com.google.api.server.spi.config.*;
import com.google.api.server.spi.response.*;
import com.google.appengine.api.datastore.ShortBlob;
import com.google.common.base.Preconditions;
import com.jasify.schedule.appengine.communication.Communicator;
import com.jasify.schedule.appengine.dao.common.OrganizationDao;
import com.jasify.schedule.appengine.http.HttpUserSession;
import com.jasify.schedule.appengine.model.EntityNotFoundException;
import com.jasify.schedule.appengine.model.UserContext;
import com.jasify.schedule.appengine.model.UserSession;
import com.jasify.schedule.appengine.model.history.HistoryHelper;
import com.jasify.schedule.appengine.model.users.*;
import com.jasify.schedule.appengine.oauth2.*;
import com.jasify.schedule.appengine.spi.auth.JasifyAuthenticator;
import com.jasify.schedule.appengine.spi.auth.JasifyEndpointUser;
import com.jasify.schedule.appengine.spi.dm.*;
import com.jasify.schedule.appengine.spi.transform.*;
import com.jasify.schedule.appengine.util.DigestUtil;
import com.jasify.schedule.appengine.util.TypeUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

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
        transformers = {
                /* one per line in alphabetical order to avoid merge conflicts */
                JasAccountTransformer.class,
                JasActivityPackageTransformer.class,
                JasActivityTransformer.class,
                JasActivityTypeTransformer.class,
                JasGroupTransformer.class,
                JasHistoryTransformer.class,
                JasKeyTransformer.class,
                JasOrganizationTransformer.class,
                JasPaymentTransformer.class,
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
public class AuthEndpoint {
    private static final Logger log = LoggerFactory.getLogger(AuthEndpoint.class);
    private final OrganizationDao organizationDao = new OrganizationDao();

    @ApiMethod(name = "auth.changePassword", path = "auth/change-password", httpMethod = ApiMethod.HttpMethod.POST)
    public void changePassword(User caller, HttpServletRequest httpServletRequest, JasChangePasswordRequest request)
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

        HistoryHelper.addPasswordChanged(user, httpServletRequest);
    }

    @ApiMethod(name = "auth.login", path = "auth/login", httpMethod = ApiMethod.HttpMethod.POST)
    public JasLoginResponse login(HttpServletRequest httpServletRequest, JasLoginRequest request) throws UnauthorizedException, BadRequestException {

        Preconditions.checkNotNull(request);

        if (StringUtils.isAnyBlank(request.getUsername(), request.getPassword())) {
            throw new BadRequestException("Login failed");
        }

        try {
            com.jasify.schedule.appengine.model.users.User user = UserServiceFactory.getUserService().login(request.getUsername(), request.getPassword());
            boolean isOrgMember = organizationDao.isUserMemberOfAnyOrganization(user.getId());
            HttpUserSession userSession = new HttpUserSession(user, isOrgMember).put(httpServletRequest);
            log.info("[{}] user={} logged in!", httpServletRequest.getRemoteAddr(), user.getName());

            HistoryHelper.addLogin(user, httpServletRequest);

            return new JasLoginResponse(user, userSession);
        } catch (LoginFailedException e) {
            log.info("[{}] user={} login failed!", httpServletRequest.getRemoteAddr(), request.getUsername(), e);
            HistoryHelper.addLoginFailed(request.getUsername(), httpServletRequest);
            throw new UnauthorizedException("Login failed");
        }
    }

    @ApiMethod(name = "auth.restore", path = "auth/restore", httpMethod = ApiMethod.HttpMethod.GET)
    public JasLoginResponse restore() throws UnauthorizedException, BadRequestException {
        UserSession userSession = UserContext.getCurrentUser();
        if (userSession instanceof HttpUserSession) {
            com.jasify.schedule.appengine.model.users.User user = UserServiceFactory.getUserService().get(userSession.getUserId());
            //TODO: Do we want to HistoryHelper.addRestore here?
            return new JasLoginResponse(user, userSession);
        }

        log.info("Restore failed");
        return new JasLoginResponse(true, "No session present");
    }

    @ApiMethod(name = "auth.logout", path = "auth/logout", httpMethod = ApiMethod.HttpMethod.POST)
    public void logout(User caller, HttpServletRequest httpServletRequest) {
        UserSession userSession = UserContext.getCurrentUser();
        if (userSession != null) {
            HistoryHelper.addLogout(httpServletRequest);
            userSession.invalidate();
        }
        log.info("Logged out {}", caller);
    }

    @ApiMethod(name = "auth.providerAuthorize", path = "auth/provider-authorize", httpMethod = ApiMethod.HttpMethod.POST)
    public JasProviderAuthorizeResponse providerAuthorize(JasProviderAuthorizeRequest request) {

        OAuth2ProviderEnum provider = Preconditions.checkNotNull(request.getProvider());
        GenericUrl baseUrl = new GenericUrl(Preconditions.checkNotNull(request.getBaseUrl()));
        String data = StringUtils.trimToEmpty(request.getData());

        JasProviderAuthorizeResponse response = new JasProviderAuthorizeResponse();

        GenericUrl authorizeUrl = OAuth2ServiceFactory
                .getOAuth2Service()
                .createCodeRequestUrl(baseUrl, provider, data);

        response.setAuthorizeUrl(authorizeUrl.build());

        return response;
    }

    @ApiMethod(name = "auth.providerAuthenticate", path = "auth/provider-authenticate", httpMethod = ApiMethod.HttpMethod.POST)
    public JasProviderAuthenticateResponse providerAuthenticate(User authenticatedUser, HttpServletRequest httpServletRequest, JasProviderAuthenticateRequest request) throws BadRequestException, NotFoundException, ConflictException {
        OAuth2Info oAuth2Info;

        try {
            OAuth2Service oAuth2Service = OAuth2ServiceFactory.getOAuth2Service();
            OAuth2UserToken userToken = oAuth2Service.fetchUserToken(new GenericUrl(request.getCallbackUrl()));
            oAuth2Info = oAuth2Service.fetchInfo(userToken);
        } catch (OAuth2Exception e) {
            log.info("OAuth authentication failed", e);
            throw new BadRequestException(e.getMessage());
        }

        UserService userService = UserServiceFactory.getUserService();

        if (authenticatedUser != null) {
            JasifyEndpointUser jasifyUser = (JasifyEndpointUser) authenticatedUser;
            UserLogin userLogin = new UserLogin(oAuth2Info);
            try {
                userService.addLogin(userService.get(jasifyUser.getUserId()), userLogin);
            } catch (EntityNotFoundException e) {
                log.error("Failed to login with oauth: " + jasifyUser.getUserId(), e);

                HistoryHelper.addLoginFailed(userLogin, httpServletRequest, Objects.toString(e));

                throw new NotFoundException(e);
            } catch (UserLoginExistsException e) {
                /* Maybe it's this user?
                   This seems to happen if the browser didn't reload but the session still existed, in this case the
                   logged in user could be the owner of this user login. See #245
                 */
                boolean found = false;
                for (UserLogin login : userService.getUserLogins(jasifyUser.getUserId())) {
                    found = StringUtils.equals(login.getProvider(), userLogin.getProvider()) &&
                            StringUtils.equals(login.getUserId(), userLogin.getUserId());
                    if (found) break;
                }
                if (!found) {
                    log.error("Failed to login with oauth: " + jasifyUser.getUserId() + " UserLogin exists");
                    HistoryHelper.addLoginFailed(userLogin, httpServletRequest, "UserLogin exists");
                    throw new ConflictException("UserLogin exists");
                }
            }

            HistoryHelper.addLogin(userLogin, httpServletRequest, "Previously authenticated");
            //TODO: I'm not sure this works
            return new JasProviderAuthenticateResponse(Objects.toString(oAuth2Info.getState()));
        } else {
            com.jasify.schedule.appengine.model.users.User existingUser = userService.findByLogin(oAuth2Info.getProvider().name(), oAuth2Info.getUserId());
            UserLogin userLogin = new UserLogin(oAuth2Info);
            String comment;
            if (existingUser == null) {
                log.info("Creating new user={} authenticated via oauth", userLogin.getEmail());
                comment = "Post user creation";
                try {
                    existingUser = userService.create(new com.jasify.schedule.appengine.model.users.User(userLogin), userLogin);
                    HistoryHelper.addAccountCreated(existingUser, userLogin, httpServletRequest);
                } catch (EmailExistsException e) {
                    String reason = "Email exists";
                    HistoryHelper.addAccountCreationFailed(userLogin, httpServletRequest, reason);
                    throw new ConflictException(reason);
                } catch (UsernameExistsException e) {
                    String reason = "Username exists";
                    HistoryHelper.addAccountCreationFailed(userLogin, httpServletRequest, reason);
                    throw new ConflictException(reason);
                } catch (UserLoginExistsException e) {
                    String reason = "UserLogin exists";
                    HistoryHelper.addAccountCreationFailed(userLogin, httpServletRequest, reason);
                    throw new ConflictException(reason);
                }
            } else {
                comment = "OAuth login";
            }

            boolean isOrgMember = organizationDao.isUserMemberOfAnyOrganization(existingUser.getId());
            //LOGIN!
            HttpUserSession userSession = new HttpUserSession(existingUser, isOrgMember).put(httpServletRequest);//todo: simulate log in
            HistoryHelper.addLogin(userLogin, httpServletRequest, comment);
            return new JasProviderAuthenticateResponse(existingUser, userSession, Objects.toString(oAuth2Info.getState()));
        }
    }


    @ApiMethod(name = "auth.forgotPassword", path = "auth/forgot-password", httpMethod = ApiMethod.HttpMethod.POST)
    public void forgotPassword(HttpServletRequest httpServletRequest, JasForgotPasswordRequest request) throws BadRequestException, NotFoundException {
        Preconditions.checkNotNull(request.getEmail(), "email");
        Preconditions.checkNotNull(request.getUrl(), "url");
        PasswordRecovery recovery;
        try {
            recovery = UserServiceFactory.getUserService().registerPasswordRecovery(request.getEmail());
            HistoryHelper.addForgottenPassword(recovery, httpServletRequest);
        } catch (EntityNotFoundException e) {
            HistoryHelper.addForgottenPasswordFailed(request.getEmail(), httpServletRequest);
            throw new NotFoundException(e);
        }
        notify(recovery);
    }

    @ApiMethod(name = "auth.recoverPassword", path = "auth/recover-password", httpMethod = ApiMethod.HttpMethod.POST)
    public void recoverPassword(HttpServletRequest httpServletRequest, JasRecoverPasswordRequest request) throws BadRequestException, NotFoundException {
        String code = Preconditions.checkNotNull(request.getCode(), "code");
        String newPassword = Preconditions.checkNotNull(request.getNewPassword(), "newPassword");
        try {
            PasswordRecovery recovery = UserServiceFactory.getUserService().recoverPassword(code, newPassword);
            HistoryHelper.addRecoveredPassword(recovery, httpServletRequest);
        } catch (EntityNotFoundException e) {
            throw new NotFoundException(e);
        }
    }


    /**
     * @param recovery holding code and user
     */
    private void notify(PasswordRecovery recovery) {
        try {
            Communicator.notifyOfPasswordRecovery(recovery.getUserRef().getModel(), recovery);
        } catch (Exception e) {
            log.warn("Failed to notify", e);
        }
    }
}
