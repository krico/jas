package com.jasify.schedule.appengine;

import com.google.appengine.api.datastore.Key;
import com.jasify.schedule.appengine.model.HasId;
import junit.framework.AssertionFailedError;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slim3.datastore.Datastore;

import java.util.ArrayList;
import java.util.List;

import static com.jasify.schedule.appengine.AssertionHelper.assertIdsEqual;

public class AssertionHelperTest {

    @BeforeClass
    public static void datastore() {
        TestHelper.initializeDatastore();
    }

    @AfterClass
    public static void cleanup() {
        TestHelper.cleanupDatastore();
    }

    @Test
    public void testAssertIdsEqualSameOrder() throws Exception {
        List<Sample> expected = new ArrayList<>();
        List<Sample> real = new ArrayList<>();
        for (int i = 0; i < 20; ++i) {
            Sample sample = new Sample(Datastore.allocateId("Sample"));
            expected.add(sample);
            real.add(new Sample(sample.getId()));
        }

        assertIdsEqual(expected, real);
    }

    @Test
    public void testAssertIdsEqualReverse() throws Exception {
        List<Sample> expected = new ArrayList<>();
        for (int i = 0; i < 20; ++i) {
            expected.add(new Sample(Datastore.allocateId("Sample")));
        }

        List<Sample> real = new ArrayList<>();

        for (int i = 0; i < expected.size(); ++i) {
            real.add(expected.get(expected.size() - 1 - i));
        }
        assertIdsEqual(expected, real);
    }

    @Test
    public void testAssertIdsEqualEmpty() throws Exception {
        List<Sample> expected = new ArrayList<>();
        List<Sample> real = new ArrayList<>();
        assertIdsEqual(expected, real);
    }

    @Test(expected = AssertionFailedError.class)
    public void testAssertIdsEqualDifferent() throws Exception {
        List<Sample> expected = new ArrayList<>();
        List<Sample> real = new ArrayList<>();
        expected.add(new Sample(Datastore.allocateId("Sample")));
        real.add(new Sample(Datastore.allocateId("Sample")));

        assertIdsEqual(expected, real);
    }

    class Sample implements HasId {
        private Key id;

        public Sample(Key id) {
            this.id = id;
        }

        @Override
        public Key getId() {
            return id;
        }

        public void setId(Key id) {
            this.id = id;
        }
    }
}