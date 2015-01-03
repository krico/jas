(function (ng) {

    ng.module('jasifyScheduleControllers').controller('AdminUsersController', AdminUsersController);

    function AdminUsersController($location, User) {
        var vm = this;

        vm.sort = 'DESC';
        vm.page = 1;
        vm._perPage = 10;
        vm.total = 0;
        vm.numPages = 1;
        vm.maxSize = 4;
        vm.users = [];
        vm.searchBy = 'name';
        vm.query = '';
        vm.regex = false;

        vm.pageChanged = pageChanged;
        vm.typeChanged = typeChanged;
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

            vm.users = User.query({
                    page: vm.page,
                    size: vm._perPage,
                    sort: vm.sort,
                    field: vm.searchBy,
                    query: q
                },
                function (data, h) {
                    var t = h('X-Total');
                    if (t != vm.total)
                        vm.total = Math.floor(t);
                },
                function (response) {
                    vm.total = 0;
                    //TODO: show error
                });
        }

        function typeChanged() {
            if (vm.query) {
                vm.queryChanged();
            }
        }

        function queryChanged() {
            vm.page = 1;
            vm.pageChanged();
        }

        function perPage(newValue) {
            if (ng.isDefined(newValue)) {
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

        function viewUser(id) {
            $location.path('/admin/user/' + id);
        }

    }

})(angular);