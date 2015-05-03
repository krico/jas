(function (angular) {
    angular.module('jasifyComponents').factory('ActivityPackage', activityPackage);

    function activityPackage(Endpoint, $q, $log) {
        var ActivityPackage = {
            query: query,
            get: get,
            update: update,
            add: add,
            addActivity: addActivity,
            removeActivity: removeActivity,
            remove: remove
        };

        return ActivityPackage;

        function query(organizationId) {
            return Endpoint.jasify(function (jasify) {
                return jasify.activityPackages.query({organizationId: fetchId(organizationId)})
                    .then(resultHandler, errorHandler);
            });
        }

        function get(id) {
            return Endpoint.jasify(function (jasify) {
                return jasify.activityPackages.get({id: id})
                    .then(resultHandler, errorHandler);
            });
        }

        function update(activityPackage) {
            return Endpoint.jasify(function (jasify) {
                return jasify.activityPackages.update(activityPackage)
                    .then(resultHandler, errorHandler);
            });
        }

        function add(activityPackage, activities) {
            return Endpoint.jasify(function (jasify) {
                return jasify.activityPackages.add({
                    activityPackage: activityPackage,
                    activities: activities
                }).then(resultHandler, errorHandler);
            });
        }

        function addActivity(activityPackage, activity) {
            return Endpoint.jasify(function (jasify) {
                return jasify.activityPackages.addActivity({
                    activityPackageId: fetchId(activityPackage),
                    activityId: fetchId(activity)
                }).then(resultHandler, errorHandler);
            });
        }

        function removeActivity(activityPackage, activity) {
            return Endpoint.jasify(function (jasify) {
                return jasify.activityPackages.removeActivity({
                    activityPackageId: fetchId(activityPackage),
                    activityId: fetchId(activity)
                }).then(resultHandler, errorHandler);
            });
        }

        function remove(id) {
            return Endpoint.jasify(function (jasify) {
                return jasify.activityPackages.remove({id: fetchId(id)})
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

    }
})(angular);