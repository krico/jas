(function (angular) {
    angular.module('jasifyComponents').factory('Activity', activity);

    function activity(Endpoint, $q, $log) {
        var Activity = {
            query: query,
            get: get,
            update: update,
            add: add,
            remove: remove,
            subscribe: subscribe,
            isSubscribed: isSubscribed
        };

        function query(param) {
            return Endpoint.jasify(function (jasify) {
                return jasify.activities.query(param)
                    .then(resultHandler, errorHandler);
            });
        }

        function get(id) {
            return Endpoint.jasify(function (jasify) {
                return jasify.activities.get({id: id})
                    .then(resultHandler, errorHandler);
            });
        }

        function update(activity) {
            return Endpoint.jasify(function (jasify) {
                return jasify.activities.update(activity)
                    .then(resultHandler, errorHandler);
            });
        }

        function add(activity, repeatDetails) {
            var req = {activity: activity};
            if(repeatDetails) {
                req.repeatDetails = repeatDetails;
            }

            return Endpoint.jasify(function (jasify) {
                return jasify.activities.add(req)
                    .then(resultHandler, errorHandler);
            });
        }

        function remove(id) {
            return Endpoint.jasify(function (jasify) {
                return jasify.activities.remove({id: fetchId(id)})
                    .then(resultHandler, errorHandler);
            });
        }

        function subscribe(user, activity) {
            return Endpoint.jasify(function (jasify) {
                return jasify.activitySubscriptions.add({userId: fetchId(user), activityId: fetchId(activity)})
                    .then(resultHandler, errorHandler);
            });
        }

        function isSubscribed(user, activity) {
            return Endpoint.jasify(function (jasify) {
                return jasify.activitySubscriptions.query({userId: fetchId(user), activityId: fetchId(activity)})
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

        return Activity;
    }
})(angular);