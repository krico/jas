(function (angular) {

    angular.module('jasify.authenticate').controller('RecoverPasswordController', RecoverPasswordController);

    function RecoverPasswordController($log, $timeout) {
        var vm = this;
        vm.email = '';
        vm.recover = recover;
        vm.inProgress = false;
        vm.passwordSent = false;
        vm.forgotForm = {};

        function recover() {
            vm.inProgress = true;
            $timeout(function () {
                $log.debug("Timeout");
                vm.inProgress = false;
                vm.passwordSent = true;
            }, 1000);
        }
    }
})(angular);