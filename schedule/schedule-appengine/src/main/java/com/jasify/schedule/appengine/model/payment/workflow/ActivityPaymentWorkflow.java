package com.jasify.schedule.appengine.model.payment.workflow;

import com.google.appengine.api.datastore.Key;
import com.jasify.schedule.appengine.model.EntityNotFoundException;
import com.jasify.schedule.appengine.model.OperationException;
import com.jasify.schedule.appengine.model.UniqueConstraintException;
import com.jasify.schedule.appengine.model.activity.ActivityServiceFactory;
import com.jasify.schedule.appengine.model.activity.Subscription;
import com.jasify.schedule.appengine.model.balance.BalanceServiceFactory;
import com.jasify.schedule.appengine.model.payment.Payment;
import com.jasify.schedule.appengine.model.payment.PaymentTypeEnum;
import org.slim3.datastore.Model;

/**
 * @author krico
 * @since 05/04/15.
 */
@Model
public class ActivityPaymentWorkflow extends PaymentWorkflow {

    private Key activityId;
    private Key subscriptionId;

    public ActivityPaymentWorkflow() {
    }

    public ActivityPaymentWorkflow(Key activityId) {
        this.activityId = activityId;
    }

    public Key getActivityId() {
        return activityId;
    }

    public void setActivityId(Key activityId) {
        this.activityId = activityId;
    }

    public Key getSubscriptionId() {
        return subscriptionId;
    }

    public void setSubscriptionId(Key subscriptionId) {
        this.subscriptionId = subscriptionId;
    }

    @Override
    public void onCreated() throws PaymentWorkflowException {
        Payment payment = getPaymentRef().getModel();
        try {
            Subscription subscribe = ActivityServiceFactory.getActivityService().subscribe(payment.getUserRef().getKey(), activityId);
            subscriptionId = subscribe.getId();
        } catch (EntityNotFoundException | UniqueConstraintException | OperationException e) {
            throw new PaymentWorkflowException(e);
        }
    }

    @Override
    public void onCanceled() throws PaymentWorkflowException {
        if (subscriptionId != null) {
            try {
                ActivityServiceFactory.getActivityService().cancelSubscription(subscriptionId);
            } catch (EntityNotFoundException e) {
                throw new PaymentWorkflowException(e);
            }
        }
    }

    @Override
    public void onCompleted() throws PaymentWorkflowException {
        if (subscriptionId != null) {
            Payment payment = getPaymentRef().getModel();
            if (payment.getType() == PaymentTypeEnum.Cash) {
                try {
                    BalanceServiceFactory.getBalanceService().unpaidSubscription(subscriptionId);
                } catch (EntityNotFoundException e) {
                    throw new PaymentWorkflowException(e);
                }
            } else {
                try {
                    BalanceServiceFactory.getBalanceService().subscription(subscriptionId);
                } catch (EntityNotFoundException e) {
                    throw new PaymentWorkflowException(e);
                }
            }
        }

    }
}
