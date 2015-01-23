(function (angular) {

    angular.module('jasifyScheduleControllers').controller('ApplicationController', ApplicationController);

    function ApplicationController($route, $scope, $rootScope, $modal, $log, $location, $cookies, Auth, AUTH_EVENTS) {
        var appVm = this;

        $scope.currentUser = null;
        $scope.setCurrentUser = setCurrentUser;

        appVm.menuActive = menuActive;
        appVm.restore = restore;

        appVm.notAuthenticated = notAuthenticated;
        appVm.loginFailed = loginFailed;
        appVm.notAuthorized = notAuthorized;

        $scope.$on(AUTH_EVENTS.notAuthenticated, appVm.notAuthenticated);
        $scope.$on(AUTH_EVENTS.loginFailed, appVm.loginFailed);
        $scope.$on(AUTH_EVENTS.notAuthorized, appVm.notAuthorized);

        appVm.restore();

        function setCurrentUser(u) {
            $scope.currentUser = u;
        }

        function menuActive(path) {
            if (path == $location.path()) {
                return 'active';
            }
            return false;
        }

        function restore() {
            if ($cookies.loggedIn) {
                Auth.restore().then(function (u) {
                    $scope.setCurrentUser(u);
                    $rootScope.$broadcast(AUTH_EVENTS.loginSuccess);
                });
            }
        }

        function notAuthenticated() {
            var modalInstance = $modal.open({
                //TODO: should bring up login.html some how
                templateUrl: 'authenticate/authenticate.html',
                controller: 'AuthenticateController',
                controllerAs: 'vm',
                size: 'sm'
            });

            modalInstance.result.then(function (reason) {
                Auth.restore(true).then(ok, fail);
                function ok(u) {
                    $scope.setCurrentUser(u);
                    $rootScope.$broadcast(AUTH_EVENTS.loginSuccess);
                    $route.reload();
                }

                function fail() {

                }
            }, function () {
                $log.info('Modal dismissed at: ' + new Date());
            });
        }

        function loginFailed() {
            $modal.open({
                templateUrl: 'modal/login-failed.html',
                size: 'sm'
            });
        }

        function notAuthorized() {
            var modalInstance = $modal.open({
                templateUrl: 'modal/not-authorized.html',
                //controller: 'ModalInstanceCtrl',
                size: 'sm'
            });

            modalInstance.result.then(function (reason) {
                $log.info('Modal accepted at: ' + new Date());
            }, function () {
                $log.info('Modal dismissed at: ' + new Date());
            });
        }

    }

})(angular);