package com.jasify.schedule.appengine.spi;

import com.google.api.client.http.GenericUrl;
import com.google.api.server.spi.auth.common.User;
import com.google.api.server.spi.config.*;
import com.google.api.server.spi.response.*;
import com.google.appengine.api.datastore.ShortBlob;
import com.google.common.base.Preconditions;
import com.jasify.schedule.appengine.http.HttpUserSession;
import com.jasify.schedule.appengine.model.EntityNotFoundException;
import com.jasify.schedule.appengine.model.UserContext;
import com.jasify.schedule.appengine.model.UserSession;
import com.jasify.schedule.appengine.model.users.*;
import com.jasify.schedule.appengine.oauth2.*;
import com.jasify.schedule.appengine.spi.auth.JasifyAuthenticator;
import com.jasify.schedule.appengine.spi.auth.JasifyEndpointUser;
import com.jasify.schedule.appengine.spi.dm.*;
import com.jasify.schedule.appengine.spi.transform.*;
import com.jasify.schedule.appengine.util.DigestUtil;
import com.jasify.schedule.appengine.util.TypeUtil;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.servlet.http.HttpServletRequest;
import java.util.Objects;
import java.util.Properties;

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

            return new JasLoginResponse(user, userSession);
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
            try {
                userService.addLogin(userService.get(jasifyUser.getUserId()), new UserLogin(oAuth2Info));
            } catch (EntityNotFoundException e) {
                throw new NotFoundException(e);
            } catch (UserLoginExistsException e) {
                throw new ConflictException("UserLogin exists");
            }

            return new JasProviderAuthenticateResponse(Objects.toString(oAuth2Info.getState()));

        } else {

            com.jasify.schedule.appengine.model.users.User existingUser = userService.findByLogin(oAuth2Info.getProvider().name(), oAuth2Info.getUserId());
            if (existingUser == null) {
                UserLogin userLogin = new UserLogin(oAuth2Info);
                log.info("Creating new user={} authenticated via oauth", userLogin.getEmail());

                try {
                    existingUser = userService.create(new com.jasify.schedule.appengine.model.users.User(userLogin), userLogin);
                } catch (EmailExistsException e) {
                    throw new ConflictException("Email exists");
                } catch (UsernameExistsException e) {
                    throw new ConflictException("Username exists");
                } catch (UserLoginExistsException e) {
                    throw new ConflictException("UserLogin exists");
                }
            }

            //LOGIN!
            HttpUserSession userSession = new HttpUserSession(existingUser).put(httpServletRequest);//todo: simulate log in
            return new JasProviderAuthenticateResponse(existingUser, userSession, Objects.toString(oAuth2Info.getState()));
        }
    }


    @ApiMethod(name = "auth.forgotPassword", path = "auth/forgot-password", httpMethod = ApiMethod.HttpMethod.POST)
    public void forgotPassword(JasForgotPasswordRequest request) throws BadRequestException, NotFoundException {
        Preconditions.checkNotNull(request.getEmail(), "email");
        Preconditions.checkNotNull(request.getUrl(), "url");
        PasswordRecovery recovery;
        try {
            recovery = UserServiceFactory.getUserService().registerPasswordRecovery(request.getEmail());
        } catch (EntityNotFoundException e) {
            throw new NotFoundException(e);
        }
        notify(recovery, request.getUrl());
    }

    @ApiMethod(name = "auth.recoverPassword", path = "auth/recover-password", httpMethod = ApiMethod.HttpMethod.POST)
    public void recoverPassword(JasRecoverPasswordRequest request) throws BadRequestException, NotFoundException {
        String code = Preconditions.checkNotNull(request.getCode(), "code");
        String newPassword = Preconditions.checkNotNull(request.getNewPassword(), "newPassword");
        try {
            UserServiceFactory.getUserService().recoverPassword(code, newPassword);
        } catch (EntityNotFoundException e) {
            throw new NotFoundException(e);
        }
    }


    /**
     * TODO: SEND EMAIL TO USER, comment here to explain params
     * TODO: wzsarmach!!!!  Move this away, it hurts my eyes :)
     *
     * @param recovery holding code and user
     * @param siteUrl  this is so we know what url the user is accessing, to avoid hard-coding jasify-schedule.app...
     */
    private void notify(PasswordRecovery recovery, String siteUrl) {
        /*
            TODO: Whenever we fix #142 we should also replace this method
            TODO: I wrote this method to avoid merge conflicts
            TODO: It is completely wrong place should be in MailService
            TODO: Remember to remove this comment
         */
        try {
            com.jasify.schedule.appengine.model.users.User user = recovery.getUserRef().getModel();
            String email = user.getEmail();
            String code = recovery.getCode().getName();
            GenericUrl recoverUrl = new GenericUrl(siteUrl);
            //recoverUrl.setScheme("https");
            recoverUrl.clear();
            recoverUrl.setRawPath("/");
            recoverUrl.setFragment("/recover-password/" + code);

            String subject = String.format("[Jasify] Password assistance [%s]", user.getName());
            String htmlBody = new StringBuilder()
                    .append("<h1>User: ").append(user.getName()).append("</h1>")
                    .append("<p>")
                    .append("Code: ").append(code).append("<br/>")
                    .append("Url: <a href=\"").append(recoverUrl.build()).append("\">")
                    .append(recoverUrl.build()).append("</a>")
                    .append("</p>")
                    .append("<p>Regards,<br>Jasify</p>")
                    .toString();

            //TODO: this code was copy & pasted just to avoid merge conflicts
            Session session = Session.getDefaultInstance(new Properties(), null);

            InternetAddress senderAddress = new InternetAddress("DoNotReply@jasify-schedule.appspotmail.com", "Jasify (Do Not Reply)");

            log.debug("Sent e-mail [{}] as [{}] to {}", subject, "krico@cwa.to", email);

            Message msg = new MimeMessage(session);
            msg.setFrom(senderAddress);
            msg.addRecipient(Message.RecipientType.TO, new InternetAddress(user.getEmail(), user.getName()));
            msg.setSubject(subject);

            Multipart mp = new MimeMultipart();

            MimeBodyPart htmlPart = new MimeBodyPart();
            htmlPart.setContent(htmlBody, "text/html");
            mp.addBodyPart(htmlPart);

            String textBody = Jsoup.parse(htmlBody).text();
            MimeBodyPart textPart = new MimeBodyPart();
            textPart.setContent(textBody, "text/plain");
            mp.addBodyPart(textPart);

            msg.setContent(mp);

            Transport.send(msg);
        } catch (Exception e) {
            log.warn("Failed to notify", e);
        }
    }


}
