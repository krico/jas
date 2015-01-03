(function (angular) {

    angular.module('jasifyScheduleControllers').controller('ApplicationController', ApplicationController);

    function ApplicationController($scope, $rootScope, $modal, $log, $location, $cookies, $window, Auth, AUTH_EVENTS, Endpoint /* TODO: Just so it is created, maybe its not needed */) {


        $scope.currentUser = null;

        $scope.setCurrentUser = function (u) {
            $scope.currentUser = u;
        };

        $scope.menuActive = function (path) {
            if (path == $location.path()) {
                return 'active';
            }
            return false;
        };

        //TODO: handle other authEvents

        $scope.$on(AUTH_EVENTS.notAuthenticated, function () {
            var modalInstance = $modal.open({
                //TODO: should bring up login.html some how
                templateUrl: 'views/modal/not-authenticated.html',
                //controller: 'ModalInstanceCtrl',
                size: 'sm'
            });

            modalInstance.result.then(function (reason) {
                $log.info('Modal accepted at: ' + new Date());
                $location.path('/login');//TODO: LOGIN SHOULD BE POPUP
            }, function () {
                $log.info('Modal dismissed at: ' + new Date());
            });
        });
        $scope.$on(AUTH_EVENTS.loginFailed, function () {
            var modalInstance = $modal.open({
                templateUrl: 'views/modal/login-failed.html',
                size: 'sm'
            });
        });

        $scope.$on(AUTH_EVENTS.notAuthorized, function () {
            var modalInstance = $modal.open({
                templateUrl: 'views/modal/not-authorized.html',
                //controller: 'ModalInstanceCtrl',
                size: 'sm'
            });

            modalInstance.result.then(function (reason) {
                $log.info('Modal accepted at: ' + new Date());
            }, function () {
                $log.info('Modal dismissed at: ' + new Date());
            });
        });

        if ($cookies.loggedIn) {
            Auth.restore().then(function (u) {
                $scope.setCurrentUser(u);
                $rootScope.$broadcast(AUTH_EVENTS.loginSuccess);
            });
        }

    }

})(angular);