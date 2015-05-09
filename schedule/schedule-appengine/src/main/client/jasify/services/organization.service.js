(function (angular) {
    angular.module('jasifyComponents').factory('Organization', organization);

    function organization(Endpoint, $q, $log) {
        var Organization = {
            query: query,
            queryPublic: queryPublic,
            get: get,
            update: update,
            add: add,
            users: users,
            groups: groups,
            addUser: addUser,
            removeUser: removeUser,
            addGroup: addGroup,
            removeGroup: removeGroup,
            remove: remove
        };

        function query() {
            return Endpoint.jasify(function (jasify) {
                return jasify.organizations.query()
                    .then(resultHandler, errorHandler);
            });
        }

        function queryPublic() {
            return Endpoint.jasify(function (jasify) {
                return jasify.organizations.queryPublic()
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
            var organizationId = angular.isObject(organization) ? organization.id : organization;
            var userId = angular.isObject(user) ? user.id : user;

            return Endpoint.jasify(function (jasify) {
                return jasify.organizations.addUser({organizationId: organizationId, userId: userId})
                    .then(resultHandler, errorHandler);
            });
        }

        function removeUser(organization, user) {
            var organizationId = angular.isObject(organization) ? organization.id : organization;
            var userIds = [];
            if (!angular.isArray(user)) user = [user];

            for (var x = 0; x < user.length; ++x) {
                var userId = angular.isObject(user[x]) ? user[x].id : user[x];
                userIds.push(userId);
            }

            var promises = [];
            for (var i = 0; i < userIds.length; ++i) {
                promises.push(removeOneUser(organizationId, userIds[i]));
            }

            function removeOneUser(organizationId, userId) {
                return Endpoint.jasify(function (jasify) {
                    return jasify.organizations.removeUser({organizationId: organizationId, userId: userId})
                        .then(resultHandler, errorHandler);
                });
            }

            if (promises.length == 1) return promises[0];
            return $q.all(promises);
        }

        function addGroup(organization, group) {
            var organizationId = angular.isObject(organization) ? organization.id : organization;
            var groupId = angular.isObject(group) ? group.id : group;

            return Endpoint.jasify(function (jasify) {
                return jasify.organizations.addGroup({organizationId: organizationId, groupId: groupId})
                    .then(resultHandler, errorHandler);
            });
        }

        function removeGroup(organization, group) {
            var organizationId = angular.isObject(organization) ? organization.id : organization;
            var groupIds = [];
            if (!angular.isArray(group)) group = [group];

            for (var x = 0; x < group.length; ++x) {
                var groupId = angular.isObject(group[x]) ? group[x].id : group[x];
                groupIds.push(groupId);
            }

            var promises = [];
            for (var i = 0; i < groupIds.length; ++i) {
                promises.push(removeOneGroup(organizationId, groupIds[i]));
            }

            function removeOneGroup(organizationId, groupId) {
                return Endpoint.jasify(function (jasify) {
                    return jasify.organizations.removeGroup({organizationId: organizationId, groupId: groupId})
                        .then(resultHandler, errorHandler);
                });
            }

            if (promises.length == 1) return promises[0];
            return $q.all(promises);
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