package com.jasify.schedule.appengine.model.history;

import com.jasify.schedule.appengine.model.payment.Payment;
import org.slim3.datastore.Model;
import org.slim3.datastore.ModelRef;

/**
 * @author krico
 * @since 07/09/15.
 */
@Model
public class PaymentHistory extends History {
    private ModelRef<Payment> paymentRef = new ModelRef<>(Payment.class);

    public PaymentHistory() {
    }

    public PaymentHistory(HistoryTypeEnum type, Payment payment) {
        super(type);
        paymentRef.setModel(payment);
    }

    public ModelRef<Payment> getPaymentRef() {
        return paymentRef;
    }
}
