package com.jasify.schedule.appengine.model.payment.workflow;

import com.google.appengine.api.datastore.Key;
import com.jasify.schedule.appengine.model.payment.Payment;
import com.jasify.schedule.appengine.model.payment.PaymentStateEnum;
import org.slim3.datastore.*;

import java.util.Date;

/**
 * @author krico
 * @since 04/04/15.
 */
@Model
public abstract class PaymentWorkflow implements PaymentWorkflowHandler {
    @Attribute(primaryKey = true)
    private Key id;

    @Attribute(listener = CreationDate.class)
    private Date created;

    @Attribute(listener = ModificationDate.class)
    private Date modified;

    private PaymentStateEnum state = PaymentStateEnum.New;

    private ModelRef<Payment> paymentRef = new ModelRef<>(Payment.class);

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

    public PaymentStateEnum getState() {
        return state;
    }

    public void setState(PaymentStateEnum state) {
        this.state = state;
    }

    public ModelRef<Payment> getPaymentRef() {
        return paymentRef;
    }
}
