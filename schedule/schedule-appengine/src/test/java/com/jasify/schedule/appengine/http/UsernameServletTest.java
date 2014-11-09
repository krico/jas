package com.jasify.schedule.appengine.http;

import com.jasify.schedule.appengine.TestHelper;
import com.jasify.schedule.appengine.http.json.JsonResponse;
import com.jasify.schedule.appengine.util.JSON;
import com.meterware.httpunit.PostMethodWebRequest;
import com.meterware.httpunit.WebRequest;
import com.meterware.httpunit.WebResponse;
import com.meterware.servletunit.ServletRunner;
import com.meterware.servletunit.ServletUnitClient;
import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.TestCase.*;

public class UsernameServletTest {

    private ServletRunner servletRunner;

    @Before
    public void servletRunner() {
        TestHelper.initializeJasify();
        servletRunner = new ServletRunner();
        servletRunner.registerServlet("username", UsernameServlet.class.getName());
    }

    @After
    public void stopDatastore() {
        TestHelper.cleanupDatastore();
    }

    @Test
    public void testPostEmpty() throws Exception {
        ServletUnitClient client = servletRunner.newClient();
        WebRequest request = new PostMethodWebRequest("http://schedule.jasify.com/username");
        WebResponse response = client.getResponse(request);
        assertNotNull("No response received", response);
        assertEquals("content type", JSON.CONTENT_TYPE, response.getContentType());
        String text = response.getText();
        assertNotNull(text);
        JsonResponse jr = JsonResponse.parse(text);
        assertNotNull(jr);
        assertTrue(jr.isNok());
        assertFalse(jr.isOk());
        assertNotNull(jr.getNokText());
    }

    @Test
    public void testPostWithValidUsername() throws Exception {
        ServletUnitClient client = servletRunner.newClient();

        PostMethodWebRequest request = new PostMethodWebRequest("http://schedule.jasify.com/username", IOUtils.toInputStream("krico"), JSON.CONTENT_TYPE);

        WebResponse response = client.getResponse(request);
        assertNotNull("No response received", response);
        assertEquals("content type", JSON.CONTENT_TYPE, response.getContentType());
        String text = response.getText();
        assertNotNull(text);
        JsonResponse jr = JsonResponse.parse(text);
        assertNotNull(jr);
        assertTrue(jr.isOk());
        assertFalse(jr.isNok());
    }


}