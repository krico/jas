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

        $scope.$on(AUTH_EVENTS.logoutSuccess, gotoLogin);

        if (!$location.path() || $location.path().indexOf('/oauth/') === -1) {
            //don't restore if we are doing oauth
            restore();
        }

        function gotoLogin() {
            if ($location.path() &&
                $location.path() !== '/') {
                if ($location.path().indexOf('oauth') === -1 &&
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
        }, 2000);
    }

})(window.angular);