(function (angular) {
    "use strict";

    angular.module('jasify.admin').controller('AdminPaymentController', AdminPaymentController);

    function AdminPaymentController($moment, aButtonController, $q, $timeout, Payment, payment) {
        var vm = this;
        vm.payment = payment;

        vm.canExecute = false;
        vm.executePaymentBtn = aButtonController.createPaymentExecute();
        vm.executePayment = executePayment;

        vm.canCancel = false;
        vm.cancelPaymentBtn = aButtonController.createPaymentCancel();
        vm.cancelPayment = cancelPayment;
        vm.expiryDate = expiryDate;
        vm.sameFee = sameFee;

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
            var deferred = /* TODO: real call */ $q.defer();
            $timeout(function () {
                deferred.resolve();
            }, 1500);

            var promise = deferred.promise;


            vm.cancelPaymentBtn.start(promise);

        }

        function executePayment() {
            var deferred = /* TODO: real call */ $q.defer();
            $timeout(function () {
                deferred.resolve();
            }, 1500);

            var promise = deferred.promise;


            vm.executePaymentBtn.start(promise);
        }
    }

})(angular);