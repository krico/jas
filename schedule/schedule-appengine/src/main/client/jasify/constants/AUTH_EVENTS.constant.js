(function (angular) {

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
        notAuthenticated: 'auth-not-authenticated',
        notAuthorized: 'auth-not-authorized',
        notGuest: 'auth-not-guest'
    });

})(angular);