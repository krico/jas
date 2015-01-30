package com.jasify.schedule.appengine.spi;

import com.google.api.client.http.GenericUrl;
import com.google.api.server.spi.auth.common.User;
import com.google.api.server.spi.config.*;
import com.google.api.server.spi.response.BadRequestException;
import com.google.api.server.spi.response.ForbiddenException;
import com.google.api.server.spi.response.NotFoundException;
import com.google.api.server.spi.response.UnauthorizedException;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.ShortBlob;
import com.google.common.base.Preconditions;
import com.jasify.schedule.appengine.http.HttpUserSession;
import com.jasify.schedule.appengine.model.EntityNotFoundException;
import com.jasify.schedule.appengine.model.UserContext;
import com.jasify.schedule.appengine.model.UserSession;
import com.jasify.schedule.appengine.model.users.LoginFailedException;
import com.jasify.schedule.appengine.model.users.UserServiceFactory;
import com.jasify.schedule.appengine.oauth2.OAuth2ProviderEnum;
import com.jasify.schedule.appengine.oauth2.OAuth2ServiceFactory;
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
        transformers = {JasUserLoginTransformer.class, JasUserTransformer.class, JasKeyTransformer.class, JasActivityTypeTransformer.class, JasActivityTransformer.class, JasOrganizationTransformer.class, JasGroupTransformer.class, JasSubscriptionTransformer.class},
        auth = @ApiAuth(allowCookieAuth = AnnotationBoolean.TRUE /* todo: I don't know another way :-( */),
        namespace = @ApiNamespace(ownerDomain = "jasify.com",
                ownerName = "Jasify",
                packagePath = ""))
public class AuthEndpoint {
    private static final Logger log = LoggerFactory.getLogger(AuthEndpoint.class);

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

}
