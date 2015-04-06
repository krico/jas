package com.jasify.schedule.appengine.model.cart;

import com.google.appengine.api.datastore.Key;
import com.jasify.schedule.appengine.model.activity.Activity;
import com.jasify.schedule.appengine.model.payment.PayPalPaymentProvider;
import com.jasify.schedule.appengine.util.FormatUtil;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @author krico
 * @since 23/03/15.
 */
public class ShoppingCart implements Serializable {
    private String id;
    private String currency = "CHF";
    private Double total;
    private Double grandTotal;
    private List<Item> items = new ArrayList<>();

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

    public Double getGrandTotal() {
        return grandTotal;
    }

    public void setGrandTotal(Double grandTotal) {
        this.grandTotal = grandTotal;
    }

    public List<Item> getItems() {
        return items;
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
        double fee = PayPalPaymentProvider.calculateHandlingFee(getTotal());
        setGrandTotal(getTotal() + fee);
        return this;
    }

    public static class ItemBuilder {
        private Key itemId;
        private String description;
        private int units;
        private double price;

        public ItemBuilder activity(Activity activity) {
            description = FormatUtil.toString(activity);
            units = 1;
            price = activity.getPrice();
            itemId = activity.getId();
            return this;
        }

        public Item build() {
            Item item = new Item(description, units, price);
            item.setItemId(itemId);
            return item;
        }
    }

    public static class Item implements Serializable {
        private int ordinal;
        private Key itemId;
        private String description;
        private int units;
        private double price;

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
    }
}
