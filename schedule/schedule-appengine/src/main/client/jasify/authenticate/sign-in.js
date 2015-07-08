(function (angular) {

    'use strict';

    angular.module('jasify.authenticate').controller('SignInController', SignInController);

    function SignInController($rootScope, $window, $location, AUTH_EVENTS, Auth, BrowserData) {
        var vm = this;

        vm.user = {};
        vm.email = false;
        vm.rememberMe = !!BrowserData.getRememberUser();
        vm.signIn = signIn;
        vm.oauth = oauth;
        vm.forgot = forgot;

        if (vm.rememberMe) {
            vm.user.email = BrowserData.getRememberUser();
        }

        function forgot(fn) {
            BrowserData.setForgotPasswordOrigin($location.path());
            $location.path('/forgot-password');
            if (angular.isFunction(fn)) {
                fn();
            }
        }
        function oauth(provider, cb) {
            Auth.providerAuthorize(provider).then(function (resp) {
                $window.location.href = resp.result.authorizeUrl;
            }, function fail() {
                $rootScope.$broadcast(AUTH_EVENTS.loginFailed);
            });
        }

        function signIn(onSuccess) {

            var cred = {
                name: vm.user.email,
                email: vm.user.email,
                password: vm.user.password
            };

            Auth.login(cred).then(function (session) {
                if (vm.rememberMe) {
                    BrowserData.setRememberUser(cred.name);
                } else {
                    BrowserData.clearRememberUser();
                }
                $rootScope.$broadcast(AUTH_EVENTS.loginSuccess);
                if (angular.isFunction(onSuccess)) {
                    onSuccess();
                }
            }, function fail() {
                $rootScope.$broadcast(AUTH_EVENTS.loginFailed);
            });
        }
    }
})(window.angular);