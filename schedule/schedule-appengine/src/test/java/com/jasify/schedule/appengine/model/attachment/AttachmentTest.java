package com.jasify.schedule.appengine.model.attachment;

import com.google.common.net.MediaType;
import com.jasify.schedule.appengine.TestHelper;
import com.jasify.schedule.appengine.dao.attachment.AttachmentDao;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;

public class AttachmentTest {
    @Before
    public void initializeDatastore() {
        TestHelper.initializeJasify();
    }

    @After
    public void cleanupDatastore() {
        TestHelper.cleanupDatastore();
    }


    @Test
    public void testCreateWithMediaType() throws Exception {
        Attachment attachment = Attachment.create("foo.pdf", MediaType.PDF, new byte[]{0});
        assertEquals(attachment.getMimeType(), "application/pdf");
    }

    @Test
    public void testCreateAndSave() throws Exception {
        String text = "Text in the file\nHere it is...\n";
        String name = "file.txt";
        String mimeType = "text/plain";
        Attachment attachment = Attachment.create(name, mimeType, text.getBytes());

        assertNotNull(attachment);
        assertEquals(name, attachment.getName());
        assertEquals(mimeType, attachment.getMimeType());
        assertNotNull(attachment.getData());
        assertEquals(text, new String(attachment.getData().getBytes()));

        AttachmentDao dao = new AttachmentDao();
        dao.save(attachment);
        assertNotNull(attachment.getId());
        Attachment fetched = dao.get(attachment.getId());

        assertNotNull(fetched);
        assertEquals(name, fetched.getName());
        assertEquals(mimeType, fetched.getMimeType());
        assertNotNull(fetched.getData());
        assertEquals(text, new String(fetched.getData().getBytes()));
    }
}