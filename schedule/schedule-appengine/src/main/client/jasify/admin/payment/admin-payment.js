(function (angular) {
    "use strict";

    angular.module('jasify.admin').controller('AdminPaymentController', AdminPaymentController);

    function AdminPaymentController($moment, jasDialogs, aButtonController, $q, $timeout, Payment, payment) {
        var vm = this;
        vm.payment = payment;

        vm.canExecute = isOpenInvoice;
        vm.executePaymentBtn = aButtonController.createPaymentExecute();
        vm.executePayment = executePayment;

        vm.canCancel = isOpenInvoice;
        vm.cancelPaymentBtn = aButtonController.createPaymentCancel();
        vm.cancelPayment = cancelPayment;
        vm.expiryDate = expiryDate;
        vm.sameFee = sameFee;

        function isOpenInvoice() {
            return vm.payment && vm.payment.type && vm.payment.state && vm.payment.type == 'Invoice' && vm.payment.state == 'Created';
        }

        function sameFee(f, rf) {
            if (f || rf) {
                return f == rf;
            }
            return true;
        }

        function expiryDate(date, days) {
            return $moment(date).add(days, 'days');
        }

        function cancelPayment() {
            if (!vm.canCancel()) {
                jasDialogs.error('This Payment cannot be cancelled');
                return;
            }
            var promise = Payment.cancelPayment(vm.payment).then(ok, fail);
            vm.cancelPaymentBtn.start(promise);
            function ok(p) {
                vm.payment = p;
            }

            function fail(r) {
                jasDialogs.error('Failed to Cancel Payment: ' + r.statusText);
            }
        }

        function executePayment() {
            if (!vm.canCancel()) {
                jasDialogs.error('This Payment cannot be executed');
                return;
            }
            var promise = Payment.executePayment(vm.payment).then(ok, fail);
            vm.executePaymentBtn.start(promise);

            function ok(p) {
                vm.payment = p;
            }

            function fail(r) {
                jasDialogs.error('Failed to Execute Payment: ' + r.statusText);
            }
        }
    }

})(angular);