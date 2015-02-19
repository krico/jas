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

    private Double amount;

    private String description;

    private String reference;

    private ModelRef<Account> accountRef = new ModelRef<>(Account.class);

    private ModelRef<Transfer> transferRef = new ModelRef<>(Transfer.class);

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

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
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
}
