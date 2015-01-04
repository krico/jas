(function (angular) {

    angular.module('jasifyScheduleControllers').controller('ProfileController', ProfileController);

    function ProfileController($scope, $routeParams, $log, Session, User) {
        var vm = this;
        vm.isWelcome = isWelcome;
        vm.alert = alert;
        vm.save = save;
        vm.reset = reset;
        vm.user = null;

        vm.alerts = [];

        vm.reset();

        function isWelcome() {
            if ($routeParams.extra) {
                return 'welcome' == $routeParams.extra;
            }
            return false;
        }

        function alert(t, m) {
            vm.alerts.push({type: t, msg: m});
        }

        function save() {
            vm.user.$save().then(saveSuccess, saveFailed);
            function saveSuccess() {
                vm.alert('success', 'Profile updated!');
                $scope.setCurrentUser(vm.user);
                if (vm.profileForm) {
                    vm.profileForm.$setPristine();
                }

            }

            function saveFailed(msg) {
                vm.alert('danger', 'Failed to save...');
                $log.debug('Failed to save: ' + msg);
            }
        }

        function reset() {
            vm.user = User.get({id: Session.userId}, function () {
                if (vm.profileForm) {
                    vm.profileForm.$setPristine();
                }
            });
        }
    }

})(angular);