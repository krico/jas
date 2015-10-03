(function (angular) {

    angular.module('jasify.admin').controller('AdminBookingsController', AdminBookingsController);

    function AdminBookingsController(Activity, jasDialogs, $filter, $moment, $q, User) {
        var vm = this;

        vm.getSubscriptions = getSubscriptions;
        vm.allSubscriptions = [];
        vm.subscriptions = [];
        vm.inProgress = false;
        vm.fromDate = $moment().set('hour', 0).set('minute', 0).set('second', 0).format();
        vm.toDate = undefined;

        vm.searchUsers = searchUsers;
        vm.userFilter = userFilter;
        vm.displayUser = displayUser;
        vm.allUsers = null;
        vm.users = [];
        vm.selectedUsers = [];
        vm.user = null;

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

        function getSubscriptions() {
            if (vm.inProgress) {
                return;
            }

            if (!vm.user) return;

            vm.inProgress = true;
            vm.allSubscriptions = [];
            vm.subscriptions = [];

            Activity.getUserSubscriptions(vm.user.id, vm.fromDate, vm.toDate).then(ok, fail);

            function ok(resp) {
                vm.inProgress = false;
                vm.allSubscriptions = resp.items;
                vm.allSubscriptions.sort(function (a, b) {
                    return a.start - b.start;
                });
                vm.pagination.total = resp.items.length;
                pageChanged();
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

        function userFilter(user, viewValue) {
            if (user.name && user.name.indexOf(viewValue) != -1) {
                return true;
            }
            if (user.email && user.email.indexOf(viewValue) != -1) {
                return true;
            }
            return user.realName && user.realName.toLowerCase().indexOf(viewValue.toLowerCase()) != -1;
        }

        function displayUser(user) {
            if (!user || !user.id) return '';
            var ret = "";
            if (user.name) {
                ret += user.name;
            } else if (user.email) {
                ret += user.email;
            } else if (user.realName) {
                ret += user.realName;
            }
            return ret;
        }

        function searchUsers(v) {
            if (vm.allUsers === null) {
                vm.allUsers = [];
                return User.query({limit: 0}).then(ok, fail);
            } else {
                var ret = [];
                for (var i in vm.allUsers) {
                    if (vm.userFilter(vm.allUsers[i], v)) {
                        ret.push(vm.allUsers[i]);
                    }
                }
                return $q.when(ret);
            }

            function ok(res) {
                vm.allUsers = res.users;
                return vm.searchUsers(v);
            }
        }

        function fail(resp) {
            vm.inProgress = false;
            var failedPleaseRetryTranslation = $translate('FAILED_PLEASE_RETRY');
            jasDialogs.resultError(failedPleaseRetryTranslation, resp);
        }
    }
})(angular);