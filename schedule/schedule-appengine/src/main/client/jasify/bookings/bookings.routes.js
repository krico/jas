(function (angular) {
    angular.module('jasify.bookings').config(jasifyRoutes);

    function jasifyRoutes($routeProvider) {
        $routeProvider
            .when('/bookings/agenda', {
                templateUrl: 'bookings/bookings-agenda.html',
                controller: 'BookingsAgendaController',
                controllerAs: 'vm',
                resolve: {
                    allow: /*@ngInject*/ function (Allow) {
                        return Allow.user();
                    }
                }
            })
        ;
    }

})(angular);
