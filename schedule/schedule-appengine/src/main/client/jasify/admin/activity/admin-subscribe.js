(function (angular) {

    angular.module('jasify.admin').controller('AdminSubscribeController', AdminSubscribeController);

    function AdminSubscribeController(jasPagerSettings, $location, $filter, User, Activity, activity, subscriptions) {
        var vm = this;

        vm.sort = 'DESC';
        vm.pagerSizes = jasPagerSettings.pages;
        vm._searchBy = 'name';
        vm.users = [];
        vm.query = '';
        vm.regex = false;

        vm.activity = activity;
        vm.subscriptions = subscriptions.items;
        vm.subscribe = subscribe;
        vm.isSubscribed = isSubscribed;
        vm.back = back;
        vm.subscribedUsers = [];

        vm.searchBy = searchBy;
        vm.pageChanged = pageChanged;
        vm.searchByChanged = searchByChanged;
        vm.queryChanged = queryChanged;
        vm.perPage = perPage;

        vm.pagination = {
            total: 0,
            page: 1,
            itemsPerPage: 10,
            numPages: 1,
            maxSize: 5
        };

        vm.init = init;

        var $translate = $filter('translate');

        vm.init();

        function init() {
            if (vm.subscriptions !== null) {
                for (var i = 0; i < vm.subscriptions.length; i++) {
                    vm.subscribedUsers[vm.subscriptions[i].user.id] = true;
                }
            }
            vm.pageChanged();
        }


        function pageChanged() {
            var q;

            if (vm.regex) {
                q = vm.query;
            } else {
                q = RegExp.quote(vm.query);
            }

            User.query({
                offset: vm.pagination.itemsPerPage * (vm.pagination.page - 1),
                limit: vm.pagination.itemsPerPage,
                sort: vm.sort,
                field: vm.searchBy(),
                query: q
            }).then(ok, fail);

            function ok(r) {
                if (r.total != vm.pagination.total)
                    vm.pagination.total = Math.floor(r.total);
                vm.users = r.users;
            }

            function fail(response) {
                vm.pagination.total = 0;
            }
        }

        function searchByChanged() {
            if (vm.query) {
                vm.queryChanged();
            }
        }

        function queryChanged() {
            vm.pagination.page = 1;
            vm.pageChanged();
        }

        function perPage(newValue) {
            if (angular.isDefined(newValue)) {
                var old = vm.pagination.itemsPerPage;
                if (old != newValue) {
                    /* stay on the same record */
                    vm.pagination.page = Math.floor(1 + (((vm.pagination.page - 1) * old) / newValue));

                    vm.pagination.itemsPerPage = newValue;
                    vm.pageChanged();
                }
            }
            return vm.pagination.itemsPerPage;
        }

        function searchBy(newValue) {
            if (angular.isDefined(newValue)) {
                vm._searchBy = newValue;
                vm.searchByChanged();
            }
            return vm._searchBy;
        }

        function subscribe(user) {
            Activity.subscribe(user, vm.activity).then(ok, fail);

            function ok(r) {
                vm.subscribedUsers[user.id] = true;
                vm.activity.subscriptionCount = vm.activity.subscriptionCount + 1;
            }

            function fail(r) {
                var failedPleaseRetryTranslation = $translate('FAILED_PLEASE_RETRY');
                jasDialogs.resultError(failedPleaseRetryTranslation, r);
            }
        }

        function isSubscribed(user) {
            return user.id in vm.subscribedUsers;
        }

        function back() {
            var orgId = null;
            if (vm.activity.activityType && vm.activity.activityType.organizationId) {
                orgId = vm.activity.activityType.organizationId;
            } else if (vm.organization.id) {
                orgId = vm.organization.id;
            }

            if (orgId === null) {
                $location.path("/admin/activities");
            } else {
                $location.path("/admin/activities/" + orgId); //TODO: path orgID to activity/X
            }
        }
    }

})(angular);