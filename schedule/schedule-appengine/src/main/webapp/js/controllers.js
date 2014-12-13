/**
 * Created by krico on 02/11/14.
 */
var jasifyScheduleControllers = angular.module('jasifyScheduleControllers', []);

/**
 * ApplicationCtrl
 * - Root of the scope tree.  Practically all other scopes will inherit from this one.
 */
jasifyScheduleControllers.controller('ApplicationCtrl', ['$scope',
    function ($scope) {
        $scope.currentUser = null;

        $scope.setCurrentUser = function (u) {
            $scope.currentUser = u;
        };
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
            }
        };

        $scope.$on(AUTH_EVENTS.loginSuccess, $scope.loginSucceeded);

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
jasifyScheduleControllers.controller('LoginCtrl', ['$scope', '$rootScope', 'Auth', 'AUTH_EVENTS',
    function ($scope, $rootScope, Auth, AUTH_EVENTS) {

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

    }]);

/**
 * SignUpCtrl
 */
jasifyScheduleControllers.controller('SignUpCtrl', ['$scope', '$rootScope', 'AUTH_EVENTS', 'User', 'Auth',
    function ($scope, $rootScope, AUTH_EVENTS, User, Auth) {

        $scope.alerts = [];

        $scope.inProgress = false;
        $scope.registered = false;

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

            //TODO: this seems a little too complicated...  maybe save could already login the user?
            User.save($scope.user,
                //User.save success
                function (value, responseHeaders) {
                    $scope.registered = true;
                    $scope.inProgress = false;

                    $scope.alert('success', 'Registration succeeded! Your browser should be redirected shortly...');

                    //Simulate a login
                    Auth.login($scope.user)
                        .then(
                        //Login success
                        function (user) {
                            $rootScope.$broadcast(AUTH_EVENTS.loginSuccess);
                            $scope.setCurrentUser(user);
                        },
                        //Login failure
                        function (message) {
                            $scope.alert('danger', 'Funny, even though we just registered you, your login failed...');
                        });
                },
                //User.save error
                function (httpResponse) {
                    $scope.inProgress = false;

                    $scope.alert('danger', ":-( registration failed, since this was really unexpected, please change some fields and try again.");

                });
        };
    }]);

/**
 * LogoutCtrl
 */
jasifyScheduleControllers.controller('LogoutCtrl', ['$scope', '$rootScope', 'AUTH_EVENTS', 'Auth',
    function ($scope, $rootScope, AUTH_EVENTS, Auth) {
        $scope.logout = function () {
            Auth.logout().then(
                function () {
                    $scope.setCurrentUser(null);
                    $rootScope.$broadcast(AUTH_EVENTS.logoutSuccess);
                }
            );
        };
    }]);

/**
 * ProfileCtrl
 */
jasifyScheduleControllers.controller('ProfileCtrl', ['$scope', 'Session', 'User',
    function ($scope, Session, User) {
        $scope.user = null;

        $scope.alerts = [];

        $scope.alert = function (t, m) {
            $scope.alerts.push({type: t, msg: m});
        };

        $scope.save = function () {
            $scope.user.$save().then(function () {
                $scope.alert('success', 'Profile updated!');
                //TODO: We probably need to check for failures
                $scope.setCurrentUser($scope.user);
            });
        };

        $scope.reset = function () {
            $scope.user = User.get({id: Session.userId});
        };
        $scope.reset();
    }]);

