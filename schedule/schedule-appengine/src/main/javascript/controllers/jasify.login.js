(function (angular) {

    angular.module('jasifyScheduleControllers').controller('LoginController', LoginController);

    function LoginController($scope, $rootScope, Auth, AUTH_EVENTS, Popup) {
        var vm = this;
        vm.login = login;
        vm.oauth = oauth;
        vm.credentials = {
            name: '',
            password: ''
        };

        function login(cred) {
            Auth.login(cred).then(
                function (user) {
                    $scope.setCurrentUser(user);
                    $rootScope.$broadcast(AUTH_EVENTS.loginSuccess);
                },
                function () {
                    $rootScope.$broadcast(AUTH_EVENTS.loginFailed);
                });
        }

        function oauth(provider) {
            Popup.open('/oauth2/request/' + provider, provider)
                .then(popupSuccess, popupFailed);

            function popupSuccess(oauthDetail) {
                if (oauthDetail.loggedIn) {
                    Auth.restore(true).then(restoreSuccess, restoreFailed);
                }

                function restoreSuccess(u) {
                    $scope.setCurrentUser(u);
                    $rootScope.$broadcast(AUTH_EVENTS.loginSuccess);
                }

                function restoreFailed(msg) {
                    $rootScope.$broadcast(AUTH_EVENTS.loginFailed);
                }
            }

            function popupFailed(msg) {
                $rootScope.$broadcast(AUTH_EVENTS.loginFailed);
            }
        }

    }

})(angular);