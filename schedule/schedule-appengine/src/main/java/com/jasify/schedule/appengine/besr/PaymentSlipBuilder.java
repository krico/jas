package com.jasify.schedule.appengine.besr;

import org.apache.commons.lang3.StringUtils;

import java.util.Locale;

/**
 * @author krico
 * @since 29/07/15.
 */
public class PaymentSlipBuilder {

    SlipTypeEnum slipType;
    String account;
    String subscriber;
    String codeLine;
    String referenceCode;
    String currency = "CHF";
    String recipient;
    String amount;
    String identificationNumber;
    String invoiceNumber;


    PaymentSlipBuilder() {
    }

    public static PaymentSlipBuilder isrChf() {
        return new PaymentSlipBuilder().slipType(SlipTypeEnum.ISR_CHF);
    }

    public static PaymentSlipBuilder isrPlusChf() {
        return new PaymentSlipBuilder().slipType(SlipTypeEnum.ISR_Plus_CHF);
    }

    public PaymentSlipBuilder slipType(SlipTypeEnum slipType) {
        this.slipType = slipType;
        return this;
    }

    public PaymentSlipBuilder identificationNumber(String identificationNumber) {
        this.identificationNumber = identificationNumber;
        return this;
    }

    public PaymentSlipBuilder invoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
        return this;
    }

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

    public PaymentSlipBuilder subscriber(String subscriber) {
        this.subscriber = subscriber;
        return this;
    }

    public PaymentSlipBuilder amount(double amount) {
        this.amount = String.format(Locale.ROOT, "%.2f", amount).replace(".", "");
        return this;
    }

    public PaymentSlip build() {
        if (StringUtils.isBlank(referenceCode)) {
            referenceCode = new ReferenceCode(identificationNumber, invoiceNumber).toReferenceCode();
        }
        if (StringUtils.isBlank(subscriber)) {
            subscriber = account;
        }
        if (StringUtils.isBlank(codeLine)) {
            codeLine = new CodeLine(slipType, amount, referenceCode, subscriber).toCodeLine();
        }
        return new PaymentSlip(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PaymentSlipBuilder that = (PaymentSlipBuilder) o;

        if (account != null ? !account.equals(that.account) : that.account != null) return false;
        if (amount != null ? !amount.equals(that.amount) : that.amount != null) return false;
        if (codeLine != null ? !codeLine.equals(that.codeLine) : that.codeLine != null) return false;
        if (currency != null ? !currency.equals(that.currency) : that.currency != null) return false;
        if (identificationNumber != null ? !identificationNumber.equals(that.identificationNumber) : that.identificationNumber != null)
            return false;
        if (invoiceNumber != null ? !invoiceNumber.equals(that.invoiceNumber) : that.invoiceNumber != null)
            return false;
        if (recipient != null ? !recipient.equals(that.recipient) : that.recipient != null) return false;
        if (referenceCode != null ? !referenceCode.equals(that.referenceCode) : that.referenceCode != null)
            return false;
        if (slipType != that.slipType) return false;
        if (subscriber != null ? !subscriber.equals(that.subscriber) : that.subscriber != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = slipType != null ? slipType.hashCode() : 0;
        result = 31 * result + (account != null ? account.hashCode() : 0);
        result = 31 * result + (subscriber != null ? subscriber.hashCode() : 0);
        result = 31 * result + (codeLine != null ? codeLine.hashCode() : 0);
        result = 31 * result + (referenceCode != null ? referenceCode.hashCode() : 0);
        result = 31 * result + (currency != null ? currency.hashCode() : 0);
        result = 31 * result + (recipient != null ? recipient.hashCode() : 0);
        result = 31 * result + (amount != null ? amount.hashCode() : 0);
        result = 31 * result + (identificationNumber != null ? identificationNumber.hashCode() : 0);
        result = 31 * result + (invoiceNumber != null ? invoiceNumber.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "PaymentSlipBuilder{" +
                "slipType=" + slipType +
                ", account='" + account + '\'' +
                ", subscriber='" + subscriber + '\'' +
                ", codeLine='" + codeLine + '\'' +
                ", referenceCode='" + referenceCode + '\'' +
                ", currency='" + currency + '\'' +
                ", recipient='" + recipient + '\'' +
                ", amount='" + amount + '\'' +
                ", identificationNumber='" + identificationNumber + '\'' +
                ", invoiceNumber='" + invoiceNumber + '\'' +
                '}';
    }
}
