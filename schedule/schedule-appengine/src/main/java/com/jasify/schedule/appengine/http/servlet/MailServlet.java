package com.jasify.schedule.appengine.http.servlet;

import com.google.appengine.api.datastore.Blob;
import com.jasify.schedule.appengine.model.mail.MailMessage;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slim3.datastore.Datastore;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Properties;

/**
 * This servlet handles http calls that reflect incoming e-mails to the application.
 * Details: https://cloud.google.com/appengine/docs/java/mail/#Java_Receiving_mail
 *
 * @author krico
 * @since 26/11/14.
 */
public class MailServlet extends HttpServlet {
    private static final Logger log = LoggerFactory.getLogger(MailServlet.class);

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        String pathInfo = StringUtils.trimToEmpty(req.getPathInfo());

        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);
        byte[] bytes = IOUtils.toByteArray(req.getInputStream());
        MailMessage save = new MailMessage();
        save.setPathInfo(pathInfo);
        save.setMessageData(new Blob(bytes));
        try {
            MimeMessage message = new MimeMessage(session, new ByteArrayInputStream(bytes));
            save.setFrom(StringUtils.abbreviate(Arrays.toString(message.getFrom()), 450));
            save.setSubject(StringUtils.abbreviate(message.getSubject(), 450));
        } catch (MessagingException e) {
            log.warn("Failed to handle incoming message", e);
        }
        Datastore.put(save);
    }
}
