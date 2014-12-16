package com.jasify.schedule.appengine.http.servlet;

import com.google.appengine.api.datastore.Query;
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
import java.util.ArrayList;
import java.util.List;
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
        if (UserContext.getCurrentUser() == null) {

            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;

        }

        String pathInfo = StringUtils.trimToEmpty(req.getPathInfo());
        if (pathInfo.isEmpty()) {
            doGetList(req, resp);
        } else {
            doGetUser(req, resp);
        }
    }

    private void doGetList(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType(JSON.CONTENT_TYPE);

        if (!UserContext.isCurrentUserAdmin()) {

            log.info("Unauthorized GET access");
            resp.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;

        }
        int page = 1;
        int size = 20;
        Query.SortDirection order = Query.SortDirection.ASCENDING;

        if (req.getParameter("page") != null) page = Integer.parseInt(req.getParameter("page"));
        if (req.getParameter("size") != null) size = Integer.parseInt(req.getParameter("size"));
        if (req.getParameter("sort") != null) {
            order = StringUtils.equalsIgnoreCase("desc", req.getParameter("sort")) ? Query.SortDirection.DESCENDING : Query.SortDirection.ASCENDING;
        }
        int offset = (page - 1) * size;

        String field = req.getParameter("field");

        List<User> list;
        String query = StringUtils.trimToNull(req.getParameter("query"));
        if ("email".equals(field)) {
            list = UserServiceFactory.getUserService().searchByEmail(query == null ? null : Pattern.compile(query), order, offset, size);
        } else if ("name".equals(field)) {
            list = UserServiceFactory.getUserService().searchByName(query == null ? null : Pattern.compile(query), order, offset, size);
        } else {
            list = UserServiceFactory.getUserService().list(order, offset, size);
        }
        ArrayList<JsonUser> ret = new ArrayList<>();
        for (User user : list) {
            ret.add(new JsonUser(user));
        }
        resp.addHeader("X-Total", Integer.toString(UserServiceFactory.getUserService().getTotalUsers()));
        JSON.toJson(resp.getWriter(), ret);
    }

    private void doGetUser(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType(JSON.CONTENT_TYPE);

        String pathInfo = StringUtils.trimToEmpty(req.getPathInfo());
        UserSession currentUser = UserContext.getCurrentUser();

        Matcher matcher = PATH_INFO_PATTERN.matcher(pathInfo);
        if (matcher.matches()) {

            String matched = matcher.group(1);
            long userId = Long.parseLong(matched);
            if (UserContext.isCurrentUserAdmin() || userId == currentUser.getUserId()) {

                User user = Preconditions.checkNotNull(UserServiceFactory.getUserService().get(userId), "Logged in user was deleted?");
                new JsonUser(user).toJson(resp.getWriter());

            } else {

                log.info("Unauthorized GET access to {} by user {}", currentUser);
                resp.sendError(HttpServletResponse.SC_FORBIDDEN);

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

                    if (UserContext.isCurrentUserAdmin()) {
                        user.setAdmin(js.isAdmin());
                    }

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
