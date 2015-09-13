package com.jasify.schedule.appengine.template;

/**
 * A place to hard-code all template paths...
 *
 * @author krico
 * @since 20/08/15.
 */
public interface TemplateNames {
    String STYLES_CSS = "styles.css";
    String JASIFY_NEW_VERSION_HTML = "jasify/NewVersion.html.vm";
    String JASIFY_NEW_VERSION_TXT = "jasify/NewVersion.txt.vm";
    String JASIFY_NEW_USER_HTML = "jasify/NewUser.html.vm";
    String JASIFY_NEW_USER_TXT = "jasify/NewUser.txt.vm";
    String SUBSCRIBER_PASSWORD_RECOVERY_HTML = "subscriber/PasswordRecovery.html.vm";
    String SUBSCRIBER_PASSWORD_RECOVERY_TXT = "subscriber/PasswordRecovery.txt.vm";

    String SUBSCRIBER_INVOICE_PAYMENT_CREATED_HTML = "subscriber/InvoicePaymentCreated.html.vm";
    String SUBSCRIBER_INVOICE_PAYMENT_CREATED_TXT = "subscriber/InvoicePaymentCreated.txt.vm";
    String PUBLISHER_INVOICE_PAYMENT_CREATED_HTML = "publisher/InvoicePaymentCreated.html.vm";
    String PUBLISHER_INVOICE_PAYMENT_CREATED_TXT = "publisher/InvoicePaymentCreated.txt.vm";

    String SUBSCRIBER_INVOICE_PAYMENT_CANCELLED_HTML = "subscriber/InvoicePaymentCancelled.html.vm";
    String SUBSCRIBER_INVOICE_PAYMENT_CANCELLED_TXT = "subscriber/InvoicePaymentCancelled.txt.vm";
    String PUBLISHER_INVOICE_PAYMENT_CANCELLED_HTML = "publisher/InvoicePaymentCancelled.html.vm";
    String PUBLISHER_INVOICE_PAYMENT_CANCELLED_TXT = "publisher/InvoicePaymentCancelled.txt.vm";

    String SUBSCRIBER_INVOICE_PAYMENT_EXECUTED_HTML = "subscriber/InvoicePaymentExecuted.html.vm";
    String SUBSCRIBER_INVOICE_PAYMENT_EXECUTED_TXT = "subscriber/InvoicePaymentExecuted.txt.vm";
    String PUBLISHER_INVOICE_PAYMENT_EXECUTED_HTML = "publisher/InvoicePaymentExecuted.html.vm";
    String PUBLISHER_INVOICE_PAYMENT_EXECUTED_TXT = "publisher/InvoicePaymentExecuted.txt.vm";
}
