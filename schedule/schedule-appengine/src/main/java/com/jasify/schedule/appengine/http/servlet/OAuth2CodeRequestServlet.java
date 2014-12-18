package com.jasify.schedule.appengine.http.servlet;

import com.google.api.client.auth.oauth2.AuthorizationCodeRequestUrl;
import com.google.api.client.http.GenericUrl;
import com.jasify.schedule.appengine.oauth2.OAuth2ProviderConfig;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Arrays;

/**
 * Redirects the user to the oauth authentication request url
 *
 * @author krico
 * @since 18/12/14.
 */
public class OAuth2CodeRequestServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String state = new BigInteger(130, new SecureRandom()).toString(32);
        HttpSession session = req.getSession(true);
        session.setAttribute("oauth-request-state", state);

        GenericUrl redirectUrl = new GenericUrl(req.getRequestURL().toString());
        redirectUrl.setRawPath("/oauth2/callback");
        OAuth2ProviderConfig providerConfig = OAuth2ProviderConfig.ProviderEnum.Google.config();

        String clientRequestUrl = new AuthorizationCodeRequestUrl(providerConfig.getAuthorizationUrl(), providerConfig.getClientId())
                .setState(state)
                .setRedirectUri(redirectUrl.build())
                .setScopes(Arrays.asList("email"))
                .build();

        resp.sendRedirect(clientRequestUrl);
    }
}
