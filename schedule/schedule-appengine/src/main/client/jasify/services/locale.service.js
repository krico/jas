/*global window */
(function (angular) {

    'use strict';

    var module = angular.module('jasify.locale', ['angularMoment', 'LocalStorageModule']);

    module.factory('jasLocale', function ($log, $translate, localStorageService, amMoment) {

        var localeKey = 'jas-locale',
            defaultLocale = 'de';

        return {
            locale: function (newLocale) {
                if (newLocale) {
                    $log.debug('Locale changed: ' + newLocale);
                    localStorageService.set(localeKey, newLocale);
                    amMoment.changeLocale(newLocale);
                    $translate.use(newLocale);
                    return newLocale;
                }

                return localStorageService.get(localeKey);
            },
            initialize: function () {
                $log.debug('Initializing locale');
                this.locale(this.locale() || defaultLocale);
            }
        };
    });

}(window.angular));

