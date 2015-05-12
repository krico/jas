(function (angular) {

    angular.module('jasify.admin').controller('AdminUserController', AdminUserController);

    function AdminUserController($routeParams, $location, user, User, Auth, aButtonController) {
        var vm = this;

        vm.pw = {};
        vm.created = !!$routeParams.created;
        vm.user = user;
        vm.forms = {};
        vm.reset = reset;
        vm.changePassword = changePassword;
        vm.submit = user.id ? save : createUser;

        vm.passwordBtn = aButtonController.createPassword();
        vm.submitBtn = aButtonController.createProfileSave();
        vm.resetBtn = aButtonController.createProfileReset();

        function save() {
            var promise = User.update(vm.user);
            vm.submitBtn.start(promise);
            promise.then(function ok(u) {
                vm.submitResult = 'success';
                vm.user = u;
                vm.forms.userForm.$setPristine();
                vm.forms.userForm.$setUntouched();
            });
        }

        function reset() {
            if (vm.forms.userForm) {
                vm.forms.userForm.$setPristine();
                vm.forms.userForm.$setUntouched();
            }

            if (user.id) {
                var promise = User.get(user.id);
                vm.resetBtn.start(promise);
                promise.then(function (result) {
                    vm.user = result;
                });
            } else {
                vm.resetBtn.pulse();
                vm.user = {};
            }
        }

        function createUser() {
            var promise = User.add(vm.user, vm.user.password);
            vm.submitBtn.start(promise);
            promise.then(function(r) {
                $location.path('/admin/user/' + r.id + '/created');
            });
        }


        function changePassword() {
            var promise = Auth.changePassword(vm.user, vm.pw.oldPassword, vm.pw.newPassword);
            vm.passwordBtn.start(promise);
            promise.then(function () {
                vm.forms.passwordForm.$setPristine();
                vm.forms.passwordForm.$setUntouched();
                vm.pw = {};
            }, function () {
                vm.forms.passwordForm.$setPristine();
                vm.forms.passwordForm.$setUntouched();
            });
        }

    }

})(angular);