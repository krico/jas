/*global window, _ */
(function (angular, _) {

    angular
        .module('jasify.bookingViaJasify')
        .controller('BookingViaJasify', BookingViaJasify);

    function BookingViaJasify(AUTH_EVENTS, $scope, $log, $rootScope, $location, $q, localStorageService, sessionStorageKeys,
                              BrowserData, ShoppingCart, ActivityPackage, Auth, activities, activityPackages, jasDialogs) {

        var vm = this;

        vm.activities = activities.items;
        vm.activitySelection = [];

        vm.activityPackages = activityPackages.items;
        vm.activityPackageActivities = {};
        vm.activityPackageSelection = {};

        wireUpSessionStorage();

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
        this.isActivitySelected = function (activity) {
            return _.find(vm.activitySelection, {'id': activity.id});
        };
        this.isSelectedActivityPackageItem = function(activity, activityPackage) {
            return vm.activityPackageSelection[activityPackage.id] &&
                _.find(vm.activityPackageSelection[activityPackage.id], {'id': activity.id});
        };

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
                _.find(vm.activityPackageSelection[activityPackage.id], { id: activity.id })) {
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

        function wireUpSessionStorage() {
            $scope.$watch('vm.activityPackageSelection', function (newValue) {
                localStorageService.set(sessionStorageKeys.activityPackageSelection, newValue);
            }, true);

            $scope.$watchCollection('vm.activitySelection', function (newValue) {
                localStorageService.set(sessionStorageKeys.activitySelection, newValue);
            });

            vm.activityPackageSelection = localStorageService.get(sessionStorageKeys.activityPackageSelection) || {};
            vm.activitySelection = localStorageService.get(sessionStorageKeys.activitySelection) || [];
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

}(window.angular, _));