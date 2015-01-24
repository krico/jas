package com.jasify.schedule.appengine.validators;

import com.jasify.schedule.appengine.model.users.UserServiceFactory;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @author krico
 * @since 10/11/14.
 */
public class EmailValidator implements Validator<String> {
    public static final EmailValidator INSTANCE = new EmailValidator();

    public static final String REASON_INVALID = "Invalid e-mail.";
    public static final String REASON_EXISTS = "Username already exists.";

    private static final org.apache.commons.validator.routines.EmailValidator COMMONS_VALIDATOR = org.apache.commons.validator.routines.EmailValidator.getInstance();

    @Nonnull
    @Override
    public List<String> validate(String value) {
        ArrayList<String> ret = new ArrayList<>();
        String email = StringUtils.trimToEmpty(value);
        email = StringUtils.lowerCase(email);

        if(!COMMONS_VALIDATOR.isValid(email)){
            ret.add(REASON_INVALID);
        }

        if (ret.isEmpty() && UserServiceFactory.getUserService().emailExists(email)) {
            ret.add(REASON_EXISTS);
        }

        return ret;
    }
}
