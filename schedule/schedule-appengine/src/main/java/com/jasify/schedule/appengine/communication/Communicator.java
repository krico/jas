package com.jasify.schedule.appengine.communication;

import com.google.appengine.api.datastore.Key;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.jasify.schedule.appengine.Version;
import com.jasify.schedule.appengine.dao.users.UserDao;
import com.jasify.schedule.appengine.mail.MailServiceFactory;
import com.jasify.schedule.appengine.model.EntityNotFoundException;
import com.jasify.schedule.appengine.model.Navigate;
import com.jasify.schedule.appengine.model.activity.ActivityPackageExecution;
import com.jasify.schedule.appengine.model.activity.Subscription;
import com.jasify.schedule.appengine.model.common.Organization;
import com.jasify.schedule.appengine.model.payment.InvoicePayment;
import com.jasify.schedule.appengine.model.users.PasswordRecovery;
import com.jasify.schedule.appengine.model.users.User;
import com.jasify.schedule.appengine.template.TemplateEngine;
import com.jasify.schedule.appengine.template.TemplateEngineBuilder;
import com.jasify.schedule.appengine.template.TemplateEngineException;
import com.jasify.schedule.appengine.template.TemplateNames;
import com.jasify.schedule.appengine.util.KeyUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.context.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.internet.InternetAddress;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class handles all external communication, to subscribers, publishers and admins
 *
 * @author krico
 * @since 19/08/15.
 */
public class Communicator {
    private static final Logger log = LoggerFactory.getLogger(Communicator.class);

    private static final TemplateEngine templateEngine = new TemplateEngineBuilder().build();
    private static final ThreadLocal<Context> GLOBAL_CONTEXT = new ThreadLocal<Context>() {
        @Override
        protected Context initialValue() {
            return new ApplicationContextImpl();
        }
    };

    public static void notifyOfNewVersion() throws TemplateEngineException {
        VelocityContext context = new VelocityContext(GLOBAL_CONTEXT.get());
        context.put("version", Version.INSTANCE);
        String html = templateEngine.render(TemplateNames.JASIFY_NEW_VERSION_HTML, context);
        String text = templateEngine.render(TemplateNames.JASIFY_NEW_VERSION_TXT, context);

        String subject = String.format("[Jasify] New Version In Prod (%s) [%s]", Version.getDeployVersion(), Version.toShortVersionString());
        MailServiceFactory.getMailService().sendToApplicationOwners(subject, html, text);
    }

    public static void notifyOfNewUser(User user) throws TemplateEngineException {
        VelocityContext context = new VelocityContext(GLOBAL_CONTEXT.get());
        context.put("user", user);
        String html = templateEngine.render(TemplateNames.JASIFY_NEW_USER_HTML, context);
        String text = templateEngine.render(TemplateNames.JASIFY_NEW_USER_TXT, context);

        String nonBlankName = StringUtils.isNoneBlank(user.getRealName()) ? user.getRealName() : user.getName();
        String subject = String.format("[Jasify] New User [%s]", nonBlankName);
        MailServiceFactory.getMailService().sendToApplicationOwners(subject, html, text);
    }

    public static void notifyOfPasswordRecovery(User user, PasswordRecovery recovery) throws TemplateEngineException, UnsupportedEncodingException {
        VelocityContext context = new VelocityContext(GLOBAL_CONTEXT.get());
        context.put("user", user);
        context.put("recovery", recovery);
        String html = templateEngine.render(TemplateNames.SUBSCRIBER_PASSWORD_RECOVERY_HTML, context);
        String text = templateEngine.render(TemplateNames.SUBSCRIBER_PASSWORD_RECOVERY_TXT, context);

        String nonBlankName = StringUtils.isNoneBlank(user.getRealName()) ? user.getRealName() : user.getName();
        String subject = String.format("[Jasify] Password Assistance [%s]", nonBlankName);
        MailServiceFactory.getMailService().send(new InternetAddress(user.getEmail(), user.getRealName()), subject, html, text);
    }

    public static void notifyOfInvoiceCreated(InvoicePayment payment) throws TemplateEngineException, UnsupportedEncodingException, EntityNotFoundException {
        List<Subscription> subscriptions = payment.getSubscriptions();
        List<ActivityPackageExecution> executions = payment.getActivityPackageExecutions();

        UserDao userDao = new UserDao();
        User user = userDao.get(payment.getUserRef().getKey());

        notifySubscriberOfInvoiceCreated(user, payment, subscriptions, executions);

        Map<Key, Organization> organizationById = new HashMap<>();
        ListMultimap<Key, Subscription> subscriptionsByOrganization = ArrayListMultimap.create();
        ListMultimap<Key, ActivityPackageExecution> executionsByOrganization = ArrayListMultimap.create();

        for (Subscription subscription : subscriptions) {
            Organization organization = Navigate.organization(subscription);
            if (organization == null) continue;
            if (!organizationById.containsKey(organization.getId())) {
                organizationById.put(organization.getId(), organization);
            }
            subscriptionsByOrganization.put(organization.getId(), subscription);
        }

        for (ActivityPackageExecution execution : executions) {
            Organization organization = Navigate.organization(execution);
            if (organization == null) continue;
            if (!organizationById.containsKey(organization.getId())) {
                organizationById.put(organization.getId(), organization);
            }
            executionsByOrganization.put(organization.getId(), execution);
        }

        for (Key organizationId : organizationById.keySet()) {
            List<Subscription> orgSubscriptions = subscriptionsByOrganization.get(organizationId);
            List<ActivityPackageExecution> orgExecutions = executionsByOrganization.get(organizationId);
            notifyPublisherOfInvoiceCreated(organizationById.get(organizationId), user, payment, orgSubscriptions, orgExecutions);
        }
    }

    static void notifyPublisherOfInvoiceCreated(Organization organization, User user, InvoicePayment payment, List<Subscription> subscriptions, List<ActivityPackageExecution> executions) throws TemplateEngineException, UnsupportedEncodingException {
        List<User> users = Navigate.users(organization);
        if (users.isEmpty()) {
            log.warn("Cannot notify organization with no users [{}]", organization.getId());
            return;
        }
        InternetAddress[] toAddresses = new InternetAddress[users.size()];
        for (int i = 0; i < toAddresses.length; i++) {
            toAddresses[i] = new InternetAddress(users.get(i).getEmail(), users.get(i).getDisplayName());
        }
        VelocityContext context = new VelocityContext(GLOBAL_CONTEXT.get());
        context.put("user", user);
        context.put("payment", payment);
        context.put("subscriptions", subscriptions);
        context.put("executions", executions);
        context.put("organization", organization);

        String html = templateEngine.render(TemplateNames.PUBLISHER_INVOICE_PAYMENT_CREATED_HTML, context);
        String text = templateEngine.render(TemplateNames.PUBLISHER_INVOICE_PAYMENT_CREATED_TXT, context);

        String nonBlankName = StringUtils.isNoneBlank(user.getRealName()) ? user.getRealName() : user.getName();
        String subject = String.format("[Jasify] Subscription with Invoice [%s]", nonBlankName);
        MailServiceFactory.getMailService().send(toAddresses, subject, html, text);
    }

    static void notifySubscriberOfInvoiceCreated(User user, InvoicePayment payment, List<Subscription> subscriptions, List<ActivityPackageExecution> executions) throws TemplateEngineException, UnsupportedEncodingException {
        VelocityContext context = new VelocityContext(GLOBAL_CONTEXT.get());
        context.put("user", user);
        context.put("payment", payment);
        context.put("subscriptions", subscriptions);
        context.put("executions", executions);

        String html = templateEngine.render(TemplateNames.SUBSCRIBER_INVOICE_PAYMENT_CREATED_HTML, context);
        String text = templateEngine.render(TemplateNames.SUBSCRIBER_INVOICE_PAYMENT_CREATED_TXT, context);

        String nonBlankName = StringUtils.isNoneBlank(user.getRealName()) ? user.getRealName() : user.getName();
        String subject = String.format("[Jasify] Invoice# %s for [%s]", KeyUtil.toHumanReadableString(payment.getId()), nonBlankName);
        MailServiceFactory.getMailService().send(new InternetAddress(user.getEmail(), user.getDisplayName()), subject, html, text, payment.getAttachmentRef().getModel());
    }

    public static void notifyOfPaymentCancelled(InvoicePayment payment) throws TemplateEngineException, UnsupportedEncodingException {
        //TODO: write test
        User user = Navigate.user(payment);
        List<Organization> organizations = Navigate.organizations(payment);

        VelocityContext context = new VelocityContext(GLOBAL_CONTEXT.get());
        context.put("user", user);
        context.put("payment", payment);

        String html = templateEngine.render(TemplateNames.SUBSCRIBER_INVOICE_PAYMENT_CANCELLED_HTML, context);
        String text = templateEngine.render(TemplateNames.SUBSCRIBER_INVOICE_PAYMENT_CANCELLED_TXT, context);

        String nonBlankName = StringUtils.isNoneBlank(user.getRealName()) ? user.getRealName() : user.getName();
        String subject = String.format("[Jasify] CANCELLED Invoice# %s for [%s]", KeyUtil.toHumanReadableString(payment.getId()), nonBlankName);
        MailServiceFactory.getMailService().send(new InternetAddress(user.getEmail(), user.getDisplayName()), subject, html, text);

        for (Organization organization : organizations) {
            context.put("organization", organization);
            String oHtml = templateEngine.render(TemplateNames.SUBSCRIBER_INVOICE_PAYMENT_CANCELLED_HTML, context);
            String oText = templateEngine.render(TemplateNames.SUBSCRIBER_INVOICE_PAYMENT_CANCELLED_TXT, context);

            String oSubject = String.format("[Jasify] CANCELLED Invoice of [%s]", nonBlankName);
            List<User> users = Navigate.users(organization);
            InternetAddress[] toAddresses = new InternetAddress[users.size()];
            for (int i = 0; i < toAddresses.length; i++) {
                toAddresses[i] = new InternetAddress(users.get(i).getEmail(), users.get(i).getDisplayName());
            }
            MailServiceFactory.getMailService().send(toAddresses, oSubject, oHtml, oText);
        }
    }

    public static void notifyOfPaymentExecuted(InvoicePayment payment) throws TemplateEngineException, UnsupportedEncodingException {
        //TODO: write test
        User user = Navigate.user(payment);
        List<Organization> organizations = Navigate.organizations(payment);

        VelocityContext context = new VelocityContext(GLOBAL_CONTEXT.get());
        context.put("user", user);
        context.put("payment", payment);

        String html = templateEngine.render(TemplateNames.SUBSCRIBER_INVOICE_PAYMENT_EXECUTED_HTML, context);
        String text = templateEngine.render(TemplateNames.SUBSCRIBER_INVOICE_PAYMENT_EXECUTED_TXT, context);

        String nonBlankName = StringUtils.isNoneBlank(user.getRealName()) ? user.getRealName() : user.getName();
        String subject = String.format("[Jasify] EXECUTED Invoice# %s for [%s]", KeyUtil.toHumanReadableString(payment.getId()), nonBlankName);
        MailServiceFactory.getMailService().send(new InternetAddress(user.getEmail(), user.getDisplayName()), subject, html, text);

        for (Organization organization : organizations) {
            context.put("organization", organization);
            String oHtml = templateEngine.render(TemplateNames.SUBSCRIBER_INVOICE_PAYMENT_EXECUTED_HTML, context);
            String oText = templateEngine.render(TemplateNames.SUBSCRIBER_INVOICE_PAYMENT_EXECUTED_TXT, context);

            String oSubject = String.format("[Jasify] EXECUTED Invoice of [%s]", nonBlankName);
            List<User> users = Navigate.users(organization);
            InternetAddress[] toAddresses = new InternetAddress[users.size()];
            for (int i = 0; i < toAddresses.length; i++) {
                toAddresses[i] = new InternetAddress(users.get(i).getEmail(), users.get(i).getDisplayName());
            }
            MailServiceFactory.getMailService().send(toAddresses, oSubject, oHtml, oText);
        }
    }
}
