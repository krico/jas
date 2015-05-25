/*global window */
(function (angular, _, moment) {

    'use strict';

    var bookingViaJasify = angular.module('jasify.bookingViaJasify', [
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
        "checklist-model",
        "jasify.filters"
    ]);

    bookingViaJasify.config(bookingViaRoutes);
    bookingViaJasify.constant('sessionStorageKeys', {
        activityPackageSelection: 'activityPackageSelection',
        activitySelection: 'activitySelection',
        selectedTabIndex: 'selectedTabIndex'
    });

    function bookingViaRoutes($routeProvider) {
        $routeProvider
            .when('/done', {
                templateUrl: 'booking-via-jasify/booking-via-jasify-done.html',
                resolve: {
                    allow: /*@ngInject*/ function (Allow, localStorageService, sessionStorageKeys) {
                        localStorageService.remove(sessionStorageKeys.activityPackageSelection);
                        localStorageService.remove(sessionStorageKeys.activitySelection);
                        localStorageService.remove(sessionStorageKeys.selectedTabIndex);
                        return Allow.all();
                    }
                }
            })
            .when('/:organizationId', {
                templateUrl: 'booking-via-jasify/booking-via-jasify.html',
                controller: 'BookingViaJasify',
                controllerAs: 'vm',
                resolve: {
                    allow: function (Allow) {
                        return Allow.all();
                    },
                    activities: function ($route, Activity) {
                        return Activity.query({
                            fromDate: new Date().toISOString(),
                            organizationId: $route.current.params.organizationId
                        });
                    },
                    activityPackages: function ($q, $route, ActivityPackage) {

                        if ($route.current.params.organizationId) {
                            var dfd = $q.defer();
                            ActivityPackage.query($route.current.params.organizationId).then(function (result) {
                                result.items = _.filter(result.items, function (item) {
                                    return !item.validUntil || moment().isBefore(item.validUntil);
                                });
                                dfd.resolve(result);
                            });
                            return dfd.promise;
                        }

                        return {items: []};
                    }
                }
            });
    }

    bookingViaJasify.config(function (localStorageServiceProvider, CheckoutProvider) {
        //TODO: We use storage to communicate with checkout, so needs to be local and prefix jasify
        localStorageServiceProvider
            //.setStorageType('sessionStorage')
            .setPrefix('Jasify');
        CheckoutProvider.popupMode(true);
    });

}(window.angular, window._, window.moment));
