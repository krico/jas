package com.jasify.schedule.appengine.communication;

import com.jasify.schedule.appengine.TestHelper;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

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
        //TODO: Check that e-mail contains version info stuff
        //TODO: delete MailParser.notifyOfNewVersion
        Communicator.notifyOfNewVersion();
    }
}