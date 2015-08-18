(function (angular) {
    angular.module('jasifyComponents').factory('History', history);

    function history(Endpoint, $q, $log) {
        var History = {
            query: query
        };

        function query(fromDate, toDate) {
            return Endpoint.jasify(function (jasify) {
                return jasify.histories.query({fromDate: fromDate, toDate: toDate})
                    .then(Endpoint.itemsResultHandler, Endpoint.rejectHandler);
            });
        }

        return History;
    }
})(angular);