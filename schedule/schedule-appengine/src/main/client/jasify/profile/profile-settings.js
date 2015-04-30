(function (angular) {

    angular.module('jasifyWeb').controller('ProfileSettingsController', ProfileSettingsController);

    function ProfileSettingsController($scope, $timeout, $routeParams, Session, User) {

        var vm = this;

        vm.submitOptions = {
            buttonSuccessText: 'Profile updated',
            buttonSuccessClass: 'btn-success',
            buttonInitialIcon: 'fa fa-send'
        };

        vm.isWelcome = $routeParams.extra === 'welcome';
        vm.save = save;
        vm.reset = reset;
        vm.user = {};

        vm.reset();

        function save() {

            vm.isSubmitting = true;

            /*
             * Simulate long running request
             */
            $timeout(function() {
                User.update(vm.user).then(function saveSuccess(result) {

                    vm.result = 'success';
                    $scope.setCurrentUser(vm.user);

                    vm.user = result;
                    vm.profileForm.$setPristine();
                    vm.profileForm.$setUntouched();

                });
            }, 6000);
        }

        function reset() {
            User.get(Session.userId).then(function (user) {
                vm.profileForm.$setPristine();
                vm.profileForm.$setUntouched();
                vm.user = user;
            });
        }
    }

})(angular);