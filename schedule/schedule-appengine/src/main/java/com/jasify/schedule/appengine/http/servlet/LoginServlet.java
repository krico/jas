package com.jasify.schedule.appengine.http.servlet;

import com.jasify.schedule.appengine.http.UserSession;
import com.jasify.schedule.appengine.http.json.JsonLoginRequest;
import com.jasify.schedule.appengine.http.json.JsonResponse;
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
public class LoginServlet extends HttpServlet {
    private static final Logger log = LoggerFactory.getLogger(LoginServlet.class);

    private final JsonResponse OK = new JsonResponse(true);
    private final JsonResponse FAIL = new JsonResponse("Invalid username/password.");

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        resp.setContentType(JSON.CONTENT_TYPE);

        JsonLoginRequest jr = JsonLoginRequest.parse(req.getReader());

        if (jr == null) {

            FAIL.toJson(resp.getWriter());

        } else {

            try {

                User user = UserServiceFactory.getUserService().login(jr.getName(), jr.getPassword());
                new UserSession(user).put(req);
                OK.toJson(resp.getWriter());
                log.info("[{}] user={} logged in!", req.getRemoteAddr(), user.getName());

            } catch (LoginFailedException e) {

                log.info("[{}] user={} login failed!", req.getRemoteAddr(), jr.getName());
                FAIL.toJson(resp.getWriter());

            }

        }
    }
}
