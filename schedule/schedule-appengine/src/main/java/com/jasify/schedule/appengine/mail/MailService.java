package com.jasify.schedule.appengine.mail;

import com.jasify.schedule.appengine.model.attachment.Attachment;

import javax.mail.internet.InternetAddress;

/**
 * @author krico
 * @since 26/11/14.
 */
public interface MailService {
    boolean sendToApplicationOwners(String subject, String htmlBody, String textBody);

    boolean send(String toEmail, String subject, String htmlBody, String textBody) throws Exception;

    boolean send(InternetAddress toAddress, String subject, String htmlBody, String textBody, Attachment... attachments);

    boolean send(InternetAddress[] toAddresses, String subject, String htmlBody, String textBody, Attachment... attachments);
}
