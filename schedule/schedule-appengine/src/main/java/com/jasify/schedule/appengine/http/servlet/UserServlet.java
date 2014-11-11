package com.jasify.schedule.appengine.http.servlet;

import com.jasify.schedule.appengine.http.json.JsonUser;
import com.jasify.schedule.appengine.model.UserContext;
import com.jasify.schedule.appengine.model.UserSession;
import com.jasify.schedule.appengine.model.users.User;
import com.jasify.schedule.appengine.model.users.UserServiceFactory;
import com.jasify.schedule.appengine.util.JSON;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * CRUD operations for User...
 *
 * @author krico
 * @since 10/11/14.
 */
public class UserServlet extends HttpServlet {
    private static final Logger log = LoggerFactory.getLogger(UserServlet.class);

    private static final Pattern PATH_INFO_PATTERN = Pattern.compile("^(?:/)([0-9]+)$");

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType(JSON.CONTENT_TYPE);

        String pathInfo = StringUtils.trimToEmpty(req.getPathInfo());
        UserSession currentUser = UserContext.getCurrentUser();

        if (currentUser == null) {
            log.info("Unauthorized GET access to {}", pathInfo);
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        Matcher matcher = PATH_INFO_PATTERN.matcher(pathInfo);
        if (matcher.matches()) {

            long userId = Long.parseLong(matcher.group(1));
            if (userId == currentUser.getUserId()) { //TODO: isSysAdmin should be allowed
                User user = UserServiceFactory.getUserService().get(userId);
                if (user != null) {

                    new JsonUser(user).toJson(resp.getWriter());

                } else {
                    log.warn("Weird GET access to {} by user {} but user NOT FOUND", currentUser);
                    resp.sendError(HttpServletResponse.SC_NOT_FOUND);
                }
            } else {

                log.info("Unauthorized GET access to {} by user {}", currentUser);
                resp.sendError(HttpServletResponse.SC_UNAUTHORIZED);

            }

        } else {

            log.info("Invalid GET pathInfo: {}", pathInfo);
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);

        }
    }
}
