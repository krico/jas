/**
 * Created by krico on 02/11/14.
 */
var jasifyScheduleControllers = angular.module('jasifyScheduleControllers', ['mgcrea.ngStrap']);

jasifyScheduleControllers.controller('HomeCtrl', ['$scope',
    function ($scope) {
    }]);

jasifyScheduleControllers.controller('SignUpCtrl', ['$scope',
    function ($scope) {

        $scope.state = {
            'username': {
                'group': '',
                'glyph': '',
                'spin': true
            }
        };

        $scope.checkUsername = function () {
            $scope.state.username.spin = false;
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

