(function (angular) {

    angular.module('jasifyComponents').factory('User', user);

    function user(Endpoint, $q) {
        var User = {
            query: query,
            get: get,
            update: update,
            add: add
        };

        function errorHandler(e) {
            return $q.reject(e);
        }

        function resultHandler(resp) {
            return resp.result;
        }

        /**
         * {offset, limit, query, field, orderBy, order
         * @param params
         */
        function query(params) {
            return Endpoint.jasify(function (jasify) {
                return jasify.users.query(params)
                    .then(resultHandler, errorHandler);
            });
        }

        function get(id) {
            return Endpoint.jasify(function (jasify) {
                return jasify.users.get({id: id})
                    .then(resultHandler, errorHandler);
            });
        }

        function update(user) {
            return Endpoint.jasify(function (jasify) {
                return jasify.users.update(user)
                    .then(resultHandler, errorHandler);
            });
        }

        function add(user, password) {
            return Endpoint.jasify(function (jasify) {
                return jasify.users.add({password: password, user: user})
                    .then(resultHandler, errorHandler);
            });
        }

        return User;
    }

})(angular);