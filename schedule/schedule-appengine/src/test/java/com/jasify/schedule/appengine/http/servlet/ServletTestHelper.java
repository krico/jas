package com.jasify.schedule.appengine.http.servlet;

import com.jasify.schedule.appengine.TestHelper;
import com.jasify.schedule.appengine.http.json.JsonLoginRequest;
import com.jasify.schedule.appengine.http.json.JsonSessionResponse;
import com.jasify.schedule.appengine.util.JSON;
import com.meterware.httpunit.PostMethodWebRequest;
import com.meterware.httpunit.WebRequest;
import com.meterware.httpunit.WebResponse;
import com.meterware.servletunit.InvocationContext;
import com.meterware.servletunit.ServletUnitClient;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.xml.sax.SAXException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static junit.framework.TestCase.*;

/**
 * @author krico
 * @since 15/12/14.
 */
public final class ServletTestHelper {
    private ServletTestHelper() {
    }

    public static InvocationContext expectResponse(ServletUnitClient client, WebRequest request, int responseType) throws IOException, ServletException, SAXException {
        return expectResponse(client, request, responseType, null);
    }

    public static InvocationContext expectResponse(ServletUnitClient client, WebRequest request, int responseType, String responseMessage) throws IOException, ServletException, SAXException {
        InvocationContext ic = client.newInvocation(request);
        ic.service();
        WebResponse servletResponse = ic.getServletResponse();
        ;
        assertNotNull(servletResponse);
        assertEquals(responseType, servletResponse.getResponseCode());
        if (responseMessage != null) {
            assertEquals(responseMessage, servletResponse.getResponseMessage());
        }
        if (servletResponse.getResponseCode() < HttpServletResponse.SC_BAD_REQUEST) {
            client.getResponse(ic);
        }
        return ic;
    }

    public static ServletUnitClient login(String name, String password) throws IOException, SAXException, ServletException {
        ServletUnitClient client = TestHelper.servletRunner().newClient();
        JsonLoginRequest req = new JsonLoginRequest(name, password);
        WebRequest request = new PostMethodWebRequest(Urls.LOGIN_URL, IOUtils.toInputStream(req.toJson()), JSON.CONTENT_TYPE);
        InvocationContext ic = expectResponse(client, request, HttpServletResponse.SC_OK);
        JsonSessionResponse jsr = JsonSessionResponse.parse(ic.getServletResponse().getText());
        assertNotNull("session response", jsr);
        assertTrue("no session id", StringUtils.isNotBlank(jsr.getId()));
        assertTrue("no user id", jsr.getUserId() > 0);
        assertNotNull("no user", jsr.getUser());
        assertEquals("bad id", jsr.getUserId(), jsr.getUser().getId());
        assertEquals("wrong name", name.toLowerCase(), jsr.getUser().getName().toLowerCase());
        return client;
    }

    public static class Urls {
        public static final String CHANGE_PASSWORD = "http://schedule.jasify.com/auth/change-password";
        public static final String LOGIN_URL = "http://schedule.jasify.com/auth/login";
        public static final String RESTORE_URL = "http://schedule.jasify.com/auth/restore";
        public static final String LOGOUT_URL = "http://schedule.jasify.com/logout";
    }
}
