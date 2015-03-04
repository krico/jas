(function (angular) {

    angular.module('jasifyWeb').controller('BalanceViewController', BalanceViewController);

    function BalanceViewController($location, account) {
        var vm = this;
        vm.alert = alert;
        vm.alerts = [];
        vm.account = account;
        vm.paymentStatus = paymentStatus;

        vm.paymentStatus();

        function paymentStatus() {
            //TODO: temporary hack, can be removed whenever you see it
            if (!vm.account.currency) vm.account.currency = 'CHF';

            //If we are the callback from payment-make
            var ps = $location.search().paymentStatus;
            if (ps && ps == 'success') {
                vm.alert('success', 'Payment successfully processed!');
            }
        }

        function alert(t, m) {
            vm.alerts.push({type: t, msg: m});
        }
    }
})(angular);