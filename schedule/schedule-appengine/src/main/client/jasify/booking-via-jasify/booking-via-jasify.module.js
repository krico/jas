(function (angular) {

    var module = angular.module('jasify.bookingViaJasify', [
        'ngRoute',
        'ngResource',
        'ngMessages',
        'ngCookies',
        'ngSanitize',
        'ui.bootstrap',
        'angularSpinner',
        'ui.bootstrap.datetimepicker',
        'jasifyComponents',
        'jasify.authenticate',
        'jasify.payment',
        'jasify.balance',
        'jasify.checkout',
        'jasify.templates',
        "checklist-model",
        "jasifyFilters"
    ]).config(bookingViaRoutes);

    function bookingViaRoutes($routeProvider) {
        $routeProvider
            .when('/:organizationId', {
                templateUrl: 'booking-via-jasify/booking-via-jasify.html',
                controller: 'BookingViaJasify',
                controllerAs: 'vm',
                resolve: {
                    allow: function(Allow) {
                        return Allow.all();
                    },
                    activities: function($route, Activity) {
                        return Activity.query({organizationId: $route.current.params.organizationId });
                    }
                }
            }).when('/done', {
                templateUrl: 'booking-via-jasify/booking-via-jasify-done.html',
                resolve: {
                    allow: /*@ngInject*/ function (Allow) {
                        return Allow.all();
                    }
                }
            });
    }

})(angular);
