package com.jasify.schedule.appengine.model.payment.workflow;

import com.google.appengine.api.datastore.Key;
import com.jasify.schedule.appengine.model.EntityNotFoundException;
import com.jasify.schedule.appengine.model.OperationException;
import com.jasify.schedule.appengine.model.UniqueConstraintException;
import com.jasify.schedule.appengine.model.activity.ActivityPackageExecution;
import com.jasify.schedule.appengine.model.activity.ActivityServiceFactory;
import com.jasify.schedule.appengine.model.balance.BalanceServiceFactory;
import com.jasify.schedule.appengine.model.payment.Payment;
import com.jasify.schedule.appengine.model.payment.PaymentTypeEnum;
import org.slim3.datastore.Model;

import java.util.List;

/**
 * @author krico
 * @since 11/05/15.
 */
@Model
public class ActivityPackagePaymentWorkflow extends PaymentWorkflow {
    private Key activityPackageId;
    private List<Key> activityIds;
    private Key activityPackageExecutionId;

    public ActivityPackagePaymentWorkflow() {
    }

    public ActivityPackagePaymentWorkflow(Key activityPackageId, List<Key> activityIds) {
        this.activityPackageId = activityPackageId;
        this.activityIds = activityIds;
    }

    public Key getActivityPackageId() {
        return activityPackageId;
    }

    public void setActivityPackageId(Key activityPackageId) {
        this.activityPackageId = activityPackageId;
    }

    public List<Key> getActivityIds() {
        return activityIds;
    }

    public void setActivityIds(List<Key> activityIds) {
        this.activityIds = activityIds;
    }

    public Key getActivityPackageExecutionId() {
        return activityPackageExecutionId;
    }

    public void setActivityPackageExecutionId(Key activityPackageExecutionId) {
        this.activityPackageExecutionId = activityPackageExecutionId;
    }

    @Override
    public void onCreated() throws PaymentWorkflowException {
        Payment payment = getPaymentRef().getModel();
        try {
            ActivityPackageExecution activityPackageExecution = ActivityServiceFactory.getActivityService().subscribe(payment.getUserRef().getKey(), activityPackageId, activityIds);
            activityPackageExecutionId = activityPackageExecution.getId();
        } catch (EntityNotFoundException | UniqueConstraintException | OperationException e) {
            throw new PaymentWorkflowException(e);
        }
    }

    @Override
    public void onCanceled() throws PaymentWorkflowException {
        if (activityPackageExecutionId != null) {
            try {
                ActivityServiceFactory.getActivityService().cancelActivityPackageExecution(activityPackageExecutionId);
            } catch (EntityNotFoundException e) {
                throw new PaymentWorkflowException(e);
            }
        }
    }

    @Override
    public void onCompleted() throws PaymentWorkflowException {
        if (activityPackageExecutionId != null) {
            Payment payment = getPaymentRef().getModel();
            try {
                if (payment.getType() == PaymentTypeEnum.Cash) {
                    BalanceServiceFactory.getBalanceService().unpaidActivityPackageExecution(activityPackageExecutionId);
                } else {
                    BalanceServiceFactory.getBalanceService().activityPackageExecution(activityPackageExecutionId);
                }
            } catch (EntityNotFoundException e) {
                throw new PaymentWorkflowException(e);
            }
        }
    }
}
