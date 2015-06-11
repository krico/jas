package com.jasify.schedule.appengine;

import com.google.appengine.api.datastore.Key;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.jasify.schedule.appengine.model.HasId;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;

/**
 * @author krico
 * @since 11/06/15.
 */
public final class AssertionHelper {
    private AssertionHelper() {
    }

    public static <T extends HasId> void assertIdsEqual(List<T> expected, List<T> real) {
        assertNotNull("expected is NULL", expected);
        assertNotNull("real is NULL", real);
        assertEquals("expected.size != real.size", expected.size(), real.size());

        Function<T, Key> toKey = new Function<T, Key>() {
            @Nullable
            @Override
            public Key apply(T t) {
                return t.getId();
            }
        };
        List<Key> expectedKeys = new ArrayList<>(Lists.transform(expected, toKey));
        List<Key> realKeys = new ArrayList<>(Lists.transform(real, toKey));
        Collections.sort(expectedKeys);
        Collections.sort(realKeys);
        for (int i = 0; i < expectedKeys.size(); ++i) {
            assertEquals("key[" + i + "]", expectedKeys.get(i), realKeys.get(i));
        }
    }
}
