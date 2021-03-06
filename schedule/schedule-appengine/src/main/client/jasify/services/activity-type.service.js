(function (angular) {
    angular.module('jasifyComponents').factory('ActivityType', activityType);

    function activityType(Endpoint) {
        var ActivityType = {
            query: query,
            get: get,
            update: update,
            add: add,
            remove: remove
        };

        function query(organizationId) {
            return Endpoint.jasify(function (jasify) {
                return jasify.activityTypes.query({organizationId: Endpoint.fetchId(organizationId)})
                    .then(Endpoint.itemsResultHandler, Endpoint.rejectHandler);
            });
        }

        function get(id) {
            return Endpoint.jasify(function (jasify) {
                return jasify.activityTypes.get({id: id})
                    .then(Endpoint.resultHandler, Endpoint.rejectHandler);
            });
        }

        function update(activityType) {
            return Endpoint.jasify(function (jasify) {
                return jasify.activityTypes.update(activityType)
                    .then(Endpoint.resultHandler, Endpoint.rejectHandler);
            });
        }

        function add(organizationId, activityType) {
            return Endpoint.jasify(function (jasify) {
                return jasify.activityTypes.add({
                    organizationId: Endpoint.fetchId(organizationId),
                    activityType: activityType
                }).then(Endpoint.resultHandler, Endpoint.rejectHandler);
            });
        }

        function remove(id) {
            return Endpoint.jasify(function (jasify) {
                return jasify.activityTypes.remove({id: Endpoint.fetchId(id)})
                    .then(Endpoint.resultHandler, Endpoint.rejectHandler);
            });
        }

        return ActivityType;
    }
})(angular);