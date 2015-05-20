/*global window */
(function (angular) {

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
                    activityPackages: function ($route, ActivityPackage) {
                        if ($route.current.params.organizationId) {
                            return ActivityPackage.query($route.current.params.organizationId);
                        }
                        return {items: []};
                    }
                }
            });
    }

    bookingViaJasify.config(function (localStorageServiceProvider) {
        localStorageServiceProvider
            .setStorageType('sessionStorage')
            .setPrefix('bookingViaJasify');
    });

}(window.angular));
