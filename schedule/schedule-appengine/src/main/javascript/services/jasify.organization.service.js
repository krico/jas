(function (angular) {
    angular.module('jasify').factory('Organization', organization);

    function organization(Endpoint, $q) {
        var Organization = {
            query: query,
            get: get,
            update: update,
            add: add,
            users: users,
            groups: groups,
            addUser: addUser,
            remove: remove
        };

        function query() {
            return Endpoint.jasify(function (jasify) {
                return jasify.organizations.query()
                    .then(resultHandler, errorHandler);
            });
        }

        function get(id) {
            return Endpoint.jasify(function (jasify) {
                return jasify.organizations.get({id: id})
                    .then(resultHandler, errorHandler);
            });
        }

        function update(organization) {
            return Endpoint.jasify(function (jasify) {
                return jasify.organizations.update(organization)
                    .then(resultHandler, errorHandler);
            });
        }

        function addUser(organization, user) {
            return Endpoint.jasify(function (jasify) {
                return jasify.organizations.addUser({organization: organization, user: user})
                    .then(resultHandler, errorHandler);
            });
        }

        function add(organization) {
            return Endpoint.jasify(function (jasify) {
                return jasify.organizations.add(organization)
                    .then(resultHandler, errorHandler);
            });
        }

        function users(id) {
            return Endpoint.jasify(function (jasify) {
                return jasify.organizations.users({id: id})
                    .then(resultHandler, errorHandler);
            });
        }

        function groups(id) {
            return Endpoint.jasify(function (jasify) {
                return jasify.organizations.groups({id: id})
                    .then(resultHandler, errorHandler);
            });
        }

        function remove(id) {
            return Endpoint.jasify(function (jasify) {
                return jasify.organizations.remove({id: id})
                    .then(resultHandler, errorHandler);
            });
        }


        function errorHandler(e) {
            return $q.reject(e);
        }

        function resultHandler(resp) {
            return resp.result;
        }

        return Organization;
    }

})(angular);