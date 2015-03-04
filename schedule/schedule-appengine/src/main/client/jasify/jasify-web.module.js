(function (angular) {

    angular.module('jasifyWeb', [
        'ngRoute',
        'ngResource',
        'ngMessages',
        'ngCookies',
        'ui.bootstrap',
        'angularSpinner',
        'ui.bootstrap.datetimepicker',
        'ngStorage',
        'jasifyComponents',
        'jasify.authenticate',
        'jasify.payment',
        'jasify.balance',
        'jasify.templates'
    ]);

    /**
     * Listen to route changes and check
     */
    angular.module('jasifyWeb').run(jasifyWebRun);

    function jasifyWebRun($rootScope, $log) {
        //TODO: its just an example to know how its done.
        $rootScope.$on('$routeChangeError', function (event, next, current) {
            $log.debug('$routeChangeError, event=' + angular.toJson(event) + ' next=' + angular.toJson(next));
        });
    }

})(angular);