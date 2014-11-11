package com.jasify.schedule.appengine.validators;

import com.jasify.schedule.appengine.model.users.UserServiceFactory;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by krico on 10/11/14.
 */
public class UsernameValidator implements Validator<String> {
    public static final UsernameValidator INSTANCE = new UsernameValidator();

    public static final String REASON_LENGTH = "Must be at least 3 characters long.";
    public static final String REASON_VALID_CHARS = "Must only contain letters, numbers or ('.', '-', '_').";
    public static final String REASON_EXISTS = "Username already exists.";

    private static final Pattern VALID_CHARS = Pattern.compile("^[a-z0-9-._]+$");

    @Override
    public List<String> validate(String value) {
        ArrayList<String> ret = new ArrayList<>();
        String name = StringUtils.trim(value);
        name = StringUtils.lowerCase(name);

        if (name.length() < 3) {
            ret.add(REASON_LENGTH);
        }

        if (!VALID_CHARS.matcher(name).matches()) {
            ret.add(REASON_VALID_CHARS);
        }

        if (ret.isEmpty() && UserServiceFactory.getUserService().exists(name)) {
            ret.add(REASON_EXISTS);
        }

        return ret;
    }
}
