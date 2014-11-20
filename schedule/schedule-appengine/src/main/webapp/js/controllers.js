/**
 * Created by krico on 02/11/14.
 */
var jasifyScheduleControllers = angular.module('jasifyScheduleControllers', []);

jasifyScheduleControllers.controller('NavbarCtrl', ['$scope', '$location', 'Auth',
    function ($scope, $location, Auth) {
        $scope.user = Auth.getCurrentUser();
        $scope.isAdmin = function () {
            return $scope.user && $scope.user.admin;
        };

        $scope.path = "";

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

jasifyScheduleControllers.controller('HomeCtrl', ['$scope', 'Auth',
    function ($scope, Auth) {
        $scope.user = Auth.getCurrentUser();
    }]);

jasifyScheduleControllers.controller('SignUpCtrl', ['$scope', '$http', '$location', 'Util', 'User', 'Auth',
    function ($scope, $http, $location, Util, User, Auth) {

        $scope.usernameCheck = {};

        $scope.spinnerHidden = true;

        $scope.newUser = {}; //TODO: remove

        $scope.alerts = [];

        $scope.alert = function (t, m) {
            $scope.alerts.push({type: t, msg: m});
        };

        $scope.hasError = function (fieldName) {
            return Util.formFieldError($scope.signUpForm, fieldName);
        };

        $scope.hasSuccess = function (fieldName) {
            return Util.formFieldSuccess($scope.signUpForm, fieldName);
        };

        $scope.checkUsername = function () {
            if ($scope.user.name) {
                $scope.spinnerHidden = false;
                $scope.usernameCheck = User.checkUsername($scope.user.name,
                    //success
                    function (value, responseHeaders) {
                    },
                    //error
                    function (httpResponse) {
                        //simulate a nok
                        $scope.usernameCheck = {nok: true, nokText: 'Communication error'};
                    });
            } else {
                $scope.spinnerHidden = true;
                $scope.usernameCheck = {};
            }
        };

        $scope.createUser = function () {
            $scope.newUser = User.save($scope.user,
                //success
                function (value, responseHeaders) {

                    $scope.alert('success', 'Registration succeeded! Your browser should be redirected shortly...');

                    //Simulate a login
                    Auth.login($scope.user.name, $scope.user.password, function (message) {

                        $scope.alert('danger', 'Funny, even though we just registered you, your login failed...');

                        $scope.newUser = {};
                    });
                },
                //error
                function (httpResponse) {
                    //simulate a nok
                    $scope.usernameCheck = {nok: true, nokText: 'Registration failed'};
                    $scope.newUser = {};
                    $scope.alert('danger', ":-( registration failed, since this was really unexpected, please change some fields and try again.");
                });
        };
    }]);

jasifyScheduleControllers.controller('LoginCtrl', ['$scope', 'Util', 'Auth', 'Modal',
    function ($scope, Util, Auth, Modal) {

        $scope.alerts = [];

        $scope.user = {};

        $scope.credentials = {};

        $scope.alert = function (t, m) {
            $scope.alerts.push({type: t, msg: m});
        };


        $scope.hasError = function (fieldName) {
            return Util.formFieldError($scope.loginForm, fieldName);
        };

        $scope.hasSuccess = function (fieldName) {
            return Util.formFieldSuccess($scope.loginForm, fieldName);
        };

        $scope.login = function () {
            Auth.login($scope.credentials.name, $scope.credentials.password, function (reason) {
                $scope.alert('warning', 'Login failed!');
            });
        };

    }]);

jasifyScheduleControllers.controller('LogoutCtrl', ['$scope', 'Auth',
    function ($scope, Auth) {
        Auth.logout();
    }]);

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

