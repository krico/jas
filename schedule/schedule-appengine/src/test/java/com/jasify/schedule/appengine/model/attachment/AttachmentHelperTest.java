package com.jasify.schedule.appengine.model.attachment;

import com.google.api.client.http.GenericUrl;
import com.google.appengine.api.datastore.Key;
import com.google.common.net.MediaType;
import com.jasify.schedule.appengine.TestHelper;
import com.jasify.schedule.appengine.dao.attachment.AttachmentDao;
import com.jasify.schedule.appengine.util.EnvironmentUtil;
import com.jasify.schedule.appengine.util.KeyUtil;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;

public class AttachmentHelperTest {
    @Before
    public void initializeDatastore() {
        TestHelper.initializeJasify();
    }

    @After
    public void cleanupDatastore() {
        TestHelper.cleanupDatastore();
    }

    @Test
    public void testWellDefined() throws Exception {
        TestHelper.assertUtilityClassWellDefined(AttachmentHelper.class);
    }

    @Test
    public void testCreateWithMediaTypeAndFile() throws Exception {
        File test = File.createTempFile("test", ".txt");
        String expected = RandomStringUtils.randomAscii(128);
        String name = test.getName();
        FileUtils.write(test, expected);
        Attachment attachment = AttachmentHelper.create(name, MediaType.PLAIN_TEXT_UTF_8, test);
        assertNotNull(attachment);
        assertEquals(attachment.getMimeType(), "text/plain");
        assertEquals(name, attachment.getName());
        assertEquals(expected, new String(attachment.getData().getBytes()));
    }

    @Test
    public void testCreateWithMediaType() throws Exception {
        Attachment attachment = AttachmentHelper.create("foo.pdf", MediaType.PDF, new byte[]{0});
        assertEquals(attachment.getMimeType(), "application/pdf");
    }

    @Test
    public void testCreateAndSave() throws Exception {
        String text = "Text in the file\nHere it is...\n";
        String name = "file.txt";
        String mimeType = "text/plain";
        Attachment attachment = AttachmentHelper.create(name, mimeType, text.getBytes());

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

    @Test
    public void testMakeViewUrl() throws Exception {
        Key id = new AttachmentDao().save(AttachmentHelper.create("My File?.txt", "text/plain", new byte[0]));
        String expectedStr = EnvironmentUtil.defaultVersionUrl() + "/view/" + KeyUtil.toHumanReadableString(id) + "/My%20File%3F.txt";
        GenericUrl expected = new GenericUrl(expectedStr);
        GenericUrl created = AttachmentHelper.makeViewUrl(id);
        assertEquals(expected, created);
    }

    @Test
    public void testMakeDownloadUrl() throws Exception {
        Key id = new AttachmentDao().save(AttachmentHelper.create("My File?.txt", "text/plain", new byte[0]));
        String expectedStr = EnvironmentUtil.defaultVersionUrl() + "/download/" + KeyUtil.toHumanReadableString(id) + "/My%20File%3F.txt";
        GenericUrl expected = new GenericUrl(expectedStr);
        GenericUrl created = AttachmentHelper.makeDownloadUrl(id);
        assertEquals(expected, created);
    }
}