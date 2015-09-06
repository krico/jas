package com.jasify.schedule.appengine.spi.transform;

import com.google.api.server.spi.config.Transformer;
import com.jasify.schedule.appengine.model.payment.InvoicePayment;
import com.jasify.schedule.appengine.model.payment.PayPalPayment;
import com.jasify.schedule.appengine.model.payment.Payment;
import com.jasify.schedule.appengine.spi.dm.JasPayment;

/**
 * @author krico
 * @since 10/08/15.
 */
public class JasPaymentTransformer implements Transformer<Payment, JasPayment> {
    private final JasKeyTransformer keyTransformer = new JasKeyTransformer();

    /*
    private PaymentStateEnum state;

    private String currency;

    private Double amount;

    private Double fee;

    private Double realFee;
     */
    @Override
    public JasPayment transformTo(Payment internal) {
        JasPayment external = new JasPayment();
        external.setId(keyTransformer.transformTo(internal.getId()));
        external.setUserId(keyTransformer.transformTo(internal.getUserRef().getKey()));
        external.setTransferId(keyTransformer.transformTo(internal.getTransferRef().getKey()));
        external.setCreated(internal.getCreated());
        external.setType(internal.getType());
        external.setState(internal.getState());
        external.setCurrency(internal.getCurrency());
        external.setAmount(internal.getAmount());
        external.setFee(internal.getFee());
        external.setRealFee(internal.getRealFee());

        if (internal instanceof InvoicePayment) {
            InvoicePayment iInternal = (InvoicePayment) internal;
            external.setReferenceCode(iInternal.getReferenceCode());
            external.setExpireDays(iInternal.getExpireDays());
        } else if (internal instanceof PayPalPayment) {
            PayPalPayment pInternal = (PayPalPayment) internal;
            external.setExternalId(pInternal.getExternalId());
            external.setExternalState(pInternal.getExternalState());
            external.setPayerId(pInternal.getPayerId());
            external.setPayerEmail(pInternal.getPayerEmail());
            external.setPayerFirstName(pInternal.getPayerFirstName());
            external.setPayerLastName(pInternal.getPayerLastName());

        }
        return external;
    }

    @Override
    public Payment transformFrom(JasPayment external) {
        throw new UnsupportedOperationException("Update of Payment not supported");
    }
}
