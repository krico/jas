(function (angular) {

    angular.module('jasify.bookings').controller('BookingsAgendaController', BookingsAgendaController);

    function BookingsAgendaController(Activity, Session, jasDialogs, $filter, $moment) {
        var vm = this;

        vm.getSubscriptions = getSubscriptions;
        vm.allSubscriptions = [];
        vm.subscriptions = [];
        vm.inProgress = false;
        vm.userId = Session.userId;
        vm.fromDate = $moment().set('hour', 0).set('minute', 0).set('second', 0).format();
        vm.toDate = undefined;

        vm.pageChanged = pageChanged;
        vm.perPage = perPage;
        vm.pagination = {
            total: 0,
            page: 1,
            itemsPerPage: 10,
            numPages: 1,
            maxSize: 5
        };

        var $translate = $filter('translate');

        // Get initial subscriptions for current user
        vm.getSubscriptions();

        function getSubscriptions() {
            if (vm.inProgress) {
                return;
            }

            if (!vm.userId) return;

            vm.inProgress = true;
            vm.allSubscriptions = [];
            vm.subscriptions = [];

            Activity.getUserSubscriptions(vm.userId, vm.fromDate, vm.toDate).then(ok, fail);

            function ok(resp) {
                vm.inProgress = false;
                vm.allSubscriptions = resp.items;
                vm.allSubscriptions.sort(function (a, b) {
                    return a.start - b.start;
                });
                vm.pagination.total = resp.items.length;
                pageChanged();
            }

            function fail(resp) {
                vm.inProgress = false;
                var failedPleaseRetryTranslation = $translate('FAILED_PLEASE_RETRY');
                jasDialogs.resultError(failedPleaseRetryTranslation, resp);
            }
        }

        function pageChanged() {
            var limit = vm.pagination.itemsPerPage;
            var offset = (vm.pagination.page - 1) * limit;

            vm.subscriptions = vm.allSubscriptions.slice(offset, offset + limit);
        }

        function perPage() {
            return vm.pagination.itemsPerPage;
        }
    }
})(angular);