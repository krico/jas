/*global window */
(function (angular) {

    'use strict';

    angular.module('jasifyWeb').controller('ApplicationController', ApplicationController);

    function ApplicationController($timeout, $scope, $rootScope, $window, $location,
                                   localStorageService, Auth, BrowserData, AUTH_EVENTS) {

        var appVm = this;

        appVm.setCurrentUser = $rootScope.setCurrentUser = setCurrentUser;
        appVm.currentUser = null;
        appVm.menuActive = menuActive;
        appVm.noMenu = false;

        $scope.$on(AUTH_EVENTS.logoutSuccess, gotoLogin);
        $scope.$on(AUTH_EVENTS.notAuthenticated, gotoLogin);

        if (!$location.path() ||
                //don't restore if we are doing oauth or recover-password
            ($location.path().indexOf('/oauth/') === -1 && $location.path().indexOf('recover-password') === -1)) {

            restore();

        } else if ($location.path().indexOf('recover-password') !== -1) {
            //Recover password doesn't need menu
            appVm.noMenu = true;
        }

        function gotoLogin() {
            if ($location.path() &&
                $location.path() !== '/') {
                if ($location.path().indexOf('oauth') === -1 &&
                    $location.path().indexOf('recover-password') === -1 &&
                    $location.path().indexOf('logout') === -1) {
                    localStorageService.set('loginBackPath', $location.path());
                }
            } else {
                localStorageService.remove('loginBackPath');
            }

            $window.location = "login.html";
        }

        function menuActive(path) {
            if (path === $location.path()) {
                return 'active';
            }
            return false;
        }

        function setCurrentUser(user) {
            appVm.currentUser = $rootScope.currentUser = user;
        }

        function restore() {
            if (BrowserData.getLoggedIn()) {
                Auth.restore().then(function (u) {
                    setCurrentUser(u);
                    $rootScope.$broadcast(AUTH_EVENTS.loginSuccess);
                }, gotoLogin);
            }
        }

        $timeout(function () {
            appVm.hideSplash = true;
        }, 1500);
    }

})(window.angular);