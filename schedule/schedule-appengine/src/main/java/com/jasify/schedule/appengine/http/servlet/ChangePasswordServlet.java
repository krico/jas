package com.jasify.schedule.appengine.http.servlet;

import com.google.common.base.Preconditions;
import com.jasify.schedule.appengine.http.json.JsonPasswordChangeRequest;
import com.jasify.schedule.appengine.model.EntityNotFoundException;
import com.jasify.schedule.appengine.model.UserContext;
import com.jasify.schedule.appengine.model.UserSession;
import com.jasify.schedule.appengine.model.users.User;
import com.jasify.schedule.appengine.model.users.UserService;
import com.jasify.schedule.appengine.model.users.UserServiceFactory;
import com.jasify.schedule.appengine.util.DigestUtil;
import com.jasify.schedule.appengine.util.JSON;
import com.jasify.schedule.appengine.util.TypeUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author krico
 * @since 24/11/14.
 */
public class ChangePasswordServlet extends HttpServlet {
    private static final Logger log = LoggerFactory.getLogger(ChangePasswordServlet.class);

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        UserSession currentUser = UserContext.getCurrentUser();

        if (currentUser == null) {

            log.info("Unauthorized POST access");
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;

        }

        JsonPasswordChangeRequest js = JsonPasswordChangeRequest.parse(req.getReader());
        JsonPasswordChangeRequest.Credentials credentials;
        if (js == null || (credentials = js.getCredentials()) == null || StringUtils.isBlank(js.getNewPassword()) || credentials.getId() <= 0) {

            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;

        }

        long userId = credentials.getId();
        if (userId != currentUser.getUserId() && !UserContext.isCurrentUserAdmin()) {

            resp.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;

        }

        UserService userService = UserServiceFactory.getUserService();
        User user = Preconditions.checkNotNull(userService.get(userId), "User not found!");


        if (!UserContext.isCurrentUserAdmin() &&
                !DigestUtil.verify(TypeUtil.toBytes(user.getPassword()), credentials.getPassword())) {

            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;

        }


        resp.setContentType(JSON.CONTENT_TYPE);
        try {

            log.info("User {} changing password of {}", UserContext.getCurrentUser().getUserId(), user.getId());
            userService.setPassword(user, js.getNewPassword());

        } catch (EntityNotFoundException e) {

            log.warn("Failed to save user", e);
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

        }
    }
}