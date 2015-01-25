(function (angular) {
    angular.module('jasifyComponents').factory('ActivityType', activityType);

    function activityType(Endpoint, $q, $log) {
        var ActivityType = {
            query: query,
            get: get,
            update: update,
            add: add,
            remove: remove
        };

        function query(organizationId) {
            return Endpoint.jasify(function (jasify) {
                return jasify.activityTypes.query({organizationId: fetchId(organizationId)})
                    .then(resultHandler, errorHandler);
            });
        }

        function get(id) {
            return Endpoint.jasify(function (jasify) {
                return jasify.activityTypes.get({id: id})
                    .then(resultHandler, errorHandler);
            });
        }

        function update(activityType) {
            return Endpoint.jasify(function (jasify) {
                return jasify.activityTypes.update(activityType)
                    .then(resultHandler, errorHandler);
            });
        }

        function add(organizationId, activityType) {
            return Endpoint.jasify(function (jasify) {
                return jasify.activityTypes.add({organizationId: fetchId(organizationId), activityType: activityType})
                    .then(resultHandler, errorHandler);
            });
        }

        function remove(id) {
            return Endpoint.jasify(function (jasify) {
                return jasify.activityTypes.remove({id: fetchId(id)})
                    .then(resultHandler, errorHandler);
            });
        }

        function errorHandler(e) {
            return $q.reject(e);
        }

        function resultHandler(resp) {
            return resp.result;
        }

        function fetchId(o) {
            if (angular.isObject(o)) return o.id;
            return o;
        }

        return ActivityType;
    }
})(angular);