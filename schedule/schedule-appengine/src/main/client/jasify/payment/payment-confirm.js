(function (angular) {

    angular.module('jasify.payment').controller('PaymentConfirmController', PaymentConfirmController);

    function PaymentConfirmController($log, $location, $routeParams, BrowserData, Balance, jasDialogs) {
        var vm = this;
        vm.progress = false;
        vm.invoice = null;
        vm.getInvoice = getInvoice;
        vm.getInvoice($routeParams.paymentId);

        function getInvoice(paymentId) {
            vm.progress = true;

            return Balance.getPaymentInvoice(paymentId)
                .then(ok, fail);

            function ok(invoice) {
                vm.progress = false;
                vm.invoice = invoice;
            }

            function fail(reason) {
                vm.progress = false;
                jasDialogs.error('Failed to get invoice from server');
            }
        }

    }
})(angular);