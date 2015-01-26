(function (angular) {

    angular.module('jasify.bookIt').controller('BookItController', BookItController);

    function BookItController($rootScope, $route, $cookies, $modal, AUTH_EVENTS, Auth) {
        var appVm = this;

        appVm.restore = restore;
        appVm.notAuthenticated = notAuthenticated;
        appVm.authenticate = authenticate;
        appVm.logout = logout;
        appVm.signIn = signIn;

        appVm.navbarCollapsed = true;
        appVm.toggleCollapse = toggleCollapse;
        appVm.collapse = collapse;

        appVm.isAuthenticated = isAuthenticated;

        $rootScope.$on(AUTH_EVENTS.notAuthenticated, appVm.notAuthenticated);
        $rootScope.$on(AUTH_EVENTS.signIn, appVm.notAuthenticated);
        //$rootScope.$on(AUTH_EVENTS.loginSuccess, appVm.loginSuccess);
        //$rootScope.$on(AUTH_EVENTS.logoutSuccess, appVm.logoutSuccess);


        //restore if user is already logged in
        appVm.restore();


        function toggleCollapse() {
            appVm.navbarCollapsed = !appVm.navbarCollapsed;
        }

        function collapse() {
            appVm.navbarCollapsed = true;
        }


        function restore() {
            if ($cookies.loggedIn) {
                Auth.restore().then(function (u) {
                    $rootScope.$broadcast(AUTH_EVENTS.loginSuccess, u.user);
                });
            }
        }

        function signIn() {
            if (Auth.isAuthenticated()) return;
            $rootScope.$broadcast(AUTH_EVENTS.signIn);
        }

        function logout() {
            if (!Auth.isAuthenticated()) return;
            return Auth.logout().then(reload, reload);
            function reload() {
                $route.reload();
            }
        }

        function isAuthenticated() {
            return Auth.isAuthenticated();
        }

        function notAuthenticated() {
            appVm.authenticate($cookies.loggedIn);
        }

        function authenticate(isSignIn) {
            var scope = $rootScope.$new(); //new scope for the modal

            scope.signIn = !!isSignIn;

            var modalInstance = $modal.open({
                //TODO: should bring up login.html some how
                templateUrl: 'authenticate/authenticate.html',
                controller: 'AuthenticateController',
                controllerAs: 'vm',
                size: 'sm',
                scope: scope
            });

            modalInstance.result.then(modalOk, modalFailed);
            function modalOk(reason) {
                Auth.restore(true).then(ok, fail);

                function ok(u) {
                    $route.reload();
                }

                function fail() {
                    //TODO: handle failure
                    $route.reload();
                }
            }

            function modalFailed() {
                //TODO: notify user
                $route.reload();
            }
        }
    }
})(angular);