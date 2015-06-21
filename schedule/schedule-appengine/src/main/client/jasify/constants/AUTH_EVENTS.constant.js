/*global window */
(function (angular) {

    'use strict';

    /**
     * Constant for the authentication related events
     */
    angular.module('jasifyComponents').constant('AUTH_EVENTS', {
        loginSuccess: 'auth-login-success',
        loginFailed: 'auth-login-failed',
        logoutSuccess: 'auth-logout-success',
        sessionTimeout: 'auth-session-timeout',
        signIn: 'auth-sign-in',
        createAccount: 'auth-create-account',
        accountCreated: 'auth-account-created',
        notAuthenticated: 'auth-not-authenticated',
        notAuthorized: 'auth-not-authorized',
        notGuest: 'auth-not-guest'
    }).run(function ($rootScope, $log, AUTH_EVENTS) {
        for (var eventName in AUTH_EVENTS) {
            $rootScope.$on(AUTH_EVENTS[eventName], function logger(event) {
                $log.debug("Jasify broadcast", event);
            });
        }
    });

})(window.angular);