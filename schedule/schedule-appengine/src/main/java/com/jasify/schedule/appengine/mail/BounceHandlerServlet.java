package com.jasify.schedule.appengine.mail;

import com.google.appengine.api.mail.BounceNotification;
import com.google.appengine.api.mail.BounceNotificationParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author wszarmach
 * @since 28/10/15.
 */

public class BounceHandlerServlet extends HttpServlet {
    private static final Logger log = LoggerFactory.getLogger(BounceHandlerServlet.class);

    @Override
    public void doPost(HttpServletRequest req,
                       HttpServletResponse resp)
            throws IOException {
        try {
            BounceNotification bounce = BounceNotificationParser.parse(req);
            log.warn("Sent e-mail\n\tOriginal From: {}\n" +
                            "\tOriginal To: {}\n" +
                            "\tOriginal Subject: {}\n" +
                            "\tOriginal Text: {}\n" +
                            "\tNotification From: {}\n" +
                            "\tNotification To: {}\n" +
                            "\tNotification Subject: {}\n" +
                            "\tNotification Text: {}",
                    bounce.getOriginal().getFrom(),
                    bounce.getOriginal().getTo(),
                    bounce.getOriginal().getSubject(),
                    bounce.getOriginal().getText(),
                    bounce.getNotification().getFrom(),
                    bounce.getNotification().getTo(),
                    bounce.getNotification().getSubject(),
                    bounce.getNotification().getText());
        } catch (MessagingException e) {
            log.warn("Failed to log bounce notification", e);
        }
    }
}