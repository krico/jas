package com.jasify.schedule.appengine.http.servlet;

import com.google.api.client.http.GenericUrl;
import com.jasify.schedule.appengine.oauth2.OAuth2ProviderEnum;
import com.jasify.schedule.appengine.oauth2.OAuth2Service;
import com.jasify.schedule.appengine.oauth2.OAuth2ServiceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

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

        OAuth2Service service = OAuth2ServiceFactory.getOAuth2Service();
        String encodedUrl = req.getRequestURL().toString();
        GenericUrl baseUrl = new GenericUrl(encodedUrl);
        baseUrl.setRawPath("/");
        GenericUrl codeRequestUrl = service.createCodeRequestUrl(baseUrl, provider, "Unused");
        resp.sendRedirect(codeRequestUrl.build());
    }
}
