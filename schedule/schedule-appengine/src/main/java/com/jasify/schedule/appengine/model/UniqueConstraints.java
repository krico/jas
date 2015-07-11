package com.jasify.schedule.appengine.model;

import com.jasify.schedule.appengine.meta.common.OrganizationMeta;
import com.jasify.schedule.appengine.meta.users.UserLoginMeta;
import com.jasify.schedule.appengine.meta.users.UserMeta;

/**
 * This class needs to hold all unique constraints
 *
 * @author krico
 * @since 09/07/15.
 */
public final class UniqueConstraints {
    private UniqueConstraints() {
    }

    public static void ensureAllConstraintsExist() {
        createOrganizationNameConstraint();
        createUserNameConstraint();
        createUserEmailConstraint();
        createUserLoginUserIdProviderConstraint();
    }

    private static void createUserLoginUserIdProviderConstraint() {
        new UniqueConstraintBuilder()
                .forMeta(UserLoginMeta.get())
                .withUniquePropertyName(UserLoginMeta.get().userId)
                .withUniqueClassifierPropertyName(UserLoginMeta.get().provider)
                .createIfMissing(true)
                .createNoEx();
    }

    private static void createUserNameConstraint() {
        new UniqueConstraintBuilder()
                .forMeta(UserMeta.get())
                .withUniquePropertyName(UserMeta.get().name)
                .createIfMissing(true)
                .createNoEx();
    }

    private static void createUserEmailConstraint() {
        new UniqueConstraintBuilder()
                .forMeta(UserMeta.get())
                .withUniquePropertyName(UserMeta.get().email)
                .ignoreNullValues(true)
                .createIfMissing(true)
                .createNoEx();
    }

    private static void createOrganizationNameConstraint() {
        new UniqueConstraintBuilder()
                .forMeta(OrganizationMeta.get())
                .withUniquePropertyName(OrganizationMeta.get().name)
                .createIfMissing(true)
                .createNoEx();
    }
}
