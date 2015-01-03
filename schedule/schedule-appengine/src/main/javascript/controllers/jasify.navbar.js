(function (angular) {

    angular.module('jasifyScheduleControllers').controller('NavbarController', NavbarController);

    function NavbarController($scope, $log, $location, Auth, AUTH_EVENTS) {
        $scope.isAdmin = function () {
            return Auth.isAdmin();
        };

        $scope.path = "";

        $scope.navbarCollapsed = true;

        $scope.toggleCollapse = function () {
            $scope.navbarCollapsed = !$scope.navbarCollapsed;
        };

        $scope.collapse = function () {
            $scope.navbarCollapsed = true;
        };

        $scope.adminDropDown = [
            {
                "text": 'users',
                html: true,
                "href": "#/admin/users"
            }
        ];

        $scope.loginSucceeded = function () {
            $log.debug("LOGIN SUCCEEDED!");
            if ($scope.menuActive('/login')) {
                $location.path('/profile');
            } else if ($scope.menuActive('/signUp')) {
                $location.path('/profile/welcome');
            }
        };

        $scope.logoutSucceeded = function () {
            $log.debug("LOGOUT SUCCEEDED!");
        };

        $scope.$on(AUTH_EVENTS.loginSuccess, $scope.loginSucceeded);
        $scope.$on(AUTH_EVENTS.logoutSuccess, $scope.logoutSucceeded);

        $scope.$watch(function () {
            return $location.path();
        }, function (newValue, oldValue) {
            if (newValue)
                $scope.path = newValue;
        });
    }

})(angular);