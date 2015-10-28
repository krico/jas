package com.jasify.schedule.appengine.mail;

import com.jasify.schedule.appengine.dao.attachment.AttachmentDao;
import com.jasify.schedule.appengine.model.attachment.Attachment;
import com.jasify.schedule.appengine.model.attachment.AttachmentHelper;
import com.jasify.schedule.appengine.util.EnvironmentUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.*;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

/**
 * @author krico
 * @since 17/08/15.
 */
public final class MailDebug {
    public static final String JASIFY_EMAIL_DEBUG_PROP = "jasify.emailDebug";
    private static final Logger log = LoggerFactory.getLogger(MailDebug.class);

    private MailDebug() {
    }

    static void writeDebug(Message message) {
        if (EnvironmentUtil.isDevelopment()) {
            if (Boolean.parseBoolean(System.getProperty(JASIFY_EMAIL_DEBUG_PROP))) {
                try {
                    message = fixMessageWithAttachment(message);
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
                    String filename = sdf.format(new Date()) + ".eml";
                    ByteArrayOutputStream os = new ByteArrayOutputStream();
                    message.writeTo(os);
                    Attachment attachment = AttachmentHelper.create(filename, "message/rfc822", os.toByteArray());
                    new AttachmentDao().save(attachment);
                    log.info("Recorded e-mail\n\tSubject: {}\n\tURL: {}", message.getSubject(), AttachmentHelper.makeDownloadUrl(attachment));
                } catch (Exception e) {
                    log.warn("Failed to write debug email", e);
                }
            }
        } else if (log.isInfoEnabled()) { // TODO: This should be debug
            try {
                log.info("Sent e-mail\n\tFrom: {}\n\tTo: {}\n\tCc: {}\n\tBcc: {}\n\tReplyTo: {}\n\tSent Date: {}\n\tSubject: {}\n\tContent: {}",
                        message.getFrom(),
                        message.getRecipients(Message.RecipientType.TO),
                        message.getRecipients(Message.RecipientType.CC),
                        message.getRecipients(Message.RecipientType.BCC),
                        message.getReplyTo(),
                        message.getSentDate(),
                        message.getSubject(),
                        message.getContent());
            } catch (Exception e) {
                log.warn("Failed to log email", e);
            }
        }
    }

    /*
     * Google mail API doesn't accept proper mail message
     */
    private static Message fixMessageWithAttachment(Message message) throws MessagingException, IOException {
        if (message.isMimeType("multipart/*")) {
            BodyPart textObject = null;
            BodyPart htmlObject = null;
            List<BodyPart> attachments = null;

            Multipart mp;
            mp = (Multipart) message.getContent();
            for (int i = 0; i < mp.getCount(); i++) {
                BodyPart bp = mp.getBodyPart(i);
                if (bp.isMimeType("text/plain") && textObject == null) {
                    textObject = bp;
                } else if (bp.isMimeType("text/html") && htmlObject == null) {
                    htmlObject = bp;
                } else {
                    if (attachments == null) {
                        attachments = new ArrayList<>();
                    }
                    if (!mp.getContentType().startsWith("multipart/")) return message;
                    attachments.add(bp);
                }
            }
            if (attachments != null) {
                log.debug("Converting google non-compliant multipart format to mail reader friendly multipart format...");
                MimeMultipart related = new MimeMultipart("related");
                if (textObject != null || htmlObject != null) {
                    MimeMultipart alternative = new MimeMultipart("alternative");
                    if (textObject != null) alternative.addBodyPart(textObject);
                    if (htmlObject != null) alternative.addBodyPart(htmlObject);
                    MimeBodyPart wrap = new MimeBodyPart();
                    wrap.setContent(alternative);
                    related.addBodyPart(wrap);
                }
                for (BodyPart attachment : attachments) {
                    related.addBodyPart(attachment);
                }
                Session session = Session.getDefaultInstance(new Properties());
                MimeMessage newMessage = new MimeMessage(session);
                newMessage.setContent(related);
                newMessage.setSubject(message.getSubject());
                Address[] from = message.getFrom();
                if (from != null && from.length > 0) newMessage.setFrom(from[0]);
                newMessage.addRecipients(Message.RecipientType.TO, message.getRecipients(Message.RecipientType.TO));
                newMessage.addRecipients(Message.RecipientType.CC, message.getRecipients(Message.RecipientType.CC));
                newMessage.addRecipients(Message.RecipientType.BCC, message.getRecipients(Message.RecipientType.BCC));
                newMessage.setSentDate(message.getSentDate());
                return newMessage;
            }
        }
        return message;
    }
}
