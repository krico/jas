/**
 * Created by krico on 02/11/14.
 */

var gapi = gapi || {};
var jas = {};

/**
 * This is the function called by the google api client when gapi is loaded
 */
function initializeEndpoint() {
    if(window.endpointInitialize) window.endpointInitialize();
}

/**
 * When you are not sure what an object is, this function tries to help.
 * Example $log.debug('What is this: ' + jas.debugObjext(x));
 *
 * @param o anytihng
 * @returns {string} with the debug data
 */
jas.debugObject = function jasDebugObject(o) {
    try {
        return o.toSource();
    } catch (e) {
    }
    var dbg = '';
    for (var i in o) {
        dbg += 'o.' + i + ' = "';
        try {
            dbg += o[i];
        } catch (e) {
        }
        dbg += '"\n';
    }
    return dbg;
};

/**
 * A function for quoting regular expressions
 * @param str the regex
 * @returns {string} the quoted regex
 */
RegExp.quote = function (str) {
    return (str + '').replace(/([.?*+^$[\]\\(){}|-])/g, "\\$1");
};

angular.module('jasify', ['ngRoute', 'ngResource', 'ngMessages', 'ngCookies',
    'ui.bootstrap', 'angularSpinner', 'jasifyScheduleControllers']);

/**
 * Listen to route changes and check
 */
angular.module('jasify').run(function ($rootScope, $log, AUTH_EVENTS, Auth) {
    //TODO: remove, not really needed
    $rootScope.$on('$routeChangeError', function (event, next, current) {
        $log.debug('$routeChangeError, event=' + angular.toJson(event) + ' next=' + angular.toJson(next));
    });
});

/**
 * Routes for all navbar links
 */
angular.module('jasify').config(['$routeProvider',
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

            when('/profile-logins', {
                templateUrl: 'views/profile-logins.html',
                controller: 'ProfileLoginsCtrl',
                resolve: {
                    logins: function ($q, Allow, UserLogin, Session) {
                        return Allow.user().then(
                            function () {
                                return UserLogin.list(Session.userId);
                            },
                            function (reason) {
                                return $q.reject(reason);
                            }
                        );
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
angular.module('jasify').constant('AUTH_EVENTS', {
    loginSuccess: 'auth-login-success',
    loginFailed: 'auth-login-failed',
    logoutSuccess: 'auth-logout-success',
    sessionTimeout: 'auth-session-timeout',
    notAuthenticated: 'auth-not-authenticated',
    notAuthorized: 'auth-not-authorized',
    notGuest: 'auth-not-guest'
});


/**
 * To follow the "Angular way", instead of accessing gapi.client directly,
 * we provide the $gapi service and use it instead.  This allows us to
 * easily mock it for tests for example.
 */
angular.module('jasify').provider('$gapi', function $GapiProvider() {
    this.$get = function () {
        return gapi;
    };
});

/**
 *  Session is a singleton that mimics the server-side session
 */
angular.module('jasify').service('Session', function () {

    this.create = function (sessionId, userId, admin) {
        this.id = sessionId;
        this.userId = userId;
        this.admin = admin === true;
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
 * Endpoint service, provide the glue between AngularJs and gapi (or $gapi).
 * It registers a 'global' function on $window that is called when the gapi client load
 * is finished.  You can call 'Endpoint.load()' to get a promise that get resolved when
 * the gapi client is loaded.  After that you can use either $gapi directly, or you can
 * use Endpoint to get the api.
 */
angular.module('jasify').factory('Endpoint', ['$log', '$q', '$window', '$gapi',
    function ($log, $q, $window, $gapi) {

        /**
         * Function to initialize google cloud endpoints
         */
        $window.endpointInitialize = function () {
            Endpoint.init();
        };

        var Endpoint = {
            loaded: false,
            promise: null,
            deferred: null,
            failed: false,
            settings: null
        };

        Endpoint.errorHandler = function (resp) {
            return $q.reject(resp);
        };

        /**
         * Call a function with the jasify api
         */
        Endpoint.jasify = function (fn) {
            return Endpoint.load().then(function () {
                return $q.when(fn($gapi.client.jasify));
            }, Endpoint.errorHandler);
        };


        /**
         * Get a promise that gets resolved when the endpoint is loaded
         */
        Endpoint.load = function () {
            if (Endpoint.loaded) {
                var deferred = $q.defer();
                if (Endpoint.failed) {
                    deferred.reject('Loading failed');
                } else {
                    deferred.resolve('already loaded');
                }
                return deferred.promise;
            }
            if (Endpoint.promise !== null) {
                return $q.when(Endpoint.promise); //loading
            }
            Endpoint.deferred = $q.defer();
            Endpoint.promise = Endpoint.deferred.promise;
            return Endpoint.promise;
        };

        Endpoint.init = function () {
            $log.debug('Endpoint.init');
            if (Endpoint.promise === null) {
                Endpoint.load(); //create promise
            }
            $gapi.client.load('jasify', 'v1', null, '/_ah/api').then(
                Endpoint.jasifyLoaded,
                function (r) {
                    $log.warn('Failed to load api: ' + r);
                    Endpoint.loaded = true;
                    Endpoint.failed = true;
                    Endpoint.promise = null;
                    Endpoint.deferred.reject('failed');
                    Endpoint.deferred = null;

                });
        };

        Endpoint.jasifyLoaded = function () {
            Endpoint.loaded = true;
            Endpoint.promise = null;
            if (Endpoint.deferred) Endpoint.deferred.resolve('loaded');
            Endpoint.deferred = null;
            $log.debug('Endpoint.initialized');
        };

        return Endpoint;
    }]);


/**
 * Auth service
 */
angular.module('jasify').factory('Auth', ['$log', '$http', '$q', '$cookies', 'Session', 'Endpoint',
    function ($log, $http, $q, $cookies, Session, Endpoint) {

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
            return Endpoint.jasify(function (jasify) {
                return jasify.auth.login({
                    username: credentials.name,
                    password: credentials.password
                });
            }).then(function (resp) {
                $log.info("Logged in! (userId=" + resp.result.userId + ")");
                Session.create(resp.result.sessionId, resp.result.userId, resp.result.admin);
                $cookies.loggedIn = true;
                return resp.result;
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

                    if (restore.promise !== null) {
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
            return Endpoint.jasify(function (jasify) {
                return jasify.auth.changePassword({
                    userId: credentials.id,
                    oldPassword: credentials.password,
                    newPassword: newPassword
                });
            });
        };

        Auth.logout = function () {
            $log.info("Logging out (" + Session.userId + ")!");
            return Endpoint.jasify(function (jasify) {
                return jasify.auth.logout();
            }).then(function (res) {
                    $log.info("Logged out!");
                    Session.destroy();
                    $cookies.loggedIn = false;
                },
                function (message) {
                    $log.warn("F: " + message);
                    return $q.reject(message);
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
angular.module('jasify').factory('Allow', ['$log', '$q', '$rootScope', 'Auth', 'AUTH_EVENTS',
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


angular.module('jasify').factory('Username', ['$log', 'Endpoint',
    function ($log, Endpoint) {
        var Username = {};

        Username.check = function (name) {
            return Endpoint.jasify(function (jasify) {
                return jasify.username.check({username: name});
            });
        };

        return Username;
    }]);

/**
 * User service
 */
angular.module('jasify').factory('User', ['$resource', '$log', function ($resource, $log) {
    return $resource('/user/:id', {id: '@id'});
    /*        {
     'query': {method: 'GET', isArray: true}
     });
     */
}]);

/**
 * UserLogins service
 */
angular.module('jasify').factory('UserLogin', ['$q', 'Endpoint', function ($q, Endpoint) {
    var UserLogin = {};

    UserLogin.list = function (userId) {
        return Endpoint.jasify(function (jasify) {
            return jasify.userLogins.list({userId: userId});
        }).then(
            function (resp) {
                return resp.result.items;
            },
            Endpoint.errorHandler);
    };

    UserLogin.remove = function (login) {
        return Endpoint.jasify(function (jasify) {
            return jasify.userLogins.remove({loginId: login.id});
        }).then(
            function (resp) {
                return true;
            },
            Endpoint.errorHandler);
    };
    return UserLogin;
}]);

/**
 * Popup services (windows)
 * Inspired by satelizer (https://github.com/sahat/satellizer)
 */
angular.module('jasify').factory('Popup', ['$log', '$q', '$interval', '$window', function ($log, $q, $interval, $window) {
    var popupWindow = null;
    var waiting = null;

    var Popup = {};
    var Providers = {
        Google: {},
        Facebook: {height: 269}
    };
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

    Popup.open = function (url, provider) {
        var opts = {};
        if (provider && Providers[provider]) {
            opts = Providers[provider];
        }
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
                    popupWindow.location.pathname.indexOf('/oauth2/callback') === 0) {
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
 * ConfirmField directive
 */
angular.module('jasify').directive('jasConfirmField', function () {
    return {
        require: 'ngModel',
        link: function (scope, elm, attrs, ctrl) {
            scope.$watch(function () {
                    var compareTo = scope.$eval(attrs.jasConfirmField);
                    return compareTo && compareTo.$viewValue;
                },
                function (newValue, oldValue) {
                    if (ctrl.$pristine) {
                        return;
                    }
                    if (ctrl.$modelValue == newValue) {
                        return;
                    }
                    ctrl.$validate();
                });

            ctrl.$validators.jasConfirmField = function (modelValue, viewValue) {
                var compareTo = scope.$eval(attrs.jasConfirmField);
                if (compareTo && compareTo.$modelValue !== null && modelValue != compareTo.$modelValue) {
                    return false;
                }
                return compareTo && compareTo.$modelValue !== null;
            };
        }
    };
});

/**
 * Username directive
 */
angular.module('jasify').directive('jasUsername', ['$q', 'Username', function ($q, Username) {
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

/**
 * Password strength meter
 */
angular.module('jasify').directive('jasPasswordStrength', ['$log', function ($log) {
    return {
        replace: true,
        restrict: 'E' /* A - attribute name, E - element name, C - classname */,
        //require: 'ngModel',
        scope: {
            password: '=password'
        },
        link: function (scope, elm, attrs, ctrl) {

            /*
             * Algorithm that determines pw strength
             * Based on https://github.com/subarroca/ng-password-strength/blob/master/app/scripts/directives/ng-password-strength.js
             * but with no dependency on underscorejs...
             */
            scope.strength = function (pwField) {
                var p = pwField.$viewValue;

                if (!p) return -1;

                var criteria = {pos: {}, neg: {}};
                var points = {pos: {}, neg: {seqLetter: 0, seqNumber: 0, seqSymbol: 0}};
                var tmp,
                    strength = 0,
                    letters = 'abcdefghijklmnopqrstuvwxyz',
                    numbers = '01234567890',
                    symbols = '\\!@#$%&/()=?¿',
                    back,
                    forth,
                    i;

                // Benefits
                criteria.pos.lower = p.match(/[a-z]/g);
                criteria.pos.upper = p.match(/[A-Z]/g);
                criteria.pos.numbers = p.match(/\d/g);
                criteria.pos.symbols = p.match(/[$-/:-?{-~!^_`\[\]]/g);
                criteria.pos.middleNumber = p.slice(1, -1).match(/\d/g);
                criteria.pos.middleSymbol = p.slice(1, -1).match(/[$-/:-?{-~!^_`\[\]]/g);

                points.pos.lower = criteria.pos.lower ? criteria.pos.lower.length : 0;
                points.pos.upper = criteria.pos.upper ? criteria.pos.upper.length : 0;
                points.pos.numbers = criteria.pos.numbers ? criteria.pos.numbers.length : 0;
                points.pos.symbols = criteria.pos.symbols ? criteria.pos.symbols.length : 0;

                var ctx = {points: 0};
                angular.forEach(points.pos, function (value, key) {
                    this.points += Math.min(1, value);
                }, ctx);

                tmp = ctx.points;
                points.pos.numChars = p.length;
                tmp += (points.pos.numChars >= 8) ? 1 : 0;

                points.pos.requirements = (tmp >= 3) ? tmp : 0;
                points.pos.middleNumber = criteria.pos.middleNumber ? criteria.pos.middleNumber.length : 0;
                points.pos.middleSymbol = criteria.pos.middleSymbol ? criteria.pos.middleSymbol.length : 0;

                // Deductions
                criteria.neg.consecLower = p.match(/(?=([a-z]{2}))/g);
                criteria.neg.consecUpper = p.match(/(?=([A-Z]{2}))/g);
                criteria.neg.consecNumbers = p.match(/(?=(\d{2}))/g);
                criteria.neg.onlyNumbers = p.match(/^[0-9]*$/g);
                criteria.neg.onlyLetters = p.match(/^([a-z]|[A-Z])*$/g);

                points.neg.consecLower = criteria.neg.consecLower ? criteria.neg.consecLower.length : 0;
                points.neg.consecUpper = criteria.neg.consecUpper ? criteria.neg.consecUpper.length : 0;
                points.neg.consecNumbers = criteria.neg.consecNumbers ? criteria.neg.consecNumbers.length : 0;

                var reverse = function (input) {
                    var result = "";
                    input = input || "";
                    for (var i = 0; i < input.length; i++) {
                        result = input.charAt(i) + result;
                    }
                    return result;
                };

                // sequential letters (back and forth)
                for (i = 0; i < letters.length - 2; i++) {
                    var p2 = p.toLowerCase();
                    forth = letters.substring(i, parseInt(i + 3));
                    back = reverse(forth);
                    if (p2.indexOf(forth) !== -1 || p2.indexOf(back) !== -1) {
                        points.neg.seqLetter++;
                    }
                }

                // sequential numbers (back and forth)
                for (i = 0; i < numbers.length - 2; i++) {
                    forth = numbers.substring(i, parseInt(i + 3));
                    back = reverse(forth);
                    if (p.indexOf(forth) !== -1 || p.toLowerCase().indexOf(back) !== -1) {
                        points.neg.seqNumber++;
                    }
                }

                // sequential symbols (back and forth)
                for (i = 0; i < symbols.length - 2; i++) {
                    forth = symbols.substring(i, parseInt(i + 3));
                    back = reverse(forth);
                    if (p.indexOf(forth) !== -1 || p.toLowerCase().indexOf(back) !== -1) {
                        points.neg.seqSymbol++;
                    }
                }

                // repeated chars
                var counts = {};
                angular.forEach(p.toLowerCase().split(''), function (v, k) {
                    if (!this[k]) {
                        this[k] = 1;
                    } else {
                        this[k]++;
                    }
                }, counts);

                var total = {count: 0};
                angular.forEach(counts, function (v, k) {
                    if (v > 1) this.count += v;
                }, total);

                points.neg.repeated = total.count;


                // Calculations
                strength += points.pos.numChars * 4;
                if (points.pos.upper) {
                    strength += (points.pos.numChars - points.pos.upper) * 2;
                }
                if (points.pos.lower) {
                    strength += (points.pos.numChars - points.pos.lower) * 2;
                }
                if (points.pos.upper || points.pos.lower) {
                    strength += points.pos.numbers * 4;
                }
                strength += points.pos.symbols * 6;
                strength += (points.pos.middleSymbol + points.pos.middleNumber) * 2;
                strength += points.pos.requirements * 2;

                strength -= points.neg.consecLower * 2;
                strength -= points.neg.consecUpper * 2;
                strength -= points.neg.consecNumbers * 2;
                strength -= points.neg.seqNumber * 3;
                strength -= points.neg.seqLetter * 3;
                strength -= points.neg.seqSymbol * 3;

                if (criteria.neg.onlyNumbers) {
                    strength -= points.pos.numChars;
                }
                if (criteria.neg.onlyLetters) {
                    strength -= points.pos.numChars;
                }
                if (points.neg.repeated) {
                    strength -= (points.neg.repeated / points.pos.numChars) * 10;
                }

                return Math.max(5, Math.min(100, Math.round(strength)));
            };

            scope.style = function (p) {
                return {width: scope.strength(p) + '%'};
            };

            scope.css = function (p) {
                var s = scope.strength(p);
                if (s <= 15) {
                    return ['progress-bar', 'progress-bar-danger'];
                } else if (s <= 40) {
                    return ['progress-bar', 'progress-bar-warning'];
                } else {
                    return ['progress-bar', 'progress-bar-success'];
                }
            };

        },
        templateUrl: 'views/directive/password-strength.html'
    };
}]);