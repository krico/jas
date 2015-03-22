(function (angular) {

    angular.module('jasifyWeb').controller('ApplicationController', ApplicationController);

    function ApplicationController($scope, $rootScope, $modal, $log, $location, $filter, Auth, ApiSettings, BrowserData, AUTH_EVENTS, VERSION) {
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
        appVm.versionString = versionString;
        appVm.longVersion = false;
        appVm.toggleVersion = toggleVersion;
        appVm.serverVersion = serverVersion;
        appVm.serverVersionString = serverVersionString;
        appVm.SERVER_VERSION = null;

        $scope.$on(AUTH_EVENTS.notAuthenticated, appVm.notAuthenticated);
        $scope.$on(AUTH_EVENTS.signIn, appVm.signIn);
        $scope.$on(AUTH_EVENTS.createAccount, appVm.createAccount);
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
            if (BrowserData.getLoggedIn()) {
                Auth.restore().then(function (u) {
                    $scope.setCurrentUser(u);
                    $rootScope.$broadcast(AUTH_EVENTS.loginSuccess);
                });
            }
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

            modalInstance.result.then(function (reason) {
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

            modalInstance.result.then(function (reason) {
                $log.info('Modal accepted at: ' + new Date());
            }, function () {
                $log.info('Modal dismissed at: ' + new Date());
            });
        }

        function versionString() {
            var ts = new Date(Number(VERSION.timestamp));
            if (appVm.longVersion) {
                return '<span class="client-version">' +
                    '<strong>version:</strong> ' + VERSION.version +
                    ' <strong>number:</strong> ' + VERSION.number +
                    ' <strong>branch:</strong> ' + VERSION.branch +
                    ' <strong>built on:</strong> ' + $filter('date')(ts, 'yyyy-MM-dd HH:mm:ss') +
                    '</span>';
            }
            return '<span class="client-version"><strong>version:</strong> ' + VERSION.version + ' (' + $filter('date')(ts, 'yy.MM.dd') + ')</span> ';
        }

        function serverVersionString() {
            var VERSION = serverVersion();
            if (!(VERSION && VERSION.version)) {
                return ' <span class="server-version"><strong>server:</strong> fetching...</span>';
            }

            var ts = new Date(Number(VERSION.timestamp));
            if (appVm.longVersion) {
                return '<span class="server-version">' +
                    '<strong>server:</strong> ' + VERSION.version +
                    ' <strong>number:</strong> ' + VERSION.number +
                    ' <strong>branch:</strong> ' + VERSION.branch +
                    ' <strong>built on:</strong> ' + $filter('date')(ts, 'yyyy-MM-dd HH:mm:ss') +
                    '</span>';
            }
            return '<span class="server-version"><strong>server:</strong> ' + VERSION.version + ' (' + $filter('date')(ts, 'yy.MM.dd') + ')</span> ';
        }

        function serverVersion() {
            if (appVm.SERVER_VERSION === null) {
                appVm.SERVER_VERSION = {};
                ApiSettings.getVersion().then(ok, fail);
            }
            function ok(resp) {
                appVm.SERVER_VERSION = resp;
            }

            function fail(resp) {
                $log.debug("Failed to get server version");
            }
            return appVm.SERVER_VERSION;
        }

        function toggleVersion() {
            appVm.longVersion = !appVm.longVersion;
        }

    }

})(angular);