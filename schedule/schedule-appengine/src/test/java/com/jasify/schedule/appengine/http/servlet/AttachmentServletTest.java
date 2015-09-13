package com.jasify.schedule.appengine.http.servlet;

import com.google.appengine.api.datastore.Key;
import com.google.common.net.MediaType;
import com.jasify.schedule.appengine.TestHelper;
import com.jasify.schedule.appengine.dao.attachment.AttachmentDao;
import com.jasify.schedule.appengine.model.attachment.AttachmentHelper;
import com.jasify.schedule.appengine.util.KeyUtil;
import com.meterware.httpunit.GetMethodWebRequest;
import com.meterware.httpunit.WebRequest;
import com.meterware.httpunit.WebResponse;
import com.meterware.servletunit.ServletUnitClient;
import org.apache.commons.io.IOUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.xml.sax.SAXException;

import java.io.IOException;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;

public class AttachmentServletTest {
    @BeforeClass
    public static void startServletRunner() {
        TestHelper.initializeServletRunner();
    }

    @AfterClass
    public static void stopServletRunner() {
        TestHelper.cleanupServletRunner();
    }

    private void get(boolean download, boolean appendFilenameToUrl) throws com.jasify.schedule.appengine.model.ModelException, IOException, SAXException {
        AttachmentDao dao = new AttachmentDao();
        String text = "This is\nMy First\nAttachment...\n";
        byte[] bytes = text.getBytes("UTF-8");
        String filename = "first.txt";
        Key id = dao.save(AttachmentHelper.create(filename, MediaType.PLAIN_TEXT_UTF_8, bytes));
        String idStr = KeyUtil.toHumanReadableString(id);

        ServletUnitClient client = TestHelper.servletRunner().newClient();
        String urlString;
        if (download) {
            urlString = "http://schedule.jasify.com/download/" + idStr;
        } else {
            urlString = "http://schedule.jasify.com/view/" + idStr;
        }
        if (appendFilenameToUrl) {
            urlString += "/" + filename;
        }
        WebRequest request = new GetMethodWebRequest(urlString);
        WebResponse response = client.getResponse(request);
        assertNotNull(response);

        assertEquals(bytes.length, response.getContentLength());
        assertEquals("text/plain", response.getContentType());
        assertEquals(text, IOUtils.toString(response.getInputStream()));
        String disposition = response.getHeaderField("content-disposition");
        assertNotNull(disposition);
        if (download) {
            assertEquals("attachment; filename=\"" + filename + "\"", disposition);
        } else {
            assertEquals("inline; filename=\"" + filename + "\"", disposition);
        }
    }

    @Test
    public void testDoGetView() throws Exception {
        get(false, false);
    }

    @Test
    public void testDoGetDownload() throws Exception {
        get(true, false);
    }

    @Test
    public void testDoGetViewWithFilenameInUrl() throws Exception {
        get(false, true);
    }

    @Test
    public void testDoGetDownloadWithFilenameInUrl() throws Exception {
        get(true, true);
    }
}