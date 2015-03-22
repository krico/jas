package com.jasify.schedule.appengine.model.payment;

import com.google.appengine.api.datastore.Key;
import com.google.common.base.Preconditions;
import com.jasify.schedule.appengine.model.balance.HasTransfer;
import com.jasify.schedule.appengine.model.balance.Transfer;
import com.jasify.schedule.appengine.model.users.User;
import org.slim3.datastore.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @author krico
 * @since 11/01/15.
 */
@Model
public class Payment implements HasTransfer {
    @Attribute(primaryKey = true)
    private Key id;

    @Attribute(listener = CreationDate.class)
    private Date created;

    @Attribute(listener = ModificationDate.class)
    private Date modified;

    /**
     * This will define what type of payment this is.
     * for example if type = {@link PaymentTypeEnum#PayPal} the actual class
     * should be {@link com.jasify.schedule.appengine.model.payment.PayPalPayment}.
     */
    private PaymentTypeEnum type;

    private PaymentStateEnum state;

    private String currency;

    private Double amount;

    /**
     * Fees charged on top of the amount (calculated by jasify)
     */
    private Double fee;

    /**
     * Fees charged by the external system (e.g. payPal)
     */
    private Double realFee;

    private List<String> itemDescriptions = new ArrayList<>();

    private List<Integer> itemUnits = new ArrayList<>();

    private List<Double> itemPrices = new ArrayList<>();

    private ModelRef<Transfer> transferRef = new ModelRef<>(Transfer.class);

    private ModelRef<User> userRef = new ModelRef<>(User.class);

    public Payment() {
        state = PaymentStateEnum.New;
    }

    public Payment(PaymentTypeEnum type) {
        this();
        this.type = type;
    }

    @Override
    public Key getId() {
        return id;
    }

    public void setId(Key id) {
        this.id = id;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getModified() {
        return modified;
    }

    public void setModified(Date modified) {
        this.modified = modified;
    }

    public PaymentTypeEnum getType() {
        return type;
    }

    public void setType(PaymentTypeEnum type) {
        this.type = type;
    }

    public PaymentStateEnum getState() {
        return state;
    }

    public void setState(PaymentStateEnum state) {
        this.state = state;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Double getFee() {
        return fee;
    }

    public void setFee(Double fee) {
        this.fee = fee;
    }

    public Double getRealFee() {
        return realFee;
    }

    public void setRealFee(Double realFee) {
        this.realFee = realFee;
    }

    public List<String> getItemDescriptions() {
        return itemDescriptions;
    }

    public void setItemDescriptions(List<String> itemDescriptions) {
        this.itemDescriptions = itemDescriptions;
    }

    public List<Integer> getItemUnits() {
        return itemUnits;
    }

    public void setItemUnits(List<Integer> itemUnits) {
        this.itemUnits = itemUnits;
    }

    public List<Double> getItemPrices() {
        return itemPrices;
    }

    public void setItemPrices(List<Double> itemPrices) {
        this.itemPrices = itemPrices;
    }

    public void addItem(String description, int units, double price) {
        if (amount == null) amount = 0d;
        validate();
        amount += units * price;
        itemDescriptions.add(description);
        itemUnits.add(units);
        itemPrices.add(price);
    }

    public void addItemRaw(String description, int units, double price) {
        itemDescriptions.add(description);
        itemUnits.add(units);
        itemPrices.add(price);
    }

    public int getItemCount() {
        validate();
        return itemDescriptions.size();
    }

    public Item getItem(int index) {
        return new Item(index);
    }

    /**
     * Validates this payment
     *
     * @throws java.lang.NullPointerException  if any of itemDescriptions, itemUnits or itemPrices currency or amount are null
     * @throws java.lang.IllegalStateException if the sizes are not all the same or the sum of the units is not the same as the amount
     */
    public void validate() {
        Preconditions.checkNotNull(currency, "currency");
        Preconditions.checkNotNull(amount, "amount");
        Preconditions.checkNotNull(itemDescriptions, "itemDescriptions");
        Preconditions.checkNotNull(itemUnits, "itemUnits");
        Preconditions.checkNotNull(itemPrices, "itemPrices");

        Preconditions.checkState(itemDescriptions.size() == itemUnits.size() && itemUnits.size() == itemPrices.size(),
                "Please use addItem method");

        if (!itemDescriptions.isEmpty()) {
            double amount = 0d;
            for (int i = 0; i < itemDescriptions.size(); ++i) {
                amount += itemUnits.get(i) * itemPrices.get(i);
            }
            if (fee != null) amount += fee;
            Preconditions.checkState(getAmount() == amount, "Amount expected: " + amount + ", actual: " + getAmount());
        }
    }

    @Override
    public ModelRef<Transfer> getTransferRef() {
        return transferRef;
    }

    public ModelRef<User> getUserRef() {
        return userRef;
    }

    public String reference() {
        return Objects.toString(id);
    }

    public String describe() {
        StringBuilder builder = new StringBuilder();
        for (String itemDescription : itemDescriptions) {
            builder.append('[').append(itemDescription).append(']');
        }
        return builder.toString();
    }

    @Override
    public String toString() {
        return "Payment{" +
                "id=" + id +
                ", created=" + created +
                ", modified=" + modified +
                ", type=" + type +
                ", state=" + state +
                ", currency='" + currency + '\'' +
                ", amount=" + amount +
                ", fee=" + fee +
                ", realFee=" + realFee +
                ", itemDescriptions=" + itemDescriptions +
                ", itemUnits=" + itemUnits +
                ", itemPrices=" + itemPrices +
                '}';
    }

    public class Item {
        private final int index;

        public Item(int index) {
            this.index = index;
        }

        public String getDescription() {
            return itemDescriptions.get(index);
        }

        public int getUnits() {
            return itemUnits.get(index);
        }

        public double getPrice() {
            return itemPrices.get(index);
        }
    }
}
