(function (angular) {

    angular.module('jasifyScheduleControllers').controller('ProfileLoginsController', ProfileLoginsController);

    function ProfileLoginsController($scope, $log, $q, UserLogin, Session, Popup, logins) {
        var vm = this;

        vm.logins = logins;
        vm.alerts = [];

        vm.alert = function (t, m) {
            vm.alerts.push({type: t, msg: m});
        };

        vm.icon = function (login) {
            if (login && login.provider) {
                if (login.provider == 'Google') return 'ion-social-google';
                if (login.provider == 'Facebook') return 'ion-social-facebook';
            }
            return false;
        };

        vm.removeLogin = function (login) {
            UserLogin.remove(login).then(function (ok) {
                    vm.alert('success', 'Login removed!');
                    vm.reload();
                },
                function (msg) {
                    vm.alert('danger', '! ' + msg);
                }
            );
        };

        vm.reload = function () {
            vm.logins = [];
            UserLogin.list(Session.userId).then(function (logins) {
                vm.logins = logins;
            });
        };

        vm.oauth = function (provider) {
            Popup.open('/oauth2/request/' + provider, provider)
                .then(
                function (oauthDetail) {
                    if (oauthDetail.added) {
                        vm.alert('success', 'Login added!');
                        vm.reload();
                    } else if (oauthDetail.exists) {
                        vm.alert('warning', 'Login already existed...');
                    }
                },
                function (msg) {
                    vm.alert('danger', '! ' + msg);
                });
        };
    }

})(angular);