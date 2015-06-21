/*global window */
(function (angular) {

    'use strict';

    angular.module('jasifyWeb').controller('ApplicationController', ApplicationController);

    function ApplicationController($timeout, $scope, $rootScope, $window, $location,
                                   Auth, BrowserData, AUTH_EVENTS) {

        var appVm = this;

        appVm.currentUser = $rootScope.setCurrentUser = setCurrentUser;
        appVm.currentUser = null;
        appVm.menuActive = menuActive;

        $scope.$on(AUTH_EVENTS.logoutSuccess, gotoLogin);

        restore();

        function gotoLogin() {
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
                    appVm.setCurrentUser(u);
                    $rootScope.$broadcast(AUTH_EVENTS.loginSuccess);
                }, gotoLogin);
            }
        }

        $timeout(function () {
            appVm.hideSplash = true;
        }, 2000);
    }

})(window.angular);