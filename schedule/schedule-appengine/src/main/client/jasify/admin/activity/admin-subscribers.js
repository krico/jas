(function (angular) {

    angular.module('jasifyWeb').controller('AdminSubscribersController', AdminSubscribersController);

    function AdminSubscribersController($location, subscriptions) {
        var vm = this;

        vm.subscriptions = subscriptions.items;
        vm.back = back;

        function back() {
            $location.path("/admin/activities");
        }
    }

})(angular);