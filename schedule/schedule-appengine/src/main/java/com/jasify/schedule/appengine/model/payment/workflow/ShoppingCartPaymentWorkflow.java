package com.jasify.schedule.appengine.model.payment.workflow;

import com.google.appengine.api.datastore.Key;
import com.google.common.base.Preconditions;
import com.jasify.schedule.appengine.dao.cart.ShoppingCartDao;
import com.jasify.schedule.appengine.dao.common.*;
import com.jasify.schedule.appengine.dao.users.UserDao;
import com.jasify.schedule.appengine.mail.MailParser;
import com.jasify.schedule.appengine.mail.MailServiceFactory;
import com.jasify.schedule.appengine.model.EntityNotFoundException;
import com.jasify.schedule.appengine.model.activity.*;
import com.jasify.schedule.appengine.model.cart.ShoppingCart;
import com.jasify.schedule.appengine.model.common.Organization;
import com.jasify.schedule.appengine.model.payment.Payment;
import com.jasify.schedule.appengine.model.users.User;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slim3.datastore.Model;

import java.util.*;

/**
 * @author krico
 * @since 06/04/15.
 */
@Model
public class ShoppingCartPaymentWorkflow extends PaymentWorkflow {
    private static final Logger log = LoggerFactory.getLogger(ShoppingCartPaymentWorkflow.class);

    private final ActivityDao activityDao = new ActivityDao();
    private final ActivityTypeDao activityTypeDao = new ActivityTypeDao();
    private final ActivityPackageDao activityPackageDao = new ActivityPackageDao();
    private final ActivityPackageExecutionDao activityPackageExecutionDao = new ActivityPackageExecutionDao();
    private final OrganizationDao organizationDao = new OrganizationDao();
    private final SubscriptionDao subscriptionDao = new SubscriptionDao();
    private final UserDao userDao = new UserDao();

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
            ShoppingCartDao dao = new ShoppingCartDao();
            ShoppingCart cleanCart = new ShoppingCart(cartId);
            dao.put(cleanCart);
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
                    subscriptions.add(subscriptionDao.get(key));
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
                    subscriptions.add(activityPackageExecutionDao.get(key));
                } catch (EntityNotFoundException e) {
                    log.error("Failed to find subscription with key " + key.getId(), e);
                }
            }
        }
        return subscriptions;
    }

    private void notifyPublisher(List<Subscription> subscriptions, List<ActivityPackageExecution> executions) {
        Map<Key, Organization> organizations = new HashMap<>();
        Map<Key, Collection<Subscription>> subscriptionMap = new HashMap<>();
        Map<Key, Collection<ActivityPackageExecution>> executionMap = new HashMap<>();

        User user = null;
        try {
            for (Subscription subscription : subscriptions) {
                Activity activity = activityDao.get(subscription.getActivityRef().getKey());
                ActivityType activityType = activityTypeDao.get(activity.getActivityTypeRef().getKey());
                Organization organization = organizationDao.get(activityType.getOrganizationRef().getKey());
                if (!subscriptionMap.containsKey(organization.getId())) {
                    organizations.put(organization.getId(), organization);
                    subscriptionMap.put(organization.getId(), new ArrayList<Subscription>());
                }
                subscriptionMap.get(organization.getId()).add(subscription);
                if (user == null) {
                    user = userDao.get(subscription.getUserRef().getKey());
                }
            }

            for (ActivityPackageExecution execution : executions) {
                ActivityPackage activityPackage = activityPackageDao.get(execution.getActivityPackageRef().getKey());
                Organization organization = organizationDao.get(activityPackage.getOrganizationRef().getKey());
                if (!executionMap.containsKey(organization.getId())) {
                    organizations.put(organization.getId(), organization);
                    executionMap.put(organization.getId(), new ArrayList<ActivityPackageExecution>());
                }
                executionMap.get(organization.getId()).add(execution);
                if (user == null) {
                    user = userDao.get(execution.getUserRef().getKey());
                }
            }
        } catch (EntityNotFoundException e) {
            log.error("Failed to notify publishers", e);
        }

        for (Organization organization : organizations.values()) {
            String userName = "Anonymous"; // Fallback in case user is null - which should never happen

            if (user != null) {
                userName = user.getDisplayName();
            }

            String subject = String.format("[Jasify] Subscribe [%s]", userName);
            try {
                Collection<Subscription> orgSubscriptions = subscriptionMap.containsKey(organization.getId()) ? subscriptionMap.get(organization.getId()) : Collections.<Subscription>emptyList();
                Collection<ActivityPackageExecution> orgExecutions = executionMap.containsKey(organization.getId()) ? executionMap.get(organization.getId()) : Collections.<ActivityPackageExecution>emptyList();
                MailParser mailParser = MailParser.createPublisherSubscriptionEmail(orgSubscriptions, orgExecutions);
                for (User orgUser : organization.getUsers()) {
                    MailServiceFactory.getMailService().send(orgUser.getEmail(), subject, mailParser.getHtml(), mailParser.getText());
                }
            } catch (Exception e) {
                log.error("Failed to notify publisher", e);
            }
        }
    }

    private void notifySubscriber(List<Subscription> subscriptions, List<ActivityPackageExecution> executions) {
        Preconditions.checkArgument(!(subscriptions.isEmpty() && executions.isEmpty()));

        try {
            User user;
            if (!subscriptions.isEmpty()) {
                user = userDao.get(subscriptions.get(0).getUserRef().getKey());
            } else {
                user = userDao.get(executions.get(0).getUserRef().getKey());
            }
            String subject = String.format("[Jasify] Subscribe [%s]", user.getDisplayName());

            MailParser mailParser = MailParser.createSubscriberSubscriptionEmail(subscriptions, executions);
            MailServiceFactory.getMailService().send(user.getEmail(), subject, mailParser.getHtml(), mailParser.getText());
        } catch (Exception e) {
            log.error("Failed to notify application owners", e);
        }
    }
}
