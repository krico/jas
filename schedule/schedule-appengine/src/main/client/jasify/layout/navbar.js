(function (angular) {

    angular.module('jasifyWeb').controller('NavbarController', NavbarController);

    function NavbarController($rootScope, $scope, $log, $location, $route, Auth, AUTH_EVENTS) {
        var vm = this;

        vm.path = "";
        vm.navbarCollapsed = true;
        vm.toggleCollapse = toggleCollapse;
        vm.loginSucceeded = loginSucceeded;
        vm.logoutSucceeded = logoutSucceeded;
        vm.isAuthenticated = isAuthenticated;
        vm.isAdmin = isAdmin;

        vm.menuActive = menuActive;
        vm.collapse = collapse;
        vm.createAccount = createAccount;
        vm.signIn = signIn;
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

        function isAuthenticated() {
            return Auth.isAuthenticated();
        }

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

        function createAccount() {
            vm.collapse();
            $rootScope.$broadcast(AUTH_EVENTS.createAccount);
        }

        function signIn() {
            vm.collapse();
            $rootScope.$broadcast(AUTH_EVENTS.signIn);
        }

        function loginSucceeded() {
            $log.debug("LOGIN SUCCEEDED!");
            if (vm.menuActive('/logout')) {
                $location.path('/home');
            } else {
                $route.reload();
            }
        }

        function logoutSucceeded() {
            $log.debug("LOGOUT SUCCEEDED!");
        }
    }

})(angular);