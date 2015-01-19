(function (angular) {

    angular.module('jasifyScheduleControllers').controller('CreateAccountController', CreateAccountController);

    function CreateAccountController($log) {
        var vm = this;
        vm.user = {};
        vm.email = false;
        vm.withEmail = withEmail;
        vm.withOAuth = withOAuth;
        vm.isEmail = isEmail;

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