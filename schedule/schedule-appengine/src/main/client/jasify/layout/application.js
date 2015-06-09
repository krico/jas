/*global window */
(function (angular) {

    'use strict';

    angular.module('jasifyWeb').controller('ApplicationController', ApplicationController);

    function ApplicationController($timeout, $scope, $rootScope, $modal, $log, $location,
                                   Auth, BrowserData, Endpoint, AUTH_EVENTS) {
        var appVm = this;

        $scope.currentUser = null;
        $scope.setCurrentUser = setCurrentUser;

        appVm.menuActive = menuActive;
        appVm.restore = restore;

        appVm.notAuthenticated = notAuthenticated;
        appVm.signIn = signIn;
        appVm.createAccount = createAccount;
        appVm.loginFailed = loginFailed;
        appVm.notAuthorized = notAuthorized;
        appVm.authenticate = authenticate;
        appVm.isLoaded = isLoaded;
        appVm.isAuthenticated = isAuthenticated;

        $scope.$on(AUTH_EVENTS.notAuthenticated, appVm.notAuthenticated);
        $scope.$on(AUTH_EVENTS.signIn, appVm.signIn);
        $scope.$on(AUTH_EVENTS.createAccount, appVm.createAccount);
        $scope.$on(AUTH_EVENTS.loginFailed, appVm.loginFailed);
        $scope.$on(AUTH_EVENTS.notAuthorized, appVm.notAuthorized);

        appVm.restore();

        function isLoaded() {
            return Endpoint.isLoaded();
        }

        function setCurrentUser(u) {
            $scope.currentUser = u;
        }

        function menuActive(path) {
            if (path === $location.path()) {
                return 'active';
            }
            return false;
        }

        function restore() {
            if (BrowserData.getLoggedIn()) {
                Auth.restore().then(function (u) {
                    $scope.setCurrentUser(u);
                    $rootScope.$broadcast(AUTH_EVENTS.loginSuccess);
                });
            }
        }

        function isAuthenticated() {
            return Auth.isAuthenticated();
        }

        function notAuthenticated() {
            appVm.authenticate(!BrowserData.getFirstAccess());
        }

        function createAccount() {
            appVm.authenticate(false);
        }

        function signIn() {
            appVm.authenticate(true);
        }

        function authenticate(isSignIn) {
            var scope = $scope.$new(); //new scope for the modal

            scope.signIn = !!isSignIn;

            var modalInstance = $modal.open({
                //TODO: should bring up login.html some how
                templateUrl: 'authenticate/authenticate.html',
                controller: 'AuthenticateController',
                controllerAs: 'vm',
                size: 'sm',
                scope: scope
            });

            modalInstance.result.then(function () {
                Auth.restore(true).then(ok, fail);
                function ok(u) {
                    $scope.setCurrentUser(u);
                    //This gets caught by NavbarController
                    $rootScope.$broadcast(AUTH_EVENTS.loginSuccess);
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

            modalInstance.result.then(function () {
                $log.info('Modal accepted at: ' + new Date());
            }, function () {
                $log.info('Modal dismissed at: ' + new Date());
            });
        }

        $timeout(function () {
            appVm.hideSplash = true;
        }, 2000);
    }

})(window.angular);