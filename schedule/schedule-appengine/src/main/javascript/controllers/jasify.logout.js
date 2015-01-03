(function (ng) {

    ng.module('jasifyScheduleControllers').controller('LogoutController', LogoutController);

    function LogoutController($scope, $rootScope, AUTH_EVENTS, Auth) {
        $scope.logout = function () {
            if (!Auth.isAuthenticated()) return;
            Auth.logout().then(
                function () {
                    $rootScope.$broadcast(AUTH_EVENTS.logoutSuccess);
                    $scope.setCurrentUser(null);
                }
            );
        };
    }

})(angular);