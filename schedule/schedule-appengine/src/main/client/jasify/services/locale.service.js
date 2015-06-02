/*global window */
(function (angular) {

    'use strict';

    var module = angular.module('jasify.locale', ['angularMoment', 'LocalStorageModule']);

    module.factory('jasLocale', function ($log, localStorageService, amMoment) {

        var localeKey = 'jas-locale';

        return {
            locale: function (newLocale) {
                if (newLocale) {
                    $log.debug('Locale changed: ' + newLocale);
                    localStorageService.set(localeKey, newLocale);
                    amMoment.changeLocale(newLocale);
                    return newLocale;
                }

                return localStorageService.get(localeKey);
            },
            initialize: function () {
                $log.debug('Initializing locale');
                var savedLocale = this.locale();
                if (savedLocale) {
                    this.locale(savedLocale);
                } else {
                    this.locale('de');
                }
            }
        };
    });

}(window.angular));

