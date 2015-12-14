package com.jasify.schedule.appengine.spi;

import com.google.api.server.spi.config.*;
import com.google.api.server.spi.response.ConflictException;
import com.google.common.base.Preconditions;
import com.jasify.schedule.appengine.spi.auth.JasifyAuthenticator;
import com.jasify.schedule.appengine.spi.transform.*;
import com.jasify.schedule.appengine.validators.EmailValidator;
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
        transformers = {
                /* one per line in alphabetical order to avoid merge conflicts */
                JasAccountTransformer.class,
                JasActivityPackageTransformer.class,
                JasActivityTransformer.class,
                JasActivityTypeTransformer.class,
                JasContactMessageTransformer.class,
                JasGroupTransformer.class,
                JasHistoryTransformer.class,
                JasKeyTransformer.class,
                JasOrganizationTransformer.class,
                JasPaymentTransformer.class,
                JasRepeatDetailsTransformer.class,
                JasSubscriptionTransformer.class,
                JasTransactionTransformer.class,
                JasUserLoginTransformer.class,
                JasUserTransformer.class
        },
        auth = @ApiAuth(allowCookieAuth = AnnotationBoolean.TRUE /* todo: I don't know another way :-( */),
        namespace = @ApiNamespace(ownerDomain = "jasify.com",
                ownerName = "Jasify",
                packagePath = ""))
public class UniqueEndpoint {
    private UsernameValidator usernameValidator;
    private EmailValidator emailValidator;


    Validator<String> getUsernameValidator() {
        if (usernameValidator == null) {
            usernameValidator = UsernameValidator.INSTANCE;
        }
        return usernameValidator;
    }

    Validator<String> getEmailValidator() {
        if (emailValidator == null) {
            emailValidator = EmailValidator.INSTANCE;
        }
        return emailValidator;
    }


    @ApiMethod(name = "unique.username", path = "unique/username/{username}", httpMethod = ApiMethod.HttpMethod.GET)
    public void checkUsername(@Named("username") String username) throws ConflictException {
        Preconditions.checkNotNull(StringUtils.trimToNull(username));
        List<String> reasons = getUsernameValidator().validate(username);
        if (!reasons.isEmpty()) {
            throw new ConflictException(StringUtils.join(reasons, ", "));
        }
    }

    @ApiMethod(name = "unique.email", path = "unique/email/{email}", httpMethod = ApiMethod.HttpMethod.GET)
    public void checkEmail(@Named("email") String email) throws ConflictException {
        Preconditions.checkNotNull(StringUtils.trimToNull(email));
        List<String> reasons = getEmailValidator().validate(email);
        if (!reasons.isEmpty()) {
            throw new ConflictException(StringUtils.join(reasons, ", "));
        }
    }
}
