(function (angular) {

    angular.module('jasifyScheduleControllers').controller('CreateAccountController', CreateAccountController);

    function CreateAccountController($log) {
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

        function create($event) {
            if (vm.authenticateForm.$invalid) {
                vm.showErrors = true;
                return;
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