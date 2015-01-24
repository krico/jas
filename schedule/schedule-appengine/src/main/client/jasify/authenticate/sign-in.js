(function (angular) {

    angular.module('jasifyScheduleControllers').controller('SignInController', SignInController);

    function SignInController($log, $cookies, $rootScope, AUTH_EVENTS, Auth, OAuthWindow) {
        var vm = this;
        vm.user = {};
        vm.email = false;
        vm.rememberMe = !!$cookies.rememberMe;
        vm.inProgress = false;
        vm.showErrors = false;
        vm.withEmail = withEmail;
        vm.withOAuth = withOAuth;
        vm.isEmail = isEmail;
        vm.signIn = signIn;
        vm.hasSuccess = hasSuccess;
        vm.hasError = hasError;
        vm.getTooltip = getTooltip;
        vm.oauth = oauth;

        if (vm.rememberMe) {
            $log.debug('REMEMBER');
            vm.user.email = $cookies.rememberMe;
        }

        function oauth(provider, cb) {
            OAuthWindow.open('/oauth2/request/' + provider, provider)
                .then(popupSuccess, popupFailed);

            function popupSuccess(oauthDetail) {
                $log.debug('OK: ' + angular.toJson(oauthDetail));
                if (oauthDetail.loggedIn) {
                    if (angular.isFunction(cb)) {
                        cb();
                    }
                }
            }

            function popupFailed(msg) {
                $rootScope.$broadcast(AUTH_EVENTS.loginFailed);
            }
        }

        function getTooltip() {
            if (vm.authenticateForm.$invalid) {
                return 'Please fill in all fields.';
            } else {
                return '';
            }
        }


        function signIn(cb) {
            if (vm.authenticateForm.$invalid) {
                $log.debug('signIn invalid!');
                vm.showErrors = true;
                return;
            }
            $log.debug('signIn OK!');
            vm.inProgress = true;

            var cred = {
                name: vm.user.email,
                email: vm.user.email,
                password: vm.user.password
            };

            Auth.login(cred).then(ok, fail);


            function ok(session) {

                if (vm.rememberMe) {
                    $cookies.rememberMe = cred.name;
                } else {
                    delete $cookies.rememberMe;
                }

                vm.inProgress = false;
                if (angular.isFunction(cb)) {
                    cb();
                }
            }

            function fail() {
                $rootScope.$broadcast(AUTH_EVENTS.loginFailed);
            }

        }

        function hasSuccess(fieldName) {
            if (vm.authenticateForm[fieldName]) {
                var f = vm.authenticateForm[fieldName];
                return f && (f.$dirty || vm.showErrors) && f.$valid;
            } else {
                return false;
            }
        }

        function hasError(fieldName) {
            if (vm.authenticateForm[fieldName]) {
                var f = vm.authenticateForm[fieldName];
                return f && (f.$dirty || vm.showErrors) && f.$invalid;
            } else {
                return false;
            }
        }

        function withEmail() {
            vm.email = true;
        }

        function withOAuth() {
            vm.email = false;
        }

        function isEmail() {
            return vm.email;
        }
    }
})(angular);