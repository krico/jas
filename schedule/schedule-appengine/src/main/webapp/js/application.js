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
 * Modal service
 * To talk to the user from any view/controller
 */
jasifyScheduleApp.factory('Modal', ['$log', '$modal', '$rootScope',
    function ($log, $modal, $rootScope) {
        var error = {scope: $rootScope.$new()};

        error.modal = $modal({
            scope: error.scope,
            template: 'views/modal/error.html',
            animation: 'am-fade-and-scale',
            show: false
        });

        var Modal = {
            showError: function (title, description) {
                error.scope.title = title;
                error.scope.content = description;
                error.modal.show();
            }
        };
        return Modal;
    }]);
/**
 * Auth service
 */
jasifyScheduleApp.factory('Auth', ['$log', '$location', '$http', 'User', 'Modal',
    function ($log, $location, $http, User, Modal) {
        var currentUser;
        var Auth = {
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

            /**
             * Login
             * @param name username
             * @param pass password
             * @param callback optional error handler function with signature function(reason)
             */
            login: function (name, pass, callback) {
                $http.post('/login', {name: name, password: pass})
                    .success(function (data, status, headers, config) {
                        var ret;
                        try {
                            ret = angular.fromJson(data);
                        } catch (e) {
                        }
                        if (ret.ok) {
                            User.current(
                                //success
                                function (u, responseHeaders) {
                                    Auth.onLoggedIn(u);
                                },
                                //error
                                function (httpResponse) {
                                    Modal.showError('Unexpected error', 'We failed to fetch your user, sorry :-(')
                                });
                        } else {
                            var message;
                            if (ret && ret.nokText) {
                                message = ret.nokText;
                            } else {
                                message = 'We failed to log you in, sorry :-(';
                            }
                            if (callback) {
                                callback(message);
                            } else {
                                Modal.showError('Unhandled login failure', message)
                            }
                        }
                    })
                    .error(function (data, status, headers, config) {
                        var ret;
                        try {
                            ret = angular.fromJson(data);
                        } catch (e) {
                        }
                        var message;
                        if (ret && ret.nokText) {
                            message = ret.nokText;
                        } else {
                            message = 'We failed to log you in, sorry :-(';
                        }
                        if (callback) {
                            callback(message);
                        } else {
                            Modal.showError('Unhandled login failure', message)
                        }
                    });
            },
            onLoggedIn: function (user) {
                Auth.setCurrentUser(user);
                $location.path('/home');
            },
            setCurrentUser: function (u) {
                $log.info('Auth.currentUser=' + u)
                currentUser = u;
            },
            getCurrentUser: function () {
                return currentUser;
            }

        };
        return Auth;
    }]);


/**
 * Util service (global utility functions)
 */
jasifyScheduleApp.factory('Util', ['$log',
    function ($log) {
        return {
            formFieldError: function (form, fieldName) {
                var f = form[fieldName];
                return f && f.$dirty && f.$invalid;
            },
            formFieldSuccess: function (form, fieldName) {
                var f = form[fieldName];
                return f && f.$dirty && f.$valid;
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
            'current': {method: 'GET', url: '/user/current'}
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
