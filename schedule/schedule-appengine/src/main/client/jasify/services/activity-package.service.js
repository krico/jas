(function (angular) {
    angular.module('jasifyComponents').factory('ActivityPackage', activityPackage);

    function activityPackage(Endpoint) {
        var ActivityPackage = {
            query: query,
            get: get,
            update: update,
            add: add,
            addActivity: addActivity,
            removeActivity: removeActivity,
            getActivities: getActivities,
            remove: remove
        };

        return ActivityPackage;

        function query(organizationId) {
            return Endpoint.jasify(function (jasify) {
                return jasify.activityPackages.query({organizationId: Endpoint.fetchId(organizationId)})
                    .then(Endpoint.itemsResultHandler, Endpoint.rejectHandler);
            });
        }

        function getActivities(activityPackageId) {
            return Endpoint.jasify(function (jasify) {
                return jasify.activityPackages.getActivities({activityPackageId: Endpoint.fetchId(activityPackageId)})
                    .then(resultItemsHandler, Endpoint.rejectHandler);
            });
        }

        function get(id) {
            return Endpoint.jasify(function (jasify) {
                return jasify.activityPackages.get({id: id})
                    .then(Endpoint.resultHandler, Endpoint.rejectHandler);
            });
        }

        function update(activityPackage, activities) {
            return Endpoint.jasify(function (jasify) {
                return jasify.activityPackages.update({
                    id: activityPackage.id,
                    activityPackage: activityPackage,
                    activities: activities
                })
                    .then(Endpoint.resultHandler, Endpoint.rejectHandler);
            });
        }

        function add(activityPackage, activities) {
            return Endpoint.jasify(function (jasify) {
                return jasify.activityPackages.add({
                    activityPackage: activityPackage,
                    activities: activities
                }).then(Endpoint.resultHandler, Endpoint.rejectHandler);
            });
        }

        function addActivity(activityPackage, activity) {
            return Endpoint.jasify(function (jasify) {
                return jasify.activityPackages.addActivity({
                    activityPackageId: Endpoint.fetchId(activityPackage),
                    activityId: Endpoint.fetchId(activity)
                }).then(Endpoint.resultHandler, Endpoint.rejectHandler);
            });
        }

        function removeActivity(activityPackage, activity) {
            return Endpoint.jasify(function (jasify) {
                return jasify.activityPackages.removeActivity({
                    activityPackageId: Endpoint.fetchId(activityPackage),
                    activityId: Endpoint.fetchId(activity)
                }).then(Endpoint.resultHandler, Endpoint.rejectHandler);
            });
        }

        function remove(id) {
            return Endpoint.jasify(function (jasify) {
                return jasify.activityPackages.remove({id: Endpoint.fetchId(id)})
                    .then(Endpoint.resultHandler, Endpoint.rejectHandler);
            });
        }

        function resultItemsHandler(resp) {
            if (resp.result) return resp.result.items;
            return null;
        }

    }
})(angular);