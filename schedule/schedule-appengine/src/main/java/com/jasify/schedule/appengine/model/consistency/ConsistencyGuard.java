package com.jasify.schedule.appengine.model.consistency;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.labs.repackaged.com.google.common.collect.ImmutableListMultimap;
import com.google.appengine.labs.repackaged.com.google.common.collect.ListMultimap;
import com.google.common.base.Throwables;
import com.jasify.schedule.appengine.model.HasId;
import com.jasify.schedule.appengine.util.EnvironmentUtil;
import com.jasify.schedule.appengine.util.JSON;
import com.jasify.schedule.appengine.util.Threads;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.serializers.JsonSerializer;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Map;

/**
 * @author krico
 * @since 17/06/15.
 */
public final class ConsistencyGuard {
    public static final String PACKAGE_NAME = "com.jasify.schedule.appengine";
    private static final String CACHE_PATH = "META-INF/ConsistencyGuard.json";
    private static final Logger log = LoggerFactory.getLogger(ConsistencyGuard.class);
    private static boolean initialized = false;
    private static ListMultimap<Class<?>, Call> BEFORE_DELETE;

    private ConsistencyGuard() {
    }

    public static void initialize() throws InconsistentModelStateException {
        synchronized (ConsistencyGuard.class) {
            if (initialized) return;
            log.debug("Initializing");
            URL resource = ConsistencyGuard.class.getResource("/" + CACHE_PATH);
            Reflections reflections;
            if (useCache(resource)) {
                reflections = createCachedReflections(resource);
            } else {
                reflections = createDynamicReflections();
            }
            ImmutableListMultimap.Builder<Class<?>, Call> beforeDeleteBuilder = ImmutableListMultimap.builder();
            for (Method method : reflections.getMethodsAnnotatedWith(BeforeDelete.class)) {
                BeforeDelete beforeDelete = method.getAnnotation(BeforeDelete.class);
                beforeDeleteBuilder.put(beforeDelete.entityClass(), new Call(method));
            }
            BEFORE_DELETE = beforeDeleteBuilder.build();

            initialized = true;
        }
        log.debug("ConsistencyGuard initialized: BEFORE_DELETE: {}", BEFORE_DELETE);
    }

    private static boolean useCache(URL resource) {
        if (resource == null) return false;

        if (EnvironmentUtil.isContinuousIntegrationEnvironment()) return true; // no dynamic in CI

        if (resource.getProtocol().equals("file")) {
            String file = resource.getFile();
            if (file.matches(".*target/schedule-appengine.*")) {

                /* It looks like we are in dev, in appengine when we scan reflections we get a bunch of exceptions
                 * I wanted to change
                 */
                File jasifyLocalConfig = EnvironmentUtil.jasifyLocalConfig();
                if (jasifyLocalConfig.exists()) {
                    try (FileReader reader = new FileReader(jasifyLocalConfig)) {
                        Map map = JSON.fromJson(reader, Map.class);
                        Map applicationConfig = (Map) map.get("DevConfig");
                        if (applicationConfig != null) {
                            Object useCacheObj = applicationConfig.get("ConsistencyGuard.UseCache");
                            if (useCacheObj instanceof String) {
                                return Boolean.valueOf((String) useCacheObj);
                            }
                        }
                    } catch (IOException e) {
                        throw Throwables.propagate(e);
                    }
                }
                log.warn("\n!!!\n!!! A T T E N T I O N !\n!!!\nYou should set the property [ConsistencyGuard.UseCache] in [{}]\n" +
                        "Then you will no longer get this warning...\n" +
                        "It is described in DEVELOPER.md\n" +
                        "If you are not changing java code, set it to true...\n" +
                        "Get ready, you will see a bunch of exceptions in the logs now, they can be ignored but are really annoying...\n" +
                        "Sleeping for 10 seconds to make sure you see this message at some point...", jasifyLocalConfig);
                Threads.sleep(10000);
                return false;
            }
            return true;
        }
        return true;
    }

    private static Reflections createCachedReflections(URL resource) {
        log.debug("Creating cached reflections...");
        Reflections reflections;
        try (InputStream is = resource.openStream()) {
            reflections = new JsonSerializer().read(is);
        } catch (IOException e) {
            throw Throwables.propagate(e);
        }
        return reflections;
    }

    private static Reflections createDynamicReflections() {
        log.info("Creating dynamic reflections...");
        ConfigurationBuilder configuration = new ConfigurationBuilder()
                .setUrls(ClasspathHelper.forPackage(PACKAGE_NAME))
                .setScanners(
                        new SubTypesScanner().filterResultsBy(new FilterBuilder().include(EntityConsistency.class.getName())),
                        new MethodAnnotationsScanner().filterResultsBy(new FilterBuilder().include(BeforeDelete.class.getName()))
                );
        return new Reflections(configuration);
    }

    public static <T extends HasId> void beforeDelete(T entity) throws InconsistentModelStateException {
        beforeDelete(entity.getClass(), entity.getId());
    }

    public static <T> void beforeDelete(Class<T> entityClass, Key id) throws InconsistentModelStateException {
        initialize();
        for (Call call : BEFORE_DELETE.get(entityClass)) {
            call.execute(id);
        }
    }

    public static void main(String[] args) throws Exception {
        if (args.length == 0) throw new IllegalArgumentException("Usage: program target/dir");
        File targetDir = new File(args[0]);
        if (!targetDir.isDirectory()) {
            throw new IllegalArgumentException("Target directory is not a directory: " + targetDir);
        }

        File jsonCache = new File(targetDir, CACHE_PATH);
        if (jsonCache.getParentFile().mkdirs()) {
            throw new IOException("Failed to create: " + jsonCache.getParentFile());
        }
        Reflections reflexions = createDynamicReflections();
        reflexions.save(jsonCache.getAbsolutePath(), new JsonSerializer());
        System.out.println("Created [" + jsonCache + "]");
    }

    private static class Call {
        private final Method method;
        private EntityConsistency<?> instance;

        private Call(Method method) throws InconsistentModelStateException {
            this.method = method;
            if (!EntityConsistency.class.isAssignableFrom(method.getDeclaringClass())) {
                throw new InconsistentModelStateException("Invalid annotated method: " + method + ". " +
                        "Declaring class should implement " + EntityConsistency.class.getName());
            }
            Class<?>[] parameterTypes = method.getParameterTypes();
            if (parameterTypes.length != 1 && parameterTypes[0].equals(Key.class)) {
                throw new InconsistentModelStateException("Invalid annotated method: " + method + ". " +
                        "Should have signature public void method(Key id);");
            }
        }

        public void execute(Key entityId) throws InconsistentModelStateException {
            EntityConsistency<?> local = instance;
            if (local == null) {
                try {
                    local = method.getDeclaringClass().asSubclass(EntityConsistency.class).newInstance();
                } catch (Exception e) {
                    log.error("Failed to instantiate {}", method.getDeclaringClass(), e);
                    throw new InconsistentModelStateException("Failed to instantiate method: " + method);
                }
            }
            try {
                method.invoke(local, entityId);
            } catch (Exception e) {
                Throwable cause = e.getCause();
                Throwables.propagateIfInstanceOf(cause, InconsistentModelStateException.class);
                throw new InconsistentModelStateException("Exception invoking method: " + method, e);
            }
            if (instance == null) instance = local;
        }
    }
}
