package com.jasify.schedule.appengine.http.servlet;

import com.jasify.schedule.appengine.http.HttpUserSession;
import com.jasify.schedule.appengine.http.json.JsonLoginRequest;
import com.jasify.schedule.appengine.http.json.JsonSessionResponse;
import com.jasify.schedule.appengine.model.users.LoginFailedException;
import com.jasify.schedule.appengine.model.users.User;
import com.jasify.schedule.appengine.model.users.UserServiceFactory;
import com.jasify.schedule.appengine.util.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author krico
 * @since 10/11/14.
 */
@Deprecated//TODO: remove once other servlets are removed, still needed by tests
public class LoginServlet extends HttpServlet {
    private static final Logger log = LoggerFactory.getLogger(LoginServlet.class);

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        resp.setContentType(JSON.CONTENT_TYPE);

        JsonLoginRequest jr = JsonLoginRequest.parse(req.getReader());

        if (jr == null) {

            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;

        }

        try {

            User user = UserServiceFactory.getUserService().login(jr.getName(), jr.getPassword());
            HttpUserSession userSession = new HttpUserSession(user).put(req);
            log.info("[{}] user={} logged in!", req.getRemoteAddr(), user.getName());
            new JsonSessionResponse(user, userSession).toJson(resp.getWriter());

        } catch (LoginFailedException e) {

            log.info("[{}] user={} login failed!", req.getRemoteAddr(), jr.getName());
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED);

        }


    }
}
