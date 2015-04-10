(function (angular) {

    angular.module('jasify.admin').controller('AdminBalancesController', BalanceViewController);

    function BalanceViewController($log, $location, Balance, $routeParams, accounts) {
        var vm = this;
        vm.alert = alert;
        vm.alerts = [];
        vm.accounts = accounts;
        vm.account = {};
        vm.getTransactions = getTransactions;
        vm.transactions = [];
        vm.inProgress = false;
        vm.pageChanged = pageChanged;
        vm.accountChanged = accountChanged;
        vm.pagination = {
            total: 0,
            page: 1,
            itemsPerPage: 10,
            numPages: 1,
            maxSize: 5
        };
        vm.selectedId = '';

        function accountChanged() {
            if (vm.account.id && vm.selectedId != vm.account.id) {
                vm.selectedId = vm.account.id;
                $log.debug("Fetched: " + vm.account.id);
                vm.pagination.total = 0;
                vm.pagination.page = 1;
                vm.pageChanged();
            }
        }

        function getTransactions() {
            if (vm.inProgress) {
                alert('warning', 'Already in progress');
                return;
            }

            if (!(vm.account && vm.account.id)) return;

            var limit = vm.pagination.itemsPerPage;
            var offset = (vm.pagination.page - 1) * limit;

            vm.inProgress = true;
            vm.transactions = [];
            // http://localhost:8080/#/balance/view?page=2
            return Balance.getTransactions(vm.account.id, limit, offset).then(ok, fail);

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

        function alert(t, m) {
            vm.alerts.push({type: t, msg: m});
        }
    }
})(angular);