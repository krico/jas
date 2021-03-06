(function (angular) {

    angular.module('jasifyWeb').controller('LogoutController', LogoutController);

    function LogoutController($scope, $rootScope, $location, $log, AUTH_EVENTS, Auth) {
        var vm = this;
        vm.logout = logout;
        vm.logout();
        function logout() {
            if (!Auth.isAuthenticated()) return;
            Auth.logout().then(logoutSuccess, logoutFailed);

            function logoutSuccess() {
                $rootScope.$broadcast(AUTH_EVENTS.logoutSuccess);
                $scope.setCurrentUser(null);
                $location.path('');
            }

            function logoutFailed(msg) {
                $log.debug("Failed to logout: " + msg);
            }
        }
    }

})(angular);