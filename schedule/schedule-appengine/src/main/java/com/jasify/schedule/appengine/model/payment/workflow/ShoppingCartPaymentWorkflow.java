package com.jasify.schedule.appengine.model.payment.workflow;

import com.google.appengine.api.datastore.Key;
import com.google.common.base.Preconditions;
import com.jasify.schedule.appengine.mail.MailParser;
import com.jasify.schedule.appengine.mail.MailServiceFactory;
import com.jasify.schedule.appengine.model.EntityNotFoundException;
import com.jasify.schedule.appengine.model.activity.*;
import com.jasify.schedule.appengine.model.cart.ShoppingCartServiceFactory;
import com.jasify.schedule.appengine.model.common.Organization;
import com.jasify.schedule.appengine.model.payment.Payment;
import com.jasify.schedule.appengine.model.users.User;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slim3.datastore.Model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author krico
 * @since 06/04/15.
 */
@Model
public class ShoppingCartPaymentWorkflow extends PaymentWorkflow {
    private static final Logger log = LoggerFactory.getLogger(ShoppingCartPaymentWorkflow.class);

    private String cartId;

    public ShoppingCartPaymentWorkflow() {
    }

    public ShoppingCartPaymentWorkflow(String cartId) {
        this.cartId = cartId;
    }

    public String getCartId() {
        return cartId;
    }

    public void setCartId(String cartId) {
        this.cartId = cartId;
    }

    @Override
    public void onCreated() throws PaymentWorkflowException {

    }

    @Override
    public void onCanceled() throws PaymentWorkflowException {

    }

    @Override
    public void onCompleted() throws PaymentWorkflowException {
        if (StringUtils.isNotBlank(cartId)) {
            ShoppingCartServiceFactory.getShoppingCartService().clearCart(cartId);
        }

        sendNotifications();
    }

    protected void sendNotifications() {
        List<Subscription> subscriptions = getSubscriptions();
        List<ActivityPackageExecution> executions = getActivityPackageExecutions();
        if (subscriptions.isEmpty() && executions.isEmpty()) {
            // Is this possible?
            log.error("Empty subscription and executions list");
        } else {
            notifyPublisher(subscriptions, executions);
            notifySubscriber(subscriptions, executions);
            notifyJasify(subscriptions, executions);
        }
    }

    private List<Subscription> getSubscriptions() {
        List<Subscription> subscriptions = new ArrayList<>();
        // This is probably not the best way to get the subscriptions
        Payment payment = getPaymentRef().getModel();
        for (PaymentWorkflow paymentWorkflow : payment.getWorkflowListRef().getModelList()) {
            if (paymentWorkflow instanceof ActivityPaymentWorkflow) {
                Key key = ((ActivityPaymentWorkflow) paymentWorkflow).getSubscriptionId();
                try {
                    subscriptions.add(ActivityServiceFactory.getActivityService().getSubscription(key));
                } catch (EntityNotFoundException e) {
                    log.error("Failed to find subscription with key " + key.getId(), e);
                }
            }
        }
        return subscriptions;
    }

    private List<ActivityPackageExecution> getActivityPackageExecutions() {
        List<ActivityPackageExecution> subscriptions = new ArrayList<>();
        // This is probably not the best way to get the subscriptions
        Payment payment = getPaymentRef().getModel();
        for (PaymentWorkflow paymentWorkflow : payment.getWorkflowListRef().getModelList()) {
            if (paymentWorkflow instanceof ActivityPackagePaymentWorkflow) {
                Key key = ((ActivityPackagePaymentWorkflow) paymentWorkflow).getActivityPackageExecutionId();
                try {
                    subscriptions.add(ActivityServiceFactory.getActivityService().getActivityPackageExecution(key));
                } catch (EntityNotFoundException e) {
                    log.error("Failed to find subscription with key " + key.getId(), e);
                }
            }
        }
        return subscriptions;
    }

    private void notifyPublisher(List<Subscription> subscriptions, List<ActivityPackageExecution> executions) {
        // A Publisher gets an email per subscription
        for (Subscription subscription : subscriptions) {
            User user = subscription.getUserRef().getModel();
            Activity activity = subscription.getActivityRef().getModel();
            ActivityType activityType = activity.getActivityTypeRef().getModel();
            Organization organization = activityType.getOrganizationRef().getModel();

            String subject = String.format("[Jasify] Subscribe [%s]", user.getDisplayName());

            try {
                MailParser mailParser = MailParser.createPublisherSubscriptionEmail(subscription);
                for (User orgUser : organization.getUsers()) {
                    MailServiceFactory.getMailService().send(orgUser.getEmail(), subject, mailParser.getHtml(), mailParser.getText());
                }
            } catch (Exception e) {
                log.error("Failed to notify publisher", e);
            }
        }

        for (ActivityPackageExecution execution : executions) {
            User user = execution.getUserRef().getModel();
            ActivityPackage activityPackage = execution.getActivityPackageRef().getModel();
            Organization organization = activityPackage.getOrganizationRef().getModel();
            List<ActivityPackageSubscription> packageSubscriptions = execution.getSubscriptionListRef().getModelList();
            for (int i = 0; i < packageSubscriptions.size(); ++i) {
                String subject = String.format("[Jasify] Activity Package Subscribe (%d/%d) [%s]", i, packageSubscriptions.size(), user.getDisplayName());

                try {
                    MailParser mailParser = MailParser.createPublisherSubscriptionEmail(packageSubscriptions.get(i));
                    for (User orgUser : organization.getUsers()) {
                        MailServiceFactory.getMailService().send(orgUser.getEmail(), subject, mailParser.getHtml(), mailParser.getText());
                    }
                } catch (Exception e) {
                    log.error("Failed to notify publisher", e);
                }
            }
        }
    }

    private void notifySubscriber(List<Subscription> subscriptions, List<ActivityPackageExecution> executions) {
        Preconditions.checkArgument(!(subscriptions.isEmpty() && executions.isEmpty()));
        User user;
        if (!subscriptions.isEmpty()) {
            user = subscriptions.get(0).getUserRef().getModel();
        } else {
            user = executions.get(0).getUserRef().getModel();
        }
        String subject = String.format("[Jasify] Subscribe [%s]", user.getDisplayName());

        try {
            MailParser mailParser = MailParser.createSubscriberSubscriptionEmail(subscriptions, executions);
            MailServiceFactory.getMailService().send(user.getEmail(), subject, mailParser.getHtml(), mailParser.getText());
        } catch (Exception e) {
            log.error("Failed to notify application owners", e);
        }
    }

    private void notifyJasify(List<Subscription> subscriptions, List<ActivityPackageExecution> executions) {
        User user;
        if (!subscriptions.isEmpty()) {
            user = subscriptions.get(0).getUserRef().getModel();
        } else {
            user = executions.get(0).getUserRef().getModel();
        }

        String subject = String.format("[Jasify] Subscribe [%s]", user.getDisplayName());

        try {
            MailParser mailParser = MailParser.createJasifySubscriptionEmail(subscriptions, executions);
            MailServiceFactory.getMailService().sendToApplicationOwners(subject, mailParser.getHtml(), mailParser.getText());
        } catch (Exception e) {
            log.error("Failed to notify application owners", e);
        }
    }
}
