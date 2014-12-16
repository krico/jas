package com.jasify.schedule.appengine.http.servlet;

import com.jasify.schedule.appengine.model.UserContext;
import com.jasify.schedule.appengine.model.UserSession;
import com.jasify.schedule.appengine.util.JSON;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author krico
 * @since 10/11/14.
 */
public class LogoutServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType(JSON.CONTENT_TYPE);
        UserSession userSession = UserContext.getCurrentUser();
        if (userSession != null) {

            userSession.invalidate();

        }
    }
}
