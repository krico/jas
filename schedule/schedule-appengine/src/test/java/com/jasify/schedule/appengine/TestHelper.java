package com.jasify.schedule.appengine;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static junit.framework.TestCase.*;

/**
 * Created by krico on 09/11/14.
 */
public final class TestHelper {
    private TestHelper() {
    }

    public static void assertUtilityClassWellDefined(Class<?> clazz) throws Exception {
        String name = clazz.getName();
        assertTrue(name + " must be final",
                Modifier.isFinal(clazz.getModifiers()));
        assertEquals(name + " must have a single constructor", 1, clazz.getDeclaredConstructors().length);
        final Constructor<?> constructor = clazz.getDeclaredConstructor();
        if (constructor.isAccessible() || !Modifier.isPrivate(constructor.getModifiers())) {
            fail(name + " must have private constructor");
        }
        constructor.setAccessible(true);
        constructor.newInstance();
        constructor.setAccessible(false);
        for (Method method : clazz.getMethods()) {
            if (!Modifier.isStatic(method.getModifiers()) && method.getDeclaringClass().equals(clazz)) {
                fail(name + " must have only static methods:" + method);
            }
        }
    }
}
