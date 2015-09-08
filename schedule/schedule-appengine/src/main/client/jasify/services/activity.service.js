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
            isSubscribed: isSubscribed,
            getSubscribers: getSubscribers,
            cancelSubscription: cancelSubscription,
            getUserSubscriptions: getUserSubscriptions
        };

        function query(param) {
            return Endpoint.jasify(function (jasify) {
                return jasify.activities.query(param)
                    .then(Endpoint.itemsResultHandler, Endpoint.rejectHandler);
            });
        }

        function get(id) {
            return Endpoint.jasify(function (jasify) {
                return jasify.activities.get({id: id})
                    .then(Endpoint.resultHandler, Endpoint.rejectHandler);
            });
        }

        function update(activity) {
            return Endpoint.jasify(function (jasify) {
                return jasify.activities.update(activity)
                    .then(Endpoint.resultHandler, Endpoint.rejectHandler);
            });
        }

        function add(activity, repeatDetails) {
            var req = {activity: activity};
            if (repeatDetails) {
                req.repeatDetails = repeatDetails;
            }

            return Endpoint.jasify(function (jasify) {
                return jasify.activities.add(req)
                    .then(Endpoint.itemsResultHandler, Endpoint.rejectHandler);
            });
        }

        function remove(id) {
            return Endpoint.jasify(function (jasify) {
                return jasify.activities.remove({id: Endpoint.fetchId(id)})
                    .then(Endpoint.resultHandler, Endpoint.rejectHandler);
            });
        }

        function subscribe(user, activity) {
            return Endpoint.jasify(function (jasify) {
                return jasify.activitySubscriptions.add({
                    userId: Endpoint.fetchId(user),
                    activityId: Endpoint.fetchId(activity)
                })
                    .then(Endpoint.resultHandler, Endpoint.rejectHandler);
            });
        }

        function isSubscribed(user, activity) {
            return Endpoint.jasify(function (jasify) {
                return jasify.activitySubscriptions.query({
                    userId: Endpoint.fetchId(user),
                    activityId: Endpoint.fetchId(activity)
                })
                    .then(Endpoint.resultHandler, Endpoint.rejectHandler);
            });
        }

        function getSubscribers(id) {
            return Endpoint.jasify(function (jasify) {
                return jasify.activitySubscriptions.subscribers({activityId: id})
                    .then(Endpoint.itemsResultHandler, Endpoint.rejectHandler);
            });
        }

        function cancelSubscription(id) {
            return Endpoint.jasify(function (jasify) {
                return jasify.activitySubscriptions.cancel({subscriptionId: id})
                    .then(Endpoint.resultHandler, Endpoint.rejectHandler);
            });
        }

        function getUserSubscriptions(id, fromDate, toDate) {
            return Endpoint.jasify(function (jasify) {
                return jasify.activitySubscriptions.getForUser({userId: id, fromDate: fromDate, toDate: toDate})
                    .then(Endpoint.resultHandler, Endpoint.rejectHandler);
            });
        }

        return Activity;
    }
})(angular);