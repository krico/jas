(function () {

    angular
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
    angular.module('jasify').run(jasifyRun);

    function jasifyRun($rootScope, $log, AUTH_EVENTS, Auth) {
        //TODO: remove, not really needed
        $rootScope.$on('$routeChangeError', function (event, next, current) {
            $log.debug('$routeChangeError, event=' + angular.toJson(event) + ' next=' + angular.toJson(next));
        });
    }

})();