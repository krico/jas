package com.jasify.schedule.appengine.http.servlet;

import com.jasify.schedule.appengine.http.HttpUserSession;
import com.jasify.schedule.appengine.http.json.JsonResponse;
import com.jasify.schedule.appengine.model.UserContext;
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
public class IsLoggedInServlet extends HttpServlet {
    private final JsonResponse OK = new JsonResponse(true);
    private final JsonResponse NOK = new JsonResponse("Not logged in.");


    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType(JSON.CONTENT_TYPE);
        if (UserContext.getCurrentUser() == null) {
            resp.getWriter().append(NOK.toJson());
        } else {
            resp.getWriter().append(OK.toJson());
        }
    }
}