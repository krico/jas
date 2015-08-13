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
    AccountCreated,
    AccountCreationFailed,
    Message;
}
