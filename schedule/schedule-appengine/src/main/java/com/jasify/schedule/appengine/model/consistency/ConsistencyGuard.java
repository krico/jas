package com.jasify.schedule.appengine.model.consistency;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.labs.repackaged.com.google.common.collect.ImmutableListMultimap;
import com.google.appengine.labs.repackaged.com.google.common.collect.ListMultimap;
import com.google.common.base.Throwables;
import com.jasify.schedule.appengine.model.HasId;
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
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URL;

/**
 * @author krico
 * @since 17/06/15.
 */
public final class ConsistencyGuard {
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
        if (resource.getProtocol().equals("file")) {
            String file = resource.getFile();
            //So that we don't use cache in dev
            return !file.matches(".*target/schedule-appengine.*");
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
        String packageName = ConsistencyGuard.class.getPackage().getName();
        ConfigurationBuilder configuration = new ConfigurationBuilder()
                .setUrls(ClasspathHelper.forPackage(packageName))
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

        System.err.println("!!!\n!!!\n!!!\n!!!\n!!!\n!!!\n!!! YEAH " + targetDir + "\n!!!\n!!!\n!!!\n!!!\n!!!\n!!!\n!!!\n");

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
