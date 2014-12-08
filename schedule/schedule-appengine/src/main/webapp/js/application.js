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

var jasifyScheduleApp = angular.module('jasifyScheduleApp', ['ngRoute', 'ngResource', 'ngMessages', 'ui.bootstrap', 'angularSpinner', 'jasifyScheduleControllers']);

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
 * Constant for the authentication related events
 */
jasifyScheduleApp.constant('AUTH_EVENTS', {
    loginSuccess: 'auth-login-success',
    loginFailed: 'auth-login-failed',
    logoutSuccess: 'auth-logout-success',
    sessionTimeout: 'auth-session-timeout',
    notAuthenticated: 'auth-not-authenticated',
    notAuthorized: 'auth-not-authorized'
});

/**
 *  Session is a singleton that mimics the server-side session
 */
jasifyScheduleApp.service('Session', function () {

    this.create = function (sessionId, userId) {
        this.id = sessionId;
        this.userId = userId;
    };

    this.destroy = function () {
        this.id = null;
        this.userId = null;
    };

    this.destroy();

    return this;
});

/**
 * Auth service
 */
jasifyScheduleApp.factory('Auth', ['$log', '$http', '$q', 'Session',
    function ($log, $http, $q, Session) {

        var Auth = {};

        var loggedIn = function (res) {
            Session.create(res.data.id, res.data.userId);
            return res.data.user;
        };

        var restore = {
            invoked: false,
            failed: false,
            data: null
        };

        Auth.isAuthenticated = function () {
            return !!Session.id;
        };

        Auth.login = function (credentials) {
            $log.info("Logging in (name=" + credentials.name + ") ...");
            return $http.post('/auth/login', credentials)
                .then(function (res) {
                    $log.info("Logged in! (userId=" + res.data.userId + ")");
                    return loggedIn(res);
                });
        };

        Auth.restore = function () {
            if (restore.invoked) {
                //This is a cache of the last restore call, we make it look like it was called again
                $log.info("Restore called more than once, returning cached values...");

                var deferred = $q.defer();

                if (restore.failed) {
                    deferred.reject(restore.data);
                } else {
                    deferred.resolve(restore.data);
                }

                return deferred.promise;
            }

            restore.invoked = true;

            $log.debug("Restoring session...");
            return $http.get('/auth/restore')
                .then(function (res) {
                    $log.info("Session restored! (userId=" + res.data.userId + ")");
                    restore.data = loggedIn(res);
                    return restore.data;
                },
                function (reason) {
                    restore.failed = true;
                    restore.data = reason;
                    return $q.reject(restore.data);
                }
            );
        };

        Auth.changePassword = function (credentials, newPassword) {
            $log.info("Changing password (userId=" + Session.userId + ")!");
            return $http.post('/auth/change-password', {credentials: credentials, newPassword: newPassword});
        };

        Auth.logout = function () {
            $log.info("Logging out (" + Session.userId + ")!");
            return $http.get('/auth/logout')
                .then(function (res) {
                    $log.info("Logged out!");
                    Session.destroy();
                });
        };

        return Auth;
    }]);

jasifyScheduleApp.factory('Username', ['$log', '$http',
    function ($log, $http) {
        var Username = {};

        Username.check = function (name) {
            return $http.post('/username', name);
        };

        return Username;
    }]);

/**
 * User service
 */
jasifyScheduleApp.factory('User', ['$resource', '$log', function ($resource, $log) {
    return $resource('/user/:id', {id: '@id'},
        {
            'query': {
                method: 'GET',
                isArray: true,
                url: '/users/page/:page/size/:size/sort/:sort',
                params: {page: '@page', size: '@size', sort: '@sort'}
            }
        });
}]);

/**
 * Strong password directive
 */
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

/**
 * ConfirmField directive
 */
jasifyScheduleApp.directive('confirmField', function () {
    return {
        require: 'ngModel',
        link: function (scope, elm, attrs, ctrl) {
            ctrl.$validators.confirmField = function (modelValue, viewValue) {
                var compareTo = scope.$eval(attrs.confirmField);
                if (compareTo && compareTo.$modelValue != null && modelValue != compareTo.$modelValue) {
                    scope.confirmTooltip = "The passwords do not match.";
                    return false;
                }
                scope.confirmTooltip = "";
                return compareTo && compareTo.$modelValue != null;
            };
        }
    };
});

/**
 * Username directive
 */
jasifyScheduleApp.directive('username', ['$q', 'Username', function ($q, Username) {
    return {
        require: 'ngModel',
        link: function (scope, elm, attrs, ctrl) {

            ctrl.$asyncValidators.username = function (modelValue, viewValue) {

                if (ctrl.$isEmpty(modelValue)) {
                    return $q.when();
                }

                var def = $q.defer();

                return Username.check(modelValue);
            };
        }
    };
}]);