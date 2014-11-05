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

        $scope.checkUsername = function () {
            if ($scope.username) {
                $scope.spinnerHidden = false;
                $scope.usernameCheck = User.checkUsername($scope.username,
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

