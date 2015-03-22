(function (angular) {

    angular.module('jasifyComponents').factory('Auth', auth);

    function auth($log, $http, $q, $location, Session, Endpoint, BrowserData) {
        var Auth = {
            isAuthenticated: isAuthenticated,
            isAdmin: isAdmin,
            login: login,
            restore: restore,
            changePassword: changePassword,
            providerAuthorize: providerAuthorize,
            providerAuthenticate: providerAuthenticate,
            forgotPassword: forgotPassword,
            recoverPassword: recoverPassword,
            logout: logout
        };

        var restoreData = {
            invoked: false,
            failed: false,
            promise: null,
            data: null
        };

        // Try to restore early (don't remember why anymore)
        restoreOnInstantiation();


        function isAuthenticated() {
            return !!Session.id;
        }

        function isAdmin() {
            return Session.admin;
        }


        function login(credentials) {
            $log.info("Logging in (name=" + credentials.name + ") ...");
            return Endpoint.jasify(function (jasify) {
                return jasify.auth.login({
                    username: credentials.name,
                    password: credentials.password
                });
            }).then(function (resp) {
                $log.info("Logged in! (userId=" + resp.result.userId + ")");
                Session.create(resp.result.sessionId, resp.result.userId, resp.result.admin);
                BrowserData.setLoggedIn(true);
                BrowserData.setFirstAccess(false);
                return resp.result;
            });
        }

        function restore(force) {
            if (force) {
                restoreData.invoked = false;
                restoreData.failed = false;
                restoreData.promise = null;
                restoreData.data = null;
            } else {
                if (!BrowserData.getLoggedIn()) {
                    restoreData.invoked = true;
                    restoreData.failed = true;
                    restoreData.data = 'Not logged in';
                }

                if (restoreData.invoked) {
                    //This is a cache of the last restore call, we make it look like it was called again

                    if (restoreData.promise !== null) {
                        //In case the http request is pending
                        return $q.when(restoreData.promise);
                    }

                    var deferred = $q.defer();

                    if (restoreData.failed) {
                        deferred.reject(restoreData.data);
                    } else {
                        deferred.resolve(restoreData.data);
                    }

                    return deferred.promise;
                }
            }
            restoreData.invoked = true;

            var p;
            if (force && (force.id || force.sessionId) && force.userId) {
                $log.debug("Restoring session (local)...");
                p = $q.when({result: force});
            } else {
                $log.debug("Restoring session...");
                p = Endpoint.jasify(function (jasify) {
                    return jasify.auth.restore();
                });
            }

            restoreData.promise = p.then(function (res) {
                    $log.info("Session restored! (userId=" + res.result.user.numericId + ")");
                    restoreData.promise = null;

                    var sessionId = res.result.id || res.result.sessionId;
                    Session.create(sessionId, res.result.userId, res.result.user.admin);
                    BrowserData.setFirstAccess(false);
                    BrowserData.setLoggedIn(true);
                    restoreData.data = res.result.user;

                    return restoreData.data;
                },
                function (reason) {
                    $log.info("Session restore failed: (" + reason.status + ') ' + reason.statusText);
                    restoreData.promise = null;
                    restoreData.failed = true;
                    restoreData.data = reason;
                    return $q.reject(restoreData.data);
                }
            );

            return restoreData.promise;
        }

        function changePassword(credentials, newPassword) {
            $log.info("Changing password (userId=" + Session.userId + ")!");
            return Endpoint.jasify(function (jasify) {
                return jasify.auth.changePassword({
                    userId: credentials.id,
                    oldPassword: credentials.password,
                    newPassword: newPassword
                });
            });
        }

        function logout() {
            $log.info("Logging out (" + Session.userId + ")!");
            return Endpoint.jasify(function (jasify) {
                return jasify.auth.logout();
            }).then(ok, fail);

            function ok(res) {
                $log.info("Logged out!");
                Session.destroy();
                BrowserData.setLoggedIn(false);
            }

            function fail(message) {
                $log.warn("F: " + message);
                return $q.reject(message);
            }
        }

        function providerAuthorize(provider) {
            var absUrl = $location.absUrl();
            var ix = absUrl.indexOf('#');
            var baseUrl;
            if (ix == -1) {
                baseUrl = absUrl;
            } else {
                baseUrl = absUrl.substring(0, ix);
            }
            var path = $location.path();

            return Endpoint.jasify(function (jasify) {
                return jasify.auth.providerAuthorize({
                    provider: provider,
                    baseUrl: baseUrl,
                    data: path
                });
            });
        }

        function providerAuthenticate(callbackUrl) {
            return Endpoint.jasify(function (jasify) {
                return jasify.auth.providerAuthenticate({
                    callbackUrl: callbackUrl
                });
            });
        }

        function forgotPassword(email) {
            //This is for the e-mail to know which site it come from
            //Could be www.jasify.com or www.agenda.com.br
            var siteUrl = $location.absUrl();

            return Endpoint.jasify(function (jasify) {
                return jasify.auth.forgotPassword({
                    email: email,
                    url: siteUrl
                });
            });
        }

        function recoverPassword(code, password) {
            return Endpoint.jasify(function (jasify) {
                return jasify.auth.recoverPassword({
                    code: code,
                    newPassword: password
                });
            });
        }

        function restoreOnInstantiation() {
            if (BrowserData.getLoggedIn()) {
                Auth.restore().then(function (u) {
                        BrowserData.setLoggedIn(true);
                        BrowserData.setFirstAccess(false);
                    },
                    function (data) {
                        BrowserData.setLoggedIn(false);
                    });
            }
        }

        return Auth;
    }

})(angular);