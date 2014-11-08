package com.jasify.schedule.appengine.model.users;

import com.google.appengine.api.datastore.Key;
import com.jasify.schedule.appengine.model.ModelTestHelper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slim3.datastore.Datastore;

public class UserServiceTest {
    private static final Logger log = LoggerFactory.getLogger(UserServiceTest.class);

    @Before
    public void initializeDatastore() {
        ModelTestHelper.initializeDatastore();
    }

    @After
    public void cleanupDatastore() {
        ModelTestHelper.cleanupDatastore();
    }

    @Test
    public void testFoo() {
        Key key = Datastore.allocateId(User.class);
        log.info("Key: {}", key);
    }
}