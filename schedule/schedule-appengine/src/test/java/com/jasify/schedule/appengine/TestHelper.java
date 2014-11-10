package com.jasify.schedule.appengine;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalMemcacheServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.jasify.schedule.appengine.model.application.ApplicationData;
import com.meterware.servletunit.ServletRunner;

import javax.servlet.http.HttpServlet;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static junit.framework.TestCase.*;

/**
 * @author krico
 * @since 09/11/14.
 */
public final class TestHelper {
    private static final LocalServiceTestHelper datastoreHelper = new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());
    private static final LocalServiceTestHelper memcacheHelper = new LocalServiceTestHelper(new LocalMemcacheServiceTestConfig());
    private static ServletRunner servletRunner;

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

    public static void initializeJasify() {
        initializeDatastore();
        ApplicationData.instance().reload();
    }

    public static ServletInfo si(String name, Class<? extends HttpServlet> servletClass) {
        return new ServletInfo(name, servletClass);
    }

    public static void initializeServletRunner(String name, Class<? extends HttpServlet> klass) {
        initializeServletRunner(si(name, klass));
    }

    public static void initializeServletRunner(ServletInfo... servlets) {
        TestHelper.initializeJasify();
        servletRunner = new ServletRunner();
        for (ServletInfo servlet : servlets) {
            servletRunner.registerServlet(servlet.name, servlet.klass.getName());
        }
    }

    public static void cleanupServletRunner() {
        servletRunner = null;
        cleanupDatastore();
    }

    public static ServletRunner servletRunner() {
        return servletRunner;
    }

    public static void initializeDatastore() {
        datastoreHelper.setUp();
    }

    public static void cleanupDatastore() {
        datastoreHelper.tearDown();
    }

    public static void initializeMemcache() {
        memcacheHelper.setUp();
    }

    public static void cleanupMemcache() {
        memcacheHelper.tearDown();
    }

    public static class ServletInfo {
        private final String name;
        private final Class<? extends HttpServlet> klass;

        private ServletInfo(String name, Class<? extends HttpServlet> klass) {
            this.name = name;
            this.klass = klass;
        }
    }
}
