/*global window */
(function (angular, navigator) {

    'use strict';

    var module = angular.module('jasify.locale', ['angularMoment', 'LocalStorageModule']);

    module.factory('jasLocale', function ($log, $translate, localStorageService, amMoment) {

        var localeKey = 'jas-locale',
            defaultLocale;

        if (navigator.languages && navigator.languages[0]) {
            defaultLocale = navigator.languages[0];
        } else if (navigator.language) {
            defaultLocale = navigator.language;
        } else {
            defaultLocale = 'en';
        }

        $log.debug('Default locale: ' + defaultLocale);

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
                var previousLocale = this.locale();
                if (previousLocale) {
                    $log.debug('Initializing locale to previous locale: ' + previousLocale);
                } else {
                    $log.debug('Initializing locale to default locale: ' + defaultLocale);
                }
                this.locale(previousLocale || defaultLocale);
            }
        };
    });

}(window.angular, window.navigator));

