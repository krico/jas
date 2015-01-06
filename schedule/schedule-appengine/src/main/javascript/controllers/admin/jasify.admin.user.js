(function (angular) {

    angular.module('jasifyScheduleControllers').controller('AdminUserController', AdminUserController);

    function AdminUserController($routeParams, User, Auth) {
        var vm = this;

        vm.user = null;
        vm.pw = {};
        vm.create = false;
        vm.loading = true;

        vm.alerts = [];
        vm.forms = {};
        vm.alert = alert;
        vm.save = save;
        vm.reset = reset;
        vm.createUser = createUser;
        vm.changePassword = changePassword;


        vm.reset();


        function alert(t, m) {
            vm.alerts.push({type: t, msg: m});
        }

        function save() {

            vm.loading = true;

            User.update(vm.user).then(ok, fail);

            function ok(u) {
                vm.loading = false;
                vm.user = u;
                if (vm.forms.userForm) {
                    vm.forms.userForm.$setPristine();
                }

                vm.alert('success', 'User updated successfully (' + new Date() + ')');

            }

            function fail() {
                vm.loading = false;
                vm.alert('danger', 'User update failed (' + new Date() + ')');
            }
        }

        function reset() {

            vm.loading = true;

            if (vm.forms.userForm) {
                vm.forms.userForm.$setPristine();
            }

            if ($routeParams.id) {
                vm.user = {};
                User.get($routeParams.id).then(ok, fail);

            } else {
                vm.user = {};
                vm.create = true;
                vm.loading = false;
            }

            function ok(r) {
                vm.user = r;
                vm.loading = false;
            }

            function fail() {
                vm.loading = false;
                vm.alert('danger', 'Failed to read the user data from the server (' + new Date() + ')');
            }

        }

        function createUser() {

            vm.loading = true;

            User.add(vm.user).then(ok, fail);

            function ok(r) {
                vm.user = r;
                vm.alert('success', 'User creation succeeded!');
                vm.create = false;
                vm.loading = false;
            }

            function fail() {

                vm.loading = false;

                vm.alert('danger', 'User creation failed!');
            }
        }


        function changePassword() {

            vm.loading = true;

            Auth.changePassword(vm.user, vm.pw.newPassword)
                .then(
                //success
                function () {
                    vm.loading = false;
                    if (vm.forms.passwordForm) {
                        vm.forms.passwordForm.$setPristine();
                    }
                    vm.alert('success', 'Password changed (' + new Date() + ')');
                    vm.pw = {};
                },
                // failure
                function (data) {
                    vm.loading = false;
                    if (vm.forms.passwordForm) {
                        vm.forms.passwordForm.$setPristine();
                    }
                    vm.alert('danger', 'Password change failed (' + new Date() + ')');

                }
            );
        }

    }

})(angular);