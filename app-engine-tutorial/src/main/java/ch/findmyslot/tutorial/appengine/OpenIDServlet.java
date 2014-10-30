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
import java.math.BigInteger;
import java.security.SecureRandom;

/**
 * Created by krico on 29/10/14.
 */
public class OpenIDServlet extends HttpServlet {

    static class Data {
        private String clientId;
        private String state;
        private String applicationName;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Data data = new Data();
        data.clientId = Constants.OpenID.Credentials.ClientID;
        data.state = new BigInteger(130, new SecureRandom()).toString(32);
        data.applicationName = Constants.OpenID.Credentials.ApplicationName;

        HttpSession session = req.getSession(true);
        String state = (String) session.getAttribute(Constants.Session.OpenIDState);
        if (StringUtils.isEmpty(state)) {
            session.setAttribute(Constants.Session.OpenIDState, data.state);
        } else {
            data.state = state;
        }
        resp.setContentType("application/json");
        Gson gson = new GsonBuilder().create();
        gson.toJson(data, resp.getWriter());
    }
}
