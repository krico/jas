package com.jasify.schedule.appengine.spi.dm;

import com.jasify.schedule.appengine.model.payment.PaymentTypeEnum;

/**
 * @author krico
 * @since 24/02/15.
 */
public class JasPaymentRequest {
    private String baseUrl;

    private String currency;

    private Double amount;

    private PaymentTypeEnum type;

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public PaymentTypeEnum getType() {
        return type;
    }

    public void setType(PaymentTypeEnum type) {
        this.type = type;
    }
}
