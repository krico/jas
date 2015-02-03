(function (angular) {

    angular.module('jasify.authenticate').controller('ForgotPasswordController', ForgotPasswordController);

    function ForgotPasswordController($log, $timeout) {
        var vm = this;
        vm.email = '';
        vm.recover = recover;
        vm.again = again;
        vm.inProgress = false;
        vm.passwordSent = false;
        vm.failed = false;
        vm.forgotForm = {};

        function recover() {
            vm.inProgress = true;
            $timeout(function () {
                vm.inProgress = false;
                vm.passwordSent = false;
                vm.failed = true;
            }, 1000);
        }

        function again() {
            vm.failed = false;
        }
    }
})(angular);