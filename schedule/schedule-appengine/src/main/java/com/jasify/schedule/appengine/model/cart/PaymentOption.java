package com.jasify.schedule.appengine.model.cart;

import com.google.appengine.api.datastore.Key;
import com.jasify.schedule.appengine.dao.common.ActivityDao;
import com.jasify.schedule.appengine.model.EntityNotFoundException;
import com.jasify.schedule.appengine.model.activity.Activity;
import com.jasify.schedule.appengine.model.payment.InvoicePaymentProvider;
import com.jasify.schedule.appengine.model.payment.PayPalPaymentProvider;
import com.jasify.schedule.appengine.model.payment.PaymentTypeEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author wszarmach
 * @since 24/09/15.
 */
public class PaymentOption implements Serializable {
    private PaymentTypeEnum paymentType;
    private Double fee;
    private String feeReason;
    private boolean enabled;
    private String disabledReason;

    private PaymentOption() {
    }

    private static String formatNumber(BigDecimal number, int precision) {
        number = number.setScale(2, BigDecimal.ROUND_DOWN);
        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(precision);
        df.setMinimumFractionDigits(precision);
        df.setGroupingUsed(false);
        return df.format(number);
    }

    public PaymentTypeEnum getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(PaymentTypeEnum paymentType) {
        this.paymentType = paymentType;
    }

    public String getFeeReason() {
        return feeReason;
    }

    public void setFeeReason(String feeReason) {
        this.feeReason = feeReason;
    }

    public Double getFee() {
        return fee;
    }

    public void setFee(Double fee) {
        this.fee = fee;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getDisabledReason() {
        return disabledReason;
    }

    public void setDisabledReason(String disabledReason) {
        this.disabledReason = disabledReason;
    }

    public static class CashPaymentOption extends PaymentOption {
        private static final String DisabledReason = "Not supported by supplier";

        public CashPaymentOption() {
            setPaymentType(PaymentTypeEnum.Cash);
            setDisabledReason(DisabledReason);
            setEnabled(false);
            setFee(BigDecimal.ZERO.doubleValue());
        }
    }

    public static class InvoicePaymentOption extends PaymentOption {
        private static final Logger Log = LoggerFactory.getLogger(InvoicePaymentOption.class);
        private static final long AllowedDays = 7;
        private final static long InvoiceDaysBeforePaymentRequiredInMillis = 86400000 * AllowedDays;
        private static final String DisabledReason = "For Invoice payments a minimum of " + AllowedDays + " days is required before the first activity";
        private static final String FeeReason = createFeeReason();

        public InvoicePaymentOption(List<ShoppingCart.Item> items) {
            setPaymentType(PaymentTypeEnum.Invoice);
            setDisabledReason(DisabledReason);
            setEnabled(calculateEnabled(items));
            setFee(InvoicePaymentProvider.INVOICE_FEE_FLAT.doubleValue());
            setFeeReason(FeeReason);
        }

        private static String createFeeReason() {
            String fee = formatNumber(InvoicePaymentProvider.INVOICE_FEE_FLAT, 2);
            return "<nobr>For Electronic Invoice payments</nobr> there is a handling fee of <b><nobr>CHF " + fee + "</nobr></b>";
        }

        private boolean calculateEnabled(List<ShoppingCart.Item> items) {
            ActivityDao activityDao = new ActivityDao();
            Date closestDate = null;
            for (ShoppingCart.Item item : items) {
                try {
                    // TODO: Not the best to do this!!! Must fix
                    if (item.getData() != null) {
                        ArrayList<Key> keyList = (ArrayList<Key>) item.getData();
                        for (Key key : keyList) {
                            Activity activity = activityDao.get(key);
                            if (closestDate == null || activity.getStart().before(closestDate)) {
                                closestDate = activity.getStart();
                            }
                        }
                    } else {
                        Activity activity = activityDao.get(item.getItemId());
                        if (closestDate == null || activity.getStart().before(closestDate)) {
                            closestDate = activity.getStart();
                        }
                    }
                } catch (EntityNotFoundException e) {
                    Log.error("Failed to find Activity {} with key {}", item.getDescription(), item.getItemId(), e);
                }
            }
            return closestDate == null || closestDate.getTime() > System.currentTimeMillis() + InvoiceDaysBeforePaymentRequiredInMillis;
        }
    }

    public static class PayPalPaymentOption extends PaymentOption {
        private static final String FeeReason = createFeeReason();

        public PayPalPaymentOption(double cost) {
            setPaymentType(PaymentTypeEnum.PayPal);
            setEnabled(true);
            setFee(PayPalPaymentProvider.calculateHandlingFee(cost));
            setFeeReason(FeeReason);
        }

        private static String createFeeReason() {
            String percentageFee = formatNumber(PayPalPaymentProvider.PAY_PAL_FEE_MULTIPLIER.multiply(BigDecimal.valueOf(100)), 2);
            String flatFee = formatNumber(PayPalPaymentProvider.PAY_PAL_FEE_FLAT, 2);
            return "<nobr>For PayPal payments there</nobr> is a handling fee of <b>" + percentageFee + "%</b> plus <b><nobr>CHF " + flatFee + "</nobr></b>";
        }
    }
}
