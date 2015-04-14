package com.jasify.schedule.appengine.model.balance;

import com.google.appengine.api.datastore.Key;
import org.slim3.datastore.Attribute;
import org.slim3.datastore.CreationDate;
import org.slim3.datastore.Model;
import org.slim3.datastore.ModelRef;

import java.util.Date;

/**
 * This was our original discussion
 * Account
 * <p/>
 * 0)  Transaction
 * description: Deposit (PayPal)
 * amount: 20
 * from: External
 * to: JasifyAccount
 * <p/>
 * Credit amount: 20 owner JasifyAccount
 * <p/>
 * 1)  Transaction
 * description: PayPal Payment
 * amount: 20
 * from: JasifyAccount
 * to: FlavioAccount
 * <p/>
 * Debit amount: 20 owner: JasifyAccount
 * Credit amount: 20 owner FlavioAccount
 * <p/>
 * 2) Transaction
 * description: MetaFIT 20th apr subscription
 * amount: 20
 * from: FlavioAccount
 * to: JaneAccount
 * <p/>
 * Debit amount: 20 owner: FlavioAccount
 * Credit amount: 20 owner: JaneAccount
 * <p/>
 * 3) Subscription
 * funding: (Transaction 2, on Jane's account)
 * <p/>
 * ==>   At this point, Jane has 20 CHF credit in Jasify
 * <p/>
 * 1)  Transaction
 * description: Cash out (REF: CH203921)
 * amount: 20
 * from: JaneAccount
 * to: ExternalAccount
 * <p/>
 * Debit amount: 20 owner: JaneAccount
 *
 * @author krico
 * @since 19/02/15.
 */
@Model
public class Transfer {
    @Attribute(primaryKey = true)
    private Key id;

    @Attribute(listener = CreationDate.class)
    private Date created;

    private double amount;

    private double unpaid;

    private String currency;

    private String description;

    private String reference;

    private ModelRef<Transaction> beneficiaryLegRef = new ModelRef<>(Transaction.class);

    private ModelRef<Transaction> payerLegRef = new ModelRef<>(Transaction.class);

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

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
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

    public ModelRef<Transaction> getBeneficiaryLegRef() {
        return beneficiaryLegRef;
    }

    public ModelRef<Transaction> getPayerLegRef() {
        return payerLegRef;
    }
}