package com.jasify.schedule.appengine.mail;

/**
 * @author krico
 * @since 26/11/14.
 */
public interface MailService {
    boolean sendToApplicationOwners(String subject, String body);
}
