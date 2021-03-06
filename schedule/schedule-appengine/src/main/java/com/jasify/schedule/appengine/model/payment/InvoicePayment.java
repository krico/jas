package com.jasify.schedule.appengine.model.payment;

import com.google.common.base.Preconditions;
import com.jasify.schedule.appengine.model.attachment.Attachment;
import org.apache.commons.lang3.StringUtils;
import org.slim3.datastore.Model;
import org.slim3.datastore.ModelRef;

/**
 * @author krico
 * @since 05/08/15.
 */
@Model
public class InvoicePayment extends Payment {
    private String account;

    private String subscriber;

    private String referenceCode;

    private String recipient;

    private int expireDays = InvoicePaymentProvider.INVOICE_EXPIRES_DAYS;

    private ModelRef<Attachment> attachmentRef = new ModelRef<>(Attachment.class);

    public InvoicePayment() {
        super(PaymentTypeEnum.Invoice);
    }

    public String getReferenceCode() {
        return referenceCode;
    }

    public void setReferenceCode(String referenceCode) {
        this.referenceCode = referenceCode;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getSubscriber() {
        return subscriber;
    }

    public void setSubscriber(String subscriber) {
        this.subscriber = subscriber;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public int getExpireDays() {
        return expireDays;
    }

    public void setExpireDays(int expireDays) {
        this.expireDays = expireDays;
    }

    public ModelRef<Attachment> getAttachmentRef() {
        return attachmentRef;
    }

    @Override
    public void validate() {
        super.validate();
        Preconditions.checkNotNull(account, "InvoicePayment.account");
        Preconditions.checkNotNull(referenceCode, "InvoicePayment.referenceCode");
        Preconditions.checkNotNull(recipient, "InvoicePayment.recipient");
    }

    public String describe() {
        return super.describe() + " (Invoice REF=" + referenceCode + ")";
    }

    @Override
    public String toString() {
        return "InvoicePayment{" +
                super.toString() +
                ", referenceCode='" + referenceCode + '\'' +
                ", expireDays='" + expireDays + '\'' +
                ", account='" + account + '\'' +
                ", subscriber='" + subscriber + '\'' +
                ", recipient='" + StringUtils.replace(recipient, "\n", "|") + '\'' +
                '}';
    }

}
