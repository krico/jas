package com.jasify.schedule.appengine.util;

import com.jasify.schedule.appengine.TestHelper;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static junit.framework.TestCase.*;

public class DigestUtilTest {
    @BeforeClass
    public static void lowerIterations() {
        DigestUtil.setIterations(1);
    }

    @AfterClass
    public static void resetIterations() {
        DigestUtil.setIterations(DigestUtil.DEFAULT_ITERATIONS);
    }

    @Test
    public void testAssertUtilityClassWellDefined() throws Exception {
        TestHelper.assertUtilityClassWellDefined(DigestUtil.class);
    }

    @Test
    public void testEncrypt2() {
        testEncrypt();
    }

    @Test
    public void testEncrypt() {
        String password = "password";
        byte[] encrypted = DigestUtil.encrypt(password);

        assertNotNull(encrypted);
        assertTrue(encrypted.length > 32);
        assertNotSame(password, new String(encrypted));
        assertTrue(DigestUtil.verify(encrypted, password));
        assertFalse(DigestUtil.verify(encrypted, ""));
        assertFalse(DigestUtil.verify(encrypted, "other"));
        assertFalse(DigestUtil.verify(encrypted, "password1"));
    }

    @Test(expected = NullPointerException.class)
    public void testEncryptWithNullInput() {
        DigestUtil.encrypt(null);
    }

    @Test
    public void testVerifyWithNullInput() {
        assertFalse(DigestUtil.verify(null, null));
    }
}