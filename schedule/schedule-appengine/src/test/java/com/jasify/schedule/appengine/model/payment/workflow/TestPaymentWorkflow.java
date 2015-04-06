package com.jasify.schedule.appengine.model.payment.workflow;

import org.slim3.datastore.Model;

/**
 * @author krico
 * @since 05/04/15.
 */
@Model
public class TestPaymentWorkflow extends PaymentWorkflow {
    private int onCreatedCount;
    private int onCanceledCount;
    private int onCompletedCount;

    public int getOnCreatedCount() {
        return onCreatedCount;
    }

    public void setOnCreatedCount(int onCreatedCount) {
        this.onCreatedCount = onCreatedCount;
    }

    public int getOnCanceledCount() {
        return onCanceledCount;
    }

    public void setOnCanceledCount(int onCanceledCount) {
        this.onCanceledCount = onCanceledCount;
    }

    public int getOnCompletedCount() {
        return onCompletedCount;
    }

    public void setOnCompletedCount(int onCompletedCount) {
        this.onCompletedCount = onCompletedCount;
    }

    @Override
    public void onCreated() {
        ++onCreatedCount;
    }

    @Override
    public void onCanceled() {
        ++onCanceledCount;

    }

    @Override
    public void onCompleted() {
        ++onCompletedCount;
    }
}
