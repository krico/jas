(function (angular) {

    angular.module('jasify.admin').controller('AdminUsersController', AdminUsersController);

    function AdminUsersController(jasPagerSettings, $location, User) {
        var vm = this;

        vm.sort = 'DESC';
        vm.pagerSizes = jasPagerSettings.pages;
        vm._searchBy = 'name';
        vm.users = [];
        vm.query = '';
        vm.regex = false;

        vm.searchBy = searchBy;
        vm.pageChanged = pageChanged;
        vm.searchByChanged = searchByChanged;
        vm.queryChanged = queryChanged;
        vm.perPage = perPage;
        vm.viewUser = viewUser;

        vm.pagination = {
            total: 0,
            page: 1,
            itemsPerPage: 10,
            numPages: 1,
            maxSize: 5
        };

        vm.pageChanged();

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
                if (r.total !== vm.pagination.total) {
                    vm.pagination.total = Math.floor(r.total);
                }
                vm.users = r.users || [];
                if (vm.query) {
                    vm.pagination.total = vm.users.length;
                }
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

        function viewUser(id) {
            $location.path('/admin/user/' + id);
        }
    }

})(angular);