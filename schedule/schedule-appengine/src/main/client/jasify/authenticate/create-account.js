(function (angular) {

    angular.module('jasifyScheduleControllers').controller('CreateAccountController', CreateAccountController);

    function CreateAccountController($log) {
        var vm = this;
        $log.debug('CreateAccountController()');
    }
})(angular);