package com.jasify.schedule.appengine.model.payment;

import com.google.api.client.http.GenericUrl;
import com.jasify.schedule.appengine.model.SequenceGenerator;

/**
 * @author krico
 * @since 05/08/15.
 */
public class InvoicePaymentProvider implements PaymentProvider<InvoicePayment> {
    private final SequenceGenerator referenceCodeGenerator = new SequenceGenerator("BESR Reference Code");

    private InvoicePaymentProvider() {
    }

    public static InvoicePaymentProvider instance() {
        return Singleton.INSTANCE;
    }

    @Override
    public InvoicePayment newPayment() {
        InvoicePayment payment = new InvoicePayment();
        //TODO: Fetch these values from ApplicationData
        payment.setRecipient("Jasify");
        payment.setAccount("1-2345-6");
        payment.setReferenceCode(Long.toString(referenceCodeGenerator.next()));
        return payment;
    }

    @Override
    public void createPayment(InvoicePayment payment, GenericUrl baseUrl) throws PaymentException {
        payment.validate();
        payment.setState(PaymentStateEnum.Created);
    }

    @Override
    public void executePayment(InvoicePayment payment) throws PaymentException {
        payment.setState(PaymentStateEnum.Completed);
    }

    private static final class Singleton {
        private static final InvoicePaymentProvider INSTANCE = new InvoicePaymentProvider();
    }
}
