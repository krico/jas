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

var jasifyScheduleApp = angular.module('jasifyScheduleApp', ['ngRoute', 'ngResource', 'ngMessages', 'ngCookies',
    'ui.bootstrap', 'angularSpinner', 'jasifyScheduleControllers']);

/**
 * Listen to route changes and check
 */
jasifyScheduleApp.run(function ($rootScope, $log, AUTH_EVENTS, Auth) {
    //TODO: remove, not really needed
    $rootScope.$on('$routeChangeError', function (event, next, current) {
        $log.debug('$routeChangeError, event=' + angular.toJson(event) + ' next=' + angular.toJson(next));
    });
});

/**
 * Routes for all navbar links
 */
jasifyScheduleApp.config(['$routeProvider',
    function ($routeProvider) {
        $routeProvider.
            when('/', {
                templateUrl: 'views/home.html',
                controller: 'HomeCtrl',
                resolve: {
                    allow: function (Allow) {
                        return Allow.all();
                    }
                }
            }).
            when('/home', {
                templateUrl: 'views/home.html',
                controller: 'HomeCtrl',
                resolve: {
                    allow: function (Allow) {
                        return Allow.all();
                    }
                }
            }).
            when('/signUp', {
                templateUrl: 'views/signUp.html',
                controller: 'SignUpCtrl',
                resolve: {
                    allow: function (Allow) {
                        return Allow.guest();
                    }
                }
            }).
            when('/login', {
                templateUrl: 'views/login.html',
                controller: 'LoginCtrl',
                resolve: {
                    allow: function (Allow) {
                        return Allow.guest();
                    }
                }
            }).
            when('/logout', {
                templateUrl: 'views/logout.html',
                controller: 'LogoutCtrl',
                resolve: {
                    allow: function (Allow) {
                        return Allow.all();
                    }
                }
            }).
            when('/profile/:extra?', {
                templateUrl: 'views/profile.html',
                controller: 'ProfileCtrl',
                resolve: {
                    allow: function (Allow) {
                        return Allow.user();
                    }
                }
            }).

            /* BEGIN: Admin routes */
            when('/admin/users', {
                templateUrl: 'views/admin/users.html',
                controller: 'AdminUsersCtrl',
                resolve: {
                    allow: function (Allow) {
                        return Allow.admin();
                    }
                }
            }).
            when('/admin/user/:id?', {
                templateUrl: 'views/admin/user.html',
                controller: 'AdminUserCtrl',
                resolve: {
                    allow: function (Allow) {
                        return Allow.admin();
                    }
                }
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
    notAuthorized: 'auth-not-authorized',
    notGuest: 'auth-not-guest'
});

/**
 *  Session is a singleton that mimics the server-side session
 */
jasifyScheduleApp.service('Session', function () {

    this.create = function (sessionId, userId, admin) {
        this.id = sessionId;
        this.userId = userId;
        this.admin = admin == true;
    };

    this.destroy = function () {
        this.id = null;
        this.userId = null;
        this.admin = false;
    };

    this.destroy();

    return this;
});

/**
 * Auth service
 */
jasifyScheduleApp.factory('Auth', ['$log', '$http', '$q', '$cookies', 'Session',
    function ($log, $http, $q, $cookies, Session) {

        var Auth = {};

        var loggedIn = function (res) {
            Session.create(res.data.id, res.data.userId, res.data.user.admin);
            $cookies.loggedIn = true;
            return res.data.user;
        };

        Auth.isAuthenticated = function () {
            return !!Session.id;
        };

        Auth.isAdmin = function () {
            return Session.admin;
        };

        Auth.login = function (credentials) {
            $log.info("Logging in (name=" + credentials.name + ") ...");
            return $http.post('/auth/login', credentials)
                .then(function (res) {
                    $log.info("Logged in! (userId=" + res.data.userId + ")");
                    return loggedIn(res);
                });
        };

        var restore = {
            invoked: false,
            failed: false,
            promise: null,
            data: null
        };

        Auth.restore = function (force) {
            if (force) {
                restore.invoked = false;
                restore.failed = false;
                restore.promise = null;
                restore.data = null;
            } else {
                if (!$cookies.loggedIn) {
                    restore.invoked = true;
                    restore.failed = true;
                    restore.data = 'Not logged in';
                }

                if (restore.invoked) {
                    //This is a cache of the last restore call, we make it look like it was called again

                    if (restore.promise != null) {
                        //In case the http request is pending
                        return $q.when(restore.promise);
                    }

                    var deferred = $q.defer();

                    if (restore.failed) {
                        deferred.reject(restore.data);
                    } else {
                        deferred.resolve(restore.data);
                    }

                    return deferred.promise;
                }
            }
            restore.invoked = true;

            $log.debug("Restoring session...");
            restore.promise = $http.get('/auth/restore')
                .then(function (res) {
                    $log.info("Session restored! (userId=" + res.data.userId + ")");
                    restore.promise = null;
                    restore.data = loggedIn(res);
                    return restore.data;
                },
                function (reason) {
                    $log.info("Session restore failed: " + reason);
                    restore.promise = null;
                    restore.failed = true;
                    restore.data = reason;
                    return $q.reject(restore.data);
                }
            );

            return restore.promise;
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
                    $cookies.loggedIn = false;
                });
        };


        if ($cookies.loggedIn) {
            Auth.restore().then(function (u) {
                    $cookies.loggedIn = true;
                },
                function (data) {
                    $cookies.loggedIn = false;
                });
        }

        return Auth;
    }]);

/**
 * Allow - used in Route resolve promises as Allow.all for example
 */
jasifyScheduleApp.factory('Allow', ['$log', '$q', '$rootScope', 'Auth', 'AUTH_EVENTS',
    function ($log, $q, $rootScope, Auth, AUTH_EVENTS) {
        var Allow = {};

        Allow.all = function () {
            var deferred = $q.defer();
            deferred.resolve('ok');
            return deferred.promise;
        };

        Allow.restoreThen = function (fn) {
            return Auth.restore().then(function (u) {
                    return fn();
                },
                function (data) {
                    return fn();
                });
        };

        Allow.guest = function () {
            return Allow.restoreThen(function () {
                if (Auth.isAuthenticated()) {
                    $rootScope.$broadcast(AUTH_EVENTS.notGuest);
                    return $q.reject('guests only');
                } else {
                    return true;
                }
            });
        };

        Allow.user = function () {
            return Allow.restoreThen(function () {
                if (!Auth.isAuthenticated()) {
                    $rootScope.$broadcast(AUTH_EVENTS.notAuthenticated);
                    return $q.reject('users only');
                } else {
                    return true;
                }
            });
        };

        Allow.admin = function () {
            return Allow.restoreThen(function () {
                if (!Auth.isAuthenticated()) {
                    $rootScope.$broadcast(AUTH_EVENTS.notAuthenticated);
                    return $q.reject('admins only');
                } else if (!Auth.isAdmin()) {
                    $rootScope.$broadcast(AUTH_EVENTS.notAuthorized);
                    return $q.reject('admins only');
                } else {
                    return true;
                }
            });
        };

        return Allow;
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
    return $resource('/user/:id', {id: '@id'});
    /*        {
     'query': {method: 'GET', isArray: true}
     });
     */
}]);

/**
 * Popup services (windows)
 * Inspired by satelizer (https://github.com/sahat/satellizer)
 */
jasifyScheduleApp.factory('Popup', ['$log', '$q', '$interval', '$window', function ($log, $q, $interval, $window) {
    var popupWindow = null;
    var waiting = null;

    var Popup = {};
    Popup.popupWindow = popupWindow;

    Popup.getOptions = function (options) {
        options = options || {};
        var width = options.width || 500;
        var height = options.height || 500;
        return angular.extend({
            width: width,
            height: height,
            left: $window.screenX + (($window.outerWidth - width) / 2),
            top: $window.screenY + (($window.outerHeight - height) / 2.5)
        }, options);
    };

    Popup.optionsString = function (options) {
        var parts = [];
        angular.forEach(options, function (value, key) {
            parts.push(key + '=' + value);
        });
        return parts.join(',');
    };

    Popup.open = function (url, opts) {
        var optStr = Popup.optionsString(Popup.getOptions(opts));
        popupWindow = $window.open(url, '_blank', optStr);
        if (popupWindow && popupWindow.focus) {
            popupWindow.focus();
        }
        var deferred = $q.defer();

        waiting = $interval(function () {
            try {
                if (popupWindow.document &&
                    popupWindow.document.readyState == 'complete' &&
                    popupWindow.document.domain === document.domain &&
                    popupWindow.location &&
                    popupWindow.location.pathname.indexOf('/oauth2/callback') == 0) {
                    var script = popupWindow.document.getElementById("json-response");
                    popupWindow.close();
                    $interval.cancel(waiting);
                    popupWindow = null;
                    if (script && script.text) {
                        var r = angular.fromJson(script.text);
                        deferred.resolve(r);
                    } else {
                        deferred.reject('Bad response...');
                    }
                }
            } catch (error) {
                $log.debug("E: " + error);
            }

            if (popupWindow && popupWindow.closed) {
                $interval.cancel(waiting);
                popupWindow = null;
                deferred.reject('Authorization failed (window closed)');
            }
        }, 34);
        return deferred.promise;
    };


    return Popup;
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