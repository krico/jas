package com.jasify.schedule.appengine.besr;

import com.google.common.base.Preconditions;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Locale;

/**
 * @author krico
 * @since 29/07/15.
 */
public class PaymentSlipBuilder {

    SlipTypeEnum slipType;
    String account;
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

    public PaymentSlipBuilder amount(double amount) {
        int natural = (int) amount;
        int rem = (int) ((amount - natural) * 100);
        this.amount = String.valueOf(natural) + String.format(Locale.ROOT, "%02d", rem);
        return this;
    }

    public PaymentSlip build() {
        if (StringUtils.isBlank(referenceCode)) {
            referenceCode = new ReferenceCode(identificationNumber, invoiceNumber).toReferenceCode();
        }
        if (StringUtils.isBlank(codeLine)) {
            codeLine = new CodeLine(slipType, amount, referenceCode, account).toCodeLine();
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
        if (recipient != null ? !recipient.equals(that.recipient) : that.recipient != null) return false;
        if (referenceCode != null ? !referenceCode.equals(that.referenceCode) : that.referenceCode != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = account != null ? account.hashCode() : 0;
        result = 31 * result + (codeLine != null ? codeLine.hashCode() : 0);
        result = 31 * result + (referenceCode != null ? referenceCode.hashCode() : 0);
        result = 31 * result + (currency != null ? currency.hashCode() : 0);
        result = 31 * result + (recipient != null ? recipient.hashCode() : 0);
        result = 31 * result + (amount != null ? amount.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "PaymentSlipBuilder{" +
                "account='" + account + '\'' +
                ", codeLine='" + codeLine + '\'' +
                ", referenceCode='" + referenceCode + '\'' +
                ", currency='" + currency + '\'' +
                ", recipient='" + recipient + '\'' +
                ", amount='" + amount + '\'' +
                '}';
    }
}
