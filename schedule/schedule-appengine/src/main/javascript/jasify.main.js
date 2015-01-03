(function (ng) {

    ng
        .module('jasify', [
            'ngRoute',
            'ngResource',
            'ngMessages',
            'ngCookies',
            'ui.bootstrap',
            'angularSpinner',
            'jasifyScheduleControllers'
        ]);

    /**
     * Listen to route changes and check
     */
    ng.module('jasify').run(jasifyRun);

    function jasifyRun($rootScope, $log, AUTH_EVENTS, Auth) {
        //TODO: remove, not really needed
        $rootScope.$on('$routeChangeError', function (event, next, current) {
            $log.debug('$routeChangeError, event=' + ng.toJson(event) + ' next=' + ng.toJson(next));
        });
    }

})(angular);