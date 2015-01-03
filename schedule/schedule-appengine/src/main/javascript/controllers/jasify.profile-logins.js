(function (angular) {

    angular.module('jasifyScheduleControllers').controller('ProfileLoginsController', ProfileLoginsController);

    function ProfileLoginsController($scope, $log, $q, UserLogin, Session, Popup, logins) {
        $scope.logins = logins;
        $scope.alerts = [];

        $scope.alert = function (t, m) {
            $scope.alerts.push({type: t, msg: m});
        };

        $scope.icon = function (login) {
            if (login && login.provider) {
                if (login.provider == 'Google') return 'ion-social-google';
                if (login.provider == 'Facebook') return 'ion-social-facebook';
            }
            return false;
        };

        $scope.removeLogin = function (login) {
            UserLogin.remove(login).then(function (ok) {
                    $scope.alert('success', 'Login removed!');
                    $scope.reload();
                },
                function (msg) {
                    $scope.alert('danger', '! ' + msg);
                }
            );
        };

        $scope.reload = function () {
            $scope.logins = [];
            UserLogin.list(Session.userId).then(function (logins) {
                $scope.logins = logins;
            });
        };

        $scope.oauth = function (provider) {
            Popup.open('/oauth2/request/' + provider, provider)
                .then(
                function (oauthDetail) {
                    if (oauthDetail.added) {
                        $scope.alert('success', 'Login added!');
                        $scope.reload();
                    } else if (oauthDetail.exists) {
                        $scope.alert('warning', 'Login already existed...');
                    }
                },
                function (msg) {
                    $scope.alert('danger', '! ' + msg);
                });
        };
    }

})(angular);