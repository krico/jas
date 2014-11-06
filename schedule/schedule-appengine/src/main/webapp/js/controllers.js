/**
 * Created by krico on 02/11/14.
 */
var jasifyScheduleControllers = angular.module('jasifyScheduleControllers', ['mgcrea.ngStrap']);

jasifyScheduleControllers.controller('NavbarCtrl', ['$scope', '$location',
    function ($scope, $location) {
        $scope.isActive = function (viewLocation) {
            return viewLocation === $location.path();
        };
    }]);

jasifyScheduleControllers.controller('HomeCtrl', ['$scope',
    function ($scope) {
    }]);

jasifyScheduleControllers.controller('SignUpCtrl', ['$scope', '$http', 'User',
    function ($scope, $http, User) {

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
            console.log("create: " + angular.toJson($scope.user));
            $scope.newUser = User.save($scope.user,                     //success
                function (value, responseHeaders) {
                },
                //error
                function (httpResponse) {
                    //simulate a nok
                    $scope.usernameCheck = {nok: true, nokText: 'Communication error'};
                });
        };
    }]);

jasifyScheduleControllers.controller('LoginCtrl', ['$scope',
    function ($scope) {
        $scope.popover = {
            "title": "You wish...",
            "content": "This functionality is not yet available!"
        };
    }]);

jasifyScheduleControllers.controller('HelpCtrl', ['$scope',
    function ($scope) {
    }]);

jasifyScheduleControllers.controller('ContactUsCtrl', ['$scope',
    function ($scope) {
    }]);

