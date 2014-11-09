package com.jasify.schedule.appengine.validators;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Created by krico on 10/11/14.
 */
public interface Validator<T> {

    /**
     * Validate some value
     *
     * @param value to be validated
     * @return empty list if valid or a list of reasons why <code>value</code> is invalid.
     */
    @Nonnull
    List<String> validate(T value);
}
