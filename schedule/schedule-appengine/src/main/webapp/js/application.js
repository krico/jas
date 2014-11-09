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
 * User factory
 */
jasifyScheduleApp.factory('User', ['$resource', function ($resource) {
    return $resource('/user/:id', {id: '@id'},
        {
            /* User.checkUsername([params], postData, [success], [error]) */
            'checkUsername': {method: 'POST', url: '/username'}
        });
}]);

jasifyScheduleApp.directive('strongPassword', function () {
    return {
        require: 'ngModel',
        link: function (scope, elm, attrs, ctrl) {
            ctrl.$validators.strongPassword = function (modelValue, viewValue) {
                if (ctrl.$isEmpty(modelValue)) {
                    return false; // No password
                }

                var pwdValidLength = (modelValue && modelValue.length >= 8 ? true : false);
                var pwdHasUpperLetter = (modelValue && /[A-Z]/.test(modelValue)) ? true : false;
                var pwdHasLowerLetter = (modelValue && /[a-z]/.test(modelValue)) ? true : false;
                var pwdHasNumber = (modelValue && /\d/.test(modelValue)) ? true : false;

                var status = pwdValidLength && pwdHasUpperLetter && pwdHasLowerLetter && pwdHasNumber;
                ctrl.$setValidity('pwd', status);

                return status;
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
