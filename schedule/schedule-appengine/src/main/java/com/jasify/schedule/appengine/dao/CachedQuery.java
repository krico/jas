package com.jasify.schedule.appengine.dao;

import com.google.appengine.api.datastore.Key;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

/**
 * @author krico
 * @since 31/05/15.
 */
public interface CachedQuery extends Serializable {
    String key();

    List<Key> execute();

    Parameters parameters();

    class Parameters implements Serializable {
        private final Serializable[] parameters;

        private Parameters(Serializable[] parameters) {
            this.parameters = parameters;
        }

        public static Parameters of(Serializable[] parameters) {
            return new Parameters(parameters);
        }

        @SuppressWarnings("unchecked")
        public <T extends Serializable> T get(int i) {
            return (T) parameters[i];
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Parameters that = (Parameters) o;

            if (!Arrays.equals(parameters, that.parameters)) return false;

            return true;
        }

        @Override
        public int hashCode() {
            return Arrays.hashCode(parameters);
        }

        @Override
        public String toString() {
            return Arrays.toString(parameters);
        }
    }
}
