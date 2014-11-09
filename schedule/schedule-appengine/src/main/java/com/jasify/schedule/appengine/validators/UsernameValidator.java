package com.jasify.schedule.appengine.validators;

import java.util.Collections;
import java.util.List;

/**
 * Created by krico on 10/11/14.
 */
public class UsernameValidator implements Validator<String> {
    public static final UsernameValidator INSTANCE = new UsernameValidator();

    @Override
    public List<String> validate(String value) {
        //TODO: length
        //TODO: datastore
        return Collections.emptyList();
    }
}
