(function (angular) {

    angular.module('jasifyScheduleControllers').controller('HomeController', HomeController);

    function HomeController() {
        var vm = this;
        vm.home = true;
    }
})(angular);