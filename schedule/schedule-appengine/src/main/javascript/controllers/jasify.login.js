(function (angular) {

    angular.module('jasifyScheduleControllers').controller('LoginController', LoginController);

    function LoginController($scope, $rootScope, Auth, AUTH_EVENTS, Popup) {

        $scope.credentials = {
            name: '',
            password: ''
        };

        $scope.login = function (cred) {
            Auth.login(cred).then(
                function (user) {
                    $rootScope.$broadcast(AUTH_EVENTS.loginSuccess);
                    $scope.setCurrentUser(user);
                },
                function () {
                    $rootScope.$broadcast(AUTH_EVENTS.loginFailed);
                });
        };

        $scope.oauth = function (provider) {
            Popup.open('/oauth2/request/' + provider, provider)
                .then(
                function (oauthDetail) {
                    if (oauthDetail.loggedIn) {
                        Auth.restore(true).then(
                            function (u) {
                                $scope.setCurrentUser(u);
                                $rootScope.$broadcast(AUTH_EVENTS.loginSuccess);
                            },
                            function (msg) {
                                $rootScope.$broadcast(AUTH_EVENTS.loginFailed);
                            });
                    }
                },
                function (msg) {
                    $rootScope.$broadcast(AUTH_EVENTS.loginFailed);
                });
        };

    }

})(angular);