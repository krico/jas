package com.jasify.schedule.appengine.template;

import com.jasify.schedule.appengine.TestHelper;
import com.jasify.schedule.appengine.Version;
import com.jasify.schedule.appengine.communication.ApplicationContext;
import com.jasify.schedule.appengine.communication.ApplicationContextImpl;
import com.jasify.schedule.appengine.dao.common.ActivityPackageExecutionDao;
import com.jasify.schedule.appengine.model.ModelException;
import com.jasify.schedule.appengine.model.Navigate;
import com.jasify.schedule.appengine.model.activity.Activity;
import com.jasify.schedule.appengine.model.activity.ActivityPackage;
import com.jasify.schedule.appengine.model.activity.ActivityPackageExecution;
import com.jasify.schedule.appengine.model.activity.Subscription;
import com.jasify.schedule.appengine.model.attachment.Attachment;
import com.jasify.schedule.appengine.model.payment.InvoicePayment;
import com.jasify.schedule.appengine.model.users.PasswordRecovery;
import com.jasify.schedule.appengine.model.users.User;
import org.apache.commons.lang3.RandomUtils;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.context.Context;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slim3.datastore.Datastore;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static junit.framework.TestCase.assertTrue;

/**
 * This class serves two purposes.
 * 1) Ensure that all templates can be parsed (syntax check)
 * 2) Provide a mechanism to generate all templates into files so that we can view them while developing
 *
 * @author krico
 * @since 20/08/15.
 */
public class TemplateNamesTest {
    private static final Logger log = LoggerFactory.getLogger(TemplateNamesTest.class);
    private static TemplateEngine engine;
    private File templateDir;

    @BeforeClass
    public static void setup() {
        TestHelper.initializeDatastore();
        engine = new TemplateEngineBuilder().build();
    }

    @AfterClass
    public static void cleanup() {
        TestHelper.cleanupDatastore();
    }

    private static List<Subscription> createSubscriptions(User user) {
        List<Subscription> subscriptions = new ArrayList<>();
        for (int i = 0; i < 5; ++i) {
            Activity activity = TestHelper.createActivity(true);
            activity.setCurrency("CHF");
            activity.setPrice(RandomUtils.nextDouble(10, 500));
            Datastore.put(activity);

            Subscription subscription = TestHelper.createSubscription(user, activity, true);
            subscriptions.add(subscription);
        }
        return subscriptions;
    }

    private static List<ActivityPackageExecution> createActivityPackageExecutions(User user) throws ModelException {
        ActivityPackageExecutionDao activityPackageExecutionDao = new ActivityPackageExecutionDao();
        List<ActivityPackageExecution> executions = new ArrayList<>();
        for (int i = 0; i < 3; ++i) {
            ActivityPackage activityPackage = new ActivityPackage();
            activityPackage.setName("The combo of " + i);
            activityPackage.setCurrency("CHF");
            activityPackage.setPrice(RandomUtils.nextDouble(100, 500));
            Datastore.put(activityPackage);

            ActivityPackageExecution execution = new ActivityPackageExecution();
            execution.getUserRef().setModel(user);
            executions.add(execution);
            execution.getActivityPackageRef().setModel(activityPackage);
            activityPackageExecutionDao.save(execution);

            int subscriptionCount = RandomUtils.nextInt(3, 8);
            for (int s = 0; s < subscriptionCount; ++s) {
                Activity activity = TestHelper.createActivity(true);
                activity.setCurrency("CHF");
                activity.setPrice(RandomUtils.nextDouble(10, 500));
                Datastore.put(activity);

                TestHelper.createActivityPackageSubscription(user, activity, execution, true);
            }

        }

        return executions;
    }

    private void render(final String templateName, final Context context) throws Exception {
        assertTrue(templateName.endsWith(".vm"));
        String filename = templateName.substring(0, templateName.length() - 3);
        File outputFile = new File(templateDir, filename);
        File parentDir = outputFile.getParentFile();
        assertTrue(parentDir.isDirectory() || parentDir.mkdirs());
        log.info("{} -> {}", templateName, outputFile);
        try (FileWriter writer = new FileWriter(outputFile)) {
            engine.render(templateName, context, writer);
        }
    }

    @Before
    public void createTemplateDir() {
        templateDir = new File(TestHelper.baseDir(), "target/rendered-templates");
        assertTrue(templateDir.exists() || templateDir.mkdir());
        log.info("\n\n\tTEMPLATE DIR: {}\n\n", templateDir);
    }

    private void assertJasifyNewVersion(String templateName) throws Exception {
        VelocityContext context = new VelocityContext(new TestApplicationContext());
        context.put("version", Version.INSTANCE);
        render(templateName, context);
    }

    @Test
    public void testJasifyNewVersionHtml() throws Exception {
        assertJasifyNewVersion(TemplateNames.JASIFY_NEW_VERSION_HTML);
    }

    @Test
    public void testJasifyNewVersionTxt() throws Exception {
        assertJasifyNewVersion(TemplateNames.JASIFY_NEW_VERSION_TXT);
    }

    private void assertJasifyNewUser(String templateName) throws Exception {
        VelocityContext context = new VelocityContext(new TestApplicationContext());
        User user = new User();
        user.setId(Datastore.createKey(User.class, 19760715));
        user.setEmail("new@jasify.com");
        user.setName("new@jasify.com");
        user.setRealName("John Doe");
        user.setCreated(new Date());
        context.put("user", user);
        render(templateName, context);
    }

    @Test
    public void testJasifyNewUserHtml() throws Exception {
        assertJasifyNewUser(TemplateNames.JASIFY_NEW_USER_HTML);
    }

    @Test
    public void testJasifyNewUserTxt() throws Exception {
        assertJasifyNewUser(TemplateNames.JASIFY_NEW_USER_TXT);
    }

    private void assertSubscriberPasswordRecovery(String templateName) throws Exception {
        VelocityContext context = new VelocityContext(new TestApplicationContext());
        User user = new User();
        user.setId(Datastore.createKey(User.class, 19760715));
        user.setEmail("new@jasify.com");
        user.setName("new@jasify.com");
        user.setRealName("John Doe");
        user.setCreated(new Date());
        PasswordRecovery recovery = new PasswordRecovery();
        recovery.setCode(Datastore.createKey(PasswordRecovery.class, "A-CODE-X"));

        context.put("user", user);
        context.put("recovery", recovery);

        render(templateName, context);
    }

    @Test
    public void testSubscriberPasswordRecoveryHtml() throws Exception {
        assertSubscriberPasswordRecovery(TemplateNames.SUBSCRIBER_PASSWORD_RECOVERY_HTML);
    }

    @Test
    public void testSubscriberPasswordRecoveryTxt() throws Exception {
        assertSubscriberPasswordRecovery(TemplateNames.SUBSCRIBER_PASSWORD_RECOVERY_TXT);
    }

    private void assertSubscriberInvoicePaymentCreated(String templateName) throws Exception {
        VelocityContext context = new VelocityContext(new TestApplicationContext());
        User user = TestHelper.createUser(true);

        List<Subscription> subscriptions = createSubscriptions(user);
        List<ActivityPackageExecution> executions = createActivityPackageExecutions(user);

        InvoicePayment payment = new InvoicePayment();
        payment.setReferenceCode("120000000000234478943216899");
        payment.setAmount(400.25);
        payment.setCurrency("CHF");

        Attachment att = new Attachment();
        att.setName("ABC-DEF.pdf");

        payment.getAttachmentRef().setModel(att);
        Datastore.put(payment, att);

        context.put("user", user);
        context.put("payment", payment);
        context.put("subscriptions", subscriptions);
        context.put("executions", executions);
        render(templateName, context);
    }

    @Test
    public void testSubscriberInvoicePaymentCreatedHtml() throws Exception {
        assertSubscriberInvoicePaymentCreated(TemplateNames.SUBSCRIBER_INVOICE_PAYMENT_CREATED_HTML);
    }

    @Test
    public void testSubscriberInvoicePaymentCreatedTxt() throws Exception {
        assertSubscriberInvoicePaymentCreated(TemplateNames.SUBSCRIBER_INVOICE_PAYMENT_CREATED_TXT);
    }

    private void assertPublisherInvoicePaymentCreated(String templateName) throws Exception {
        VelocityContext context = new VelocityContext(new TestApplicationContext());
        User user = TestHelper.createUser(true);

        List<Subscription> subscriptions = createSubscriptions(user);
        List<ActivityPackageExecution> executions = createActivityPackageExecutions(user);

        InvoicePayment payment = new InvoicePayment();
        payment.setReferenceCode("120000000000234478943216899");
        payment.setAmount(400.25);
        payment.setCurrency("CHF");

        Attachment att = new Attachment();
        att.setName("ABC-DEF.pdf");

        payment.getAttachmentRef().setModel(att);
        Datastore.put(payment, att);


        context.put("payment", payment);
        context.put("organization", Navigate.organization(subscriptions.get(0)));
        context.put("user", user);
        context.put("subscriptions", subscriptions);
        context.put("executions", executions);
        render(templateName, context);
    }

    @Test
    public void testPublisherInvoicePaymentCreatedHtml() throws Exception {
        assertPublisherInvoicePaymentCreated(TemplateNames.PUBLISHER_INVOICE_PAYMENT_CREATED_HTML);
    }

    @Test
    public void testPublisherInvoicePaymentCreatedTxt() throws Exception {
        assertPublisherInvoicePaymentCreated(TemplateNames.PUBLISHER_INVOICE_PAYMENT_CREATED_TXT);
    }

    public static class TestApplicationContext extends ApplicationContextImpl implements ApplicationContext {
        @Override
        protected App createApp() {
            return TestHelper.createApplicationContextApp();
        }
    }
}
