(function (angular) {
    /**
     * UserLogins service
     */
    angular.module('jasifyComponents').factory('UserLogin', userLogin);

    function userLogin($q, Endpoint) {
        var UserLogin = {
            list: list,
            remove: remove
        };

        function list(userId) {
            return Endpoint.jasify(function (jasify) {
                return jasify.userLogins.list({userId: userId});
            }).then(
                function (resp) {
                    resp = resp || {};
                    resp.result = resp.result || {};
                    resp.result.items = resp.result.items || [];
                    return resp.result.items;
                },
                Endpoint.rejectHandler);
        }

        function remove(login) {
            return Endpoint.jasify(function (jasify) {
                return jasify.userLogins.remove({loginId: login.id});
            }).then(
                function (resp) {
                    return true;
                },
                Endpoint.rejectHandler);
        }

        return UserLogin;
    }
})(angular);