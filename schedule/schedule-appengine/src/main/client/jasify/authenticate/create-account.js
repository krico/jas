(function (angular) {

    angular.module('jasify.authenticate').controller('CreateAccountController', CreateAccountController);

    function CreateAccountController($rootScope, User, OAuthWindow) {
        var vm = this;
        vm.user = {};
        vm.authenticateForm = {};
        vm.email = false;
        vm.showErrors = false;
        vm.withEmail = withEmail;
        vm.withOAuth = withOAuth;
        vm.isEmail = isEmail;
        vm.passwordStrengthCallback = passwordStrengthCallback;
        vm.popoverText = '';
        vm.hasSuccess = hasSuccess;
        vm.hasError = hasError;
        vm.create = create;
        vm.getTooltip = getTooltip;
        vm.inProgress = false;
        vm.oauth = oauth;
        vm.alert = function (type, message) {
            //TODO:
            console.log("Alert[" + type + "]:" + message);
        };

        function oauth(provider, cb) {
            OAuthWindow.open('/oauth2/request/' + provider, provider)
                .then(popupSuccess, popupFailed);

            function popupSuccess(oauthDetail) {
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


        function create(cb) {
            if (vm.authenticateForm.$invalid) {
                vm.showErrors = true;
                return;
            }
            vm.inProgress = true;

            vm.user.name = vm.user.email; //username == email
            User.add(vm.user, vm.user.password).then(saveSuccess, saveError);

            function saveSuccess(ret) {
                vm.inProgress = false;
                if (angular.isFunction(cb)) {
                    cb();
                }
            }

            //User.save error
            function saveError(httpResponse) {
                vm.inProgress = false;

                vm.alert('danger', ":-( registration failed, since this was really unexpected, please change some fields and try again.");

            }

        }

        function getTooltip() {
            if (vm.authenticateForm.$invalid) {
                return 'Please fix the errors.';
            } else {
                return '';
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


        function passwordStrengthCallback(s) {
            if (s <= 0) {
                vm.popoverText = 'Choose a password.';
            } else if (s <= 15) {
                vm.popoverText = 'Weak!';
            } else if (s <= 40) {
                vm.popoverText = 'Average...';
            } else if (s <= 80) {
                vm.popoverText = 'Good!';
            } else {
                vm.popoverText = 'Excellent!!!';
            }
        }

        function withOAuth() {
            vm.email = false;
        }

        function withEmail() {
            vm.email = true;
        }

        function isEmail() {
            return vm.email;
        }
    }
})(angular);