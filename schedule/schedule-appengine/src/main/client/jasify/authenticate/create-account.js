(function (angular) {

    angular.module('jasify.authenticate').controller('CreateAccountController', CreateAccountController);

    function CreateAccountController($rootScope, $window, Auth, User) {

        var vm = this;
        vm.user = {};
        vm.authenticateForm = {};
        vm.email = false;
        vm.passwordStrengthCallback = passwordStrengthCallback;
        vm.create = create;
        vm.oauth = oauth;

        function oauth(provider, cb) {
            Auth.providerAuthorize(provider).then(function (resp) {
                $window.location.href = resp.result.authorizeUrl;
            }, function () {
                $rootScope.$broadcast(AUTH_EVENTS.loginFailed);
            });
        }

        function create(cb) {
            vm.user.name = vm.user.email;
            User.add(vm.user, vm.user.password).then(saveSuccess, saveError);

            function saveSuccess(ret) {
                if (angular.isFunction(cb)) {
                    cb();
                }
            }

            function saveError(httpResponse) {
                // TODO: create http interceptor to handler errors
            }
        }

        function passwordStrengthCallback(s) {
            if (s <= 0) {
                vm.passwordStrength = null;
                vm.passwordStrengthText = null;
            } else if (s <= 15) {
                vm.passwordStrength = 'weak';
                vm.passwordStrengthText = 'Weak!';
            } else if (s <= 40) {
                vm.passwordStrength = 'average';
                vm.passwordStrengthText = 'Average...';
            } else if (s <= 80) {
                vm.passwordStrength = 'good';
                vm.passwordStrengthText = 'Good!';
            } else {
                vm.passwordStrength = 'good';
                vm.passwordStrengthText = 'Excellent!!!';
            }
        }
    }
})(angular);