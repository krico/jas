(function (angular) {

    angular.module('jasifyScheduleControllers').controller('SignUpController', SignUpController);

    function SignUpController($scope, $rootScope, AUTH_EVENTS, User, Auth, Popup) {
        var vm = this;

        vm.alerts = [];

        vm.inProgress = false;
        vm.registered = false;
        vm.provider = null;
        vm.user = {};
        vm.alert = alert;
        vm.hasError = hasError;
        vm.hasSuccess = hasSuccess;
        vm.createUser = createUser;
        vm.oauth = oauth;

        function alert(t, m) {
            vm.alerts.push({type: t, msg: m});
        }

        function hasError(fieldName) {
            if (vm.signUpForm[fieldName]) {
                var f = vm.signUpForm[fieldName];
                return f && f.$dirty && f.$invalid;
            } else {
                return false;
            }
        }

        function hasSuccess(fieldName) {
            if (vm.signUpForm[fieldName]) {
                var f = vm.signUpForm[fieldName];
                return f && f.$dirty && f.$valid;
            } else {
                return false;
            }
        }

        function createUser() {
            vm.inProgress = true;

            User.save(vm.user, saveSuccess, saveError);

            function saveSuccess(value, responseHeaders) {
                vm.registered = true;
                vm.inProgress = false;

                vm.alert('success', 'Registration succeeded! You should be redirected shortly...');

                Auth.restore(true).then(restoreSuccess, restoreError);

                function restoreSuccess(u) {
                    $scope.setCurrentUser(u);
                    $rootScope.$broadcast(AUTH_EVENTS.loginSuccess);
                }

                function restoreError(msg) {
                    vm.alert('danger', '! Something went really wrong...');
                }

            }

            //User.save error
            function saveError(httpResponse) {
                vm.inProgress = false;

                vm.alert('danger', ":-( registration failed, since this was really unexpected, please change some fields and try again.");

            }
        }

        function oauth(provider) {
            vm.inProgress = true;
            vm.provider = provider;
            Popup.open('/oauth2/request/' + provider, provider)
                .then(popupSuccess, popupError);

            function popupSuccess(oauthDetail) {
                vm.inProgress = false;
                if (oauthDetail.loggedIn) {
                    vm.alert('info', 'Authenticated! This user is already registered, will log you in');

                    Auth.restore(true).then(restoreSuccess, restoreError);

                    return;
                }
                function restoreSuccess(u) {
                    $scope.setCurrentUser(u);
                    $rootScope.$broadcast(AUTH_EVENTS.loginSuccess);
                }

                function restoreError(msg) {
                    vm.alert('danger', '! Something went really wrong...');
                }


                if (oauthDetail) {
                    vm.user.realName = oauthDetail.realName;
                    vm.user.email = oauthDetail.email;
                }
                vm.alert('info', 'Authenticated! You just need to finish registering your user by selecting a Display Name');
                try {
                    vm.signUpForm.username.focus();
                } catch (e) {
                }
            }

            function popupError(msg) {
                vm.inProgress = false;
                vm.provider = null;
                vm.alert('danger', '! ' + msg);
            }
        }

    }

})(angular);