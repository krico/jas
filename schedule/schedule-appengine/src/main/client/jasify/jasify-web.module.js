(function (angular) {

    'use strict';

    var jasifyWeb = angular.module('jasifyWeb', [
        'ngRoute',
        'ngResource',
        'ngMessages',
        'ngCookies',
        'ngSanitize',
        'ngMaterial',
        'ngAnimate',
        'pascalprecht.translate',
        'angularMoment',
        'ui.bootstrap',
        'angularSpinner',
        'ui.bootstrap.datetimepicker',
        'jasifyComponents',
        'jasify.locale',
        'jasify.admin',
        'jasify.authenticate',
        'jasify.payment',
        'jasify.balance',
        'jasify.bookings',
        'jasify.activityPackage',
        'jasify.checkout',
        'jasify.templates',
        'jasify.filters',
        'jasify.common.ui',
        'angular.filter',
        'bs3dp4ng'
    ]);

    jasifyWeb.config(function ($mdThemingProvider) {
        $mdThemingProvider.theme('default')
            .primaryPalette('blue-grey')
            .accentPalette('orange')
            .warnPalette('red');

        $mdThemingProvider.theme('default');
    });

    jasifyWeb.config(function ($translateProvider, EndpointProvider, localStorageServiceProvider) {
        EndpointProvider.verbose(true);
        localStorageServiceProvider.setPrefix('Jasify');

        $translateProvider.useStaticFilesLoader({
            prefix: '/build/i18n/locale-',
            suffix: '.json'
        });

        //   $translateProvider.useSanitizeValueStrategy('sanitize');

        $translateProvider.registerAvailableLanguageKeys(['en', 'de'], {
                'de_CH': 'de',
                'de_DE': 'de',
                'en_GB': 'en',
                'en_US': 'en'
            })
            .determinePreferredLanguage()
            .fallbackLanguage('en');
    }).run(function (jasLocale) {
        jasLocale.initialize();
    });

    /**
     * Listen to route changes and check
     */
    jasifyWeb.run(jasifyWebRun);

    function jasifyWebRun($rootScope, $log, $window, $location, jasDialogs, AUTH_EVENTS) {

        $rootScope.$on('$routeChangeError', function (event, current, previous, rejection) {
            // Broadcasted if any of the resolve promises are rejected
            $log.debug('$routeChangeError, event=' + angular.toJson(event) + ' current=' + angular.toJson(current) + ', rejection=' + angular.toJson(rejection));

            if (isBroadcast(rejection)) {
                //no need to handle this one as an event broadcast happened
                $log.debug('Event should be handled [' + rejection + ']');
            } else if (is401(rejection)) {
                jasDialogs.error('It seems your session has expired... Press OK to Sign In again.', function () {
                    $window.location = 'login.html';
                });
            } else if (is403(rejection)) {
                jasDialogs.error('You are not authorized to access the operation you have attempted to execute.  Press OK and you will be directed back.', function () {
                    emulateRouteChangeRollback();
                });
            } else {
                jasDialogs.error('There was a problem communicating with the server.  Press OK and you will be directed back.', function () {
                    emulateRouteChangeRollback();
                });
            }

            function isBroadcast(rejection) {
                return AUTH_EVENTS.notGuest == rejection ||
                    AUTH_EVENTS.notAuthenticated == rejection ||
                    AUTH_EVENTS.notAuthorized == rejection;
            }

            function /* Unauthorized => not logged in */ is401(rejection) {
                return isRejectionCode(rejection, 401);
            }

            function /* Forbidden => insufficient privileges */ is403(rejection) {
                return isRejectionCode(rejection, 403);
            }

            function isRejectionCode(rejection, code) {
                return rejection && rejection.result && rejection.result.error && rejection.result.error.code == code;
            }

            function emulateRouteChangeRollback() {
                if (previous) {
                    $window.history.back();
                } else {
                    $location.path("/").replace();
                }

            }
        });
    }

    angular.module("template/pagination/pagination.html", []).run(["$templateCache", function ($templateCache) {
        $templateCache.put("template/pagination/pagination.html",
            "<ul class=\"pagination\">\n" +
            "  <li ng-if=\"boundaryLinks\" ng-class=\"{disabled: noPrevious()}\"><a href ng-click=\"selectPage(1)\"><i class=\"md mdi mdi-more-horiz\"></i></a></li>\n" +
            "  <li ng-if=\"directionLinks\" ng-class=\"{disabled: noPrevious()}\"><a href ng-click=\"selectPage(page - 1)\"><i class=\"md mdi mdi-chevron-left\"></i></a></li>\n" +
            "  <li ng-repeat=\"page in pages track by $index\" ng-class=\"{active: page.active}\"><a href ng-click=\"selectPage(page.number)\">{{page.text}}</a></li>\n" +
            "  <li ng-if=\"directionLinks\" ng-class=\"{disabled: noNext()}\"><a href ng-click=\"selectPage(page + 1)\"><i class=\"md mdi mdi-chevron-right\"></i></a></li>\n" +
            "  <li ng-if=\"boundaryLinks\" ng-class=\"{disabled: noNext()}\"><a href ng-click=\"selectPage(totalPages)\"><i class=\"md mdi mdi-more-horiz\"></i></a></li>\n" +
            "</ul>");
    }]);

})(window.angular);