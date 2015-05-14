(function (angular) {

    angular.module('jasify.admin').controller('AdminSubscribeController', AdminSubscribeController);

    function AdminSubscribeController(jasPagerSettings, $location, User, Activity, activity, subscriptions) {
        var vm = this;

        vm.sort = 'DESC';
        vm.page = 1;
        vm.pagerSizes = jasPagerSettings.pages;
        vm._perPage = 10;
        vm._searchBy = 'name';
        vm.total = 0;
        vm.numPages = 1;
        vm.maxSize = 4;
        vm.users = [];
        vm.query = '';
        vm.regex = false;

        vm.activity = activity;
        vm.subscriptions = subscriptions;
        vm.subscribe = subscribe;
        vm.isSubscribed = isSubscribed;
        vm.back = back;
        vm.subscribedUsers = [];

        vm.searchBy = searchBy;
        vm.pageChanged = pageChanged;
        vm.searchByChanged = searchByChanged;
        vm.queryChanged = queryChanged;
        vm.perPage = perPage;

        vm.init = init;

        vm.init();

        function init() {
            var arrayLength = vm.subscriptions.items.length;
            for (i = 0; i < arrayLength; i++) {
                vm.subscribedUsers[vm.subscriptions.items[i].user.id] = true;
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
                offset: vm._perPage * (vm.page-1),
                limit: vm._perPage,
                sort: vm.sort,
                field: vm.searchBy(),
                query: q
            }).then(ok, fail);

            function ok(r) {
                if (r.total != vm.total)
                    vm.total = Math.floor(r.total);
                vm.users = r.users;
            }

            function fail(response) {
                vm.total = 0;
            }
        }

        function searchByChanged() {
            if (vm.query) {
                vm.queryChanged();
            }
        }

        function queryChanged() {
            vm.page = 1;
            vm.pageChanged();
        }

        function perPage(newValue) {
            if (angular.isDefined(newValue)) {
                var old = vm._perPage;
                if (old != newValue) {
                    /* stay on the same record */
                    vm.page = 1 + (((vm.page - 1) * old) / newValue);

                    vm._perPage = newValue;
                    vm.pageChanged();
                }
            }
            return vm._perPage;
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
                vm.alert('danger', 'Failed to find user');
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