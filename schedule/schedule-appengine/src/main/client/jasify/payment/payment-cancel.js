(function (angular) {

    angular.module('jasify.payment').controller('PaymentCancelController', PaymentCancelController);

    function PaymentCancelController($location, $routeParams, Balance) {
        var vm = this;
        vm.alert = alert;
        vm.alerts = [];
        vm.cancelPayment = cancelPayment;
        vm.makePayment = makePayment;
        vm.complete = false;
        vm.status = '';
        vm.cancelPayment($routeParams.paymentId);

        function alert(t, m) {
            vm.alerts.push({type: t, msg: m});
        }

        function cancelPayment(paymentId) {
            if (!paymentId) {
                vm.alert('danger', 'Invalid paymentId');
                return;
            }
            vm.status = 'Canceling payment...';
            Balance.cancelPayment(paymentId).then(ok, fail);

            function ok(resp) {
                vm.complete = true;
                vm.status = 'Payment canceled!';
            }

            function fail(res) {
                vm.complete = true;
                alert('danger', 'Failed: ' + res.statusText);
            }
        }

        function makePayment() {
            $location.path('/payment/make');
        }
    }
})(angular);