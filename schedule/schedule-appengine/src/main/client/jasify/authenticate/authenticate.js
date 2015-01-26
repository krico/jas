(function (angular) {

    angular.module('jasify.authenticate').controller('AuthenticateController', AuthenticateController);

    function AuthenticateController($scope) {
        var vm = this;
        vm.signIn = !!$scope.signIn; // get this from constructor
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