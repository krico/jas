(function (angular) {

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

        // TODO: extract is somwehere
        $translateProvider
            .translations('en-US', {
                SIGN_OUT: 'Sign Out',
                SIGN_IN: 'Sign In',
                LOCATION: 'Location',
                START: 'Start',
                FINISH: 'Finish',
                SUBSCRIPTIONS: 'Subscriptions'
            }).translations('de', {
                SIGN_OUT: 'Abmelden',
                SIGN_IN: 'Anmelden',
                LOCATION: 'Lage',
                START: 'Start',
                FINISH: 'Fertig',
                SUBSCRIPTIONS: 'Abonnements'
            }).registerAvailableLanguageKeys(['en', 'de'], {
                'en_US': 'en',
                'en_UK': 'en',
                'de_DE': 'de',
                'de_CH': 'de'
            }).fallbackLanguage('en');

    }).run(function (jasLocale) {
        jasLocale.initialize();
    });

    /**
     * Listen to route changes and check
     */
    jasifyWeb.run(jasifyWebRun);

    function jasifyWebRun($rootScope, $log, $window, $location, jasDialogs) {

        $rootScope.$on('$routeChangeError', function (event, current, previous, rejection) {
            // Broadcasted if any of the resolve promises are rejected
            $log.debug('$routeChangeError, event=' + angular.toJson(event) + ' current=' + angular.toJson(current));
            jasDialogs.error('There was a problem communicating with the server.  Press OK and you will be directed back.', function () {
                if (previous) {
                    $window.history.back();
                } else {
                    $location.path("/").replace();
                }
            });
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

})(angular);