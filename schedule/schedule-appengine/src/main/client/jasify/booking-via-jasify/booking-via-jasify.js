/*global window, _ */
(function (angular, _) {

    'use strict';

    angular
        .module('jasify.bookingViaJasify')
        .controller('BookingViaJasify', BookingViaJasify);

    function BookingViaJasify(AUTH_EVENTS, $scope, $log, $rootScope, $location, $q, $timeout, localStorageService, sessionStorageKeys,
                              BrowserData, ShoppingCart, ActivityPackage, Auth, activities, activityPackages, jasDialogs, getContrast) {

        var vm = this;
        vm.wizardOptions = {
            onInit: onWizardInit,
            onTabClick: onWizardClick
        };
        vm.activities = activities.items;
        vm.activitySelection = [];

        vm.activityPackages = activityPackages.items;
        vm.activityPackageActivities = {};
        vm.activityPackageSelection = {};
        vm.activityPackageSelectAllFlags = [];

        vm.getStyle = function (activity) {
            return {
                'border-left': '1px solid ' + activity.activityType.colourTag,
                'margin-bottom': '15px'
            };
        };

        vm.getBadge = function (activity) {
            return {
                'background-color': activity.activityType.colourTag,
                'color': getContrast.compute(activity.activityType.colourTag)
            };
        };

        wireUpSessionStorage();

        angular.forEach(this.activityPackages, function (activityPackage) {
            ActivityPackage.getActivities(activityPackage.id).then(function (result) {
                vm.activityPackageActivities[activityPackage.id] = result;
                updateSelectAllFlags();
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
        this.canSelectAllForActivityPackage = canSelectAllForActivityPackage;
        this.packageSelectionTooBig = packageSelectionTooBig;
        this.selectAllForActivityPackage = selectAllForActivityPackage;
        this.updateSelectAllFlag = updateSelectAllFlag;
        this.isActivitySelected = function (activity) {
            return _.find(vm.activitySelection, {'id': activity.id});
        };

        this.isSelectedActivityPackageItem = function (activity, activityPackage) {
            return vm.activityPackageSelection[activityPackage.id] &&
                _.find(vm.activityPackageSelection[activityPackage.id], {'id': activity.id});
        };

        $rootScope.$on(AUTH_EVENTS.accountCreated, function () {
            Auth.restore(true);
        });

        function onWizardClick(tab, navigation, index) {
            $timeout(function () {
                var v = activeTab();
                $log.debug('Active tab: ' + v);
                localStorageService.set(sessionStorageKeys.selectedTabIndex, v);
            }, 500);
        }

        function onWizardInit(tab, navigation, index) {
            $timeout(function () {
                var tabIndex = localStorageService.get(sessionStorageKeys.selectedTabIndex);
                if (angular.isNumber(tabIndex)) {
                    $('#wizard').bootstrapWizard('show', tabIndex);
                }
            });

            return true;
        }

        function activeTab() {
            if ($('#activities').css('display') != 'none') {
                return 0;
            }
            if ($('#activity-packages').css('display') != 'none') {
                return 1;
            }
            if ($('#checkout').css('display') != 'none') {
                return 2;
            }
            return -1;
        }

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
                _.find(vm.activityPackageSelection[activityPackage.id], {id: activity.id})) {
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

        function selectAllForActivityPackage(activityPackage) {
            if (vm.activityPackageSelectAllFlags[activityPackage.id]) {
                vm.activityPackageSelection[activityPackage.id] =
                    angular.copy(vm.activityPackageActivities[activityPackage.id]);
            } else {
                delete vm.activityPackageSelection[activityPackage.id];
            }
        }

        function updateSelectAllFlag(activityPackageId) {
            if (vm.activityPackageSelection[activityPackageId] &&
                vm.activityPackageActivities[activityPackageId] &&
                vm.activityPackageSelection[activityPackageId].length === vm.activityPackageActivities[activityPackageId].length) {
                vm.activityPackageSelectAllFlags[activityPackageId] = true;
            } else {
                vm.activityPackageSelectAllFlags[activityPackageId] = false;
            }
        }

        function updateSelectAllFlags() {
            angular.forEach(vm.activityPackageActivities, function (activityPackageActivities, activityPackageId) {
                if (vm.activityPackageSelection[activityPackageId] &&
                    vm.activityPackageSelection[activityPackageId].length === activityPackageActivities.length) {
                    vm.activityPackageSelectAllFlags[activityPackageId] = true;
                } else {
                    vm.activityPackageSelectAllFlags[activityPackageId] = false;
                }
            });
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

        function canSelectAllForActivityPackage(activityPackage) {

            var numActivitiesToBook = 0;

            angular.forEach(vm.activityPackageActivities[activityPackage.id], function(activity) {
                if (isActivityFullyBooked(activity) === false) {
                    numActivitiesToBook += 1;
                }
            });

            return numActivitiesToBook === activityPackage.itemCount;
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
                    BrowserData.setPaymentCancelRedirect($location.path());
                    $location.path('/checkout');
                }, function () {
                    // TODO
                });

            });
        }
    }

}(window.angular, _));