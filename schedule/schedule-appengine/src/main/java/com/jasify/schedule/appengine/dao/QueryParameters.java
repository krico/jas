package com.jasify.schedule.appengine.dao;

import java.io.Serializable;
import java.util.Arrays;

/**
 * @author krico
 * @since 03/06/15.
 */
public class QueryParameters implements Serializable {
    private final Serializable[] parameters;

    private QueryParameters(Serializable[] parameters) {
        this.parameters = parameters;
    }

    public static QueryParameters of(Serializable[] parameters) {
        return new QueryParameters(parameters);
    }

    @SuppressWarnings("unchecked")
    public <T extends Serializable> T get(int i) {
        return (T) parameters[i];
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        QueryParameters that = (QueryParameters) o;

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
