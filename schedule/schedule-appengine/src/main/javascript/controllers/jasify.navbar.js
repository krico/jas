(function (angular) {

    angular.module('jasifyScheduleControllers').controller('NavbarController', NavbarController);

    function NavbarController($scope, $log, $location, Auth, AUTH_EVENTS) {
        var vm = this;

        vm.path = "";
        vm.navbarCollapsed = true;
        vm.toggleCollapse = toggleCollapse;
        vm.loginSucceeded = loginSucceeded;
        vm.logoutSucceeded = logoutSucceeded;

        vm.menuActive = menuActive;
        vm.isAdmin = isAdmin;
        vm.collapse = collapse;
        vm.adminDropDown = [
            {
                "text": 'users',
                html: true,
                "href": "#/admin/users"
            }
        ];

        $scope.$on(AUTH_EVENTS.loginSuccess, vm.loginSucceeded);
        $scope.$on(AUTH_EVENTS.logoutSuccess, vm.logoutSucceeded);
        $scope.$watch(pathWatch, onPathChanged);

        function menuActive(path) {
            if (path == $location.path()) {
                return 'active';
            }
            return false;
        }

        function pathWatch() {
            return $location.path();
        }

        function onPathChanged(newValue, oldValue) {
            if (newValue)
                vm.path = newValue;
        }

        function isAdmin() {
            return Auth.isAdmin();
        }


        function toggleCollapse() {
            vm.navbarCollapsed = !vm.navbarCollapsed;
        }

        function collapse() {
            vm.navbarCollapsed = true;
        }

        function loginSucceeded() {
            $log.debug("LOGIN SUCCEEDED!");
            if (vm.menuActive('/login')) {
                $location.path('/profile');
            } else if (vm.menuActive('/signUp')) {
                $location.path('/profile/welcome');
            }
        }

        function logoutSucceeded() {
            $log.debug("LOGOUT SUCCEEDED!");
        }
    }

})(angular);