(function (angular) {

    angular.module('jasifyWeb').controller('ProfileSettingsController', ProfileSettingsController);

    function ProfileSettingsController($scope, $timeout, $routeParams, Session, User, mask) {

        // Uncomment to see how mask works
        // $timeout(mask.show, 2000);

        var vm = this;

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

        vm.isWelcome = $routeParams.extra === 'welcome';
        vm.save = save;
        vm.reset = reset;
        vm.user = {};

        vm.reset(true);

        function save() {

            vm.isSubmitting = true;

            /*
             * Simulate long running request
             */
            //$timeout(function() {
            //
            //}, 6000);

            User.update(vm.user).then(function saveSuccess(result) {
                vm.submitResult = 'success';

                $scope.setCurrentUser(vm.user);
                vm.user = result;
                vm.profileForm.$setPristine();
                vm.profileForm.$setUntouched();

            });
        }

        function reset(initialReset) {

            if (!initialReset) {
                vm.isReseting = true;
            }

            /*
             * Simulate long running request
             */
            //$timeout(function() {
            //
            //}, 2000);

            User.get(Session.userId).then(function (user) {
                if (!initialReset) {
                    vm.profileForm.$setPristine();
                    vm.profileForm.$setUntouched();
                    vm.resetResult = 'success';
                }
                vm.user = user;
            });

        }
    }

})(angular);