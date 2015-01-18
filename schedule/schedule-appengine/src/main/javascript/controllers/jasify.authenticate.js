(function (angular) {

    angular.module('jasifyScheduleControllers').controller('AuthenticateController', AuthenticateController);

    function AuthenticateController() {
        var vm = this;
        vm.signIn = false; // this is the default
        vm.isSignIn = isSignIn;
        vm.isCreateAccount = isCreateAccount;
        vm.switchToCreateAccount = switchToCreateAccount;
        vm.switchToSignIn = switchToSignIn;

        function switchToSignIn() {
            vm.signIn = true;
        }

        function switchToCreateAccount() {
            vm.signIn = false;
        }

        function isSignIn() {
            return vm.signIn;
        }

        function isCreateAccount() {
            return !vm.isSignIn();
        }
    }
})(angular);