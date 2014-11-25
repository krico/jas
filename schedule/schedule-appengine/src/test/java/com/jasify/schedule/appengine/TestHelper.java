package com.jasify.schedule.appengine;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalMemcacheServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.jasify.schedule.appengine.http.json.JsonLoginRequest;
import com.jasify.schedule.appengine.http.json.JsonResponse;
import com.jasify.schedule.appengine.model.application.ApplicationData;
import com.jasify.schedule.appengine.model.users.User;
import com.jasify.schedule.appengine.model.users.UserServiceFactory;
import com.jasify.schedule.appengine.model.users.UsernameExistsException;
import com.jasify.schedule.appengine.util.DigestUtil;
import com.jasify.schedule.appengine.util.JSON;
import com.meterware.httpunit.PostMethodWebRequest;
import com.meterware.httpunit.WebRequest;
import com.meterware.httpunit.WebResponse;
import com.meterware.servletunit.ServletRunner;
import com.meterware.servletunit.ServletUnitClient;
import junit.framework.AssertionFailedError;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slim3.datastore.Datastore;
import org.slim3.datastore.DatastoreUtil;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

    public static File baseDir() {
        File file = new File(".");
        do {
            if (new File(file, "pom.xml").exists() &&
                    new File(file, "README.md").exists() &&
                    new File(file, "DEVELOPER.md").exists()) {
                return file;
            }
        } while ((file = file.getParentFile()) != null);
        throw new AssertionFailedError("Could not find BASE_DIR from " + new File(".").getAbsolutePath());
    }

    public static File relPath(String path) {
        return new File(baseDir(), path);
    }

    public static void initializeServletRunner() {
        TestHelper.initializeJasify();
        try {
            servletRunner = new ServletRunner(relPath("src/main/webapp/WEB-INF/web.xml"));
        } catch (Exception e) {
            throw new AssertionFailedError("Failed to create servletRunner: " + e);
        }
    }

    public static void cleanupServletRunner() {
        if (servletRunner != null) {
            servletRunner.shutDown();
        }
        servletRunner = null;
        cleanupDatastore();
    }

    public static ServletRunner servletRunner() {
        assertNotNull("Must call initializeServletRunner", servletRunner);
        return servletRunner;
    }

    public static ServletUnitClient login(String name, String password) throws IOException, SAXException {
        ServletUnitClient client = servletRunner().newClient();
        JsonLoginRequest req = new JsonLoginRequest(name, password);
        WebRequest request = new PostMethodWebRequest("http://schedule.jasify.com/login", IOUtils.toInputStream(req.toJson()), JSON.CONTENT_TYPE);
        WebResponse response = client.getResponse(request);
        assertNotNull("No response received", response);
        assertEquals("content type", JSON.CONTENT_TYPE, response.getContentType());
        String text = response.getText();
        assertNotNull(text);
        JsonResponse jr = JsonResponse.parse(text);
        assertNotNull(jr);
        assertFalse(jr.isNok());
        assertTrue(jr.isOk());
        assertTrue(StringUtils.isBlank(jr.getNokText()));

        return client;
    }

    public static void initializeDatastore() {
        datastoreHelper.setUp();
    }

    public static void cleanupDatastore() {
        datastoreHelper.tearDown();
        DatastoreUtil.clearKeysCache();
    }

    public static void initializeMemcache() {
        memcacheHelper.setUp();
    }

    public static void cleanupMemcache() {
        memcacheHelper.tearDown();
    }

    public static void assertEqualsNoMillis(Date d1, Date d2) {
        if (d1 == null || d2 == null) {
            assertEquals("both must be null", d1, d2);
        } else {
            long l1 = d1.getTime() / 1000L;
            long l2 = d2.getTime() / 1000L;
            assertEquals(new Date(l1 * 1000L), new Date(l2 * 1000L));
        }
    }

    public static List<User> createUsers(int total) throws UsernameExistsException {
        DigestUtil.setIterations(1);
        try {
            List<User> created = new ArrayList<>();
            for (int i = 0; i < total; ++i) {
                User user = new User();
                user.setId(Datastore.createKey(User.class, (long) i + 1000));
                user.setName(String.format("user%03d", i));
                user.setEmail(String.format("user%03d@new.co", i));
                created.add(UserServiceFactory.getUserService().create(user, "password"));
            }
            return created;
        } finally {
            DigestUtil.setIterations(16192);
        }
    }
}
