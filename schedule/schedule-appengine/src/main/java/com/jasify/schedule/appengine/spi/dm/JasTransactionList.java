package com.jasify.schedule.appengine.spi.dm;

import com.jasify.schedule.appengine.model.balance.Transaction;

import java.util.ArrayList;
import java.util.List;

/**
 * @author krico
 * @since 05/03/15.
 */
public class JasTransactionList implements JasEndpointEntity {
    private int total;
    private List<Transaction> transactions = new ArrayList<>();

    public void addAll(List<Transaction> transactions) {
        if (this.transactions == null) this.transactions = new ArrayList<>();
        this.transactions.addAll(transactions);
    }

    public int size() {
        return transactions == null ? -1 : transactions.size();
    }

    public Transaction get(int i) {
        return transactions == null ? null : transactions.get(i);
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }
}
