package com.jasify.schedule.appengine.model.balance;

import com.google.appengine.api.datastore.Key;
import org.slim3.datastore.Attribute;
import org.slim3.datastore.CreationDate;
import org.slim3.datastore.Model;
import org.slim3.datastore.ModificationDate;

import java.util.Date;
import java.util.Objects;

/**
 * Source or target of money transfers.
 *
 * @author krico
 * @since 19/02/15.
 */
@Model
public class Account {

    @Attribute(primaryKey = true)
    private Key id;

    @Attribute(listener = CreationDate.class)
    private Date created;

    @Attribute(listener = ModificationDate.class)
    private Date modified;

    private double balance;

    private double unpaid;

    private String currency;

    public Account() {
    }

    public Account(Key id) {
        setId(id);
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

    public Date getModified() {
        return modified;
    }

    public void setModified(Date modified) {
        this.modified = modified;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public double getUnpaid() {
        return unpaid;
    }

    public void setUnpaid(double unpaid) {
        this.unpaid = unpaid;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String toString() {
        return getClass().getSimpleName() + '[' + Objects.toString(id) + ']';
    }
}
