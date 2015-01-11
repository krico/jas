package com.jasify.schedule.appengine.model.payment;

import com.google.appengine.api.datastore.Key;
import org.slim3.datastore.Attribute;
import org.slim3.datastore.CreationDate;
import org.slim3.datastore.Model;
import org.slim3.datastore.ModificationDate;

import java.util.Date;

/**
 * @author krico
 * @since 11/01/15.
 */
@Model
public class Payment {
    @Attribute(primaryKey = true)
    private Key id;

    @Attribute(listener = CreationDate.class)
    private Date created;

    @Attribute(listener = ModificationDate.class)
    private Date modified;

    /**
     * This will define what type of payment this is.
     * for example if type = {@link PaymentTypeEnum#PayPal} the actual class
     * should be {@link com.jasify.schedule.appengine.model.payment.PayPalPayment}.
     */
    private PaymentTypeEnum type;

    private PaymentStateEnum state;

    private String currency;

    private Double amount;

    /**
     * Fees charged on top of the amount
     */
    private Double fee;

    public Payment() {
        state = PaymentStateEnum.New;
    }

    public Payment(PaymentTypeEnum type) {
        this();
        this.type = type;
    }

    public Key getId() {
        return id;
    }

    public void setId(Key id) {
        this.id = id;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getModified() {
        return modified;
    }

    public void setModified(Date modified) {
        this.modified = modified;
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
}
