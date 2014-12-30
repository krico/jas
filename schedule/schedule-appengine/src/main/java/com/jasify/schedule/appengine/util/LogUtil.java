package com.jasify.schedule.appengine.util;

import com.google.common.base.Function;

/**
 * @author krico
 * @since 30/12/14.
 */
public final class LogUtil {
    private LogUtil() {
    }

    /**
     * Create a lazy to string that only evaluate toStringFn zero or one time.
     *
     * @param toStringFn a function that builds the string
     * @return an object that will only evaluate <code>toStringFn</code> when it's toString method is called
     */
    public static <T, S extends CharSequence> Lazy<T, S> toLazyString(final Function<T, S> toStringFn, T data) {
        return new Lazy<>(toStringFn, data);
    }

    public static class Lazy<T, S extends CharSequence> {
        private final Function<T, S> fn;
        private final T data;
        private S resolved;

        private Lazy(Function<T, S> fn, T data) {
            this.fn = fn;
            this.data = data;
        }

        public S toSequence() {
            if (resolved == null) {
                resolved = fn.apply(data);
            }
            return resolved;
        }

        @Override
        public String toString() {
            return toSequence().toString();
        }
    }
}
