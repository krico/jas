(function (angular) {

    angular.module('jasify.authenticate').controller('ForgotPasswordController', ForgotPasswordController);

    function ForgotPasswordController(Auth) {
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
            Auth.forgotPassword(vm.email).then(ok, fail);

            function ok() {
                vm.inProgress = false;
                vm.passwordSent = true;
            }

            function fail() {
                vm.inProgress = false;
                vm.failed = true;
            }
        }

        function again() {
            vm.failed = false;
        }
    }
})(angular);