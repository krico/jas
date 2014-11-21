/**
 * Created by krico on 02/11/14.
 */

/**
 * A function for quoting regular expressions
 * @param str the regex
 * @returns {string} the quoted regex
 */
RegExp.quote = function (str) {
    return (str + '').replace(/([.?*+^$[\]\\(){}|-])/g, "\\$1");
};

var jasifyScheduleApp = angular.module('jasifyScheduleApp',
    ['ngRoute', 'ngResource', 'ngMessages', 'ui.bootstrap', 'angularSpinner', 'jasifyScheduleControllers']);

/**
 * Routes for all navbar links
 */
jasifyScheduleApp.config(['$routeProvider',
    function ($routeProvider) {
        $routeProvider.
            when('/', {
                templateUrl: 'views/home.html',
                controller: 'HomeCtrl'
            }).
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

            /* BEGIN: Admin routes */
            when('/admin/users', {
                templateUrl: 'views/admin/users.html',
                controller: 'AdminUsersCtrl'
            }).
            when('/admin/user/:id?', {
                templateUrl: 'views/admin/user.html',
                controller: 'AdminUserCtrl'
            });
        /* END: Admin routes */
        //
        //otherwise({
        //    redirectTo: '/home'
        //});
    }]);

/**
 * Modal service
 * To talk to the user from any view/controller
 */
jasifyScheduleApp.factory('Modal', ['$log', '$modal', '$rootScope',
    function ($log, $modal, $rootScope) {
        //error.modal = $modal({
        //    scope: error.scope,
        //    template: 'views/modal/error.html',
        //    animation: 'am-fade-and-scale',
        //    show: false
        //});

        var Modal = {
            showError: function (title, description) {
                console.log("showError(" + title + ", " + description + ")");
            }
        };
        $log.debug("new Modal");
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
                $http.get('/logout');
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
        User.current(
            //success
            function (u, responseHeaders) {
                Auth.setCurrentUser(u);
            },
            //error
            function (httpResponse) {
                $log.debug('Not logged in');//todo: remove
            });

        $log.debug("new Auth");
        return Auth;
    }]);


/**
 * Util service (global utility functions)
 */
jasifyScheduleApp.factory('Util', ['$log',
    function ($log) {
        $log.debug("new Util");

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
jasifyScheduleApp.factory('User', ['$resource', '$log', function ($resource, $log) {
    $log.debug("new User");
    var User = $resource('/user/:id', {id: '@id'},
        {
            /* User.checkUsername([params], postData, [success], [error]) */
            'checkUsername': {method: 'POST', url: '/username'},
            'create': {method: 'PUT', url: '/user/new'},
            'changePassword': {method: 'POST', url: '/change-password/:id', params: {id: '@id'}},
            'query': {
                method: 'GET',
                isArray: true,
                url: '/users/page/:page/size/:size/sort/:sort',
                params: {page: '@page', size: '@size', sort: '@sort'}
            },
            'current': {method: 'GET', url: '/user/current'}
        });
    return User;
}]);

jasifyScheduleApp.directive('strongPassword', function () {
    return {
        require: 'ngModel',
        link: function (scope, elm, attrs, ctrl) {
            ctrl.$validators.strongPassword = function (modelValue, viewValue) {
                if (ctrl.$isEmpty(modelValue)) {
                    scope.passwordTooltip = "Password must contain at least eight characters uppercase letters lowercase letters numbers";
                    return false;
                }

                var pwdValidLength = (modelValue && modelValue.length >= 8 ? true : false);
                var pwdHasUpperLetter = (modelValue && /[A-Z]/.test(modelValue)) ? true : false;
                var pwdHasLowerLetter = (modelValue && /[a-z]/.test(modelValue)) ? true : false;
                var pwdHasNumber = (modelValue && /\d/.test(modelValue)) ? true : false;

                var status = pwdValidLength && pwdHasUpperLetter && pwdHasLowerLetter && pwdHasNumber;
                ctrl.$setValidity('pwd', status);


                var tooltip = "";
                if (!status) {
                    tooltip = "Password must contain";
                    if (!pwdValidLength) tooltip += " at least eight characters";
                    if (!pwdHasUpperLetter) tooltip += " uppercase letters";
                    if (!pwdHasLowerLetter) tooltip += " lowercase letters";
                    if (!pwdHasNumber) tooltip += " numbers";
                }

                scope.passwordTooltip = tooltip;
                // TODO: If password and confirm fields are set and than password is updated the confirm field must be invalidated
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
                if (compareTo.$modelValue != null && modelValue != compareTo.$modelValue) {
                    scope.confirmTooltip = "The passwords do not match.";
                    return false;
                }
                scope.confirmTooltip = "";
                return compareTo.$modelValue != null;
            };
        }
    };
});


jasifyScheduleApp.directive('username', ['$q', 'User', function ($q, User) {
    return {
        require: 'ngModel',
        link: function (scope, elm, attrs, ctrl) {

            ctrl.$asyncValidators.username = function (modelValue, viewValue) {

                if (ctrl.$isEmpty(modelValue)) {
                    return $q.when();
                }

                var def = $q.defer();

                User.checkUsername(modelValue,
                    //success
                    function (value, responseHeaders) {
                        var check = angular.fromJson(value);
                        if (check.ok) {
                            def.resolve();
                        } else if (check.nok) {
                            def.reject(check.nokText);
                        } else {
                            def.reject('unknown error');
                        }
                    },
                    //error
                    function (httpResponse) {
                        def.reject('communication error');
                    });

                return def.promise;
            };
        }
    };
}]);