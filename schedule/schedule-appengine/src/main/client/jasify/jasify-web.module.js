(function (angular) {

    angular.module('jasifyWeb', [
        'ngRoute',
        'ngResource',
        'ngMessages',
        'ngCookies',
        'ngSanitize',
        'ui.bootstrap',
        'angularSpinner',
        'ui.bootstrap.datetimepicker',
        'jasifyComponents',
        'jasify.authenticate',
        'jasify.payment',
        'jasify.balance',
        'jasify.checkout',
        'jasify.templates'
    ]);

    /**
     * Listen to route changes and check
     */
    angular.module('jasifyWeb').run(jasifyWebRun);

    function jasifyWebRun($rootScope, $log) {
        $rootScope.$on('$routeChangeError', function (event, next, current) {
            $log.debug('$routeChangeError, event=' + angular.toJson(event) + ' next=' + angular.toJson(next));
        });
    }

})(angular);