package com.jasify.schedule.appengine.mail;

import com.jasify.schedule.appengine.TestHelper;
import com.jasify.schedule.appengine.meta.attachment.AttachmentMeta;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.slim3.datastore.Datastore;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

import static junit.framework.TestCase.assertEquals;

public class MailDebugTest {
    @AfterClass
    public static void stopEmail() {
        System.clearProperty(MailDebug.JASIFY_EMAIL_DEBUG_PROP);
    }

    @Before
    public void setupDS() {
        TestHelper.initializeDatastore();
    }

    @After
    public void cleanupDS() {
        TestHelper.cleanupDatastore();
    }

    @Test
    public void testWellDefined() throws Exception {
        TestHelper.assertUtilityClassWellDefined(MailDebug.class);
    }


    @Test
    public void testWriteEmailDebug() throws Exception {
        System.setProperty(MailDebug.JASIFY_EMAIL_DEBUG_PROP, "true");
        assertWriteEmailDebug(1);
    }

    @Test
    public void testWriteEmailDebugDoesNotWriteIfPropIsFalse() throws Exception {
        System.setProperty(MailDebug.JASIFY_EMAIL_DEBUG_PROP, "false");
        assertWriteEmailDebug(0);
    }

    @Test
    public void testWriteEmailDebugDoesNotWriteIfPropIsUnset() throws Exception {
        System.clearProperty(MailDebug.JASIFY_EMAIL_DEBUG_PROP);
        assertWriteEmailDebug(0);
    }

    private void assertWriteEmailDebug(int expectedIncrement) throws MessagingException {
        int expectedFiles = countAttachments();
        expectedFiles += expectedIncrement;


        Message message = new MimeMessage(Session.getDefaultInstance(new Properties(), null));
        message.setFrom(new InternetAddress("me@jasify.com"));
        message.setRecipient(Message.RecipientType.TO, new InternetAddress("foo@bar.com"));
        message.setSubject("Test");
        message.setText("Text");
        MailDebug.writeDebug(message);
        assertEquals(expectedFiles, countAttachments());
    }

    private int countAttachments() {
        return Datastore.query(AttachmentMeta.get()).count();
    }

}