(function (angular) {

    var jasifyComponents = angular.module('jasifyComponents', [
        'ngRoute',
        'ngResource',
        'ngMessages',
        'ngCookies',
        'ui.bootstrap',
        'angularSpinner',
        'LocalStorageModule',
        'ui.bootstrap.datetimepicker'
    ]);

    jasifyComponents.config(function (localStorageServiceProvider) {
        localStorageServiceProvider
            .setPrefix('jas')
            //.setStorageType('sessionStorage')
            .setNotify(false, false);
    });

})(angular);