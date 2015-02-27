(function (angular) {

    angular.module('jasifyWeb').controller('PaymentAcceptController', PaymentAcceptController);

    function PaymentAcceptController($routeParams, Balance) {
        var vm = this;
        vm.alert = alert;
        vm.alerts = [];

        function alert(t, m) {
            vm.alerts.push({type: t, msg: m});
        }
    }
})(angular);