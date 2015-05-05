(function (angular) {

    angular.module('jasify.admin').controller('AdminUserController', AdminUserController);

    function AdminUserController($routeParams, $location, user, User, Auth) {
        var vm = this;

        vm.pw = {};
        vm.created = !!$routeParams.created;
        vm.user = user;
        vm.forms = {};
        vm.reset = reset;
        vm.changePassword = changePassword;
        vm.submit = user.id ? save : createUser;

        vm.passwordResetOptions = {
            buttonDefaultClass: 'btn-warning',
            buttonSubmittingClass: 'bgm-deeporange',
            buttonDefaultText: 'Update Password',
            buttonSubmittingText: 'Saving...',
            buttonSuccessText: 'Password updated'
        };

        vm.submitOptions = {
            buttonDefaultText: 'Save',
            buttonSubmittingText: 'Saving...',
            buttonSuccessText: 'Profile updated'
        };

        vm.resetOptions = {
            buttonDefaultClass: 'btn-warning',
            buttonSubmittingClass: 'bgm-deeporange',
            buttonDefaultText: 'Reset',
            buttonSubmittingText: 'Reseting...',
            buttonSuccessText: 'Profile restored'
        };

        function save() {
            vm.isSubmitting = true;
            User.update(vm.user).then(function ok(u) {
                vm.submitResult = 'success';
                vm.user = u;
                vm.forms.userForm.$setPristine();
                vm.forms.userForm.$setUntouched();
            });
        }

        function reset() {

            vm.isReseting = true;

            if (vm.forms.userForm) {
                vm.forms.userForm.$setPristine();
                vm.forms.userForm.$setUntouched();
            }

            if (user.id) {
                User.get(user.id).then(function (result) {
                    vm.resetResult = 'success';
                    vm.user = result;
                });
            } else {
                vm.resetResult = 'success';
                vm.user = {};
            }
        }

        function createUser() {
            User.add(vm.user, vm.user.password).then(function(r) {
                $location.path('/admin/user/' + r.id + '/created');
            });
        }


        function changePassword() {
            vm.isResetingPassword = true;
            Auth.changePassword(vm.user, vm.pw.oldPassword, vm.pw.newPassword).then(function () {
                vm.forms.passwordForm.$setPristine();
                vm.forms.passwordForm.$setUntouched();
                vm.resetPasswordResult = 'success';
                vm.pw = {};
            }, function () {
                vm.forms.passwordForm.$setPristine();
                vm.forms.passwordForm.$setUntouched();
                vm.resetPasswordResult = 'error';
            });
        }

    }

})(angular);