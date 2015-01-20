(function (angular) {

    angular.module('jasifyScheduleControllers').controller('CreateAccountController', CreateAccountController);

    function CreateAccountController($log) {
        var vm = this;
        vm.user = {};
        vm.authenticateForm = {};
        vm.email = true;
        vm.withEmail = withEmail;
        vm.withOAuth = withOAuth;
        vm.isEmail = isEmail;
        vm.passwordStrengthCallback = passwordStrengthCallback;
        vm.popoverText = '';

        function passwordStrengthCallback(s) {
            if (s <= 0) {
                vm.popoverText = 'Type your password';
            } else if (s <= 15) {
                vm.popoverText = 'Weak!';
            } else if (s <= 40) {
                vm.popoverText = 'Average...';
            } else {
                vm.popoverText = 'Good!';
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