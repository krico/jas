package com.jasify.schedule.appengine.http.servlet;

import com.google.api.client.auth.oauth2.AuthorizationCodeRequestUrl;
import com.google.api.client.http.GenericUrl;
import com.jasify.schedule.appengine.http.HttpUserSession;
import com.jasify.schedule.appengine.oauth2.OAuth2ProviderConfig;
import com.jasify.schedule.appengine.oauth2.OAuth2ProviderEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.math.BigInteger;
import java.security.SecureRandom;

/**
 * Redirects the user to the oauth authentication request url
 *
 * @author krico
 * @since 18/12/14.
 */
public class OAuth2CodeRequestServlet extends HttpServlet {
    private static final Logger log = LoggerFactory.getLogger(OAuth2CodeRequestServlet.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        resp.setHeader("Pragma", "no-cache");
        resp.setDateHeader("Expires", 0);

        OAuth2ProviderEnum provider;
        try {
            provider = OAuth2ProviderEnum.parsePathInfo(req.getPathInfo());
        } catch (Exception e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Bad provider: " + req.getPathInfo());
            return;
        }

        String state = new BigInteger(130, new SecureRandom()).toString(32);
        HttpSession session = req.getSession(true);
        session.setAttribute(HttpUserSession.OAUTH_STATE_KEY, state);

        GenericUrl redirectUrl = new GenericUrl(req.getRequestURL().toString());
        redirectUrl.setRawPath("/oauth2/callback/" + provider.name());
        OAuth2ProviderConfig providerConfig = provider.config();

        String clientRequestUrl = provider.additionalParams(new AuthorizationCodeRequestUrl(providerConfig.getAuthorizationUrl(), providerConfig.getClientId())
                .setState(state)
                .setRedirectUri(redirectUrl.build())
                .setScopes(provider.scopes()))
                .build();

        resp.sendRedirect(clientRequestUrl);
    }
}
