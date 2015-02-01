package com.jasify.schedule.appengine.http.servlet;

import com.google.api.client.http.GenericUrl;
import com.jasify.schedule.appengine.oauth2.OAuth2Util;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;

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
        resp.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        resp.setHeader("Pragma", "no-cache");
        resp.setDateHeader("Expires", 0);
        String state = StringUtils.trimToEmpty(req.getParameter("state"));
        StringBuffer callBackUrl = req.getRequestURL();
        GenericUrl redirect = new GenericUrl(callBackUrl.toString());
        callBackUrl.append('?').append(req.getQueryString());
        redirect.setRawPath(OAuth2Util.appPath(state));
        String location = redirect.buildRelativeUrl() + "#/oauth/" + URLEncoder.encode(callBackUrl.toString(), "utf-8");
        log.info("LOCATION: {}" , location);
        resp.sendRedirect(location);
    }

}
