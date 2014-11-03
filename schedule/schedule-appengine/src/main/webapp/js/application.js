/**
 * Created by krico on 02/11/14.
 */
var jasifyScheduleApp = angular.module('jasifyScheduleApp', [
    'ngRoute',
    'ngAnimate', 'mgcrea.ngStrap',
    'jasifyScheduleControllers'
]);

jasifyScheduleApp.config(['$routeProvider',
    function ($routeProvider) {
        $routeProvider.
            when('/home', {
                templateUrl: 'views/home.html',
                controller: 'HomeCtrl'
            }).
            when('/signUp', {
                templateUrl: 'views/signUp.html',
                controller: 'SignUpCtrl'
            }).
            when('/login', {
                templateUrl: 'views/login.html',
                controller: 'LoginCtrl'
            }).
            when('/help', {
                templateUrl: 'views/help.html',
                controller: 'HelpCtrl'
            }).
            when('/contactUs', {
                templateUrl: 'views/contactUs.html',
                controller: 'ContactUsCtrl'
            }).
            otherwise({
                redirectTo: '/home'
            });
    }]);

jasifyScheduleApp.controller('NavbarCtrl', ['$scope', '$location',
    function ($scope, $location) {
        $scope.isActive = function (viewLocation) {
            return viewLocation === $location.path();
        };
    }]);