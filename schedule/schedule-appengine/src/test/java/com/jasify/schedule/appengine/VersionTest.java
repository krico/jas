package com.jasify.schedule.appengine;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertTrue;

public class VersionTest {
    private static final Logger log = LoggerFactory.getLogger(VersionTest.class);

    @Test
    public void testWellDefined() throws Exception {
        TestHelper.assertUtilityClassWellDefined(Version.class);
    }

    @Test
    public void testGetNumber() throws Exception {
        assertNotNull(Version.getNumber());
    }

    @Test
    public void testGetBranch() throws Exception {
        assertNotNull(Version.getBranch());
    }

    @Test
    public void testGetTimestamp() throws Exception {
        assertTrue(Version.getTimestamp() != 0);
    }

    @Test
    public void testGetVersion() throws Exception {
        assertNotNull(Version.getVersion());
    }

    @Test
    public void testGetTimestampVersion() throws Exception {
        assertNotNull(Version.getTimestampVersion());
    }

    @Test
    public void testToVersionString() throws Exception {
        assertNotNull(Version.toVersionString());
        log.info("Version: {}", Version.toVersionString());
    }
}