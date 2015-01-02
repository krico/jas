(function (ng) {
    /**
     * UserLogins service
     */
    ng.module('jasify').factory('UserLogin', userLogin);

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
                    return resp.result.items;
                },
                Endpoint.errorHandler);
        }

        function remove(login) {
            return Endpoint.jasify(function (jasify) {
                return jasify.userLogins.remove({loginId: login.id});
            }).then(
                function (resp) {
                    return true;
                },
                Endpoint.errorHandler);
        }

        return UserLogin;
    }
})(angular);