package com.jasify.schedule.appengine.http.servlet;

import com.meterware.httpunit.WebRequest;
import com.meterware.httpunit.WebResponse;
import com.meterware.servletunit.InvocationContext;
import com.meterware.servletunit.ServletUnitClient;
import org.xml.sax.SAXException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;

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
}
