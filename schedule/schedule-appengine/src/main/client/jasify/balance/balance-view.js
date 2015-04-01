(function (angular) {

    angular.module('jasify.balance').controller('BalanceViewController', BalanceViewController);

    function BalanceViewController($location, Balance, account) {
        var vm = this;
        vm.alert = alert;
        vm.alerts = [];
        vm.account = account || {balance: 0};
        vm.paymentStatus = paymentStatus;
        vm.getTransactions = getTransactions;
        vm.transactions = [];
        vm.inProgress = false;
        vm.pageChanged = pageChanged;
        vm.pagination = {
            total: 0,
            page: 1,
            itemsPerPage: 10,
            numPages: 1,
            maxSize: 5
        };

        vm.paymentStatus();
        vm.pageChanged();


        function getTransactions() {
            if (vm.inProgress) {
                alert('warning', 'Already in progress');
                return;
            }

            if (!(account && account.id)) return;

            var limit = vm.pagination.itemsPerPage;
            var offset = (vm.pagination.page - 1) * limit;

            vm.inProgress = true;
            vm.transactions = [];
            // http://localhost:8080/#/balance/view?page=2
            return Balance.getTransactions(account.id, limit, offset).then(ok, fail);

            function ok(resp) {
                vm.inProgress = false;
                vm.transactions = resp.transactions;
                vm.pagination.total = resp.total;
            }

            function fail(resp) {
                vm.inProgress = false;
                alert('danger', 'Failed: ' + resp.statusText);
            }
        }

        function pageChanged() {
            vm.getTransactions();
        }

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