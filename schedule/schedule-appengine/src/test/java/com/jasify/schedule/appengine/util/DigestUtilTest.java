package com.jasify.schedule.appengine.util;

import com.jasify.schedule.appengine.TestHelper;
import org.apache.commons.lang3.ArrayUtils;
import org.junit.Test;

import static junit.framework.TestCase.*;

public class DigestUtilTest {
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

}