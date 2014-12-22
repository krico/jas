package com.jasify.schedule.appengine.http.servlet;

import com.google.api.client.auth.oauth2.*;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.oauth2.Oauth2;
import com.google.api.services.oauth2.model.Tokeninfo;
import com.google.api.services.oauth2.model.Userinfoplus;
import com.jasify.schedule.appengine.http.HttpUserSession;
import com.jasify.schedule.appengine.oauth2.OAuth2ProviderConfig;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * After an oauth2 provider has authenticated a user, the user gets redirected here
 *
 * @author krico
 * @since 18/12/14.
 */
public class OAuth2CodeCallbackServlet extends HttpServlet {
    private static final Logger log = LoggerFactory.getLogger(OAuth2CodeCallbackServlet.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "No session");
            return;
        }
        String state = (String) session.getAttribute(HttpUserSession.OAUTH_STATE_KEY);
        if (StringUtils.isBlank(state)) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "No state");
            return;
        }
        session.removeAttribute(HttpUserSession.OAUTH_STATE_KEY);

        StringBuffer fullUrlBuf = req.getRequestURL();
        if (req.getQueryString() != null) {
            fullUrlBuf.append('?').append(req.getQueryString());
        }
        AuthorizationCodeResponseUrl authResponse = new AuthorizationCodeResponseUrl(fullUrlBuf.toString());
        if (StringUtils.isNotBlank(authResponse.getError())) {
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, authResponse.getErrorDescription());
            return;
        }

        if (!StringUtils.equals(state, authResponse.getState())) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Bad state");
            return;
        }

        try {

            OAuth2ProviderConfig googleConfig = OAuth2ProviderConfig.ProviderEnum.Google.config();

            NetHttpTransport transport = new NetHttpTransport();

            TokenResponse tokenResponse = new AuthorizationCodeTokenRequest(transport,
                    JacksonFactory.getDefaultInstance(),
                    new GenericUrl(googleConfig.getTokenUrl()),
                    authResponse.getCode())
                    .setRedirectUri(new GenericUrl(req.getRequestURL().toString()).build())
                    .setClientAuthentication(new ClientParametersAuthentication(googleConfig.getClientId(),
                            googleConfig.getClientSecret())).execute();

            Credential credential = new Credential.Builder(BearerToken.authorizationHeaderAccessMethod())
                    .build().setFromTokenResponse(tokenResponse);

            Oauth2 oauth2 = new Oauth2.Builder(transport, JacksonFactory.getDefaultInstance(), credential).build();
            Tokeninfo tokenInfo = oauth2.tokeninfo().setAccessToken(credential.getAccessToken()).execute();
            Userinfoplus userInfo = oauth2.userinfo().get().execute();
            PrintWriter writer = resp.getWriter();
            writer.append("<html><head><script type=\"application/json\" id=\"json-response\">");
            writer.append(tokenInfo.toString());//TODO: acutal data
            writer.append("</script></head><body></body></html>");
        } catch (TokenResponseException e) {
            log.info("Failed to get token", e);
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Failed to get token");
        }

    }
}
