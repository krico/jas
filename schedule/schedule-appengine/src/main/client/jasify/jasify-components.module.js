/*global window */
(function (angular) {

    'use strict';

    var jasifyComponents = angular.module('jasifyComponents', [
        'ngRoute',
        'ngResource',
        'ngMessages',
        'ngCookies',
        'ui.bootstrap',
        'angularSpinner',
        'LocalStorageModule',
        'ui.bootstrap.datetimepicker',
        'jasify.endpoint'
    ]);

    jasifyComponents.config(function (JasifyProvider) {
        JasifyProvider.apiRoot('/_ah/api/jasify/v1/');
    });

    jasifyComponents.config(function (localStorageServiceProvider) {
        localStorageServiceProvider
            .setPrefix('jas')
            //.setStorageType('sessionStorage')
            .setNotify(false, false);
    });

    jasifyComponents.constant('jasPagerSettings', {
        pages: [2, 10, 25, 100]
    });

}(window.angular));