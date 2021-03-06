package com.jasify.schedule.appengine.communication;

import com.google.appengine.api.mail.MailServicePb;
import com.google.appengine.api.mail.dev.LocalMailService;
import com.google.appengine.tools.development.testing.LocalMailServiceTestConfig;
import com.jasify.schedule.appengine.TestHelper;
import com.jasify.schedule.appengine.Version;
import com.jasify.schedule.appengine.model.attachment.AttachmentHelper;
import com.jasify.schedule.appengine.model.payment.InvoicePayment;
import com.jasify.schedule.appengine.model.users.PasswordRecovery;
import com.jasify.schedule.appengine.model.users.User;
import com.jasify.schedule.appengine.template.TemplateEngineException;
import com.jasify.schedule.appengine.util.EnvironmentUtil;
import com.jasify.schedule.appengine.util.KeyUtil;
import org.apache.commons.lang3.StringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slim3.datastore.Datastore;

import java.util.Date;
import java.util.List;

import static junit.framework.TestCase.*;

public class CommunicatorTest {

    @Before
    public void initialize() {
        TestHelper.initializeDatastore();
    }

    @After
    public void cleanup() {
        TestHelper.cleanupDatastore();
    }

    @Test
    public void testNotifyOfNewVersion() throws Exception {
        Communicator.notifyOfNewVersion();
        LocalMailService service = LocalMailServiceTestConfig.getLocalMailService();
        List<MailServicePb.MailMessage> sentMessages = service.getSentMessages();
        assertNotNull(sentMessages);
        assertEquals(1, sentMessages.size());
        MailServicePb.MailMessage message = sentMessages.get(0);
        assertNewVersionTemplate("HtmlBody", message.getHtmlBody());
        assertNewVersionTemplate("TextBody", message.getTextBody());
    }

    private void assertNewVersionTemplate(String name, String body) {
        assertNotNull(name, body);
        assertTrue(name, body.contains(Version.getBranch()));
        assertTrue(name, body.contains(Version.getDeployVersion()));
        assertTrue(name, body.contains(Version.getNumber()));
        assertTrue(name, body.contains(Long.toString(Version.getTimestamp())));
    }

    @Test
    public void testNotifyOfNewUser() throws Exception {
        User user = new User();
        user.setId(Datastore.createKey(User.class, 19760715));
        user.setEmail("new@jasify.com");
        user.setName("new2@jasify.com");
        user.setRealName("John Doe");
        user.setCreated(new Date());

        assertNewUserNotification(user);
    }

    @Test
    public void testNotifyOfNewUserWithNoRealName() throws Exception {
        User user = new User();
        user.setId(Datastore.createKey(User.class, 19760715));
        user.setEmail("new@jasify.com");
        user.setName("new2@jasify.com");
        user.setCreated(new Date());

        assertNewUserNotification(user);
    }

    protected void assertNewUserNotification(User user) throws TemplateEngineException {
        Communicator.notifyOfNewUser(user);
        LocalMailService service = LocalMailServiceTestConfig.getLocalMailService();
        List<MailServicePb.MailMessage> sentMessages = service.getSentMessages();
        assertNotNull(sentMessages);
        assertEquals(1, sentMessages.size());
        MailServicePb.MailMessage message = sentMessages.get(0);
        assertNewUserTemplate("HtmlBody", user, message.getSubject(), message.getHtmlBody());
        assertNewUserTemplate("TextBody", user, message.getSubject(), message.getTextBody());
    }

    private void assertNewUserTemplate(String name, User user, String subject, String body) {
        assertNotNull(name, body);
        String nonBlankName = StringUtils.isNoneBlank(user.getRealName()) ? user.getRealName() : user.getName();
        assertTrue(subject.contains(nonBlankName));
        assertTrue(name, body.contains(user.getName()));
        assertTrue(name, body.contains(StringUtils.trimToEmpty(user.getRealName())));
        assertTrue(name, body.contains(user.getCreated().toString()));
        assertTrue(name, body.contains(user.getEmail()));
        assertTrue(name, body.contains(KeyUtil.toHumanReadableString(user.getId())));
    }

    @Test
    public void testNotifyOfPasswordRecovery() throws Exception {
        User user = new User();
        user.setId(Datastore.createKey(User.class, 19760715));
        user.setEmail("new@jasify.com");
        user.setName("new2@jasify.com");
        user.setCreated(new Date());

        PasswordRecovery recovery = new PasswordRecovery();
        recovery.setCode(Datastore.createKey(PasswordRecovery.class, "EDOC"));

        Communicator.notifyOfPasswordRecovery(user, recovery);
        LocalMailService service = LocalMailServiceTestConfig.getLocalMailService();
        List<MailServicePb.MailMessage> sentMessages = service.getSentMessages();
        assertNotNull(sentMessages);
        assertEquals(1, sentMessages.size());
        MailServicePb.MailMessage message = sentMessages.get(0);
        assertTrue(message.getTo(0).contains(user.getEmail()));
        assertPasswordRecovery("HtmlBody", message.getHtmlBody(), user, recovery);
        assertPasswordRecovery("TextBody", message.getTextBody(), user, recovery);
    }

    private void assertPasswordRecovery(String name, String body, User user, PasswordRecovery recovery) {
        assertNotNull(name, body);
        assertTrue(name, body.contains(recovery.getCode().getName()));
        assertTrue(name, body.contains(EnvironmentUtil.defaultVersionHostname() + "/#/recover-password/" + recovery.getCode().getName()));
    }

    @Test
    public void testNotifyOfInvoiceCreated() throws Exception {
        User user = new User();
        user.setId(Datastore.createKey(User.class, 19760715));
        user.setEmail("new@jasify.com");
        user.setName("new2@jasify.com");
        user.setCreated(new Date());

        InvoicePayment payment = new InvoicePayment();
        payment.setCurrency("CHF");
        payment.setAmount(200.12);
        payment.setFee(1.2);
        payment.getUserRef().setModel(user);
        payment.getAttachmentRef().setModel(AttachmentHelper.create("Test.pdf", "application/pdf", new byte[10]));

        Datastore.put(payment, user, payment.getAttachmentRef().getModel());

        //TODO: Would be nice to test this a little bit better
        Communicator.notifyOfInvoiceCreated(payment);
    }
}