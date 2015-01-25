(function (angular) {

    angular.module('jasifyWeb').controller('HomeController', HomeController);

    function HomeController($rootScope, AUTH_EVENTS) {
        var vm = this;
        vm.signIn = signIn;
        vm.createAccount = createAccount;
        vm.home = true;

        function createAccount() {
            $rootScope.$broadcast(AUTH_EVENTS.createAccount);
        }

        function signIn() {
            $rootScope.$broadcast(AUTH_EVENTS.signIn);
        }
    }
})(angular);