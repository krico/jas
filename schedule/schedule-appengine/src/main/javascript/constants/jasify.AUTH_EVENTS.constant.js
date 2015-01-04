(function (angular) {

    /**
     * Constant for the authentication related events
     */
    angular.module('jasify').constant('AUTH_EVENTS', {
        loginSuccess: 'auth-login-success',
        loginFailed: 'auth-login-failed',
        logoutSuccess: 'auth-logout-success',
        sessionTimeout: 'auth-session-timeout',
        notAuthenticated: 'auth-not-authenticated',
        notAuthorized: 'auth-not-authorized',
        notGuest: 'auth-not-guest'
    });

})(angular);