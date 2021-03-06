package com.jasify.schedule.appengine.model.payment.workflow;

import com.google.appengine.api.datastore.Key;
import com.jasify.schedule.appengine.model.EntityNotFoundException;
import com.jasify.schedule.appengine.model.FieldValueException;
import com.jasify.schedule.appengine.model.ModelException;
import com.jasify.schedule.appengine.model.OperationException;
import com.jasify.schedule.appengine.model.activity.ActivityService;
import com.jasify.schedule.appengine.model.activity.ActivityServiceFactory;
import com.jasify.schedule.appengine.model.activity.Subscription;
import com.jasify.schedule.appengine.model.balance.BalanceServiceFactory;
import com.jasify.schedule.appengine.model.history.History;
import com.jasify.schedule.appengine.model.history.HistoryHelper;
import com.jasify.schedule.appengine.model.payment.Payment;
import com.jasify.schedule.appengine.model.payment.PaymentTypeEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slim3.datastore.Model;

/**
 * @author krico
 * @since 05/04/15.
 */
@Model
public class ActivityPaymentWorkflow extends PaymentWorkflow {
    private static final Logger log = LoggerFactory.getLogger(ActivityPaymentWorkflow.class);
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
        Key userId = payment.getUserRef().getKey();
        try {
            ActivityService activityService = ActivityServiceFactory.getActivityService();
            Subscription subscribe = activityService.subscribe(userId, activityId);
            subscriptionId = subscribe.getId();
            HistoryHelper.addSubscriptionCreated(subscriptionId);
        } catch (EntityNotFoundException | OperationException | FieldValueException e) {
            log.error("Failed to subscribe User={} to Activity={}", userId, activityId, e);
            HistoryHelper.addSubscriptionCreationFailed(userId, activityId);
            throw new PaymentWorkflowException(e);
        }
    }

    @Override
    public void onCanceled() throws PaymentWorkflowException {
        if (subscriptionId != null) {
            try {
                History history = HistoryHelper.createTacticalSubscriptionCancelled(subscriptionId);
                ActivityServiceFactory.getActivityService().cancelSubscription(subscriptionId);
                HistoryHelper.addTacticalSubscriptionCancelled(history);
            } catch (EntityNotFoundException | FieldValueException e) {
                log.error("Failed to cancel subscription [{}]", subscriptionId, e);
                HistoryHelper.addSubscriptionCancellationFailed(subscriptionId);
                throw new PaymentWorkflowException(e);
            }
        }
    }

    @Override
    public void onCompleted() throws PaymentWorkflowException {
        if (subscriptionId != null) {
            Payment payment = getPaymentRef().getModel();
            try {
                if (payment.getType() == PaymentTypeEnum.Cash) {
                    BalanceServiceFactory.getBalanceService().unpaidSubscription(subscriptionId);
                } else {
                    BalanceServiceFactory.getBalanceService().subscription(subscriptionId);
                }
            } catch (ModelException e) {
                log.error("Failed to complete subscription [{}]", subscriptionId, e);
                throw new PaymentWorkflowException(e);
            }
        }
    }
}
