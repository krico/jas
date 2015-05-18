/*global window */
(function (angular) {

    angular
        .module('jasify.bookingViaJasify')
        .controller('BookingViaJasify', BookingViaJasify);

    function BookingViaJasify(AUTH_EVENTS, $log, $rootScope, $location, $q,
                              BrowserData, ShoppingCart, ActivityPackage, Auth, activities, activityPackages, jasDialogs) {

        var vm = this;

        vm.activities = activities.items;
        vm.activitySelection = [];

        vm.activityPackages = activityPackages.items;
        vm.activityPackageActivities = {};
        vm.activityPackageSelection = {};

        angular.forEach(this.activityPackages, function (activityPackage) {
            ActivityPackage.getActivities(activityPackage.id).then(function (result) {
                vm.activityPackageActivities[activityPackage.id] = result;
            });
        });

        this.auth = Auth;
        this.bookIt = bookIt;
        this.isActivityFullyBooked = isActivityFullyBooked;
        this.isActivityPackageFullyBooked = isActivityPackageFullyBooked;
        this.disableActivityPackageActivitySelection = disableActivityPackageActivitySelection;
        this.confirmRemoveActivity = confirmRemoveActivity;
        this.confirmRemoveActivityPackage = confirmRemoveActivityPackage;
        this.hasCompletedActivityPackages = hasCompletedActivityPackages;
        this.packageSelectionIncomplete = packageSelectionIncomplete;
        this.packageSelectionComplete = packageSelectionComplete;
        this.packageSelectionTooBig = packageSelectionTooBig;

        $rootScope.$on(AUTH_EVENTS.accountCreated, function () {
            Auth.restore(true);
        });

        function confirmRemoveActivity(activity) {
            jasDialogs.ruSure("Do you want to remove this Activity?", function () {
                $rootScope.$apply(function () {
                    vm.activitySelection.splice(vm.activitySelection.indexOf(activity), 1);
                });
            });
        }

        function confirmRemoveActivityPackage(activityPackage) {
            jasDialogs.ruSure("Do you want to remove this Activity Package?", function () {
                $rootScope.$apply(function () {
                    delete vm.activityPackageSelection[activityPackage.id];
                });
            });
        }

        function disableActivityPackageActivitySelection(activity, activityPackage) {
            if (vm.activityPackageSelection[activityPackage.id] &&
                vm.activityPackageSelection[activityPackage.id].indexOf(activity) !== -1) {
                return false;
            }
            return vm.isActivityFullyBooked(activity) || vm.isActivityPackageFullyBooked(activityPackage);
        }

        function hasCompletedActivityPackages() {
            return _.some(vm.activityPackages, function (value) {
                return vm.packageSelectionComplete(value);
            });
        }

        function isActivityPackageFullyBooked(activityPackage) {
            return vm.activityPackageSelection[activityPackage.id] &&
                vm.activityPackageSelection[activityPackage.id].length === activityPackage.itemCount;
        }

        function isActivityFullyBooked(activity) {
            return activity.maxSubscriptions <= activity.subscriptionCount;
        }

        function packageSelectionIncomplete(activityPackage) {
            return vm.activityPackageSelection &&
                vm.activityPackageSelection[activityPackage.id] &&
                vm.activityPackageSelection[activityPackage.id].length !== 0 &&
                (activityPackage.itemCount > vm.activityPackageSelection[activityPackage.id].length);
        }

        function packageSelectionComplete(activityPackage) {
            return vm.activityPackageSelection && vm.activityPackageSelection[activityPackage.id] &&
                (activityPackage.itemCount === vm.activityPackageSelection[activityPackage.id].length);
        }

        function packageSelectionTooBig(activityPackage) {
            return vm.activityPackageSelection && vm.activityPackageSelection[activityPackage.id] &&
                (activityPackage.itemCount < vm.activityPackageSelection[activityPackage.id].length);
        }

        function bookIt() {
            ShoppingCart.clearUserCart().then(function () {
                var promises = [];

                angular.forEach(vm.activitySelection, function (value) {
                    $log.debug("Adding activity to shopping cart: " + value.id);
                    promises.push(ShoppingCart.addUserActivity(value.id));
                });

                var completeActivityPackagesList = _.filter(vm.activityPackages, vm.packageSelectionComplete);

                angular.forEach(completeActivityPackagesList, function (value) {
                    $log.debug("Adding activity package to shopping cart: " + value.id);
                    promises.push(ShoppingCart.addUserActivityPackage(value, vm.activityPackageSelection[value.id]));
                });

                $q.all(promises).then(function () {
                    $log.debug('Shopping cart is ready');
                    BrowserData.setPaymentAcceptRedirect('done');
                    $location.path('/checkout');
                }, function () {
                    // TODO
                });

            });
        }
    }

}(window.angular));