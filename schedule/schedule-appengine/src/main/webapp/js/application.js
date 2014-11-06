/**
 * Created by krico on 02/11/14.
 */
var jasifyScheduleApp = angular.module('jasifyScheduleApp', ['ngRoute', 'ngResource', 'ngAnimate', 'mgcrea.ngStrap',
    'angularSpinner', 'jasifyScheduleControllers']);

/**
 * Routes for all navbar links
 */
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
            when('/logout', {
                templateUrl: 'views/logout.html',
                controller: 'LogoutCtrl'
            }).
            when('/profile', {
                templateUrl: 'views/profile.html',
                controller: 'ProfileCtrl'
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

/**
 * Auth service
 */
jasifyScheduleApp.factory('Auth', ['$log',
    function ($log) {
        var currentUser;
        return {
            isLoggedIn: function () {
                if (currentUser) {
                    return true;
                } else {
                    return false;
                }
            },
            logout: function () {
                $log.info("Log out!");
                currentUser = null;
            },
            setCurrentUser: function (u) {
                $log.info('Auth.currentUser=' + u)
                currentUser = u;
            },
            getCurrentUser: function () {
                return currentUser;
            }

        };
    }]);

/**
 * User service
 */
jasifyScheduleApp.factory('User', ['$resource', function ($resource) {
    return $resource('/user/:id', {id: '@id'},
        {
            /* User.checkUsername([params], postData, [success], [error]) */
            'checkUsername': {method: 'POST', url: '/username'},
            'create': {method: 'PUT', url: '/user/new'},
        });
}]);

jasifyScheduleApp.directive('strongPassword', function () {
    return {
        require: 'ngModel',
        link: function (scope, elm, attrs, ctrl) {
            ctrl.$validators.strongPassword = function (modelValue, viewValue) {
                if (ctrl.$isEmpty(modelValue)) {
                    return false;
                }

                if (modelValue.length >= 4) {
                    return true;
                }
                return false;
            };
        }
    };
});

jasifyScheduleApp.directive('confirmField', function () {
    return {
        require: 'ngModel',
        link: function (scope, elm, attrs, ctrl) {
            ctrl.$validators.confirmField = function (modelValue, viewValue) {
                var compareTo = scope.$eval(attrs.confirmField);
                return modelValue == compareTo.$modelValue;
            };
        }
    };
});
