package com.jasify.schedule.appengine.util;

import com.google.appengine.api.datastore.Email;
import com.google.appengine.api.datastore.ShortBlob;
import com.google.appengine.api.datastore.Text;
import com.jasify.schedule.appengine.TestHelper;
import org.junit.Test;

import java.util.Objects;

import static junit.framework.TestCase.*;

public class TypeUtilTest {
    @Test
    public void testAssertUtilityClassWellDefined() throws Exception {
        TestHelper.assertUtilityClassWellDefined(TypeUtil.class);
    }

    @Test
    public void testEmail() {
        assertNull(TypeUtil.toString((Email) null));
        assertNull(TypeUtil.toEmail(null));
        assertEquals("jas@jasify.com", TypeUtil.toString(new Email("jas@jasify.com")));
        assertEquals(new Email("jas@jasify.com"), TypeUtil.toEmail("jas@jasify.com"));
    }

    @Test
    public void testText() {
        assertNull(TypeUtil.toString((Text) null));
        assertNull(TypeUtil.toText(null));
        assertEquals("My text", TypeUtil.toString(new Text("My text")));
        assertEquals(new Text("My text"), TypeUtil.toText("My text"));
    }

    @Test
    public void testShortBlob() {
        byte[] small = {1, 2, 3, 4, 5};
        assertNull(TypeUtil.toBytes((ShortBlob) null));
        assertNull(TypeUtil.toShortBlob(null));
        assertTrue(Objects.deepEquals(small, TypeUtil.toBytes(new ShortBlob(small))));
        assertEquals(new ShortBlob(small), TypeUtil.toShortBlob(small));
    }


}