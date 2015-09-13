(function (angular) {

    angular.module('jasify.payment').controller('PaymentConfirmController', PaymentConfirmController);

    function PaymentConfirmController($log, $location, $routeParams, BrowserData, Payment, jasDialogs) {
        var vm = this;
        vm.progress = false;
        vm.success = false;
        vm.invoice = null;
        vm.next = next;
        vm.getInvoice = getInvoice;
        vm.getInvoice($routeParams.paymentId);

        function next() {
            $location.replace();
            $location.search({paymentStatus: 'success'});
            $location.path(BrowserData.getPaymentAcceptRedirect());
            BrowserData.clearPaymentAcceptRedirect();
            BrowserData.clearPaymentCancelRedirect();
        }

        function getInvoice(paymentId) {
            vm.progress = true;

            return Payment.getPaymentInvoice(paymentId)
                .then(ok, fail);

            function ok(invoice) {
                vm.progress = false;
                vm.invoice = invoice;
            }

            function fail(reason) {
                vm.progress = false;
                vm.success = true;
                jasDialogs.error('Failed to get invoice from server');
            }
        }

    }
})(angular);