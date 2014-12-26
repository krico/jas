/**
 * Created by krico on 02/11/14.
 */
var jasifyScheduleControllers = angular.module('jasifyScheduleControllers', []);

/**
 * ApplicationCtrl
 * - Root of the scope tree.  Practically all other scopes will inherit from this one.
 */
jasifyScheduleControllers.controller('ApplicationCtrl', ['$scope', '$rootScope', '$modal', '$log', '$location', '$cookies', 'Auth', 'AUTH_EVENTS',
    function ($scope, $rootScope, $modal, $log, $location, $cookies, Auth, AUTH_EVENTS) {
        $scope.currentUser = null;

        $scope.setCurrentUser = function (u) {
            $scope.currentUser = u;
        };

        //TODO: handle other authEvents

        $scope.$on(AUTH_EVENTS.notAuthenticated, function () {
            var modalInstance = $modal.open({
                //TODO: should bring up login.html some how
                templateUrl: 'views/modal/not-authenticated.html',
                //controller: 'ModalInstanceCtrl',
                size: 'sm'
            });

            modalInstance.result.then(function (reason) {
                $log.info('Modal accepted at: ' + new Date());
                $location.path('/login');//TODO: LOGIN SHOULD BE POPUP
            }, function () {
                $log.info('Modal dismissed at: ' + new Date());
            });
        });

        $scope.$on(AUTH_EVENTS.notAuthorized, function () {
            var modalInstance = $modal.open({
                templateUrl: 'views/modal/not-authorized.html',
                //controller: 'ModalInstanceCtrl',
                size: 'sm'
            });

            modalInstance.result.then(function (reason) {
                $log.info('Modal accepted at: ' + new Date());
            }, function () {
                $log.info('Modal dismissed at: ' + new Date());
            });
        });

        if ($cookies.loggedIn) {
            Auth.restore().then(function (u) {
                $scope.setCurrentUser(u);
                $rootScope.$broadcast(AUTH_EVENTS.loginSuccess);
            });
        }

    }]);

/**
 * NavbarCtrl
 */
jasifyScheduleControllers.controller('NavbarCtrl', ['$scope', '$log', '$location', 'Auth', 'AUTH_EVENTS',
    function ($scope, $log, $location, Auth, AUTH_EVENTS) {
        $scope.isAdmin = function () {
            return $scope.currentUser && $scope.currentUser.admin;
        };

        $scope.path = "";

        $scope.navbarCollapsed = true;

        $scope.toggleCollapse = function () {
            $scope.navbarCollapsed = !$scope.navbarCollapsed;
        };

        $scope.collapse = function () {
            $scope.navbarCollapsed = true;
        };

        $scope.menuActive = function (path) {
            if (path == $location.path()) {
                return 'active';
            }
            return false;
        };

        $scope.adminDropDown = [
            {
                "text": 'users',
                html: true,
                "href": "#/admin/users"
            }
        ];

        $scope.loginSucceeded = function () {
            $log.debug("LOGIN SUCCEEDED!");
            if ($scope.menuActive('/login')) {
                $location.path('/profile');
            } else if ($scope.menuActive('/signUp')) {
                $location.path('/profile/welcome');
            }
        };

        $scope.logoutSucceeded = function () {
            $log.debug("LOGOUT SUCCEEDED!");
        };

        $scope.$on(AUTH_EVENTS.loginSuccess, $scope.loginSucceeded);
        $scope.$on(AUTH_EVENTS.logoutSuccess, $scope.logoutSucceeded);

        $scope.$watch(function () {
            return $location.path();
        }, function (newValue, oldValue) {
            if (newValue)
                $scope.path = newValue;
        });
    }]);

/**
 * HomeCtrl
 */
jasifyScheduleControllers.controller('HomeCtrl', ['$scope',
    function ($scope) {
    }]);

/**
 * LoginCtrl
 */
jasifyScheduleControllers.controller('LoginCtrl', ['$scope', '$rootScope', 'Auth', 'AUTH_EVENTS', 'Popup',
    function ($scope, $rootScope, Auth, AUTH_EVENTS, Popup) {

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
            Popup.open('/oauth2/request/' + provider)
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


    }]);

/**
 * SignUpCtrl
 */
jasifyScheduleControllers.controller('SignUpCtrl', ['$scope', '$log', '$rootScope', 'AUTH_EVENTS', 'User', 'Auth', 'Popup',
    function ($scope, $log, $rootScope, AUTH_EVENTS, User, Auth, Popup) {

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
            Popup.open('/oauth2/request/' + provider)
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

    }]);

/**
 * LogoutCtrl
 */
jasifyScheduleControllers.controller('LogoutCtrl', ['$scope', '$rootScope', 'AUTH_EVENTS', 'Auth',
    function ($scope, $rootScope, AUTH_EVENTS, Auth) {
        $scope.logout = function () {
            if (!Auth.isAuthenticated()) return;
            Auth.logout().then(
                function () {
                    $rootScope.$broadcast(AUTH_EVENTS.logoutSuccess);
                    $scope.setCurrentUser(null);
                }
            );
        };
    }]);

/**
 * ProfileCtrl
 */
jasifyScheduleControllers.controller('ProfileCtrl', ['$scope', '$routeParams', 'Session', 'User',
    function ($scope, $routeParams, Session, User) {
        $scope.user = null;

        $scope.alerts = [];

        $scope.isWelcome = function () {
            if ($routeParams.extra) {
                return 'welcome' == $routeParams.extra;
            }
            return false;
        };

        $scope.alert = function (t, m) {
            $scope.alerts.push({type: t, msg: m});
        };

        $scope.save = function () {
            $scope.user.$save().then(function () {
                $scope.alert('success', 'Profile updated!');
                //TODO: We probably need to check for failures
                $scope.setCurrentUser($scope.user);
                if ($scope.profileForm) {
                    $scope.profileForm.$setPristine();
                }

            });
        };

        $scope.reset = function () {
            $scope.user = User.get({id: Session.userId}, function () {
                if ($scope.profileForm) {
                    $scope.profileForm.$setPristine();
                }
            });
        };
        $scope.reset();
    }]);

