package com.jasify.schedule.appengine.model.history;

/**
 * @author krico
 * @since 09/08/15.
 */
public enum HistoryTypeEnum {
    Login,
    Logout,
    LoginFailed,
    PasswordChanged,
    PasswordForgotten,
    PasswordForgottenFailed,
    PasswordRecovered,
    AccountCreated,
    AccountCreationFailed,
    SubscriptionCreated,
    SubscriptionCreationFailed,
    SubscriptionCancelled,
    SubscriptionCancellationFailed,
    PaymentExecuted,
    PaymentCancelled,
    Message;
}
