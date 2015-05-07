(function (angular) {

    angular.module('jasify.admin').controller('AdminUsersController', AdminUsersController);

    function AdminUsersController(jasPagerSettings, $location, User) {
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

        vm.searchBy = searchBy;
        vm.pageChanged = pageChanged;
        vm.searchByChanged = searchByChanged;
        vm.queryChanged = queryChanged;
        vm.perPage = perPage;
        vm.viewUser = viewUser;

        vm.pageChanged();


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

        function viewUser(id) {
            $location.path('/admin/user/' + id);
        }

    }

})(angular);