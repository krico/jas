package com.jasify.schedule.appengine.memcache;

import com.google.appengine.api.ThreadManager;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.jasify.schedule.appengine.TestHelper;
import com.jasify.schedule.appengine.util.Threads;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slim3.memcache.S3ErrorHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;

import static junit.framework.TestCase.*;

public class MemcacheOperatorTest {
    private static final Logger log = LoggerFactory.getLogger(MemcacheOperatorTest.class);
    private MemcacheService service;

    @Before
    public void setupMemcache() {
        TestHelper.initializeMemcacheWithDatastore();
        service = MemcacheServiceFactory.getMemcacheService();
        service.setErrorHandler(new S3ErrorHandler());
    }

    @After
    public void cleanupMemcache() {
        TestHelper.cleanupDatastore();
    }

    @Test
    public void testPutNonPresent() {
        final String key = RandomStringUtils.randomAscii(16);
        final String expected = RandomStringUtils.randomAscii(512);

        Object o = service.get(key);
        assertNull(o);

        String update = MemcacheOperator.update(new BaseMemcacheTransaction<String>(key) {
            @Nonnull
            @Override
            public String execute(@Nullable MemcacheService.IdentifiableValue identifiable) {
                assertNull(identifiable);
                return expected;
            }
        });

        assertEquals(update, expected);
        Object fetched = service.get(key);
        assertEquals(fetched, expected);
    }

    @Test
    public void testPutPresent() {
        final String key = RandomStringUtils.randomAscii(16);
        final String before = RandomStringUtils.randomAscii(512);
        final String expected = RandomStringUtils.randomAscii(512);

        service.put(key, before);
        Object o = service.get(key);
        assertNotNull(o);
        assertEquals(before, o);

        String updated = MemcacheOperator.update(new BaseMemcacheTransaction<String>(key) {
            @Nonnull
            @Override
            public String execute(@Nullable MemcacheService.IdentifiableValue identifiable) {
                assertNotNull(identifiable);
                assertEquals(before, identifiable.getValue());
                return expected;
            }
        });

        assertEquals(expected, updated);
        Object fetched = service.get(key);
        assertEquals(expected, fetched);
    }

    @Test
    public void testParallel() throws Exception {
        final String key = RandomStringUtils.randomAscii(16);

        List<String> expected = new ArrayList<>();
        for (int i = 0; i < 500; ++i) {
            expected.add(RandomStringUtils.randomAscii(RandomUtils.nextInt(16, 32)));
        }

        ThreadFactory threadFactory = ThreadManager.currentRequestThreadFactory();
        ExecutorService executor = Executors.newFixedThreadPool(5, threadFactory);
        try {
            List<Future<?>> jobs = new ArrayList<>();
            for (final String string : expected) {
                final int pos = jobs.size();
                Future<?> job = executor.submit(new Runnable() {
                    @Override
                    public void run() {
                        MemcacheOperator
                                .update(new BaseMemcacheTransaction<List<String>>(key) {
                                    @Nonnull
                                    @Override
                                    public List<String> execute(@Nullable MemcacheService.IdentifiableValue identifiable) {
                                        List<String> data;
                                        if (identifiable == null) {
                                            data = new ArrayList<String>();
                                        } else {
                                            //noinspection unchecked
                                            data = (List<String>) identifiable.getValue();
                                        }
                                        data.add(string);
                                        Collections.sort(data);
                                        return data;
                                    }
                                });
                    }
                });
                jobs.add(job);
            }

            for (Future<?> job : jobs) {
                job.get();
            }
        } finally {
            executor.shutdown();
        }
        Collections.sort(expected);
        Object o = service.get(key);
        assertEquals(expected, o);
        MemcacheOperator.Statistics statistics = MemcacheOperator.statistics();
        assertTrue("Must have at least one concurrency error: " + statistics, statistics.getRetries() > 0);
        log.info("{}", statistics);
    }

}