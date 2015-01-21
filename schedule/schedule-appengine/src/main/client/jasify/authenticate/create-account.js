(function (angular) {

    angular.module('jasifyScheduleControllers').controller('CreateAccountController', CreateAccountController);

    function CreateAccountController($log) {
        var vm = this;
        vm.user = {};
        vm.authenticateForm = {};
        vm.email = false;
        vm.withEmail = withEmail;
        vm.withOAuth = withOAuth;
        vm.isEmail = isEmail;
        vm.passwordStrengthCallback = passwordStrengthCallback;
        vm.popoverText = '';

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