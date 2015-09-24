package com.jasify.schedule.appengine.model.cart;

import com.google.appengine.api.datastore.Key;
import com.jasify.schedule.appengine.model.activity.Activity;
import com.jasify.schedule.appengine.model.activity.ActivityPackage;
import com.jasify.schedule.appengine.model.payment.PaymentTypeEnum;
import com.jasify.schedule.appengine.util.FormatUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @author krico
 * @since 23/03/15.
 */
public class ShoppingCart implements Serializable {
    private static final Logger Log = LoggerFactory.getLogger(ShoppingCart.class);

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

        for (PaymentTypeEnum paymentType : PaymentTypeEnum.values()) {
            PaymentOption paymentOption = createPaymentOption(paymentType);
            if (paymentOption != null) {
                paymentOptions.add(paymentOption);
            }
        }
        return this;
    }

    private PaymentOption createPaymentOption(PaymentTypeEnum paymentType) {
        switch (paymentType) {
            case Cash: return new PaymentOption.CashPaymentOption();
            case Invoice: return new PaymentOption.InvoicePaymentOption(items);
            case PayPal: return new PaymentOption.PayPalPaymentOption(getTotal());
            default:
                Log.error("Unknown PaymentType {}", paymentType.name());
                return null;
        }
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
}
