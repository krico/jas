package com.jasify.schedule.appengine.mail;

/**
 * @author krico
 * @since 26/11/14.
 */
public final class MailServiceFactory {
    private MailServiceFactory() {
    }

    public static MailService getMailService() {
        return DefaultMailService.instance();
    }
}
