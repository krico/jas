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
}
