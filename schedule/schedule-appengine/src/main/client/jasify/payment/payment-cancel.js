(function (angular) {

    angular.module('jasify.payment').controller('PaymentCancelController', PaymentCancelController);

    function PaymentCancelController($location, $routeParams, $timeout, BrowserData, Balance) {
        var vm = this;
        vm.alert = alert;
        vm.alerts = [];
        vm.cancelPayment = cancelPayment;
        vm.again = again;
        vm.complete = false;
        vm.status = '';
        vm.cancelPayment($routeParams.paymentId);
        vm.autoRedirect = BrowserData.getPaymentCancelRedirectAuto();

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
                if (vm.autoRedirect) {
                    $timeout(function () {
                        vm.again();
                    }, 2000);
                }
            }

            function fail(res) {
                vm.complete = true;
                alert('danger', 'Failed: ' + res.statusText);
            }
        }

        function again() {
            $location.replace();
            $location.search({paymentStatus: 'failed'});
            $location.path(BrowserData.getPaymentCancelRedirect());
            BrowserData.clearPaymentAcceptRedirect();
            BrowserData.clearPaymentCancelRedirect();
            BrowserData.clearPaymentCancelRedirectAuto();
        }
    }
})(angular);