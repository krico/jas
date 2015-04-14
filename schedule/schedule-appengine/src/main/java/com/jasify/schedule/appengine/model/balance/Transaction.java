package com.jasify.schedule.appengine.model.balance;

import com.google.appengine.api.datastore.Key;
import org.slim3.datastore.Attribute;
import org.slim3.datastore.CreationDate;
import org.slim3.datastore.Model;
import org.slim3.datastore.ModelRef;

import java.util.Date;

/**
 * A single leg of a {@link com.jasify.schedule.appengine.model.balance.Transfer}, either a credit or a debit
 *
 * @author krico
 * @since 19/02/15.
 */
@Model
public class Transaction {
    @Attribute(primaryKey = true)
    private Key id;

    @Attribute(listener = CreationDate.class)
    private Date created;

    private String currency;

    private double amount;

    private double unpaid;

    private String description;

    private String reference;

    private ModelRef<Account> accountRef = new ModelRef<>(Account.class);

    private ModelRef<Transfer> transferRef = new ModelRef<>(Transfer.class);

    private boolean debit;

    public Transaction() {

    }

    public Transaction(Transfer transfer) {
        this(transfer, false);
    }

    public Transaction(Transfer transfer, boolean debit) {
        currency = transfer.getCurrency();
        amount = transfer.getAmount();
        unpaid = transfer.getUnpaid();
        if (debit) {
            amount = -1 * amount;
            unpaid = -1 * unpaid;
        }
        description = transfer.getDescription();
        reference = transfer.getReference();
        transferRef.setModel(transfer);
        this.debit = debit;
    }

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

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public double getUnpaid() {
        return unpaid;
    }

    public void setUnpaid(double unpaid) {
        this.unpaid = unpaid;
    }

    public ModelRef<Account> getAccountRef() {
        return accountRef;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public ModelRef<Transfer> getTransferRef() {
        return transferRef;
    }

    public boolean isDebit() {
        return debit;
    }

    public void setDebit(boolean debit) {
        this.debit = debit;
    }

    public double getBalanceAmount() {
        return amount - unpaid;
    }
}
