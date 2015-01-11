package com.jasify.schedule.appengine.spi;

import com.google.api.server.spi.config.*;
import com.google.api.server.spi.response.ConflictException;
import com.google.common.base.Preconditions;
import com.jasify.schedule.appengine.spi.auth.JasifyAuthenticator;
import com.jasify.schedule.appengine.spi.transform.*;
import com.jasify.schedule.appengine.validators.UsernameValidator;
import com.jasify.schedule.appengine.validators.Validator;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * @author krico
 * @since 11/01/15.
 */
@Api(name = "jasify", /* WARN: Its LAME but you have to copy & paste this section to all *Endpoint classes in this package */
        version = "v1",
        defaultVersion = AnnotationBoolean.TRUE,
        description = "Jasify Schedule",
        authenticators = {JasifyAuthenticator.class},
        authLevel = AuthLevel.NONE,
        transformers = {JasUserLoginTransformer.class, JasUserTransformer.class, JasKeyTransformer.class, JasActivityTypeTransformer.class, JasActivityTransformer.class, JasOrganizationTransformer.class, JasGroupTransformer.class},
        auth = @ApiAuth(allowCookieAuth = AnnotationBoolean.TRUE /* todo: I don't know another way :-( */),
        namespace = @ApiNamespace(ownerDomain = "jasify.com",
                ownerName = "Jasify",
                packagePath = ""))
public class UsernameEndpoint {
    private Validator<String> usernameValidator;


    Validator<String> getUsernameValidator() {
        if (usernameValidator == null) {
            usernameValidator = UsernameValidator.INSTANCE;
        }
        return usernameValidator;
    }


    @ApiMethod(name = "username.check", path = "username-check/{username}", httpMethod = ApiMethod.HttpMethod.GET)
    public void checkUsername(@Named("username") String username) throws ConflictException {
        Preconditions.checkNotNull(StringUtils.trimToNull(username));
        List<String> reasons = getUsernameValidator().validate(username);
        if (!reasons.isEmpty()) {
            throw new ConflictException(StringUtils.join(reasons, ", "));
        }
    }
}
