package com.jasify.schedule.appengine.model.cart;

import com.google.appengine.api.datastore.Key;
import com.jasify.schedule.appengine.dao.common.ActivityDao;
import com.jasify.schedule.appengine.model.EntityNotFoundException;
import com.jasify.schedule.appengine.model.activity.Activity;
import com.jasify.schedule.appengine.model.activity.ActivityPackage;
import com.jasify.schedule.appengine.model.payment.InvoicePaymentProvider;
import com.jasify.schedule.appengine.model.payment.PayPalPaymentProvider;
import com.jasify.schedule.appengine.model.payment.PaymentTypeEnum;
import com.jasify.schedule.appengine.util.FormatUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author krico
 * @since 23/03/15.
 */
public class ShoppingCart implements Serializable {
    private static final Logger log = LoggerFactory.getLogger(ShoppingCart.class);
    private final static long InvoiceDaysBeforePaymentRequiredInMillis = 86400000 * 7;
    private String id;
    private String currency = "CHF";
    private Double total;
    private List<Item> items = new ArrayList<>();
    private List<PaymentOption> paymentOptions = new ArrayList<>();

    public ShoppingCart() {
    }

    public ShoppingCart(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public List<Item> getItems() {
        return items;
    }

    public List<PaymentOption> getPaymentOptions() {
        return paymentOptions;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public ShoppingCart calculate() {
        BigDecimal total = BigDecimal.ZERO;
        for (int i = 0; i < items.size(); ++i) {
            Item item = items.get(i);
            item.setOrdinal(i);
            BigDecimal itemPrice = new BigDecimal(item.getPrice());
            itemPrice = itemPrice.multiply(new BigDecimal(item.getUnits()));
            total = total.add(itemPrice);
        }
        setTotal(total.doubleValue());

        for (PaymentTypeEnum paymentTypeEnum : PaymentTypeEnum.values()) {
            getPaymentOptions().add(createPaymentOptions(paymentTypeEnum));
        }
        return this;
    }

    private Date getClosestItemDate() {
        ActivityDao activityDao = new ActivityDao();
        Date closestDate = null;
        for (Item item : items) {
            try {
                // TODO: Not the best to do this!!! Must fix
                if (item.getData() != null) {
                    ArrayList<Key> keyList = (ArrayList<Key>)item.getData();
                    for (Key key  : keyList) {
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
                log.error("Failed to find Activity {} with key {}", item.getDescription(), item.getItemId(), e);
            }
        }
        return closestDate;
    }

    private PaymentOption createPaymentOptions(PaymentTypeEnum paymentType) {
        PaymentOption paymentOption = new PaymentOption(paymentType);
        paymentOption.setEnabled(true);
        switch (paymentType) {
            case Invoice:
                paymentOption.setFee(InvoicePaymentProvider.INVOICE_FEE_FLAT);
                paymentOption.setFeeReason("<nobr>For Electronic Invoice payments</nobr> there is a handling fee of <b><nobr>CHF 1.00</nobr></b>");
                Date closestDate = getClosestItemDate();
                if (closestDate != null && closestDate.getTime() < System.currentTimeMillis() + InvoiceDaysBeforePaymentRequiredInMillis) {
                    paymentOption.setEnabled(false);
                    paymentOption.setDisabledReason("For Invoice payments a minimum of 7 days is required before the first activity");
                }
                break;
            case PayPal:
                paymentOption.setFee(PayPalPaymentProvider.calculateHandlingFee(getTotal()));
                paymentOption.setFeeReason("<nobr>For PayPal payments there</nobr> is a handling fee of <b>3.5%</b> plus <b><nobr>CHF 0.55</nobr></b>");
                break;
            default:
                paymentOption.setEnabled(false);
                paymentOption.setFee(BigDecimal.ZERO.doubleValue());
                // TODO
                paymentOption.setDisabledReason("Not supported by supplier");
                break;
        }
        return paymentOption;
    }

    public static class ItemBuilder {
        private Key itemId;
        private String description;
        private int units;
        private double price;
        private Object data;

        public ItemBuilder activity(Activity activity) {
            description = FormatUtil.toString(activity);
            units = 1;
            price = activity.getPrice();
            itemId = activity.getId();
            return this;
        }

        public ItemBuilder activityPackage(ActivityPackage activityPackage) {
            description = FormatUtil.toString(activityPackage);
            units = 1;
            price = activityPackage.getPrice();
            itemId = activityPackage.getId();
            return this;
        }

        public ItemBuilder data(Object data) {
            this.data = data;
            return this;
        }

        public Item build() {
            Item item = new Item(description, units, price);
            item.setItemId(itemId);
            item.setData(data);
            return item;
        }
    }

    public static class Item implements Serializable {
        private int ordinal;
        private Key itemId;
        private String description;
        private int units;
        private double price;
        private Object data;

        public Item() {
        }

        public Item(String description, int units, double price) {
            this.description = description;
            this.units = units;
            this.price = price;
        }

        public int getOrdinal() {
            return ordinal;
        }

        public void setOrdinal(int ordinal) {
            this.ordinal = ordinal;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public int getUnits() {
            return units;
        }

        public void setUnits(int units) {
            this.units = units;
        }

        public double getPrice() {
            return price;
        }

        public void setPrice(double price) {
            this.price = price;
        }

        public Key getItemId() {
            return itemId;
        }

        public void setItemId(Key itemId) {
            this.itemId = itemId;
        }

        public Object getData() {
            return data;
        }

        public void setData(Object data) {
            this.data = data;
        }
    }

    public static class PaymentOption implements Serializable {
        private PaymentTypeEnum paymentType;
        private Double fee;
        private String feeReason;
        private boolean enabled;
        private String disabledReason;

        public PaymentOption(PaymentTypeEnum paymentType){
            setPaymentType(paymentType);
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
    }

}
