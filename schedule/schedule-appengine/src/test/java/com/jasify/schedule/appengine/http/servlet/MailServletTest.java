package com.jasify.schedule.appengine.http.servlet;

import com.jasify.schedule.appengine.TestHelper;
import com.jasify.schedule.appengine.meta.MailMessageMeta;
import com.jasify.schedule.appengine.model.MailMessage;
import com.meterware.httpunit.PostMethodWebRequest;
import com.meterware.httpunit.WebRequest;
import com.meterware.httpunit.WebResponse;
import com.meterware.servletunit.ServletUnitClient;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slim3.datastore.Datastore;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Properties;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;

public class MailServletTest {

    @Before
    public void startServletRunner() {
        TestHelper.initializeServletRunner();
    }

    @After
    public void stopServletRunner() {
        TestHelper.cleanupServletRunner();
    }

    @Test
    public void testDoPost() throws Exception {
        MimeMessage message = new MimeMessage(Session.getDefaultInstance(new Properties(), null));
        String from = "me@jasify.com";
        message.setFrom(new InternetAddress(from));
        message.setText("Hi there");
        String subject = "Test";
        message.setSubject(subject);
        String to = "you@jasify.com";
        message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        message.writeTo(bos);
        ServletUnitClient client = TestHelper.servletRunner().newClient();
        WebRequest request = new PostMethodWebRequest("http://schedule.jasify.com/_ah/mail/pathToMail",
                new ByteArrayInputStream(bos.toByteArray()), "text/plain");
        WebResponse response = client.getResponse(request);
        assertNotNull(response);

        MailMessage mailMessage = Datastore.query(MailMessageMeta.get()).asSingle();
        assertNotNull(mailMessage);
        assertEquals("[" + from + "]", mailMessage.getFrom());
        assertEquals("/pathToMail", mailMessage.getPathInfo());
        assertEquals(subject, mailMessage.getSubject());
    }
}