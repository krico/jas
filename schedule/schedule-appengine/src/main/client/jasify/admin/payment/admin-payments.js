(function (angular) {
    "use strict";

    angular.module('jasify.admin').controller('AdminPaymentsController', AdminPaymentsController);

    function AdminPaymentsController($log, $scope, $moment, jasDialogs, Payment, payments) {
        var vm = this;
        vm.allPayments = payments.items;
        vm.payments = [];
        vm.paymentTypes = ['PayPal', 'Cash', 'Invoice'];
        vm.paymentType = false;
        vm.selectPaymentType = selectPaymentType;
        vm.getPayment = getPayment;
        vm.queryChanged = queryChanged;
        vm.init = init;
        vm.datesChanged = datesChanged;

        // Client side pagination. Need this to simulate server side filtering
        vm.queryPayments = [];
        vm.displayPayments = [];
        vm.pageChanged = pageChanged;

        vm.paymentState = false;
        vm.paymentStates = ['New', 'Created', 'Completed', 'Canceled'];
        vm.selectPaymentState = selectPaymentState;
        vm.timeWindow = {};
        vm.perPage = perPage;
        vm.pagination = {
            total: 0,
            page: 1,
            itemsPerPage: 10,
            numPages: 1,
            maxSize: 5
        };

        vm.init();

        function init() {

            if (vm.allPayments) {
                vm.allPayments.reverse();
            }
            vm.payments = vm.allPayments;
            vm.queryChanged();
        }

        function reQuery() {
            vm.allPayments = [];
            vm.payments = vm.allPayments;
            vm.queryChanged();
            var s = vm.paymentState ? vm.paymentState : null;
            Payment.query(vm.queryFromDate, vm.queryToDate, s).then(ok, fail);
            function ok(response) {
                vm.allPayments = response.items;
                vm.allPayments.reverse();
                vm.payments = vm.allPayments;
                vm.queryChanged();
            }

            function fail(response) {
                jasDialogs.error("" + response.statusText);
            }
        }

        function selectPaymentState(paymentState) {
            vm.paymentState = paymentState;
            reQuery();
        }

        function datesChanged() {
            if (vm.fromDate) {
                vm.queryFromDate = $moment(vm.fromDate).format();
            } else {
                vm.queryFromDate = null;
            }
            if (vm.toDate) {
                vm.queryToDate = $moment(vm.toDate).format();
            } else {
                vm.queryToDate = null;
            }
            reQuery();
        }

        function selectPaymentType(t) {
            vm.paymentType = t;
            var newList;
            if (t) {
                newList = [];
                angular.forEach(vm.allPayments, function (value) {
                    if (value.type == vm.paymentType) {
                        this.push(value);
                    }
                }, newList);
            } else {
                newList = vm.allPayments;
            }
            vm.payments = newList;
            vm.queryChanged();
        }

        function queryChanged() {
            if (vm.query) {
                vm.queryPayments = vm.payments.filter(function (el) {
                    return el.description.indexOf(vm.query) != -1;
                });
            } else {
                vm.queryPayments = vm.payments;
            }
            vm.pagination.total = vm.queryPayments.length;
            getPayment();
        }

        function getPayment() {
            var limit = vm.pagination.itemsPerPage;
            var offset = (vm.pagination.page - 1) * limit;

            vm.displayPayments = vm.queryPayments.slice(offset, offset + limit);
            vm.pagination.total = vm.queryPayments.length;
        }

        function pageChanged() {
            vm.getPayment();
        }

        function perPage() {
            return vm.pagination.itemsPerPage;
        }
    }

})(angular);