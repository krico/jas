(function (angular) {

    angular.module('jasify.authenticate').controller('RecoverPasswordController', RecoverPasswordController);

    function RecoverPasswordController($log, $timeout, $routeParams, $rootScope, AUTH_EVENTS) {
        var vm = this;
        vm.setPassword = setPassword;
        vm.signIn = signIn;
        vm.again = again;
        vm.inProgress = false;
        vm.passwordSet = false;
        vm.failText = false;
        vm.hadCode = !!$routeParams.code;
        vm.code = $routeParams.code || '';
        vm.forgotForm = {};
        vm.passwordStrengthCallback = passwordStrengthCallback;
        vm.popoverText = '';

        function signIn() {
            $rootScope.$broadcast(AUTH_EVENTS.signIn);
        }

        function again() {
            vm.failText = false;
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

        function setPassword() {
            vm.inProgress = true;
            $timeout(function () {
                $log.debug("Timeout");
                vm.inProgress = false;
                vm.failText = 'Invalid code';
            }, 1000);
        }
    }
})(angular);