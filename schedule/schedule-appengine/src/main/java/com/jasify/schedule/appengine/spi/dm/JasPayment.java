package com.jasify.schedule.appengine.spi.dm;

import com.jasify.schedule.appengine.model.payment.PaymentStateEnum;
import com.jasify.schedule.appengine.model.payment.PaymentTypeEnum;

import java.util.Date;

/**
 * @author krico
 * @since 03/09/15.
 */
public class JasPayment {
    private String id;

    private Date created;

    private PaymentTypeEnum type;

    private PaymentStateEnum state;

    private String currency;

    private Double amount;

    private Double fee;

    private Double realFee;

    private String transferId;

    private String userId;

    /**
     * For {@link com.jasify.schedule.appengine.model.payment.InvoicePayment}
     */
    private String referenceCode;
    /**
     * For {@link com.jasify.schedule.appengine.model.payment.InvoicePayment}
     */
    private Integer expireDays;

    /**
     * For {@link com.jasify.schedule.appengine.model.payment.PayPalPayment}
     */
    private String externalId;

    /**
     * For {@link com.jasify.schedule.appengine.model.payment.PayPalPayment}
     */
    private String externalState;

    /**
     * For {@link com.jasify.schedule.appengine.model.payment.PayPalPayment}
     */
    private String payerId;

    /**
     * For {@link com.jasify.schedule.appengine.model.payment.PayPalPayment}
     */
    private String payerEmail;

    /**
     * For {@link com.jasify.schedule.appengine.model.payment.PayPalPayment}
     */
    private String payerFirstName;

    /**
     * For {@link com.jasify.schedule.appengine.model.payment.PayPalPayment}
     */
    private String payerLastName;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public PaymentTypeEnum getType() {
        return type;
    }

    public void setType(PaymentTypeEnum type) {
        this.type = type;
    }

    public PaymentStateEnum getState() {
        return state;
    }

    public void setState(PaymentStateEnum state) {
        this.state = state;
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

    public Double getFee() {
        return fee;
    }

    public void setFee(Double fee) {
        this.fee = fee;
    }

    public Double getRealFee() {
        return realFee;
    }

    public void setRealFee(Double realFee) {
        this.realFee = realFee;
    }

    public String getTransferId() {
        return transferId;
    }

    public void setTransferId(String transferId) {
        this.transferId = transferId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getReferenceCode() {
        return referenceCode;
    }

    public void setReferenceCode(String referenceCode) {
        this.referenceCode = referenceCode;
    }

    public Integer getExpireDays() {
        return expireDays;
    }

    public void setExpireDays(Integer expireDays) {
        this.expireDays = expireDays;
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

    public String getPayerEmail() {
        return payerEmail;
    }

    public void setPayerEmail(String payerEmail) {
        this.payerEmail = payerEmail;
    }

    public String getPayerFirstName() {
        return payerFirstName;
    }

    public void setPayerFirstName(String payerFirstName) {
        this.payerFirstName = payerFirstName;
    }

    public String getPayerLastName() {
        return payerLastName;
    }

    public void setPayerLastName(String payerLastName) {
        this.payerLastName = payerLastName;
    }
}
