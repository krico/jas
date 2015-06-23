(function (angular) {

    'use strict';

    angular.module('jasify.checkoutWindow', [
        'ngRoute',
        'ngResource',
        'ngMessages',
        'ngCookies',
        'ngSanitize',
        'ui.bootstrap',
        'angularSpinner',
        'LocalStorageModule',
        'ui.bootstrap.datetimepicker',
        'jasifyComponents',
        'jasify.authenticate',
        'jasify.payment',
        'jasify.balance',
        'jasify.checkout',
        'jasify.templates',
        "jasify.filters"
    ])
        .config(configureStorage)
        .run(jasifyCheckoutWindowRun);

    function configureStorage(localStorageServiceProvider) {
        localStorageServiceProvider
            .setPrefix('Jasify');
    }

    function jasifyCheckoutWindowRun($rootScope, $log, $location) {
        $rootScope.$on('$routeChangeError', function (event, next, current) {
            $log.debug('$routeChangeError, event=' + angular.toJson(event) + ' next=' + angular.toJson(next));
            $location.path('/route-error');
        });
    }

}(window.angular));
