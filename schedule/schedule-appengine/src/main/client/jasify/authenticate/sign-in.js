(function (angular) {

    angular.module('jasifyScheduleControllers').controller('SignInController', SignInController);

    function SignInController($log) {
        var vm = this;
        vm.user = {};
        vm.email = false;
        vm.withEmail = withEmail;
        vm.withOAuth = withOAuth;
        vm.isEmail = isEmail;

        function withEmail() {
            vm.email = true;
        }

        function withOAuth() {
            vm.email = false;
        }

        function isEmail() {
            return vm.email;
        }
    }
})(angular);