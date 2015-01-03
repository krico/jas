(function (angular) {

    angular.module('jasifyScheduleControllers').controller('SignUpController', SignUpController);

    function SignUpController($scope, $rootScope, AUTH_EVENTS, User, Auth, Popup) {
        $scope.alerts = [];

        $scope.inProgress = false;
        $scope.registered = false;
        $scope.provider = null;
        $scope.user = {};

        $scope.alert = function (t, m) {
            $scope.alerts.push({type: t, msg: m});
        };

        $scope.hasError = function (fieldName) {
            if ($scope.signUpForm[fieldName]) {
                var f = $scope.signUpForm[fieldName];
                return f && f.$dirty && f.$invalid;
            } else {
                return false;
            }
        };

        $scope.hasSuccess = function (fieldName) {
            if ($scope.signUpForm[fieldName]) {
                var f = $scope.signUpForm[fieldName];
                return f && f.$dirty && f.$valid;
            } else {
                return false;
            }
        };

        $scope.createUser = function () {
            $scope.inProgress = true;

            User.save($scope.user,
                //User.save success
                function (value, responseHeaders) {
                    $scope.registered = true;
                    $scope.inProgress = false;

                    $scope.alert('success', 'Registration succeeded! You should be redirected shortly...');

                    Auth.restore(true).then(
                        function (u) {
                            $scope.setCurrentUser(u);
                            $rootScope.$broadcast(AUTH_EVENTS.loginSuccess);
                        },
                        function (msg) {
                            $scope.alert('danger', '! Something went really wrong...');
                        });

                },
                //User.save error
                function (httpResponse) {
                    $scope.inProgress = false;

                    $scope.alert('danger', ":-( registration failed, since this was really unexpected, please change some fields and try again.");

                });
        };

        $scope.oauth = function (provider) {
            $scope.inProgress = true;
            $scope.provider = provider;
            Popup.open('/oauth2/request/' + provider, provider)
                .then(
                function (oauthDetail) {
                    $scope.inProgress = false;
                    if (oauthDetail.loggedIn) {
                        $scope.alert('info', 'Authenticated! This user is already registered, will log you in');

                        Auth.restore(true).then(
                            function (u) {
                                $scope.setCurrentUser(u);
                                $rootScope.$broadcast(AUTH_EVENTS.loginSuccess);
                            },
                            function (msg) {
                                $scope.alert('danger', '! Something went really wrong...');
                            });
                        return;
                    }

                    if (oauthDetail) {
                        $scope.user.realName = oauthDetail.realName;
                        $scope.user.email = oauthDetail.email;
                    }
                    $scope.alert('info', 'Authenticated! You just need to finish registering your user by selecting a Display Name');
                    try {
                        $scope.signUpForm.username.focus();
                    } catch (e) {
                    }
                },
                function (msg) {
                    $scope.inProgress = false;
                    $scope.provider = null;
                    $scope.alert('danger', '! ' + msg);
                });
        };

    }

})(angular);