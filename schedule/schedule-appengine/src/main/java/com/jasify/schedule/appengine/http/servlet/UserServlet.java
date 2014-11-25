package com.jasify.schedule.appengine.http.servlet;

import com.google.common.base.Preconditions;
import com.jasify.schedule.appengine.http.json.JsonSignUpUser;
import com.jasify.schedule.appengine.http.json.JsonUser;
import com.jasify.schedule.appengine.model.EntityNotFoundException;
import com.jasify.schedule.appengine.model.FieldValueException;
import com.jasify.schedule.appengine.model.UserContext;
import com.jasify.schedule.appengine.model.UserSession;
import com.jasify.schedule.appengine.model.users.User;
import com.jasify.schedule.appengine.model.users.UserService;
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

    private static final Pattern PATH_INFO_PATTERN = Pattern.compile("^(?:/)([0-9]+|current)$");

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

            String matched = matcher.group(1);
            long userId = "current".equals(matched) ? currentUser.getUserId() : Long.parseLong(matched);
            if (UserContext.isCurrentUserAdmin() || userId == currentUser.getUserId()) {

                User user = Preconditions.checkNotNull(UserServiceFactory.getUserService().get(userId), "Logged in user was deleted?");
                new JsonUser(user).toJson(resp.getWriter());

            } else {

                log.info("Unauthorized GET access to {} by user {}", currentUser);
                resp.sendError(HttpServletResponse.SC_UNAUTHORIZED);

            }

        } else {

            log.info("Invalid GET pathInfo: {}", pathInfo);
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);

        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType(JSON.CONTENT_TYPE);

        String pathInfo = StringUtils.trimToEmpty(req.getPathInfo());
        if (pathInfo.isEmpty()) {
            doPostCreate(req, resp);
        } else {
            doPostUpdate(req, resp);
        }
    }

    private void doPostCreate(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        try {
            JsonSignUpUser signUp = JsonSignUpUser.parse(req.getReader());
            String pw = Preconditions.checkNotNull(StringUtils.trimToNull(signUp.getPassword()), "NULL password");
            if (!pw.equals(signUp.getConfirmPassword()))
                throw new IllegalArgumentException("password and confirm do not match");

            UserService userService = UserServiceFactory.getUserService();

            User newUser = signUp.writeTo(userService.newUser());
            newUser.setName(signUp.getName());

            if (UserContext.isCurrentUserAdmin()) {
                newUser.setAdmin(signUp.isAdmin());
            }

            new JsonUser(userService.create(newUser, signUp.getPassword())).toJson(resp.getWriter());

        } catch (Exception e) {
            log.warn("Failed to create user", e);
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    private void doPostUpdate(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = StringUtils.trimToEmpty(req.getPathInfo());

        UserSession currentUser = UserContext.getCurrentUser();

        if (currentUser == null) {

            log.info("Unauthorized POST access to {}", pathInfo);
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;

        }

        Matcher matcher = PATH_INFO_PATTERN.matcher(pathInfo);
        if (matcher.matches()) {

            String matched = matcher.group(1);
            long userId = "current".equals(matched) ? currentUser.getUserId() : Long.parseLong(matched);
            if (UserContext.isCurrentUserAdmin() || userId == currentUser.getUserId()) {

                try {

                    UserService userService = UserServiceFactory.getUserService();
                    User user = Preconditions.checkNotNull(userService.get(userId), "Logged in user was deleted?");
                    JsonUser js = JsonUser.parse(req.getReader());
                    js.writeTo(user);

                    new JsonUser(userService.save(user)).toJson(resp.getWriter());

                } catch (EntityNotFoundException | FieldValueException | NullPointerException e) {

                    log.warn("Failed to save user", e);
                    resp.sendError(HttpServletResponse.SC_BAD_REQUEST);

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
