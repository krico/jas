package com.jasify.schedule.appengine;

import com.google.appengine.api.datastore.*;
import com.google.appengine.repackaged.org.joda.time.DateTime;
import com.google.appengine.tools.development.testing.*;
import com.google.common.base.Throwables;
import com.jasify.schedule.appengine.meta.activity.ActivityMeta;
import com.jasify.schedule.appengine.meta.activity.ActivityPackageExecutionMeta;
import com.jasify.schedule.appengine.meta.activity.ActivityTypeMeta;
import com.jasify.schedule.appengine.meta.activity.SubscriptionMeta;
import com.jasify.schedule.appengine.meta.users.UserMeta;
import com.jasify.schedule.appengine.model.UniqueConstraint;
import com.jasify.schedule.appengine.model.UniqueConstraintException;
import com.jasify.schedule.appengine.model.UserContext;
import com.jasify.schedule.appengine.model.activity.*;
import com.jasify.schedule.appengine.model.application.ApplicationData;
import com.jasify.schedule.appengine.model.common.Organization;
import com.jasify.schedule.appengine.model.users.User;
import com.jasify.schedule.appengine.model.users.UsernameExistsException;
import com.jasify.schedule.appengine.oauth2.OAuth2ProviderEnum;
import com.jasify.schedule.appengine.util.DigestUtil;
import com.meterware.servletunit.ServletRunner;
import io.github.benas.jpopulator.api.Populator;
import io.github.benas.jpopulator.api.Randomizer;
import io.github.benas.jpopulator.impl.PopulatorBuilder;
import io.github.benas.jpopulator.randomizers.DateRangeRandomizer;
import junit.framework.AssertionFailedError;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.math3.random.RandomDataGenerator;
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
import java.util.*;
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
        UserContext.clearContext();
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
        assertNotSame("User class fails on travis-ci, I DON'T KNOW WHY :-(", User.class, type);

        PopulatorBuilder builder = new PopulatorBuilder();

        ArrayList<Field> declaredFields = new ArrayList<>(Arrays.asList(type.getDeclaredFields()));
        Set<String> excluded = new HashSet<>(Arrays.asList(excludedFields));

        for (Field declaredField : declaredFields) {
            Class<?> fieldType = declaredField.getType();
            String fieldName = declaredField.getName();
            if (excluded.contains(fieldName)) continue;
            if (fieldType.equals(ShortBlob.class)) {
                registerRandomizer(type, builder, fieldType, fieldName, new Randomizer() {
                    @Override
                    public Object getRandomValue() {
                        return new ShortBlob(RandomUtils.nextBytes(RandomUtils.nextInt(10, 500)));
                    }
                });
            } else if (fieldType.equals(Text.class)) {
                registerRandomizer(type, builder, fieldType, fieldName, new Randomizer() {
                    @Override
                    public Object getRandomValue() {
                        return new Text(RandomStringUtils.randomAscii(RandomUtils.nextInt(10, 500)));
                    }
                });
            } else if (fieldType.equals(Blob.class)) {
                registerRandomizer(type, builder, fieldType, fieldName, new Randomizer() {
                    @Override
                    public Object getRandomValue() {
                        return new Blob(RandomUtils.nextBytes(RandomUtils.nextInt(128, 1024)));
                    }
                });
            } else if (fieldType.equals(Key.class)) {
                registerRandomizer(type, builder, fieldType, fieldName, new Randomizer() {
                    @Override
                    public Object getRandomValue() {
                        return Datastore.allocateId("RandomKind");
                    }
                });
            } else if (fieldType.equals(Category.class)) {
                registerRandomizer(type, builder, fieldType, fieldName, new Randomizer() {
                    @Override
                    public Object getRandomValue() {
                        return new Category(RandomStringUtils.randomAlphabetic(RandomUtils.nextInt(3, 16)));
                    }
                });
            } else if (fieldType.equals(Email.class)) {
                registerRandomizer(type, builder, fieldType, fieldName, new Randomizer() {
                    @Override
                    public Object getRandomValue() {
                        return new Email("a" + RandomStringUtils.randomNumeric(RandomUtils.nextInt(3, 8)) + "@random.com");
                    }
                });
            } else if (fieldType.equals(GeoPt.class)) {
                registerRandomizer(type, builder, fieldType, fieldName, new Randomizer() {
                    @Override
                    public Object getRandomValue() {
                        return new GeoPt(RandomUtils.nextFloat(0, 90), RandomUtils.nextFloat(0, 180));
                    }
                });
            } else if (fieldType.equals(IMHandle.class)) {
                registerRandomizer(type, builder, fieldType, fieldName, new Randomizer() {
                    @Override
                    public Object getRandomValue() {
                        return new IMHandle(IMHandle.Scheme.xmpp, "a" + RandomStringUtils.randomNumeric(RandomUtils.nextInt(3, 8)) + "@random.com");
                    }
                });
            } else if (fieldType.equals(Link.class)) {
                registerRandomizer(type, builder, fieldType, fieldName, new Randomizer() {
                    @Override
                    public Object getRandomValue() {
                        return new Link("http://a" + RandomStringUtils.randomNumeric(RandomUtils.nextInt(3, 8)) + ".random.com");
                    }
                });
            } else if (fieldType.equals(PhoneNumber.class)) {
                registerRandomizer(type, builder, fieldType, fieldName, new Randomizer() {
                    @Override
                    public Object getRandomValue() {
                        return new PhoneNumber("555-" + RandomStringUtils.randomNumeric(4));
                    }
                });
            } else if (fieldType.equals(PostalAddress.class)) {
                registerRandomizer(type, builder, fieldType, fieldName, new Randomizer() {
                    @Override
                    public Object getRandomValue() {
                        return new PostalAddress("Street, " + RandomStringUtils.randomNumeric(4));
                    }
                });
            } else if (fieldType.equals(Rating.class)) {
                registerRandomizer(type, builder, fieldType, fieldName, new Randomizer() {
                    @Override
                    public Object getRandomValue() {
                        return new Rating(RandomUtils.nextInt(Rating.MIN_VALUE, Rating.MAX_VALUE));
                    }
                });
            }
        }

        return builder.build().populateBean(type, excludedFields);
    }

    private static <T> void registerRandomizer(final Class<T> type, PopulatorBuilder builder, final Class<?> fieldType, final String fieldName, final Randomizer randomizer) {
        builder.registerRandomizer(type, fieldType, fieldName, new Randomizer() {
            @Override
            public Object getRandomValue() {
                try {
                    return randomizer.getRandomValue();
                } catch (Exception e) {
                    System.err.println("Exception on randomizer [type=" + type + ", fieldType=" + fieldType + ", fieldName=" + fieldName + "]: " + e);
                    e.printStackTrace(System.err);
                    throw Throwables.propagate(e);
                }
            }
        });
    }

    public static class PriceRandomizer implements Randomizer<Double> {
        @Override
        public Double getRandomValue() {
            return new Double((new RandomDataGenerator()).nextLong(0, (long) Double.MAX_VALUE));
        }
    }

    public static class MaxCountRandomizer implements Randomizer<Integer> {
        @Override
        public Integer getRandomValue() {
            return new Long(((new RandomDataGenerator()).nextLong(1, (long) Integer.MAX_VALUE))).intValue();
        }
    }

    public static Organization createOrganization(boolean store) {
        Organization organization = com.jasify.schedule.appengine.TestHelper.populateBean(Organization.class, "id", "lcName", "organizationMemberListRef");
        if (store) {
            Datastore.put(organization);
        }
        return organization;
    }

    public static ActivityType createActivityType(Organization organization, boolean store) {
        PopulatorBuilder populatorBuilder = new PopulatorBuilder();
        populatorBuilder.registerRandomizer(ActivityType.class, Double.class, "price", new PriceRandomizer());
        populatorBuilder.registerRandomizer(ActivityType.class, int.class, "maxSubscriptions", new MaxCountRandomizer());
        Populator populator = populatorBuilder.build();

        ActivityType activityType = populator.populateBean(ActivityType.class, "id", "organizationRef", "lcName");
        activityType.setLcName(StringUtils.lowerCase(activityType.getName()));
        if (store) {
            // TODO: ActivityType requires the model not to be set but Activity requires the model to be set??
            activityType.getOrganizationRef().setModel(organization);
            activityType.setId(Datastore.allocateId(organization.getId(), ActivityTypeMeta.get()));
            Datastore.put(activityType);
        }
        return activityType;
    }

    public static Activity createActivity(ActivityType activityType, boolean store) {
        PopulatorBuilder populatorBuilder = new PopulatorBuilder();
        populatorBuilder.registerRandomizer(Activity.class, Double.class, "price", new PriceRandomizer());
        populatorBuilder.registerRandomizer(Activity.class, int.class, "maxSubscriptions", new MaxCountRandomizer());
        DateTime start = new DateTime().plusHours(1);
        DateTime finish = new DateTime().plusYears(10);

        populatorBuilder.registerRandomizer(Activity.class, Date.class, "start", new DateRangeRandomizer(start.toDate(), finish.toDate()));
        Populator populator = populatorBuilder.build();
        Activity activity = populator.populateBean(Activity.class, "id", "activityTypeRef", "repeatDetailsRef", "subscriptionListRef", "finish", "subscriptionCount");
        activity.setFinish(new DateTime(activity.getStart().getTime()).plusHours(1).toDate());
        activity.getActivityTypeRef().setModel(activityType);
        if (store) {
            activity.setId(Datastore.allocateId(activityType.getOrganizationRef().getKey(), ActivityMeta.get()));
            Datastore.put(activity);
        }
        return activity;
    }

    public static ActivityPackage createActivityPackage(Organization organization, boolean store) {
        PopulatorBuilder populatorBuilder = new PopulatorBuilder();
        populatorBuilder.registerRandomizer(ActivityPackage.class, Double.class, "price", new PriceRandomizer());
        populatorBuilder.registerRandomizer(ActivityPackage.class, int.class, "maxSubscriptions", new MaxCountRandomizer());
        populatorBuilder.registerRandomizer(ActivityPackage.class, int.class, "maxExecutions", new MaxCountRandomizer());
        populatorBuilder.registerRandomizer(ActivityPackage.class, int.class, "itemCount", new MaxCountRandomizer());
        Populator populator = populatorBuilder.build();
        ActivityPackage activityPackage = populator.populateBean(ActivityPackage.class, "id", "organizationRef", "activityPackageActivityListRef", "executionCount");
        activityPackage.getOrganizationRef().setModel(organization);
        if (store) {
            Datastore.put(activityPackage);
        }
        return activityPackage;
    }

    public static ActivityPackageExecution createActivityPackageExecution(User user, ActivityPackage activityPackage, boolean store) {
        PopulatorBuilder populatorBuilder = new PopulatorBuilder();
        Populator populator = populatorBuilder.build();
        ActivityPackageExecution activityPackageExecution = populator.populateBean(ActivityPackageExecution.class, "id", "activityPackageRef", "subscriptionListRef", "transferRef", "userRef");
        activityPackageExecution.setId(Datastore.allocateId(user.getId(), ActivityPackageExecutionMeta.get()));
        activityPackageExecution.getActivityPackageRef().setModel(activityPackage);
        activityPackageExecution.getUserRef().setModel(user);
        if (store) {
            Datastore.put(activityPackageExecution);
        }
        return activityPackageExecution;
    }

    public static Subscription createSubscription(User user, Activity activity, boolean store) {
        PopulatorBuilder populatorBuilder = new PopulatorBuilder();
        Populator populator = populatorBuilder.build();
        Subscription subscription = populator.populateBean(Subscription.class, "id", "activityRef", "userRef", "transferRef");
        subscription.setId(Datastore.allocateId(user.getId(), SubscriptionMeta.get()));
        subscription.getActivityRef().setModel(activity);
        subscription.getUserRef().setModel(user);
        if (store) {
            Datastore.put(subscription);
        }
        return subscription;
    }

    public static Activity createActivity(boolean storeAll) {
        Organization organization = createOrganization(storeAll);
        ActivityType activityType = createActivityType(organization, storeAll);
        return createActivity(activityType, storeAll);
    }

    public static User createUser(boolean store) {
        User user = new User("fred", "Em@il.com", "Real Name");
        if (store) {
            Datastore.put(user);
        }
        return user;
    }
}
