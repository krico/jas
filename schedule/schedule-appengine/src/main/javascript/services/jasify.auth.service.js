(function () {


    /**
     * Auth service
     */
    angular.module('jasify').factory('Auth', auth);

    function auth($log, $http, $q, $cookies, Session, Endpoint) {

        var Auth = {
            isAuthenticated: isAuthenticated,
            isAdmin: isAdmin,
            login: login,
            restore: restore,
            changePassword: changePassword,
            logout: logout
        };

        var restoreData = {
            invoked: false,
            failed: false,
            promise: null,
            data: null
        };


        function loggedIn(res) {
            Session.create(res.data.id, res.data.userId, res.data.user.admin);
            $cookies.loggedIn = true;
            return res.data.user;
        }

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
                $cookies.loggedIn = true;
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
                if (!$cookies.loggedIn) {
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

            $log.debug("Restoring session...");
            restoreData.promise = $http.get('/auth/restore')
                .then(function (res) {
                    $log.info("Session restored! (userId=" + res.data.userId + ")");
                    restoreData.promise = null;
                    restoreData.data = loggedIn(res);
                    return restoreData.data;
                },
                function (reason) {
                    $log.info("Session restore failed: " + reason);
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
            }).then(function (res) {
                    $log.info("Logged out!");
                    Session.destroy();
                    $cookies.loggedIn = false;
                },
                function (message) {
                    $log.warn("F: " + message);
                    return $q.reject(message);
                });
        }


        if ($cookies.loggedIn) {
            Auth.restore().then(function (u) {
                    $cookies.loggedIn = true;
                },
                function (data) {
                    $cookies.loggedIn = false;
                });
        }

        return Auth;
    }

})();