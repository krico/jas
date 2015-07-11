package com.jasify.schedule.appengine.model;

import com.jasify.schedule.appengine.TestHelper;
import com.jasify.schedule.appengine.dao.ExampleMeta;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class UniqueConstraintsTest {
    /**
     * This method is to allow UniqueIndexCacheTest to create constraints
     */
    public static void createExampleConstraints() {
        new UniqueConstraintBuilder()
                .forMeta(ExampleMeta.get())
                .withUniquePropertyName(ExampleMeta.get().data)
                .createIfMissing(true)
                .createNoEx();

        new UniqueConstraintBuilder()
                .forMeta(ExampleMeta.get())
                .withUniquePropertyName(ExampleMeta.get().data)
                .withUniqueClassifierPropertyName(ExampleMeta.get().dataType)
                .createIfMissing(true)
                .createNoEx();
    }

    @Before
    public void initializeDatastore() {
        TestHelper.initializeDatastore();
    }

    @After
    public void cleanupDatastore() {
        TestHelper.cleanupDatastore();
    }

    @Test
    public void testWellDefined() throws Exception {
        TestHelper.assertUtilityClassWellDefined(UniqueConstraints.class);
    }
}