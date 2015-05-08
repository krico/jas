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
    ]);

    module.controller('BookingViaJasify', BookingViaJasify);
    module.run(jasifyWebRun);
    module.config(bookingViaRoutes);

    function bookingViaRoutes($routeProvider) {
        $routeProvider
            .when('/', {
                templateUrl: 'booking-via-jasify/booking-via-jasify.html',
                controller: 'BookingViaJasify',
                controllerAs: 'vm',
                resolve: {
                    allow: function(Allow) {
                        return Allow.all();
                    },
                    activities: function(Activity) {
                        return Activity.query({organizationId: 'O53'});
                    }
                }
            })
        ;
    }

    function jasifyWebRun($rootScope, $log) {
        $rootScope.$on('$routeChangeError', function (event, next, current) {
            $log.debug('$routeChangeError, event=' + angular.toJson(event) + ' next=' + angular.toJson(next));
        });
    }

    function BookingViaJasify(AUTH_EVENTS, $rootScope, $location, $q, BrowserData, ShoppingCart, Auth, activities) {

        var vm = this;

        this.selection = [];
        this.activities = activities.items;
        this.auth = Auth;
        this.bookIt = bookIt;

        $rootScope.$on(AUTH_EVENTS.accountCreated, function() {
            Auth.restore(true);
        });

        function bookIt() {
            var promises = [];

            angular.forEach(this.selection, function(value) {
               promises.push(ShoppingCart.addUserActivity(value.id));
            });

            $q.all(promises).then(function() {
                BrowserData.setPaymentAcceptRedirect($location.path());
                $location.path('/checkout');
            }, function() {
                alert("!");
            })
        }
    }

})(angular);
