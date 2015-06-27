(function (angular) {

    angular.module('jasify.payment').controller('PaymentAcceptController', PaymentAcceptController);

    function PaymentAcceptController($log, $location, $routeParams, BrowserData, Balance) {
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
            vm.status = 'Processing payment...';
            var payerId = $location.search().PayerID;

            Balance.executePayment({id: paymentId, payerId: payerId}).then(ok, fail);

            function ok(resp) {
                vm.complete = true;
                vm.success = true;
                vm.status = 'Payment processed! You will be redirected...';
                $location.replace();
                $location.search({paymentStatus: 'success'});
                $log.debug('Redirecting to BrowserData.getPaymentAcceptRedirect()=' + BrowserData.getPaymentAcceptRedirect());
                $location.path(BrowserData.getPaymentAcceptRedirect());
                BrowserData.clearPaymentAcceptRedirect();
                BrowserData.clearPaymentCancelRedirect();
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