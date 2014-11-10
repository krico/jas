package com.jasify.schedule.appengine.http;

import com.jasify.schedule.appengine.http.json.JsonResponse;
import com.jasify.schedule.appengine.util.JSON;
import com.jasify.schedule.appengine.validators.UsernameValidator;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nonnull;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Servlet to verify that a username is valid and available
 * Created by krico on 09/11/14.
 */
public class UsernameServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //TODO: we should prevent username probing...  Make the checks slower after X attempts for example
        resp.setContentType(JSON.CONTENT_TYPE);
        String username = IOUtils.toString(req.getInputStream());
        List<String> reasons = UsernameValidator.INSTANCE.validate(username);
        JsonResponse jr;
        if (reasons.isEmpty()) {
            resp.getWriter().append(new JsonResponse(true).toJson());
        } else {
            resp.getWriter().append(new JsonResponse(StringUtils.join(reasons, reasons, '\n')).toJson());
        }
    }

    @Nonnull
    private JsonResponse validate(String username) {
        if (StringUtils.isEmpty(username)) {
            return new JsonResponse("Username cannot be empty.");
        }

        if (username.length() < 3) {
            return new JsonResponse("Username cannot be empty.");
        }
        return new JsonResponse(true);
    }
}
