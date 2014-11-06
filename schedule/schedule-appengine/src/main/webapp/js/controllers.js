/**
 * Created by krico on 02/11/14.
 */
var jasifyScheduleControllers = angular.module('jasifyScheduleControllers', ['mgcrea.ngStrap']);

jasifyScheduleControllers.controller('NavbarCtrl', ['$scope', '$location', 'Auth',
    function ($scope, $location, Auth) {
        $scope.user = Auth.getCurrentUser();

        $scope.isActive = function (viewLocation) {
            return viewLocation === $location.path();
        };

        $scope.$watch(Auth.getCurrentUser, function (newValue, oldValue) {
            $scope.user = Auth.getCurrentUser();
        });
    }]);

jasifyScheduleControllers.controller('HomeCtrl', ['$scope', 'Auth',
    function ($scope, Auth) {
        $scope.user = Auth.getCurrentUser();
    }]);

jasifyScheduleControllers.controller('SignUpCtrl', ['$scope', '$http', '$alert', '$location', 'User', 'Auth',
    function ($scope, $http, $alert, $location, User, Auth) {

        $scope.usernameCheck = {};

        $scope.spinnerHidden = true;

        $scope.newUser = {}; //TODO: remove

        $scope.hasError = function (fieldName) {
            var f = $scope.signUpForm[fieldName];
            return f.$dirty && f.$invalid;
        };

        $scope.hasSuccess = function (fieldName) {
            var f = $scope.signUpForm[fieldName];
            return f.$dirty && f.$valid;
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
                    Auth.setCurrentUser($scope.newUser);
                    $alert({
                        title: 'Registration succeeded!',
                        content: 'You were successfully registered.',
                        container: '#alert-container',
                        type: 'success',
                        show: true
                    });
                    $location.path('/home');
                },
                //error
                function (httpResponse) {
                    //simulate a nok
                    $scope.usernameCheck = {nok: true, nokText: 'Registration failed'};
                    $scope.newUser = {};
                    $alert({
                        title: 'Registration failed!',
                        content: 'Registration failed, please try again.',
                        container: '#alert-container',
                        type: 'error',
                        show: true
                    });
                });
        };
    }]);

jasifyScheduleControllers.controller('LoginCtrl', ['$scope',
    function ($scope) {
        $scope.user = {};
        $scope.credentials = {};
        $scope.login = function () {
            $scope.user = User.login($scope.credentials);
        }
    }]);

jasifyScheduleControllers.controller('LogoutCtrl', ['$scope', 'Auth',
    function ($scope, Auth) {
        Auth.logout();
    }]);

jasifyScheduleControllers.controller('ProfileCtrl', ['$scope', 'Auth',
    function ($scope, Auth) {
        $scope.user = Auth.getCurrentUser();
    }]);

jasifyScheduleControllers.controller('HelpCtrl', ['$scope',
    function ($scope) {
    }]);

jasifyScheduleControllers.controller('ContactUsCtrl', ['$scope',
    function ($scope) {
    }]);

