package com.jasify.schedule.appengine.mail;

/**
 * @author krico
 * @since 26/11/14.
 */
public interface MailService {
    boolean sendToApplicationOwners(String subject, String htmlBody, String textBody);
    boolean send(String toEmail, String subject, String htmlBody, String textBody) throws Exception;
}
