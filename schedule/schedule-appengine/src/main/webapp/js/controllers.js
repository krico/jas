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
jasifyScheduleControllers.controller('NavbarCtrl', ['$scope', '$location', 'Auth', 'AUTH_EVENTS',
    function ($scope, $location, Auth, AUTH_EVENTS) {
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

        $scope.isActive = function (viewLocation) {
            return viewLocation === $location.path();
        };

        $scope.$on(AUTH_EVENTS.loginSuccess, function () {
            console.log("LOGIN!!!");
        });

        $scope.$watch(Auth.getCurrentUser, function (newValue, oldValue) {
            $scope.user = Auth.getCurrentUser();
        });

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
jasifyScheduleControllers.controller('HomeCtrl', ['$scope', 'Auth',
    function ($scope, Auth) {
        $scope.user = Auth.getCurrentUser();
    }]);

/**
 * SignUpCtrl
 */
jasifyScheduleControllers.controller('SignUpCtrl', ['$scope', '$http', '$location', 'User', 'Auth',
    function ($scope, $http, $location, User, Auth) {

        $scope.alerts = [];

        $scope.inProgress = false;
        $scope.registered = false;

        $scope.alert = function (t, m) {
            $scope.alerts.push({type: t, msg: m});
        };

        $scope.hasError = function (fieldName) {
            var f = $scope.signUpForm[fieldName];
            return f && f.$dirty && f.$invalid;
        };

        $scope.hasSuccess = function (fieldName) {
            var f = $scope.signUpForm[fieldName];
            return f && f.$dirty && f.$valid;
        };

        $scope.createUser = function () {
            $scope.inProgress = true;

            User.save($scope.user,
                //success
                function (value, responseHeaders) {
                    $scope.registered = true;
                    $scope.inProgress = false;

                    $scope.alert('success', 'Registration succeeded! Your browser should be redirected shortly...');

                    //Simulate a login
                    Auth.login($scope.user.name, $scope.user.password, function (message) {

                        $scope.alert('danger', 'Funny, even though we just registered you, your login failed...');

                    });
                },
                //error
                function (httpResponse) {
                    $scope.inProgress = false;

                    $scope.alert('danger', ":-( registration failed, since this was really unexpected, please change some fields and try again.");

                });
        };
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

        $scope.alert = function (t, m) {
            $scope.alerts.push({type: t, msg: m});
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
 * LogoutCtrl
 */
jasifyScheduleControllers.controller('LogoutCtrl', ['$scope', 'Auth',
    function ($scope, Auth) {
        Auth.logout();
    }]);

/**
 * ProfileCtrl
 */
jasifyScheduleControllers.controller('ProfileCtrl', ['$scope', 'Auth', 'User',
    function ($scope, Auth, User) {
        $scope.user = {};

        $scope.alerts = [];

        $scope.alert = function (t, m) {
            $scope.alerts.push({type: t, msg: m});
        };

        $scope.save = function () {
            $scope.user.$save().then(function () {
                $scope.alert('success', 'Profile updated!');
                //TODO: We probably need to check for failures
                Auth.setCurrentUser($scope.user);
            });
        };
        $scope.reset = function () {
            $scope.user = User.get({id: Auth.getCurrentUser().id});
        };
        $scope.reset();
    }]);

