(function (angular) {

    angular.module('jasify', [
        'ngRoute',
        'ngResource',
        'ngMessages',
        'ngCookies',
        'ui.bootstrap',
        'angularSpinner',
        'jasifyScheduleControllers',
        'ui.bootstrap.datetimepicker',
        'templates'
    ]);

    /**
     * Listen to route changes and check
     */
    angular.module('jasify').run(jasifyRun);

    function jasifyRun($rootScope, $log) {
        //TODO: remove, not really needed
        $rootScope.$on('$routeChangeError', function (event, next, current) {
            $log.debug('$routeChangeError, event=' + angular.toJson(event) + ' next=' + angular.toJson(next));
        });
    }

})(angular);