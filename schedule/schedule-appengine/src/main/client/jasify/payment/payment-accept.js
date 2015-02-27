(function (angular) {

    angular.module('jasifyWeb').controller('PaymentAcceptController', PaymentAcceptController);

    function PaymentAcceptController($location, $routeParams, Balance) {
        var vm = this;
        vm.alert = alert;
        vm.alerts = [];
        vm.executePayment = executePayment;
        vm.complete = false;
        vm.success = false;
        vm.failed = false;
        vm.statusClass = statusClass;
        vm.status = '';
        vm.executePayment($routeParams.paymentId);

        function alert(t, m) {
            vm.alerts.push({type: t, msg: m});
        }

        function statusClass() {
            if (vm.success) return 'text-success';
            if (vm.failed) return 'text-danger';
        }

        function executePayment(paymentId) {
            if (!paymentId) {
                vm.alert('danger', 'Invalid paymentId');
                return;
            }
            vm.status = 'Confirming payment...';
            var payerId = $location.search().PayerID;

            Balance.executePayment({id: paymentId, payerId: payerId}).then(ok, fail);

            function ok(resp) {
                vm.complete = true;
                vm.success = true;
                vm.status = 'Payment confirmed!';
            }

            function fail(res) {
                vm.complete = true;
                vm.failed = true;
                vm.status = 'Unable to confirm payment...';
                alert('danger', 'Failed: ' + res.statusText);
            }
        }
    }
})(angular);