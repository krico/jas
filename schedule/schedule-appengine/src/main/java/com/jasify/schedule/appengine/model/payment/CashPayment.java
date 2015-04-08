package com.jasify.schedule.appengine.model.payment;

import com.jasify.schedule.appengine.model.users.User;
import org.slim3.datastore.Model;
import org.slim3.datastore.ModelRef;

/**
 * @author krico
 * @since 08/04/15.
 */
@Model
public class CashPayment extends Payment {
    private boolean confirmed;

    private String comment;

    private ModelRef<User> confirmedByRef = new ModelRef<>(User.class);

    public CashPayment() {
        super(PaymentTypeEnum.Cash);
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public void setConfirmed(boolean confirmed) {
        this.confirmed = confirmed;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public ModelRef<User> getConfirmedByRef() {
        return confirmedByRef;
    }

    public String describe() {
        return super.describe() + " (cash)";
    }

}