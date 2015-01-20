package com.jasify.schedule.appengine.mail;

import com.google.appengine.api.mail.MailServicePb;
import com.google.appengine.api.mail.dev.LocalMailService;
import com.google.appengine.tools.development.testing.LocalMailServiceTestConfig;
import com.jasify.schedule.appengine.TestHelper;
import org.junit.*;

import java.util.Date;
import java.util.List;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;

/**
 * @author krico
 * @since 26/11/14.
 */
public class MailServiceTest {
    @BeforeClass
    public static void startEmail() {
        TestHelper.initializeMail();
    }

    @AfterClass
    public static void stopEmail() {
        TestHelper.cleanupMail();
    }

    @After
    public void after() {
        LocalMailServiceTestConfig.getLocalMailService().clearSentMessages();
        Assume.assumeTrue(MailServiceFactory.getMailService() instanceof DefaultMailService);
        ((DefaultMailService) MailServiceFactory.getMailService()).reset();
    }

    @Test
    public void testMailServiceFactoryClass() throws Exception {
        TestHelper.assertUtilityClassWellDefined(MailServiceFactory.class);
    }

    @Test
    public void testMailServiceFactoryGetMailService() {
        assertNotNull(MailServiceFactory.getMailService());
    }

    @Test
    public void testNotifyApplicationOwners() {
        String subject = "Test: " + new Date();
        String body = "This e-mail is a test e-mail";
        assertTrue(MailServiceFactory.getMailService().sendToApplicationOwners(subject, body));
        LocalMailService service = LocalMailServiceTestConfig.getLocalMailService();
        List<MailServicePb.MailMessage> sentMessages = service.getSentMessages();
        assertNotNull(sentMessages);
        assertEquals(1, sentMessages.size());
        MailServicePb.MailMessage mailMessage = sentMessages.get(0);
        assertEquals(subject, mailMessage.getSubject());
        assertEquals(body, mailMessage.getTextBody());
        assertEquals(body, mailMessage.getHtmlBody());
        String expectedSender = String.format("\"%s\" <%s>", DefaultMailService.DEFAULT_SENDER_NAME, DefaultMailService.DEFAULT_SENDER);
        assertEquals(expectedSender, mailMessage.getSender());
        assertEquals(DefaultMailService.DEFAULT_OWNER, mailMessage.getTo(0));
    }

    @Test
    public void testInvalidSenders() {
        assertFalse(MailServiceFactory.getMailService().sendToApplicationOwners(null, null));
    }
}
