package com.jasify.schedule.appengine.model.cart;

import com.jasify.schedule.appengine.model.payment.PayPalPaymentProvider;

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

    public void calculate() {
        BigDecimal total = BigDecimal.ZERO.setScale(2);
        for (Item item : items) {
            BigDecimal itemPrice = new BigDecimal(item.getPrice()).setScale(2, BigDecimal.ROUND_CEILING);
            itemPrice = itemPrice.multiply(new BigDecimal(item.getUnits()));
            total = total.add(itemPrice);
        }
        setTotal(total.doubleValue());
        double fee = PayPalPaymentProvider.calculateHandlingFee(getTotal());
        setGrandTotal(getTotal() + fee);
    }

    public static class Item implements Serializable{
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
    }
}
