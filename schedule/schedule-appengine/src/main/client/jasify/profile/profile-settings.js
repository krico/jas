(function (angular) {

    angular.module('jasifyWeb').controller('ProfileSettingsController', ProfileSettingsController);

    function ProfileSettingsController($scope, $routeParams, Session, User, aButtonController) {

        // Uncomment to see how mask works
        // $timeout(mask.show, 2000);

        var vm = this;

        vm.setLocale = setLocale;
        vm.saveBtn = aButtonController.createProfileSave();
        vm.resetBtn = aButtonController.createProfileReset();

        vm.isWelcome = $routeParams.extra === 'welcome';
        vm.save = save;
        vm.reset = reset;
        vm.user = {};

        vm.reset(true);

        function setLocale(locale) {
            vm.user.locale = locale;
        }

        function save() {

            /*
             * Simulate long running request
             */
            //$timeout(function() {
            //
            //}, 6000);

            var promise = User.update(vm.user);
            vm.saveBtn.start(promise);
            promise.then(function saveSuccess(result) {
                $scope.setCurrentUser(vm.user);
                vm.user = result;
                vm.profileForm.$setPristine();
                vm.profileForm.$setUntouched();
            });
        }

        function reset(initialReset) {
            /*
             * Simulate long running request
             */
            //$timeout(function() {
            //
            //}, 2000);

            var promise = User.get(Session.userId);
            if (!initialReset) {
                vm.resetBtn.start(promise);
            }
            promise.then(function (user) {
                if (!initialReset) {
                    vm.profileForm.$setPristine();
                    vm.profileForm.$setUntouched();
                }
                vm.user = user;
            });

        }
    }

})(angular);