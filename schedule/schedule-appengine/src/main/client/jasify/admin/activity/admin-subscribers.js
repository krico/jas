(function (angular) {

    angular.module('jasify.admin').controller('AdminSubscribersController', AdminSubscribersController);

    function AdminSubscribersController($location, $filter, jasDialogs, subscriptions, Activity) {
        var vm = this;

        vm.subscriptions = subscriptions.items;
        vm.displaySubscriptions = [];
        vm.cancel = cancel;
        vm.back = back;

        vm.getSubscriptions = getSubscriptions;

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

        vm.pageChanged();

        function cancel(id) {
            Activity.cancelSubscription(id).then(ok, fail);
            function ok(r) {
                var newS = [];
                angular.forEach(vm.subscriptions, function (value, key) {
                    if (id != value.id) {
                        this.push(value);
                    }
                }, newS);
                vm.subscriptions = newS;
                pageChanged();
            }

            function fail(r) {
                var translation = $translate('FAILED_PLEASE_RETRY');
                jasDialogs.resultError(translation, r);
            }
        }

        function getSubscriptions() {
            var limit = vm.pagination.itemsPerPage;
            var offset = (vm.pagination.page - 1) * limit;

            vm.displaySubscriptions = vm.subscriptions.slice(offset, offset + limit);
            vm.pagination.total = vm.subscriptions.length;
        }

        function pageChanged() {
            vm.getSubscriptions();
        }

        function perPage() {
            return vm.pagination.itemsPerPage;
        }

        function back() {
            $location.path("/admin/activities");
        }
    }

})(angular);