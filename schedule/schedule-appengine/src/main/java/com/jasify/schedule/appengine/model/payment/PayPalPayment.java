package com.jasify.schedule.appengine.model.payment;

import com.google.appengine.api.datastore.Link;
import org.slim3.datastore.Model;

/**
 * @author krico
 * @since 11/01/15.
 */
@Model
public class PayPalPayment extends Payment {
    private String externalId;//EG: PAY-06Y09932M7212011EKSZDC2Y

    private String externalState;

    private String payerId; //We get this on the approval

    private Link selfUrl;

    private Link approveUrl;

    private Link executeUrl;

    public PayPalPayment() {
        super(PaymentTypeEnum.PayPal);
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public String getExternalState() {
        return externalState;
    }

    public void setExternalState(String externalState) {
        this.externalState = externalState;
    }

    public String getPayerId() {
        return payerId;
    }

    public void setPayerId(String payerId) {
        this.payerId = payerId;
    }

    public Link getSelfUrl() {
        return selfUrl;
    }

    public void setSelfUrl(Link selfUrl) {
        this.selfUrl = selfUrl;
    }

    public Link getApproveUrl() {
        return approveUrl;
    }

    public void setApproveUrl(Link approveUrl) {
        this.approveUrl = approveUrl;
    }

    public Link getExecuteUrl() {
        return executeUrl;
    }

    public void setExecuteUrl(Link executeUrl) {
        this.executeUrl = executeUrl;
    }
}
