(function (angular) {

    angular.module('jasifyWeb').controller('PaymentMakeController', PaymentMakeController);

    function PaymentMakeController($filter, $routeParams, $window, Balance) {
        var vm = this;
        vm.alert = alert;
        vm.createPayment = createPayment;
        vm.inProgress = false;
        vm.redirecting = false;
        vm.paymentForm = {};
        vm.alerts = [];
        vm.payment = {
            currency: 'CHF',
            type: 'PayPal',
            amount: formatNumber($routeParams.amount)
        };

        function createPayment() {

            if (vm.paymentForm.$invalid || !vm.payment.amount) {
                vm.alert('info', 'Invalid amount, needs to be formatted as, for example, "50" or "10.50"');
                return;
            }

            if (parseFloat(vm.payment.amount) === 0) {
                vm.alert('info', 'Amount cannot be zero.');
                return;
            }

            vm.inProgress = true;
            Balance.createPayment(vm.payment).then(ok, fail);

            function ok(resp) {
                vm.redirecting = true;
                $window.location.href = resp.approveUrl;
            }

            function fail(res) {
                vm.inProgress = false;
                alert('danger', 'Failed: ' + res.statusText);
            }
        }

        function formatNumber(val) {
            if (!val) {
                return;
            }
            return $filter('number')(parseFloat(val), 2);
        }

        function alert(t, m) {
            vm.alerts.push({type: t, msg: m});
        }
    }

})(angular);