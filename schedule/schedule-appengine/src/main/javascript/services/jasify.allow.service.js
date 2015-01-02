(function (ng) {

    /**
     * Allow - used in Route resolve promises as Allow.all for example
     */
    ng.module('jasify').factory('Allow', allow);

    function allow($log, $q, $rootScope, Auth, AUTH_EVENTS) {
        var Allow = {
            all: all,
            restoreThen: restoreThen,
            guest: guest,
            user: user,
            admin: admin
        };

        function all() {
            var deferred = $q.defer();
            deferred.resolve('ok');
            return deferred.promise;
        }

        function restoreThen(fn) {
            return Auth.restore().then(function (u) {
                    return fn();
                },
                function (data) {
                    return fn();
                });
        }

        function guest() {
            return Allow.restoreThen(function () {
                if (Auth.isAuthenticated()) {
                    $rootScope.$broadcast(AUTH_EVENTS.notGuest);
                    return $q.reject('guests only');
                } else {
                    return true;
                }
            });
        }

        function user() {
            return Allow.restoreThen(function () {
                if (!Auth.isAuthenticated()) {
                    $rootScope.$broadcast(AUTH_EVENTS.notAuthenticated);
                    return $q.reject('users only');
                } else {
                    return true;
                }
            });
        }

        function admin() {
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
        }

        return Allow;
    }

})(angular);