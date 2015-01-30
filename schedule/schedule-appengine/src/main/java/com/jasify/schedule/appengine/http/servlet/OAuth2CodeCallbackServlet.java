package com.jasify.schedule.appengine.http.servlet;

import com.google.api.client.auth.oauth2.*;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.oauth2.Oauth2;
import com.google.api.services.oauth2.model.Tokeninfo;
import com.google.api.services.oauth2.model.Userinfoplus;
import com.google.common.base.Preconditions;
import com.jasify.schedule.appengine.http.HttpUserSession;
import com.jasify.schedule.appengine.http.json.JsonOAuthDetail;
import com.jasify.schedule.appengine.model.EntityNotFoundException;
import com.jasify.schedule.appengine.model.UserContext;
import com.jasify.schedule.appengine.model.UserSession;
import com.jasify.schedule.appengine.model.users.*;
import com.jasify.schedule.appengine.oauth2.*;
import com.jasify.schedule.appengine.util.JSON;
import com.jasify.schedule.appengine.util.TypeUtil;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * After an oauth2 provider has authenticated a user, the user gets redirected here
 *
 * @author krico
 * @since 18/12/14.
 */
public class OAuth2CodeCallbackServlet extends HttpServlet {
    private static final Logger log = LoggerFactory.getLogger(OAuth2CodeCallbackServlet.class);
    private static Pattern FB_ACCESS_TOKEN_PATTERN = Pattern.compile("^access_token=(.*)&expires=([0-9]+)$");

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        resp.setHeader("Pragma", "no-cache");
        resp.setDateHeader("Expires", 0);

        OAuth2Service service = OAuth2ServiceFactory.getOAuth2Service();
        StringBuffer fullUrlBuf = req.getRequestURL();
        if (req.getQueryString() != null) {
            fullUrlBuf.append('?').append(req.getQueryString());
        }

        try {
            OAuth2UserToken userToken = service.fetchUserToken(new GenericUrl(fullUrlBuf.toString()));
            OAuth2ProviderEnum provider = userToken.getProvider();
            OAuth2Info oAuth2Info = service.fetchInfo(userToken);
            JsonOAuthDetail detail = new JsonOAuthDetail();
            User existingUser = UserServiceFactory.getUserService().findByLogin(provider.name(), oAuth2Info.getUserId());
            if (existingUser == null) {
                UserLogin userLogin = new UserLogin(provider.name(), oAuth2Info.getUserId());
                userLogin.setAvatar(TypeUtil.toLink(oAuth2Info.getAvatar()));
                userLogin.setProfile(TypeUtil.toLink(oAuth2Info.getProfile()));
                userLogin.setEmail(oAuth2Info.getEmail());
                userLogin.setRealName(oAuth2Info.getRealName());

                detail.setEmail(oAuth2Info.getEmail());
                detail.setRealName(oAuth2Info.getRealName());

                if (UserContext.getCurrentUser() == null) {
                    log.info("Creating new user={} authenticated via oauth", userLogin.getEmail());
                    User newUser = new User(userLogin);
                    try {
                        newUser = UserServiceFactory.getUserService().create(newUser, userLogin);

                        //TODO: these two exceptions need to be handled better so that the web interface can react
                    } catch (EmailExistsException e) {
                        resp.sendError(HttpServletResponse.SC_FORBIDDEN, "E-mail exists");
                        return;
                    } catch (UsernameExistsException e) {
                        resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Username exists");
                        return;
                    }
                    detail.setLoggedIn(true);
                    new HttpUserSession(newUser).put(req); //todo: simulate log in
                } else {
                    UserSession currentUser = UserContext.getCurrentUser();
                    User user = UserServiceFactory.getUserService().get(currentUser.getUserId());
                    UserServiceFactory.getUserService().addLogin(user, userLogin);
                    detail.setAdded(true);
                }
            } else {
                if (UserContext.getCurrentUser() == null) {
                    //if user not logged in yet
                    detail.setLoggedIn(true);
                    new HttpUserSession(existingUser).put(req); //todo: simulate log in
                } else {
                    detail.setExists(true);
                }
            }


            PrintWriter writer = resp.getWriter();
            writer.append("<html><head><script type=\"application/json\" id=\"json-response\">");
            detail.toJson(writer);
            writer.append("</script></head><body></body></html>");
        } catch (OAuth2Exception e) {
            log.info("Failed to process", e);
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, e.toString());
        } catch (TokenResponseException | UserLoginExistsException | EntityNotFoundException e) {
            log.info("Failed to process", e);
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Failed to process");
        }

    }

    private OAuth2Info extractInfo(OAuth2ProviderEnum provider, AuthorizationCodeResponseUrl authResponse, HttpServletRequest req) throws IOException {
        OAuth2ProviderConfig providerConfig = provider.config();

        NetHttpTransport transport = new NetHttpTransport();

        switch (provider) {
            case Google: {
                AuthorizationCodeTokenRequest tokenRequest = new AuthorizationCodeTokenRequest(transport,
                        JacksonFactory.getDefaultInstance(),
                        new GenericUrl(providerConfig.getTokenUrl()),
                        authResponse.getCode())
                        .setRedirectUri(new GenericUrl(req.getRequestURL().toString()).build())
                        .setClientAuthentication(new ClientParametersAuthentication(providerConfig.getClientId(),
                                providerConfig.getClientSecret()));

                TokenResponse tokenResponse = tokenRequest.execute();

                Credential credential = new Credential.Builder(BearerToken.authorizationHeaderAccessMethod())
                        .build().setFromTokenResponse(tokenResponse);

                Oauth2 oauth2 = new Oauth2.Builder(transport, JacksonFactory.getDefaultInstance(), credential).build();
                Tokeninfo tokenInfo = oauth2.tokeninfo().setAccessToken(credential.getAccessToken()).execute();
                Userinfoplus userInfo = oauth2.userinfo().get().execute();

                OAuth2Info ret = new OAuth2Info();
                ret.setUserId(tokenInfo.getUserId());
                ret.setAvatar(userInfo.getPicture());
                ret.setProfile(userInfo.getLink());
                ret.setEmail(tokenInfo.getEmail());
                ret.setRealName(userInfo.getName());
                return ret;

            }
            case Facebook: {
                GenericUrl tokenRequest = new GenericUrl(providerConfig.getTokenUrl());

                tokenRequest.set("client_id", providerConfig.getClientId());
                tokenRequest.set("redirect_uri", new GenericUrl(req.getRequestURL().toString()).build());
                tokenRequest.set("client_secret", providerConfig.getClientSecret());
                tokenRequest.set("code", authResponse.getCode());

                HttpRequestFactory requestFactory = transport.createRequestFactory();
                HttpRequest getToken = requestFactory.buildGetRequest(tokenRequest);
                HttpResponse tokenResponse = getToken.execute();
                String accessToken;
                long expires;
                try {
                    if (!tokenResponse.isSuccessStatusCode()) {
                        return null;
                    }
                    String responseData = IOUtils.toString(tokenResponse.getContent());
                    Matcher matcher = FB_ACCESS_TOKEN_PATTERN.matcher(responseData);
                    if (!matcher.matches()) {
                        return null;
                    }
                    accessToken = matcher.group(1);
                    expires = Integer.parseInt(matcher.group(2));
                    log.info("RD: {}", responseData);
                } finally {
                    tokenResponse.disconnect();
                }

                GenericUrl infoRequest = new GenericUrl(providerConfig.getUserInfoUrl());
                infoRequest.set("access_token", accessToken);

                HttpRequest getInfo = requestFactory.buildGetRequest(infoRequest);
                HttpResponse infoResponse = getInfo.execute();
                Map infoData;
                try {
                    String data = IOUtils.toString(infoResponse.getContent());
                    log.info("D: {}", data);
                    infoData = JSON.fromJson(data, Map.class);
                } finally {
                    infoResponse.disconnect();
                }
                log.info("ID: {}", infoData);
                OAuth2Info ret = new OAuth2Info();
                ret.setUserId(Objects.toString(Preconditions.checkNotNull(infoData.get("id"))));
                ret.setProfile(Objects.toString(infoData.get("link")));
                ret.setEmail(Objects.toString(infoData.get("email")));
                ret.setRealName(Objects.toString(infoData.get("name")));
                return ret;

            }
        }
        return null;
    }

}
