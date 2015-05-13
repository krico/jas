(function (angular) {

    angular
        .module('jasify.bookingViaJasify')
        .controller('BookingViaJasify', BookingViaJasify);

    function BookingViaJasify(AUTH_EVENTS, $rootScope, $location, $q, BrowserData, ShoppingCart, Auth, activities, jasDialogs) {

        var vm = this;

        this.selection = [];

        this.activities = activities.items;
        this.auth = Auth;
        this.bookIt = bookIt;
        this.isFullyBooked = isFullyBooked;
        this.confirmRemove = confirmRemove;

        $rootScope.$on(AUTH_EVENTS.accountCreated, function () {
            Auth.restore(true);
        });

        function confirmRemove(activity) {
            jasDialogs.ruSure("Do you want to remove this activity?", function() {
                $rootScope.$apply(function () {
                    vm.selection.splice(vm.selection.indexOf(activity), 1);
                })
            })
        }

        function isFullyBooked(activity) {
            return activity.maxSubscriptions <= activity.subscriptionCount;
        }

        function bookIt() {
            ShoppingCart.clearUserCart().then(function () {
                var promises = [];

                angular.forEach(vm.selection, function (value) {
                    promises.push(ShoppingCart.addUserActivity(value.id));
                });

                $q.all(promises).then(function () {
                    BrowserData.setPaymentAcceptRedirect('done');
                    $location.path('/checkout');
                }, function () {
                    // TODO
                });
            });
        }
    }

}(angular));