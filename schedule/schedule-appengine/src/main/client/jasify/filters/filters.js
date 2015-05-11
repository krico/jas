(function (angular) {

    angular.module('jasify.filters', [])
        .filter('tableShortDateTime', tableShortDateTime);

    function tableShortDateTime($filter) {
        return function(value) {
            var from = value[0],
                to = value[1],
                fromDate = $filter('date')(from, 'EEE, d MMM'),
                toDate = $filter('date')(to, 'EEE, d MMM'),
                fromTime = $filter('date')(from, 'h:mm a'),
                toTime = $filter('date')(to, 'h:mm a');

            if (fromDate === toDate) {
                return fromDate + ' ' + fromTime + ' - ' + toTime;
            } else {
                return fromDate + ' ' + fromTime + ' - ' + toDate + ' ' + toTime;
            }
        };
    }
})(angular);