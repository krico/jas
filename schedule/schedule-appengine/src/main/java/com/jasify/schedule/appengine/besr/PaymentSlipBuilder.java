package com.jasify.schedule.appengine.besr;

/**
 * @author krico
 * @since 29/07/15.
 */
public class PaymentSlipBuilder {
    String account;
    String codeLine;
    String referenceCode;
    String currency = "CHF";
    String recipient;
    String amount;

    public PaymentSlipBuilder account(String account) {
        this.account = account;
        return this;
    }

    public PaymentSlipBuilder codeLine(String codeLine) {
        this.codeLine = codeLine;
        return this;
    }

    public PaymentSlipBuilder referenceCode(String referenceCode) {
        this.referenceCode = referenceCode;
        return this;
    }

    public PaymentSlipBuilder currency(String currency) {
        this.currency = currency;
        return this;
    }

    public PaymentSlipBuilder recipient(String recipient) {
        this.recipient = recipient;
        return this;
    }

    public PaymentSlipBuilder amount(String amount) {
        this.amount = amount;
        return this;
    }

    public PaymentSlip build() {
        return new PaymentSlip(this);
    }

}
