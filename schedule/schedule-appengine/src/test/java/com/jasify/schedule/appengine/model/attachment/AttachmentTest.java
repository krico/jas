package com.jasify.schedule.appengine.model.attachment;

import com.google.common.net.MediaType;
import com.jasify.schedule.appengine.TestHelper;
import com.jasify.schedule.appengine.dao.attachment.AttachmentDao;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;

public class AttachmentTest {
    @Before
    public void initializeDatastore() {
        TestHelper.initializeJasify();
    }

    @After
    public void cleanupDatastore() {
        TestHelper.cleanupDatastore();
    }



}