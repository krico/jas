package com.jasify.schedule.appengine.communication;

import com.google.appengine.api.mail.MailServicePb;
import com.google.appengine.api.mail.dev.LocalMailService;
import com.google.appengine.tools.development.testing.LocalMailServiceTestConfig;
import com.jasify.schedule.appengine.TestHelper;
import com.jasify.schedule.appengine.Version;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;

import static junit.framework.TestCase.*;

public class CommunicatorTest {

    @BeforeClass
    public static void initialize() {
        TestHelper.initializeDatastore();
    }

    @AfterClass
    public static void cleanup() {
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
        assertTemplate("HtmlBody", message.getHtmlBody());
        assertTemplate("TextBody", message.getTextBody());
    }

    private void assertTemplate(String name, String body) {
        assertNotNull(name, body);
        assertTrue(name, body.contains(Version.getBranch()));
        assertTrue(name, body.contains(Version.getDeployVersion()));
        assertTrue(name, body.contains(Version.getNumber()));
        assertTrue(name, body.contains(Long.toString(Version.getTimestamp())));
    }
}