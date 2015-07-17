(function (angular) {

    /**
     * Allow - used in Route resolve promises as Allow.all for example
     */
    angular.module('jasifyComponents').factory('Allow', allow);

    function allow($q, $rootScope, Auth, AUTH_EVENTS) {
        var Allow = {
            all: all,
            restoreThen: restoreThen,
            guest: guest,
            user: user,
            admin: admin,
            adminOrOrgMember: adminOrOrgMember
        };

        function all() {
            return Allow.restoreThen(function () {
                return true;
            });
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
                    return $q.reject(AUTH_EVENTS.notGuest);
                } else {
                    return true;
                }
            });
        }

        function user() {
            return Allow.restoreThen(function () {
                if (!Auth.isAuthenticated()) {
                    $rootScope.$broadcast(AUTH_EVENTS.notAuthenticated);
                    return $q.reject(AUTH_EVENTS.notAuthenticated);
                } else {
                    return true;
                }
            });
        }

        function admin() {
            return Allow.restoreThen(function () {
                if (!Auth.isAuthenticated()) {
                    $rootScope.$broadcast(AUTH_EVENTS.notAuthenticated);
                    return $q.reject(AUTH_EVENTS.notAuthenticated);
                } else if (!Auth.isAdmin()) {
                    $rootScope.$broadcast(AUTH_EVENTS.notAuthorized);
                    return $q.reject(AUTH_EVENTS.notAuthorized);
                } else {
                    return true;
                }
            });
        }

        function adminOrOrgMember() {
            return Allow.restoreThen(function () {
                if (!Auth.isAuthenticated()) {
                    $rootScope.$broadcast(AUTH_EVENTS.notAuthenticated);
                    return $q.reject(AUTH_EVENTS.notAuthenticated);
                } else if (!(Auth.isAdmin() || Auth.isOrgMember())) {
                    $rootScope.$broadcast(AUTH_EVENTS.notAuthorized);
                    return $q.reject(AUTH_EVENTS.notAuthorized);
                } else {
                    return true;
                }
            });
        }

        return Allow;
    }

})(angular);