package ch.findmyslot.tutorial.appengine;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by krico on 29/10/14.
 */
public class OpenIDCallbackServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        PrintWriter writer = resp.getWriter();

        Gson gson = new GsonBuilder().create();

        String state = req.getParameter("state");
        if (StringUtils.isBlank(state)) {
            gson.toJson("No state in callback to validate", writer);
            return;
        }

        String code = req.getParameter("code");
        if (StringUtils.isBlank(code)) {
            gson.toJson("No code in callback", writer);
            return;
        }

        HttpSession session = req.getSession();
        if (session == null) {
            gson.toJson("No session to validate state", writer);
            return;
        }
        String sessionState = (String) session.getAttribute(Constants.Session.OpenIDState);
        if (StringUtils.isBlank(sessionState)) {
            gson.toJson("No state in current session to validate", writer);
            return;
        }

        if (StringUtils.equals(sessionState, state)) {
            gson.toJson("Invalid state", writer);
            return;
        }


    }
}
