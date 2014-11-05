/**
 * Created by krico on 02/11/14.
 */
var jasifyScheduleControllers = angular.module('jasifyScheduleControllers', ['mgcrea.ngStrap']);

jasifyScheduleControllers.controller('HomeCtrl', ['$scope',
    function ($scope) {
    }]);

jasifyScheduleControllers.controller('SignUpCtrl', ['$scope', '$http',
    function ($scope, $http) {

        $scope.usernameGroup = undefined;
        $scope.usernameGlyph =undefined;
        $scope.usernameHideSpin =undefined;

        $scope.resetUsername = function () {
            $scope.usernameGlyph = 'form-control-feedback';
            $scope.usernameGroup = 'has-feedback';
            $scope.usernameHideSpin = true;
        };
        $scope.resetUsername();

        $scope.acceptUsername = function () {
            $scope.usernameGlyph = 'glyphicon-ok form-control-feedback';
            $scope.usernameGroup = 'has-success has-feedback';
            $scope.usernameHideSpin = true;
        };

        $scope.rejectUsername = function () {
            $scope.usernameGlyph = 'glyphicon-remove form-control-feedback';
            $scope.usernameGroup = 'has-error has-feedback';
            $scope.usernameHideSpin = true;
        };

        $scope.checkUsername = function () {
            $scope.resetUsername();
            $scope.usernameHideSpin = false;
            $http.post('/username/valid', {username: $scope.username}).
                success(function (data, status, headers, config) {
                    var ret = angular.fromJson(data);
                    if (ret.status == 0)
                        $scope.acceptUsername();
                    else
                        $scope.rejectUsername();//TODO: show the message
                }).
                error(function (data, status, headers, config) {
                    $scope.rejectUsername();
                });
        };

        $scope.popover = {
            "title": "Coming soon...",
            "content": "To a theater near you!"
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

