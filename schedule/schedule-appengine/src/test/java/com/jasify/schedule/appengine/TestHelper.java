package com.jasify.schedule.appengine;

import com.google.appengine.api.datastore.*;
import com.google.appengine.tools.development.testing.*;
import com.google.common.base.Throwables;
import com.jasify.schedule.appengine.meta.users.UserMeta;
import com.jasify.schedule.appengine.model.UniqueConstraint;
import com.jasify.schedule.appengine.model.UniqueConstraintException;
import com.jasify.schedule.appengine.model.application.ApplicationData;
import com.jasify.schedule.appengine.model.users.User;
import com.jasify.schedule.appengine.model.users.UsernameExistsException;
import com.jasify.schedule.appengine.oauth2.OAuth2ProviderEnum;
import com.jasify.schedule.appengine.util.DigestUtil;
import com.meterware.servletunit.ServletRunner;
import io.github.benas.jpopulator.api.Randomizer;
import io.github.benas.jpopulator.impl.PopulatorBuilder;
import junit.framework.AssertionFailedError;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slim3.datastore.Datastore;
import org.slim3.datastore.DatastoreUtil;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;

import static junit.framework.TestCase.*;

/**
 * @author krico
 * @since 09/11/14.
 */
public final class TestHelper {
    private static final Logger log = LoggerFactory.getLogger(TestHelper.class);

    static {
        setSystemProperties();
    }

    private static final LocalServiceTestHelper mailHelper = new LocalServiceTestHelper(
            createDatastoreServiceTestConfig(),
            new LocalMailServiceTestConfig()
                    .setLogMailBody(false)
                    .setLogMailLevel(Level.OFF)
    );
    private static final LocalServiceTestHelper appIdentityHelper = new LocalServiceTestHelper(new LocalAppIdentityServiceTestConfig());
    private static final LocalServiceTestHelper datastoreHelper = new LocalServiceTestHelper(
            createDatastoreServiceTestConfig()
    );
    private static final LocalServiceTestHelper memcacheWithDatastoreHelper = new LocalServiceTestHelper(createDatastoreServiceTestConfig(), new LocalMemcacheServiceTestConfig());
    private static ServletRunner servletRunner;

    private TestHelper() {
    }

    public static void setSystemProperties() {
        System.setProperty("slim3.useXGTX", "true");
    }

    public static LocalDatastoreServiceTestConfig createDatastoreServiceTestConfig() {
        return new LocalDatastoreServiceTestConfig()
                .setApplyAllHighRepJobPolicy();
//                .setDefaultHighRepJobPolicyUnappliedJobPercentage(100);
// todo: read https://cloud.google.com/appengine/docs/java/tools/localunittesting#Java_Writing_High_Replication_Datastore_tests
    }

    public static void assertUtilityClassWellDefined(Class<?> clazz) throws Exception {
        String name = clazz.getName();
        assertTrue(name + " must be final", Modifier.isFinal(clazz.getModifiers()));

        assertEquals(name + " must have a single constructor " + Arrays.toString(clazz.getDeclaredConstructors()), 1, clazz.getDeclaredConstructors().length);
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

    public static void initializeJasify(LocalServiceTestHelper datastoreHelper) {
        initializeDatastore(datastoreHelper);
        ApplicationData.instance().reload();
    }

    public static void initializeJasify() {
        initializeJasify(datastoreHelper);
    }

    public static void initializeJasifyWithOAuthProviderData() {
        initializeJasify(datastoreHelper);
        initializeOAuthProviderProperties();

    }

    public static void initializeOAuthProviderProperties() {
        for (OAuth2ProviderEnum provider : OAuth2ProviderEnum.values()) {
            ApplicationData.instance().setProperty(provider.clientIdKey(), provider + "ID");
            ApplicationData.instance().setProperty(provider.clientSecretKey(), provider + "Secret");
        }
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

    public static void initializeDatastore() {
        initializeDatastore(datastoreHelper);
    }

    public static void initializeDatastore(LocalServiceTestHelper datastoreHelper) {
        datastoreHelper.setUp();
    }

    public static void cleanupDatastore() {
        cleanupDatastore(datastoreHelper);
    }

    public static void cleanupDatastore(LocalServiceTestHelper datastoreHelper) {
        LocalDatastoreServiceTestConfig.getLocalDatastoreService().stop();
        datastoreHelper.tearDown();
        DatastoreUtil.clearKeysCache();
        DatastoreUtil.clearActiveGlobalTransactions();
    }

    public static void initializeMemcacheWithDatastore() {
        memcacheWithDatastoreHelper.setUp();
    }

    public static void cleanupMemcacheWithDatastore() {
        memcacheWithDatastoreHelper.tearDown();
    }

    public static void initializeAppIdentity() {
        appIdentityHelper.setUp();
    }

    public static void cleanupAppIdentity() {
        appIdentityHelper.tearDown();
    }

    public static void initializeMail() {
        mailHelper.setUp();
    }

    public static void cleanupMail() {
        mailHelper.tearDown();
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
        int i = 0;
        try {

            UniqueConstraint constraint = UniqueConstraint.create(UserMeta.get(), UserMeta.get().name);

            List<User> created = new ArrayList<>();
            ShortBlob shortBlob = new ShortBlob(DigestUtil.encrypt("password"));
            for (; i < total; ++i) {
                User user = new User();
                user.setId(Datastore.allocateId(User.class));
                user.setName(String.format("user%03d", i));
                constraint.reserve(user.getName());
                user.setEmail(String.format("user%03d@new.co", i));
                user.setPassword(shortBlob);
                created.add(user);
            }
            Datastore.put(created);
            return created;
        } catch (UniqueConstraintException e) {
            throw new UsernameExistsException(e.toString());
        } catch (RuntimeException e) {
            System.err.println("ERR i=" + i);
            throw e;
        }
    }

    public static void tearDown(TestService... testServices) {
        List<Exception> exceptions = new ArrayList<>();
        for (TestService testService : testServices) {
            try {
                testService.tearDown();
            } catch (Exception e) {
                log.debug("Exception during tearDown of " + testService, e);
                exceptions.add(e);
            }
        }
        if (!exceptions.isEmpty()) {
            StringBuilder builder = new StringBuilder("Exceptions(").append(exceptions.size()).append(") during tearDown:\n\n");
            for (Exception exception : exceptions) {
                builder.append(Throwables.getStackTraceAsString(exception)).append("\n");
            }
            throw new AssertionFailedError(builder.toString());
        }
    }

    public static void assertSerializable(Object anyObject) throws IOException {
        assertNotNull(anyObject);
        try {
            new ObjectOutputStream(new ByteArrayOutputStream()).writeObject(anyObject);
        } catch (Exception e) {
            fail("NOT SERIALIZABLE [" + anyObject.getClass().getName() + "]: " + e);
        }
    }

    public static <T> T populateBean(final Class<T> type, final String... excludedFields) {

        PopulatorBuilder builder = new PopulatorBuilder();

        ArrayList<Field> declaredFields = new ArrayList<>(Arrays.asList(type.getDeclaredFields()));

        for (Field declaredField : declaredFields) {
            Class<?> fieldType = declaredField.getType();
            String fieldName = declaredField.getName();
            if (fieldType.equals(ShortBlob.class)) {
                builder.registerRandomizer(type, fieldType, fieldName, new Randomizer() {
                    @Override
                    public Object getRandomValue() {
                        return new ShortBlob(RandomUtils.nextBytes(RandomUtils.nextInt(10, 500)));
                    }
                });
            } else if (fieldType.equals(Text.class)) {
                builder.registerRandomizer(type, fieldType, fieldName, new Randomizer() {
                    @Override
                    public Object getRandomValue() {
                        return new Text(RandomStringUtils.randomAscii(RandomUtils.nextInt(10, 500)));
                    }
                });
            } else if (fieldType.equals(Blob.class)) {
                builder.registerRandomizer(type, fieldType, fieldName, new Randomizer() {
                    @Override
                    public Object getRandomValue() {
                        return new Blob(RandomUtils.nextBytes(RandomUtils.nextInt(128, 1024)));
                    }
                });
            } else if (fieldType.equals(Key.class)) {
                builder.registerRandomizer(type, fieldType, fieldName, new Randomizer() {
                    @Override
                    public Object getRandomValue() {
                        return Datastore.allocateId("RandomKind");
                    }
                });
            } else if (fieldType.equals(Category.class)) {
                builder.registerRandomizer(type, fieldType, fieldName, new Randomizer() {
                    @Override
                    public Object getRandomValue() {
                        return new Category(RandomStringUtils.randomAlphabetic(RandomUtils.nextInt(3, 16)));
                    }
                });
            } else if (fieldType.equals(Email.class)) {
                builder.registerRandomizer(type, fieldType, fieldName, new Randomizer() {
                    @Override
                    public Object getRandomValue() {
                        return new Email("a" + RandomStringUtils.randomNumeric(RandomUtils.nextInt(3, 8)) + "@random.com");
                    }
                });
            } else if (fieldType.equals(GeoPt.class)) {
                builder.registerRandomizer(type, fieldType, fieldName, new Randomizer() {
                    @Override
                    public Object getRandomValue() {
                        return new GeoPt(RandomUtils.nextFloat(0, 90), RandomUtils.nextFloat(0, 180));
                    }
                });
            } else if (fieldType.equals(IMHandle.class)) {
                builder.registerRandomizer(type, fieldType, fieldName, new Randomizer() {
                    @Override
                    public Object getRandomValue() {
                        return new IMHandle(IMHandle.Scheme.xmpp, "a" + RandomStringUtils.randomNumeric(RandomUtils.nextInt(3, 8)) + "@random.com");
                    }
                });
            } else if (fieldType.equals(Link.class)) {
                builder.registerRandomizer(type, fieldType, fieldName, new Randomizer() {
                    @Override
                    public Object getRandomValue() {
                        return new Link("http://a" + RandomStringUtils.randomNumeric(RandomUtils.nextInt(3, 8)) + ".random.com");
                    }
                });
            } else if (fieldType.equals(PhoneNumber.class)) {
                builder.registerRandomizer(type, fieldType, fieldName, new Randomizer() {
                    @Override
                    public Object getRandomValue() {
                        return new PhoneNumber("555-" + RandomStringUtils.randomNumeric(4));
                    }
                });
            } else if (fieldType.equals(PostalAddress.class)) {
                builder.registerRandomizer(type, fieldType, fieldName, new Randomizer() {
                    @Override
                    public Object getRandomValue() {
                        return new PostalAddress("Street, " + RandomStringUtils.randomNumeric(4));
                    }
                });
            } else if (fieldType.equals(Rating.class)) {
                builder.registerRandomizer(type, fieldType, fieldName, new Randomizer() {
                    @Override
                    public Object getRandomValue() {
                        return new Rating(RandomUtils.nextInt(Rating.MIN_VALUE, Rating.MAX_VALUE));
                    }
                });
            }
        }

        return builder.build().populateBean(type, excludedFields);
    }
}
