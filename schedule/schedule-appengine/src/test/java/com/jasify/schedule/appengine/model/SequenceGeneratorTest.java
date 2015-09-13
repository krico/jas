package com.jasify.schedule.appengine.model;

import com.jasify.schedule.appengine.TestHelper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;

public class SequenceGeneratorTest {
    @Before
    public void setupDatastore() {
        TestHelper.initializeDatastore();
    }

    @After
    public void cleanupDatastore() {
        TestHelper.cleanupDatastore();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIncrementCheckedWhenZero() {
        new SequenceGenerator("Sequence", 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIncrementCheckedWhenNegative() {
        new SequenceGenerator("Sequence", -1);
    }

    @Test
    public void testGenerateFirst() {
        SequenceGenerator sequence = new SequenceGenerator("Sequence");
        assertEquals(1, sequence.next());
    }

    @Test
    public void testGenerateFirstWithTwo() {
        SequenceGenerator sequence1 = new SequenceGenerator("Sequence1");
        SequenceGenerator sequence2 = new SequenceGenerator("Sequence2");
        assertEquals(1, sequence1.next());
        assertEquals(1, sequence2.next());
    }

    @Test
    public void testGenerateWithIncrementOne() {
        SequenceGenerator sequence1 = new SequenceGenerator("Sequence", 1);
        long expected = 1L;
        for (int i = 0; i < 10; ++i) {
            assertEquals(expected + i, sequence1.next());
        }
    }

    @Test
    public void testGenerateWithIncrementOneTwoSeqs() {
        SequenceGenerator sequence1 = new SequenceGenerator("Sequence", 1);
        SequenceGenerator sequence2 = new SequenceGenerator("Sequence", 1);
        long expected = 1L;
        for (int i = 0; i < 10; ++i) {
            long next;
            if (i % 2 == 0)
                next = sequence1.next();
            else
                next = sequence2.next();
            assertEquals(expected + i, next);
        }
    }

    @Test
    public void testGenerateWithIncrementHundred() {
        SequenceGenerator sequence1 = new SequenceGenerator("Sequence", 100);
        long expected = 1L;
        for (int i = 0; i < 1000; ++i) {
            assertEquals(expected + i, sequence1.next());
        }
    }

}