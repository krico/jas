(function (angular) {

    angular.module('jasifyComponents').factory('User', user);

    function user(Endpoint, $q) {
        var User = {
            query: query,
            get: get,
            update: update,
            add: add
        };

        /**
         * {offset, limit, query, field, orderBy, order
         * @param params
         */
        function query(params) {
            return Endpoint.jasify(function (jasify) {
                return jasify.users.query(params)
                    .then(Endpoint.resultHandler, Endpoint.rejectHandler);
            });
        }

        function get(id) {
            return Endpoint.jasify(function (jasify) {
                return jasify.users.get({id: id})
                    .then(Endpoint.resultHandler, Endpoint.rejectHandler);
            });
        }

        function update(user) {
            return Endpoint.jasify(function (jasify) {
                return jasify.users.update(user)
                    .then(Endpoint.resultHandler, Endpoint.rejectHandler);
            });
        }

        function add(user, password) {
            return Endpoint.jasify(function (jasify) {
                return jasify.users.add({password: password, user: user})
                    .then(Endpoint.resultHandler, Endpoint.rejectHandler);
            });
        }

        return User;
    }

})(angular);