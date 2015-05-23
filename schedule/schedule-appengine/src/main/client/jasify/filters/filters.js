(function (angular) {

    angular.module('jasify.filters', [])
        .filter('tableShortDateTime', tableShortDateTime)
        .filter('calendarLong', calendarLong);

    function calendarLong($filter) {
        return function (value) {
            var mValue = moment(value).format(),
                weekBefore = moment().add('day', -6).format(),
                weekAfter = moment().add('day', 6).format();

            if (mValue > weekAfter || mValue < weekBefore) {
                return $filter('amDateFormat')(value, 'L LT');
            }
            return $filter('amCalendar')(value);
        };
    }

    function tableShortDateTime($filter) {
        return function (value) {
            var from = value[0],
                to = value[1],
                fromDate = $filter('date')(from, 'EEE, d MMM'),
                toDate = $filter('date')(to, 'EEE, d MMM'),
                fromTime = $filter('date')(from, 'h:mm a'),
                toTime = $filter('date')(to, 'h:mm a');

            if (fromDate === toDate) {
                return fromDate + ' ' + fromTime + ' - ' + toTime;
            }
            return fromDate + ' ' + fromTime + ' - ' + toDate + ' ' + toTime;
        };
    }

})(angular);