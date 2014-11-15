package com.jasify.schedule.appengine.http.servlet;

import com.jasify.schedule.appengine.TestHelper;
import com.jasify.schedule.appengine.http.json.JsonResponse;
import com.jasify.schedule.appengine.http.servlet.UsernameServlet;
import com.jasify.schedule.appengine.util.JSON;
import com.meterware.httpunit.PostMethodWebRequest;
import com.meterware.httpunit.WebRequest;
import com.meterware.httpunit.WebResponse;
import com.meterware.servletunit.ServletUnitClient;
import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.TestCase.*;

public class UsernameServletTest {

    @Before
    public void servletRunner() {
        TestHelper.initializeServletRunner();
    }

    @After
    public void stopDatastore() {
        TestHelper.cleanupServletRunner();
    }

    @Test
    public void testPostEmpty() throws Exception {
        ServletUnitClient client = TestHelper.servletRunner().newClient();
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
        ServletUnitClient client = TestHelper.servletRunner().newClient();

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