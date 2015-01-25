(function (angular) {
    angular.module('jasifyComponents').factory('Group', group);

    function group(Endpoint, $q, $log) {
        var Group = {
            query: query,
            get: get,
            update: update,
            add: add,
            remove: remove,
            users: users,
            addUser: addUser,
            removeUser: removeUser
        };

        function query() {
            return Endpoint.jasify(function (jasify) {
                return jasify.groups.query()
                    .then(resultHandler, errorHandler);
            });
        }

        function get(id) {
            return Endpoint.jasify(function (jasify) {
                return jasify.groups.get({id: id})
                    .then(resultHandler, errorHandler);
            });
        }

        function update(group) {
            return Endpoint.jasify(function (jasify) {
                return jasify.groups.update(group)
                    .then(resultHandler, errorHandler);
            });
        }

        function add(group) {
            return Endpoint.jasify(function (jasify) {
                return jasify.groups.add(group)
                    .then(resultHandler, errorHandler);
            });
        }

        function users(id) {
            return Endpoint.jasify(function (jasify) {
                return jasify.groups.users({id: id})
                    .then(resultHandler, errorHandler);
            });
        }

        function remove(id) {
            return Endpoint.jasify(function (jasify) {
                return jasify.groups.remove({id: id})
                    .then(resultHandler, errorHandler);
            });
        }

        function addUser(group, user) {
            var groupId = angular.isObject(group) ? group.id : group;
            var userId = angular.isObject(user) ? user.id : user;

            return Endpoint.jasify(function (jasify) {
                return jasify.groups.addUser({groupId: groupId, userId: userId})
                    .then(resultHandler, errorHandler);
            });
        }

        function removeUser(group, user) {
            var groupId = angular.isObject(group) ? group.id : group;
            var userIds = [];
            if (!angular.isArray(user)) user = [user];

            for (var x = 0; x < user.length; ++x) {
                var userId = angular.isObject(user[x]) ? user[x].id : user[x];
                userIds.push(userId);
            }

            var promises = [];
            for (var i = 0; i < userIds.length; ++i) {
                promises.push(removeOneUser(groupId, userIds[i]));
            }

            function removeOneUser(groupId, userId) {
                return Endpoint.jasify(function (jasify) {
                    return jasify.groups.removeUser({groupId: groupId, userId: userId})
                        .then(resultHandler, errorHandler);
                });
            }

            if (promises.length == 1) return promises[0];
            return $q.all(promises);
        }


        function errorHandler(e) {
            return $q.reject(e);
        }

        function resultHandler(resp) {
            return resp.result;
        }

        return Group;
    }
})(angular);