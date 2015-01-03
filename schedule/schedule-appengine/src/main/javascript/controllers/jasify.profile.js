(function (ng) {

    ng.module('jasifyScheduleControllers').controller('ProfileController', ProfileController);

    function ProfileController($scope, $routeParams, $log, Session, User) {
        $scope.user = null;

        $scope.alerts = [];

        $scope.isWelcome = function () {
            if ($routeParams.extra) {
                return 'welcome' == $routeParams.extra;
            }
            return false;
        };

        $scope.alert = function (t, m) {
            $scope.alerts.push({type: t, msg: m});
        };

        $scope.save = function () {
            $scope.user.$save().then(function () {
                $scope.alert('success', 'Profile updated!');
                //TODO: We probably need to check for failures
                $scope.setCurrentUser($scope.user);
                if ($scope.profileForm) {
                    $scope.profileForm.$setPristine();
                }

            });
        };

        $scope.reset = function () {
            $scope.user = User.get({id: Session.userId}, function () {
                if ($scope.profileForm) {
                    $scope.profileForm.$setPristine();
                }
            });
        };
        $scope.reset();
    }

})(angular);