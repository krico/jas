package com.jasify.schedule.appengine.http.servlet;

import com.jasify.schedule.appengine.TestHelper;
import com.jasify.schedule.appengine.http.HttpUserSession;
import com.jasify.schedule.appengine.oauth2.OAuth2ProviderEnum;
import com.meterware.httpunit.GetMethodWebRequest;
import com.meterware.httpunit.WebRequest;
import com.meterware.servletunit.ServletRunner;
import com.meterware.servletunit.ServletUnitClient;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.jasify.schedule.appengine.http.servlet.ServletTestHelper.expectResponse;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;

public class OAuth2CodeRequestServletTest {
    private static Map parameters = new HashMap();

    @Before
    public void servletRunner() {
        TestHelper.initializeServletRunner();
        parameters.clear();
    }

    @After
    public void stopDatastore() {
        TestHelper.cleanupServletRunner();
        parameters.clear();
    }

    @Test
    public void testDoGet() throws Exception {

        ServletRunner servletRunner = TestHelper.servletRunner();
//        servletRunner.registerServlet("https://accounts.google.com/o/oauth2/auth", TestServlet.class.getName());
        servletRunner.registerServlet("/o/oauth2/auth", TestServlet.class.getName());
        ServletUnitClient client = servletRunner.newClient();

        WebRequest request = new GetMethodWebRequest("http://schedule.jasify.com/oauth2/request/Google");
        expectResponse(client, request, HttpServletResponse.SC_FOUND);

        String code = Objects.toString(client.getSession(false).getAttribute(HttpUserSession.OAUTH_STATE_KEY));
        assertNotNull(parameters.get("state"));
        assertEquals(code, parameters.get("state"));
        assertEquals("code", parameters.get("response_type"));
        assertEquals(OAuth2ProviderEnum.Google.config().getClientId(), parameters.get("client_id"));
        assertEquals("http://schedule.jasify.com/oauth2/callback/Google", parameters.get("redirect_uri"));
    }

    public static class TestServlet extends HttpServlet {
        @SuppressWarnings("unchecked")
        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
            Enumeration names = req.getParameterNames();
            while (names.hasMoreElements()) {
                String o = (String) names.nextElement();
                parameters.put(o, req.getParameter(o));
            }
        }
    }
}