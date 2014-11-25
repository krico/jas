package com.jasify.schedule.appengine.http.servlet;

import com.google.common.base.Preconditions;
import com.jasify.schedule.appengine.http.json.JsonPasswordChangeRequest;
import com.jasify.schedule.appengine.http.json.JsonResponse;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author krico
 * @since 24/11/14.
 */
public class ChangePasswordServlet extends HttpServlet {
    private static final Logger log = LoggerFactory.getLogger(ChangePasswordServlet.class);

    private static final Pattern PATH_INFO_PATTERN = Pattern.compile("^(?:/)([0-9]+|current)$");
    private final JsonResponse OK = new JsonResponse(true);
    private final JsonResponse FAIL_OLD = new JsonResponse("Old password did not match");
    private final JsonResponse FAIL_NEW = new JsonResponse("Invalid password");

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType(JSON.CONTENT_TYPE);

        JsonPasswordChangeRequest js = JsonPasswordChangeRequest.parse(req.getReader());
        if (js == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        String pathInfo = StringUtils.trimToEmpty(req.getPathInfo());

        UserSession currentUser = UserContext.getCurrentUser();

        if (currentUser == null) {

            log.info("Unauthorized POST access to {}", pathInfo);
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;

        }

        Matcher matcher = PATH_INFO_PATTERN.matcher(pathInfo);
        if (matcher.matches()) {

            if (StringUtils.isBlank(js.getNewPassword())) {
                FAIL_NEW.toJson(resp.getWriter());
                return;
            }
            String matched = matcher.group(1);
            long userId = "current".equals(matched) ? currentUser.getUserId() : Long.parseLong(matched);
            if (UserContext.isCurrentUserAdmin() || userId == currentUser.getUserId()) {

                UserService userService = UserServiceFactory.getUserService();
                User user = Preconditions.checkNotNull(userService.get(userId), "Logged in user was deleted?");


                if (!UserContext.isCurrentUserAdmin() &&
                        !DigestUtil.verify(TypeUtil.toBytes(user.getPassword()), js.getOldPassword())) {
                    FAIL_OLD.toJson(resp.getWriter());
                    return;
                }

                try {

                    userService.setPassword(user, js.getNewPassword());
                    OK.toJson(resp.getWriter());

                } catch (EntityNotFoundException e) {

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
