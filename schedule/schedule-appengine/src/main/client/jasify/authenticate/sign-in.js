(function (angular) {

    angular.module('jasifyScheduleControllers').controller('SignInController', SignInController);

    function SignInController($log) {
        var vm = this;
        $log.debug('SignInController()');
    }
})(angular);